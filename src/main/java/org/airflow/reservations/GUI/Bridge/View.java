package org.airflow.reservations.GUI.Bridge;

import org.airflow.reservations.model.Airplane;
import org.airflow.reservations.model.City;
import org.airflow.reservations.model.Flight;
import org.airflow.reservations.model.Seat;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

public interface View {

    String SEARCH_FLIGHT_CMD = "SEARCH_FLIGHT";
    String DETAILS_FLIGHT_CMD = "DETAILS_FLIGHT";
    String BOOK_SEAT_CMD = "BOOK_SEAT";
    String CONFIRM_RESERVATION_CMD = "CONFIRM_RESERVATION";
    String GO_TO_PAYMENT_CMD = "GO_TO_PAYMENT";
    String CLEAR_SEATS_CMD = "CLEAR_SEATS";
    String BACK_TO_SEAT_SELECTION_CMD = "BACK_TO_SEAT_SELECTION";
    String BACK_TO_FLIGHTS_CMD = "BACK_TO_FLIGHTS";
    String LOGOUT_CMD = "LOGOUT";

    void addActionListener(ActionListener listener);

    void showPanel(String panelName);

    JFrame getFrame();

    String getOrigin();

    String getDestination();

    LocalDate getDepartureDate();

    LocalDate getReturnDate();

    ArrayList<Seat> getSelectedSeats();

    void setFlightDetails(Flight flight, City origin, City destination, Airplane airplane);

    void setBookSeatsData(Flight flight, Airplane airplane, ArrayList<Seat> seats);

    void setConfirmationData(Flight flight, City originCity, City destinationCity, Airplane airplane,
                             ArrayList<Seat> selectedSeats, Map<Seat.SeatClass, Double> classMultipliers, double basePrice);

    void displayFlights(ArrayList<Flight> flights);
}
