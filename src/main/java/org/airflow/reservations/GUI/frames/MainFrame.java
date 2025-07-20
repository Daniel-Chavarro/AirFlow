package org.airflow.reservations.GUI.frames;


import org.airflow.reservations.model.Airplane;
import org.airflow.reservations.model.City;
import org.airflow.reservations.model.Flight;
import org.airflow.reservations.model.Seat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import com.formdev.flatlaf.FlatLightLaf;
import org.airflow.reservations.GUI.Bridge.View;
import org.airflow.reservations.GUI.panels.*;


/**
 * MainFrame class represents the main window of the Airflow Reservations application.
 * It contains a horizontal menu with a logo and buttons, and a content panel that
 * displays different panels for searching flights, viewing flight details, and confirming reservations.
 */
public class MainFrame extends JFrame implements View{
    private JPanel menuPanel;
    private JPanel logoPanel;
    private JPanel buttonsPanel;
    private JPanel contentPanel;
    private JScrollPane scrollPane;


    private CardLayout cardLayout;
    private JLabel imageLabel;
    private JLabel titleLabel;

    private BookSeatsPanel bookSeatsPanel;
    private ConfirmPanel confirmPanel;
    private DetailsFlightPanel detailsFlightPanel;
    private SearchFlightPanel searchFlightPanel;




