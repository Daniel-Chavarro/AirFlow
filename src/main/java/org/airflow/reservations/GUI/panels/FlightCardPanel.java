package org.airflow.reservations.GUI.panels;

import org.airflow.reservations.GUI.Bridge.View;
import org.airflow.reservations.model.City;
import org.airflow.reservations.model.Flight;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

/**
 * A panel that displays a summary of a flight as a card.
 * It includes the flight route, times, duration, price, and a button to view details.
 */
public class FlightCardPanel extends JPanel {
    /**
     * Formatter for displaying time in HH:mm a format.
     */
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");

    /**
     * The flight data associated with this card.
     */
    private Flight flight;

    /**
     * The button to trigger viewing the detailed information of the flight.
     */
    private JButton viewDetailsButton;

    /**
     * Constructs a new FlightCardPanel.
     *
     * @param flight          The flight to display.
     * @param originCity      The origin city of the flight.
     * @param destinationCity The destination city of the flight.
     */
    public FlightCardPanel(Flight flight, City originCity, City destinationCity) {
        this.flight = flight;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        setBackground(Color.WHITE);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        // Panel for flight info
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Route and Times
        JLabel routeLabel = new JLabel(String.format("%s → %s", originCity.getCode(), destinationCity.getCode()));
        routeLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        infoPanel.add(routeLabel, gbc);

        JLabel timeLabel = new JLabel(String.format("%s - %s",
                flight.getDeparture_time().format(TIME_FORMATTER),
                flight.getArrival_time().format(TIME_FORMATTER)));
        timeLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridy = 1;
        infoPanel.add(timeLabel, gbc);

        // Duration
        JLabel durationLabel = new JLabel(String.format("Duration: %dh %dm",
                flight.getScheduledDuration().toHours(),
                flight.getScheduledDuration().toMinutesPart()));
        durationLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        gbc.gridy = 2;
        infoPanel.add(durationLabel, gbc);

        // Panel for price and button
        JPanel actionPanel = new JPanel(new BorderLayout(10, 5));
        actionPanel.setBackground(Color.WHITE);

        // Price
        JLabel priceLabel = new JLabel(String.format("$%.2f", flight.getPrice_base()));
        priceLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        priceLabel.setForeground(new Color(0, 102, 204));
        priceLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        actionPanel.add(priceLabel, BorderLayout.NORTH);

        // View Details Button
        viewDetailsButton = new JButton("View Details");
        viewDetailsButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        viewDetailsButton.setBackground(new Color(0, 122, 255));
        viewDetailsButton.setForeground(Color.WHITE);
        viewDetailsButton.setFocusPainted(false);
        viewDetailsButton.setActionCommand(View.DETAILS_FLIGHT_CMD + "_" + flight.getId());
        actionPanel.add(viewDetailsButton, BorderLayout.SOUTH);

        add(infoPanel, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.EAST);
    }

    /**
     * Returns the button used to view flight details.
     *
     * @return The "View Details" button.
     */
    public JButton getViewDetailsButton() {
        return viewDetailsButton;
    }

    /**
     * Returns the flight associated with this card.
     *
     * @return The flight data object.
     */
    public Flight getFlight() {
        return flight;
    }

    /**
     * Sets the flight associated with this card.
     *
     * @param flight The flight data object to associate with this card.
     */
    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    /**
     * Sets the view details button for this card.
     *
     * @param viewDetailsButton The button to use for viewing flight details.
     */
    public void setViewDetailsButton(JButton viewDetailsButton) {
        this.viewDetailsButton = viewDetailsButton;
    }
}