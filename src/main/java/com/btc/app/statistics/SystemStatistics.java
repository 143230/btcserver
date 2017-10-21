package com.btc.app.statistics;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

public class SystemStatistics {

    private static class LazyHolder {
        private static final SystemStatistics INSTANCE = new SystemStatistics();
    }

    public static final SystemStatistics getInstance() {
        return SystemStatistics.LazyHolder.INSTANCE;
    }

    private final Map<String, Integer> statMap;
    private ThreadPoolExecutor executor;

    private SystemStatistics(){
        statMap = new ConcurrentHashMap<String, Integer>();
    }

    public void setThreadPoolExecutor(ThreadPoolExecutor executor){
        this.executor = executor;
    }
    public void set(String key,int value){
        statMap.put(key,value);
    }

    public void add(String key,int value){
        synchronized (statMap){
            Integer val = statMap.get(key);
            if(val == null){
                statMap.put(key,value);
            }else{
                statMap.put(key,val+value);
            }
        }
    }

    public void sub(String key,int value){
        synchronized (statMap){
            Integer val = statMap.get(key);
            if(val == null){
                statMap.put(key,-value);
            }else{
                statMap.put(key,val-value);
            }
        }
    }

    private void updateRealTimeData(){
        if(executor != null){
            int count = executor.getActiveCount();
            statMap.put("activeThreadCount",count);
        }
    }

    public int get(String key){
        updateRealTimeData();
        return statMap.get(key);
    }

    public Map<String, Integer> getAll(){
        updateRealTimeData();
        return statMap;
    }
}
