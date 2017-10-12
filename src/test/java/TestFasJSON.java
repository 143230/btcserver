import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.btc.app.bean.CoinBean;

import java.util.ArrayList;
import java.util.List;

public class TestFasJSON {
    public static void main(String[] args){


        List<CoinBean> list=new ArrayList<CoinBean>();
        for(int i=0;i<1;i++)
        {
            CoinBean entity=new CoinBean();
            entity.setRank(1);
            entity.setPlatform("a");
            entity.setMarket_type(1);
            entity.setChinesename("比特币");
            entity.setEnglishname("btc");
            //list.add(entity);
            list.add(0,entity);
        }
        String json= JSON.toJSONString(list);
        System.out.println(json);
        /*
        QuoteFieldNames———-输出key时是否使用双引号,默认为true
        WriteMapNullValue——–是否输出值为null的字段,默认为false
        WriteNullNumberAsZero—-数值字段如果为null,输出为0,而非null
        WriteNullListAsEmpty—–List字段如果为null,输出为[],而非null
        WriteNullStringAsEmpty—字符类型字段如果为null,输出为”“,而非null
        WriteNullBooleanAsFalse–Boolean字段如果为null,输出为false,而非null
        */
        //使用双引号
        System.out.println(JSONObject.toJSONString(list, SerializerFeature.QuoteFieldNames));
        //使用缩进
        System.out.println(JSON.toJSONString(list, SerializerFeature.WriteNullNumberAsZero, SerializerFeature.PrettyFormat));
        //输出值为null的字段
        System.out.println(JSONObject.toJSONString(list, SerializerFeature.WriteMapNullValue));
        System.out.println(JSONObject.toJSONString(list, SerializerFeature.WriteNullNumberAsZero));
        System.out.println(JSONObject.toJSONString(new ArrayList<CoinBean>(), SerializerFeature.WriteNullListAsEmpty));
        System.out.println(JSONObject.toJSONString(list, SerializerFeature.WriteNullStringAsEmpty));
        System.out.println(JSONObject.toJSONString(list, SerializerFeature.SortField));
    }
}
