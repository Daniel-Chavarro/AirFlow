package org.airflow.reservations.service;

import org.airflow.reservations.DAO.ReservationDAO;
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
 * Integration tests for the ReservationService class.
 * Tests the waitlist functionality and reservation management operations.
 */
public class ReservationServiceIntegrationTest {
    private Connection connection;
    private ReservationService reservationService;
    private ReservationDAO reservationDAO;
    private static ArrayList<Integer> testReservationIds = new ArrayList<>();

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
        reservationService = new ReservationService(reservationDAO);
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
     * Creates multiple reservations with different statuses and times.
     *
     * @throws SQLException if a database error occurs
     */
    private void createTestData() throws SQLException {
        testReservationIds.clear();
        
        try (Statement statement = connection.createStatement()) {
            // Create waitlist reservations with different times to test ordering
            LocalDateTime time1 = LocalDateTime.now().minusHours(3);
            LocalDateTime time2 = LocalDateTime.now().minusHours(2);
            LocalDateTime time3 = LocalDateTime.now().minusHours(1);
            
            // Oldest waiting reservation (should be first in queue)
            String insert1 = "INSERT INTO reservations (user_FK, status_FK, flight_FK, reserved_at) VALUES " +
                    "(1, 1, 1, '" + time1 + "')";
            statement.executeUpdate(insert1, Statement.RETURN_GENERATED_KEYS);
            var rs1 = statement.getGeneratedKeys();
            if (rs1.next()) {
                testReservationIds.add(rs1.getInt(1));
            }

            // Second oldest waiting reservation
            String insert2 = "INSERT INTO reservations (user_FK, status_FK, flight_FK, reserved_at) VALUES " +
                    "(2, 1, 1, '" + time2 + "')";
            statement.executeUpdate(insert2, Statement.RETURN_GENERATED_KEYS);
            var rs2 = statement.getGeneratedKeys();
            if (rs2.next()) {
                testReservationIds.add(rs2.getInt(1));
            }

            // Third waiting reservation for different flight
            String insert3 = "INSERT INTO reservations (user_FK, status_FK, flight_FK, reserved_at) VALUES " +
                    "(3, 1, 2, '" + time3 + "')";
            statement.executeUpdate(insert3, Statement.RETURN_GENERATED_KEYS);
            var rs3 = statement.getGeneratedKeys();
            if (rs3.next()) {
                testReservationIds.add(rs3.getInt(1));
            }

            // Confirmed reservation (should not appear in waitlist)
            String insert4 = "INSERT INTO reservations (user_FK, status_FK, flight_FK, reserved_at) VALUES " +
                    "(4, 2, 1, '" + time3 + "')";
            statement.executeUpdate(insert4, Statement.RETURN_GENERATED_KEYS);
            var rs4 = statement.getGeneratedKeys();
            if (rs4.next()) {
                testReservationIds.add(rs4.getInt(1));
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
            for (Integer id : testReservationIds) {
                statement.executeUpdate("DELETE FROM reservations WHERE id_PK = " + id);
            }
        }
        testReservationIds.clear();
    }

    /**
     * Tests the getWaitlist method to ensure it retrieves waiting reservations ordered by time.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testGetWaitlist() throws SQLException {
        ArrayList<Reservation> waitlist = reservationService.getWaitlist();

        assertNotNull(waitlist);
        assertFalse(waitlist.isEmpty(), "Waitlist should not be empty");

        // Verify that only waiting reservations (status_FK = 1) are returned
        for (Reservation reservation : waitlist) {
            assertEquals(1, reservation.getStatus_FK(), "All reservations in waitlist should have status_FK = 1");
        }

        // Verify ordering by reserved_at ASC
        for (int i = 0; i < waitlist.size() - 1; i++) {
            assertTrue(
                waitlist.get(i).getReserved_at().isBefore(waitlist.get(i + 1).getReserved_at()) ||
                waitlist.get(i).getReserved_at().isEqual(waitlist.get(i + 1).getReserved_at()),
                "Waitlist should be ordered by reserved_at ASC"
            );
        }
    }

    /**
     * Tests the getWaitlistByFlight method to ensure it retrieves waiting reservations for a specific flight.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testGetWaitlistByFlight() throws SQLException {
        ArrayList<Reservation> waitlistFlight1 = reservationService.getWaitlistByFlight(1);
        ArrayList<Reservation> waitlistFlight2 = reservationService.getWaitlistByFlight(2);

        assertNotNull(waitlistFlight1);
        assertNotNull(waitlistFlight2);

        // Verify flight 1 has at least 2 waiting reservations
        assertTrue(waitlistFlight1.size() >= 2, "Flight 1 should have at least 2 waiting reservations");
        
        // Verify flight 2 has at least 1 waiting reservation
        assertTrue(waitlistFlight2.size() >= 1, "Flight 2 should have at least 1 waiting reservation");

        // Verify all reservations are for the correct flight and are waiting
        for (Reservation reservation : waitlistFlight1) {
            assertEquals(1, reservation.getFlight_FK(), "All reservations should be for flight 1");
            assertEquals(1, reservation.getStatus_FK(), "All reservations should be waiting status");
        }

        for (Reservation reservation : waitlistFlight2) {
            assertEquals(2, reservation.getFlight_FK(), "All reservations should be for flight 2");
            assertEquals(1, reservation.getStatus_FK(), "All reservations should be waiting status");
        }
    }

    /**
     * Tests the assignNextWaitingUser method to ensure it correctly assigns the oldest waiting user.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testAssignNextWaitingUser() throws SQLException {
        // Get initial waitlist
        ArrayList<Reservation> initialWaitlist = reservationService.getWaitlist();
        int initialWaitlistSize = initialWaitlist.size();
        assertTrue(initialWaitlistSize > 0, "Initial waitlist should not be empty");

        // Get the oldest reservation (first in line)
        Reservation oldestReservation = initialWaitlist.get(0);
        int oldestUserId = oldestReservation.getUser_FK();
        int oldestReservationId = oldestReservation.getId();

        // Assign next waiting user
        Reservation assignedReservation = reservationService.assignNextWaitingUser();

        assertNotNull(assignedReservation, "Should return the assigned reservation");
        assertEquals(oldestReservationId, assignedReservation.getId(), "Should assign the oldest reservation");
        assertEquals(oldestUserId, assignedReservation.getUser_FK(), "Should assign the oldest user");
        assertEquals(2, assignedReservation.getStatus_FK(), "Status should be changed to confirmed (2)");

        // Verify the reservation was updated in the database
        Reservation updatedReservation = reservationService.getReservationById(oldestReservationId);
        assertEquals(2, updatedReservation.getStatus_FK(), "Status should be confirmed in database");

        // Verify waitlist is now shorter
        ArrayList<Reservation> newWaitlist = reservationService.getWaitlist();
        assertEquals(initialWaitlistSize - 1, newWaitlist.size(), "Waitlist should be one reservation shorter");

        // Verify the assigned reservation is no longer in the waitlist
        for (Reservation reservation : newWaitlist) {
            assertNotEquals(oldestReservationId, reservation.getId(), "Assigned reservation should not be in waitlist");
        }
    }

    /**
     * Tests the assignNextWaitingUserByFlight method for a specific flight.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testAssignNextWaitingUserByFlight() throws SQLException {
        // Get initial waitlist for flight 1
        ArrayList<Reservation> initialWaitlist = reservationService.getWaitlistByFlight(1);
        int initialWaitlistSize = initialWaitlist.size();
        assertTrue(initialWaitlistSize > 0, "Initial waitlist for flight 1 should not be empty");

        // Get the oldest reservation for flight 1
        Reservation oldestReservation = initialWaitlist.get(0);
        int oldestUserId = oldestReservation.getUser_FK();
        int oldestReservationId = oldestReservation.getId();

        // Assign next waiting user for flight 1
        Reservation assignedReservation = reservationService.assignNextWaitingUserByFlight(1);

        assertNotNull(assignedReservation, "Should return the assigned reservation");
        assertEquals(oldestReservationId, assignedReservation.getId(), "Should assign the oldest reservation for flight 1");
        assertEquals(oldestUserId, assignedReservation.getUser_FK(), "Should assign the oldest user for flight 1");
        assertEquals(1, assignedReservation.getFlight_FK(), "Should be for flight 1");
        assertEquals(2, assignedReservation.getStatus_FK(), "Status should be changed to confirmed (2)");

        // Verify waitlist for flight 1 is now shorter
        ArrayList<Reservation> newWaitlist = reservationService.getWaitlistByFlight(1);
        assertEquals(initialWaitlistSize - 1, newWaitlist.size(), "Waitlist for flight 1 should be one reservation shorter");

        // Verify waitlist for flight 2 is unchanged
        ArrayList<Reservation> waitlistFlight2 = reservationService.getWaitlistByFlight(2);
        assertTrue(waitlistFlight2.size() >= 1, "Waitlist for flight 2 should be unchanged");
    }

    /**
     * Tests the assignNextWaitingUser method with an empty waitlist.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testAssignNextWaitingUserEmptyWaitlist() throws SQLException {
        // Remove all waiting reservations to simulate empty waitlist
        ArrayList<Reservation> allWaitingReservations = reservationService.getWaitlist();
        for (Reservation reservation : allWaitingReservations) {
            reservationService.deleteReservation(reservation.getId());
        }

        // Verify waitlist is empty
        ArrayList<Reservation> emptyWaitlist = reservationService.getWaitlist();
        assertTrue(emptyWaitlist.isEmpty(), "Waitlist should be empty");

        // Try to assign next waiting user
        Reservation assignedReservation = reservationService.assignNextWaitingUser();

        assertNull(assignedReservation, "Should return null when waitlist is empty");

        // Verify waitlist is still empty
        ArrayList<Reservation> stillEmptyWaitlist = reservationService.getWaitlist();
        assertTrue(stillEmptyWaitlist.isEmpty(), "Waitlist should still be empty");
    }

    /**
     * Tests the assignNextWaitingUserByFlight method with an empty waitlist for a specific flight.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testAssignNextWaitingUserByFlightEmptyWaitlist() throws SQLException {
        // Use a flight ID that has no waiting reservations
        int nonExistentFlightId = 999;

        // Try to assign next waiting user for non-existent flight
        Reservation assignedReservation = reservationService.assignNextWaitingUserByFlight(nonExistentFlightId);

        assertNull(assignedReservation, "Should return null when no users are waiting for the flight");

        // Verify general waitlist is unchanged
        ArrayList<Reservation> generalWaitlist = reservationService.getWaitlist();
        assertFalse(generalWaitlist.isEmpty(), "General waitlist should still have reservations");
    }

    /**
     * Tests creating a new reservation through the service.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testCreateReservation() throws SQLException {
        Reservation newReservation = new Reservation();
        newReservation.setUser_FK(5);
        newReservation.setStatus_FK(1); // Waiting status
        newReservation.setFlight_FK(1);
        newReservation.setReserved_at(LocalDateTime.now());

        reservationService.createReservation(newReservation);

        // Verify the reservation was created and appears in waitlist
        ArrayList<Reservation> waitlist = reservationService.getWaitlist();
        boolean found = false;
        int newReservationId = -1;

        for (Reservation reservation : waitlist) {
            if (reservation.getUser_FK() == 5 && reservation.getFlight_FK() == 1) {
                found = true;
                newReservationId = reservation.getId();
                break;
            }
        }

        assertTrue(found, "New reservation should appear in waitlist");

        // Clean up
        if (newReservationId != -1) {
            reservationService.deleteReservation(newReservationId);
        }
    }

    /**
     * Tests the getAllReservations method.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testGetAllReservations() throws SQLException {
        ArrayList<Reservation> allReservations = reservationService.getAllReservations();

        assertNotNull(allReservations);
        assertFalse(allReservations.isEmpty(), "Should have reservations");

        // Verify test reservations are included
        int foundTestReservations = 0;
        for (Reservation reservation : allReservations) {
            if (testReservationIds.contains(reservation.getId())) {
                foundTestReservations++;
            }
        }

        assertEquals(testReservationIds.size(), foundTestReservations, "All test reservations should be found");
    }

    /**
     * Tests getting reservations by user ID.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testGetReservationsByUser() throws SQLException {
        ArrayList<Reservation> userReservations = reservationService.getReservationsByUser(1);

        assertNotNull(userReservations);
        assertFalse(userReservations.isEmpty(), "User 1 should have reservations");

        for (Reservation reservation : userReservations) {
            assertEquals(1, reservation.getUser_FK(), "All reservations should belong to user 1");
        }
    }

    /**
     * Tests getting reservations by flight ID.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testGetReservationsByFlight() throws SQLException {
        ArrayList<Reservation> flightReservations = reservationService.getReservationsByFlight(1);

        assertNotNull(flightReservations);
        assertFalse(flightReservations.isEmpty(), "Flight 1 should have reservations");

        for (Reservation reservation : flightReservations) {
            assertEquals(1, reservation.getFlight_FK(), "All reservations should be for flight 1");
        }
    }
}