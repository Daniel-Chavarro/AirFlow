package org.airflow.reservations.DAO;

import org.airflow.reservations.model.Airplane;
import org.airflow.reservations.utils.ConnectionDB;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Year;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the AirplaneDAO class.
 * Tests CRUD operations and specialized queries for Airplane entities.
 */
public class AirplaneDAOTest {
    private Connection connection;
    private AirplaneDAO airplaneDAO;
    private static int testAirplaneId;

    /**
     * Sets up the test environment before each test.
     * Establishes a database connection and creates test data.
     *
     * @throws SQLException if a database error occurs
     */
    @BeforeEach
    void setUp() throws SQLException {
        connection = ConnectionDB.getConnection();
        airplaneDAO = new AirplaneDAO(connection);
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
     * Creates test airplane data in the database for testing purposes.
     *
     * @throws SQLException if a database error occurs
     */
    private void createTestData() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String insertAirplane = "INSERT INTO airplanes (airline, model, code, capacity, year) VALUES " +
                    "('TestAirline', 'TestModel', 'TA123', 200, 2020)";

            statement.executeUpdate(insertAirplane, Statement.RETURN_GENERATED_KEYS);
            var rs = statement.getGeneratedKeys();
            if (rs.next()) {
                testAirplaneId = rs.getInt(1);
            }
        }
    }

    /**
     * Removes test airplane data from the database after tests are complete.
     *
     * @throws SQLException if a database error occurs
     */
    private void cleanupTestData() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM airplanes WHERE code = 'TA123'");
        }
    }

    /**
     * Tests the getAll method to ensure it retrieves all airplanes from the database.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testGetAll() throws SQLException {
        ArrayList<Airplane> airplanes = airplaneDAO.getAll();

        assertNotNull(airplanes);
        assertFalse(airplanes.isEmpty(), "Airplane list should not be empty");

        boolean found = false;
        for (Airplane airplane : airplanes) {
            if (airplane.getCode().equals("TA123")) {
                found = true;
                break;
            }
        }

        assertTrue(found, "Test airplane should be in the list");
    }

    /**
     * Tests the getById method to ensure it retrieves a specific airplane by ID.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testGetById() throws SQLException {
        Airplane airplane = airplaneDAO.getById(testAirplaneId);

        assertNotNull(airplane);
        assertEquals("TestAirline", airplane.getAirline());
        assertEquals("TestModel", airplane.getModel());
        assertEquals("TA123", airplane.getCode());
        assertEquals(200, airplane.getCapacity());
        assertEquals(Year.of(2020), airplane.getYear());
    }

    /**
     * Tests the create method to ensure it properly inserts a new airplane into the database.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testCreate() throws SQLException {
        Airplane newAirplane = new Airplane();
        newAirplane.setAirline("NewAirline");
        newAirplane.setModel("NewModel");
        newAirplane.setCode("NA456");
        newAirplane.setCapacity(150);
        newAirplane.setYear(Year.of(2021));

        airplaneDAO.create(newAirplane);

        ArrayList<Airplane> airplanes = airplaneDAO.getAll();
        boolean found = false;
        for (Airplane airplane : airplanes) {
            if (airplane.getCode().equals("NA456")) {
                found = true;
                airplaneDAO.delete(airplane.getId());
                break;
            }
        }

        assertTrue(found, "New airplane should have been created");
    }

    /**
     * Tests the update method to ensure it properly updates an existing airplane in the database.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testUpdate() throws SQLException {
        Airplane airplane = airplaneDAO.getById(testAirplaneId);

        airplane.setCapacity(250);
        airplane.setAirline("UpdatedAirline");

        airplaneDAO.update(testAirplaneId, airplane);

        Airplane updatedAirplane = airplaneDAO.getById(testAirplaneId);
        assertEquals("UpdatedAirline", updatedAirplane.getAirline());
        assertEquals(250, updatedAirplane.getCapacity());
        assertEquals("TA123", updatedAirplane.getCode());
    }

    /**
     * Tests the delete method to ensure it properly removes an airplane from the database.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testDelete() throws SQLException {
        Airplane airplaneToDelete = new Airplane();
        airplaneToDelete.setAirline("DeleteAirline");
        airplaneToDelete.setModel("DeleteModel");
        airplaneToDelete.setCode("DEL123");
        airplaneToDelete.setCapacity(100);
        airplaneToDelete.setYear(Year.of(2019));

        airplaneDAO.create(airplaneToDelete);

        ArrayList<Airplane> airplanes = airplaneDAO.getAll();
        int deleteId = -1;
        for (Airplane a : airplanes) {
            if (a.getCode().equals("DEL123")) {
                deleteId = a.getId();
                break;
            }
        }

        airplaneDAO.delete(deleteId);

        airplanes = airplaneDAO.getAll();
        boolean found = false;
        for (Airplane a : airplanes) {
            if (a.getCode().equals("DEL123")) {
                found = true;
                break;
            }
        }

        assertFalse(found, "Airplane should have been deleted");
    }

    /**
     * Tests the getByCode method to ensure it retrieves an airplane by its code.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testGetByCode() throws SQLException {
        Airplane airplane = airplaneDAO.getByCode("TA123");

        assertNotNull(airplane);
        assertEquals("TestAirline", airplane.getAirline());
        assertEquals("TestModel", airplane.getModel());
        assertEquals(200, airplane.getCapacity());
        assertEquals(Year.of(2020), airplane.getYear());
    }

    /**
     * Tests the getByAirline method to ensure it retrieves airplanes with airline names matching the search string.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testGetByAirline() throws SQLException {
        ArrayList<Airplane> airplanes = airplaneDAO.getByAirline("TestAir");

        assertNotNull(airplanes);
        assertFalse(airplanes.isEmpty(), "Should find airplanes with airline containing 'TestAir'");

        boolean found = false;
        for (Airplane airplane : airplanes) {
            if (airplane.getAirline().equals("TestAirline")) {
                found = true;
                break;
            }
        }

        assertTrue(found, "Test airplane should be in the results");

        // Test with a partial airline name that should match
        airplanes = airplaneDAO.getByAirline("Test");
        assertFalse(airplanes.isEmpty(), "Should find airplanes with airline containing 'Test'");

        // Test with an airline name that shouldn't match
        airplanes = airplaneDAO.getByAirline("NonExistentAirline");
        assertTrue(airplanes.isEmpty(), "Should not find airplanes with airline containing 'NonExistentAirline'");
    }

    /**
     * Tests the getByCapacity method to ensure it retrieves airplanes with the specified capacity.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testGetByCapacity() throws SQLException {
        ArrayList<Airplane> airplanes = airplaneDAO.getByCapacity(200);

        assertNotNull(airplanes);
        assertFalse(airplanes.isEmpty(), "Should find airplanes with capacity of 200");

        boolean found = false;
        for (Airplane airplane : airplanes) {
            if (airplane.getCode().equals("TA123")) {
                found = true;
                break;
            }
        }

        assertTrue(found, "Test airplane should be in the results");

        // Test with a capacity that shouldn't match
        airplanes = airplaneDAO.getByCapacity(999);
        assertTrue(airplanes.isEmpty(), "Should not find airplanes with capacity of 999");
    }
}
