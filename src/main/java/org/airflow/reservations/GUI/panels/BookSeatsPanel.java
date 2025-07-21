package org.airflow.reservations.GUI.panels;

import org.airflow.reservations.GUI.Bridge.View;
import org.airflow.reservations.model.Seat;
import org.airflow.reservations.model.Flight;
import org.airflow.reservations.model.Airplane;
import org.airflow.reservations.model.City;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Comparator;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * BookSeatsPanel class provides a panel for booking seats on a specific flight.
 * It displays flight information, a visual seat map, and allows seat selection.
 */
public class BookSeatsPanel extends JPanel {

    /** The flight for which seats are being booked. */
    private Flight flight;
    /** The airplane information. */
    private Airplane airplane;
    /** The list of available seats. */
    private ArrayList<Seat> seats;
    /** The list of currently selected seats. */
    private ArrayList<Seat> selectedSeats;
    /** A map of seat numbers to their corresponding buttons. */
    private Map<String, JButton> seatButtons;
    /** The origin city information. */
    private City originCity;
    /** The destination city information. */
    private City destinationCity;
    /** The action listener for seat and button interactions. */
    private ActionListener actionListener;

    /** The main panel containing all components. */
    private JPanel mainPanel;
    /** The panel displaying flight information. */
    private JPanel flightInfoPanel;
    /** The panel displaying the seat map. */
    private JPanel seatMapPanel;
    /** The panel displaying the booking summary. */
    private JPanel summaryPanel;
    /** The panel containing action buttons. */
    private JPanel buttonPanel;

    /** The label for the base price. */
    private JLabel priceLabel;
    /** The label for the selected seats. */
    private JLabel selectedSeatsLabel;
    /** The label for the total price. */
    private JLabel totalPriceLabel;
    /** The button to confirm the booking. */
    private JButton confirmButton;
    /** The button to cancel the booking. */
    private JButton cancelButton;
    /** The button to clear all selected seats. */
    private JButton clearSeatsButton;

    /** Formatter for currency values. */
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(Locale.US);

    /**
     * Sets the flight data and rebuilds the entire panel with real data.
     *
     * @param flight   The flight for which seats are being booked
     * @param airplane The airplane information
     * @param seats    The list of available seats from database
     */
    public void setFlightData(Flight flight, Airplane airplane, ArrayList<Seat> seats) {
        this.flight = flight;
        this.airplane = airplane;
        this.seats = seats;
        this.selectedSeats = new ArrayList<>();
        this.seatButtons = new HashMap<>();
        this.originCity = null;
        this.destinationCity = null;

        // Clear existing content
        removeAll();

        // Reinitialize with real data
        initializeComponents();
        layoutComponents();
        
        // Re-add action listener if one was previously set
        if (actionListener != null) {
            addActionListener(actionListener);
        }
        
        revalidate();
        repaint();
    }

    /**
     * Sets the flight data and rebuilds the entire panel with real data including city information.
     *
     * @param flight   The flight for which seats are being booked
     * @param airplane The airplane information
     * @param seats    The list of available seats from database
     * @param originCity The origin city information
     * @param destinationCity The destination city information
     */
    public void setFlightData(Flight flight, Airplane airplane, ArrayList<Seat> seats, City originCity, City destinationCity) {
        this.flight = flight;
        this.airplane = airplane;
        this.seats = seats;
        this.selectedSeats = new ArrayList<>();
        this.seatButtons = new HashMap<>();
        this.originCity = originCity;
        this.destinationCity = destinationCity;

        // Clear existing content
        removeAll();

        // Reinitialize with real data
        initializeComponents();
        layoutComponents();
        
        // Re-add action listener if one was previously set
        if (actionListener != null) {
            addActionListener(actionListener);
        }
        
        revalidate();
        repaint();
    }

    /**
     * Constructor for BookSeatsPanel with flight data.
     *
     * @param flight The flight for which seats are being booked
     * @param airplane The airplane information
     * @param seats List of available seats
     */
    public BookSeatsPanel(Flight flight, Airplane airplane, ArrayList<Seat> seats) {
        this.flight = flight;
        this.airplane = airplane;
        this.seats = seats;
        this.selectedSeats = new ArrayList<>();
        this.seatButtons = new HashMap<>();

        initializeComponents();
        layoutComponents();
    }

