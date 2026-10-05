package base;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import pages.LoginPage;
import utils.DriverFactory;

/**
 * Every UI test class extends this so each @Test method gets a fresh
 * browser session (test isolation) and the driver is always quit,
 * even if the test fails.
 */
public class BaseTest {

    protected WebDriver driver;
    protected LoginPage loginPage;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        // Set headless = true when running in CI (no display available)
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));
        driver = DriverFactory.createChromeDriver(headless);
        loginPage = new LoginPage(driver);
        loginPage.navigateTo();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
