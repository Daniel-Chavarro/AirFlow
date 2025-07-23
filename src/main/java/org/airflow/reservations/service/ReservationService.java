package org.airflow.reservations.service;

import org.airflow.reservations.DAO.*;
import org.airflow.reservations.model.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

/**
 * Service class for managing reservation-related operations.
 * This class handles the business logic for creating, updating, and managing
 * flight reservations, including seat assignments and validation.
 */
public class ReservationService {
    /** Data Access Object for reservation operations */
    private final ReservationDAO reservationDAO;
    /** Data Access Object for flight operations */
    private final FlightDAO flightDAO;
    /** Data Access Object for seat operations */
    private final SeatDAO SeatDAO;
    /** Data Access Object for city operations */
    private final CityDAO cityDAO;
    /** The current user making reservations */
    private final User User;
    /** Service for seat-related operations */
    private final SeatService seatService;

    /**
     * Default constructor that initializes the ReservationService with necessary DAOs.
     *
     * @param User The user who will be making reservations
     * @throws SQLException if there's an error connecting to the database
     */
    public ReservationService(User User) throws SQLException {
        this.flightDAO = new FlightDAO();
        this.reservationDAO = new ReservationDAO();
        this.SeatDAO = new SeatDAO();
        this.cityDAO = new CityDAO();
        this.seatService = new SeatService(this.SeatDAO);
        this.User = User;
    }

    /**
     * Constructor for ReservationService with dependency injection.
     * Allows injecting specific DAO instances, useful for testing.
     *
     * @param User The user who will be making reservations
     * @param reservationDAO The ReservationDAO instance to use
     * @param flightDAO The FlightDAO instance to use
     * @param SeatDAO The SeatDAO instance to use
     * @param cityDAO The CityDAO instance to use
     * @param seatService The SeatService instance to use
     */
    public ReservationService(User User, ReservationDAO reservationDAO, FlightDAO flightDAO, SeatDAO SeatDAO, CityDAO cityDAO, SeatService seatService) {
        this.reservationDAO = reservationDAO;
        this.flightDAO = flightDAO;
        this.SeatDAO = SeatDAO;
        this.cityDAO = cityDAO;
        this.seatService = seatService;
        this.User = User;
    }

    /**
     * Function to check if the seat and flight given are able for reservation.
     * Validates that the flight exists, seat is available, departure time allows reservation,
     * and flight status permits bookings.
     *
     * @param selectedFlight The flight ID to be reserved
     * @param selectedSeat The seat ID to be reserved
     * @return true if the reservation can be created, false otherwise
     * @throws SQLException if a database access error occurs
     * @throws IllegalArgumentException if the flight or seat doesn't exist
     */

    private boolean ableForReservation(int selectedFlight, int selectedSeat) throws SQLException{
        Flight flight= flightDAO.getById(selectedFlight);
        if (flight.getId() == 0) throw new IllegalArgumentException("El vuelo no existe");
        Seat seat = SeatDAO.getById(selectedSeat);
        if (seat.getId() == 0) throw new IllegalArgumentException("El asiento no existe");
        long differenceHours = ChronoUnit.HOURS.between(LocalDateTime.now(),flight.getDeparture_time());
        if (differenceHours < 3) return false;
        if (flight.getStatus_FK() == 7 || flight.getStatus_FK() == 3) return false;
        if ( seat.getAirplane_FK() != flight.getAirplane_FK()) return false;
        return seat.getReservation_FK() == null;
    }

    /**
     * Function to create a reservation.
     *
     * @param selectedFlightID : the flight Id to be reserved.
     * @param selectedSeatIDs  : the seats Ids to be reserved.
     * @return reservation created
     * @throws SQLException             : if a database access error occurs.
     * @throws IllegalArgumentException if no seats selected or any seat are not able for reserve.
     */

