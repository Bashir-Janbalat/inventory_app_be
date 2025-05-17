package org.inventory.app.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Component
public class DatabaseCleaner {

    @Autowired
    private DataSource dataSource;

    List<String> applicationTables = List.of(
            "PURCHASE_ITEMS",
            "PURCHASES",
            "STOCK_MOVEMENTS",
            "STOCK",
            "PRODUCT_ATTRIBUTES",
            "PRODUCTS",
            "IMAGES",
            "ATTRIBUTES",
            "BRANDS",
            "CATEGORIES",
            "SUPPLIERS",
            "WAREHOUSES",
            "USERS",
            "USER_ROLES",
            "ROLES"
    );

    public void clean() {
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("SET REFERENTIAL_INTEGRITY FALSE");

            for (String table : applicationTables) {
                stmt.executeUpdate("TRUNCATE TABLE " + table);
            }

            stmt.execute("SET REFERENTIAL_INTEGRITY TRUE");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clean database", e);
        }
    }
}
