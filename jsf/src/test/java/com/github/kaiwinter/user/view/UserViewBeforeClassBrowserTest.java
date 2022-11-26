package com.github.kaiwinter.user.view;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testcontainers.containers.BrowserWebDriverContainer;

import com.github.kaiwinter.testsupport.arquillian.WildflyDockerExtension;

@RunWith(Arquillian.class)
public class UserViewBeforeClassBrowserTest extends BaseViewTest {

	static BrowserWebDriverContainer<?> chrome;
	static BrowserWebDriverContainer<?> firefox;

	@SuppressWarnings("resource")
	@BeforeClass
	public static void setup() {
		chrome = new BrowserWebDriverContainer<>().withCapabilities(new ChromeOptions());
		chrome.start();
		firefox = new BrowserWebDriverContainer<>().withCapabilities(new FirefoxOptions());
		firefox.start();
	}

	@Test
	@RunAsClient
	public void testBrowserCallFirefox() {
		RemoteWebDriver driverFirefox = firefox.getWebDriver();
		testTableData(driverFirefox);
	}

	@Test
	@RunAsClient
	public void testBrowserCallChrome() {
		RemoteWebDriver driverChrome = chrome.getWebDriver();
		testTableData(driverChrome);
	}

	private void testTableData(RemoteWebDriver driver) {
		String address = WildflyDockerExtension.getBaseUrl() + "users.xhtml";
		driver.get(address);
		WebElement datatable = driver.findElement(By.className("ui-datatable-data"));
		List<WebElement> datatableRows = datatable.findElements(By.className("ui-widget-content"));
		assertEquals(5, datatableRows.size());
	}

	@AfterClass
	public static void tearDown() {
		chrome.stop();
		firefox.stop();
	}
}
