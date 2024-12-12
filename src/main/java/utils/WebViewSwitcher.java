package utils;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.WebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

public class WebViewSwitcher {
    private final AppiumDriver driver;

    public WebViewSwitcher(AppiumDriver driver) {
        this.driver = driver;
    }

    public void switchToWebView() {
        try {
            // Cast driver to WebDriver if necessary
            WebDriver webDriver = (WebDriver) driver;

            // Get available contexts
            Set<String> contexts = (Set<String>) driver.executeScript("mobile: getContexts");
            System.out.println("Available contexts: " + contexts);

            // Switch to the WebView context
            for (String context : contexts) {
                if (context.contains("WEBVIEW")) {
                    driver.executeScript("mobile: switchToContext", context);
                    System.out.println("Switched to WebView: " + context);
                    return;
                }
            }
            System.out.println("No WebView context found.");
        } catch (Exception e) {
            System.err.println("Error switching to WebView: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            // Set Desired Capabilities for Android
            UiAutomator2Options options = new UiAutomator2Options();
            options.setPlatformName("Android");
            options.setDeviceName("MyDevice");
            options.setApp("/path/to/your/app.apk");
            options.setAutomationName("UiAutomator2");

            // Initialize Appium Driver
            AppiumDriver driver = new AppiumDriver(new URL("http://localhost:4723/wd/hub"), options);

            // Use the WebViewSwitcher to switch contexts
            WebViewSwitcher switcher = new WebViewSwitcher(driver);
            switcher.switchToWebView();

            // Perform actions on WebView

            // Quit the driver
            driver.quit();
        } catch (MalformedURLException e) {
            System.err.println("Invalid URL for Appium server: " + e.getMessage());
        }
    }
}
