package org.airflow.reservations.GUI.panels;

import com.formdev.flatlaf.FlatLightLaf;
import com.github.lgooddatepicker.components.DatePicker;
import org.airflow.reservations.GUI.Bridge.View;
import org.airflow.reservations.model.City;
import org.airflow.reservations.model.Flight;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * SearchFlightPanel class provides a panel for searching flights.
 * It includes input fields for origin, destination, departure and return dates,
 * and a search button. The results panel is also included to display search results.
 */
public class SearchFlightPanel extends JPanel {

    /** The main panel for the search flight screen. */
    private JPanel searchFlightPanel;
    /** The panel containing the search form. */
    private JPanel formPanel;
    /** The panel to display search results. */
    private JPanel resultsPanel;
    /** The panel that holds the flight cards. */
    private JPanel cardsPanel;
    /** The label for the book flight section. */
    private JLabel bookFlightLabel;
    /** The combo box for selecting the origin city. */
    private JComboBox<String> originComboBox;
    /** The combo box for selecting the destination city. */
    private JComboBox<String> destinationComboBox;
    /** The date picker for selecting the departure date. */
    private DatePicker departureDatePicker;
    /** The date picker for selecting the return date. */
    private DatePicker returnDatePicker;
    /** The button to initiate the flight search. */
    private JButton searchButton;

    /**
     * Constructor for the SearchFlightPanel class.
     * Initializes the panel with components for searching flights.
     */
    public SearchFlightPanel() {
        // Initialize the main panel
        setLayout(new BorderLayout());
        searchFlightPanel = new JPanel();
        searchFlightPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        searchFlightPanel.setLayout(new BoxLayout(searchFlightPanel, BoxLayout.Y_AXIS));
        searchFlightPanel.setBackground(Color.WHITE);

        // Start the form panel
        startFormPanel();
        // Start the results panel
        startResultsPanel();


        // Set the main panel as the viewport view for the JScrollPane
        add(searchFlightPanel);
    }

    /**
     * Sets the cities data for the origin and destination combo boxes.
     * This method should be called from the Controller to populate the dropdowns.
     *
     * @param cities The list of cities to populate the combo boxes with
     */
    public void setCitiesData(ArrayList<City> cities) {
        // Clear existing items
        originComboBox.removeAllItems();
        destinationComboBox.removeAllItems();

        // Add select options
        originComboBox.addItem("Select Origin");
        destinationComboBox.addItem("Select Destination");

        // Add cities from database
        if (cities != null) {
            for (City city : cities) {
                originComboBox.addItem(city.getName());
                destinationComboBox.addItem(city.getName());
            }
        }

        // Refresh the UI
        originComboBox.revalidate();
        destinationComboBox.revalidate();
    }

    /**
     * Initializes the form panel with input fields and a search button.
     * The form includes fields for origin, destination, departure date,
     * return date, and a search button.
     */
    public void startFormPanel(){
        formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Dimension inputSize = new Dimension(400, 40);

        // Title
        bookFlightLabel = new JLabel("Book a Flight");
        bookFlightLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        gbc.gridy = 0;
        formPanel.add(bookFlightLabel, gbc);

        // Origin
        originComboBox = new JComboBox<>();
        originComboBox.setPreferredSize(inputSize);
        originComboBox.setMaximumSize(inputSize);
        originComboBox.setBackground(new Color(240, 242, 245));
        originComboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        originComboBox.setBorder(BorderFactory.createEmptyBorder());
        gbc.gridy++;
        formPanel.add(originComboBox, gbc);

        // Destination
        destinationComboBox = new JComboBox<>();
        destinationComboBox.setPreferredSize(inputSize);
        destinationComboBox.setMaximumSize(inputSize);
        destinationComboBox.setBackground(new Color(240, 242, 245));
        destinationComboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        destinationComboBox.setBorder(BorderFactory.createEmptyBorder());
        gbc.gridy++;
        formPanel.add(destinationComboBox, gbc);

        // Departure
        departureDatePicker = new DatePicker();
        departureDatePicker.setDateToToday();
        departureDatePicker.setFont(new Font("SansSerif", Font.PLAIN, 14));
        departureDatePicker.setBackground(new Color(240, 242, 245));
        departureDatePicker.setPreferredSize(inputSize);
        departureDatePicker.setMaximumSize(inputSize);
        gbc.gridy++;
        formPanel.add(departureDatePicker, gbc);

        // Return
        returnDatePicker = new DatePicker();
        returnDatePicker.setDateToToday();
        returnDatePicker.setFont(new Font("SansSerif", Font.PLAIN, 14));
        returnDatePicker.setBackground(new Color(240, 242, 245));
        returnDatePicker.setPreferredSize(inputSize);
        returnDatePicker.setMaximumSize(inputSize);
        gbc.gridy++;
        formPanel.add(returnDatePicker, gbc);

        // Search button
        searchButton = new JButton("Search");
        searchButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        searchButton.setBackground(new Color(0, 122, 255));
        searchButton.setForeground(Color.WHITE);
        searchButton.setPreferredSize(inputSize);
        searchButton.setMaximumSize(inputSize);
        searchButton.setActionCommand(View.SEARCH_FLIGHT_CMD);
        gbc.gridy++;
        formPanel.add(searchButton, gbc);


        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        searchFlightPanel.add(formPanel);
    }

    /**
     * Initializes the results panel where search results will be displayed.
     * Currently, it contains a placeholder label indicating where results will appear.
     */
    public void startResultsPanel() {
        resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBackground(Color.WHITE);
        resultsPanel.setBorder(BorderFactory.createTitledBorder("Available Flights"));

        cardsPanel = new JPanel();
        cardsPanel.setLayout(new BoxLayout(cardsPanel, BoxLayout.Y_AXIS));
        cardsPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);

