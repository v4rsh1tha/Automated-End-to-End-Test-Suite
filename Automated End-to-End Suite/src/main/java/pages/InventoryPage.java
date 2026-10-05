package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

/**
 * Page Object for the product inventory screen shown after a successful login.
 */
public class InventoryPage extends BasePage {

    private final By inventoryContainer = By.id("inventory_container");
    private final By cartBadge = By.className("shopping_cart_badge");
    private final By cartIcon = By.className("shopping_cart_link");
    private final By sortDropdown = By.className("product_sort_container");
    private final By menuButton = By.id("react-burger-menu-btn");
    private final By logoutLink = By.id("logout_sidebar_link");

    public InventoryPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return isDisplayed(inventoryContainer);
    }

    private By addToCartButtonFor(String productSlug) {
        return By.id("add-to-cart-" + productSlug);
    }

    /** productSlug examples: "sauce-labs-backpack", "sauce-labs-bike-light" */
    public InventoryPage addItemToCart(String productSlug) {
        click(addToCartButtonFor(productSlug));
        return this;
    }

    public int getCartItemCount() {
        // The badge element isn't rendered at all when the cart is empty,
        // so check for its existence first instead of waiting the full
        // explicit-wait timeout on every call.
        return elementExists(cartBadge) ? Integer.parseInt(getText(cartBadge)) : 0;
    }

    public CartPage goToCart() {
        click(cartIcon);
        // Wait on the URL actually changing to the cart page rather than a
        // specific container id -- that way this can't break if the DOM
        // structure differs from what we assumed.
        wait.until(ExpectedConditions.urlContains("cart.html"));
        return new CartPage(driver);
    }

    /**
     * Sort the inventory list. The dropdown is a native &lt;select&gt;, so it
     * must be driven through Selenium's Select wrapper rather than sendKeys,
     * which does not reliably choose an option on a select element.
     *
     * @param value the option's value attribute, e.g. "lohi", "hilo", "az", "za"
     */
    public void sortBy(String value) {
        Select sort = new Select(waitForVisible(sortDropdown));
        sort.selectByValue(value);
    }

    public void logout() {
        click(menuButton);
        click(logoutLink);
    }
}
