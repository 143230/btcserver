package test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import com.machinepublishers.jbrowserdriver.Timezone;
import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.Settings;
import org.openqa.selenium.WebElement;

import java.util.List;

public class TestJBrowserSpider {
    public static void main(String[] args) throws InterruptedException {

        // You can optionally pass a Settings object here,
        // constructed using Settings.Builder
        JBrowserDriver driver = new JBrowserDriver(Settings.builder().
                timezone(Timezone.ASIA_SHANGHAI).build());

        // This will block for the page load and any
        // associated AJAX requests
        driver.get("https://wx.qq.com");

        // You can get status code unlike other Selenium drivers.
        // It blocks for AJAX requests and page loads after clicks
        // and keyboard events.
        System.out.println(driver.getStatusCode());

        WebElement image = driver.findElement(By.xpath(".//div[@class=\"qrcode\"]/img[@class=\"img\"]"));
        System.out.println(image.getAttribute("src"));
        while(true){
            List<WebElement> divisions = driver.findElements(By.xpath(".//h3[@class=\"nickname\"]"));
            for (WebElement division : divisions) {
                if(division.getText().equalsIgnoreCase("崔轩,JK,JK")){
                    System.out.println();
                    division.click();
                    System.out.println("发现： "+division.getText());
                    Thread.sleep(10000);
                    List<WebElement> chats = driver.findElements(By.xpath(".//div[@class=\"plain\"]/pre[@ng-bind-html]"));
                    for(WebElement chat:chats){
                        System.out.println("聊天记录："+chat.getText());
                    }
                    WebElement element = driver.findElement(By.xpath(".//pre[@id=\"editArea\"]"));
                    element.sendKeys("你好");
                    Thread.sleep(10000);
                    if(element.getText().equalsIgnoreCase("你好")){
                        System.out.println("点击文字： "+element.getText());
                        WebElement element1 = driver.findElement(By.xpath(".//a[@class=\"btn btn_send\"]"));
                        System.out.println("点击按钮："+element1.getText());
                        element1.click();
                    }
                }
            }
            if(divisions == null ||divisions.size() <= 0)continue;
        }
/*
        // Returns the page source in its current state, including
        // any DOM updates that occurred after page load
        System.out.println(driver.getPageSource());

        // Close the browser. Allows this thread to terminate.
        driver.quit();*/
    }
}
