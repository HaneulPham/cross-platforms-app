package base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * A robust Emulator Manager that:
 *   1) Handles cross-platform paths (Windows vs. macOS/Linux)
 *   2) Uses environment variables (ANDROID_SDK_ROOT, ANDROID_HOME) if available
 *   3) Implements timeouts for process management
 *   4) Checks command availability (emulator, adb) before usage
 *   5) Provides flexible logging for different verbosity levels
 */
public class EmulatorManager {

    private static final Logger logger = LoggerFactory.getLogger(EmulatorManager.class);

    private final String avdName;
    private final String sdkPath;
    private final boolean isWindows;

    // Default time (in seconds) to wait for emulator commands to complete.
//    private static final int DEFAULT_COMMAND_TIMEOUT = 60; // 1 minute
    // Default time (in seconds) to wait for emulator to fully boot.
    private static final int DEFAULT_BOOT_TIMEOUT = 180;   // 3 minutes

    /**
     * Constructor that attempts to resolve the SDK path from environment variables
     * (ANDROID_SDK_ROOT, ANDROID_HOME) before falling back to a user-specified partial path.
     *
     * @param avdName         The name of the AVD to launch (e.g., "Pixel_8_Pro_Haneul_API_35_1")
     * @param partialSdkPath  A fallback path (e.g., "/Library/Android/sdk") appended to user.home on Unix
     */
    public EmulatorManager(String avdName, String partialSdkPath) {
        this.avdName = avdName;

        // Detect whether we're on Windows.
        String osName = System.getProperty("os.name").toLowerCase();
        this.isWindows = osName.contains("win");

        // Attempt to get SDK path from environment variables first.
        String sdkEnv = System.getenv("ANDROID_SDK_ROOT");
        if (sdkEnv == null || sdkEnv.isEmpty()) {
            sdkEnv = System.getenv("ANDROID_HOME");
        }

        if (sdkEnv != null && !sdkEnv.isEmpty()) {
            this.sdkPath = sdkEnv;
            logger.info("Using SDK path from environment variable: {}", this.sdkPath);
        } else {
            // Fallback: Combine user.home and partialSdkPath if environment var is missing.
            String userHome = System.getProperty("user.home");
            this.sdkPath = userHome + partialSdkPath;
            logger.info("Using fallback SDK path: {}", this.sdkPath);
        }

        logger.info("Operating system detected: {}", osName);
        logger.info("AVD Name: {}", this.avdName);
    }

    /**
     * Lists available AVDs: "<sdk>/emulator/emulator -list-avds"
     * Uses a short timeout (e.g., 30s) to avoid hanging.
     */
    public void verifyAvdList() {
        String emulatorPath = getEmulatorExecutable();
        if (checkCommandAvailability(emulatorPath)) {
            logger.error("Emulator executable not found or unavailable: {}", emulatorPath);
            return;
        }

        String[] command = { emulatorPath, "-list-avds" };
        logger.info("Fetching AVD list...");
        try {
            String output = executeCommand(command, 30); // 30s timeout
            logger.debug("Available AVDs:\n{}", output);
        } catch (IOException e) {
            logger.error("Error while fetching AVD list", e);
        }
    }

    /**
     * Ensures the emulator binary is executable on Unix-based systems.
     * No-op on Windows.
     */
    public void ensureEmulatorPermissions() {
        if (isWindows) {
            logger.info("Skipping chmod on Windows.");
            return;
        }

        String emulatorPath = getEmulatorExecutable();
        logger.info("Ensuring emulator binary is executable (Unix-based OS).");
        String[] command = { "chmod", "+x", emulatorPath };
        try {
            executeCommand(command, 10); // 10s timeout
            logger.info("Emulator binary permissions adjusted.");
        } catch (IOException e) {
            logger.warn("Failed to chmod +x the emulator (could be expected on non-Unix).", e);
        }
    }

    /**
     * Starts the emulator in either GUI or headless mode, then waits for it to boot.
     *
     * @param quietBoot    If true, uses "-no-window" (headless) on Unix-based systems
     * @param bootTimeout  Max time (in seconds) to wait for sys.boot_completed
     */
    public void startEmulator(boolean quietBoot, int bootTimeout) {
        String emulatorPath = getEmulatorExecutable();
        if (checkCommandAvailability(emulatorPath)) {
            throw new RuntimeException("Emulator not found or not on PATH: " + emulatorPath);
        }

        List<String> cmdList = new ArrayList<>();
        cmdList.add(emulatorPath);
        cmdList.add("-avd");
        cmdList.add(avdName);
        cmdList.add("-no-snapshot-load");
        cmdList.add("-no-boot-anim");

        // If requested, run in headless mode (on non-Windows).
        if (quietBoot && !isWindows) {
            cmdList.add("-no-window");
        }

        String[] command = cmdList.toArray(new String[0]);
        logger.info("Attempting to launch emulator (quiet={}): {}", quietBoot, avdName);

        try {
            // Start emulator in a separate process; no need to waitFor() here.
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            pb.start();

            // Wait for the emulator to boot or time out.
            waitForEmulatorToBoot(bootTimeout);
            logger.info("Emulator started and ready.");
        } catch (IOException e) {
            logger.error("Failed to start the emulator. Check SDK path and AVD name.", e);
            throw new RuntimeException("Error starting the emulator", e);
        }
    }

