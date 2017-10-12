import com.btc.app.listener.SchedulerThread;
import com.btc.app.service.impl.CoinServiceImpl;
import com.btc.app.service.impl.NewsServiceImpl;
import com.btc.app.service.impl.WeiboServiceImpl;

public class TestSchedulerThread {
    public static void main(String[] args) {
        Thread thread = new SchedulerThread(new CoinServiceImpl(),new NewsServiceImpl(),new WeiboServiceImpl());
        thread.start();
    }
}
