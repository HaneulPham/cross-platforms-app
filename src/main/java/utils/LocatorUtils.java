package utils;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

public class LocatorUtils {
    public static WebElement findElement(AppiumDriver<?> driver, By... locators) {
        for (By locator : locators) {
            try {
                return driver.findElement(locator);
            } catch (NoSuchElementException ignored) {
                // Continue to the next locator
            }
        }
        throw new NoSuchElementException("No locator matched!");
    }
    public static void main(String[] args){
        AppiumDriver driver;// Initialize your Appium driver here

                WebElement element = LocatorUtils.findElement(driver,
                By.id("example_id"),
                By.xpath("//example/xpath"));
        System.out.println("Element found: " + element.getText());

    }
}
