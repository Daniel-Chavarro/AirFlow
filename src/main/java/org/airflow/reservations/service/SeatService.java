package org.airflow.reservations.service;

import org.airflow.reservations.DAO.SeatDAO;
import org.airflow.reservations.model.Seat;

import java.sql.SQLException;
import java.util.ArrayList;

public class SeatService {
    private SeatDAO SeatDAO;

    public SeatService() throws Exception {
        this.SeatDAO = new SeatDAO();
    }
    public SeatService(SeatDAO flightDAO) {
        this.SeatDAO = flightDAO;
    }

    /**
     * Function to check if a seat class is valid.
     * @param seatClassStr : the string representation of the seat class.
     * @throws SQLException : if a database access error occurs.
     */
    public void AbleValueForClass(String seatClassStr) throws SQLException {
        try {
            Seat.SeatClass seatClass = Seat.SeatClass.valueOf(seatClassStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Clase de asiento inválida: " + seatClassStr);
        }
    }

    /**
     * Function to get all the available seats for a given airplane, class and window.
     * @param AirplaneId : the id of the airplane.
     * @param seatClass : the class of the seats to be returned.
     * @param Window : true if the seats are for the window, false if the seats are for the aisle.
     * @return ArrayList<String> with the codes of the available seats.
     * @throws SQLException
     */
    public ArrayList<String> getSeatsByAirplaneIdClassWindow(int AirplaneId ,String seatClass, boolean Window) throws SQLException {
        AbleValueForClass(seatClass);
        ArrayList<Seat> seats = SeatDAO.getByavailableSeatsByAirplaneIdClassAndWindow(AirplaneId,seatClass,Window);
        return availableSeatsToString(seats);
    }

    /**
     * Function to return all the codes of the available seats.
     * @param seats : ArrayList<Seat> with the seats to be checked.
     * @return ArrayList<String> with the codes of the available seats.
     */

    private ArrayList<String> availableSeatsToString(ArrayList<Seat> seats) {
        try{
            ArrayList<String> availableSeats = new ArrayList<>();
            for (Seat seat : seats) {
                availableSeats.add(seat.getSeat_number());
            }
            return availableSeats;
        }
        catch(Exception e){
            throw new IllegalArgumentException("No hay asientos disponibles");
        }

    }

    /**
     * Function to update the status of a seat.
     * @param seatId : the id of the seat to be updated.
     * @param Status : the new status of the seat.
     * @throws SQLException : if a database access error occurs.
     */

    public void updateSeatStatus(int seatId , int Status) throws SQLException {
        Seat seat = SeatDAO.getById(seatId);
        try{
            if(Status == 0){
                seat.setReservation_FK(null);
                return;
            }
            seat.setReservation_FK(Status);
        }
        catch(Exception e){
            throw new IllegalArgumentException("El asiento no existe");
        }
        SeatDAO.update(seatId,seat);
    }
}
