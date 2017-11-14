package com.btc.app.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.btc.app.util.MarketTypeMapper;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static com.btc.app.util.MarketTypeMapper.getMarketNameType;

public class CoinBean {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @JSONField(serialize = false)
    private int id;
    private String url;
    private String englishname;
    private String chinesename;
    private String symbol;
    private String coin_id;
    private BigDecimal price;
    private BigDecimal yprice;
    private BigDecimal percent;
    private BigDecimal turnvolume;
    private BigDecimal turnnumber;
    private Date update_time;
    private int market_type;
    private int rank;
    private String platform;
    private CoinInfoBean infoBean;

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCoin_id() {
        return coin_id;
    }

    public void setCoin_id(String coin_id) {
        this.coin_id = coin_id;
    }

    public int getRank() {
        return rank;
    }

    public CoinInfoBean getInfoBean() {
        return infoBean;
    }

    public void setInfoBean(CoinInfoBean infoBean) {
        this.infoBean = infoBean;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public BigDecimal getYprice() {

        return yprice;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setYprice(BigDecimal yprice) {
        this.yprice = yprice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEnglishname() {
        return englishname;
    }

    public void setEnglishname(String englishname) {
        this.englishname = englishname;
    }

    public String getChinesename() {
        return chinesename;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    public BigDecimal getTurnnumber() {
        return turnnumber;
    }

    public void setTurnnumber(BigDecimal turnnumber) {
        this.turnnumber = turnnumber;
    }

    public BigDecimal getTurnvolume() {

        return turnvolume;
    }

    public void setTurnvolume(BigDecimal turnvolume) {
        this.turnvolume = turnvolume;
    }

    public BigDecimal getPercent() {

        return percent;
    }

    public void setPercent(BigDecimal percent) {
        this.percent = percent;
    }

    public BigDecimal getPrice() {

        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setChinesename(String chinesename) {
        this.chinesename = chinesename;
    }

    public int getMarket_type() {
        return market_type;
    }

    public void setMarket_type(int market_type) {
        this.market_type = market_type;
    }

    @Override
    public String toString() {
        return "排名["+rank+"]\t中文名：["+chinesename+"]\t英文名：["+englishname+"]\t价格：["+getMarketSymbol()+bigDecimalToString(price)+"]\t24H成交额：["
                +getMarketSymbol()+bigDecimalToString(turnvolume)+"]\t24H成交量：["+bigDecimalToString(turnnumber)
                +"]\t涨跌幅：["+bigDecimalToString(percent) +"]\t时间：["+dateToString(update_time)
                +"]\t昨日价格：["+bigDecimalToString(yprice)+"]\t市场类型：["+marketType()+"]\t平台：["+platform+"]";
    }

    private String bigDecimalToString(BigDecimal decimal){
        if(decimal == null)return "-";
        return decimal.setScale(6,BigDecimal.ROUND_HALF_UP).toString();
    }

    public String getMarketSymbol(){
        switch (market_type){
            case 1:return "￥";
            case 2:return "฿";
            case 3:return "E";
            case 4:return "$";
            default:return getMarketNameType(market_type);
        }

    }

    public String getTypeStr(){
        return this.marketType();
    }

    public String marketType(){
        if(this.market_type >= 64){
            int mfrom = market_type>>6;
            int mto = market_type % 64;
            return getMarketNameType(mfrom)+"/"+getMarketNameType(mto);
        }
        return getMarketNameType(this.market_type);
    }

    private String dateToString(Date date){
        if(date == null)return "-";
        return sdf.format(date);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)return false;
        if(!(obj instanceof CoinBean))return false;
        CoinBean bean = (CoinBean)obj;
        boolean a = bean.getMarket_type() == market_type;
        boolean b = coin_id == null ? bean.getCoin_id() == null : coin_id.equals(bean.getCoin_id());
        boolean c = platform == null ? bean.getPlatform() == null : platform.equals(bean.getPlatform());
        return a && b && c;
    }

    @Override
    public int hashCode() {
        return market_type
                + (coin_id == null ? 0 :coin_id.toUpperCase().hashCode())
                + (platform == null ? 0 : platform.hashCode());
    }

    public static void main(String[] args) {
        HashMap<CoinBean,CoinBean> map = new HashMap<CoinBean, CoinBean>();
        CoinBean bean = new CoinBean();
        bean.setPlatform("a");
        bean.setMarket_type(1);
        bean.setChinesename("比特币");
        bean.setEnglishname("btc");
        map.put(bean,bean);
        bean = new CoinBean();
        bean.setPlatform("a");
        bean.setChinesename("狗狗币");
        bean.setMarket_type(1);
        bean.setEnglishname("btc");
        map.put(bean,bean);
        System.out.println(map.size());
        for(CoinBean bean1:map.keySet()){
            System.out.println(bean1);
        }
    }
}
