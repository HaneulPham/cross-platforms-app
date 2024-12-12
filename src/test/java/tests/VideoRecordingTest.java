package tests;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import utils.VideoRecorder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class VideoRecordingTest {
    public static void main(String[] args) {
        try {
            // Set Desired Capabilities
            UiAutomator2Options options = new UiAutomator2Options();
            options.setPlatformName("Android");
            options.setDeviceName("MyDevice");
            options.setApp("/path/to/your/app.apk");
            options.setAutomationName("UiAutomator2");

            // Initialize Appium Driver
            AppiumDriver driver = new AndroidDriver(new URL("http://localhost:4723/wd/hub"), options);

            // Initialize the VideoRecorder
            VideoRecorder recorder = new VideoRecorder(driver, "reports/videos");

            // Start screen recording
            recorder.startRecording();

            // Perform some actions in your app
            Thread.sleep(5000); // Simulate actions for 5 seconds

            // Stop and save the recording
            recorder.stopAndSaveRecording("testVideo");

            // Quit the driver
            driver.quit();
        } catch (MalformedURLException e) {
            System.err.println("Invalid URL: " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("Thread interrupted: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error saving video: " + e.getMessage());
        }
    }
}