        resultsPanel.add(scrollPane, BorderLayout.CENTER);

        searchFlightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        searchFlightPanel.add(resultsPanel);
    }

    /**
     * Displays the list of flights as cards in the results panel.
     *
     * @param flights     The list of flights to display.
     * @param origin      The origin city.
     * @param destination The destination city.
     * @param listener    The action listener for the "View Details" buttons.
     */
    public void displayFlights(ArrayList<Flight> flights, City origin, City destination, ActionListener listener) {
        cardsPanel.removeAll();
        if (flights == null || flights.isEmpty()) {
            JLabel noResultsLabel = new JLabel("No flights found for the selected criteria.");
            noResultsLabel.setHorizontalAlignment(SwingConstants.CENTER);
            noResultsLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
            cardsPanel.add(noResultsLabel);
        } else {
            for (Flight flight : flights) {
                FlightCardPanel card = new FlightCardPanel(flight, origin, destination);
                card.getViewDetailsButton().addActionListener(listener);
                cardsPanel.add(card);
                cardsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }


    /**
     * The main method for testing the SearchFlightPanel.
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf");
        }

        JFrame frame = new JFrame("Search Flight Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 800);
        frame.setLayout(new BorderLayout());
        SearchFlightPanel searchFlightPanel = new SearchFlightPanel();
        frame.add(searchFlightPanel, BorderLayout.CENTER);


        // Create fake data for testing
        City origin = new City();
        origin.setCode("JFK");
        origin.setName("New York");

        City destination = new City();
        destination.setCode("LAX");
        destination.setName("Los Angeles");

        ArrayList<Flight> flights = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Flight flight = new Flight();
            flight.setId(i);
            flight.setPrice_base(250.00f + (i * 20));
            flight.setDeparture_time(java.time.LocalDateTime.now().plusHours(i * 2));
            flight.setArrival_time(java.time.LocalDateTime.now().plusHours((i * 2) + 5));
            flights.add(flight);
        }

        // Mock ActionListener
        ActionListener listener = e -> {
            String command = e.getActionCommand();
            JOptionPane.showMessageDialog(frame, "Action Command: " + command);
        };

        // Display the flights
        searchFlightPanel.displayFlights(flights, origin, destination, listener);

        frame.setVisible(true);
        frame.setLocationRelativeTo(null); // Center the frame on the screen
        searchFlightPanel.setVisible(true);
    }

    /**
     * Gets the main search flight panel.
     * @return The search flight panel.
     */
    public JPanel getSearchFlightPanel() {
        return searchFlightPanel;
    }

    /**
     * Sets the main search flight panel.
     * @param searchFlightPanel The search flight panel.
     */
    public void setSearchFlightPanel(JPanel searchFlightPanel) {
        this.searchFlightPanel = searchFlightPanel;
    }

    /**
     * Gets the form panel.
     * @return The form panel.
     */
    public JPanel getFormPanel() {
        return formPanel;
    }

    /**
     * Sets the form panel.
     * @param formPanel The form panel.
     */
    public void setFormPanel(JPanel formPanel) {
        this.formPanel = formPanel;
    }

    /**
     * Gets the results panel.
     * @return The results panel.
     */
    public JPanel getResultsPanel() {
        return resultsPanel;
    }

    /**
     * Sets the results panel.
     * @param resultsPanel The results panel.
     */
    public void setResultsPanel(JPanel resultsPanel) {
        this.resultsPanel = resultsPanel;
    }

    /**
     * Gets the book flight label.
     * @return The book flight label.
     */
    public JLabel getBookFlightLabel() {
        return bookFlightLabel;
    }

    /**
     * Sets the book flight label.
     * @param bookFlightLabel The book flight label.
     */
    public void setBookFlightLabel(JLabel bookFlightLabel) {
        this.bookFlightLabel = bookFlightLabel;
    }

    /**
     * Gets the origin combo box.
     * @return The origin combo box.
     */
    public JComboBox<String> getOriginComboBox() {
        return originComboBox;
    }

    /**
     * Sets the origin combo box.
     * @param originComboBox The origin combo box.
     */
    public void setOriginComboBox(JComboBox<String> originComboBox) {
        this.originComboBox = originComboBox;
    }

    /**
     * Gets the destination combo box.
     * @return The destination combo box.
     */
    public JComboBox<String> getDestinationComboBox() {
        return destinationComboBox;
    }

    /**
     * Sets the destination combo box.
     * @param destinationComboBox The destination combo box.
     */
    public void setDestinationComboBox(JComboBox<String> destinationComboBox) {
        this.destinationComboBox = destinationComboBox;
    }

    /**
     * Gets the departure date picker.
     * @return The departure date picker.
     */
    public DatePicker getDepartureDatePicker() {
        return departureDatePicker;
    }

    /**
     * Sets the departure date picker.
     * @param departureDatePicker The departure date picker.
     */
    public void setDepartureDatePicker(DatePicker departureDatePicker) {
        this.departureDatePicker = departureDatePicker;
    }

    /**
     * Gets the return date picker.
     * @return The return date picker.
     */
    public DatePicker getReturnDatePicker() {
        return returnDatePicker;
    }

    /**
     * Sets the return date picker.
     * @param returnDatePicker The return date picker.
     */
    public void setReturnDatePicker(DatePicker returnDatePicker) {
        this.returnDatePicker = returnDatePicker;
    }

    /**
     * Gets the search button.
     * @return The search button.
     */
    public JButton getSearchButton() {
        return searchButton;
    }

    /**
     * Sets the search button.
     * @param searchButton The search button.
     */
    public void setSearchButton(JButton searchButton) {
        this.searchButton = searchButton;
    }
}
