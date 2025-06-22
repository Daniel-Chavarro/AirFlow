package org.airflow.reservations.GUI;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.CardLayout;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Import custom panels


public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel cardPanel;


    public MainFrame() throws HeadlessException {
//        super("Airflow Flight Reservation");
//
//        cardLayout = new CardLayout();
//        cardPanel = new JPanel(cardLayout);
//
//        // Initialize custom panels
//        flightSearchPanel = new FlightSearchPanel();
//        bookingPanel = new BookingPanel();
//        reservationListPanel = new ReservationListPanel();
//
//        // Add panels to cardPanel
//        cardPanel.add(flightSearchPanel, "SEARCH");
//        cardPanel.add(bookingPanel, "BOOKING");
//        cardPanel.add(reservationListPanel, "RESERVATIONS");
//
//        // Create navigation panel
//        JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
//        JButton searchButton = new JButton("Search Flights");
//        JButton bookButton = new JButton("Book Flight (Placeholder)"); // Placeholder until a flight is selected
//        JButton reservationsButton = new JButton("View Reservations");
//
//        // Add ActionListeners for navigation buttons
//        searchButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                showPanel("SEARCH");
//            }
//        });
//
//        bookButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                // For now, this directly shows the booking panel.
//                // In a real app, this might be enabled/disabled based on flight selection.
//                showPanel("BOOKING");
//            }
//        });
//
//        reservationsButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                showPanel("RESERVATIONS");
//            }
//        });
//
//        navigationPanel.add(searchButton);
//        navigationPanel.add(bookButton);
//        navigationPanel.add(reservationsButton);
//
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setSize(800, 600);
//
//        // Add components to the frame's content pane
//        getContentPane().setLayout(new BorderLayout()); // Set layout for the content pane
//        getContentPane().add(navigationPanel, BorderLayout.NORTH);
//        getContentPane().add(cardPanel, BorderLayout.CENTER);
//
//        // Show default panel
//        showPanel("SEARCH");
//
//        setVisible(true);
    }

    public void showPanel(String panelName) {
        cardLayout.show(cardPanel, panelName);
    }
}
