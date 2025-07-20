package org.airflow.reservations.service;

import org.airflow.reservations.DAO.ReservationDAO;
import org.airflow.reservations.model.Reservation;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Service class for managing reservations and waitlist operations.
 * Provides methods to handle reservation business logic including waitlist management.
 */
public class ReservationService {
    private final ReservationDAO reservationDAO;

    /**
     * Constructor for ReservationService.
     * Initializes the service with a ReservationDAO instance and handles SQL exceptions.
     *
     * @throws SQLException if an error occurs while connecting to the database
     */
    public ReservationService() throws SQLException {
        this.reservationDAO = new ReservationDAO();
    }

    /**
     * Constructor for ReservationService with dependency injection.
     * Used primarily for testing with mock DAOs.
     *
     * @param reservationDAO the ReservationDAO to be used by the service
     */
    public ReservationService(ReservationDAO reservationDAO) {
        this.reservationDAO = reservationDAO;
    }

    /**
     * Retrieves the waitlist of reservations.
     * Returns all reservations with status_FK = 1 (waiting status) ordered by reserved_at ASC.
     *
     * @return an ArrayList of Reservation objects representing the waitlist, ordered by reservation time
     * @throws SQLException if a database access error occurs
     */
    public ArrayList<Reservation> getWaitlist() throws SQLException {
        return reservationDAO.getWaitlistReservations();
    }

    /**
     * Retrieves the waitlist of reservations for a specific flight.
     * Returns all reservations with status_FK = 1 (waiting status) for the given flight,
     * ordered by reserved_at ASC.
     *
     * @param flightId the ID of the flight
     * @return an ArrayList of Reservation objects representing the waitlist for the flight
     * @throws SQLException if a database access error occurs
     */
    public ArrayList<Reservation> getWaitlistByFlight(int flightId) throws SQLException {
        return reservationDAO.getWaitlistReservationsByFlight(flightId);
    }

    /**
     * Assigns a seat to the next user in the waitlist.
     * Changes the status_FK from 1 (waiting) to 2 (confirmed) for the oldest reservation in the waitlist.
     * If no users are waiting, returns null and no changes are made.
     *
     * @return the Reservation object that was confirmed, or null if the waitlist is empty
     * @throws SQLException if a database access error occurs
     */
    public Reservation assignNextWaitingUser() throws SQLException {
        ArrayList<Reservation> waitlist = getWaitlist();
        
        if (waitlist.isEmpty()) {
            return null; // No users in waitlist
        }

        // Get the first (oldest) reservation in the waitlist
        Reservation oldestReservation = waitlist.get(0);
        
        // Update status to confirmed (status_FK = 2)
        oldestReservation.setStatus_FK(2);
        reservationDAO.update(oldestReservation.getId(), oldestReservation);
        
        return oldestReservation;
    }

    /**
     * Assigns a seat to the next user in the waitlist for a specific flight.
     * Changes the status_FK from 1 (waiting) to 2 (confirmed) for the oldest reservation
     * in the waitlist for the specified flight.
     *
     * @param flightId the ID of the flight
     * @return the Reservation object that was confirmed, or null if no users are waiting for this flight
     * @throws SQLException if a database access error occurs
     */
    public Reservation assignNextWaitingUserByFlight(int flightId) throws SQLException {
        ArrayList<Reservation> waitlist = getWaitlistByFlight(flightId);
        
        if (waitlist.isEmpty()) {
            return null; // No users in waitlist for this flight
        }

        // Get the first (oldest) reservation in the waitlist for this flight
        Reservation oldestReservation = waitlist.get(0);
        
        // Update status to confirmed (status_FK = 2)
        oldestReservation.setStatus_FK(2);
        reservationDAO.update(oldestReservation.getId(), oldestReservation);
        
        return oldestReservation;
    }

    /**
     * Creates a new reservation.
     *
     * @param reservation the Reservation object to be created
     * @throws SQLException if a database access error occurs
     */
    public void createReservation(Reservation reservation) throws SQLException {
        reservationDAO.create(reservation);
    }

    /**
     * Returns all reservations in the system.
     *
     * @return an ArrayList of all Reservation objects
     * @throws SQLException if a database access error occurs
     */
    public ArrayList<Reservation> getAllReservations() throws SQLException {
        return reservationDAO.getAll();
    }

    /**
     * Returns a reservation by its ID.
     *
     * @param id the reservation ID
     * @return the Reservation object with the specified ID
     * @throws SQLException if a database access error occurs
     */
    public Reservation getReservationById(int id) throws SQLException {
        return reservationDAO.getById(id);
    }

    /**
     * Returns reservations for a specific user.
     *
     * @param userId the user ID
     * @return an ArrayList of Reservation objects for the specified user
     * @throws SQLException if a database access error occurs
     */
    public ArrayList<Reservation> getReservationsByUser(int userId) throws SQLException {
        return reservationDAO.getByUserId(userId);
    }

    /**
     * Returns reservations for a specific flight.
     *
     * @param flightId the flight ID
     * @return an ArrayList of Reservation objects for the specified flight
     * @throws SQLException if a database access error occurs
     */
    public ArrayList<Reservation> getReservationsByFlight(int flightId) throws SQLException {
        return reservationDAO.getByFlightId(flightId);
    }

    /**
     * Updates an existing reservation.
     *
     * @param id the ID of the reservation to update
     * @param reservation the new reservation data
     * @throws SQLException if a database access error occurs
     */
    public void updateReservation(int id, Reservation reservation) throws SQLException {
        reservationDAO.update(id, reservation);
    }

    /**
     * Deletes a reservation.
     *
     * @param id the ID of the reservation to delete
     * @throws SQLException if a database access error occurs
     */
    public void deleteReservation(int id) throws SQLException {
        reservationDAO.delete(id);
    }
}