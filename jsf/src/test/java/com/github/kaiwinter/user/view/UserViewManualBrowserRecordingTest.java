package com.github.kaiwinter.user.view;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.containers.BrowserWebDriverContainer.VncRecordingMode;
import org.testcontainers.containers.VncRecordingContainer.VncRecordingFormat;

import com.github.kaiwinter.testsupport.arquillian.WildflyDockerExtension;
import com.github.kaiwinter.user.view.testcontainers.TestcontainersHelper;

@RunWith(Arquillian.class)
public class UserViewManualBrowserRecordingTest extends BaseViewTest {

	@Test
	@RunAsClient
	public void testBrowserCallChrome() {
		try (BrowserWebDriverContainer<?> chrome = new BrowserWebDriverContainer<>()) {
			chrome.withCapabilities(new ChromeOptions()).withNetwork(TestcontainersHelper.network)
					.withRecordingMode(VncRecordingMode.RECORD_ALL, new File("./recording/"), VncRecordingFormat.MP4);
			chrome.start();
			RemoteWebDriver driver = chrome.getWebDriver();
			String address = WildflyDockerExtension.getNetworkBaseUrl() + "users.xhtml";
			driver.get(address);
			WebElement datatable = driver.findElement(By.className("ui-datatable-data"));
			List<WebElement> datatableRows = datatable.findElements(By.className("ui-widget-content"));
			assertEquals(5, datatableRows.size());
		}
	}
}
