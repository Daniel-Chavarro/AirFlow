package org.airflow.reservations.service;

import org.airflow.reservations.DAO.FlightDAO;
import org.airflow.reservations.DAO.ReservationDAO;
import org.airflow.reservations.DAO.SeatDAO;
import org.airflow.reservations.DAO.UsersDAO;
import org.airflow.reservations.DAO.CityDAO;
import org.airflow.reservations.model.Flight;
import org.airflow.reservations.model.Reservation;
import org.airflow.reservations.model.Seat;
import org.airflow.reservations.model.User;
import org.airflow.reservations.utils.ConnectionDB;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing flight reassignments due to cancellations.
 * Provides functionalities for suggesting alternative flights, processing passenger
 * acceptance or rejection, and notifying affected passengers.
 */
public class SuggestionService {

    private FlightDAO flightDAO;
    private ReservationDAO reservationDAO;
    private SeatDAO seatDAO;
    private UsersDAO userDAO;
    private CityDAO cityDAO;
    private FlightService flightService;
    private ReservationService reservationService;
    private SeatService seatService;
    private User currentUser;

    /**
     * Default constructor for SuggestionService.
     * Initializes DAO and service dependencies with new instances using a shared database connection.
     *
     * @throws SQLException if a database access error occurs during DAO initialization.
     * @throws Exception if an error occurs during ReservationService or SeatService initialization.
     */
    public SuggestionService() throws SQLException, Exception {
        Connection connection = ConnectionDB.getConnection();
        this.flightDAO = new FlightDAO(connection);
        this.reservationDAO = new ReservationDAO(connection);
        this.seatDAO = new SeatDAO(connection);
        this.userDAO = new UsersDAO(connection);
        this.cityDAO = new CityDAO(connection);
        this.flightService = new FlightService(this.flightDAO); // Use the initialized flightDAO

        // For the default constructor, assume a default user or null,
        // or that the current user will be obtained by the caller.
        this.currentUser = new User(); // Or null, depending on how the current user is handled.
        this.seatService = new SeatService(this.seatDAO); // Initialize SeatService
        this.reservationService = new ReservationService(this.currentUser, this.reservationDAO, this.flightDAO, this.seatDAO, this.cityDAO, this.seatService);
    }

    /**
     * Constructor for SuggestionService with provided DAOs and services, useful for testing.
     *
     * @param flightDAO The FlightDAO instance.
     * @param reservationDAO The ReservationDAO instance.
     * @param seatDAO The SeatDAO instance.
     * @param userDAO The UserDAO instance.
     * @param cityDAO The CityDAO instance.
     * @param flightService The FlightService instance.
     * @param reservationService The ReservationService instance.
     * @param seatService The SeatService instance.
     * @param currentUser The current User object.
     */
    public SuggestionService(FlightDAO flightDAO, ReservationDAO reservationDAO, SeatDAO seatDAO, UsersDAO userDAO,
                             CityDAO cityDAO, FlightService flightService, ReservationService reservationService,
                             SeatService seatService, User currentUser) {
        this.flightDAO = flightDAO;
        this.reservationDAO = reservationDAO;
        this.seatDAO = seatDAO;
        this.userDAO = userDAO;
        this.cityDAO = cityDAO;
        this.flightService = flightService;
        this.reservationService = reservationService;
        this.seatService = seatService;
        this.currentUser = currentUser;
    }

