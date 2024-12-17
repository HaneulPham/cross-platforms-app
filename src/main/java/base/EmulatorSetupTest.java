package base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class EmulatorSetupTest {

    private static final Logger logger = LoggerFactory.getLogger(EmulatorSetupTest.class);
    private final String sdkPath;

    public EmulatorSetupTest() {
        // Dynamically determine the user's home directory
        String userHome = System.getProperty("user.home");
        this.sdkPath = userHome + "/Library/Android/sdk";
        logger.info("SDK Path set to: {}", sdkPath);
    }

    private String executeCommand(String[] command) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }
        process.waitFor();
        return output.toString();
    }

    public void verifyAvdList() throws Exception {
        String emulatorPath = sdkPath + "/emulator/emulator";
        String[] command = { emulatorPath, "-list-avds" };
        logger.info("Fetching AVD list...");
        String output = executeCommand(command);
        logger.debug("Available AVDs: \n{}", output);
    }

    public void ensureEmulatorPermissions() throws Exception {
        String emulatorPath = sdkPath + "/emulator/emulator";
        String[] command = { "chmod", "+x", emulatorPath };
        logger.info("Ensuring emulator binary is executable...");
        executeCommand(command);
        logger.info("Emulator binary permissions adjusted.");
    }

    public void testEmulatorLaunch(String avdName) throws Exception {
        String emulatorPath = sdkPath + "/emulator/emulator";
        String[] command = {
                emulatorPath, "-avd", avdName, "-no-snapshot-load", "-no-boot-anim"
        };
        logger.info("Attempting to launch the emulator with AVD: {}", avdName);
        String output = executeCommand(command);
        logger.debug("Emulator launch output: \n{}", output);
    }

    public static void main(String[] args) {
        try {
            EmulatorSetupTest setupTest = new EmulatorSetupTest();
            String avdName = "Pixel_8_Pro_Haneul_API_35_1";

            setupTest.verifyAvdList();
            setupTest.ensureEmulatorPermissions();
            setupTest.testEmulatorLaunch(avdName);

        } catch (Exception e) {
            logger.error("An error occurred during the emulator setup process", e);
        }
    }
}
