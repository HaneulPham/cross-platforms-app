package tests;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.testng.ITestContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;

public class SampleTestListener {
    private AppiumDriver driver;

    @BeforeMethod
    public void setupDriver(ITestContext context) throws MalformedURLException {
        UiAutomator2Options options = new UiAutomator2Options();
        options.setPlatformName("Android");
        options.setDeviceName("MyDevice");
        options.setApp("/path/to/your/app.apk");
        options.setAutomationName("UiAutomator2");

        driver = new AppiumDriver(new URL("http://localhost:4723/wd/hub"), options);

        // Store the driver in the TestNG context
        context.setAttribute("driver", driver);
    }

    @Test
    public void sampleTest() {
        // Example test logic
        System.out.println("Running test...");
        assert false : "Simulating a test failure";
    }
}
