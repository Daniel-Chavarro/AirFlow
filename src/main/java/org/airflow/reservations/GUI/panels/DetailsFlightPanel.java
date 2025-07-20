package org.airflow.reservations.GUI.panels;

import org.airflow.reservations.GUI.Bridge.View;
import org.airflow.reservations.model.Airplane;
import org.airflow.reservations.model.City;
import org.airflow.reservations.model.Flight;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

/**
 * A panel that displays detailed information about a specific flight.
 * It includes flight route, times, duration, aircraft details, baggage allowance, and action buttons.
 */
public class DetailsFlightPanel extends JPanel {
    /** Formatter for displaying dates in "MMM dd, yyyy" format. */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    /** Formatter for displaying time in "hh:mm a" format. */
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");
    /** The main panel holding all components. */
    private JPanel detailsFlightPanel;
    /** The panel for the main flight details. */
    private JPanel detailsPanel;
    /** The panel for action buttons. */
    private JPanel buttonsPanel;
    /** The panel for flight information. */
    private JPanel flightInfoPanel;
    /** The panel for baggage allowance information. */
    private JPanel baggagePanel;
    /** The label for the flight details section. */
    private JLabel flightDetailsLabel;
    /** The label for the baggage allowance section. */
    private JLabel baggageAllowanceLabel;
    /** The label indicating if the flight is nonstop. */
    private JLabel nonstopLabel;
    /** The label for the flight code. */
    private JLabel flightCodeLabel;
    /** The label for the flight status. */
    private JLabel statusLabel;
    /** The label for the airplane details. */
    private JLabel airplaneLabel;
    /** The label for the flight route. */
    private JLabel routeLabel;
    /** The label for the departure and arrival times. */
    private JLabel timeLabel;
    /** The label for the flight duration. */
    private JLabel durationLabel;
    /** The label for the flight date. */
    private JLabel dateLabel;
    /** The button to go back to the previous screen. */
    private JButton backButton;
    /** The button to proceed with booking the flight. */
    private JButton continueButton;
    /** The flight data object. */
    private Flight flight;
    /** The origin city of the flight. */
    private City originCity;
    /** The destination city of the flight. */
    private City destinationCity;
    /** The airplane for the flight. */
    private Airplane airplane;

    /**
     * Constructs a new DetailsFlightPanel.
     */
    public DetailsFlightPanel() {
        setLayout(new BorderLayout());
        detailsFlightPanel = new JPanel();
        detailsFlightPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        detailsFlightPanel.setLayout(new BoxLayout(detailsFlightPanel, BoxLayout.Y_AXIS));
        detailsFlightPanel.setBackground(Color.WHITE);
    }

    /**
     * Sets the data for the panel and rebuilds the UI.
     *
     * @param flight          The flight to display.
     * @param originCity      The origin city.
     * @param destinationCity The destination city.
     * @param airplane        The airplane for the flight.
     */
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

    /**
     * Initializes the top details panel with a title.
     */
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


    /**
     * Creates the flight information panel that displays detailed flight information.
     * This includes flight code, route, nonstop status, duration, times, date, airplane details, and price.
     */
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
        timeLabel = new JLabel("Time: " + flight.getDeparture_time().format(TIME_FORMATTER) + " - " +
                flight.getScheduled_arrival_time().format(TIME_FORMATTER));
        timeLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        flightInfoPanel.add(timeLabel, gbc);

        // Date
        gbc.gridy++;
        dateLabel = new JLabel("Date: " + flight.getDeparture_time().format(DATE_FORMATTER));
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

    /**
     * Creates the baggage allowance panel.
     */
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