    /**
     * Constructor for the MainFrame class.
     * Initializes the main frame with a title, size, and layout,
     * and sets up the horizontal menu and content panel.
     */
    public MainFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Airflow Reservations");
        setSize(1000, 700);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());


        bookSeatsPanel = new BookSeatsPanel();
        confirmPanel = new ConfirmPanel();
        detailsFlightPanel = new DetailsFlightPanel();
        searchFlightPanel = new SearchFlightPanel();

        createHorizontalMenu();
        createContentPanel();
    }

    /**
     * Creates the horizontal menu at the top of the main frame.
     * The menu includes a logo, title, and buttons for different sections.
     */
    public void createHorizontalMenu(){
        menuPanel = new JPanel();
        menuPanel.setLayout(new BorderLayout());
        menuPanel.setBackground(Color.WHITE);
        menuPanel.setPreferredSize(new Dimension(1000, 60));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Logo panel
        logoPanel = new JPanel();
        logoPanel.setLayout(new FlowLayout(FlowLayout.LEFT,10,0));
        logoPanel.setBackground(Color.WHITE);

        imageLabel = new JLabel();
        imageLabel.setIcon(new ImageIcon(new ImageIcon(
                Objects.requireNonNull(getClass()
                        .getResource("/images/logo.png")))
                .getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH)));
        imageLabel.setPreferredSize(new Dimension(24, 24));

        logoPanel.add(imageLabel);

        // Title label
        titleLabel = new JLabel("Airflow");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(Color.BLACK);

        logoPanel.add(titleLabel);

        // Buttons panel
        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        buttonsPanel.setBackground(Color.WHITE);

        String[] sections = {"Flights", "Logout"};

        for (String section : sections) {
            JButton btn = new JButton(section);
            btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
            btn.setFocusPainted(false);
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setForeground(Color.BLACK);
            if (section.equals("Logout")) {
                btn.setActionCommand(LOGOUT_CMD);
            } else {
                btn.setActionCommand(BACK_TO_FLIGHTS_CMD);
            }
            buttonsPanel.add(btn);
        }

        menuPanel.add(logoPanel, BorderLayout.WEST);
        menuPanel.add(buttonsPanel, BorderLayout.EAST);

        add(menuPanel, BorderLayout.NORTH);
    }

    /**
     * Creates the content panel that holds different sections of the application.
     * It uses a CardLayout to switch between different panels.
     */
    private void createContentPanel() {
        contentPanel = new JPanel();
        cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);
        contentPanel.setBackground(Color.WHITE);

        contentPanel.add(searchFlightPanel, "SearchFlightPanel");
        contentPanel.add(detailsFlightPanel, "DetailsFlightPanel");
        contentPanel.add(bookSeatsPanel, "BookSeatsPanel");
        contentPanel.add(confirmPanel, "ConfirmPanel");



        scrollPane = new JScrollPane(contentPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    public void addActionListener(ActionListener listener) {
        searchFlightPanel.getSearchButton().addActionListener(listener);
        detailsFlightPanel.getBackButton().addActionListener(listener);
        detailsFlightPanel.getContinueButton().addActionListener(listener);
        bookSeatsPanel.getConfirmButton().addActionListener(listener);
        bookSeatsPanel.getClearSeatsButton().addActionListener(listener);
        bookSeatsPanel.getCancelButton().addActionListener(listener);
        confirmPanel.getBackButton().addActionListener(listener);
        confirmPanel.getConfirmButton().addActionListener(listener);

        for (Component component : buttonsPanel.getComponents()) {
            if (component instanceof JButton) {
                ((JButton) component).addActionListener(listener);
            }
        }
    }

    @Override
    public void showPanel(String panelName) {
        cardLayout.show(contentPanel, panelName);
    }

    @Override
    public JFrame getFrame() {
        return this;
    }

    @Override
    public String getOrigin() {
        return (String) searchFlightPanel.getOriginComboBox().getSelectedItem();
    }

    @Override
    public String getDestination() {
        return (String) searchFlightPanel.getDestinationComboBox().getSelectedItem();
    }

    @Override
    public LocalDate getDepartureDate() {
        return searchFlightPanel.getDepartureDatePicker().getDate();
    }

    @Override
    public LocalDate getReturnDate() {
        return searchFlightPanel.getReturnDatePicker().getDate();
    }

    @Override
    public ArrayList<Seat> getSelectedSeats() {
        return bookSeatsPanel.getSelectedSeats();
    }

    @Override
    public void setFlightDetails(Flight flight, City origin, City destination, Airplane airplane) {
        detailsFlightPanel.setData(flight, origin, destination, airplane);
    }

    @Override
    public void setBookSeatsData(Flight flight, Airplane airplane, ArrayList<Seat> seats) {
        bookSeatsPanel.setFlight(flight);
        bookSeatsPanel.setAirplane(airplane);
        bookSeatsPanel.setSeats(seats);
    }

    @Override
    public void setConfirmationData(Flight flight, City originCity, City destinationCity, Airplane airplane, ArrayList<Seat> selectedSeats, Map<Seat.SeatClass, Double> classMultipliers, double basePrice) {
        confirmPanel.setData(flight, originCity, destinationCity, airplane, selectedSeats, classMultipliers, basePrice);
    }

    @Override
    public void displayFlights(ArrayList<Flight> flights) {
        // This method should be implemented to display the flights in the results panel
        // For now, it will just print the flights to the console
        System.out.println("Flights found: " + flights.size());
    }

    /**
     * Main method to run the application.
     * Initializes the FlatLaf look and feel, creates the main frame,
     * and sets it visible.
     * Just for testing purposes, it shows the SearchFlightPanel by default.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf");
        }

        MainFrame mainFrame = new MainFrame();
        mainFrame.showPanel("SearchFlightPanel");
        mainFrame.setVisible(true);
    }

    public JPanel getMenuPanel() {
        return menuPanel;
    }

    public void setMenuPanel(JPanel menuPanel) {
        this.menuPanel = menuPanel;
    }

    public JPanel getLogoPanel() {
        return logoPanel;
    }

    public void setLogoPanel(JPanel logoPanel) {
        this.logoPanel = logoPanel;
    }

    public JPanel getButtonsPanel() {
        return buttonsPanel;
    }

    public void setButtonsPanel(JPanel buttonsPanel) {
        this.buttonsPanel = buttonsPanel;
    }

    public JPanel getContentPanel() {
        return contentPanel;
    }

    public void setContentPanel(JPanel contentPanel) {
        this.contentPanel = contentPanel;
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public void setScrollPane(JScrollPane scrollPane) {
        this.scrollPane = scrollPane;
    }

    public JLabel getImageLabel() {
        return imageLabel;
    }

    public void setImageLabel(JLabel imageLabel) {
        this.imageLabel = imageLabel;
    }

    public JLabel getTitleLabel() {
        return titleLabel;
    }

    public void setTitleLabel(JLabel titleLabel) {
        this.titleLabel = titleLabel;
    }

    public ConfirmPanel getConfirmPanel() {
        return confirmPanel;
    }

    public void setConfirmPanel(ConfirmPanel confirmPanel) {
        this.confirmPanel = confirmPanel;
    }

    public DetailsFlightPanel getDetailsFlightPanel() {
        return detailsFlightPanel;
    }

    public void setDetailsFlightPanel(DetailsFlightPanel detailsFlightPanel) {
        this.detailsFlightPanel = detailsFlightPanel;
    }

    public SearchFlightPanel getSearchFlightPanel() {
        return searchFlightPanel;
    }

    public void setSearchFlightPanel(SearchFlightPanel searchFlightPanel) {
        this.searchFlightPanel = searchFlightPanel;
    }
}
