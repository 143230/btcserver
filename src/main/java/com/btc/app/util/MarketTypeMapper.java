package com.btc.app.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MarketTypeMapper {
    private static final Map<String, Integer> coinNameMap = new ConcurrentHashMap<String, Integer>();
    private static final Map<Integer, String> coinMarketMap = new ConcurrentHashMap<Integer, String>();
    public static final String[] markets = {
            "AUD", "BRL", "CAD", "CHF", "CLP", "CNY", "CZK",
            "DKK", "EUR", "GBP", "HKD", "HUF", "IDR", "ILS",
            "INR", "JPY", "KRW", "MXN", "MYR", "NOK", "NZD",
            "PHP", "PKR", "PLN", "RUB", "SEK", "SGD", "THB",
            "TRY", "TWD", "ZAR", "BTC", "USD"
    };

    static {
        for (int i = 0; i < markets.length; i++) {
            coinNameMap.put(markets[i], i + 1);
            coinMarketMap.put(i + 1, markets[i]);
        }
    }

    public static int getMarketType(String source) {
        if (coinNameMap.containsKey(source.toUpperCase())) {
            return coinNameMap.get(source.toUpperCase());
        }
        return -1;
    }
    public static String getMarketNameType(int source) {
        if (coinMarketMap.containsKey(source)) {
            return coinMarketMap.get(source);
        }
        return "OTHER";
    }
}
