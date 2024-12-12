package listeners;

import io.appium.java_client.AppiumDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import utils.VideoRecorder;

import java.io.IOException;

public class VideoRecordingListener extends TestListenerAdapter {
    private VideoRecorder recorder;

    @Override
    public void onStart(ITestContext context) {
        // Initialize VideoRecorder and store it in the context
        AppiumDriver driver = (AppiumDriver) context.getAttribute("driver");
        recorder = new VideoRecorder(driver, "reports/videos");
        recorder.startRecording();
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        // Delete video if the test passed
        recorder.deleteVideo();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        // Stop and save the recording if the test failed
        try {
            recorder.stopAndSaveRecording(result.getName());
        } catch (IOException e) {
            System.err.println("Error saving video: " + e.getMessage());
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        // Ensure the recording is stopped after all tests
        try {
            recorder.stopAndSaveRecording("final_recording");
        } catch (IOException e) {
            System.err.println("Error saving final recording: " + e.getMessage());
        }
    }
}
