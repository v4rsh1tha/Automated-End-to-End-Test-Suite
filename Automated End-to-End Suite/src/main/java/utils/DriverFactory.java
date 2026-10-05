package utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.HashMap;
import java.util.Map;

/**
 * Central place to create and configure WebDriver instances.
 * Uses WebDriverManager so no manual driver-binary downloads/PATH setup
 * are required on any machine that runs this suite.
 */
public class DriverFactory {

    public static WebDriver createChromeDriver(boolean headless) {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");

        // Stop Chrome's "password found in a data breach" / save-password
        // popups from appearing mid-test. These are native browser dialogs
        // that can sit on top of the page and intercept clicks meant for
        // whatever element is underneath them. This is a known Chrome
        // 145+ regression (Chromium issue 42323769) -- the prefs alone
        // stopped being reliable, so the feature flag below is required too.
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.password_manager_leak_detection", false);
        prefs.put("reduce-security-for-testing", true);
        options.setExperimentalOption("prefs", prefs);
        options.addArguments("--disable-features=PasswordLeakDetection");

        // Removes the "Chrome is being controlled by automated test software"
        // info bar and its underlying automation flag detection.
        options.setExperimentalOption("excludeSwitches", new String[] {"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("--disable-blink-features=AutomationControlled");

        if (headless) {
            // "--start-maximized" has no effect without a real display, so
            // headless runs get an explicit window size instead.
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
        } else {
            options.addArguments("--start-maximized");
        }

        return new ChromeDriver(options);
    }
}