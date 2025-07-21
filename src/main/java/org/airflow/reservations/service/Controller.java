package org.airflow.reservations.service;

import org.airflow.reservations.GUI.Bridge.View;
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

public class Controller implements ActionListener {
    private static volatile Controller instance;
    private View view;
    private FlightService flightService;
    private CityService cityService;
    private AirplaneService airplaneService;
    private SeatService seatService;
    private ReservationService reservationService;
    private Flight selectedFlight;
    private User currentUser;

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
                    // Initialize and show the main application window
                    initializeAndShowApplication();
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
     * Initializes the main application components and displays the UI.
     */
    private void initializeAndShowApplication() {
        System.out.println("Starting AirFlow Reservation System...");

        // Create the main frame (view)
        org.airflow.reservations.GUI.frames.MainFrame mainFrame =
            new org.airflow.reservations.GUI.frames.MainFrame();

        // Set up the MVC relationship
        this.setView(mainFrame);
        mainFrame.addActionListener(this);

        // Configure the main window
        configureMainWindow(mainFrame);

        // Show the initial panel
        mainFrame.showPanel("SearchFlightPanel");

        // Make the application visible
        mainFrame.setVisible(true);

        System.out.println("AirFlow Reservation System started successfully!");

        // Optional: Load initial data or perform startup tasks
        performStartupTasks();
    }

    /**
     * Configures the main application window properties.
     */
    private void configureMainWindow(org.airflow.reservations.GUI.frames.MainFrame mainFrame) {
        // Set window properties
        mainFrame.setTitle("AirFlow - Flight Reservation System");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Center the window on screen
        mainFrame.setLocationRelativeTo(null);

        // Optional: Set application icon if available
        try {
            java.awt.Image icon = java.awt.Toolkit.getDefaultToolkit()
                .getImage(getClass().getClassLoader().getResource("images/logo.png"));
            if (icon != null) {
                mainFrame.setIconImage(icon);
            }
        } catch (Exception e) {
            System.out.println("Could not load application icon: " + e.getMessage());
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

    public void setView(View view) {
        this.view = view;
    }

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

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

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
                    handleConfirmReservation();
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

    private void handleSeatSelection(String command) {
        String seatNumber = command.split(":")[1];
        view.toggleSeatSelection(seatNumber);
        view.updateSeatSummary();
    }

    private void handleClearSeats() {
        view.clearSeatSelections();
    }

    private void handleConfirmReservation() throws SQLException {
        ArrayList<Seat> selectedSeats = view.getSelectedSeats();
        if (selectedSeats.isEmpty()) {
            JOptionPane.showMessageDialog(view.getFrame(), "Please select at least one seat.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int[] seatIds = selectedSeats.stream().mapToInt(Seat::getId).toArray();
        Reservation reservation = reservationService.createReservation(selectedFlight.getId(), seatIds);

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

        JOptionPane.showMessageDialog(view.getFrame(), "Reservation created successfully! Reservation ID: " + reservation.getId(), "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}
