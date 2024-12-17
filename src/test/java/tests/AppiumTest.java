package tests;

import base.AppiumServerManager;
import base.EmulatorManager;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import utils.CapabilitiesLoader;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public class AppiumTest {
    public static void main(String[] args) {
        // Define platform and environment
        String platform = "android"; // or "ios"
        String environment = "dev"; // or "prod"

        try {
            // Load capabilities
            CapabilitiesLoader loader = new CapabilitiesLoader();
            DesiredCapabilities capabilities = loader.loadCapabilities(platform, environment);

            // Define the Appium server URL
            URL appiumServerUrl = new URL("http://localhost:4723/wd/hub");

            // Initialize the driver based on the platform
            AppiumDriver driver;
            driver = new AndroidDriver(appiumServerUrl, capabilities);

            // Perform operations with the driver
            System.out.println("Driver started successfully!");

            // Clean up (quit driver)
            driver.quit();

        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Appium server URL!", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Appium driver!", e);
        }
    }
    protected AppiumServerManager appiumServerManager;
    protected EmulatorManager emulatorManager;
    @Test
    public void appiumTest1() {
        emulatorManager = new EmulatorManager("/Library/Android/sdk","Pixel_8_Pro_Haneul_API_35_1");
        emulatorManager.startEmulator();
        appiumServerManager = new AppiumServerManager();
        appiumServerManager.startServer();
        System.out.println(appiumServerManager.getServerUrl());
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("appium:automationName", "UiAutomator2");
        capabilities.setCapability("appium:deviceName", "Pixel 8 Pro Haneul API 35");
        capabilities.setCapability("appium:appActivity", "com.ins.smarthomemini.presentation.app.MainActivity");
        capabilities.setCapability("appium:appPackage", "com.ins.smarthomemini.debug");
        capabilities.setCapability("appium:isHeadless", false);

        try {
            URL appiumServerUrl = new URL("http://127.0.0.1:4723");
            AppiumDriver driver = new AndroidDriver(appiumServerUrl, capabilities);

            // Set implicit wait timeout
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));

            System.out.println("Appium driver initialized successfully!");

            // Perform your test actions here

            // Quit the driver
            driver.quit();
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Appium server URL!", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Appium driver!", e);
        }
    }
    @Test
    public void test2(){
        LoginTest loginTest = new LoginTest();
        loginTest.setup("android","dev");
    }
    @AfterClass
    public void closeAppium(){
        appiumServerManager.stopServer();
    }
}

