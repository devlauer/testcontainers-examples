package com.github.kaiwinter.user.view;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testcontainers.containers.BrowserWebDriverContainer;

import com.github.kaiwinter.testsupport.arquillian.WildflyDockerExtension;

@ExtendWith(ArquillianExtension.class)
class UserViewManualBrowserRecordingTest extends BaseViewTest {

	@Test
	@RunAsClient
	void testBrowserCallChrome() throws IOException {
		try (BrowserWebDriverContainer<?> chrome = new BrowserWebDriverContainer<>()) {
			chrome.start();
			RemoteWebDriver driver = chrome.getWebDriver();
			String address = WildflyDockerExtension.getBaseUrl() + "users.xhtml";
			driver.get(address);
			WebElement datatable = driver.findElement(By.className("ui-datatable-data"));
			List<WebElement> datatableRows = datatable.findElements(By.className("ui-widget-content"));
			assertEquals(5, datatableRows.size());
		}
	}
}
