package org.airflow.reservations.DAO;

import org.airflow.reservations.model.Flight;
import org.airflow.reservations.utils.ConnectionDB;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the FlightDAO class.
 * Tests CRUD operations and specialized queries for Flight entities.
 */
public class FlightDAOTest {
    private Connection connection;
    private FlightDAO flightDAO;
    private static int testFlightId;

    /**
     * Sets up the test environment before each test.
     * Establishes a database connection and creates test data.
     *
     * @throws SQLException if a database error occurs
     */
    @BeforeEach
    void setUp() throws SQLException {
        connection = ConnectionDB.getConnection();
        flightDAO = new FlightDAO(connection);
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
     * Creates test flight data in the database for testing purposes.
     *
     * @throws SQLException if a database error occurs
     */
    private void createTestData() throws SQLException {
        LocalDateTime departureTime = LocalDateTime.now().plusDays(1);
        LocalDateTime arrivalTime = departureTime.plusHours(2);

        try (Statement statement = connection.createStatement()) {
            String insertFlight = "INSERT INTO flights (airplane_FK, status_FK, origin_city_FK, destination_city_FK, " +
                    "code, departure_time, arrival_time, price_base) VALUES " +
                    "(1, 1, 1, 2, 'TEST123', '" + departureTime + "', '" + arrivalTime + "', 150.0)";

            statement.executeUpdate(insertFlight, Statement.RETURN_GENERATED_KEYS);
            var rs = statement.getGeneratedKeys();
            if (rs.next()) {
                testFlightId = rs.getInt(1);
            }
        }
    }

    /**
     * Removes test flight data from the database after tests are complete.
     *
     * @throws SQLException if a database error occurs
     */
    private void cleanupTestData() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM flights WHERE code = 'TEST123'");
        }
    }

    /**
     * Tests the getAll method to ensure it retrieves all flights from the database.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testGetAll() throws SQLException {
        ArrayList<Flight> flights = flightDAO.getAll();

        assertNotNull(flights);
        assertFalse(flights.isEmpty(), "Flight list should not be empty");

        boolean found = false;
        for (Flight flight : flights) {
            if (flight.getCode().equals("TEST123")) {
                found = true;
                break;
            }
        }

        assertTrue(found, "Test flight should be in the list");
    }

    /**
     * Tests the getById method to ensure it retrieves a specific flight by ID.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testGetById() throws SQLException {
        Flight flight = flightDAO.getById(testFlightId);

        assertNotNull(flight);
        assertEquals("TEST123", flight.getCode());
        assertEquals(150.0f, flight.getPrice_base(), 0.001);
    }

    /**
     * Tests the create method to ensure it properly inserts a new flight into the database.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testCreate() throws SQLException {
        Flight newFlight = new Flight();
        newFlight.setAirplane_FK(1);
        newFlight.setStatus_FK(1);
        newFlight.setOrigin_city_FK(2);
        newFlight.setDestination_city_FK(1);
        newFlight.setCode("TEST456");
        newFlight.setDeparture_time(LocalDateTime.now().plusDays(2));
        newFlight.setArrival_time(LocalDateTime.now().plusDays(2).plusHours(3));
        newFlight.setPrice_base(200.0f);

        flightDAO.create(newFlight);

        ArrayList<Flight> flights = flightDAO.getAll();
        boolean found = false;
        for (Flight flight : flights) {
            if (flight.getCode().equals("TEST456")) {
                found = true;
                flightDAO.delete(flight.getId());
                break;
            }
        }

        assertTrue(found, "New flight should have been created");
    }

    /**
     * Tests the update method to ensure it properly updates an existing flight in the database.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testUpdate() throws SQLException {
        Flight flight = flightDAO.getById(testFlightId);

        flight.setPrice_base(175.0f);
        flight.setCode("UPDATED");

        flightDAO.update(testFlightId, flight);

        Flight updatedFlight = flightDAO.getById(testFlightId);
        assertEquals("UPDATED", updatedFlight.getCode());
        assertEquals(175.0f, updatedFlight.getPrice_base(), 0.001);
    }

    /**
     * Tests the delete method to ensure it properly removes a flight from the database.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testDelete() throws SQLException {
        Flight flightToDelete = new Flight();
        flightToDelete.setAirplane_FK(1);
        flightToDelete.setStatus_FK(1);
        flightToDelete.setOrigin_city_FK(1);
        flightToDelete.setDestination_city_FK(2);
        flightToDelete.setCode("DELETE_ME");
        flightToDelete.setDeparture_time(LocalDateTime.now().plusDays(3));
        flightToDelete.setArrival_time(LocalDateTime.now().plusDays(3).plusHours(2));
        flightToDelete.setPrice_base(100.0f);

        flightDAO.create(flightToDelete);

        ArrayList<Flight> flights = flightDAO.getAll();
        int deleteId = -1;
        for (Flight f : flights) {
            if (f.getCode().equals("DELETE_ME")) {
                deleteId = f.getId();
                break;
            }
        }

        flightDAO.delete(deleteId);

        flights = flightDAO.getAll();
        boolean found = false;
        for (Flight f : flights) {
            if (f.getCode().equals("DELETE_ME")) {
                found = true;
                break;
            }
        }

        assertFalse(found, "Flight should have been deleted");
    }

    /**
     * Tests the getByOriginCity method to ensure it retrieves flights by origin city.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testGetByOriginCity() throws SQLException {
        ArrayList<Flight> flights = flightDAO.getByOriginCity(1);

        assertNotNull(flights);
        assertFalse(flights.isEmpty(), "There should be flights with origin city 1");

        for (Flight flight : flights) {
            assertEquals(1, flight.getOrigin_city_FK());
        }
    }

    /**
     * Tests the getByDestinationCity method to ensure it retrieves flights by destination city.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testGetByDestinationCity() throws SQLException {
        ArrayList<Flight> flights = flightDAO.getByDestinationCity(2);

        assertNotNull(flights);
        assertFalse(flights.isEmpty(), "There should be flights with destination city 2");

        for (Flight flight : flights) {
            assertEquals(2, flight.getDestination_city_FK());
        }
    }

    /**
     * Tests the getByCode method to ensure it retrieves flights with codes matching the search string.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testGetByCode() throws SQLException {
        ArrayList<Flight> flights = flightDAO.getByCode("TEST");

        assertNotNull(flights);
        assertFalse(flights.isEmpty(), "Should find flights with code containing 'TEST'");

        boolean found = false;
        for (Flight flight : flights) {
            if (flight.getCode().equals("TEST123")) {
                found = true;
                break;
            }
        }

        assertTrue(found, "Test flight should be in the results");

        // Test with a partial code that should match
        flights = flightDAO.getByCode("123");
        assertFalse(flights.isEmpty(), "Should find flights with code containing '123'");

        // Test with a code that shouldn't match
        flights = flightDAO.getByCode("NONEXISTENT");
        assertTrue(flights.isEmpty(), "Should not find flights with code containing 'NONEXISTENT'");
    }
}