    /**
     * Identifies and suggests alternative flights for passengers affected by a cancelled flight.
     *
     * @param cancelledFlightId The ID of the cancelled flight.
     * @return A list of suggested alternative flights.
     * @throws SQLException if a database access error occurs.
     */
    public List<Flight> suggestAlternativeFlights(int cancelledFlightId) throws SQLException {
        Flight cancelledFlight = flightDAO.getById(cancelledFlightId);
        if (cancelledFlight == null) {
            throw new IllegalArgumentException("Cancelled flight with ID " + cancelledFlightId + " not found.");
        }
        System.out.println("DEBUG (SuggestionService): Cancelled Flight ID: " + cancelledFlight.getId() + ", Origin: " + cancelledFlight.getOrigin_city_FK() + ", Destination: " + cancelledFlight.getDestination_city_FK() + ", Departure: " + cancelledFlight.getDeparture_time() + ", Status: " + cancelledFlight.getStatus_name());

        LocalDateTime searchStartTime = cancelledFlight.getDeparture_time().minusHours(24);
        LocalDateTime searchEndTime = cancelledFlight.getDeparture_time().plusHours(48);
        System.out.println("DEBUG (SuggestionService): Search time range: " + searchStartTime + " to " + searchEndTime);

        // Call to the DAO: Pay attention to the order of parameters.
        // The DAO method signature is getByDestinationAndOriginCity(int destinationCityId, int originCityId)
        System.out.println("DEBUG (SuggestionService): Calling getByDestinationAndOriginCity with Destination ID: " + cancelledFlight.getDestination_city_FK() + " and Origin ID: " + cancelledFlight.getOrigin_city_FK());
        List<Flight> potentialFlights = flightDAO.getByDestinationAndOriginCity(
                cancelledFlight.getDestination_city_FK(), // This is the first parameter: Destination ID
                cancelledFlight.getOrigin_city_FK()       // This is the second parameter: Origin ID
        );

        System.out.println("DEBUG (SuggestionService): Potential flights found by DAO: " + potentialFlights.size());
        for (Flight pFlight : potentialFlights) {
            System.out.println("DEBUG (SuggestionService): Potential flight from DAO - ID: " + pFlight.getId() + ", Code: " + pFlight.getCode() +
                               ", Origin: " + pFlight.getOrigin_city_FK() + ", Destination: " + pFlight.getDestination_city_FK() +
                               ", Departure: " + pFlight.getDeparture_time() + ", Status Name: " + pFlight.getStatus_name() + ", Status ID: " + pFlight.getStatus_FK());
        }

        List<Flight> suitableAlternatives = new ArrayList<>();
        for (Flight flight : potentialFlights) {
            boolean passesTimeCheck = flight.getDeparture_time().isAfter(searchStartTime) && flight.getDeparture_time().isBefore(searchEndTime);
            boolean isNotCancelledFlight = flight.getId() != cancelledFlightId;
            // Ensure the status name is not "CANCELLED". Verify the actual status_FK value in your DB for "CANCELLED".
            // If flight.getStatus_name() is coming null or incorrect, this part could be the problem.
            boolean isNotCancelledStatus = flight.getStatus_name() != null && !"CANCELLED".equalsIgnoreCase(flight.getStatus_name());
            boolean isNotDelayedStatus = flight.getStatus_name() != null && !"DELAYED".equalsIgnoreCase(flight.getStatus_name());

            System.out.println("DEBUG (SuggestionService) - Filtrando Vuelo ID " + flight.getId() + " (Código: " + flight.getCode() + "):");
            System.out.println("  Chequeo de tiempo (" + flight.getDeparture_time() + " entre " + searchStartTime + " y " + searchEndTime + "): " + passesTimeCheck);
            System.out.println("  No es el Vuelo Cancelado ID (" + flight.getId() + " != " + cancelledFlightId + "): " + isNotCancelledFlight);
            System.out.println("  No es Estado 'CANCELLED' (Nombre Estado: '" + flight.getStatus_name() + "', ID Estado: " + flight.getStatus_FK() + "): " + isNotCancelledStatus);
            System.out.println("  No es Estado 'DELAYED' (Nombre Estado: '" + flight.getStatus_name() + "', ID Estado: " + flight.getStatus_FK() + "): " + isNotDelayedStatus);


            if (passesTimeCheck && isNotCancelledFlight && isNotCancelledStatus && isNotDelayedStatus) {
                suitableAlternatives.add(flight);
                System.out.println("DEBUG (SuggestionService): Añadiendo alternativa adecuada: " + flight.getCode());
            } else {
                System.out.println("DEBUG (SuggestionService): Saltando vuelo " + flight.getCode() + " - falló el filtro.");
            }
        }
        System.out.println("DEBUG (SuggestionService): Conteo final de alternativas adecuadas: " + suitableAlternatives.size());
        return suitableAlternatives;
    }

