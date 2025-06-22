package org.airflow.reservations.service;

import org.airflow.reservations.DAO.FlightDAO;
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
 * Unit tests for the FlightService class.
 * Tests additional business logic not covered in FlightDAO.
 */
public class FlightServiceTest {
    private Connection connection;
    private FlightService flightService;
    private static int insertedFlightId;

    /**
     * Initializes a database connection and FlightService before each test.
     */
    @BeforeEach
    void setUp() throws SQLException {
        connection = ConnectionDB.getConnection();
        FlightDAO flightDAO = new FlightDAO(connection);
        flightService = new FlightService(flightDAO);
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
     * Creates a test flight in the database.
     */
    private void createTestData() throws SQLException {
        LocalDateTime departure = LocalDateTime.now().plusDays(1);
        LocalDateTime scheduledArrival = departure.plusHours(2);
        LocalDateTime actualArrival = scheduledArrival.plusMinutes(15); // 15 minutos de retraso

        try (Statement stmt = connection.createStatement()) {
            String sql = "INSERT INTO flights (airplane_FK, status_FK, origin_city_FK, destination_city_FK, " +
                    "code, departure_time, scheduled_arrival_time, arrival_time, price_base) VALUES " +
                    "(1, 1, 1, 2, 'SERVICE001', '" + departure + "', '" + scheduledArrival + "', '" +
                    actualArrival + "', 120.0)";
            stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);

            var rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                insertedFlightId = rs.getInt(1);
            }
        }
    }

    /**
     * Deletes the test flight from the database.
     */
    private void cleanupTestData() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DELETE FROM flights WHERE code IN ('SERVICE001', 'SERVICE_DUP', 'INVALID_DATE')");
        }
    }

    /**
     * Tests if existsFlightWithCode returns true when a flight exists.
     */
    @Test
    void testExistsFlightWithCode_True() throws SQLException {
        assertTrue(flightService.existsFlightWithCode("SERVICE001"));
    }

    /**
     * Tests if existsFlightWithCode returns false when a flight does not exist.
     */
    @Test
    void testExistsFlightWithCode_False() throws SQLException {
        assertFalse(flightService.existsFlightWithCode("NON_EXISTENT"));
    }

    /**
     * Tests that registerFlight inserts a new valid flight.
     */
    @Test
    void testRegisterFlight_Success() throws SQLException {
        Flight newFlight = new Flight(0, 1, 1, 1, 2, "SERVICE_002",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(2).plusHours(3),
                LocalDateTime.now().plusDays(2).plusHours(3),
                200.0f);

        flightService.registerFlight(newFlight);

        assertTrue(flightService.existsFlightWithCode("SERVICE_002"));
    }

    /**
     * Tests that registerFlight throws when code already exists.
     */
    @Test
    void testRegisterFlight_DuplicateCode_ThrowsException() throws SQLException {
        Flight duplicate = new Flight(0, 1, 1, 1, 2, "SERVICE_002",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(2).plusHours(3),
                LocalDateTime.now().plusDays(2).plusHours(3),
                200.0f);

        assertThrows(IllegalArgumentException.class, () -> {
            flightService.registerFlight(duplicate);
        });
    }

    /**
     * Tests that registerFlight throws when departure is not before arrival.
     */
    @Test
    void testRegisterFlight_InvalidTimes_ThrowsException() {
        Flight invalid = new Flight(0, 1, 1, 1, 2, "SERVICE_002",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(2).plusHours(3),
                LocalDateTime.now().plusDays(2).plusHours(3),
                200.0f);

        assertThrows(IllegalArgumentException.class, () -> {
            flightService.registerFlight(invalid);
        });
    }
}
