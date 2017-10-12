package com.btc.app.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CoinNameMapper {
    private static final Map<String, String> coinChineseNameMap = new ConcurrentHashMap<String, String>();
    private static final Map<String, String> coinEnglishNameMap = new ConcurrentHashMap<String, String>();

    static {
        coinChineseNameMap.put("BTC", "比特币");
        coinChineseNameMap.put("ETH", "以太坊");
        coinChineseNameMap.put("LTC", "莱特币");
        coinChineseNameMap.put("ETC", "以太经典");
        coinChineseNameMap.put("BCC", "BCC");
    }

    public static String getChineseName(String source) {
        if (coinChineseNameMap.containsKey(source.toLowerCase())) {
            return coinChineseNameMap.get(source.toLowerCase());
        }
        return source.toUpperCase();
    }
}
