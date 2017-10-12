package com.btc.app.bean;

import java.util.Date;

public class WeiboBean {
    private int id;
    private String uid;
    private String imageurl;
    private String wbid;
    private String wbname;
    private String from_device;
    private String text;
    private Date update_time;
    private String rawText;
    private String from_web;

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getFrom_web() {
        return from_web;
    }

    public void setFrom_web(String from_web) {
        this.from_web = from_web;
    }

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWbid() {
        return wbid;
    }

    public void setWbid(String wbid) {
        this.wbid = wbid;
    }

    public String getWbname() {
        return wbname;
    }

    public void setWbname(String wbname) {
        this.wbname = wbname;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFrom_device() {
        return from_device;
    }

    public void setFrom_device(String from_device) {
        this.from_device = from_device;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    @Override
    public String toString() {
        return rawText+"\t"+wbid+"\t"+from_device+"\t"+update_time;
    }
}
