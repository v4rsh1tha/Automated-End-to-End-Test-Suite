package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.*;

public class CheckoutTest extends BaseTest {

    @Test(description = "Full checkout flow: login -> add to cart -> checkout -> confirmation")
    public void completeCheckoutFlowSucceeds() {
        InventoryPage inventoryPage = loginPage.loginAs("standard_user", "secret_sauce");
        inventoryPage.addItemToCart("sauce-labs-backpack");

        CartPage cartPage = inventoryPage.goToCart();
        CheckoutStepOnePage stepOne = cartPage.proceedToCheckout();

        CheckoutStepTwoPage stepTwo = stepOne.fillInfoAndContinue("Akshay", "Kumar", "500001");
        Assert.assertFalse(stepTwo.getTotal().isEmpty(), "Order total was not displayed on summary page");

        CheckoutCompletePage completePage = stepTwo.finishOrder();
        Assert.assertTrue(completePage.getConfirmationMessage().toLowerCase().contains("thank you"),
                "Expected an order confirmation message after finishing checkout");
    }

    @Test(description = "Checkout should block progress when required customer info is missing")
    public void checkoutBlocksOnMissingInfo() {
        InventoryPage inventoryPage = loginPage.loginAs("standard_user", "secret_sauce");
        inventoryPage.addItemToCart("sauce-labs-backpack");

        CartPage cartPage = inventoryPage.goToCart();
        CheckoutStepOnePage stepOne = cartPage.proceedToCheckout();
        stepOne.fillInfoAndContinue("", "", "");

        Assert.assertFalse(stepOne.getErrorMessage().isEmpty(),
                "Expected a validation error when required checkout fields are empty");
    }
}
