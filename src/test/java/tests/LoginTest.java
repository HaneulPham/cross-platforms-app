package tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;
import utils.CapabilitiesLoader;

import java.io.File;
import java.util.Map;

public class LoginTest {

    private AppiumDriver driver;
    private static Map config;
    CapabilitiesLoader loader;
    private static final Logger logger = LoggerFactory.getLogger(LoginTest.class);

    @BeforeClass
    public void setUp() throws Exception {
        // 3. Read server config from the Map
        Map<String, Object> serverConfig = (Map<String, Object>) config.get("server");
        String host = getStringOrDefault(serverConfig, "host", "127.0.0.1");
        int port = getIntOrDefault(serverConfig, "port", 4723);

        // 4. Select which driver config to use based on test or environment
        //    For demonstration, let's assume we're using the Android "uiautomator2" driver
        Map<String, Object> driverMap = (Map<String, Object>) ((Map<String, Object>) config.get("driver")).get("uiautomator2");

        // 5. Build AppiumOptions (could be UiAutomator2Options, XCUITestOptions, etc.)
        UiAutomator2Options options = new UiAutomator2Options();
        options.setAutomationName(getStringOrDefault(driverMap, "automationName", "UiAutomator2"));
        options.setPlatformName(getStringOrDefault(driverMap, "platformName", "Android"));
        options.setAppActivity(getStringOrDefault(driverMap, "appActivity", "com.ins.smarthomemini.presentation.app.MainActivity"));

        // 6. (Optional) Handle environment variable overrides:
        //    APPIUM_HOST and APPIUM_PORT can override JSON config
        String envHost = System.getenv("APPIUM_HOST");
        if (envHost != null) {
            host = envHost;
        }
        String envPort = System.getenv("APPIUM_PORT");
        if (envPort != null) {
            port = Integer.parseInt(envPort);
        }

        // 7. Initialize the driver
        String remoteURL = String.format("http://%s:%d", host, port);
        driver = new AppiumDriver(new java.net.URL(remoteURL), options);

        System.out.println("Driver initialized. Host: " + host + ", Port: " + port);
        logger.info("Driver initialized. Host: " + host + ", Port: " + port);
    }
    @Test
    public void readDataFromJsonConfig(){
        try {
            // Load the JSON file
            String filePath = "path/to/your.json"; // Replace with your JSON file path
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(new File(filePath));

            // Extract data for UiAutomator2Options
            JsonNode uiautomator2Node = rootNode
                    .path("appium")
                    .path("drivers")
                    .path("uiautomator2")
                    .path("emulators")
                    .get(0); // Assuming you want the first emulator configuration

            // Create and configure UiAutomator2Options
            UiAutomator2Options options = new UiAutomator2Options();

            options.setAutomationName(uiautomator2Node.path("automationName").asText("UiAutomator2"));
            options.setPlatformName(uiautomator2Node.path("platformName").asText("Android"));
            options.setPlatformVersion(uiautomator2Node.path("platformVersion").asText("13.0"));
            options.setDeviceName(uiautomator2Node.path("deviceName").asText("Default Device"));
            options.setApp(uiautomator2Node.path("app").asText());
            options.setAppActivity(uiautomator2Node.path("appActivity").asText("DefaultActivity"));
            options.setAppPackage(uiautomator2Node.path("appPackage").asText("DefaultPackage"));

            // Print configured options
            System.out.println("Configured UiAutomator2Options:");
            System.out.println("Automation Name: " + options.getAutomationName());
            System.out.println("Platform Name: " + options.getPlatformName());
            System.out.println("Platform Version: " + options.getPlatformVersion());
            System.out.println("Device Name: " + options.getDeviceName());
            System.out.println("App: " + options.getApp());
            System.out.println("App Activity: " + options.getAppActivity());
            System.out.println("App Package: " + options.getAppPackage());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void exampleTest() {
        // 8. Use the driver to run a simple test
        // For demonstration, just print a line or check the session details
        System.out.println("Running exampleTest with AppiumDriver session: " + driver.getSessionId());

        // Add your actual test logic here (e.g., find elements, run assertions, etc.)
    }

    @AfterTest
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // Utility methods for reading map values
    private String getStringOrDefault(Map<String, Object> map, String key, String defaultVal) {
        Object value = map.get(key);
        return value == null ? defaultVal : value.toString();
    }

    private int getIntOrDefault(Map<String, Object> map, String key, int defaultVal) {
        Object value = map.get(key);
        if (value == null) return defaultVal;
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }
}
