package ru.jilime.documentum;

/*
Copyright 2013 Jilime.ru

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.Select;

import java.util.concurrent.TimeUnit;

public class WebtopChecks {
    private static WebDriver driver;
    private static String baseUrl;
    private static String m_user;
    private static String m_password;

    public WebtopChecks(String url, String user, String password) {
        m_user = user;
        m_password = password;
        baseUrl = url;
    }

    public void setUp() throws Exception {
        System.setProperty("webdriver.ie.driver", "D:\\develop\\gnuwin32\\bin\\IEDriverServer.exe");
        DesiredCapabilities capabilities = DesiredCapabilities.htmlUnit();
        capabilities.setJavascriptEnabled(true);
        capabilities.setBrowserName("internet explorer");
        capabilities.setVersion("8");

        driver = new InternetExplorerDriver();
        //driver = new HtmlUnitDriver(capabilities);
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    public long testManagerInbox() throws Exception {
        setUp();
        driver.get(baseUrl);
        driver.findElement(By.id("LoginUsername")).clear();
        driver.findElement(By.id("LoginUsername")).sendKeys(m_user);
        driver.findElement(By.id("LoginPassword")).clear();
        driver.findElement(By.id("LoginPassword")).sendKeys(m_password);
        Select select = new Select(driver.findElement(By.id("DocbaseName")));
        select.selectByVisibleText("MC");
        driver.findElement(By.name("Login_loginButton_0")).click();
        synchronized (driver) {
            driver.wait(5000);
        }
        driver.switchTo().frame("view").switchTo().frame("tabbar");
        ((JavascriptExecutor) driver).executeScript("var value='inboxstreamline';document.TabBar_0.TabBar_tabView_hidden_0.value=value;storeScrollPosition('TabBar_0', '__dmfHiddenX', '__dmfHiddenY');safeCall(postServerEvent,\"TabBar_0\", \"TabBar_inboxstreamline_0\",\"TabBar_0\",\"onTabClick\");");
        synchronized (driver) {
            driver.wait(5000);
        }
        driver.switchTo().defaultContent();
        long start = System.nanoTime() / 1000;
        driver.switchTo().frame("view").switchTo().frame("content").findElement(By.name("TasksList_ActionLink_1")).click();
        long elapsedTimeSec = System.nanoTime() / 1000 - start;
        synchronized (driver) {
            driver.wait(5000);
        }
        driver.switchTo().defaultContent().switchTo().frame("titlebar").findElement(By.name("TitleBar_logout_0")).click();
        tearDown();
        return TimeUnit.SECONDS.convert(elapsedTimeSec, TimeUnit.NANOSECONDS);
    }

    public void tearDown() throws Exception {
        driver.quit();
    }
}