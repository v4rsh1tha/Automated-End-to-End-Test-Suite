package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Page Object for the shopping cart screen.
 */
public class CartPage extends BasePage {

    private final By cartItems = By.className("cart_item");
    private final By checkoutButton = By.id("checkout");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public int getItemCount() {
        List<WebElement> items = driver.findElements(cartItems);
        return items.size();
    }

    public CheckoutStepOnePage proceedToCheckout() {
        click(checkoutButton);
        return new CheckoutStepOnePage(driver);
    }
}
