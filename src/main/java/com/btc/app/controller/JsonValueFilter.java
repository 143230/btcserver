package com.btc.app.controller;

import com.alibaba.fastjson.serializer.ValueFilter;

import java.math.BigDecimal;

public class JsonValueFilter implements ValueFilter {
    public Object process(Object object, String name, Object value) {
        if(value instanceof BigDecimal){
            BigDecimal decimal = (BigDecimal)value;
            decimal.setScale(8, BigDecimal.ROUND_HALF_UP);
            return decimal.toPlainString();
        }
        return value;
    }
}
