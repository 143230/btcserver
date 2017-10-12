package test;

import com.btc.app.listener.SchedulerThread;
import com.btc.app.pool.BoundedBlockingPool;
import com.btc.app.pool.Pool;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.sql.Connection;
import java.util.*;

public class TestPool {
    public static void main(String[] args) throws IOException {
        String text = "【印度顶尖比特币交易所Unocoin开展与Blockchain钱包的合作】<a data-url=\"http://t.cn/RpG7VFn\" target=\"_blank\" href=\"https://weibo.cn/sinaurl/blockedbfbc96f7?luicode=10000011&lfid=1076033632226187&u=http%3A%2F%2Fwww.bitcoin86.com%2Fnews%2F16706.html&ep=FkTG0rGGO%2C3632226187%2CFkTG0rGGO%2C3632226187\" class=\"\"><span class=\"url-icon\"><img src=\"//h5.sinaimg.cn/upload/2015/09/25/3/timeline_card_small_web_default.png\"></span></i><span class=\"surl-text\">网页链接</a> 主要印度比特币交易所Unocoin已经与Blockchain.info旗下的钱包公司Blockchain展开合作。这样一来，Unocoin的40多万用户现在可以使用他们自己的Blockchain.info钱包购买比特币了。";
    }
}