package tests;

import db.DatabaseHelper;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.SQLException;

/**
 * Demonstrates SQL-based backend validation of order data, independent
 * of the UI. In a full end-to-end pipeline, order IDs/totals captured
 * from the UI checkout flow would be cross-checked against these same
 * queries to confirm the UI and backend agree.
 */
public class DatabaseValidationTest {

    private DatabaseHelper db;

    @BeforeClass
    public void setUp() throws SQLException {
        db = new DatabaseHelper();
    }

    @Test(description = "Order total should equal product price * quantity (JOIN validation)")
    public void orderTotalMatchesProductPriceTimesQuantity() throws SQLException {
        int orderId = db.insertOrder(1, 2, "PLACED"); // 2x Sauce Labs Backpack @ 29.99
        double total = db.getOrderTotal(orderId);
        Assert.assertEquals(total, 59.98, 0.001, "Order total did not match price * quantity");
    }

    @Test(description = "Order status should update correctly after checkout confirmation (CRUD: Update)")
    public void orderStatusUpdatesAfterConfirmation() throws SQLException {
        int orderId = db.insertOrder(2, 1, "PLACED");
        db.updateOrderStatus(orderId, "CONFIRMED");
        Assert.assertEquals(db.getOrderStatus(orderId), "CONFIRMED",
                "Order status was not updated as expected");
    }

    @Test(description = "Cancelled orders should be removable from the orders table (CRUD: Delete)")
    public void cancelledOrderCanBeDeleted() throws SQLException {
        int orderId = db.insertOrder(3, 1, "PLACED");
        db.deleteOrder(orderId);
        Assert.assertThrows(java.sql.SQLException.class, () -> {
            double total = db.getOrderTotal(orderId); // no row left to read -> throws
        });
    }

    @AfterClass
    public void tearDown() throws SQLException {
        db.close();
    }
}
