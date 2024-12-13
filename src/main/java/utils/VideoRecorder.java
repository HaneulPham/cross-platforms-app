package utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class VideoRecorder {
    private final AppiumDriver driver;
    private final String outputDirectory;
    private String currentVideoPath;

    public VideoRecorder(AppiumDriver driver, String outputDirectory) {
        if (!(driver instanceof AndroidDriver || driver instanceof AppiumDriver)) {
            throw new IllegalArgumentException("Driver does not support screen recording");
        }
        this.driver = driver;
        this.outputDirectory = outputDirectory;

        // Ensure the output directory exists
        File dir = new File(outputDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public void startRecording() {
//        driver.startRecordingScreen();
        System.out.println("Screen recording started.");
    }

    public String stopAndSaveRecording(String fileName) throws IOException {
//        String base64Video = driver.stopRecordingScreen();
//        byte[] decodedVideo = Base64.getDecoder().decode(base64Video);

        currentVideoPath = outputDirectory + File.separator + fileName + ".mp4";
        try (FileOutputStream fos = new FileOutputStream(currentVideoPath)) {
//            fos.write(decodedVideo);
        }

        System.out.println("Screen recording saved at: " + currentVideoPath);
        return currentVideoPath;
    }

    public void deleteVideo() {
        if (currentVideoPath != null) {
            File videoFile = new File(currentVideoPath);
            if (videoFile.exists() && videoFile.delete()) {
                System.out.println("Screen recording deleted: " + currentVideoPath);
            } else {
                System.err.println("Failed to delete screen recording: " + currentVideoPath);
            }
        }
    }
}
