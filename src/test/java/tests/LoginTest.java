package tests;

import io.appium.java_client.AppiumDriver;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import utils.CapabilitiesLoader;

import java.net.MalformedURLException;
import java.net.URL;

public class LoginTest {
    protected AppiumDriver driver;

    @BeforeMethod
    @Parameters({"platform", "appPath"})
    public void setup(String platform, String env) {
        try {
            // Create an instance of CapabilitiesLoader
            CapabilitiesLoader loader = new CapabilitiesLoader();
            driver = new AppiumDriver(
                    new URL("http://localhost:4723"),
                    loader.loadCapabilities(platform, env)
            );
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid URL for Appium server: " + e.getMessage());
        }
    }



}