    /**
     * Default constructor for BookSeatsPanel.
     * Creates a panel with sample data for testing.
     */
    public BookSeatsPanel() {
        this.selectedSeats = new ArrayList<>();
        this.seatButtons = new HashMap<>();

        // Create sample flight data for testing
        createSampleData();

        initializeComponents();
        layoutComponents();

        // Pre-select seats 4B and 4C as shown in the screenshot
        preselectSampleSeats();
    }

    /**
     * Creates sample data for testing purposes.
     */
    private void createSampleData() {
        // Create sample flight from Bogotá to Lima
        this.flight = new Flight();
        this.flight.setId(1);
        this.flight.setCode("AV8001");
        this.flight.setPrice_base(80.70f); // Economy base price in USD

        // Create sample airplane
        this.airplane = new Airplane();
        this.airplane.setId(1);
        this.airplane.setAirline("Avianca");
        this.airplane.setModel("Airbus A320");
        this.airplane.setCapacity(180);

        // Create sample seats
        this.seats = new ArrayList<>();
        createSampleSeats();
    }

    /**
     * Creates sample seats for testing purposes.
     */
    private void createSampleSeats() {
        int seatId = 1;
        // Creating a more realistic seat layout for an Airbus A320
        String[] rows = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"};
        String[] columns = {"A", "B", "C", "D", "E", "F"};

        for (String row : rows) {
            for (String col : columns) {
                Seat seat = new Seat();
                seat.setId(seatId++);
                seat.setAirplane_FK(1);
                seat.setSeat_number(row + col);

                // Set seat class based on row
                if (Integer.parseInt(row) <= 4) {
                    seat.setSeat_class(Seat.SeatClass.FIRST);
                } else if (Integer.parseInt(row) <= 8) {
                    seat.setSeat_class(Seat.SeatClass.BUSINESS);
                } else {
                    seat.setSeat_class(Seat.SeatClass.ECONOMY);
                }

                // Set window seats
                seat.setIs_window(col.equals("A") || col.equals("F"));

                // Mark some seats as reserved (4B, 4C are selected in the image)
                if ((row.equals("4") && col.equals("B")) || (row.equals("4") && col.equals("C"))) {
                    // These are shown as selected in the screenshot
                    seat.setReservation_FK(null); // Available but will be pre-selected
                } else if (Math.random() > 0.8) {
                    seat.setReservation_FK(999); // Reserved
                }

                seats.add(seat);
            }
        }
    }

    /**
     * Pre-selects sample seats for testing purposes.
     */
    private void preselectSampleSeats() {
        // Pre-select seats 4B and 4C as shown in the screenshot
        SwingUtilities.invokeLater(() -> {
            for (Seat seat : seats) {
                if (seat.getSeat_number().equals("4B") || seat.getSeat_number().equals("4C")) {
                    selectedSeats.add(seat);
                    JButton button = seatButtons.get(seat.getSeat_number());
                    if (button != null) {
                        button.setBackground(new Color(60, 120, 60));
                        button.setForeground(Color.WHITE);
                    }
                }
            }
            updateSummary();
        });
    }

