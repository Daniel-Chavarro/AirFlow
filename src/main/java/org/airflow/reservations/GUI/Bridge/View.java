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

/**
 * The View interface defines the contract for the user interface in the MVC pattern.
 * It provides methods for the controller to interact with the UI, such as displaying panels,
 * retrieving user input, and setting data.
 */
public interface View {

    /** Command for searching for a flight. */
    String SEARCH_FLIGHT_CMD = "SEARCH_FLIGHT";
    /** Command for viewing the details of a flight. */
    String DETAILS_FLIGHT_CMD = "DETAILS_FLIGHT";
    /** Command for booking a seat on a flight. */
    String BOOK_SEAT_CMD = "BOOK_SEAT";
    /** Command for confirming a reservation. */
    String CONFIRM_RESERVATION_CMD = "CONFIRM_RESERVATION";
    /** Command for the final confirmation of a reservation. */
    String FINAL_CONFIRM_CMD = "FINAL_CONFIRM";
    /** Command for clearing selected seats. */
    String CLEAR_SEATS_CMD = "CLEAR_SEATS";
    /** Command for going back to the seat selection screen. */
    String BACK_TO_SEAT_SELECTION_CMD = "BACK_TO_SEAT_SELECTION";
    /** Command for going back to the flight selection screen. */
    String BACK_TO_FLIGHTS_CMD = "BACK_TO_FLIGHTS";
    /** Command for logging out. */
    String LOGOUT_CMD = "LOGOUT";
    /** Command for selecting a seat. */
    String SELECT_SEAT = "SELECT_SEAT";

    /**
     * Adds an action listener to the view's components.
     *
     * @param listener The action listener to add.
     */
    void addActionListener(ActionListener listener);

    /**
     * Shows a specific panel in the main frame.
     *
     * @param panelName The name of the panel to show.
     */
    void showPanel(String panelName);

    /**
     * Gets the main frame of the application.
     *
     * @return The main JFrame.
     */
    JFrame getFrame();

    /**
     * Gets the selected origin city from the UI.
     *
     * @return The selected origin city.
     */
    String getOrigin();

    /**
     * Gets the selected destination city from the UI.
     *
     * @return The selected destination city.
     */
    String getDestination();

    /**
     * Gets the selected departure date from the UI.
     *
     * @return The selected departure date.
     */
    LocalDate getDepartureDate();

    /**
     * Gets the selected return date from the UI.
     *
     * @return The selected return date.
     */
    LocalDate getReturnDate();

    /**
     * Sets the cities data for the search panel combo boxes.
     * This method allows the controller to populate the origin and destination dropdowns
     * with cities from the database through the service layer.
     *
     * @param cities The list of cities to populate the combo boxes with.
     */
    void setCitiesData(ArrayList<City> cities);

    /**
     * Gets the list of selected seats from the UI.
     *
     * @return The list of selected seats.
     */
    ArrayList<Seat> getSelectedSeats();

    /**
     * Sets the flight details to be displayed in the details panel.
     *
     * @param flight      The flight to display.
     * @param origin      The origin city of the flight.
     * @param destination The destination city of the flight.
     * @param airplane    The airplane for the flight.
     */
    void setFlightDetails(Flight flight, City origin, City destination, Airplane airplane);

    /**
     * Sets the data for the book seats panel.
     *
     * @param flight   The flight for which to book seats.
     * @param airplane The airplane for the flight.
     * @param seats    The list of available seats.
     */
    void setBookSeatsData(Flight flight, Airplane airplane, ArrayList<Seat> seats);

    /**
     * Sets the data for the book seats panel with city information.
     *
     * @param flight   The flight for which to book seats.
     * @param airplane The airplane for the flight.
     * @param seats    The list of available seats.
     * @param originCity The origin city of the flight.
     * @param destinationCity The destination city of the flight.
     */
    void setBookSeatsData(Flight flight, Airplane airplane, ArrayList<Seat> seats, City originCity, City destinationCity);

    /**
     * Sets the data for the confirmation panel.
     *
     * @param flight           The flight to confirm.
     * @param originCity       The origin city of the flight.
     * @param destinationCity  The destination city of the flight.
     * @param airplane         The airplane for the flight.
     * @param selectedSeats    The list of selected seats.
     */
    void setConfirmationData(Flight flight, City originCity, City destinationCity, Airplane airplane,
                             ArrayList<Seat> selectedSeats, Map<Seat.SeatClass, Double> classMultipliers, double basePrice);

    /**
     * Displays a list of flights in the search results panel.
     *
     * @param flights     The list of flights to display.
     * @param origin      The origin city of the flights.
     * @param destination The destination city of the flights.
     */
    void displayFlights(ArrayList<Flight> flights, City origin, City destination);

    void toggleSeatSelection(String seatNumber);

    void clearSeatSelections();

    void updateSeatSummary();
}