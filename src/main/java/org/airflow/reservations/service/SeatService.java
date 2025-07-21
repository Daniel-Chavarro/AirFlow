package org.airflow.reservations.service;

import org.airflow.reservations.DAO.SeatDAO;
import org.airflow.reservations.model.Seat;

import java.sql.SQLException;
import java.util.ArrayList;

public class SeatService {
    private final SeatDAO seatDAO;

    public SeatService() throws SQLException {
        this.seatDAO = new SeatDAO();
    }

    public SeatService(SeatDAO seatDAO) {
        this.seatDAO = seatDAO;
    }

    public ArrayList<Seat> getSeatsByAirplaneId(int airplaneId) throws SQLException {
        return seatDAO.getByAirplaneId(airplaneId);
    }

    public void updateSeatStatus(int seatId, Integer reservationId) throws SQLException {
        Seat seat = seatDAO.getById(seatId);
        if (seat == null) {
            throw new IllegalArgumentException("Seat not found: " + seatId);
        }
        seat.setReservation_FK(reservationId);
        seatDAO.update(seatId, seat);
    }

    public ArrayList<Seat> getSeatsByReservationId(int reservationId) throws SQLException {
        return seatDAO.getByReservationId(reservationId);
    }

    public Seat getSeatById(int seatId) throws SQLException {
        return seatDAO.getById(seatId);
    }
}
