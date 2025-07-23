package org.airflow.reservations.service;

import org.airflow.reservations.model.City;
import org.airflow.reservations.utils.ConnectionDB;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the CityService class.
 * Tests the service layer functionality for city operations.
 */
public class CityServiceTest {
    private Connection connection;
    private CityService cityService;
    private static int insertedCityId;

    /**
     * Initializes a database connection and CityService before each test.
     */
    @BeforeEach
    void setUp() throws SQLException {
        connection = ConnectionDB.getConnection();
        cityService = new CityService(); // This throws SQLException
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
     * Creates test city data in the database.
     */
    private void createTestData() throws SQLException {
        Statement statement = connection.createStatement();

        // Insert test city
        String insertCitySQL = "INSERT INTO cities (name, country, code) " +
                "VALUES ('Test City', 'Test Country', 'TST')";

        statement.executeUpdate(insertCitySQL, Statement.RETURN_GENERATED_KEYS);

        var resultSet = statement.getGeneratedKeys();
        if (resultSet.next()) {
            insertedCityId = resultSet.getInt(1);
        }

        statement.close();
    }

    /**
     * Removes test data from the database.
     */
    private void cleanupTestData() throws SQLException {
        Statement statement = connection.createStatement();

        // Delete test city
        if (insertedCityId > 0) {
            statement.executeUpdate("DELETE FROM cities WHERE id_PK = " + insertedCityId);
        }

        statement.close();
    }

    /**
     * Tests retrieval of a city by name.
     */
    @Test
    void testGetCityByName() throws SQLException {
        // Act
        City city = cityService.getCityByName("Test City");

        // Assert
        assertNotNull(city, "City should not be null");
        assertEquals("Test City", city.getName(), "City name should match");
        assertEquals("Test Country", city.getCountry(), "Country should match");
        assertEquals("TST", city.getCode(), "Code should match");
    }

    /**
     * Tests retrieval of a city by ID.
     */
    @Test
    void testGetCityById() throws SQLException {
        // Act
        City city = cityService.getCityById(insertedCityId);

        // Assert
        assertNotNull(city, "City should not be null");
        assertEquals(insertedCityId, city.getId(), "City ID should match");
        assertEquals("Test City", city.getName(), "City name should match");
        assertEquals("Test Country", city.getCountry(), "Country should match");
        assertEquals("TST", city.getCode(), "Code should match");
    }

    /**
     * Tests retrieval of all cities.
     */
    @Test
    void testGetAllCities() throws SQLException {
        // Act
        ArrayList<City> cities = cityService.getAllCities();

        // Assert
        assertNotNull(cities, "Cities list should not be null");
        assertFalse(cities.isEmpty(), "Cities list should not be empty");

        // Find our test city in the results
        boolean testCityFound = false;
        for (City city : cities) {
            if (city.getName().equals("Test City")) {
                testCityFound = true;
                assertEquals("Test Country", city.getCountry(), "Country should match");
                assertEquals("TST", city.getCode(), "Code should match");
                break;
            }
        }
        assertTrue(testCityFound, "Test city should be found in the results");
    }

    /**
     * Tests retrieval of a non-existent city by name.
     */
    @Test
    void testGetCityByNameNotFound() throws SQLException {
        // Act
        City city = cityService.getCityByName("Nonexistent City");

        // Assert - When no record is found, DAO returns empty object with ID = 0
        assertNotNull(city, "City object should not be null");
        assertEquals(0, city.getId(), "City ID should be 0 for non-existent name");
        assertEquals("", city.getName(), "City name should be empty for non-existent record");
    }

    /**
     * Tests retrieval of a non-existent city by ID.
     */
    @Test
    void testGetCityByIdNotFound() throws SQLException {
        // Act
        City city = cityService.getCityById(99999);

        // Assert - When no record is found, DAO returns empty object with ID = 0
        assertNotNull(city, "City object should not be null");
        assertEquals(0, city.getId(), "City ID should be 0 for non-existent ID");
        assertEquals("", city.getName(), "City name should be empty for non-existent record");
    }

    /**
     * Tests edge cases for city name search.
     */
    @Test
    void testGetCityByNameEdgeCases() throws SQLException {
        // Test with null name
        City city = cityService.getCityByName(null);
        assertNotNull(city, "City object should not be null");
        assertEquals(0, city.getId(), "City ID should be 0 for null name");

        // Test with empty name
        city = cityService.getCityByName("");
        assertNotNull(city, "City object should not be null");
        assertEquals(0, city.getId(), "City ID should be 0 for empty name");
    }

    /**
     * Tests edge cases for city ID search.
     */
    @Test
    void testGetCityByIdEdgeCases() throws SQLException {
        // Test with negative ID
        City city = cityService.getCityById(-1);
        assertNotNull(city, "City object should not be null");
        assertEquals(0, city.getId(), "City ID should be 0 for negative ID");

        // Test with zero ID
        city = cityService.getCityById(0);
        assertNotNull(city, "City object should not be null");
        assertEquals(0, city.getId(), "City ID should be 0 for zero ID");
    }

    /**
     * Tests that the service properly handles database connection issues.
     */
    @Test
    void testServiceInitialization() {
        // Assert
        assertNotNull(cityService, "CityService should be properly initialized");

        // Test that we can create a new service instance
        assertDoesNotThrow(() -> {
            CityService newService = new CityService();
            assertNotNull(newService, "New CityService instance should be created");
        }, "CityService creation should not throw exception");
    }
}
