package com.btc.app.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CoinNameMapper {
    private static final Map<String, String> coinChineseNameMap = new ConcurrentHashMap<String, String>();
    private static final Map<String, String> coinEnglishNameMap = new ConcurrentHashMap<String, String>();
    public static final String COIN_MARKET = "https://coinmarketcap.com";
    public static final Map<String, String> OTHER_PLATFORM_MAP = new HashMap<String, String>();

    static {
        coinChineseNameMap.put("BTC", "比特币");
        coinChineseNameMap.put("ETH", "以太坊");
        coinChineseNameMap.put("LTC", "莱特币");
        coinChineseNameMap.put("ETC", "以太经典");
        coinChineseNameMap.put("BCC", "BCC");

        OTHER_PLATFORM_MAP.put("poloniex", "https://poloniex.com");
        OTHER_PLATFORM_MAP.put("bitfinex", "https://www.bitfinex.com");
    }

    public static String getChineseName(String source) {
        if (coinChineseNameMap.containsKey(source.toLowerCase())) {
            return coinChineseNameMap.get(source.toLowerCase());
        }
        return source.toUpperCase();
    }
}
