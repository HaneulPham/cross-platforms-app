package utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.net.MalformedURLException;
import java.net.URL;

public class AccessibilityUtils {

    /**
     * Checks if the given element is accessible based on its content description.
     *
     * @param element the WebElement to check
     * @return true if the element has a non-empty content description; false otherwise
     */
    public static boolean isElementAccessible(WebElement element) {
        try {
            String contentDesc = element.getAttribute("contentDescription");
            return contentDesc != null && !contentDesc.isEmpty();
        } catch (Exception e) {
            System.err.println("Error checking accessibility: " + e.getMessage());
            return false;
        }
    }

    public static void main(String[] args) throws MalformedURLException {
        CapabilitiesLoader capabilitiesLoader = new CapabilitiesLoader();
        // Set Desired Capabilities
        UiAutomator2Options options = new UiAutomator2Options();
        options.setPlatformName("Android");
        options.setDeviceName("MyDevice");
        options.setApp("/path/to/your/app.apk");
        options.setAutomationName("UiAutomator2");

        // Initialize Appium Driver
        AppiumDriver driver = new AppiumDriver(new URL("http://localhost:4723/wd/hub"), options);

        // Locate a WebElement
        WebElement element = driver.findElement(By.id("example_id"));

        // Check if the element is accessible
        boolean isAccessible = AccessibilityUtils.isElementAccessible(element);
        System.out.println("Is element accessible: " + isAccessible);

        // Quit the driver
        driver.quit();
    }
}