    public Reservation createReservation(int selectedFlightID, int[] selectedSeatIDs) throws SQLException{
        //try {
            if (selectedFlightID == 0) throw new IllegalArgumentException("No hay vuelo seleccionado");
            if (selectedSeatIDs.length == 0) throw new IllegalArgumentException("No hay asientos seleccionados");
            for (int seatId : selectedSeatIDs) {
                if (!ableForReservation(selectedFlightID,seatId)) {
                    throw new IllegalArgumentException("No se puede reservar este asiento  " + seatId);
                }
            }
            Reservation reservation = new Reservation();
            reservation.setFlight_FK(selectedFlightID);
            reservation.setUser_FK(User.getId());
            reservation.setStatus_FK(3);
            reservationDAO.create(reservation);
            ArrayList<Reservation> reservations = reservationDAO.getByFlightIdAndUserId(selectedFlightID,User.getId());
            reservation = reservations.get(reservations.size()-1);
            int reservationID = reservation.getId();
            for (int seatId : selectedSeatIDs) {
                seatService.updateSeatStatus(seatId,reservationID);
            }
            return reservation;
        //}
        //catch (Exception e){
            //if (e instanceof IllegalArgumentException){throw e;}
          //  else{throw new IllegalArgumentException("Datos no válidos");}
        //}
    }

    /**
     * Function to find reservations by UserId
     * @return ArrayList of Ids of reservations found
     * @throws SQLException If database access error occurs
     * @throws IllegalArgumentException : if the reservation does not exist
     */

    public ArrayList<Reservation> FindReservation_byUserId() throws SQLException{
        return reservationDAO.getByUserId(User.getId());
    }

    /**
     * Function to find reservations by flight id
     * @param flightId : Id of the reservation flight
     * @return ArrayList of Ids of reservations found
     * @throws SQLException If database access error occurs
     */

    public ArrayList<Reservation> FindReservation_byUserAndFlight(int flightId) throws SQLException{
        return reservationDAO.getByFlightIdAndUserId(User.getId(),flightId);
    }

    /**
     * Finds a reservation by its unique identifier.
     *
     * @param reservationId The unique identifier of the reservation to find
     * @return The Reservation object if found, or an empty Reservation object if not found
     * @throws SQLException if a database access error occurs
     */
    public Reservation FindByReservationId(int reservationId) throws SQLException {
        return reservationDAO.getById(reservationId);
    }

    /**
     * Function to check if a reservation can be canceled.
     * Validates that the reservation exists and that there are at least 12 hours before departure.
     *
     * @param selectedReservation The reservation ID to be canceled
     * @return true if the reservation can be canceled, false otherwise
     * @throws SQLException if a database access error occurs
     * @throws IllegalArgumentException if the reservation doesn't exist
     */
    private boolean ableForCancelation(int selectedReservation) throws SQLException{

        Reservation reservation = reservationDAO.getById(selectedReservation);
        if (reservation.getId() == 0) throw new IllegalArgumentException("La reserva no existe");
        Flight flight= flightDAO.getById(reservation.getFlight_FK());
        if(flight.getId() == 0) throw new IllegalArgumentException("El vuelo no existe");
        long differenceHours = ChronoUnit.HOURS.between(LocalDateTime.now(),flight.getDeparture_time());
        return differenceHours > 12;

    }

    /**
     * Function to cancel a reservation and all the seats reserved.
     * @param selectedReservation : the reservation to be canceled.
     * @throws SQLException : if a database access error occurs.
     * @throws IllegalArgumentException : if the reservation do not exist or if the reservation can not be canceled.
     */

    public void cancelReservation(int selectedReservation) throws SQLException{
        try {
            if (ableForCancelation(selectedReservation)) {
                Reservation reservation = reservationDAO.getById(selectedReservation);
                ArrayList<Seat> seat = SeatDAO.getByReservationId(selectedReservation);
                for (Seat s : seat) {
                    seatService.updateSeatStatus(s.getId(), 0);
                }
                reservationDAO.delete(selectedReservation);
            } else {
                throw new IllegalArgumentException("No se puede cancelar la reserva porque la hora de salida es menor a 12 horas");
            }
        }
        catch (Exception e){
            if (e instanceof IllegalArgumentException){throw e;}
            else{throw new IllegalArgumentException("Datos no válidos");}
        }
    }

