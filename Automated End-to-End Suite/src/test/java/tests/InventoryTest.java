package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.InventoryPage;

public class InventoryTest extends BaseTest {

    private InventoryPage inventoryPage;

    @BeforeMethod(alwaysRun = true)
    public void loginAsStandardUser() {
        inventoryPage = loginPage.loginAs("standard_user", "secret_sauce");
    }

    @Test(description = "Adding a single item should update the cart badge to 1")
    public void addingSingleItemUpdatesCartBadge() {
        inventoryPage.addItemToCart("sauce-labs-backpack");
        Assert.assertEquals(inventoryPage.getCartItemCount(), 1,
                "Cart badge count did not update after adding one item");
    }

    @Test(description = "Adding multiple items should update the cart badge correctly")
    public void addingMultipleItemsUpdatesCartBadge() {
        inventoryPage.addItemToCart("sauce-labs-backpack")
                      .addItemToCart("sauce-labs-bike-light")
                      .addItemToCart("sauce-labs-bolt-t-shirt");
        Assert.assertEquals(inventoryPage.getCartItemCount(), 3,
                "Cart badge count did not reflect all three added items");
    }

    @Test(description = "Cart page should list the same number of items that were added")
    public void cartPageReflectsAddedItems() {
        inventoryPage.addItemToCart("sauce-labs-backpack")
                      .addItemToCart("sauce-labs-bike-light");
        CartPage cartPage = inventoryPage.goToCart();
        Assert.assertEquals(cartPage.getItemCount(), 2,
                "Cart page item count did not match items added on inventory page");
    }
}
