package org.airflow.reservations.DAO;

import org.airflow.reservations.model.City;
import org.airflow.reservations.utils.ConnectionDB;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the CityDAO class.
 * Tests CRUD operations and specialized queries for City entities.
 */
public class CityDAOTest {
    private Connection connection;
    private CityDAO cityDAO;
    private static int testCityId;

    /**
     * Sets up the test environment before each test.
     * Establishes a database connection and creates test data.
     *
     * @throws SQLException if a database error occurs
     */
    @BeforeEach
    void setUp() throws SQLException {
        connection = ConnectionDB.getConnection();
        cityDAO = new CityDAO(connection);
        createTestData();
    }

    /**
     * Cleans up the test environment after each test.
     * Removes test data and closes the database connection.
     *
     * @throws SQLException if a database error occurs
     */
    @AfterEach
    void tearDown() throws SQLException {
        cleanupTestData();

        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    /**
     * Creates test city data in the database for testing purposes.
     *
     * @throws SQLException if a database error occurs
     */
    private void createTestData() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String insertCity = "INSERT INTO cities (name, country, code) VALUES " +
                    "('TestCity', 'TestCountry', 'TCY')";

            statement.executeUpdate(insertCity, Statement.RETURN_GENERATED_KEYS);
            var rs = statement.getGeneratedKeys();
            if (rs.next()) {
                testCityId = rs.getInt(1);
            }
        }
    }

    /**
     * Removes test city data from the database after tests are complete.
     *
     * @throws SQLException if a database error occurs
     */
    private void cleanupTestData() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM cities WHERE code = 'TCY'");
        }
    }

    /**
     * Tests the getAll method to ensure it retrieves all cities from the database.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testGetAll() throws SQLException {
        ArrayList<City> cities = cityDAO.getAll();

        assertNotNull(cities);
        assertFalse(cities.isEmpty(), "City list should not be empty");

        boolean found = false;
        for (City city : cities) {
            if (city.getCode().equals("TCY")) {
                found = true;
                break;
            }
        }

        assertTrue(found, "Test city should be in the list");
    }

    /**
     * Tests the getById method to ensure it retrieves a specific city by ID.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testGetById() throws SQLException {
        City city = cityDAO.getById(testCityId);

        assertNotNull(city);
        assertEquals("TestCity", city.getName());
        assertEquals("TestCountry", city.getCountry());
        assertEquals("TCY", city.getCode());
    }

    /**
     * Tests the create method to ensure it properly inserts a new city into the database.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testCreate() throws SQLException {
        City newCity = new City();
        newCity.setName("NewCity");
        newCity.setCountry("NewCountry");
        newCity.setCode("NCY");

        cityDAO.create(newCity);

        ArrayList<City> cities = cityDAO.getAll();
        boolean found = false;
        for (City city : cities) {
            if (city.getCode().equals("NCY")) {
                found = true;
                cityDAO.delete(city.getId());
                break;
            }
        }

        assertTrue(found, "New city should have been created");
    }

    /**
     * Tests the update method to ensure it properly updates an existing city in the database.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testUpdate() throws SQLException {
        City city = cityDAO.getById(testCityId);

        city.setName("UpdatedCity");
        city.setCountry("UpdatedCountry");

        cityDAO.update(testCityId, city);

        City updatedCity = cityDAO.getById(testCityId);
        assertEquals("UpdatedCity", updatedCity.getName());
        assertEquals("UpdatedCountry", updatedCity.getCountry());
        assertEquals("TCY", updatedCity.getCode());
    }

    /**
     * Tests the delete method to ensure it properly removes a city from the database.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testDelete() throws SQLException {
        City cityToDelete = new City();
        cityToDelete.setName("DeleteMe");
        cityToDelete.setCountry("TestCountry");
        cityToDelete.setCode("DEL");

        cityDAO.create(cityToDelete);

        ArrayList<City> cities = cityDAO.getAll();
        int deleteId = -1;
        for (City c : cities) {
            if (c.getCode().equals("DEL")) {
                deleteId = c.getId();
                break;
            }
        }

        cityDAO.delete(deleteId);

        cities = cityDAO.getAll();
        boolean found = false;
        for (City c : cities) {
            if (c.getCode().equals("DEL")) {
                found = true;
                break;
            }
        }

        assertFalse(found, "City should have been deleted");
    }

    /**
     * Tests the getByName method to ensure it retrieves cities with names matching the search string.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testGetByName() throws SQLException {
        ArrayList<City> cities = cityDAO.getByName("TestCi");

        assertNotNull(cities);
        assertFalse(cities.isEmpty(), "Should find cities with name containing 'TestCi'");

        boolean found = false;
        for (City city : cities) {
            if (city.getName().equals("TestCity")) {
                found = true;
                break;
            }
        }

        assertTrue(found, "Test city should be in the results");

        // Test with a partial name that should match
        cities = cityDAO.getByName("Test");
        assertFalse(cities.isEmpty(), "Should find cities with name containing 'Test'");

        // Test with a name that shouldn't match
        cities = cityDAO.getByName("NonExistentCity");
        assertTrue(cities.isEmpty(), "Should not find cities with name containing 'NonExistentCity'");
    }

    /**
     * Tests the getByCountry method to ensure it retrieves cities with countries matching the search string.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testGetByCountry() throws SQLException {
        ArrayList<City> cities = cityDAO.getByCountry("TestCoun");

        assertNotNull(cities);
        assertFalse(cities.isEmpty(), "Should find cities with country containing 'TestCoun'");

        boolean found = false;
        for (City city : cities) {
            if (city.getCountry().equals("TestCountry")) {
                found = true;
                break;
            }
        }

        assertTrue(found, "Test city should be in the results");

        // Test with a partial country that should match
        cities = cityDAO.getByCountry("Test");
        assertFalse(cities.isEmpty(), "Should find cities with country containing 'Test'");

        // Test with a country that shouldn't match
        cities = cityDAO.getByCountry("NonExistentCountry");
        assertTrue(cities.isEmpty(), "Should not find cities with country containing 'NonExistentCountry'");
    }

    /**
     * Tests the getByCode method to ensure it retrieves cities with codes matching the search string.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testGetByCode() throws SQLException {
        ArrayList<City> cities = cityDAO.getByCode("TC");

        assertNotNull(cities);
        assertFalse(cities.isEmpty(), "Should find cities with code containing 'TC'");

        boolean found = false;
        for (City city : cities) {
            if (city.getCode().equals("TCY")) {
                found = true;
                break;
            }
        }

        assertTrue(found, "Test city should be in the results");

        // Test with a partial code that should match
        cities = cityDAO.getByCode("T");
        assertFalse(cities.isEmpty(), "Should find cities with code containing 'T'");

        // Test with a code that shouldn't match
        cities = cityDAO.getByCode("XYZ");
        assertTrue(cities.isEmpty(), "Should not find cities with code containing 'XYZ'");
    }
}
