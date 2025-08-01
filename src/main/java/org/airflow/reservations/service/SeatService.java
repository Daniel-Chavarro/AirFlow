package org.airflow.reservations.service;

import org.airflow.reservations.DAO.SeatDAO;
import org.airflow.reservations.model.Seat;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Service class for managing seat-related operations.
 * This class provides business logic for seat management including
 * seat retrieval, status updates, and reservation handling.
 */
public class SeatService {
    /** Data Access Object for seat operations */
    private final SeatDAO seatDAO;

    /**
     * Default constructor that initializes the SeatService with a new SeatDAO.
     *
     * @throws SQLException if there's an error connecting to the database
     */
    public SeatService() throws SQLException {
        this.seatDAO = new SeatDAO();
    }

    /**
     * Constructor for SeatService with dependency injection.
     * Allows injecting a specific SeatDAO instance, useful for testing.
     *
     * @param seatDAO the SeatDAO instance to use
     */
    public SeatService(SeatDAO seatDAO) {
        this.seatDAO = seatDAO;
    }

    /**
     * Validates if the provided string represents a valid seat class.
     *
     * @param seatClassStr the seat class string to validate
     * @throws IllegalArgumentException if the seat class string is not valid
     */
    public void validateSeatClass(String seatClassStr) {
        try {
            Seat.SeatClass.valueOf(seatClassStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Not a valid seat class: " + seatClassStr);
        }
    }

    /**
     * Retrieves all seats associated with a specific airplane.
     *
     * @param airplaneId The unique identifier of the airplane
     * @return ArrayList containing all Seat objects for the specified airplane
     * @throws SQLException if there's an error executing the database query
     */
    public ArrayList<Seat> getSeatsByAirplaneId(int airplaneId) throws SQLException {
        return seatDAO.getByAirplaneId(airplaneId);
    }

    /**
     * Converts a list of Seat objects to a list of seat number strings.
     * This method is used internally to extract seat numbers from seat objects.
     *
     * @param seats the list of Seat objects to convert
     * @return ArrayList containing seat numbers as strings
     * @throws IllegalArgumentException if no seats are available or an error occurs during conversion
     */
    public ArrayList<String> availableSeatsToString(ArrayList<Seat> seats) {
        try {
            ArrayList<String> availableSeats = new ArrayList<>();
            for (Seat seat : seats) {
                if (seat.getReservation_FK() == null) { // Only include available seats
                    availableSeats.add(seat.getSeat_number());
                }
            }
            return availableSeats;
        } catch (Exception e) {
            throw new IllegalArgumentException("Error processing available seats: " + e.getMessage());
        }
    }

    /**
     * Updates the reservation status of a specific seat.
     * Associates a seat with a reservation or clears the association.
     *
     * @param seatId The unique identifier of the seat
     * @param reservationId The reservation ID to associate with the seat, or null to clear
     * @throws SQLException if there's an error executing the database query
     * @throws IllegalArgumentException if the seat with the given ID is not found
     */    
    public void updateSeatStatus(int seatId, Integer reservationId) throws SQLException {
        Seat seat = seatDAO.getById(seatId);
        if (seat == null) {
            throw new IllegalArgumentException("Seat not found: " + seatId);
        }
        seat.setReservation_FK(reservationId);
        seatDAO.update(seatId, seat);
    }

    /**
     * Retrieves all seats associated with a specific reservation.
     *
     * @param reservationId The unique identifier of the reservation
     * @return ArrayList containing all Seat objects for the specified reservation
     * @throws SQLException if there's an error executing the database query
     */
    public ArrayList<Seat> getSeatsByReservationId(int reservationId) throws SQLException {
        return seatDAO.getByReservationId(reservationId);
    }

    /**
     * Retrieves a specific seat by its unique identifier.
     *
     * @param seatId The unique identifier of the seat
     * @return The Seat object if found, or null if not found
     * @throws SQLException if there's an error executing the database query
     */
    public Seat getSeatById(int seatId) throws SQLException {
        return seatDAO.getById(seatId);
    }

    /**
     * Retrieves all available seats for a specific airplane.
     * Available seats are those that are not currently reserved.
     *
     * @param airplaneId The unique identifier of the airplane
     * @return ArrayList containing all available Seat objects for the specified airplane
     * @throws SQLException if there's an error executing the database query
     */
    public ArrayList<Seat> getAvailableSeatsByAirplaneId(int airplaneId) throws SQLException {
        ArrayList<Seat> allSeats = getSeatsByAirplaneId(airplaneId);
        ArrayList<Seat> availableSeats = new ArrayList<>();

        for (Seat seat : allSeats) {
            if (seat.getReservation_FK() == null) {
                availableSeats.add(seat);
            }
        }

        return availableSeats;
    }

    /**
     * Checks if a specific seat is available for reservation.
     *
     * @param seatId The unique identifier of the seat
     * @return true if the seat is available, false if it's already reserved
     * @throws SQLException if there's an error executing the database query
     * @throws IllegalArgumentException if the seat with the given ID is not found
     */
    public boolean isSeatAvailable(int seatId) throws SQLException {
        Seat seat = getSeatById(seatId);
        if (seat == null) {
            throw new IllegalArgumentException("Seat not found: " + seatId);
        }
        return seat.getReservation_FK() == null;
    }
}
