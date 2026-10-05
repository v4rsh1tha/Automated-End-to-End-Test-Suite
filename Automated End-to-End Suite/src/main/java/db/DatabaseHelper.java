package db;

import java.sql.*;

/**
 * Lightweight helper around an embedded H2 database.
 *
 * Purpose: demonstrate SQL-based validation of order/checkout data as part
 * of the automation suite (CRUD operations, JOIN-based validation), the way
 * a real project would cross-check UI results against backend records.
 *
 * H2 runs in-memory, so no external database server or network access is
 * required to build or run this project.
 */
public class DatabaseHelper {

    private final Connection connection;

    public DatabaseHelper() throws SQLException {
        // In-memory DB; DB_CLOSE_DELAY=-1 keeps it alive for the JVM's lifetime
        this.connection = DriverManager.getConnection(
                "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
        createSchema();
        seedData();
    }

    private void createSchema() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS products (
                    product_id   INT PRIMARY KEY,
                    name         VARCHAR(100) NOT NULL,
                    price        DECIMAL(10,2) NOT NULL
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS orders (
                    order_id     INT PRIMARY KEY AUTO_INCREMENT,
                    product_id   INT NOT NULL,
                    quantity     INT NOT NULL,
                    status       VARCHAR(20) NOT NULL,
                    FOREIGN KEY (product_id) REFERENCES products(product_id)
                )
            """);
        }
    }

    private void seedData() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("MERGE INTO products KEY(product_id) VALUES " +
                    "(1, 'Sauce Labs Backpack', 29.99), " +
                    "(2, 'Sauce Labs Bike Light', 9.99), " +
                    "(3, 'Sauce Labs Bolt T-Shirt', 15.99)");
        }
    }

    /** CRUD: Create - insert a new order row, mirrors a checkout completing in the UI. */
    public int insertOrder(int productId, int quantity, String status) throws SQLException {
        String sql = "INSERT INTO orders (product_id, quantity, status) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, productId);
            ps.setInt(2, quantity);
            ps.setString(3, status);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    /** CRUD: Read + JOIN - validate an order's total price against the product catalog. */
    public double getOrderTotal(int orderId) throws SQLException {
        String sql = """
            SELECT p.price * o.quantity AS total
            FROM orders o
            JOIN products p ON o.product_id = p.product_id
            WHERE o.order_id = ?
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getDouble("total");
            }
        }
    }

    /** CRUD: Update - change order status, e.g. after checkout confirmation in the UI. */
    public void updateOrderStatus(int orderId, String status) throws SQLException {
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        }
    }

    public String getOrderStatus(int orderId) throws SQLException {
        String sql = "SELECT status FROM orders WHERE order_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getString("status");
            }
        }
    }

    /** CRUD: Delete - remove a cancelled order. */
    public void deleteOrder(int orderId) throws SQLException {
        String sql = "DELETE FROM orders WHERE order_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.executeUpdate();
        }
    }

    public void close() throws SQLException {
        connection.close();
    }
}
