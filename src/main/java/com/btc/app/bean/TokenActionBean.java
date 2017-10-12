package com.btc.app.bean;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TokenActionBean {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final int ADD_TAG_FOR_TOKEN = 1;
    public static final int DEL_TAG_FOR_TOKEN = 2;
    private int id;
    private int uid;
    private String token;
    private String symbol;
    private int actiontype;
    private String other;
    private Date actiontime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getActiontype() {
        return actiontype;
    }

    public void setActiontype(int actiontype) {
        this.actiontype = actiontype;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public Date getActiontime() {
        return actiontime;
    }

    public void setActiontime(Date actiontime) {
        this.actiontime = actiontime;
    }
}