    /**
     * Waits until 'sys.boot_completed' is 1 or the timeout is reached.
     *
     * @param bootTimeout  Time (in seconds) to wait for the emulator to signal it's ready
     */
    private void waitForEmulatorToBoot(int bootTimeout) {
        logger.info("Waiting (up to {}s) for emulator to boot...", bootTimeout);
        long deadline = System.currentTimeMillis() + (bootTimeout * 1000L);

        try {
            boolean booted = false;
            while (!booted && System.currentTimeMillis() < deadline) {
                Thread.sleep(5000); // check every 5 seconds

                /* "adb shell getprop sys.boot_completed" -> "1" if fully booted */
                String output = executeCommand(
                        new String[]{"adb", "shell", "getprop", "sys.boot_completed"},
                        10 // give 10s for each 'adb' call
                );
                if ("1".equals(output.trim())) {
                    booted = true;
                }
            }
            if (booted) {
                logger.info("Emulator is fully booted.");
            } else {
                logger.warn("Emulator did not boot within {} seconds.", bootTimeout);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Interrupted while waiting for emulator boot.", e);
            throw new RuntimeException("Interrupted while waiting for emulator to boot", e);
        } catch (IOException e) {
            logger.error("Error checking emulator boot status.", e);
            throw new RuntimeException("Error during emulator boot check", e);
        }
    }

    /**
     * Sends "adb emu kill" to stop the first running emulator recognized by adb.
     * If multiple emulators run, you may specify serial with: "adb -s emulator-5554 emu kill".
     */
    public void stopEmulator() {
        logger.info("Stopping the emulator via 'adb emu kill'...");
        if (checkCommandAvailability("adb")) {
            logger.error("'adb' not found or not on PATH.");
            return;
        }

        try {
            executeCommand(new String[]{"adb", "emu", "kill"}, 15);
            logger.info("Stop command issued. The emulator should close shortly.");
        } catch (IOException e) {
            logger.error("Failed to stop the emulator.", e);
        }
    }

    /**
     * Executes a shell command with a specified timeout in seconds.
     * If the command doesn't finish within that timeframe, it is forcibly terminated.
     *
     * @param command        The command to run (including arguments)
     * @param timeoutSeconds The maximum time to wait for completion
     * @return The combined stdout/stderr output
     * @throws IOException If the process fails to start or reading output fails
     */
    private String executeCommand(String[] command, int timeoutSeconds) throws IOException {
        logger.debug("Executing command (timeout={}s): {}", timeoutSeconds, String.join(" ", command));

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        StringBuilder output = new StringBuilder();
        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        // Wait for completion or timeout
        boolean finished;
        try {
            finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Command execution interrupted", e);
        }

        if (!finished) {
            // Process didn't finish in time; forcibly kill it.
            process.destroyForcibly();
            logger.warn("Command timed out after {}s: {}", timeoutSeconds, String.join(" ", command));
        }

        // Exit code
        int exitCode = process.exitValue();
        if (exitCode != 0) {
            logger.warn("Command exited with status {}: {}", exitCode, String.join(" ", command));
        }
        return output.toString();
    }

    /**
     * Checks whether a given command or path is available:
     *   - If it's an absolute path, checks if the file exists and is executable.
     *   - Otherwise, uses 'which' (Unix) or 'where' (Windows) to see if it's in the PATH.
     *
     * @param cmd Command or absolute path
     * @return true if the command/path appears to be usable, false otherwise checkCommandAvailability
     */
    private boolean checkCommandAvailability(String cmd) {
        // If cmd is an absolute path, just verify existence & executability
        File file = new File(cmd);
        if (file.isAbsolute()) {
            if (file.exists() && file.canExecute()) {
                return false;
            }
            logger.warn("Absolute path does not exist or isn't executable: {}", cmd);
            return true;
        }

        // If it's not an absolute path, we rely on 'which' (Unix) or 'where' (Windows)
        String[] checkCmd = isWindows
                ? new String[]{"where", cmd}
                : new String[]{"which", cmd};

        try {
            String output = executeCommand(checkCmd, 5); // short timeout for checking
            return output.trim().isEmpty(); // If we got a result, it's available
        } catch (IOException e) {
            logger.warn("Failed to check command availability for '{}'.", cmd, e);
            return true;
        }
    }

    /**
     * Returns the absolute path to the 'emulator' binary, accounting for '.exe' on Windows.
     */
    private String getEmulatorExecutable() {
        // Example: <sdkPath>/emulator/emulator(.exe on Windows)
        String emulatorExec = sdkPath + File.separator + "emulator" + File.separator + "emulator";
        if (isWindows) {
            emulatorExec += ".exe";
        }
        return emulatorExec;
    }

    /**
     * Example usage in a main method. Adjust paths, AVD names, and timeouts per your needs.
     */
    public static void main(String[] args) {
        try {
            // Provide your AVD name and fallback partial path
            String avdName = "Pixel_8_Pro_Haneul_API_35_1";
            String partialSdkPath = "/Library/Android/sdk";

            EmulatorManager manager = new EmulatorManager(avdName, partialSdkPath);

            // 1) Check which AVDs exist
            manager.verifyAvdList();

            // 2) Make emulator binary executable (no-op on Windows)
            manager.ensureEmulatorPermissions();

            // 3) Start emulator in 'quiet' mode if not on Windows, wait up to 3 min
            manager.startEmulator(false, DEFAULT_BOOT_TIMEOUT);

            // ... run tests or interact with the emulator ...

            // 4) Stop the running emulator
            manager.stopEmulator();

        } catch (Exception e) {
            logger.error("An error occurred while managing the emulator:", e);
        }
    }
}