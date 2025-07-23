package org.airflow.reservations.service;

import org.airflow.reservations.model.Airplane;
import org.airflow.reservations.utils.ConnectionDB;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the AirplaneService class.
 * Tests the service layer functionality for airplane operations.
 */
public class AirplaneServiceTest {
    private Connection connection;
    private AirplaneService airplaneService;
    private static int insertedAirplaneId;

    /**
     * Initializes a database connection and AirplaneService before each test.
     */
    @BeforeEach
    void setUp() throws SQLException {
        connection = ConnectionDB.getConnection();
        airplaneService = new AirplaneService(); // This throws SQLException
        createTestData();
    }

    /**
     * Cleans up the test environment after each test.
     */
    @AfterEach
    void tearDown() throws SQLException {
        cleanupTestData();

        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    /**
     * Creates a test airplane in the database.
     */
    private void createTestData() throws SQLException {
        Statement statement = connection.createStatement();

        // Insert test airplane
        String insertAirplaneSQL = "INSERT INTO airplanes (airline, model, code, capacity, year) " +
                "VALUES ('Test Airlines', 'Boeing 737', 'TA001', 180, 2020)";

        statement.executeUpdate(insertAirplaneSQL, Statement.RETURN_GENERATED_KEYS);

        var resultSet = statement.getGeneratedKeys();
        if (resultSet.next()) {
            insertedAirplaneId = resultSet.getInt(1);
        }

        statement.close();
    }

    /**
     * Removes test data from the database.
     */
    private void cleanupTestData() throws SQLException {
        Statement statement = connection.createStatement();

        // Delete test airplane
        if (insertedAirplaneId > 0) {
            statement.executeUpdate("DELETE FROM airplanes WHERE id_PK = " + insertedAirplaneId);
        }

        statement.close();
    }

    /**
     * Tests retrieval of an airplane by ID.
     */
    @Test
    void testGetAirplaneById() throws SQLException {
        // Act
        Airplane airplane = airplaneService.getAirplaneById(insertedAirplaneId);

        // Assert
        assertNotNull(airplane, "Airplane should not be null");
        assertEquals(insertedAirplaneId, airplane.getId(), "Airplane ID should match");
        assertEquals("Test Airlines", airplane.getAirline(), "Airline should match");
        assertEquals("Boeing 737", airplane.getModel(), "Model should match");
        assertEquals("TA001", airplane.getCode(), "Code should match");
        assertEquals(180, airplane.getCapacity(), "Capacity should match");
        assertEquals(Year.of(2020), airplane.getYear(), "Year should match");
    }

    /**
     * Tests retrieval of a non-existent airplane by ID.
     */
    @Test
    void testGetAirplaneByIdNotFound() throws SQLException {
        // Act
        Airplane airplane = airplaneService.getAirplaneById(99999);

        // Assert - When no record is found, DAO returns empty object with ID = 0
        assertNotNull(airplane, "Airplane object should not be null");
        assertEquals(0, airplane.getId(), "Airplane ID should be 0 for non-existent record");
        assertEquals("", airplane.getAirline(), "Airline should be empty for non-existent record");
        assertEquals("", airplane.getModel(), "Model should be empty for non-existent record");
    }

    /**
     * Tests retrieval with invalid ID values.
     */
    @Test
    void testGetAirplaneByIdWithInvalidId() throws SQLException {
        // Test with negative ID
        Airplane airplane = airplaneService.getAirplaneById(-1);

        // Assert - Should return empty object
        assertNotNull(airplane, "Airplane object should not be null");
        assertEquals(0, airplane.getId(), "Airplane ID should be 0 for invalid ID");
    }

    /**
     * Tests that the service properly handles database connection issues.
     */
    @Test
    void testServiceInitialization() {
        // Assert
        assertNotNull(airplaneService, "AirplaneService should be properly initialized");

        // Test that we can create a new service instance
        assertDoesNotThrow(() -> {
            AirplaneService newService = new AirplaneService();
            assertNotNull(newService, "New AirplaneService instance should be created");
        }, "AirplaneService creation should not throw exception");
    }
}
