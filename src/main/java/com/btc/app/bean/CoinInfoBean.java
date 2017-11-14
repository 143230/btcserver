package com.btc.app.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.Date;

public class CoinInfoBean implements Serializable{
    private String symbol;
    private String chinesename;
    private String englishname;
    private String imageurl;
    private String coinid;
    private long total_num;
    private long cur_num;
    private Date publish_time;
    private String websites;
    private String block_stations;
    private String description;

    public long getTotal_num() {
        return total_num;
    }

    public void setTotal_num(long total_num) {
        this.total_num = total_num;
    }

    public long getCur_num() {
        return cur_num;
    }

    public void setCur_num(long cur_num) {
        this.cur_num = cur_num;
    }

    public Date getPublish_time() {
        return publish_time;
    }

    public void setPublish_time(Date publish_time) {
        this.publish_time = publish_time;
    }

    public String getWebsites() {
        return websites;
    }

    public void setWebsites(String websites) {
        this.websites = websites;
    }

    public String getBlock_stations() {
        return block_stations;
    }

    public void setBlock_stations(String block_stations) {
        this.block_stations = block_stations;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoinid() {
        return coinid;
    }

    public void setCoinid(String coinid) {
        this.coinid = coinid;
    }

    @JSONField(serialize = false)
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getChinesename() {
        return chinesename;
    }

    public void setChinesename(String chinesename) {
        this.chinesename = chinesename;
    }

    public String getEnglishname() {
        return englishname;
    }

    public void setEnglishname(String englishname) {
        this.englishname = englishname;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }
}
