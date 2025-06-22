package org.airflow.reservations.GUI.panels;

import javax.swing.*;
import java.awt.*;


import com.formdev.flatlaf.FlatLightLaf;
import com.github.lgooddatepicker.components.DatePicker;


public class SearchFlightPanel extends JPanel {

    private JPanel searchFlightPanel;
    private JPanel formPanel;
    private JPanel resultsPanel;

    private JLabel bookFlightLabel;

    private JComboBox<String> originComboBox;
    private JComboBox<String> destinationComboBox;

    private DatePicker departureDatePicker;
    private DatePicker returnDatePicker;

    private JButton searchButton;

    public SearchFlightPanel() {
        // Initialize the main panel
        setLayout(new BorderLayout());
        searchFlightPanel = new JPanel();
        searchFlightPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        searchFlightPanel.setLayout(new BoxLayout(searchFlightPanel, BoxLayout.Y_AXIS));
        searchFlightPanel.setBackground(Color.WHITE);

        // Start the form panel
        startFormPanel();
        // Start the results panel
        startResultsPanel();


        // Set the main panel as the viewport view for the JScrollPane
        add(searchFlightPanel);
    }

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
        originComboBox = new JComboBox<>(new String[]{"Select Origin", "New York", "Los Angeles", "Chicago"});
        originComboBox.setPreferredSize(inputSize);
        originComboBox.setMaximumSize(inputSize);
        originComboBox.setBackground(new Color(240, 242, 245));
        originComboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        originComboBox.setBorder(BorderFactory.createEmptyBorder());
        gbc.gridy++;
        formPanel.add(originComboBox, gbc);

        // Destination
        destinationComboBox = new JComboBox<>(new String[]{"Select Destination", "Miami", "San Francisco", "Seattle"});
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
        searchButton.setActionCommand("SEARCH_FLIGHTS   ");
        gbc.gridy++;
        formPanel.add(searchButton, gbc);


        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        searchFlightPanel.add(formPanel);
    }

    public void startResultsPanel() {
        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BorderLayout());
        resultsPanel.setBackground(Color.WHITE);

        // Add a placeholder label for results
        JLabel resultsLabel = new JLabel("Search Results will appear here");
        resultsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        resultsLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        resultsPanel.add(resultsLabel, BorderLayout.CENTER);

        // Add some space between the form and results panel
        searchFlightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        searchFlightPanel.add(resultsPanel);

    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf");
        }

        JFrame frame = new JFrame("Search Flight Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());
        SearchFlightPanel searchFlightPanel = new SearchFlightPanel();
        frame.add(searchFlightPanel, BorderLayout.CENTER);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null); // Center the frame on the screen
        searchFlightPanel.setVisible(true);
    }
}
