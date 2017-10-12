package test;

import com.btc.app.spider.htmlunit.HtmlUnitBasicSpider;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by cuixuan on 2017/8/23.
 */
public class TestChromeSpider {
    private static WebDriver driver;

    public static void main(String[] args) throws InterruptedException {
        DesiredCapabilities capabilities;
        capabilities = DesiredCapabilities.phantomjs();
        capabilities.setJavascriptEnabled(true);
        //capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX,"Y");
        capabilities.setCapability("phantomjs.page.settings.userAgent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36");

//intialize driver and set capabilties

        driver = new PhantomJSDriver(capabilities);
//        WebDriver driver  = new HtmlUnitDriver(capabilities);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        driver.get("https://wx.qq.com/");
        String targetGroupName = "崔轩,JK,JK";

        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        WebElement image = driver.findElement(By.xpath(".//div[@class=\"login_box\"]/div[@class=\"qrcode\"]/img[@mm-src-load=\"qrcodeLoad\"]"));
        System.out.println(image.getAttribute("src"));
        while(true){
            List<WebElement> divisions = driver.findElements(By.xpath(".//h3[@class=\"nickname\"]"));
            for (WebElement division : divisions) {
                if(division.getText().equalsIgnoreCase(targetGroupName)){
                    division.click();
                    System.out.println("发现： "+division.getText());
                    if(!waitForChatWindow(targetGroupName)){
                        System.out.println("聊天窗口未出现");
                        continue;
                    }else{
                        System.out.println("发现聊天窗口");
                    }
                    /*List<WebElement> chats = driver.findElements(By.xpath(".//div[@class=\"plain\"]/pre[@ng-bind-html]"));
                    for(WebElement chat:chats){
                        System.out.println("聊天记录："+chat.getText());
                    }*/
                    WebElement element = new WebDriverWait(driver,10).until(
                            ExpectedConditions.presenceOfElementLocated(By.xpath(".//pre[@id=\"editArea\"]")));
//                    WebElement element = driver.findElement(By.xpath(".//pre[@id=\"editArea\"]"));
                    String sendmsg = "你好啊"+new Random().nextInt(100);
                    String jsexec = String.format("var scope = angular.element('#editArea').scope();scope.editAreaCtn = '%s';",sendmsg);
                    ((JavascriptExecutor) driver).executeScript(jsexec);

                    new WebDriverWait(driver,10).until(
                            ExpectedConditions.textToBePresentInElementLocated(By.xpath(".//pre[@id=\"editArea\"]"), sendmsg));

                    WebElement send = new WebDriverWait(driver,10).until(
                            ExpectedConditions.presenceOfElementLocated(By.xpath(".//a[@class=\"btn btn_send\"]")));
//                    WebElement element1 = driver.findElement(By.xpath(".//a[@class=\"btn btn_send\"]"));
                    if(element.getText().equalsIgnoreCase(sendmsg)){
                        System.out.println("点击文字： "+element.getText());
                        System.out.println("点击按钮："+send.getText());
                        send.click();
                    }
                    List<WebElement> chats = driver.findElements(By.xpath(".//div[@class=\"plain\"]/pre[@ng-bind-html]"));
                    for(WebElement chat:chats){
                        System.out.println("聊天记录："+chat.getText());
                    }
                    System.out.println("Success for sending Message.");
                    Thread.sleep(10000);
                }
            }
        }
    }
    private static boolean waitForChatWindow(String name){

        final String xpath = ".//div[@class=\"title_wrap\"]" +
                "/div[contains(@class,\"title\")]" +
                "/a[contains(@class,\"title_name\")]";
        String countxpath = ".//div[@class=\"title_wrap\"]" +
                "/div[contains(@class,\"title\")]" +
                "/span[contains(@class,\"title_count\")]";
        new WebDriverWait(driver,10).until(
                ExpectedConditions.textToBePresentInElementLocated(By.xpath(xpath), name));
        WebElement nameEle = driver.findElement(By.xpath(xpath));
        WebElement count = driver.findElement(By.xpath(countxpath));
        String titlename = nameEle.getText();
        if(titlename.equalsIgnoreCase(name)){
            System.out.println(titlename);
            System.out.println("群里人数:"+count.getText());
            return true;
        }
        return false;

    }
}
