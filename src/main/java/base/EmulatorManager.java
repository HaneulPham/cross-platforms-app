package base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class EmulatorManager {

    private static final Logger logger = LoggerFactory.getLogger(EmulatorManager.class);

    private final String sdkPath;
    private final String avdName;

    public EmulatorManager(String sdkPath, String avdName) {
        // Dynamically build the full SDK path using user's home directory
        String userHome = System.getProperty("user.home");
        this.sdkPath = userHome + sdkPath;
        this.avdName = avdName;
    }

    /**
     * Starts the Android emulator using the specified AVD.
     */
    public void startEmulator() {
        String emulatorPath = sdkPath + "/emulator/emulator";
        String[] command = {
                emulatorPath, "-avd", avdName, "-no-snapshot-load", "-no-boot-anim"
        };

        logger.info("Starting the emulator: {}", avdName);

        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true); // Redirect errors to output
            Process process = pb.start();

            waitForEmulatorToBoot(); // Wait until the emulator boots
            logger.info("Emulator started successfully.");

        } catch (IOException e) {
            logger.error("Failed to start the emulator. Check the SDK path and AVD name.", e);
            throw new RuntimeException("Error starting the emulator", e);
        }
    }

    /**
     * Waits until the emulator has fully booted by checking the 'sys.boot_completed' property.
     */
    private void waitForEmulatorToBoot() {
        logger.info("Waiting for the emulator to boot...");

        try {
            boolean booted = false;
            while (!booted) {
                Thread.sleep(5000); // Check every 5 seconds
                String output;
                output = executeCommand(new String[]{"adb", "shell", "getprop", "sys.boot_completed"});

                if (output.trim().equals("1")) {
                    booted = true;
                }
            }
            logger.info("Emulator is fully booted and ready.");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted state
            logger.error("Thread interrupted while waiting for the emulator to boot.", e);
            throw new RuntimeException("Interrupted while waiting for emulator boot", e);
        } catch (IOException e) {
            logger.error("Error while checking emulator boot status.", e);
            throw new RuntimeException("Error during emulator boot check", e);
        }
    }

    /**
     * Executes a command using ProcessBuilder and returns its output.
     */
    private String executeCommand(String[] command) throws IOException {
        logger.debug("Executing command: {}", String.join(" ", command));
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true); // Redirect stderr to stdout

        Process process = processBuilder.start();
        StringBuilder output = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        int exitCode;
        try {
            exitCode = process.waitFor();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (exitCode != 0) {
            logger.warn("Command exited with non-zero status: {}", exitCode);
        }

        return output.toString();
    }

    /**
     * Main method to execute the emulator manager.
     */
    public static void main(String[] args) {
        try {
            // Provide SDK path and AVD name here
            String sdkPath = "/Library/Android/sdk";
            String avdName = "Pixel_8_Pro_Haneul_API_35_1";

            EmulatorManager manager = new EmulatorManager(sdkPath, avdName);
            manager.startEmulator();

        } catch (Exception e) {
            logger.error("An error occurred while managing the emulator.", e);
        }
    }
}