    /**
     * Function to delete a reservation and a selected set of seats.
     * @param selectedReservation : the reservation to be canceled.
     * @param seatsIdtoCancel : the seats to be deleted.
     * @throws SQLException : if a database access error occurs.
     * @throws IllegalArgumentException : if the reservation does not exist
     */

    public void deleteSeatsfromReservation (int selectedReservation, ArrayList<Integer> seatsIdtoCancel) throws SQLException{
        try{
            if (reservationDAO.getById(selectedReservation) == null) throw new IllegalArgumentException("La reserva no existe");
            if (ableForCancelation(selectedReservation)) {
                ArrayList<Seat> seats = SeatDAO.getByReservationId(selectedReservation);
                if (seatsIdtoCancel.isEmpty()) {
                    throw new IllegalArgumentException("No hay asientos seleccionados");
                }

                for (Seat s : seats) {
                    if (seatsIdtoCancel.contains(s.getId())) {
                        seatService.updateSeatStatus(s.getId(), 0);
                        seatsIdtoCancel.remove(Integer.valueOf(s.getId()));
                    }
                }
                if (!seatsIdtoCancel.isEmpty()) {
                    throw new IllegalArgumentException("Hay asientos que no pertenecen a la reserva");
                }
                if (SeatDAO.getByReservationId(selectedReservation).isEmpty()) {
                    cancelReservation(selectedReservation);
                }
            }
            else{
                throw new IllegalArgumentException("Error al cancelar asientos");
            }
        }
        catch(Exception e){
            if (e instanceof IllegalArgumentException){throw e;}
            else{throw new IllegalArgumentException("Datos no válidos");}
        }

    }

    /**
     * Function to confirm a reservation.
     * @param reservationId : the reservation to be confirmed.
     * @throws SQLException : if a database access error occurs.
     * @throws IllegalArgumentException : if the reservation do not exist
     */

    public void confirmReservation(int reservationId)throws SQLException{
        try{
            if (reservationDAO.getById(reservationId) == null) throw new IllegalArgumentException("La reserva no existe");

            if (reservationDAO.getById(reservationId).getStatus_FK() == 3) {
                Reservation reservation = reservationDAO.getById(reservationId);
                reservation.setStatus_FK(1);
                reservationDAO.update(reservationId, reservation);
            }
            else{
                throw new IllegalArgumentException("No es posible confirmar la reserva");
            }
        }
        catch(Exception e){
            if (e instanceof IllegalArgumentException){throw e;}
            else{throw new IllegalArgumentException("Datos no válidos");}
        }
    }

    /**
     * Function to check if a reservation can be checked in.
     * @param selectedReservation : the reservation to be checked in.
     * @return boolean: true if the reservation can be checked in, false otherwise.
     * @throws SQLException : if a database access error occurs.
     * @throws IllegalArgumentException : if the reservation do not exist or if the reservation is not confirmed.
     */

    private boolean ableForCheckIn(int selectedReservation) throws SQLException{
        Reservation reservation = reservationDAO.getById(selectedReservation);
        if (reservation.getId() == 0) throw new IllegalArgumentException("La reserva no existe");
        Flight flight= flightDAO.getById(reservation.getFlight_FK());
        if(flight.getId() == 0) throw new IllegalArgumentException("El vuelo no existe");
        long differenceHours = ChronoUnit.HOURS.between(LocalDateTime.now(),flight.getDeparture_time());
        return differenceHours > 2 && reservation.getStatus_FK() == 1;
    }

    /**
     * Function to check in a reservation.
     * @param ReservationId : the reservation to be checked in.
     * @throws SQLException : if a database access error occurs.
     * @throws IllegalArgumentException : if the reservation do not exist or if the reservation is not confirmed.
     */

