package com.btc.app.push.weixin;

import com.btc.app.push.xinge.XinGePush;
import com.btc.app.spider.phantomjs.BasicSpider;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by cuixuan on 2017/8/23.
 */
public class WXAutoChatSpider extends BasicSpider {
    private Logger logger = Logger.getLogger(WXAutoChatSpider.class);
    public static final String WX_URL = "https://wx.qq.com/";
    private static final String NICK_NAME = "崔轩";

    public WXAutoChatSpider(WebDriver driver, String url) {
        super(driver, url);
        driver.get(url);
    }

    /**
     * Unused in this method.
     *
     * @param wait_time
     * @throws InterruptedException
     */
    @Override
    public void parseHtml(int wait_time) throws Exception {
        throw new Exception("Please Do Not Invoke This Method In Wx.");
    }

    public synchronized boolean sendMessage(String groupName, String msg) {
        if (driver == null) {
            driver = createDriver();
            driver.get(url);
        }
        String src = "";
        boolean isSuccess = false;
        while (!isSuccess) {
            try {
                while (true) {
                    if (isLoginState(5)) break;
                    WebElement image = new WebDriverWait(driver, 30).until(
                            ExpectedConditions.visibilityOfElementLocated(By.xpath(".//div[@class=\"login_box\"]" +
                                    "/div[@class=\"qrcode\"]/img[@mm-src-load=\"qrcodeLoad\"]")));
                    String current = image.getAttribute("src");
                    if (!current.equalsIgnoreCase(src)) {
                        logger.info("Please Scan This QrCode: " + current);
                        src = current;
                    }
                    if (isLoginState(120)) break;
                    driver.navigate().refresh();
                }
                List<WebElement> divs = new WebDriverWait(driver, 10).until(
                        ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(".//h3[@class=\"nickname\"]")));
                boolean found = false;
//                File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
//                System.out.println("File:" + srcFile);
                for (WebElement division : divs) {
                    if (division.getText().equals(groupName)) {
                        found = true;
                        division.click();
                        if (!waitForChatWindow(groupName)) {
                            logger.info("Click for the target not response: " + division.getText());
//                            srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
//                            System.out.println("File:" + srcFile);
                            break;
                        }

//                        srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
//                        System.out.println("File:" + srcFile);
                        new WebDriverWait(driver, 10).until(
                                ExpectedConditions.presenceOfElementLocated(By.xpath(".//pre[@id=\"editArea\"]")));
                        String jsexec = String.format("var scope = angular.element('#editArea').scope();scope.editAreaCtn = '%s';", msg);
                        ((JavascriptExecutor) driver).executeScript(jsexec);

                        boolean isRealEdited = new WebDriverWait(driver, 10).until(
                                ExpectedConditions.textMatches(By.xpath(".//pre[@id=\"editArea\"]"), Pattern.compile(".{1,}")));


//                        srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
//                        System.out.println("File:" + srcFile);
                        if (isRealEdited) {
                            WebElement send = new WebDriverWait(driver, 10).until(
                                    ExpectedConditions.presenceOfElementLocated(By.xpath(".//a[@class=\"btn btn_send\"]")));
                            send.click();

                            List<WebElement> chats = driver.findElements(By.xpath(".//div[@class=\"plain\"]/pre[@ng-bind-html]"));
                            if (chats.size() > 0) {
                                WebElement chatContent = chats.get(chats.size() - 1);
                                if (chatContent.getText().length() > 0) {
                                    logger.info("Success Send Message: " + chatContent.getText() + " To Group: " + groupName);
                                    isSuccess = true;
                                    break;
                                } else {
                                    logger.info("Send Message Failed, Click send button not response.");
                                }
                            }
                        } else {
                            logger.info("Js Edit Content Failed: " + msg);
                            break;
                        }
                    }
                }
                if (!found) {
                    logger.info("The Group Not Found in the visible list.");
                }


            } catch (TimeoutException e) {
                logger.info(e.getMessage());
                continue;
            }
        }
        return isSuccess;
    }

    private boolean isLoginState(int time) {
        String xpath = ".//div[@class=\"info\"]/h3[@class=\"nickname\"]/span[@ng-bind-html=\"account.NickName\"]";
        try {
            WebElement element = new WebDriverWait(driver, time).until(
                    ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
            String accountName = element.getText();
            if (!accountName.trim().equalsIgnoreCase(NICK_NAME)) {
                logger.info("WeChat Account is Not Right, Real: " + element.getText().trim() + ", Needed: " + NICK_NAME);
                throw new IllegalStateException("WeChat Account is Not Right, Real: " + element.getText().trim() + ", Needed: " + NICK_NAME);
            }
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    private boolean waitForChatWindow(String name) {
        final String xpath = ".//div[@class=\"title_wrap\"]" +
                "/div[contains(@class,\"title\")]" +
                "/a[contains(@class,\"title_name\")]";
        String countxpath = ".//div[@class=\"title_wrap\"]" +
                "/div[contains(@class,\"title\")]" +
                "/span[contains(@class,\"title_count\")]";
        new WebDriverWait(driver, 10).until(
                ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
        WebElement nameEle = driver.findElement(By.xpath(xpath));
        WebElement count = driver.findElement(By.xpath(countxpath));
//        String titlename = nameEle.getText();
        System.out.println("群里人数:" + count.getText());
        return true;

    }


    public static void main(String[] args) {
        try {
            DesiredCapabilities capabilities;
            capabilities = DesiredCapabilities.phantomjs();
            capabilities.setJavascriptEnabled(true);
            //capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX,"Y");
            capabilities.setCapability("phantomjs.page.settings.userAgent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36");

            capabilities.setCapability("phantomjs.page.settings.cookie", "pgv_pvi=1419327488; RK=OblbvL7KY3; pac_uid=1_873059043; tvfe_boss_uuid=a5ea7d477fa0e4b2; hjstat_uv=22241826703621009564|679544; sd_userid=30201501039634761; sd_cookie_crttime=1501039634761; eas_sid=S1B550Q3U4t0r103t233z5p0u6; luin=o0873059043; lskey=00010000856aff81011406935cf71bce02d1d14d3a3c7b14ed69f0dbab9659f25cc5ddbc1a8fe206060970ec; o_cookie=873059043; _ga=GA1.2.1265991810.1504076456; pgv_pvid=7492851044; webwxuvid=8c475a40ede381a84287c3c74624d6b9b2ee15d53734c1e068ae9ee39e91690e06155bcf640adc781255ce8c7a8fabee; pgv_si=s9249119232; pgv_info=ssid=s6761392208; refreshTimes=5; MM_WX_NOTIFY_STATE=1; MM_WX_SOUND_STATE=1; mm_lang=zh_CN; webwx_auth_ticket=CIsBEOjskKoJGoABWo2WqvahrYQ093rXTWhEj3LawNwRUUJRF5g9npEozJnDHnxqQ0XgDl03QAOCLpGMyikAtMvP+qJ2e8HEMr3UysHxt7t81MMbcssaMBegkPT8Vw7ddPkcusOlKHJNqnAs60ip7OSXUOEj4xM2z27XScT6YW/WzYXxipUoJn1Pxqg=; login_frequency=1; last_wxuin=2594108040; wxloadtime=1505646052_expired; ptisp=ctc; ptcz=bee437bfaee698bd79c620142f26b4a75789178af94aeae51ac4afdfe95f5c9f; pt2gguin=o0873059043; uin=o0873059043; skey=@yDebKxHZH; wxpluginkey=1505641141; wxuin=2594108040; wxsid=ESTdr7vyYSQGfU5k; webwx_data_ticket=gSecFZMm88RIkRq6gBj0o/dM");
//intialize driver and set capabilties

            WebDriver driver = new PhantomJSDriver(capabilities);
//            WebDriver driver = createDriver();
            WXAutoChatSpider spider = new WXAutoChatSpider(driver, WX_URL);
            spider.sendMessage("比特币信息推送", "测试发送内容");
            for (int i = 0; i < 10; i++) {
                Thread.sleep(1000);
                System.out.println(i);
            }
            spider.sendMessage("比特币信息推送", "重复发送内容");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
