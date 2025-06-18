package org.airflow.reservations.GUI;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.BorderFactory; // Added for EmptyBorder
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Dimension; // Added for preferred size

public class FlightSearchPanel extends JPanel {

    private JLabel originLabel;
    private JLabel destinationLabel;
    private JLabel dateLabel;
    private JTextField originTextField;
    private JTextField destinationTextField;
    private JTextField dateTextField;
    private JButton searchButton;
    private JTable resultsTable;
    private JScrollPane tableScrollPane;
    private DefaultTableModel tableModel;

    public FlightSearchPanel() {
        // Initialize UI components
        originLabel = new JLabel("Origin:");
        destinationLabel = new JLabel("Destination:");
        dateLabel = new JLabel("Date (YYYY-MM-DD):");

        originTextField = new JTextField(20);
        destinationTextField = new JTextField(20);
        dateTextField = new JTextField(10);

        searchButton = new JButton("Search Flights");
        searchButton.setActionCommand("searchFlights");

        // Setup table model and table
        String[] columnNames = {"Flight Number", "Origin", "Destination", "Departure Time", "Arrival Time", "Price"};
        tableModel = new DefaultTableModel(columnNames, 0); // 0 rows initially
        resultsTable = new JTable(tableModel);
        tableScrollPane = new JScrollPane(resultsTable);

        // Layout
        setLayout(new BorderLayout(10, 10)); // Increased gaps for overall panel layout

        // Input panel using GridLayout
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10)); // Increased gaps for inputs
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Added padding
        inputPanel.add(originLabel);
        inputPanel.add(originTextField);
        inputPanel.add(destinationLabel);
        inputPanel.add(destinationTextField);
        inputPanel.add(dateLabel);
        inputPanel.add(dateTextField);

        // Add components to the main panel
        add(inputPanel, BorderLayout.NORTH);
        // Ensure table takes up available space
        tableScrollPane.setPreferredSize(new Dimension(750, 400));
        add(tableScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Panel for the button
        buttonPanel.add(searchButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0)); // Padding for button panel
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Getter methods for components that might need to be accessed externally (e.g., for adding listeners)
    public JTextField getOriginTextField() {
        return originTextField;
    }

    public JTextField getDestinationTextField() {
        return destinationTextField;
    }

    public JTextField getDateTextField() {
        return dateTextField;
    }

    public JButton getSearchButton() {
        return searchButton;
    }

    public JTable getResultsTable() {
        return resultsTable;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }
}
