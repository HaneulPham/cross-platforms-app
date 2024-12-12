package utils;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

public class ContextSwitcher {
    private final AppiumDriver driver;

    public ContextSwitcher(AppiumDriver driver) {
        this.driver = driver;
    }

    public void switchToNative() {
        try {
            driver.executeScript("mobile: switchToContext", "NATIVE_APP");
            System.out.println("Switched to Native App context.");
        } catch (Exception e) {
            System.err.println("Error switching to Native App context: " + e.getMessage());
        }
    }
    @Test
    public void testHybridApp() {
        WebViewSwitcher webViewSwitcher = new WebViewSwitcher(driver);
        webViewSwitcher.switchToWebView();
        driver.findElement(By.id("web-element-id")).click();
        switchToNative();
    }
}
