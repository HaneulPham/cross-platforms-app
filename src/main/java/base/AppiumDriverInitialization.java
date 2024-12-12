package base;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;

public class AppiumDriverInitialization {
    public static AppiumDriver initializeDriver() {
        try {
            // Set Desired Capabilities
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability("platformName", "Android"); // For Android; use "iOS" for iOS
            capabilities.setCapability("platformVersion", "12.0"); // Replace with your device's platform version
            capabilities.setCapability("deviceName", "MyDevice"); // Replace with your device name
            capabilities.setCapability("app", "/path/to/your/app.apk"); // Path to your app
            capabilities.setCapability("automationName", "UiAutomator2"); // Android: UiAutomator2, iOS: XCUITest
            capabilities.setCapability("noReset", true); // Optional: Avoid resetting app state

            // Initialize the Appium Driver
            return new AppiumDriver(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);

        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Appium server URL: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Initialize the Appium Driver
        AppiumDriver driver = initializeDriver();

        try {
            // Example usage: Find an element by ID (Replace with your locator)
            WebElement element = driver.findElement(By.id("example_id"));
            System.out.println("Element found: " + element.getText());

        } catch (Exception e) {
            System.err.println("Error interacting with the app: " + e.getMessage());
        } finally {
            // Quit the driver to release resources
            if (driver != null) {
                driver.quit();
            }
        }
    }
}

