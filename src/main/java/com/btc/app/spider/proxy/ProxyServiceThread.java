package com.btc.app.spider.proxy;

import com.btc.app.bean.ProxyBean;
import com.btc.app.listener.CustomRejectedExecutionHandler;
import com.btc.app.listener.CustomThreadFactory;
import com.btc.app.push.xinge.XinGePush;

import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.Level;

public class ProxyServiceThread extends  Thread{

    private static class LazyHolder {
        private static final ProxyServiceThread INSTANCE = new ProxyServiceThread();
    }
    public static final ProxyServiceThread getInstance() {
        return ProxyServiceThread.LazyHolder.INSTANCE;
    }

    private ThreadPoolExecutor executor;
    private BlockingQueue<ProxyBean> inQueue;
    private BlockingQueue<ProxyBean> outQueue;
    private Map<ProxyBean, Boolean> proxyMap;

    public ProxyBean getProxy() throws InterruptedException {
        return outQueue.take();
    }

    public ProxyBean getProxy(int timeout,TimeUnit unit) throws InterruptedException {
        return outQueue.poll(timeout,unit);
    }

    public int size(){
        return outQueue.size();
    }

    public void releaseProxy(final ProxyBean bean) throws InterruptedException {
        executor.submit(new Runnable() {
            public void run() {
                try {
                    outQueue.put(bean);
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                }
            }
        });
    }

    private ProxyServiceThread(){
        inQueue  = new LinkedBlockingQueue<ProxyBean>();
        outQueue = new LinkedBlockingQueue<ProxyBean>();
        proxyMap = new ConcurrentHashMap<ProxyBean, Boolean>();
        executor = new ThreadPoolExecutor(
                20,
                100,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(10),
                new CustomThreadFactory(),
                new CustomRejectedExecutionHandler());
    }
    public void run() {
        for(int i=0;i<10;i++) {
            executor.submit(new ProxyScanner(inQueue, outQueue, proxyMap));
        }
        System.out.println("--------开始执行-----------");
        for(int i=0;;i++){
            try {
                if(i%60 == 0) {
                    executor.submit(new Data5UHtmlUnitSpiderService(inQueue, "http://www.data5u.com/free/gngn/index.shtml"));
                    executor.submit(new Data5UHtmlUnitSpiderService(inQueue, "http://www.data5u.com/free/index.shtml"));
                    executor.submit(new Data5UHtmlUnitSpiderService(inQueue, "http://www.data5u.com/free/gnpt/index.shtml"));
                    executor.submit(new Data5UHtmlUnitSpiderService(inQueue, "http://www.data5u.com/free/gngn/index.shtml"));
                    executor.submit(new Data5UHtmlUnitSpiderService(inQueue, "http://www.data5u.com/free/gwgn/index.shtml"));
                    executor.submit(new Data5UHtmlUnitSpiderService(inQueue, "http://www.data5u.com/free/gwpt/index.shtml"));
                    executor.submit(new XiCiDaiLiHtmlUnitSpiderService(inQueue, "http://www.xicidaili.com/nn/"));
                    executor.submit(new XiCiDaiLiHtmlUnitSpiderService(inQueue, "http://www.xicidaili.com/nt/"));
                    executor.submit(new XiCiDaiLiHtmlUnitSpiderService(inQueue, "http://www.xicidaili.com/wn/"));
                    executor.submit(new XiCiDaiLiHtmlUnitSpiderService(inQueue, "http://www.xicidaili.com/wt/"));
                    executor.submit(new KuaiDaiLiHtmlUnitSpiderService(inQueue,"http://123.206.57.100/free/inha/"));
                    executor.submit(new KuaiDaiLiHtmlUnitSpiderService(inQueue,"http://123.206.57.100/free/intr/"));
                    executor.submit(new KuaiDaiLiHtmlUnitSpiderService(inQueue,"http://www.ip3366.net/free/?stype=1"));
                    executor.submit(new KuaiDaiLiHtmlUnitSpiderService(inQueue,"http://www.ip3366.net/free/?stype=2"));
                    executor.submit(new KuaiDaiLiHtmlUnitSpiderService(inQueue,"http://www.ip3366.net/free/?stype=3"));
                    executor.submit(new KuaiDaiLiHtmlUnitSpiderService(inQueue,"http://www.ip3366.net/free/?stype=4"));
                    executor.submit(new ThreeOneFHtmlUnitSpiderService(inQueue,"http://31f.cn/http-proxy/"));
                    executor.submit(new ThreeOneFHtmlUnitSpiderService(inQueue,"http://31f.cn/https-proxy/"));
                    executor.submit(new ThreeOneFHtmlUnitSpiderService(inQueue,"http://31f.cn/socks-proxy/"));
                    System.out.println("Current Proxy Number: "+proxyMap.size());
                }
                sleep(1000);
            } catch (Exception e){
                System.out.println("ProxyServiceThread Was been Interrupted.");
                executor.shutdownNow();
                break;
            }
        }
        System.out.println("____FUCK TIME:" + System.currentTimeMillis());
    }

    public static void main(String[] args) {
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("org.apache.http.client").setLevel(Level.OFF);
        Thread thread = ProxyServiceThread.getInstance();
        thread.start();
    }
}
