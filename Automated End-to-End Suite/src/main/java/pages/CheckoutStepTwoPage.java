package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for checkout step two: order overview / summary.
 */
public class CheckoutStepTwoPage extends BasePage {

    private final By finishButton = By.id("finish");
    private final By totalLabel = By.className("summary_total_label");
    private final By subtotalLabel = By.className("summary_subtotal_label");

    public CheckoutStepTwoPage(WebDriver driver) {
        super(driver);
    }

    public String getSubtotal() {
        return getText(subtotalLabel);
    }

    public String getTotal() {
        return getText(totalLabel);
    }

    public CheckoutCompletePage finishOrder() {
        click(finishButton);
        return new CheckoutCompletePage(driver);
    }
}
