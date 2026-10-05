package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.InventoryPage;

public class LoginTest extends BaseTest {

    @Test(description = "Valid credentials should land the user on the inventory page")
    public void validLoginNavigatesToInventoryPage() {
        InventoryPage inventoryPage = loginPage.loginAs("standard_user", "secret_sauce");
        Assert.assertTrue(inventoryPage.isLoaded(), "Inventory page did not load after valid login");
    }

    @Test(description = "Locked-out user should see an error and stay on the login page")
    public void lockedOutUserSeesErrorMessage() {
        loginPage.attemptLogin("locked_out_user", "secret_sauce");
        Assert.assertTrue(loginPage.isErrorDisplayed(), "Expected an error message for locked-out user");
        Assert.assertTrue(loginPage.getErrorMessage().contains("locked out"),
                "Error message did not mention the account being locked out");
    }

    @Test(dataProvider = "invalidCredentials",
          description = "Invalid credential combinations should all be rejected")
    public void invalidCredentialsAreRejected(String username, String password) {
        loginPage.attemptLogin(username, password);
        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "Expected login to fail for username='" + username + "'");
    }

    @DataProvider(name = "invalidCredentials")
    public Object[][] invalidCredentials() {
        return new Object[][]{
                {"standard_user", "wrong_password"},
                {"invalid_user", "secret_sauce"},
                {"", "secret_sauce"},
                {"standard_user", ""}
        };
    }
}