    /**
     * Processes the acceptance of an alternative flight by a passenger.
     * This method is transactional and ensures data consistency.
     *
     * @param reservationId The ID of the reservation to be reallocated.
     * @param newFlightId The ID of the new flight accepted by the passenger.
     * @throws SQLException if a database access error occurs.
     * @throws IllegalArgumentException if the reservation or new flight is not found,
     * or if the new flight has no available seats.
     */
    public void processSuggestionAcceptance(int reservationId, int newFlightId) throws SQLException {
        Connection connection = null;
        try {
            connection = ConnectionDB.getConnection();
            connection.setAutoCommit(false); // Begin transaction

            Reservation reservation = reservationDAO.getById(reservationId);
            if (reservation == null) {
                throw new IllegalArgumentException("Reservation with ID " + reservationId + " not found.");
            }

            Flight newFlight = flightDAO.getById(newFlightId);
            if (newFlight == null) {
                throw new IllegalArgumentException("New flight with ID " + newFlightId + " not found.");
            }

            // Calls the extended method in ReservationService to reassign and manage seats.
            // Ensure that reservationService.reassignPassenger handles the logic of releasing the old seat
            // and assigning a new one in a transactional manner.
            reservationService.reassignPassenger(reservationId, newFlightId);

            connection.commit(); // Commit transaction

            // Notify the passenger about the successful reassignment
            Optional<User> passenger = Optional.ofNullable(userDAO.getById(reservation.getUser_FK()));
            if (passenger.isPresent()) {
                sendNotification(passenger.get().getEmail(),
                        "Your reservation has been successfully reassigned to flight " + newFlight.getCode() +
                        " departing on " + newFlight.getDeparture_time() + ".");
            }

        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback(); // Rollback transaction in case of error
                } catch (SQLException rollbackEx) {
                    System.err.println("Error rolling back transaction: " + rollbackEx.getMessage());
                }
            }
            throw new SQLException("Error processing suggestion acceptance: " + e.getMessage(), e);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true); // Restore auto-commit
                    connection.close(); // Close connection
                } catch (SQLException closeEx) {
                    System.err.println("Error closing connection: " + closeEx.getMessage());
                }
            }
        }
    }

    /**
     * Processes the rejection of an alternative flight by a passenger.
     * This might involve marking the reservation for manual review or refund.
     *
     * @param reservationId The ID of the reservation for which the suggestion was rejected.
     * @throws SQLException if a database access error occurs.
     */
    public void processSuggestionRejection(int reservationId) throws SQLException {
        Reservation reservation = reservationDAO.getById(reservationId);
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation with ID " + reservationId + " not found.");
        }

        // Here you could change the reservation status to "REJECTED" or "PENDING_REFUND"
        // and/or notify support staff for manual handling.
        // Assuming there is a status_FK for "REJECTED" or similar in the reservation status table.
        // For example: reservation.setStatus_FK(ID_STATUS_RECHAZADO);
        // reservationDAO.update(reservation.getId(), reservation);

        Optional<User> passenger = Optional.ofNullable(userDAO.getById(reservation.getUser_FK()));
        if (passenger.isPresent()) {
            sendNotification(passenger.get().getEmail(),
                    "We have received your rejection of the alternatives. We will contact you to discuss other options or process a refund.");
        }

        System.out.println("Suggestion rejected for reservation ID: " + reservationId);
    }

    /**
     * Sends a notification to the specified email address.
     * This is a placeholder for actual notification logic (e.g., email service integration).
     *
     * @param recipientEmail The email address of the recipient.
     * @param message The message to send.
     */
    private void sendNotification(String recipientEmail, String message) {
        System.out.println("Sending notification to: " + recipientEmail);
        System.out.println("Message: " + message);
        // Actual email or SMS service integration would go here.
    }
}