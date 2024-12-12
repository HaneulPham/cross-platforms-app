package utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.apache.commons.io.FileUtils; // Import Apache Commons IO
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotUtils {
    public static String captureScreenshot(AppiumDriver driver, String fileName) {
        //check file screenshot direction path
        File screenshotDir = new File("reports/screenshots/");
        if (!screenshotDir.exists()) {
            screenshotDir.mkdirs();
        }
        // Generate the custom fileName with date and timestamp
        String timestamp = new SimpleDateFormat("MMddyyyy_HHmmss").format(new Date());
        fileName = "screenshot_" + timestamp;
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String path = "reports/screenshots/" + fileName + ".png";
        try {
            // Copy screenshot to the desired location
            FileUtils.copyFile(screenshot, new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }
//    screenshot test
    public static void main(String[] args) {
        try {
            // Set desired capabilities
            UiAutomator2Options options = new UiAutomator2Options();
            options.setPlatformName("Android");
            options.setDeviceName("MyDevice");
            options.setApp("/path/to/your/app.apk");
            options.setAutomationName("UiAutomator2");

            // Initialize Appium Driver
            AppiumDriver driver = new AppiumDriver(new URL("http://localhost:4723/wd/hub"), options);

            // Capture a screenshot
            String screenshotPath = ScreenshotUtils.captureScreenshot(driver, "test_screenshot");
            System.out.println("Screenshot saved at: " + screenshotPath);

            // Quit the driver
            driver.quit();
        } catch (MalformedURLException e) {
            System.err.println("Invalid URL: " + e.getMessage());
        }
    }
}
