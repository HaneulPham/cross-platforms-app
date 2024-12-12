package utils;

import io.appium.java_client.AppiumDriver;

import java.util.Set;

public class WebViewSwitcher {
    private AppiumDriver driver;

    public WebViewSwitcher(AppiumDriver driver) {
        this.driver = driver;
    }

    public void switchToWebView() {
        Set<String> contexts = driver.getContextHandles();
        contexts.stream()
                .filter(context -> context.contains("WEBVIEW"))
                .findFirst()
                .ifPresent(driver::context);
    }
}