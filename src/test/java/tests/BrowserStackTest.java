package tests;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;

public class BrowserStackTest {
    /**
     * To upload an app to BrowserStack, use the following command in your terminal:
     * curl -u "your_user:your_key" -X POST "https://api-cloud.browserstack.com/app-automate/upload" -F "file=@/path/to/your/app.apk"
     * @param args
     */
    public static void main(String[] args) {
        DesiredCapabilities capabilities = new DesiredCapabilities();

        // BrowserStack credentials
        capabilities.setCapability("browserstack.user", "your_user");
        capabilities.setCapability("browserstack.key", "your_key");

        // App details
        capabilities.setCapability("app", "your_app_url"); // Replace with your app URL from BrowserStack

        // Device details
        capabilities.setCapability("device", "Google Pixel 6");
        capabilities.setCapability("os_version", "12.0");

        // Additional capabilities
        capabilities.setCapability("project", "My First Project");
        capabilities.setCapability("build", "Build 1.0");
        capabilities.setCapability("name", "Sample Test");

        try {
            // Initialize the Appium Driver with BrowserStack URL
            AppiumDriver driver = new AppiumDriver(new URL("http://hub.browserstack.com/wd/hub"), capabilities);

            // Example: Perform actions on the app
            System.out.println("Session created. Performing actions...");

            // Quit the driver
            driver.quit();
            System.out.println("Test completed!");
        } catch (MalformedURLException e) {
            System.err.println("Invalid URL: " + e.getMessage());
        }
    }
}
