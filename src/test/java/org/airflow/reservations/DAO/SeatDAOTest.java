package org.airflow.reservations.DAO;

import org.airflow.reservations.model.Seat;
import org.airflow.reservations.model.SeatClass;
import org.airflow.reservations.utils.ConnectionDB;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the SeatDAO class.
 * Tests CRUD operations and specialized queries for Seat entities.
 */
public class SeatDAOTest {
    private Connection connection;
    private SeatDAO seatDAO;
    private static int testSeatId;

    /**
     * Sets up the test environment before each test.
     * Establishes a database connection and creates test data.
     *
     * @throws SQLException if a database error occurs
     */
    @BeforeEach
    void setUp() throws SQLException {
        connection = ConnectionDB.getConnection();
        seatDAO = new SeatDAO(connection);
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
     * Creates test seat data in the database for testing purposes.
     *
     * @throws SQLException if a database error occurs
     */
    private void createTestData() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String insertSeat = "INSERT INTO seats (airplane_FK, reservation_FK, seat_number, seat_class, is_window) VALUES " +
                    "(1, NULL, 'A1', 'ECONOMY', true)";

            statement.executeUpdate(insertSeat, Statement.RETURN_GENERATED_KEYS);
            var rs = statement.getGeneratedKeys();
            if (rs.next()) {
                testSeatId = rs.getInt(1);
            }
        }
    }

    /**
     * Removes test seat data from the database after tests are complete.
     *
     * @throws SQLException if a database error occurs
     */
    private void cleanupTestData() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM seats WHERE seat_number = 'A1' AND airplane_FK = 1");
        }
    }

    /**
     * Tests the getAll method to ensure it retrieves all seats from the database.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testGetAll() throws SQLException {
        ArrayList<Seat> seats = seatDAO.getAll();

        assertNotNull(seats);
        assertFalse(seats.isEmpty(), "Seat list should not be empty");

        boolean found = false;
        for (Seat seat : seats) {
            if (seat.getSeat_number().equals("A1") && seat.getAirplane_FK() == 1) {
                found = true;
                break;
            }
        }

        assertTrue(found, "Test seat should be in the list");
    }

    /**
     * Tests the getById method to ensure it retrieves a specific seat by ID.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testGetById() throws SQLException {
        Seat seat = seatDAO.getById(testSeatId);

        assertNotNull(seat);
        assertEquals("A1", seat.getSeat_number());
        assertEquals(1, seat.getAirplane_FK());
        assertEquals(SeatClass.ECONOMY, seat.getSeat_class());
        assertTrue(seat.getIs_window());
    }

    /**
     * Tests the create method to ensure it properly inserts a new seat into the database.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testCreate() throws SQLException {
        Seat newSeat = new Seat();
        newSeat.setAirplane_FK(1);
        newSeat.setReservation_FK(null);
        newSeat.setSeat_number("B2");
        newSeat.setSeat_class(SeatClass.BUSINESS);
        newSeat.setIs_window(false);

        seatDAO.create(newSeat);

        ArrayList<Seat> seats = seatDAO.getAll();
        boolean found = false;
        for (Seat seat : seats) {
            if (seat.getSeat_number().equals("B2") && seat.getAirplane_FK() == 1) {
                found = true;
                seatDAO.delete(seat.getId());
                break;
            }
        }

        assertTrue(found, "New seat should have been created");
    }

    /**
     * Tests the update method to ensure it properly updates an existing seat in the database.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testUpdate() throws SQLException {
        Seat seat = seatDAO.getById(testSeatId);

        seat.setSeat_class(SeatClass.FIRST);
        seat.setIs_window(false);

        seatDAO.update(testSeatId, seat);

        Seat updatedSeat = seatDAO.getById(testSeatId);
        assertEquals(SeatClass.FIRST, updatedSeat.getSeat_class());
        assertFalse(updatedSeat.getIs_window());
        assertEquals("A1", updatedSeat.getSeat_number());
    }

    /**
     * Tests the delete method to ensure it properly removes a seat from the database.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testDelete() throws SQLException {
        Seat seatToDelete = new Seat();
        seatToDelete.setAirplane_FK(1);
        seatToDelete.setReservation_FK(null);
        seatToDelete.setSeat_number("C3");
        seatToDelete.setSeat_class(SeatClass.ECONOMY);
        seatToDelete.setIs_window(true);

        seatDAO.create(seatToDelete);

        ArrayList<Seat> seats = seatDAO.getAll();
        int deleteId = -1;
        for (Seat s : seats) {
            if (s.getSeat_number().equals("C3") && s.getAirplane_FK() == 1) {
                deleteId = s.getId();
                break;
            }
        }

        seatDAO.delete(deleteId);

        seats = seatDAO.getAll();
        boolean found = false;
        for (Seat s : seats) {
            if (s.getSeat_number().equals("C3") && s.getAirplane_FK() == 1) {
                found = true;
                break;
            }
        }

        assertFalse(found, "Seat should have been deleted");
    }

    /**
     * Tests the getSeatsByAirplane method to ensure it retrieves seats by airplane ID.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testGetSeatsByAirplane() throws SQLException {
        ArrayList<Seat> seats = seatDAO.getByAirplaneId(1);

        assertNotNull(seats);
        assertFalse(seats.isEmpty(), "There should be seats for airplane 1");

        for (Seat seat : seats) {
            assertEquals(1, seat.getAirplane_FK());
        }
    }

    /**
     * Tests the getAvailableSeatsByFlight method to ensure it retrieves available seats for a flight.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testGetAvailableSeatsByFlight() throws SQLException {
        ArrayList<Seat> seats = seatDAO.getByReservationId(1);


        for (Seat seat : seats) {
            assertNotNull(seat.getReservation_FK(), "Available seats should not have null reservation_FK");
        }
    }

    /**
     * Tests the getAvailableSeatsByAirplaneId method to ensure it retrieves available seats for a specific airplane.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testGetAvailableSeatsByAirplaneId() throws SQLException {
        ArrayList<Seat> availableSeats = seatDAO.getAvailableSeatsByAirplaneId(1);

        assertNotNull(availableSeats);
        for (Seat seat : availableSeats) {
            assertEquals(1, seat.getAirplane_FK());
            assertNull(seat.getReservation_FK(), "Available seats should have null reservation_FK");
        }
    }

    /**
     * Tests the getAvailableSeatsBySeatClass method to ensure it retrieves available seats for a specific seat class.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testGetAvailableSeatsBySeatClass() throws SQLException {
        ArrayList<Seat> economySeats = seatDAO.getAvailableSeatsBySeatClass(SeatClass.ECONOMY);

        assertNotNull(economySeats);
        for (Seat seat : economySeats) {
            assertEquals(SeatClass.ECONOMY, seat.getSeat_class());
            assertNull(seat.getReservation_FK(), "Available seats should have null reservation_FK");
        }
    }

    /**
     * Tests the getAvailableSeatsBySeatClass method with another seat class to ensure it works for different classes.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testGetAvailableSeatsBySeatClassBusiness() throws SQLException {
        ArrayList<Seat> businessSeats = seatDAO.getAvailableSeatsBySeatClass(SeatClass.BUSINESS);

        assertNotNull(businessSeats);
        for (Seat seat : businessSeats) {
            assertEquals(SeatClass.BUSINESS, seat.getSeat_class());
            assertNull(seat.getReservation_FK(), "Available seats should have null reservation_FK");
        }
    }
}
