package base;

import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;

public class AppiumServerManager {
    private AppiumDriverLocalService service;

    /**
     * Initializes the Appium service with desired configurations.
     * Customize the flags and arguments as needed.
     * Approach A: Build the Service in the Constructor
     */
    public AppiumServerManager() {
        this.service = new AppiumServiceBuilder()
                .withIPAddress("127.0.0.1")// Specify the IP address
                .usingPort(4723) // Specify the port
//                .usingAnyFreePort()
                .withArgument(GeneralServerFlag.SESSION_OVERRIDE)
                .withArgument(GeneralServerFlag.LOG_LEVEL, "info")
                .build();
    }

    /**
     * Starts the Appium server. If the server is already running, this will restart it.
     */
    public void startServer() {
        if (service == null || !service.isRunning()) {
            service.start();
            System.out.println("Appium REST http interface listener started on " + service.getUrl());
        } else {
            System.out.println("Appium Server is already running at: " + service.getUrl());
        }
    }

    /**
     * Approach B: Build the Service in startServer() if Needed
     */
    public void startService() {
        if (service == null) {
            service = AppiumDriverLocalService.buildDefaultService();
        }
        if (!service.isRunning()) {
            service.start();
            System.out.println("Appium server started at: " + service.getUrl());
        } else {
            System.out.println("Appium server is already running at: " + service.getUrl());
        }
    }


    /**
     * Stops the Appium server if it is running.
     */
    public void stopServer() {
        if (service != null && service.isRunning()) {
            service.stop();
            System.out.println("Appium Server Stopped.");
        } else {
            System.out.println("Appium Server is not running, no need to stop.");
        }
    }

    /**
     * Returns the service URL if the server is running.
     */
    public String getServerUrl() {
        if (service != null && service.isRunning()) {
            return service.getUrl().toString();
        }
        return null;
    }

    public static void main(String[] args) {
        // Create an instance of AppiumServerManager
        AppiumServerManager serverManager = new AppiumServerManager();

        // Start the server
        serverManager.startServer();

        // ... Your test logic here ...
        // For demonstration, let's pretend we run some tests and then stop the server.

        // Stop the server after done
//        serverManager.stopServer();
    }
}

