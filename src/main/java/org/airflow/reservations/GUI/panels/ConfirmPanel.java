package org.airflow.reservations.GUI.panels;

import org.airflow.reservations.GUI.Bridge.View;
import org.airflow.reservations.model.Airplane;
import org.airflow.reservations.model.City;
import org.airflow.reservations.model.Flight;
import org.airflow.reservations.model.Seat;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * ConfirmPanel is a JPanel that displays a reservation confirmation interface.
 * It shows flight details, selected seats, pricing breakdown, and allows final confirmation.
 * The panel is designed to be the final step in the flight reservation process.
 */
public class ConfirmPanel extends JPanel {
    /** The main panel that holds all other components. */
    private final JPanel mainPanel;
    /** Formatter for displaying time. */
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");
    /** Formatter for displaying dates. */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    // Main components
    /** The panel for the header section. */
    private JPanel headerPanel;
    /** The panel for the flight summary. */
    private JPanel flightSummaryPanel;
    /** The panel for the seat summary. */
    private JPanel seatSummaryPanel;
    /** The panel for the pricing breakdown. */
    private JPanel pricingPanel;
    /** The panel for the action buttons. */
    private JPanel buttonsPanel;

    // Header labels
    /** The main title label. */
    private JLabel titleLabel;
    /** The confirmation message label. */
    private JLabel confirmationLabel;

    // Flight summary labels
    /** The title for the flight summary section. */
    private JLabel flightSummaryTitle;
    /** The label for the flight route. */
    private JLabel flightRouteLabel;
    /** The label for the flight time. */
    private JLabel flightTimeLabel;
    /** The label for the flight date. */
    private JLabel flightDateLabel;
    /** The label for the aircraft details. */
    private JLabel aircraftLabel;
    /** The label for the flight code. */
    private JLabel flightCodeLabel;

    // Seat summary components
    /** The title for the seat summary section. */
    private JLabel seatSummaryTitle;
    /** The panel that lists the selected seats. */
    private JPanel seatListPanel;

    // Pricing labels
    /** The title for the pricing section. */
    private JLabel pricingTitle;
    /** The label for the subtotal. */
    private JLabel subtotalLabel;
    /** The label for taxes and fees. */
    private JLabel taxesLabel;
    /** The label for the total price. */
    private JLabel totalLabel;

    // Navigation buttons
    /** The button to go back to the previous screen. */
    private JButton backButton;
    /** The button to confirm the reservation. */
    private JButton confirmButton;

    // Data
    /** The flight being confirmed. */
    private Flight flight;
    /** The origin city of the flight. */
    private City originCity;
    /** The destination city of the flight. */
    private City destinationCity;
    /** The airplane for the flight. */
    private Airplane airplane;
    /** The list of selected seats. */
    private ArrayList<Seat> selectedSeats;
    /** The map of seat class multipliers for pricing. */
    private Map<Seat.SeatClass, Double> classMultipliers;
    /** The base price of the flight. */
    private double basePrice;
    /** The total price of the reservation. */
    private double totalPrice;

    /**
     * Constructor for ConfirmPanel.
     * Initializes the panel with a layout and sets up the main components.
     */
    public ConfirmPanel() {
        setLayout(new BorderLayout());
        mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);

        selectedSeats = new ArrayList<>();

