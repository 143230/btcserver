package com.btc.app.pool;

/**
 * Represents a cached pool of objects.
 *
 * @param <T> the type of object to pool.
 *            <p>
 *            source: http://ifeve.com/generic-concurrent-object-pool/
 * @author Swaranga
 */

public interface Pool<T> {
    /**
     * Returns an instance from the pool.
     * The call may be a blocking one or a non-blocking one
     * and that is determined by the internal implementation.
     * <p>
     * If the call is a blocking call,
     * the call returns immediately with a valid object
     * if available, else the thread is made to wait
     * until an object becomes available.
     * In case of a blocking call,
     * it is advised that clients react
     * to {@link InterruptedException} which might be thrown
     * when the thread waits for an object to become available.
     * <p>
     * If the call is a non-blocking one,
     * the call returns immediately irrespective of
     * whether an object is available or not.
     * If any object is available the call returns it
     * else the call returns < code >null< /code >.
     * <p>
     * The validity of the objects are determined using the
     * {@link Validator} interface, such that
     * an object < code >o< /code > is valid if
     * < code > Validator.isValid(o) == true < /code >.
     *
     * @return T one of the pooled objects.
     */
    T get();

    /**
     * Releases the object and puts it back to the pool.
     * <p>
     * The mechanism of putting the object back to the pool is
     * generally asynchronous,
     * however future implementations might differ.
     *
     * @param t the object to return to the pool
     */

    void release(T t);

    /**
     * Shuts down the pool. In essence this call will not
     * accept any more requests
     * and will release all resources.
     * Releasing resources are done
     * via the < code >invalidate()< /code >
     * method of the {@link Validator} interface.
     */

    void shutdown();

    int size();

    /**
     * destroy a target object getting from the pool
     *
     * @param t the object will be destroyed.
     */
    void destroy(T t);

    /**
     * Represents the functionality to
     * validate an object of the pool
     * and to subsequently perform cleanup activities.
     *
     * @param < T > the type of objects to validate and cleanup.
     * @author Swaranga
     */
    public static interface Validator<T> {
        /**
         * Checks whether the object is valid.
         *
         * @param t the object to check.
         * @return <code>true</code>
         * if the object is valid else <code>false</code>.
         */
        public boolean isValid(T t);

        /**
         * Performs any cleanup activities
         * before discarding the object.
         * For example before discarding
         * database connection objects,
         * the pool will want to close the connections.
         * This is done via the
         * <code>invalidate()</code> method.
         *
         * @param t the object to cleanup
         */

        public void invalidate(T t);

    }
}

