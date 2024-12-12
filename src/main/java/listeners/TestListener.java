package listeners;

import io.appium.java_client.AppiumDriver;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import utils.ScreenshotUtils;

import io.appium.java_client.AppiumDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

public class TestListener extends TestListenerAdapter {
    @Override
    public void onTestFailure(ITestResult result) {
        try {
            // Retrieve the Test Context
            ITestContext context = result.getTestContext();

            // Retrieve the AppiumDriver from the test context
            AppiumDriver driver = (AppiumDriver) context.getAttribute("driver");

            // Capture a screenshot
            String screenshotPath = ScreenshotUtils.captureScreenshot(driver, result.getName());

            // Log the screenshot path
            System.out.println("Screenshot captured: " + screenshotPath);
        } catch (Exception e) {
            System.err.println("Failed to capture screenshot on test failure: " + e.getMessage());
        }
    }
}