    /**
     * Initializes all the components of the panel.
     */
    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Main panel
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);

        // Flight info panel
        flightInfoPanel = new JPanel(new GridBagLayout());
        flightInfoPanel.setBackground(Color.WHITE);
        flightInfoPanel.setBorder(BorderFactory.createTitledBorder("Flight Information"));

        // Seat map panel
        seatMapPanel = new JPanel();
        seatMapPanel.setBackground(Color.WHITE);
        seatMapPanel.setBorder(BorderFactory.createTitledBorder("Select Your Seats"));

        // Summary panel
        summaryPanel = new JPanel(new GridBagLayout());
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Booking Summary"));

        // Button panel
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        createFlightInfoComponents();
        createSeatMapComponents();
        createSummaryComponents();
        createButtonComponents();
    }

    /**
     * Creates the components for the flight information panel.
     */
    private void createFlightInfoComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Flight route - use real city data if available, otherwise use flight code
        String routeText;
        String fromToText;
        if (originCity != null && destinationCity != null) {
            routeText = originCity.getCode() + " - " + destinationCity.getCode();
            fromToText = originCity.getName() + " to " + destinationCity.getName();
        } else {
            routeText = "Flight " + flight.getCode();
            fromToText = "Flight Route";
        }
        
        JLabel routeLabel = new JLabel(routeText);
        routeLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        flightInfoPanel.add(routeLabel, gbc);

        // Flight details
        gbc.gridwidth = 1;
        JLabel fromLabel = new JLabel(fromToText);
        fromLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 1;
        flightInfoPanel.add(fromLabel, gbc);

        // Flight code
        JLabel flightCodeLabel = new JLabel("Flight: " + flight.getCode());
        flightCodeLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        gbc.gridx = 1; gbc.gridy = 1;
        flightInfoPanel.add(flightCodeLabel, gbc);

        // Airline and aircraft
        JLabel airlineLabel = new JLabel("Airline: " + airplane.getAirline());
        airlineLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 2;
        flightInfoPanel.add(airlineLabel, gbc);

        JLabel aircraftLabel = new JLabel("Aircraft: " + airplane.getModel());
        aircraftLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 1; gbc.gridy = 2;
        flightInfoPanel.add(aircraftLabel, gbc);

        // Base price
        priceLabel = new JLabel("Economy Base Price: " + CURRENCY_FORMAT.format(flight.getPrice_base()));
        priceLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        priceLabel.setForeground(new Color(0, 122, 255));
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        flightInfoPanel.add(priceLabel, gbc);

        // Seat class pricing information
        gbc.gridwidth = 1;
        gbc.gridy = 4;

        JLabel pricingTitleLabel = new JLabel("Seat Class Pricing:");
        pricingTitleLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        pricingTitleLabel.setForeground(new Color(80, 80, 80));
        gbc.gridx = 0; gbc.gridwidth = 2;
        flightInfoPanel.add(pricingTitleLabel, gbc);

        // Economy pricing
        gbc.gridwidth = 1;
        gbc.gridy = 5;
        JLabel economyPriceLabel = new JLabel("• Economy: " + CURRENCY_FORMAT.format(80.70));
        economyPriceLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        economyPriceLabel.setForeground(new Color(120, 160, 220));
        gbc.gridx = 0;
        flightInfoPanel.add(economyPriceLabel, gbc);

        // Business/Plus pricing
        JLabel businessPriceLabel = new JLabel("• Plus: " + CURRENCY_FORMAT.format(193.60));
        businessPriceLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        businessPriceLabel.setForeground(new Color(255, 140, 0));
        gbc.gridx = 1;
        flightInfoPanel.add(businessPriceLabel, gbc);

        // First class pricing
        gbc.gridy = 6;
        JLabel firstClassPriceLabel = new JLabel("• First Class: " + CURRENCY_FORMAT.format(193.60));
        firstClassPriceLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        firstClassPriceLabel.setForeground(new Color(220, 120, 120));
        gbc.gridx = 0;
        flightInfoPanel.add(firstClassPriceLabel, gbc);
    }

    /**
     * Creates the components for the seat map panel.
     */
    private void createSeatMapComponents() {
        seatMapPanel.setLayout(new BorderLayout());

        // Legend panel
        JPanel legendPanel = createLegendPanel();
        seatMapPanel.add(legendPanel, BorderLayout.NORTH);

        // Seat grid panel
        JPanel seatGridPanel = createSeatGridPanel();
        JScrollPane scrollPane = new JScrollPane(seatGridPanel);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        seatMapPanel.add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Creates the legend panel for the seat map.
     * @return The legend panel.
     */
    private JPanel createLegendPanel() {
        JPanel legendPanel = new JPanel(new FlowLayout());
        legendPanel.setBackground(Color.WHITE);

        // First Class
        JButton firstClassBtn = new JButton();
        firstClassBtn.setPreferredSize(new Dimension(25, 25));
        firstClassBtn.setBackground(new Color(220, 120, 120));
        firstClassBtn.setEnabled(false);
        legendPanel.add(firstClassBtn);
        legendPanel.add(new JLabel("First Class ($193.60)"));

        legendPanel.add(Box.createHorizontalStrut(15));

        // Business/Plus Class
        JButton businessBtn = new JButton();
        businessBtn.setPreferredSize(new Dimension(25, 25));
        businessBtn.setBackground(new Color(255, 200, 120));
        businessBtn.setEnabled(false);
        legendPanel.add(businessBtn);
        legendPanel.add(new JLabel("Plus ($193.60)"));

        legendPanel.add(Box.createHorizontalStrut(15));

        // Economy Class
        JButton economyBtn = new JButton();
        economyBtn.setPreferredSize(new Dimension(25, 25));
        economyBtn.setBackground(new Color(120, 160, 220));
        economyBtn.setEnabled(false);
        legendPanel.add(economyBtn);
        legendPanel.add(new JLabel("Economy ($80.70)"));

        legendPanel.add(Box.createHorizontalStrut(15));

        // Selected seat
        JButton selectedBtn = new JButton();
        selectedBtn.setPreferredSize(new Dimension(25, 25));
        selectedBtn.setBackground(new Color(60, 120, 60));
        selectedBtn.setEnabled(false);
        legendPanel.add(selectedBtn);
        legendPanel.add(new JLabel("Selected"));

        legendPanel.add(Box.createHorizontalStrut(15));

        // Reserved seat
        JButton reservedBtn = new JButton();
        reservedBtn.setPreferredSize(new Dimension(25, 25));
        reservedBtn.setBackground(new Color(120, 120, 120));
        reservedBtn.setEnabled(false);
        legendPanel.add(reservedBtn);
        legendPanel.add(new JLabel("Reserved"));

        return legendPanel;
    }

    /**
     * Creates the grid panel for the seat map.
     * @return The seat grid panel.
     */
    private JPanel createSeatGridPanel() {
        JPanel gridPanel = new JPanel(new GridBagLayout());
        gridPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);

        // Group seats by row number (e.g., "1", "2", "3")
        Map<String, List<Seat>> seatsByRow = new TreeMap<>((s1, s2) -> {
            // Allow proper numeric sorting for row keys
            return Integer.compare(Integer.parseInt(s1), Integer.parseInt(s2));
        });

        for (Seat seat : seats) {
            String seatNumber = seat.getSeat_number();
            String row = seatNumber.replaceAll("[A-Z]", "");
            seatsByRow.computeIfAbsent(row, k -> new ArrayList<>()).add(seat);
        }

        // Define the desired order of seat columns
        List<String> columnOrder = List.of("A", "B", "C", "D", "E", "F");

        int gridY = 0;
        for (Map.Entry<String, List<Seat>> entry : seatsByRow.entrySet()) {
            String rowNumber = entry.getKey();
            List<Seat> rowSeats = entry.getValue();

            // Sort seats in the row according to the defined column order
            rowSeats.sort(Comparator.comparing(seat -> {
                String col = seat.getSeat_number().replaceAll("[0-9]", "");
                return columnOrder.indexOf(col);
            }));

            // Add row label
            JLabel rowLabel = new JLabel(rowNumber);
            rowLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
            gbc.gridx = 0;
            gbc.gridy = gridY;
            gridPanel.add(rowLabel, gbc);

            // Add seat buttons
            int gridX = 1;
            for (Seat seat : rowSeats) {
                String column = seat.getSeat_number().replaceAll("[0-9]", "");
                
                // Add aisle gap after column C
                if (column.equals("D")) {
                    gridX++; // Leave a column empty for the aisle
                }

                JButton seatButton = createSeatButton(seat);
                gbc.gridx = gridX++;
                gbc.gridy = gridY;
                gridPanel.add(seatButton, gbc);
                seatButtons.put(seat.getSeat_number(), seatButton);
            }
            gridY++;
        }
        return gridPanel;
    }

    /**
     * Creates a button for a single seat.
     * @param seat The seat to create a button for.
     * @return The seat button.
     */
    private JButton createSeatButton(Seat seat) {
        JButton button = new JButton(seat.getSeat_number());
        button.setFont(new Font("SansSerif", Font.PLAIN, 10));

        // Set button color based on seat status and class
        if (seat.getReservation_FK() != null) {
            // Seat is reserved - use dark gray
            button.setBackground(new Color(120, 120, 120));
            button.setForeground(Color.WHITE);
            button.setEnabled(false);
        } else {
            // Seat is available - color based on class
            Color seatColor = getSeatClassColor(seat.getSeat_class());
            button.setBackground(seatColor);
            button.setForeground(Color.BLACK);
            button.setEnabled(true);
            // This internal listener provides immediate visual feedback.
            button.addActionListener(e -> {
                toggleSeatSelection(seat);
            });
        }

        // This allows the main controller to also listen to the event if needed.
        button.setActionCommand(View.SELECT_SEAT + ":" + seat.getSeat_number());

        return button;
    }

    /**
     * Gets the color for a given seat class.
     * @param seatClass The seat class.
     * @return The color for the seat class.
     */
    public Color getSeatClassColor(Seat.SeatClass seatClass) {
        return switch (seatClass) {
            case FIRST -> new Color(220, 120, 120); // Light red/pink for first class
            case BUSINESS -> new Color(255, 200, 120); // Light orange for business/plus
            case ECONOMY -> new Color(120, 160, 220); // Light blue for economy
            default -> new Color(200, 200, 200); // Light gray as fallback
        };
    }

    /**
     * Creates the components for the summary panel.
     */
    private void createSummaryComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        selectedSeatsLabel = new JLabel("Selected Seats: None");
        selectedSeatsLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 0;
        summaryPanel.add(selectedSeatsLabel, gbc);

        totalPriceLabel = new JLabel("Total Price: " + CURRENCY_FORMAT.format(0.0));
        totalPriceLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        totalPriceLabel.setForeground(new Color(0, 122, 255));
        gbc.gridx = 0; gbc.gridy = 1;
        summaryPanel.add(totalPriceLabel, gbc);
    }

    /**
     * Creates the components for the button panel.
     */
    private void createButtonComponents() {
        confirmButton = new JButton("Confirm Booking");
        confirmButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        confirmButton.setBackground(new Color(0, 122, 255));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setPreferredSize(new Dimension(180, 40));
        confirmButton.setEnabled(false);
        confirmButton.setActionCommand(View.CONFIRM_RESERVATION_CMD);

        clearSeatsButton = new JButton("Clear Selection");
        clearSeatsButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        clearSeatsButton.setBackground(new Color(255, 140, 0));
        clearSeatsButton.setForeground(Color.WHITE);
        clearSeatsButton.setPreferredSize(new Dimension(140, 40));
        clearSeatsButton.setEnabled(false);
        clearSeatsButton.setActionCommand(View.CLEAR_SEATS_CMD);

        cancelButton = new JButton("Back");
        cancelButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        cancelButton.setBackground(Color.LIGHT_GRAY);
        cancelButton.setPreferredSize(new Dimension(100, 40));
        cancelButton.setActionCommand(View.BACK_TO_FLIGHTS_CMD);

        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(clearSeatsButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(confirmButton);
    }

    /**
     * Lays out the components of the panel.
     */
    private void layoutComponents() {
        mainPanel.add(flightInfoPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(seatMapPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(summaryPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(buttonPanel);

        add(mainPanel, BorderLayout.CENTER);
    }

    /**
     * Updates the summary panel with the current selection.
     */
    public void updateSummary() {
        if (selectedSeats.isEmpty()) {
            selectedSeatsLabel.setText("Selected Seats: None");
            totalPriceLabel.setText("Total Price: " + CURRENCY_FORMAT.format(0.0));
            confirmButton.setEnabled(false);
            clearSeatsButton.setEnabled(false);
        } else {
            StringBuilder seatsText = new StringBuilder("Selected Seats: ");
            double totalPrice = 0;

            for (int i = 0; i < selectedSeats.size(); i++) {
                Seat seat = selectedSeats.get(i);
                seatsText.append(seat.getSeat_number());
                if (i < selectedSeats.size() - 1) {
                    seatsText.append(", ");
                }
                totalPrice += flight.getPrice_base();
            }

            selectedSeatsLabel.setText(seatsText.toString());
            totalPriceLabel.setText("Total Price: " + CURRENCY_FORMAT.format(totalPrice));
            confirmButton.setEnabled(true);
            clearSeatsButton.setEnabled(true);
        }
    }

    /**
     * Clears all seat selections.
     */
    public void clearAllSelections() {
        for (Seat seat : new ArrayList<>(selectedSeats)) {
            toggleSeatSelection(seat);
        }
        updateSummary();
    }

    public void toggleSeatSelection(Seat seat) {
        JButton button = seatButtons.get(seat.getSeat_number());
        if (selectedSeats.contains(seat)) {
            selectedSeats.remove(seat);
            if (button != null) {
                button.setBackground(getSeatClassColor(seat.getSeat_class()));
                button.setForeground(Color.BLACK);
            }
        } else {
            selectedSeats.add(seat);
            if (button != null) {
                button.setBackground(new Color(60, 120, 60));
                button.setForeground(Color.WHITE);
            }
        }
        updateSummary();
    }

    public Seat getSeatByNumber(String seatNumber) {
        for (Seat seat : seats) {
            if (seat.getSeat_number().equals(seatNumber)) {
                return seat;
            }
        }
        return null;
    }


    /**
     * Sets the flight for the panel.
     * @param flight The flight to set.
     */
    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    /**
     * Gets the airplane for the panel.
     * @return The airplane.
     */
    public Airplane getAirplane() {
        return airplane;
    }

    /**
     * Sets the airplane for the panel.
     * @param airplane The airplane to set.
     */
    public void setAirplane(Airplane airplane) {
        this.airplane = airplane;
    }

    /**
     * Gets the list of seats for the panel.
     * @return The list of seats.
     */
    public ArrayList<Seat> getSeats() {
        return seats;
    }

    /**
     * Sets the list of seats for the panel.
     * @param seats The list of seats to set.
     */
    public void setSeats(ArrayList<Seat> seats) {
        this.seats = seats;
    }

    /**
     * Sets the list of selected seats for the panel.
     * @param selectedSeats The list of selected seats to set.
     */
    public void setSelectedSeats(ArrayList<Seat> selectedSeats) {
        this.selectedSeats = selectedSeats;
    }

    /**
     * Gets the map of seat buttons.
     * @return The map of seat buttons.
     */
    public Map<String, JButton> getSeatButtons() {
        return seatButtons;
    }

    /**
     * Sets the map of seat buttons.
     * @param seatButtons The map of seat buttons to set.
     */
    public void setSeatButtons(Map<String, JButton> seatButtons) {
        this.seatButtons = seatButtons;
    }

    /**
     * Gets the main panel.
     * @return The main panel.
     */
    public JPanel getMainPanel() {
        return mainPanel;
    }

    /**
     * Sets the main panel.
     * @param mainPanel The main panel to set.
     */
    public void setMainPanel(JPanel mainPanel) {
        this.mainPanel = mainPanel;
    }

    /**
     * Gets the flight info panel.
     * @return The flight info panel.
     */
    public JPanel getFlightInfoPanel() {
        return flightInfoPanel;
    }

    /**
     * Sets the flight info panel.
     * @param flightInfoPanel The flight info panel to set.
     */
    public void setFlightInfoPanel(JPanel flightInfoPanel) {
        this.flightInfoPanel = flightInfoPanel;
    }

    /**
     * Gets the seat map panel.
     * @return The seat map panel.
     */
    public JPanel getSeatMapPanel() {
        return seatMapPanel;
    }

    /**
     * Sets the seat map panel.
     * @param seatMapPanel The seat map panel to set.
     */
    public void setSeatMapPanel(JPanel seatMapPanel) {
        this.seatMapPanel = seatMapPanel;
    }

    /**
     * Gets the summary panel.
     * @return The summary panel.
     */
    public JPanel getSummaryPanel() {
        return summaryPanel;
    }

    /**
     * Sets the summary panel.
     * @param summaryPanel The summary panel to set.
     */
    public void setSummaryPanel(JPanel summaryPanel) {
        this.summaryPanel = summaryPanel;
    }

    /**
     * Gets the button panel.
     * @return The button panel.
     */
    public JPanel getButtonPanel() {
        return buttonPanel;
    }

    /**
     * Sets the button panel.
     * @param buttonPanel The button panel to set.
     */
    public void setButtonPanel(JPanel buttonPanel) {
        this.buttonPanel = buttonPanel;
    }

    /**
     * Gets the price label.
     * @return The price label.
     */
    public JLabel getPriceLabel() {
        return priceLabel;
    }

    /**
     * Sets the price label.
     * @param priceLabel The price label to set.
     */
    public void setPriceLabel(JLabel priceLabel) {
        this.priceLabel = priceLabel;
    }

    /**
     * Gets the selected seats label.
     * @return The selected seats label.
     */
    public JLabel getSelectedSeatsLabel() {
        return selectedSeatsLabel;
    }

    /**
     * Sets the selected seats label.
     * @param selectedSeatsLabel The selected seats label to set.
     */
    public void setSelectedSeatsLabel(JLabel selectedSeatsLabel) {
        this.selectedSeatsLabel = selectedSeatsLabel;
    }

    /**
     * Gets the total price label.
     * @return The total price label.
     */
    public JLabel getTotalPriceLabel() {
        return totalPriceLabel;
    }

    /**
     * Sets the total price label.
     * @param totalPriceLabel The total price label to set.
     */
    public void setTotalPriceLabel(JLabel totalPriceLabel) {
        this.totalPriceLabel = totalPriceLabel;
    }

    /**
     * Gets the confirm button.
     * @return The confirm button.
     */
    public JButton getConfirmButton() {
        return confirmButton;
    }

    /**
     * Sets the confirm button.
     * @param confirmButton The confirm button to set.
     */
    public void setConfirmButton(JButton confirmButton) {
        this.confirmButton = confirmButton;
    }

    /**
     * Gets the cancel button.
     * @return The cancel button.
     */
    public JButton getCancelButton() {
        return cancelButton;
    }

    /**
     * Sets the cancel button.
     * @param cancelButton The cancel button to set.
     */
    public void setCancelButton(JButton cancelButton) {
        this.cancelButton = cancelButton;
    }

    /**
     * Gets the clear seats button.
     * @return The clear seats button.
     */
    public JButton getClearSeatsButton() {
        return clearSeatsButton;
    }

    /**
     * Sets the clear seats button.
     * @param clearSeatsButton The clear seats button to set.
     */
    public void setClearSeatsButton(JButton clearSeatsButton) {
        this.clearSeatsButton = clearSeatsButton;
    }

    /**
     * Gets the currency format.
     * @return The currency format.
     */
    public NumberFormat getCurrencyFormat() {
        return CURRENCY_FORMAT;
    }

    public void addActionListener(ActionListener listener) {
        this.actionListener = listener;

        // --- Remove existing listeners to prevent duplicates ---
        for(ActionListener al : confirmButton.getActionListeners()) {
            confirmButton.removeActionListener(al);
        }
        for(ActionListener al : clearSeatsButton.getActionListeners()) {
            clearSeatsButton.removeActionListener(al);
        }
        for(ActionListener al : cancelButton.getActionListeners()) {
            cancelButton.removeActionListener(al);
        }
        for (JButton button : seatButtons.values()) {
            for(ActionListener al : button.getActionListeners()) {
                button.removeActionListener(al);
            }
        }

        // --- Add listeners ---

        // Add internal listener for immediate visual feedback on seat clicks
        for (JButton button : seatButtons.values()) {
            Seat seat = getSeatByNumber(button.getText());
            if (seat != null && seat.getReservation_FK() == null) {
                button.addActionListener(e -> toggleSeatSelection(seat));
            }
        }

        // Add the main controller listener to all buttons
        confirmButton.addActionListener(listener);
        clearSeatsButton.addActionListener(listener);
        cancelButton.addActionListener(listener);
        for (JButton button : seatButtons.values()) {
            button.addActionListener(listener);
        }
    }

    // Getters for external access
    /**
     * Gets the flight for the panel.
     * @return The flight.
     */
    public Flight getFlight() {
        return flight;
    }

    /**
     * Gets the list of selected seats.
     * @return The list of selected seats.
     */
    public ArrayList<Seat> getSelectedSeats() {
        return new ArrayList<>(selectedSeats);
    }

    /**
     * Main method for testing the BookSeatsPanel.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Book Seats - BOG to LIM");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 700);
            frame.setLocationRelativeTo(null);

            BookSeatsPanel bookSeatsPanel = new BookSeatsPanel();
            
            // Example of how an external listener (like the Controller) would work
            bookSeatsPanel.addActionListener(e -> {
                String command = e.getActionCommand();
                if (command != null && command.startsWith(View.SELECT_SEAT)) {
                    System.out.println("Seat action detected: " + command);
                    // The internal listener already handles the color toggle,
                    // but the main controller would handle other logic here.
                } else {
                    System.out.println("Action detected: " + command);
                }
            });

            frame.add(bookSeatsPanel);
            frame.setVisible(true);
        });
    }
}