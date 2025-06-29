package org.airflow.reservations.service;

import org.airflow.reservations.DAO.*;
import org.airflow.reservations.model.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class ReservationService {
    private ReservationDAO reservationDAO;
    private FlightDAO flightDAO;
    private SeatDAO SeatDAO;
    private CityDAO cityDAO;
    private User User;
    private SeatService seatService;

    public ReservationService(User User) throws Exception {
        this.flightDAO = new FlightDAO();
        this.reservationDAO = new ReservationDAO();
        this.SeatDAO = new SeatDAO();
        this.cityDAO = new CityDAO();
        this.seatService = new SeatService(this.SeatDAO);
        this.User = User;
    }
    public ReservationService(User User, ReservationDAO reservationDAO, FlightDAO flightDAO, SeatDAO SeatDAO, CityDAO cityDAO, SeatService seatService) {
        this.reservationDAO = reservationDAO;
        this.flightDAO = flightDAO;
        this.SeatDAO = SeatDAO;
        this.cityDAO = cityDAO;
        this.seatService = seatService;
        this.User = User;
    }

    /**
     * Function to create a reservation.
     * @param selectedFlight : the flight to be reserved.
     * @param selectedSeat : the seat to be reserved.
     * @throws SQLException : if a database access error occurs.
     */

    public void createReservation(int selectedFlight, int selectedSeat) throws SQLException{
        Reservation reservation = new Reservation();
        reservation.setFlight_FK(selectedFlight);
        reservation.setUser_FK(User.getId());
        reservation.setStatus_FK(3);
        reservationDAO.create(reservation);
        reservation = reservationDAO.getById(reservation.getId());
        seatService.updateSeatStatus(selectedSeat,reservation.getId());
    }

    /**
     * Function to check if a reservation can be canceled.
     * @param selectedReservation : the reservation to be canceled.
     * @return boolean: true if the reservation can be canceled, false otherwise.
     * @throws SQLException : if a database access error occurs.
     * @throws IllegalArgumentException : if the reservation do not exist
     */
    private boolean ableForCancelation(int selectedReservation) throws SQLException{
        try{
            Reservation reservation = reservationDAO.getById(selectedReservation);
            Flight flight= flightDAO.getById(reservation.getFlight_FK());
            long differenceHours = ChronoUnit.HOURS.between(LocalDateTime.now(),flight.getDeparture_time());
            if (differenceHours < 12){
                return true;
            }
            else{
                return false;
            }
        }
        catch(Exception e){
            throw new IllegalArgumentException("La reserva no existe");
        }
    }

    /**
     * Function to cancel a reservation and all the seats reserved.
     * @param selectedReservation : the reservation to be canceled.
     * @throws SQLException : if a database access error occurs.
     * @throws IllegalArgumentException : if the reservation do not exist or if the reservation can not be canceled.
     */

    public void cancelReservation(int selectedReservation) throws SQLException{
        try{
            if (ableForCancelation(selectedReservation)){
                Reservation reservation = reservationDAO.getById(selectedReservation);
                ArrayList<Seat> seat = SeatDAO.getByReservationId(selectedReservation);
                for (Seat s : seat) {
                    seatService.updateSeatStatus(s.getId(),0);
                }
                reservationDAO.delete(selectedReservation);
            }
            else{
                throw new IllegalArgumentException("No se puede cancelar la reserva porque la hora de salida es menor a 12 horas");
            }
        }
        catch(Exception e){
            throw new IllegalArgumentException("La reserva no existe");
        }
    }

    /**
     * Function to delete a reservation and a selected set of seats.
     * @param selectedReservation : the reservation to be canceled.
     * @param seatsIdtoCancel : the seats to be deleted.
     * @throws SQLException : if a database access error occurs.
     * @throws IllegalArgumentException : if the reservation do not exist
     */

    public void deleteSeatsfromReservation (int selectedReservation, ArrayList<Integer> seatsIdtoCancel) throws SQLException{
        try{
            Reservation reservation = reservationDAO.getById(selectedReservation);
            ArrayList<Seat> seat = SeatDAO.getByReservationId(selectedReservation);
            for (Seat s : seat) {
                if (seatsIdtoCancel.contains(s.getId())){
                    seatService.updateSeatStatus(s.getId(),0);
                    seat.remove(s);
                }
            }
            if (seat.isEmpty()){
                reservationDAO.delete(selectedReservation);
            }
        }
        catch(Exception e){
            throw new IllegalArgumentException("La reserva no existe");
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
            Reservation reservation = reservationDAO.getById(reservationId);
            reservation.setStatus_FK(1);
            reservationDAO.update(reservationId,reservation);
        }
        catch(Exception e){
            throw new IllegalArgumentException("La reserva no existe");
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
        try{
            Reservation reservation = reservationDAO.getById(selectedReservation);
            Flight flight= flightDAO.getById(reservation.getFlight_FK());
            long differenceHours = ChronoUnit.HOURS.between(LocalDateTime.now(),flight.getDeparture_time());
            if (differenceHours < 2 && reservation.getStatus_FK()==1){
                return true;
            }
            else{
                return false;
            }
        }
        catch(Exception e){
            throw new IllegalArgumentException("La reserva no existe");
        }
    }

    /**
     * Function to check in a reservation.
     * @param ReservationId : the reservation to be checked in.
     * @throws SQLException : if a database access error occurs.
     * @throws IllegalArgumentException : if the reservation do not exist or if the reservation is not confirmed.
     */

    public void check_inReservation(int ReservationId)throws SQLException{
        try {
            if (!ableForCheckIn(ReservationId)) {
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
            throw new IllegalArgumentException("La reserva no existe");
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
            throw new IllegalArgumentException("El vuelo no existe");
        }


    }

    /**
     * Function to check if a reservation can be canceled if the user are not checked in
     * @param reservationId : the reservation to be canceled.
     * @return boolean: true if the reservation can be canceled, false otherwise.
     * @throws SQLException : if a database access error occurs.
     * @throws IllegalArgumentException : if the reservation do not exist.
     */
    private boolean ableToCanel(int reservationId)throws SQLException{
        try{
            Reservation reservation = reservationDAO.getById(reservationId);
            if (reservation.getStatus_FK() == 4){
               return false;
            }
            else{
                return true;
            }
        }
        catch(Exception e){
            throw new IllegalArgumentException("La reserva no existe");
        }
    }

    /**
     * Function to cancel a reservation, this function should be run by a system boot to cancel all
     * the reservations that are not checked in and the Flight must depart
     * @param ReservationId : the reservation to be canceled.
     * @throws SQLException : if a database access error occurs.
     * @throws IllegalArgumentException : if the reservation do not exist.
     */
    private void cancel_reservation(int ReservationId)throws SQLException{
    try{
        if (ableToCanel(ReservationId)){
            Reservation reservation = reservationDAO.getById(ReservationId);
            reservation.setStatus_FK(2);
            reservationDAO.update(ReservationId,reservation);
        }
    }
    catch(Exception e){
        throw new IllegalArgumentException("La reserva no existe");
    }
}

    /**
     * Function to search the flights that will departure and cancel the reservations that are not checked in.
     * Must be runed by a system boot every 15 minutes.
     * @throws SQLException : if a database access error occurs.
     * @throws IllegalArgumentException : if there are no flights to be checked.
     */

public void reservations_control()throws SQLException{
    try {
        LocalDateTime now = LocalDateTime.now().plusHours(2);
        ArrayList<Flight> flights_near_departure = flightDAO.getByDepartureTime(now);
        for (Flight flight : flights_near_departure) {
            ArrayList<Reservation> reservations = reservationDAO.getByFlightId(flight.getId());
            for (Reservation reservation : reservations) {
                cancel_reservation(reservation.getId());
            }
        }
    }
    catch (Exception e){
        throw new IllegalArgumentException("No hay vuelos para verificar por ahora");
    }

}
}
