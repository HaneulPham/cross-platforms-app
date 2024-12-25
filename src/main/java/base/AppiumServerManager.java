package base;

import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AppiumServerManager {
    private static final Logger logger = LoggerFactory.getLogger(AppiumServerManager.class);
    private AppiumDriverLocalService service;

    /**
     * Initializes the Appium service with desired configurations.
     * Customize the flags and arguments as needed.
     * Approach A: Build the Service in the Constructor
     */
    public AppiumServerManager() {
        logger.info("Initializing AppiumServerManager...");
        this.service = new AppiumServiceBuilder()
                .withIPAddress("127.0.0.1") // Specify the IP address
                .usingPort(4723)           // Specify the port
//                .usingAnyFreePort()
                .withArgument(GeneralServerFlag.SESSION_OVERRIDE)
                .withArgument(GeneralServerFlag.LOG_LEVEL, "info")
                .build();
        logger.info("AppiumServiceBuilder configured.");
    }

    /**
     * Starts the Appium server. If the server is already running, this will restart it.
     */
    public void startServer() {
        if (service == null || !service.isRunning()) {
            assert service != null;
            service.start();
            logger.info("Appium REST http interface listener started on " + service.getUrl());
        } else {
            logger.warn("Appium Server is already running at: " + service.getUrl());
        }
    }

    /**
     * Approach B: Build the Service in startServer() if Needed
     */
    public void startService() {
        if (service == null) {
            logger.info("Building the default Appium service...");
            service = AppiumDriverLocalService.buildDefaultService();
        }
        if (!service.isRunning()) {
            service.start();
            logger.info("Appium server started at: " + service.getUrl());
        } else {
            logger.warn("Appium server is already running at: " + service.getUrl());
        }
    }

    /**
     * Stops the Appium server if it is running.
     */
    public void stopServer() {
        if (service != null && service.isRunning()) {
            service.stop();
            logger.info("Appium Server Stopped.");
        } else {
            logger.warn("Appium Server is not running, no need to stop.");
        }
    }

    /**
     * Returns the service URL if the server is running.
     */
    public String getServerUrl() {
        if (service != null && service.isRunning()) {
            logger.info("Returning Appium server URL: " + service.getUrl());
            return service.getUrl().toString();
        }
        logger.warn("Appium server is not running, no URL to return.");
        return null;
    }

    public static void main(String[] args) {
        // Create an instance of AppiumServerManager
        AppiumServerManager serverManager = new AppiumServerManager();

        // Start the server
        serverManager.startServer();
        serverManager.startService();

        // Your test logic here
        logger.info("Appium server is running. You can proceed with your tests.");

        // Stop the server after done
        // serverManager.stopServer();
    }
}