        // Initialize buttons early so they can be accessed by MainFrame
        initializeButtons();
    }

    /**
     * Initializes the buttons that need to be accessible immediately.
     */
    private void initializeButtons() {
        // Initialize back button
        backButton = new JButton("Back to Seat Selection");
        backButton.setPreferredSize(new Dimension(180, 40));
        backButton.setBackground(Color.WHITE);
        backButton.setForeground(new Color(0, 102, 204));
        backButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 102, 204), 2),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        backButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        backButton.setFocusPainted(false);
        backButton.setActionCommand(View.BACK_TO_SEAT_SELECTION_CMD);

        // Initialize confirm button
        confirmButton = new JButton("Confirm Reservation");
        confirmButton.setPreferredSize(new Dimension(180, 40));
        confirmButton.setBackground(new Color(0, 102, 204));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        confirmButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        confirmButton.setFocusPainted(false);
        confirmButton.setActionCommand(View.CONFIRM_RESERVATION_CMD);
    }

    /**
     * Sets the data for the confirmation panel and builds the interface.
     *
     * @param flight           the flight to be confirmed
     * @param originCity       the origin city
     * @param destinationCity  the destination city
     * @param airplane         the airplane for the flight
     * @param selectedSeats    the list of selected seats
     * @param classMultipliers the pricing multipliers for seat classes
     * @param basePrice        the base price for the flight
     */
    public void setData(Flight flight, City originCity, City destinationCity, Airplane airplane,
                        ArrayList<Seat> selectedSeats, Map<Seat.SeatClass, Double> classMultipliers, double basePrice) {
        this.flight = flight;
        this.originCity = originCity;
        this.destinationCity = destinationCity;
        this.airplane = airplane;
        this.selectedSeats = new ArrayList<>(selectedSeats);
        this.classMultipliers = classMultipliers;
        this.basePrice = basePrice;

        calculateTotalPrice();
        buildInterface();

        add(mainPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    /**
     * Calculates the total price based on selected seats and class multipliers.
     */
    private void calculateTotalPrice() {
        totalPrice = 0.0;
        for (Seat seat : selectedSeats) {
            double multiplier = classMultipliers.getOrDefault(seat.getSeat_class(), 1.0);
            totalPrice += basePrice * multiplier;
        }
    }

    /**
     * Builds the complete interface with all panels.
     */
    private void buildInterface() {
        // Clear any existing components
        mainPanel.removeAll();

        createHeaderPanel();
        createFlightSummaryPanel();
        createSeatSummaryPanel();
        createPricingPanel();
        createButtonsPanel();

        mainPanel.add(headerPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(flightSummaryPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(seatSummaryPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(pricingPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(buttonsPanel);
    }

    /**
     * Creates the header panel with title and confirmation message.
     */
    private void createHeaderPanel() {
        headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        titleLabel = new JLabel("Confirm Your Reservation");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0, 102, 204));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        confirmationLabel = new JLabel("Please review your booking details below");
        confirmationLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        confirmationLabel.setForeground(Color.DARK_GRAY);
        confirmationLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        headerPanel.add(confirmationLabel);
    }

    /**
     * Creates the flight summary panel showing flight details.
     */
    private void createFlightSummaryPanel() {
        flightSummaryPanel = new JPanel();
        flightSummaryPanel.setLayout(new BorderLayout());
        flightSummaryPanel.setBackground(Color.WHITE);
        flightSummaryPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Title
        flightSummaryTitle = new JLabel("Flight Information");
        flightSummaryTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        flightSummaryTitle.setForeground(new Color(0, 102, 204));

        // Content panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 20);
        gbc.anchor = GridBagConstraints.WEST;

        // Flight route
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(new JLabel("Route:"), gbc);
        gbc.gridx = 1;
        flightRouteLabel = new JLabel(String.format("%s → %s",
                originCity.getName(), destinationCity.getName()));
        flightRouteLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        contentPanel.add(flightRouteLabel, gbc);

        // Flight code
        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPanel.add(new JLabel("Flight:"), gbc);
        gbc.gridx = 1;
        flightCodeLabel = new JLabel(flight.getCode());
        flightCodeLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        contentPanel.add(flightCodeLabel, gbc);

        // Date
        gbc.gridx = 0;
        gbc.gridy = 2;
        contentPanel.add(new JLabel("Date:"), gbc);
        gbc.gridx = 1;
        flightDateLabel = new JLabel(flight.getDeparture_time().format(DATE_FORMATTER));
        flightDateLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        contentPanel.add(flightDateLabel, gbc);

        // Time
        gbc.gridx = 0;
        gbc.gridy = 3;
        contentPanel.add(new JLabel("Time:"), gbc);
        gbc.gridx = 1;
        flightTimeLabel = new JLabel(String.format("%s - %s",
                flight.getDeparture_time().format(TIME_FORMATTER),
                flight.getArrival_time().format(TIME_FORMATTER)));
        flightTimeLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        contentPanel.add(flightTimeLabel, gbc);

        // Aircraft
        gbc.gridx = 0;
        gbc.gridy = 4;
        contentPanel.add(new JLabel("Aircraft:"), gbc);
        gbc.gridx = 1;
        aircraftLabel = new JLabel(String.format("%s %s",
                airplane.getAirline(), airplane.getModel()));
        aircraftLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        contentPanel.add(aircraftLabel, gbc);

        flightSummaryPanel.add(flightSummaryTitle, BorderLayout.NORTH);
        flightSummaryPanel.add(Box.createRigidArea(new Dimension(0, 10)), BorderLayout.CENTER);
        flightSummaryPanel.add(contentPanel, BorderLayout.SOUTH);
    }

    /**
     * Creates the seat summary panel showing selected seats.
     */
    private void createSeatSummaryPanel() {
        seatSummaryPanel = new JPanel();
        seatSummaryPanel.setLayout(new BorderLayout());
        seatSummaryPanel.setBackground(Color.WHITE);
        seatSummaryPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Title
        seatSummaryTitle = new JLabel(String.format("Selected Seats (%d)", selectedSeats.size()));
        seatSummaryTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        seatSummaryTitle.setForeground(new Color(0, 102, 204));

        // Seat list panel
        seatListPanel = new JPanel();
        seatListPanel.setLayout(new BoxLayout(seatListPanel, BoxLayout.Y_AXIS));
        seatListPanel.setBackground(Color.WHITE);
        seatListPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        for (int i = 0; i < selectedSeats.size(); i++) {
            Seat seat = selectedSeats.get(i);
            JPanel seatPanel = createSeatRow(seat);
            seatListPanel.add(seatPanel);

            // Add spacing between seats, but not after the last one
            if (i < selectedSeats.size() - 1) {
                seatListPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        // Content panel for better layout control - no scroll pane, let it size naturally
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)), BorderLayout.NORTH);
        contentPanel.add(seatListPanel, BorderLayout.CENTER);

        seatSummaryPanel.add(seatSummaryTitle, BorderLayout.NORTH);
        seatSummaryPanel.add(contentPanel, BorderLayout.CENTER);
    }

    /**
     * Creates a row displaying seat information.
     *
     * @param seat the seat to display
     * @return JPanel containing seat information
     */
    private JPanel createSeatRow(Seat seat) {
        JPanel seatPanel = new JPanel(new BorderLayout());
        seatPanel.setBackground(Color.WHITE);
        seatPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(240, 240, 240), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        // Set a consistent size for each seat row
        seatPanel.setPreferredSize(new Dimension(0, 45));
        seatPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        seatPanel.setMinimumSize(new Dimension(200, 45));

        // Seat identifier
        JLabel seatLabel = new JLabel(String.format("Seat %s", seat.getSeat_number()));
        seatLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        // Seat class
        JLabel classLabel = new JLabel(seat.getSeat_class().toString());
        classLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        classLabel.setForeground(getClassColor(seat.getSeat_class()));

        // Price
        double seatPrice = basePrice * classMultipliers.getOrDefault(seat.getSeat_class(), 1.0);
        JLabel priceLabel = new JLabel(String.format("$%.2f", seatPrice));
        priceLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        priceLabel.setForeground(new Color(0, 128, 0));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.add(seatLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        leftPanel.add(classLabel);

        seatPanel.add(leftPanel, BorderLayout.WEST);
        seatPanel.add(priceLabel, BorderLayout.EAST);

        return seatPanel;
    }

    /**
     * Returns the color associated with a seat class.
     *
     * @param seatClass the seat class
     * @return Color for the seat class
     */
    private Color getClassColor(Seat.SeatClass seatClass) {
        switch (seatClass) {
            case FIRST:
                return new Color(255, 215, 0); // Gold
            case BUSINESS:
                return new Color(70, 130, 180); // Steel Blue
            case ECONOMY:
            default:
                return new Color(128, 128, 128); // Gray
        }
    }

    /**
     * Creates the pricing panel showing cost breakdown.
     */
    private void createPricingPanel() {
        pricingPanel = new JPanel();
        pricingPanel.setLayout(new BorderLayout());
        pricingPanel.setBackground(Color.WHITE);
        pricingPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Title
        pricingTitle = new JLabel("Price Summary");
        pricingTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        pricingTitle.setForeground(new Color(0, 102, 204));

        // Pricing details panel
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 20);
        gbc.anchor = GridBagConstraints.WEST;

        // Subtotal
        gbc.gridx = 0;
        gbc.gridy = 0;
        detailsPanel.add(new JLabel("Subtotal:"), gbc);
        gbc.gridx = 1;
        subtotalLabel = new JLabel(String.format("$%.2f", totalPrice));
        subtotalLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        detailsPanel.add(subtotalLabel, gbc);

        // Taxes and fees
        double taxes = totalPrice * 0.12; // 12% tax rate
        gbc.gridx = 0;
        gbc.gridy = 1;
        detailsPanel.add(new JLabel("Taxes & Fees:"), gbc);
        gbc.gridx = 1;
        taxesLabel = new JLabel(String.format("$%.2f", taxes));
        taxesLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        detailsPanel.add(taxesLabel, gbc);

        // Separator line
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        JSeparator separator = new JSeparator();
        detailsPanel.add(separator, gbc);

        // Total
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(5, 0, 5, 20);
        JLabel totalTitleLabel = new JLabel("Total:");
        totalTitleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        detailsPanel.add(totalTitleLabel, gbc);
        gbc.gridx = 1;
        totalLabel = new JLabel(String.format("$%.2f", totalPrice + taxes));
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        totalLabel.setForeground(new Color(0, 128, 0));
        detailsPanel.add(totalLabel, gbc);

        pricingPanel.add(pricingTitle, BorderLayout.NORTH);
        pricingPanel.add(Box.createRigidArea(new Dimension(0, 10)), BorderLayout.CENTER);
        pricingPanel.add(detailsPanel, BorderLayout.SOUTH);
    }

    /**
     * Creates the buttons panel with navigation controls.
     */
    private void createButtonsPanel() {
        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonsPanel.setBackground(Color.WHITE);

        // Add the already initialized buttons to the panel
        buttonsPanel.add(backButton);
        buttonsPanel.add(confirmButton);
    }

    /**
     * Gets the selected seats list.
     *
     * @return ArrayList of selected seats
     */
    public ArrayList<Seat> getSelectedSeats() {
        return new ArrayList<>(selectedSeats);
    }

    /**
     * Gets the total price including taxes.
     *
     * @return total price with taxes
     */
    public double getTotalPriceWithTaxes() {
        return totalPrice + (totalPrice * 0.12);
    }

    /**
     * Override getPreferredSize to ensure the panel sizes properly based on content.
     *
     * @return the preferred size for this panel
     */
    @Override
    public Dimension getPreferredSize() {
        // Calculate height based on number of components and seats
        int baseHeight = 500; // Base height for header, flight info, pricing, buttons
        int seatHeight = selectedSeats.size() * 55; // Each seat row with spacing
        int totalHeight = baseHeight + seatHeight;

        return new Dimension(600, Math.max(600, totalHeight));
    }

    /**
     * Main method for testing the ConfirmPanel.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Confirm Reservation - Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 700);
            frame.setLocationRelativeTo(null);

            // Create test data
            Flight testFlight = new Flight();
            testFlight.setCode("AF101");
            testFlight.setDeparture_time(java.time.LocalDateTime.now().plusDays(7));
            testFlight.setArrival_time(java.time.LocalDateTime.now().plusDays(7).plusHours(8));

            City origin = new City();
            origin.setName("New York");

            City destination = new City();
            destination.setName("London");

            Airplane airplane = new Airplane();
            airplane.setAirline("Boeing");
            airplane.setModel("777-300ER");

            // Create test seats
            ArrayList<Seat> selectedSeats = new ArrayList<>();

            Seat seat1 = new Seat();
            seat1.setSeat_number("12A");
            seat1.setSeat_class(Seat.SeatClass.ECONOMY);
            selectedSeats.add(seat1);

            Seat seat2 = new Seat();
            seat2.setSeat_number("12B");
            seat2.setSeat_class(Seat.SeatClass.BUSINESS);
            selectedSeats.add(seat2);

            // Create multipliers map
            Map<Seat.SeatClass, Double> multipliers = new HashMap<>();
            multipliers.put(Seat.SeatClass.ECONOMY, 1.0);
            multipliers.put(Seat.SeatClass.BUSINESS, 2.5);
            multipliers.put(Seat.SeatClass.FIRST, 4.0);

            ConfirmPanel confirmPanel = new ConfirmPanel();
            confirmPanel.setData(testFlight, origin, destination, airplane, selectedSeats, multipliers, 299.99);

            frame.add(confirmPanel);
            frame.setVisible(true);
        });
    }

    /**
     * Gets the back button.
     * @return The back button.
     */
    public JButton getBackButton() {
        return backButton;
    }

    /**
     * Gets the confirm button.
     * @return The confirm button.
     */
    public JButton getConfirmButton() {
        return confirmButton;
    }
}
