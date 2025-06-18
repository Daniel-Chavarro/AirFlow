package org.airflow.reservations.GUI;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.table.DefaultTableModel;
import javax.swing.BorderFactory; // Added for EmptyBorder
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Dimension; // Added for preferred size

public class ReservationListPanel extends JPanel {

    private JTable reservationTable;
    private JScrollPane tableScrollPane;
    private DefaultTableModel tableModel;
    private JButton cancelReservationButton;
    private JButton modifyReservationButton;
    private JPanel buttonPanel;

    public ReservationListPanel() {
        // Initialize UI components
        String[] columnNames = {"Reservation ID", "Flight Number", "Passenger Name", "Seat", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0); // 0 rows initially
        reservationTable = new JTable(tableModel);
        tableScrollPane = new JScrollPane(reservationTable);

        cancelReservationButton = new JButton("Cancel Reservation");
        cancelReservationButton.setActionCommand("cancelReservation");

        modifyReservationButton = new JButton("Modify Reservation");
        modifyReservationButton.setActionCommand("modifyReservation");

        // Button panel
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Increased gaps for buttons
        buttonPanel.add(modifyReservationButton);
        buttonPanel.add(cancelReservationButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0)); // Padding for button panel


        // Layout
        setLayout(new BorderLayout(10, 10)); // Increased gaps for overall panel layout
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding for the whole panel

        // Add components to the main panel
        // Ensure table takes up available space
        tableScrollPane.setPreferredSize(new Dimension(750, 450));
        add(tableScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Getter methods
    public JTable getReservationTable() {
        return reservationTable;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JButton getCancelReservationButton() {
        return cancelReservationButton;
    }

    public JButton getModifyReservationButton() {
        return modifyReservationButton;
    }
}
