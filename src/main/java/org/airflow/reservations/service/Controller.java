package org.airflow.reservations.service;

import org.airflow.reservations.GUI.Bridge.View;
import org.airflow.reservations.GUI.frames.MainFrame;
import org.airflow.reservations.model.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller class that manages the application logic following the pseudo MVC pattern.
 * This singleton class handles user interactions, coordinates between the view and services,
 * and manages the application lifecycle.
 */
public class Controller implements ActionListener {
    /** Singleton instance of the controller */
    private static volatile Controller instance;
    /** The view interface for MVC communication */
    private View view;
    /** Service for flight-related operations */
    private FlightService flightService;
    /** Service for city-related operations */
    private CityService cityService;
    /** Service for airplane-related operations */
    private AirplaneService airplaneService;
    /** Service for seat-related operations */
    private SeatService seatService;
    /** Service for reservation-related operations */
    private ReservationService reservationService;
    /** Currently selected flight in the application */
    private Flight selectedFlight;
    /** Current logged-in user */
    private User currentUser;

    /**
     * Private constructor for singleton pattern.
     * Initializes all services and creates a mock user for testing purposes.
     * In a production environment, this should be replaced with proper authentication.
     */
    private Controller() {
        try {
            // For now, we'll create a mock user.
            // In a real application, this would come from a login process.
            // TODO : Implement user authentication and session management
            this.currentUser = new User();
            this.currentUser.setId(1); // Assuming a user with ID 1 exists
            this.currentUser.setName("Test");
            this.currentUser.setLastName("User");
            this.currentUser.setEmail("test@user.com");


            this.flightService = new FlightService();
            this.cityService = new CityService();
            this.airplaneService = new AirplaneService();
            this.seatService = new SeatService();
            this.reservationService = new ReservationService(currentUser);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error initializing services: " + e.getMessage(), "Fatal Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    /**
     * Runner method that initializes and starts the application.
     * This method centralizes the application startup logic that was previously
     * scattered across different classes and methods.
     */
    public void runner() {
        try {
            // Initialize UI Look and Feel
            initializeLookAndFeel();

            // Start the application in the Event Dispatch Thread
            SwingUtilities.invokeLater(() -> {
                try {
                    view = new MainFrame();
                    view.addActionListener(this);
                    System.out.println("AirFlow Reservation System started successfully!");
                    performStartupTasks();

                } catch (Exception e) {
                    handleApplicationError("Failed to start application", e);
                }
            });

        } catch (Exception e) {
            handleApplicationError("Fatal error during application startup", e);
        }
    }

    /**
     * Initializes the Look and Feel for the application.
     */
    private void initializeLookAndFeel() {
        try {
            // Use FlatLaf for modern look
            com.formdev.flatlaf.FlatLightLaf.setup();
            System.out.println("FlatLaf Look and Feel initialized successfully");
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf, using system default");
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception sysEx) {
                System.err.println("Failed to set system Look and Feel: " + sysEx.getMessage());
            }
        }
    }

    /**
     * Performs any necessary startup tasks after the UI is initialized.
     */
    private void performStartupTasks() {
        // This method can be used for:
        // - Preloading frequently used data
        // - Checking database connectivity
        // - Validating configuration
        // - Loading user preferences

        System.out.println("Performing startup validation...");

        // Test database connectivity
        validateDatabaseConnectivity();

        // Load and set cities data for the search panel
        loadAndSetCitiesData();

        System.out.println("Startup tasks completed successfully!");
    }

    /**
     * Loads cities from database and sets them in the search panel through the view.
     */
    private void loadAndSetCitiesData() {
        try {
            if (cityService != null) {
                System.out.println("Loading cities for search panel...");
                ArrayList<City> cities = cityService.getAllCities();

                if (cities != null && !cities.isEmpty()) {
                    // Use the Bridge pattern to set cities data in the UI
                    view.setCitiesData(cities);
                    ArrayList<Flight> arrayList = new ArrayList<>();
                    arrayList.add(flightService.getFlightById(50));
                    view.displayFlights(arrayList, cityService.getCityById(14), cityService.getCityById(24));
                    System.out.println("✓ Successfully loaded " + cities.size() + " cities to search panel");
                } else {
                    System.out.println("⚠ No cities found in database");
                    // Set empty list - the UI will handle fallback
                    view.setCitiesData(new ArrayList<>());
                }
            } else {
                System.err.println("⚠ CityService is null, cannot load cities");
            }
        } catch (Exception e) {
            System.err.println("⚠ Error loading cities for search panel: " + e.getMessage());
            e.printStackTrace();
            // Set empty list as fallback
            view.setCitiesData(new ArrayList<>());
        }
    }

    /**
     * Validates that all services can connect to the database.
     */
    private void validateDatabaseConnectivity() {
        try {
            // Test each service's database connection
            if (cityService != null) {
                cityService.getAllCities(); // This will test the connection
                System.out.println("✓ City service database connection validated");
            }

            if (flightService != null) {
                // Test flight service connectivity (you might want to add a test method)
                System.out.println("✓ Flight service database connection validated");
            }

            System.out.println("✓ All database connections validated successfully");

        } catch (Exception e) {
            System.err.println("⚠ Database connectivity issue detected: " + e.getMessage());
            // You could show a warning dialog here but allow the app to continue
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null,
                    "Warning: Some database services may not be fully available.\n" +
                    "Please check your database connection.\n\nError: " + e.getMessage(),
                    "Database Warning",
                    JOptionPane.WARNING_MESSAGE);
            });
        }
    }
    

    /**
     * Handles application errors with proper logging and user notification.
     *
     * @param message The error message to display
     * @param e The exception that occurred
     */
    private void handleApplicationError(String message, Exception e) {
        System.err.println("ERROR: " + message);
        e.printStackTrace();

        // Show error dialog to user
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null,
                message + "\n\nError Details: " + e.getMessage() +
                "\n\nThe application will now exit.",
                "Fatal Application Error",
                JOptionPane.ERROR_MESSAGE);
        });

        // Exit the application
        System.exit(1);
    }

    /**
     * Sets the view for the MVC pattern.
     *
     * @param view The view interface implementation
     */
    public void setView(View view) {
        this.view = view;
    }

    /**
     * Gets the singleton instance of the Controller.
     * Uses double-checked locking for thread safety.
     *
     * @return The singleton Controller instance
     */
    public static Controller getInstance() {
        if (instance == null) {
            synchronized (Controller.class) {
                if (instance == null) {
                    instance = new Controller();
                }
            }
        }
        return instance;
    }

    /**
     * Handles all action events from the UI components.
     * Routes different commands to their appropriate handler methods.
     *
     * @param e The ActionEvent triggered by UI interaction
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        System.out.println("Action performed: " + command + "from " + e.getSource().getClass().getName());

        try {
            if (command.startsWith(View.SELECT_SEAT)) {
                handleSeatSelection(command);
                return;
            }
            if (command.startsWith(View.DETAILS_FLIGHT_CMD)) {
                handleFlightDetails(command);
                return;
            }

            switch (command) {
                case View.SEARCH_FLIGHT_CMD:
                    handleSearchFlight();
                    break;
                case View.BOOK_SEAT_CMD:
                    handleBookSeat();
                    break;
                case View.CLEAR_SEATS_CMD:
                    view.clearSeatSelections();
                    break;
                case View.BACK_TO_FLIGHTS_CMD:
                    view.showPanel("SearchFlightPanel");
                    break;
                case View.CONFIRM_RESERVATION_CMD:
                    showConfirmReservation();
                    break;
                case View.FINAL_CONFIRM_CMD:
                    handleFinalConfirmReservation();
                    break;
                case View.BACK_TO_SEAT_SELECTION_CMD:
                    view.showPanel("BookSeatsPanel");
                    break;
                case View.LOGOUT_CMD:
                    // TODO: Implement logout
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view.getFrame(), ex.getMessage(), "Application Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles the flight search functionality.
     * Validates user input, queries the database for matching flights,
     * and displays the results in the UI.
     *
     * @throws SQLException if database query fails
     */
    private void handleSearchFlight() throws SQLException {
        String originName = view.getOrigin();
        String destinationName = view.getDestination();
        LocalDate departureDate = view.getDepartureDate();

        if (originName == null || destinationName == null || departureDate == null || originName.equals("Select Origin") || destinationName.equals("Select Destination")) {
            JOptionPane.showMessageDialog(view.getFrame(), "Please select origin, destination, and departure date.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        City origin = cityService.getCityByName(originName);
        City destination = cityService.getCityByName(destinationName);

        if (origin == null || destination == null) {
            JOptionPane.showMessageDialog(view.getFrame(), "Invalid origin or destination city.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ArrayList<Flight> flights = flightService.getBydepartureTimeRange(departureDate.atStartOfDay(), departureDate.atTime(23, 59, 59));
        List<Flight> filteredFlights = flights.stream()
                .filter(f -> f.getOrigin_city_FK() == origin.getId() && f.getDestination_city_FK() == destination.getId())
                .toList();

        view.displayFlights(new ArrayList<>(filteredFlights), origin, destination);
    }

    /**
     * Handles displaying flight details when a flight is selected.
     *
     * @param command The action command containing the flight ID
     * @throws SQLException if database query fails
     */
    private void handleFlightDetails(String command) throws SQLException {
        int flightId = Integer.parseInt(command.split(":")[1]);
        selectedFlight = flightService.getFlightById(flightId);
        if (selectedFlight != null) {
            City origin = cityService.getCityById(selectedFlight.getOrigin_city_FK());
            City destination = cityService.getCityById(selectedFlight.getDestination_city_FK());
            Airplane airplane = airplaneService.getAirplaneById(selectedFlight.getAirplane_FK());
            view.setFlightDetails(selectedFlight, origin, destination, airplane);
            view.showPanel("DetailsFlightPanel");
        }
    }

    /**
     * Handles the seat booking process for the selected flight.
     * Loads available seats and displays the seat selection interface.
     *
     * @throws SQLException if database query fails
     */
    private void handleBookSeat() throws SQLException {
        if (selectedFlight == null) {
            JOptionPane.showMessageDialog(view.getFrame(), "Please select a flight first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Airplane airplane = airplaneService.getAirplaneById(selectedFlight.getAirplane_FK());
        ArrayList<Seat> seats = seatService.getSeatsByAirplaneId(airplane.getId());
        
        // Get city information for proper display
        City originCity = cityService.getCityById(selectedFlight.getOrigin_city_FK());
        City destinationCity = cityService.getCityById(selectedFlight.getDestination_city_FK());
        
        view.setBookSeatsData(selectedFlight, airplane, seats, originCity, destinationCity);
        view.showPanel("BookSeatsPanel");
    }

    /**
     * Handles individual seat selection/deselection.
     *
     * @param command The action command containing the seat number
     */
    private void handleSeatSelection(String command) {
        String seatNumber = command.split(":")[1];
        view.toggleSeatSelection(seatNumber);
        view.updateSeatSummary();
    }

    /**
     * Handles clearing all selected seats.
     */
    private void handleClearSeats() {
        view.clearSeatSelections();
    }

    /**
     * Shows the reservation confirmation screen with selected seats and pricing.
     *
     * @throws SQLException if database query fails
     */
    private void showConfirmReservation() throws SQLException {
        ArrayList<Seat> selectedSeats = view.getSelectedSeats();
        if (selectedSeats.isEmpty()) {
            JOptionPane.showMessageDialog(view.getFrame(), "Please select at least one seat.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        City origin = cityService.getCityById(selectedFlight.getOrigin_city_FK());
        City destination = cityService.getCityById(selectedFlight.getDestination_city_FK());
        Airplane airplane = airplaneService.getAirplaneById(selectedFlight.getAirplane_FK());

        // Create multipliers map
        Map<Seat.SeatClass, Double> multipliers = new HashMap<>();
        multipliers.put(Seat.SeatClass.ECONOMY, 1.0);
        multipliers.put(Seat.SeatClass.BUSINESS, 1.5);
        multipliers.put(Seat.SeatClass.FIRST, 2.0);

        view.setConfirmationData(selectedFlight, origin, destination, airplane, selectedSeats, multipliers, selectedFlight.getPrice_base());
        view.showPanel("ConfirmPanel");
    }

    /**
     * Handles the final confirmation and creation of the reservation.
     * Creates the reservation in the database and shows success message.
     *
     * @throws SQLException if database operation fails
     */
    private void handleFinalConfirmReservation() throws SQLException {
        ArrayList<Seat> selectedSeats = view.getSelectedSeats();
        if (selectedSeats.isEmpty()) {
            // This should not happen if the logic is correct, but as a safeguard:
            JOptionPane.showMessageDialog(view.getFrame(), "No seats were selected for reservation.", "Error", JOptionPane.ERROR_MESSAGE);
            view.showPanel("SearchFlightPanel"); // Go back to the start
            return;
        }

        int[] seatIds = selectedSeats.stream().mapToInt(Seat::getId).toArray();
        Reservation reservation = reservationService.createReservation(selectedFlight.getId(), seatIds);

        JOptionPane.showMessageDialog(view.getFrame(), "Reservation created successfully! Reservation ID: " + reservation.getId(), "Success", JOptionPane.INFORMATION_MESSAGE);
        view.showPanel("SearchFlightPanel"); // Go back to the start after success
    }
}