    /**
     * Creates the panel with action buttons.
     */
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
        backButton.setActionCommand(View.BACK_TO_FLIGHTS_CMD);
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
        continueButton.setActionCommand(View.BOOK_SEAT_CMD);
        buttonsPanel.add(continueButton);
    }

    /**
     * Main method for testing the DetailsFlightPanel.
     * @param args Command line arguments.
     */
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

    /**
     * Gets the back button.
     * @return The back button.
     */
    public JButton getBackButton() {
        return backButton;
    }


    /**
     * Gets the main details flight panel.
     * @return The details flight panel.
     */
    public JPanel getDetailsFlightPanel() {
        return detailsFlightPanel;
    }

    /**
     * Gets the date formatter.
     * @return The date formatter.
     */
    public DateTimeFormatter getDateFormatter() {
        return DATE_FORMATTER;
    }

    /**
     * Gets the time formatter.
     * @return The time formatter.
     */
    public DateTimeFormatter getTimeFormatter() {
        return TIME_FORMATTER;
    }

    /**
     * Gets the details panel.
     * @return The details panel.
     */
    public JPanel getDetailsPanel() {
        return detailsPanel;
    }

    /**
     * Sets the details panel.
     * @param detailsPanel The details panel.
     */
    public void setDetailsPanel(JPanel detailsPanel) {
        this.detailsPanel = detailsPanel;
    }

    /**
     * Gets the buttons panel.
     * @return The buttons panel.
     */
    public JPanel getButtonsPanel() {
        return buttonsPanel;
    }

    /**
     * Sets the buttons panel.
     * @param buttonsPanel The buttons panel.
     */
    public void setButtonsPanel(JPanel buttonsPanel) {
        this.buttonsPanel = buttonsPanel;
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
     * @param flightInfoPanel The flight info panel.
     */
    public void setFlightInfoPanel(JPanel flightInfoPanel) {
        this.flightInfoPanel = flightInfoPanel;
    }

    /**
     * Gets the baggage panel.
     * @return The baggage panel.
     */
    public JPanel getBaggagePanel() {
        return baggagePanel;
    }

    /**
     * Sets the baggage panel.
     * @param baggagePanel The baggage panel.
     */
    public void setBaggagePanel(JPanel baggagePanel) {
        this.baggagePanel = baggagePanel;
    }

    /**
     * Gets the flight details label.
     * @return The flight details label.
     */
    public JLabel getFlightDetailsLabel() {
        return flightDetailsLabel;
    }

    /**
     * Sets the flight details label.
     * @param flightDetailsLabel The flight details label.
     */
    public void setFlightDetailsLabel(JLabel flightDetailsLabel) {
        this.flightDetailsLabel = flightDetailsLabel;
    }

    /**
     * Gets the baggage allowance label.
     * @return The baggage allowance label.
     */
    public JLabel getBaggageAllowanceLabel() {
        return baggageAllowanceLabel;
    }

    /**
     * Sets the baggage allowance label.
     * @param baggageAllowanceLabel The baggage allowance label.
     */
    public void setBaggageAllowanceLabel(JLabel baggageAllowanceLabel) {
        this.baggageAllowanceLabel = baggageAllowanceLabel;
    }

    /**
     * Gets the nonstop label.
     * @return The nonstop label.
     */
    public JLabel getNonstopLabel() {
        return nonstopLabel;
    }

    /**
     * Sets the nonstop label.
     * @param nonstopLabel The nonstop label.
     */
    public void setNonstopLabel(JLabel nonstopLabel) {
        this.nonstopLabel = nonstopLabel;
    }

    /**
     * Gets the flight code label.
     * @return The flight code label.
     */
    public JLabel getFlightCodeLabel() {
        return flightCodeLabel;
    }

    /**
     * Sets the flight code label.
     * @param flightCodeLabel The flight code label.
     */
    public void setFlightCodeLabel(JLabel flightCodeLabel) {
        this.flightCodeLabel = flightCodeLabel;
    }

    /**
     * Gets the status label.
     * @return The status label.
     */
    public JLabel getStatusLabel() {
        return statusLabel;
    }

    /**
     * Sets the status label.
     * @param statusLabel The status label.
     */
    public void setStatusLabel(JLabel statusLabel) {
        this.statusLabel = statusLabel;
    }

    /**
     * Gets the airplane label.
     * @return The airplane label.
     */
    public JLabel getAirplaneLabel() {
        return airplaneLabel;
    }

    /**
     * Sets the airplane label.
     * @param airplaneLabel The airplane label.
     */
    public void setAirplaneLabel(JLabel airplaneLabel) {
        this.airplaneLabel = airplaneLabel;
    }

    /**
     * Gets the route label.
     * @return The route label.
     */
    public JLabel getRouteLabel() {
        return routeLabel;
    }

    /**
     * Sets the route label.
     * @param routeLabel The route label.
     */
    public void setRouteLabel(JLabel routeLabel) {
        this.routeLabel = routeLabel;
    }

    /**
     * Gets the time label.
     * @return The time label.
     */
    public JLabel getTimeLabel() {
        return timeLabel;
    }

    /**
     * Sets the time label.
     * @param timeLabel The time label.
     */
    public void setTimeLabel(JLabel timeLabel) {
        this.timeLabel = timeLabel;
    }

    /**
     * Gets the duration label.
     * @return The duration label.
     */
    public JLabel getDurationLabel() {
        return durationLabel;
    }

    /**
     * Sets the duration label.
     * @param durationLabel The duration label.
     */
    public void setDurationLabel(JLabel durationLabel) {
        this.durationLabel = durationLabel;
    }

    /**
     * Gets the date label.
     * @return The date label.
     */
    public JLabel getDateLabel() {
        return dateLabel;
    }

    /**
     * Sets the date label.
     * @param dateLabel The date label.
     */
    public void setDateLabel(JLabel dateLabel) {
        this.dateLabel = dateLabel;
    }

    /**
     * Sets the back button.
     * @param backButton The back button.
     */
    public void setBackButton(JButton backButton) {
        this.backButton = backButton;
    }

    /**
     * Gets the continue button.
     * @return The continue button.
     */
    public JButton getContinueButton() {
        return continueButton;
    }

    /**
     * Sets the continue button.
     * @param continueButton The continue button.
     */
    public void setContinueButton(JButton continueButton) {
        this.continueButton = continueButton;
    }

    /**
     * Gets the flight.
     * @return The flight.
     */
    public Flight getFlight() {
        return flight;
    }

    /**
     * Sets the flight.
     * @param flight The flight.
     */
    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    /**
     * Gets the origin city.
     * @return The origin city.
     */
    public City getOriginCity() {
        return originCity;
    }

    /**
     * Sets the origin city.
     * @param originCity The origin city.
     */
    public void setOriginCity(City originCity) {
        this.originCity = originCity;
    }

    /**
     * Gets the destination city.
     * @return The destination city.
     */
    public City getDestinationCity() {
        return destinationCity;
    }

    /**
     * Sets the destination city.
     * @param destinationCity The destination city.
     */
    public void setDestinationCity(City destinationCity) {
        this.destinationCity = destinationCity;
    }

    /**
     * Gets the airplane.
     * @return The airplane.
     */
    public Airplane getAirplane() {
        return airplane;
    }

    /**
     * Sets the airplane.
     * @param airplane The airplane.
     */
    public void setAirplane(Airplane airplane) {
        this.airplane = airplane;
    }

    public void setDetailsFlightPanel(JPanel detailsFlightPanel) {
        this.detailsFlightPanel = detailsFlightPanel;
    }
}