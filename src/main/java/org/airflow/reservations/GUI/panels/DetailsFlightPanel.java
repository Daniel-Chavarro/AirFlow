package org.airflow.reservations.GUI.panels;

import org.airflow.reservations.model.Airplane;
import org.airflow.reservations.model.City;
import org.airflow.reservations.model.Flight;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

public class DetailsFlightPanel extends JPanel {
    private final JPanel detailsFlightPanel;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
    private JPanel detailsPanel;
    private JPanel buttonsPanel;
    private JPanel flightInfoPanel;
    private JPanel baggagePanel;
    private JLabel flightDetailsLabel;
    private JLabel baggageAllowanceLabel;
    private JLabel nonstopLabel;
    private JLabel flightCodeLabel;
    private JLabel statusLabel;
    private JLabel airplaneLabel;
    private JLabel routeLabel;
    private JLabel timeLabel;
    private JLabel durationLabel;
    private JLabel dateLabel;
    private JButton backButton;
    private JButton continueButton;
    private Flight flight;
    private City originCity;
    private City destinationCity;
    private Airplane airplane;

    public DetailsFlightPanel() {
        setLayout(new BorderLayout());
        detailsFlightPanel = new JPanel();
        detailsFlightPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        detailsFlightPanel.setLayout(new BoxLayout(detailsFlightPanel, BoxLayout.Y_AXIS));
        detailsFlightPanel.setBackground(Color.WHITE);
    }

    public void setData(Flight flight, City originCity, City destinationCity, Airplane airplane) {
        this.flight = flight;
        this.originCity = originCity;
        this.destinationCity = destinationCity;
        this.airplane = airplane;

        startDetailsPanel();
        createFlightInfoPanel();
        createBaggageAllowancePanel();
        createButtonsPanel();

        detailsFlightPanel.add(detailsPanel);
        detailsFlightPanel.add(flightInfoPanel);
        detailsFlightPanel.add(baggagePanel);
        detailsFlightPanel.add(buttonsPanel);

        add(detailsFlightPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public void startDetailsPanel() {
        detailsPanel = new JPanel();
        detailsPanel.setLayout(new GridBagLayout());
        detailsPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 0;

        flightDetailsLabel = new JLabel("Flight Details");
        flightDetailsLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        detailsPanel.add(flightDetailsLabel, gbc);
    }

    private void createFlightInfoPanel() {
        flightInfoPanel = new JPanel();
        flightInfoPanel.setLayout(new GridBagLayout());
        flightInfoPanel.setBackground(Color.WHITE);
        flightInfoPanel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Flight Info Label
        JLabel flightInfoLabel = new JLabel("Flight Information");
        flightInfoLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        flightInfoPanel.add(flightInfoLabel, gbc);

        // Flight Code
        gbc.gridy++;
        flightCodeLabel = new JLabel("Flight: " + flight.getCode());
        flightCodeLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        flightInfoPanel.add(flightCodeLabel, gbc);

        // Route
        gbc.gridy++;
        routeLabel = new JLabel(originCity.getName() + " (" + originCity.getCode() + ") to " +
                destinationCity.getName() + " (" + destinationCity.getCode() + ")");
        routeLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        flightInfoPanel.add(routeLabel, gbc);

        // Nonstop Label
        gbc.gridy++;
        nonstopLabel = new JLabel("Nonstop");
        nonstopLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        flightInfoPanel.add(nonstopLabel, gbc);

        // Flight Duration
        gbc.gridy++;
        Duration duration = flight.getScheduledDuration();
        durationLabel = new JLabel("Duration: " + duration.toHours() + "h " + (duration.toMinutesPart()) + "m");
        durationLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        flightInfoPanel.add(durationLabel, gbc);

        // Departure and Arrival Times
        gbc.gridy++;
        timeLabel = new JLabel("Time: " + flight.getDeparture_time().format(timeFormatter) + " - " +
                flight.getScheduled_arrival_time().format(timeFormatter));
        timeLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        flightInfoPanel.add(timeLabel, gbc);

        // Date
        gbc.gridy++;
        dateLabel = new JLabel("Date: " + flight.getDeparture_time().format(dateFormatter));
        dateLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        flightInfoPanel.add(dateLabel, gbc);

        // Airplane Details
        gbc.gridy++;
        airplaneLabel = new JLabel("Aircraft: " + airplane.getAirline() + " " + airplane.getModel());
        airplaneLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        flightInfoPanel.add(airplaneLabel, gbc);

        // Status
        gbc.gridy++;
        statusLabel = new JLabel("Status: " + flight.getStatus_name());
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        flightInfoPanel.add(statusLabel, gbc);

        // Price
        gbc.gridy++;
        JLabel priceLabel = new JLabel(String.format("Price: $%.2f", flight.getPrice_base()));
        priceLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        flightInfoPanel.add(priceLabel, gbc);
    }

    private void createBaggageAllowancePanel() {
        baggagePanel = new JPanel();
        baggagePanel.setLayout(new GridBagLayout());
        baggagePanel.setBackground(Color.WHITE);
        baggagePanel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Baggage Allowance Label
        baggageAllowanceLabel = new JLabel("Baggage Allowance");
        baggageAllowanceLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        baggagePanel.add(baggageAllowanceLabel, gbc);

        // Personal Item
        gbc.gridy++;
        JLabel personalItemLabel = new JLabel("1 personal item");
        personalItemLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        baggagePanel.add(personalItemLabel, gbc);

        // Carry-on Bag
        gbc.gridy++;
        JLabel carryOnLabel = new JLabel("1 carry-on bag");
        carryOnLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        baggagePanel.add(carryOnLabel, gbc);

        // Checked Baggage
        gbc.gridy++;
        JLabel checkedBaggageLabel = new JLabel("Checked baggage not included");
        checkedBaggageLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        baggagePanel.add(checkedBaggageLabel, gbc);
    }

    private void createButtonsPanel() {
        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.setBackground(Color.WHITE);

        // Back Button
        backButton = new JButton("Back");
        backButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        backButton.setBackground(new Color(240, 240, 240));
        backButton.setForeground(Color.BLACK);
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setPreferredSize(new Dimension(120, 40));
        buttonsPanel.add(backButton);

        // Continue to Book Button (similar to the one in the image)
        continueButton = new JButton("Continue to Book");
        continueButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        continueButton.setBackground(new Color(0, 123, 255));
        continueButton.setForeground(Color.WHITE);
        continueButton.setFocusPainted(false);
        continueButton.setBorderPainted(false);
        continueButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        continueButton.setPreferredSize(new Dimension(180, 40));
        buttonsPanel.add(continueButton);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Flight Details");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 800);

        DetailsFlightPanel detailsFlightPanel = new DetailsFlightPanel();
        Flight flight = new Flight(); // Assume this is populated with flight data
        City originCity = new City();
        originCity.setName("New York");
        originCity.setCode("NYC");
        City destinationCity = new City(); // Example city
        destinationCity.setName("Los Angeles");
        destinationCity.setCode("LAX");
        Airplane airplane = new Airplane();
        airplane.setAirline("Airline Example");
        airplane.setModel("Model X");


        detailsFlightPanel.setData(flight, originCity, destinationCity, airplane);
        frame.add(detailsFlightPanel);
        frame.setVisible(true);
    }

    public JButton getBackButton() {
        return backButton;
    }
}
