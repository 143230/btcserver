package com.btc.app.bean;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NewsBean {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private int id;
    private String url;
    private String webname;
    private String webicon;
    private int new_type;
    private String imageurl;
    private String title;
    private String abstracts;
    private Date update_time;

    public String getWebname() {
        return webname;
    }

    public void setWebname(String webname) {
        this.webname = webname;
    }

    public String getWebicon() {
        return webicon;
    }

    public void setWebicon(String webicon) {
        this.webicon = webicon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getNew_type() {
        return new_type;
    }

    public void setNew_type(int new_type) {
        this.new_type = new_type;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAbstracts() {
        return abstracts;
    }

    public void setAbstracts(String abstracts) {
        this.abstracts = abstracts;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    private String dateToString(Date date){
        if(date == null)return "-";
        return sdf.format(date);
    }
    @Override
    public String toString() {
        return this.url+"\t:\t"+this.title+"\t:\t"+dateToString(update_time);
    }
}
