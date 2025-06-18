package org.airflow.reservations.GUI;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.BorderFactory; // Added for EmptyBorder
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.FlowLayout; // Added for button panel

public class BookingPanel extends JPanel {

    private JLabel passengerNameLabel;
    private JLabel passengerEmailLabel;
    private JLabel seatSelectionLabel;
    private JTextField passengerNameTextField;
    private JTextField passengerEmailTextField;
    private JTextField seatSelectionTextField;
    private JButton confirmBookingButton;

    public BookingPanel() {
        // Initialize UI components
        passengerNameLabel = new JLabel("Passenger Name:");
        passengerEmailLabel = new JLabel("Passenger Email:");
        seatSelectionLabel = new JLabel("Seat Selection (e.g., 12A):");

        passengerNameTextField = new JTextField(20);
        passengerEmailTextField = new JTextField(20);
        seatSelectionTextField = new JTextField(5);

        confirmBookingButton = new JButton("Confirm Booking");
        confirmBookingButton.setActionCommand("confirmBooking");

        // Layout
        setLayout(new BorderLayout(10, 10)); // Increased gaps

        // Input panel using GridLayout
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10)); // Increased gaps
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Added padding
        inputPanel.add(passengerNameLabel);
        inputPanel.add(passengerNameTextField);
        inputPanel.add(passengerEmailLabel);
        inputPanel.add(passengerEmailTextField);
        inputPanel.add(seatSelectionLabel);
        inputPanel.add(seatSelectionTextField);

        // Add components to the main panel
        add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Panel for the button
        buttonPanel.add(confirmBookingButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0)); // Padding for button panel
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Getter methods
    public JTextField getPassengerNameTextField() {
        return passengerNameTextField;
    }

    public JTextField getPassengerEmailTextField() {
        return passengerEmailTextField;
    }

    public JTextField getSeatSelectionTextField() {
        return seatSelectionTextField;
    }

    public JButton getConfirmBookingButton() {
        return confirmBookingButton;
    }
}
