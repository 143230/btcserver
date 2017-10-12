package com.btc.app.bean;

import java.net.Proxy;

public class ProxyBean implements Comparable<ProxyBean> {
    private String host;
    private int port;
    private Proxy.Type type;
    private long time;
    private String country;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public ProxyBean(String host, int port, Proxy.Type type, String country) {
        this.host = host;
        this.port = port;
        this.type = type;
        this.country = country;
    }

    public ProxyBean(String host, int port, Proxy.Type type, long time) {
        this.host = host;
        this.port = port;
        this.type = type;
        this.time = time;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public ProxyBean copy(long time){
        return new ProxyBean(host,port,type,time);
    }

    public Proxy.Type getType() {
        return type;
    }

    public void setType(Proxy.Type type) {
        this.type = type;
    }

    public int getPort() {

        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {

        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public int hashCode() {
        return port+host.hashCode()+type.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)return false;
        if(!(obj instanceof ProxyBean))return false;
        ProxyBean bean = (ProxyBean) obj;
        boolean a = bean.getHost() == host;
        boolean b = bean.getPort() == port;
        boolean c = bean.getType() == type;
        return a && b && c;
    }

    @Override
    public String toString() {
        return host+":"+port+"\t"+type;
    }

    public int compareTo(ProxyBean o) {
        return ((Long)time).compareTo(o.getTime());
    }
}
