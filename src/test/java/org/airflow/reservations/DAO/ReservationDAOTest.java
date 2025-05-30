package org.airflow.reservations.DAO;

import org.airflow.reservations.model.Reservation;
import org.airflow.reservations.utils.ConnectionDB;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the ReservationDAO class.
 * Tests CRUD operations and specialized queries for Reservation entities.
 */
public class ReservationDAOTest {
    private Connection connection;
    private ReservationDAO reservationDAO;
    private static int testReservationId;

    /**
     * Sets up the test environment before each test.
     * Establishes a database connection and creates test data.
     *
     * @throws SQLException if a database error occurs
     */
    @BeforeEach
    void setUp() throws SQLException {
        connection = ConnectionDB.getConnection();
        reservationDAO = new ReservationDAO(connection);
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
     * Creates test reservation data in the database for testing purposes.
     *
     * @throws SQLException if a database error occurs
     */
    private void createTestData() throws SQLException {
        LocalDateTime reservedAt = LocalDateTime.now();

        try (Statement statement = connection.createStatement()) {
            String insertReservation = "INSERT INTO reservations (user_FK, status_FK, flight_FK, reserved_at) VALUES " +
                    "(1, 1, 1, '" + reservedAt + "')";

            statement.executeUpdate(insertReservation, Statement.RETURN_GENERATED_KEYS);
            var rs = statement.getGeneratedKeys();
            if (rs.next()) {
                testReservationId = rs.getInt(1);
            }
        }
    }

    /**
     * Removes test reservation data from the database after tests are complete.
     *
     * @throws SQLException if a database error occurs
     */
    private void cleanupTestData() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM reservations WHERE id_PK = " + testReservationId);
        }
    }

    /**
     * Tests the getAll method to ensure it retrieves all reservations from the database.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testGetAll() throws SQLException {
        ArrayList<Reservation> reservations = reservationDAO.getAll();

        assertNotNull(reservations);
        assertFalse(reservations.isEmpty(), "Reservation list should not be empty");

        boolean found = false;
        for (Reservation reservation : reservations) {
            if (reservation.getId() == testReservationId) {
                found = true;
                break;
            }
        }

        assertTrue(found, "Test reservation should be in the list");
    }

    /**
     * Tests the getById method to ensure it retrieves a specific reservation by ID.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testGetById() throws SQLException {
        Reservation reservation = reservationDAO.getById(testReservationId);

        assertNotNull(reservation);
        assertEquals(testReservationId, reservation.getId());
        assertEquals(1, reservation.getUser_FK());
        assertEquals(1, reservation.getStatus_FK());
        assertEquals(1, reservation.getFlight_FK());
    }

    /**
     * Tests the create method to ensure it properly inserts a new reservation into the database.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testCreate() throws SQLException {
        Reservation newReservation = new Reservation();
        newReservation.setUser_FK(2);
        newReservation.setStatus_FK(1);
        newReservation.setFlight_FK(2);
        newReservation.setReserved_at(LocalDateTime.now());

        reservationDAO.create(newReservation);

        ArrayList<Reservation> reservations = reservationDAO.getAll();
        boolean found = false;
        int newId = -1;

        for (Reservation r : reservations) {
            if (r.getUser_FK() == 2 && r.getFlight_FK() == 2) {
                found = true;
                newId = r.getId();
                break;
            }
        }

        assertTrue(found, "New reservation should have been created");

        // Clean up the created reservation
        if (newId != -1) {
            reservationDAO.delete(newId);
        }
    }

    /**
     * Tests the update method to ensure it properly updates an existing reservation in the database.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testUpdate() throws SQLException {
        Reservation reservation = reservationDAO.getById(testReservationId);

        // Change status
        reservation.setStatus_FK(2);

        reservationDAO.update(testReservationId, reservation);

        Reservation updatedReservation = reservationDAO.getById(testReservationId);
        assertEquals(2, updatedReservation.getStatus_FK());
        assertEquals(1, updatedReservation.getUser_FK());
        assertEquals(1, updatedReservation.getFlight_FK());
    }

    /**
     * Tests the delete method to ensure it properly removes a reservation from the database.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testDelete() throws SQLException {
        // Create a reservation to delete
        Reservation reservationToDelete = new Reservation();
        reservationToDelete.setUser_FK(3);
        reservationToDelete.setStatus_FK(1);
        reservationToDelete.setFlight_FK(3);
        reservationToDelete.setReserved_at(LocalDateTime.now());

        reservationDAO.create(reservationToDelete);

        // Find the ID of the newly created reservation
        ArrayList<Reservation> reservations = reservationDAO.getAll();
        int deleteId = -1;
        for (Reservation r : reservations) {
            if (r.getUser_FK() == 3 && r.getFlight_FK() == 3) {
                deleteId = r.getId();
                break;
            }
        }

        // Delete the reservation
        reservationDAO.delete(deleteId);

        // Verify it's been deleted
        reservations = reservationDAO.getAll();
        boolean found = false;
        for (Reservation r : reservations) {
            if (r.getId() == deleteId) {
                found = true;
                break;
            }
        }

        assertFalse(found, "Reservation should have been deleted");
    }

    /**
     * Tests the getReservationsByUser method to ensure it retrieves reservations by user ID.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testGetReservationsByUser() throws SQLException {
        ArrayList<Reservation> reservations = reservationDAO.getByUserId(1);

        assertNotNull(reservations);
        assertFalse(reservations.isEmpty(), "There should be reservations for user 1");

        for (Reservation reservation : reservations) {
            assertEquals(1, reservation.getUser_FK());
        }
    }

    /**
     * Tests the getReservationsByFlight method to ensure it retrieves reservations by flight ID.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testGetReservationsByFlight() throws SQLException {
        ArrayList<Reservation> reservations = reservationDAO.getByFlightId(1);

        assertNotNull(reservations);
        assertFalse(reservations.isEmpty(), "There should be reservations for flight 1");

        for (Reservation reservation : reservations) {
            assertEquals(1, reservation.getFlight_FK());
        }
    }
}
