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
    public void ableValueForClass(String seatClassStr) throws SQLException {
        try {
            Seat.SeatClass seatClass = Seat.SeatClass.valueOf(seatClassStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("No a valid seat class: " + seatClassStr);
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

   

    private ArrayList<String> availableSeatsToString(ArrayList<Seat> seats) {
        try{
            ArrayList<String> availableSeats = new ArrayList<>();
            for (Seat seat : seats) {
                availableSeats.add(seat.getSeat_number());
            }
            return availableSeats;
        }
        catch(Exception e){
            throw new IllegalArgumentException("No available seats");

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
     * @return The Seat object if found, or an empty Seat object if not found
     * @throws SQLException if there's an error executing the database query
     */
    public Seat getSeatById(int seatId) throws SQLException {
        return seatDAO.getById(seatId);
    }
}
