package tests;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.contextswitching.Context;
import io.appium.java_client.contextswitching.SupportsContextSwitching;

import java.util.Optional;
import java.util.Set;

public class WebViewSwitcher {
    private final AppiumDriver driver;

    public WebViewSwitcher(AppiumDriver driver) {
        this.driver = driver;
    }

    public void switchToWebView() {
        // Ensure the driver supports context switching
        if (driver instanceof SupportsContextSwitching supportsContextSwitching) {
            Set<Context> contexts = supportsContextSwitching.getContexts();
            Optional<Context> webViewContext = contexts.stream()
                    .filter(context -> context.getName().contains("WEBVIEW"))
                    .findFirst();

            if (webViewContext.isPresent()) {
                supportsContextSwitching.context(webViewContext.get());
                System.out.println("Switched to WebView: " + webViewContext.get().getName());
            } else {
                System.out.println("No WebView context found.");
            }
        } else {
            throw new UnsupportedOperationException("Driver does not support context switching.");
        }
    }
}
