package com.btc.app.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EmojiMapper {
    private static final Map<String, String> emojiMap = new ConcurrentHashMap<String,String>();
    static {
        emojiMap.put("[笑cry]","\uD83D\uDE02");
        emojiMap.put("[赞]","\uD83D\uDC4D");
    }
    public static String getUTF8Emoji(String source){
        if(emojiMap.containsKey(source.toLowerCase()))return emojiMap.get(source.toLowerCase());
        return source;
    }
}
