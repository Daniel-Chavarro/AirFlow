package org.airflow.reservations.utils;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the ConnectionDB class.
 * Tests the database connection functionality.
 */
public class ConnectionDBTest {

    /**
     * Tests that a connection to the database can be successfully established.
     */
    @Test
    public void testGetConnection() {
        try {
            Connection connection = ConnectionDB.getConnection();
            assertNotNull(connection, "Connection should not be null");
            assertFalse(connection.isClosed(), "Connection should be open");
            connection.close();
        } catch (SQLException e) {
            fail("Failed to establish database connection: " + e.getMessage());
        }
    }

    /**
     * Tests that a connection can be properly closed.
     */
    @Test
    public void testCloseConnection() {
        try {
            Connection connection = ConnectionDB.getConnection();
            connection.close();
            assertTrue(connection.isClosed(), "Connection should be closed");
        } catch (SQLException e) {
            fail("Error closing connection: " + e.getMessage());
        }
    }
}
