package ru.jilime.documentum;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.Select;

import java.util.concurrent.TimeUnit;

public class WebtopTest {
    private WebDriver driver;
    private String baseUrl;

    @Before
    public void setUp() throws Exception {
        System.setProperty("webdriver.ie.driver", "D:\\develop\\gnuwin32\\bin\\IEDriverServer.exe");
        DesiredCapabilities capabilities = DesiredCapabilities.htmlUnit();
/*        capabilities.setCapability(CapabilityType.SUPPORTS_JAVASCRIPT, true);
        capabilities.setCapability(CapabilityType.SUPPORTS_FINDING_BY_CSS,true);
        capabilities.setCapability(CapabilityType.BROWSER_NAME,"internet explorer");
        capabilities.setCapability(CapabilityType.VERSION,"8");*/
        capabilities.setJavascriptEnabled(true);
        capabilities.setBrowserName("internet explorer");
        capabilities.setVersion("8");

        driver = new InternetExplorerDriver(capabilities);
        //driver = new HtmlUnitDriver(capabilities);
        baseUrl = "";
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    @Test
    public void testSad() throws Exception {
        driver.get(baseUrl);
        driver.findElement(By.id("LoginUsername")).clear();
        driver.findElement(By.id("LoginUsername")).sendKeys("");
        driver.findElement(By.id("LoginPassword")).clear();
        driver.findElement(By.id("LoginPassword")).sendKeys("");
        Select select = new Select(driver.findElement(By.id("DocbaseName")));
        select.selectByVisibleText("");
        driver.findElement(By.name("Login_loginButton_0")).click();
        synchronized (driver) {
            driver.wait(10000);
        }
        //((JavascriptExecutor) driver).executeScript("safeCall(postServerEvent2,\"Login_0\",null,\"Login_loginButton_0\",\"Login_0\",\"onLogin\");");
        //System.out.println(driver.getPageSource());
        //System.out.println(driver.getPageSource());
        driver.switchTo().frame("view").switchTo().frame("tabbar");
        ((JavascriptExecutor) driver).executeScript("var value='inboxstreamline';document.TabBar_0.TabBar_tabView_hidden_0.value=value;storeScrollPosition('TabBar_0', '__dmfHiddenX', '__dmfHiddenY');safeCall(postServerEvent,\"TabBar_0\", \"TabBar_inboxstreamline_0\",\"TabBar_0\",\"onTabClick\");");
        driver.switchTo().defaultContent();
/*        System.out.println(driver.getPageSource());*/
        driver.switchTo().frame("view").switchTo().frame("content").findElement(By.name("TasksList_ActionLink_1")).click();
        driver.switchTo().defaultContent().switchTo().frame("titlebar").findElement(By.name("TitleBar_logout_0")).click();
    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
    }
}