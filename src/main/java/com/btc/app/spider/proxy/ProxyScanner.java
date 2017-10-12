package com.btc.app.spider.proxy;

import com.btc.app.bean.ProxyBean;

import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class ProxyScanner implements Runnable{

    private BlockingQueue<ProxyBean> inQueue;
    private BlockingQueue<ProxyBean> outQueue;
    private Map<ProxyBean, Boolean> proxyMap;

    public ProxyScanner(BlockingQueue<ProxyBean> inQueue, BlockingQueue<ProxyBean> outQueue,Map<ProxyBean, Boolean> proxyMap) {
        this.inQueue = inQueue;
        this.outQueue = outQueue;
        this.proxyMap = proxyMap;
    }

    public void run() {
        while (true){
            ProxyBean bean = null;
            try{
                bean = inQueue.take();
                if(proxyMap.containsKey(bean) && !proxyMap.get(bean))continue;
                //System.out.println("Scanning Proxy: "+bean);
                long time = ProxyUtils.HttpProxy(bean);
                //System.out.println("Success Test Http Proxy: "+bean+"\t With Time: "+time);
                bean.setTime(time);
                //proxyMap.put(bean,true);
                //outQueue.add(bean);
                //System.out.println(outQueue.size());

                /*time = ProxyUtils.foreignHttpsProxy(bean);
                System.out.println("Success Test Foreign Proxy: "+bean+"\t With Time: "+time);
                if(time > bean.getTime()){
                    bean.setTime(time);
                }*/
                //proxyMap.put(bean,true);
                //outQueue.add(bean);
                //System.out.println(outQueue.size());

                time = ProxyUtils.HttpsProxy(bean);
                System.out.println("Success Test Https Proxy: "+bean+"\t With Time: "+time);
                //bean = bean.copy(time);
                if(time > bean.getTime()){
                    bean.setTime(time);
                }
                outQueue.add(bean);
                proxyMap.put(bean, true);
                //System.out.println(outQueue.size());
            } catch (SocketTimeoutException e){
                //System.out.println("Proxy: " + bean + " Timeout");
            } catch (InterruptedException ie){
                break;
            }catch (Exception e){
                //e.printStackTrace();
                //System.out.println(e.getMessage());
                continue;
            }
        }

    }
}