    public void check_inReservation(int ReservationId)throws SQLException{
        try {
            if (ableForCheckIn(ReservationId)) {
                Reservation reservation = reservationDAO.getById(ReservationId);
                if (reservation.getStatus_FK() == 2) {
                    throw new IllegalArgumentException("El vuelo ya ha partido");
                }

                reservation.setStatus_FK(4);
                reservationDAO.update(ReservationId, reservation);
            }
            else{
                throw new IllegalArgumentException("No se puede confirmar el check in porque la hora de salida es " +
                        "menor a 2 horas o la reserva no ha sido confirmada");
            }
        }
        catch(Exception e){
            if (e instanceof IllegalArgumentException){throw e;}
            else{throw new IllegalArgumentException("Datos no válidos");}
        }
    }

    /**
     * Function to complete all the reservations for a given flight that must have arrived to the destiny
     * @param FlightId : the id of the flight to be checked.
     * @throws SQLException : if a database access error occurs.
     * @throws IllegalArgumentException : if the flight do not exist or if the flight is not completed.
     */
    public void completed_reservations(int FlightId)throws SQLException{
        try{
            if (flightDAO.getById(FlightId) == null) throw new IllegalArgumentException("El vuelo no existe");
            if (flightDAO.getById(FlightId).getStatus_FK() == 7) {
                ArrayList<Reservation> reservations = reservationDAO.getByFlightId(FlightId);

                for (Reservation reservation : reservations) {
                    if (reservation.getStatus_FK() == 4) {
                        reservation.setStatus_FK(5);
                        reservationDAO.update(reservation.getId(), reservation);
                    }
                }
            }
        }
        catch(Exception e){
            if (e instanceof IllegalArgumentException){throw e;}
            else{throw new IllegalArgumentException("Datos no válidos");}
        }


    }

    /**
     * Function to check if a reservation can be canceled if the user are not checked in
     * @param reservationId : the reservation to be canceled.
     * @return boolean: true if the reservation can be canceled, false otherwise.
     * @throws SQLException : if a database access error occurs.
     * @throws IllegalArgumentException : if the reservation do not exist.
     */
    private boolean ableToCanelAutomatically(int reservationId)throws SQLException{
        Reservation reservation = reservationDAO.getById(reservationId);
        if (reservation.getId() == 0) throw new IllegalArgumentException("La reserva no existe");
        return reservation.getStatus_FK() != 4 && reservation.getStatus_FK() != 5;
    }

    /**
     * Automatically cancels a reservation when flight departure conditions are met.
     * This method should be called by system processes to handle automatic cancellations.
     *
     * @param ReservationId The reservation ID to be canceled
     * @throws SQLException if a database access error occurs
     * @throws IllegalArgumentException if the reservation doesn't exist
     */
    private void cancel_reservation(int ReservationId) throws SQLException{

        if (ableToCanelAutomatically(ReservationId)){
            Reservation reservation = reservationDAO.getById(ReservationId);
            reservation.setStatus_FK(2);
            reservationDAO.update(ReservationId,reservation);
        }

}

    /**
     * System control method to automatically cancel reservations for flights near departure.
     * This method should be run by a system scheduler every 15 minutes to maintain
     * reservation integrity for flights departing soon.
     *
     * @throws SQLException if a database access error occurs
     * @throws IllegalArgumentException if there are validation errors
     */
    public void reservations_control()throws SQLException{
    try {
        LocalDateTime topRange = LocalDateTime.now().plusHours(2);
        LocalDateTime bottomRange = LocalDateTime.now().minusMinutes(15);
        ArrayList<Flight> flights_near_departure = flightDAO.getByDepartureTimeRange(bottomRange,topRange);
        for (Flight flight : flights_near_departure) {
            ArrayList<Reservation> reservations = reservationDAO.getByFlightId(flight.getId());
            for (Reservation reservation : reservations) {
                cancel_reservation(reservation.getId());
            }
        }
    }
    catch (Exception e){
        if (e instanceof IllegalArgumentException){throw e;}
        else{throw new IllegalArgumentException("Datos no válidos");}
    }

}
}
