package base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A unified class that manages:
 *  - Listing AVDs
 *  - Ensuring emulator is executable (Unix-based systems)
 *  - Starting the emulator (with optional "quiet" / headless mode)
 *  - Waiting for the emulator to boot
 *  - Stopping (killing) the emulator
 */
public class EmulatorManagerOld {

    private static final Logger logger = LoggerFactory.getLogger(EmulatorManagerOld.class);

    private final String sdkPath;
    private final String avdName;

    public EmulatorManagerOld(String partialSdkPath, String avdName) {
        /*
         * For example:
         *  - partialSdkPath = "/Library/Android/sdk"
         *  - user.home = "/Users/jdoe"
         *  => final sdkPath = "/Users/jdoe/Library/Android/sdk"
         *
         * If you already have a fully qualified path, either omit user.home,
         * or remove the leading slash from partialSdkPath.
         */
        String userHome = System.getProperty("user.home");
        this.sdkPath = userHome + partialSdkPath;
        this.avdName = avdName;

        logger.info("SDK Path: {}", this.sdkPath);
        logger.info("AVD Name: {}", this.avdName);
    }

    /**
     * Lists the available AVDs by running: <sdk>/emulator/emulator -list-avds
     */
    public void verifyAvdList() {
        String emulatorPath = sdkPath + "/emulator/emulator";
        String[] command = { emulatorPath, "-list-avds" };
        logger.info("Fetching AVD list...");
        try {
            String output = executeCommand(command);
            logger.debug("Available AVDs:\n{}", output);
        } catch (IOException e) {
            logger.error("Error while fetching AVD list.", e);
        }
    }

    /**
     * Ensures the emulator binary is executable (Unix-based systems).
     * No-op on Windows unless adapted.
     */
    public void ensureEmulatorPermissions() {
        String emulatorPath = sdkPath + "/emulator/emulator";
        String[] command = { "chmod", "+x", emulatorPath };
        logger.info("Ensuring emulator binary is executable...");
        try {
            executeCommand(command);
            logger.info("Emulator binary permissions adjusted.");
        } catch (IOException e) {
            // On Windows, 'chmod' is not applicable.
            logger.warn("Failed to chmod +x the emulator. This may be expected on non-Unix systems.", e);
        }
    }

    /**
     * Starts the emulator using the specified AVD.
     * @param quietBoot if true, adds -no-window (headless mode)
     */
    public void startEmulator(boolean quietBoot) {
        String emulatorPath = sdkPath + "/emulator/emulator";

        /*
         * Build the command in a List so we can optionally add flags.
         */
        List<String> cmdList = new ArrayList<>();
        cmdList.add(emulatorPath);
        cmdList.add("-avd");
        cmdList.add(avdName);
        cmdList.add("-no-snapshot-load");
        cmdList.add("-no-boot-anim");

        // If requested, run in headless mode (no window).
        if (quietBoot) {
            cmdList.add("-no-window");
        }

        // Convert List to array for executeCommand, if needed
        String[] command = cmdList.toArray(new String[0]);

        logger.info("Attempting to launch emulator (quietBoot={}): {}", quietBoot, avdName);

        try {
            // Start the emulator process
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            pb.start();

            // Wait for it to boot
            waitForEmulatorToBoot();
            logger.info("Emulator started and ready.");
        } catch (IOException e) {
            logger.error("Failed to start the emulator. Check SDK path and AVD name.", e);
            throw new RuntimeException("Error starting the emulator", e);
        }
    }

    /**
     * Blocks until the emulator is fully booted by checking 'sys.boot_completed'.
     */
    private void waitForEmulatorToBoot() {
        logger.info("Waiting for the emulator to boot...");

        try {
            boolean booted = false;
            while (!booted) {
                Thread.sleep(5000); // Check every 5 seconds

                // "adb shell getprop sys.boot_completed" will return "1" once the system is fully up
                String output = executeCommand(new String[]{"adb", "shell", "getprop", "sys.boot_completed"});
                if ("1".equals(output.trim())) {
                    booted = true;
                }
            }
            logger.info("Emulator is fully booted and ready.");
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt(); // Restore interrupted state
            logger.error("Interrupted while waiting for emulator boot.", ie);
            throw new RuntimeException("Interrupted while waiting for emulator boot", ie);
        } catch (IOException e) {
            logger.error("Error while checking emulator boot status.", e);
            throw new RuntimeException("Error during emulator boot check", e);
        }
    }

    /**
     * Closes the emulator by sending the "emu kill" command via adb.
     *
     * If multiple emulators are running, this kills the first device ADB finds,
     * or the emulator that's currently targeted by your local adb instance.
     *
     * For more precise control, you can run "adb devices" to get the serial
     * (e.g. emulator-5554) and do "adb -s emulator-5554 emu kill".
     */
    public void stopEmulator() {
        logger.info("Stopping the emulator...");
        try {
            executeCommand(new String[]{"adb", "emu", "kill"});
            logger.info("Emulator stop command issued.");
        } catch (IOException e) {
            logger.error("Failed to stop the emulator.", e);
        }
    }

    /**
     * Executes a shell command and returns its output as a String.
     */
    private String executeCommand(String[] command) throws IOException {
        logger.debug("Executing command: {}", String.join(" ", command));

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();
        StringBuilder output = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        int exitCode;
        try {
            exitCode = process.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Command execution interrupted", e);
        }

        if (exitCode != 0) {
            logger.warn("Command exited with non-zero status: {}", exitCode);
        }
        return output.toString();
    }

    /**
     * Example usage.
     */
    public static void main(String[] args) {
        try {
            /*
             * Provide a partial or full SDK path + AVD name here
             * For example, if partialSdkPath = "/Library/Android/sdk",
             * final path becomes: /Users/jdoe/Library/Android/sdk
             */
            String partialSdkPath = "/Library/Android/sdk";
            String avdName = "Pixel_8_Pro_Haneul_API_35_1";

            EmulatorManagerOld manager = new EmulatorManagerOld(partialSdkPath, avdName);
            manager.verifyAvdList();            // Check which AVDs exist
            manager.ensureEmulatorPermissions(); // Make emulator binary executable (Unix only)

            // Start emulator in quiet/headless mode if desired
            boolean quietBoot = false;
            manager.startEmulator(quietBoot);    // Launch emulator, wait for boot

            // ... do stuff with the running emulator (e.g., install APKs, run tests, etc.)

            // Finally, close the emulator
            manager.stopEmulator();

        } catch (Exception e) {
            logger.error("An error occurred during the emulator management process.", e);
        }
    }
}