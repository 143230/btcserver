package com.btc.app.pool;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public final class BoundedBlockingPool<T>
        extends AbstractPool<T>
        implements BlockingPool<T> {
    private Logger logger = Logger.getLogger(BoundedBlockingPool.class);
    private static final int DEFAULT_MAX_USE_TIME = 50;
    private static final int DEFAULT_MAX_POOL_SIZE = 30;
    private static final long DEFAULT_EXPIRE_TIME = Long.MAX_VALUE;

    /**
     * The capacity bound, or DEFAULT_MAX_POOL_SIZE if none
     */
    private final int capacity;
    /**
     * The create expire time of an object
     */
    private final long expire_time;
    private Map<T, Long> objectLifeLongMap;
    private final int maxUseTime;
    private Map<T, Integer> objectUsedTimeMap;
    private BlockingQueue<T> objects;
    private Validator<T> validator;
    private ObjectFactory<T> objectFactory;
    private ExecutorService executor =
            Executors.newCachedThreadPool();
    private AtomicBoolean shutdownCalled = new AtomicBoolean();
    /**
     * Current number of object in object pool
     */
    private final AtomicInteger count_in_pool = new AtomicInteger();
    private final AtomicInteger count_in_used = new AtomicInteger();

    private final ReentrantLock getLock = new ReentrantLock(true);

    public BoundedBlockingPool(Validator validator,
                               ObjectFactory objectFactory) {
        this(0, validator, objectFactory);
    }


    public BoundedBlockingPool(int initSize,
                               Validator validator,
                               ObjectFactory objectFactory) {
        this(DEFAULT_EXPIRE_TIME,
                DEFAULT_MAX_POOL_SIZE,
                DEFAULT_MAX_USE_TIME,
                initSize, validator, objectFactory);
    }

    public BoundedBlockingPool(long expire_time,
                               int maxPoolSize,
                               int maxObjectUseTime,
                               int initSize,
                               Validator validator,
                               ObjectFactory objectFactory) {
        super();
        this.capacity = maxPoolSize;
        this.expire_time = expire_time;
        this.maxUseTime = maxObjectUseTime;
        this.validator = validator;
        this.objectFactory = objectFactory;
        objectLifeLongMap = new HashMap<T, Long>();
        objectUsedTimeMap = new HashMap<T, Integer>();
        objects = new LinkedBlockingQueue<T>(DEFAULT_MAX_POOL_SIZE);
        initializeObjects(initSize);
        shutdownCalled.set(false);
    }

    public T get(long timeOut, TimeUnit unit) {
        if (!shutdownCalled.get()) {
            T t = null;
            final ReentrantLock getLock = this.getLock;
            getLock.lock();
            try {
                if (count_in_pool.get() == 0 && count_in_used.get() < capacity) {
                    t = objectFactory.createNew();
                    t = checkCreate(t);
                } else {
                    t = objects.poll(timeOut, unit);
                    t = checkDequeue(t);
                }
                return t;
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            } finally {
                getLock.unlock();
            }
            return t;
        }
        throw new IllegalStateException("Object pool is already shutdown");
    }

    public T get() {
        if (!shutdownCalled.get()) {
            T t = null;
            final ReentrantLock getLock = this.getLock;
            while (t == null) {
                getLock.lock();
                try {
                    //logger.info("Count In Pool: " + count_in_pool.get() + "\tCount In Used: " + count_in_used.get());
                    if (count_in_pool.get() == 0 && count_in_used.get() < capacity) {
                        t = objectFactory.createNew();
                        //logger.info("create new client: "+t +" ObjectPool Size: "+size());
                        checkCreate(t);
                    } else {
                        t = objects.poll(1, TimeUnit.SECONDS);
                        t = checkDequeue(t);
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                } finally {
                    getLock.unlock();
                }
            }
            return t;
        }
        throw new IllegalStateException(
                "Object pool is already shutdown");
    }

    public void shutdown() {
        if (shutdownCalled.compareAndSet(false, true)) {
            executor.shutdownNow();
            clearResources();
        }
    }

    public int size() {
        return objects.size();
    }

    private T checkDequeue(T t) {
        if (t != null && isValid(t)) {
            count_in_pool.decrementAndGet();
            count_in_used.incrementAndGet();
            int usedTime = objectUsedTimeMap.get(t);
            objectUsedTimeMap.put(t, usedTime + 1);
        } else if (t != null) {
            count_in_pool.decrementAndGet();
            objectLifeLongMap.remove(t);
            objectUsedTimeMap.remove(t);
            //logger.info("1:Webclient Exipred, Destroy: " + t + "\tCount In Pool: " + count_in_pool.get() + "\tCount In Used: " + count_in_used.get());
            validator.invalidate(t);
            t = null;
        }
        return t;
    }

    private T checkCreate(T t) {
        if (t != null && validator.isValid(t)) {
            count_in_used.incrementAndGet();
            long now = System.currentTimeMillis();
            objectLifeLongMap.put(t, now);
            objectUsedTimeMap.put(t, 1);
        } else if (t != null) {
            //logger.info("2:Webclient Exipred, Destroy: " + t + "\tCount In Pool: " + count_in_pool.get() + "\tCount In Used: " + count_in_used.get());
            validator.invalidate(t);
            t = null;
        }
        return t;
    }

    private void clearResources() {
        try {
            while (!objects.isEmpty()) {
                T t = objects.poll(1, TimeUnit.SECONDS);
                if (t != null) {
                    count_in_pool.decrementAndGet();
                    objectUsedTimeMap.remove(t);
                    objectLifeLongMap.remove(t);
                    if (isValid(t)) {
                        //logger.info("3:Webclient Exipred, Destroy: " + t + "\tCount In Pool: " + count_in_pool.get() + "\tCount In Used: " + count_in_used.get());
                        validator.invalidate(t);
                    }
                }
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    protected void returnToPool(T t) {
        if (!shutdownCalled.get()) {
            executor.submit(new ObjectReturner(objects, t));
        } else {
            destroy(t);
        }
    }

    @Override
    protected void handleInvalidReturn(T t) {
        destroy(t);
    }

    @Override
    protected boolean isValid(T t) {
        if (isExpire(t) || isExceedTime(t)) return false;
        return validator.isValid(t);
    }

    public synchronized void destroy(T t) {
        if (t != null) {
            if (objectLifeLongMap.containsKey(t)) {
                count_in_used.decrementAndGet();
                objectUsedTimeMap.remove(t);
                objectLifeLongMap.remove(t);
            }
            if (validator.isValid(t)) {
                //logger.info("4:Webclient Exipred, Destroy: " + t + "\tCount In Pool: " + count_in_pool.get() + "\tCount In Used: " + count_in_used.get());
                validator.invalidate(t);
            }
        }
    }

    private boolean isExpire(T t) {
        long now = System.currentTimeMillis();
        Long createTime = objectLifeLongMap.get(t);
        if (createTime == null || now < createTime) {
            throw new IllegalStateException("The State of Object: " + t + " is Illegaled,CreateTime: " + createTime + "\tNow:" + now);
        } else {
            return (now - createTime) >= expire_time;
        }
    }

    private boolean isExceedTime(T t) {
        Integer usedTime = objectUsedTimeMap.get(t);
        if (usedTime == null || usedTime < 0) {
            throw new IllegalStateException("The State of Object: " + t + " is Illegaled");
        }
        return usedTime >= maxUseTime;
    }

    private void initializeObjects(final int initSize) {
        this.count_in_used.set(0);
        for (int i = 0; i < initSize; i++) {
            T t = objectFactory.createNew();
            if (objects.add(t)) {
                this.count_in_pool.incrementAndGet();
                objectLifeLongMap.put(t, System.currentTimeMillis());
                objectUsedTimeMap.put(t, 0);
            }
        }
    }

    /**
     * Since the internal storage is a blocking pool,
     * if we tried to put the returned element
     * directly into the LinkedBlockingPool,
     * it might block he client if the queue is full.
     * But we do not want a client of an object
     * pool to block just for a mundane task like
     * returning an object to the pool.
     * So we have made the actual task of inserting
     * the object into the LinkedBlockingQueue as an
     * asynchronous task and submit it to an Executor
     * instance so that the client thread can return
     * immediately.
     *
     * @param <E> object want to return back;
     */
    private class ObjectReturner<E>
            implements Callable {
        private BlockingQueue queue;
        private E e;

        public ObjectReturner(BlockingQueue queue, E e) {
            this.queue = queue;
            this.e = e;
        }

        public Void call() {
            while (true) {
                try {
                    queue.put(e);
                    count_in_used.decrementAndGet();
                    count_in_pool.incrementAndGet();
                    break;
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
            return null;
        }

    }
}