package org.airflow.reservations.service;

import org.airflow.reservations.DAO.FlightDAO;
import org.airflow.reservations.DAO.ReservationDAO;
import org.airflow.reservations.DAO.SeatDAO;
import org.airflow.reservations.DAO.UsersDAO;
import org.airflow.reservations.DAO.CityDAO; // Importar CityDAO
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
 * Provides functionalities for suggesting alternative flights,
 * processing passenger acceptance/rejection, and notifying affected passengers.
 */
public class SuggestionService {

    private FlightDAO flightDAO;
    private ReservationDAO reservationDAO;
    private SeatDAO seatDAO;
    private UsersDAO userDAO;
    private CityDAO cityDAO; // Nueva dependencia: CityDAO
    private FlightService flightService;
    private ReservationService reservationService;
    private SeatService seatService; // Nueva dependencia: SeatService
    private User currentUser; // Para el constructor de ReservationService

    /**
     * Default constructor for SuggestionService.
     * Initializes DAO and service dependencies with new instances.
     * Requires a default user or mechanism to get one.
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
        this.cityDAO = new CityDAO(connection); // Inicializa CityDAO
        this.flightService = new FlightService(flightDAO);
        
        // Asumiendo que para el constructor por defecto, se puede usar un usuario por defecto o nulo,
        // o que se obtendrá el usuario actual de alguna forma.
        // Para este ejemplo, lo inicializamos con un User por defecto o null (será responsabilidad del llamador)
        this.currentUser = new User(); // O null, dependiendo de cómo manejes el usuario actual
        this.seatService = new SeatService(this.seatDAO); // Inicializa SeatService
        this.reservationService = new ReservationService(this.currentUser, reservationDAO, flightDAO, seatDAO, cityDAO, seatService);
    }

    /**
     * Constructor for SuggestionService with provided DAOs and services, useful for testing.
     *
     * @param flightDAO          The FlightDAO instance.
     * @param reservationDAO     The ReservationDAO instance.
     * @param seatDAO            The SeatDAO instance.
     * @param userDAO            The UserDAO instance.
     * @param cityDAO            The CityDAO instance.
     * @param flightService      The FlightService instance.
     * @param reservationService The ReservationService instance.
     * @param seatService        The SeatService instance.
     * @param currentUser        The current User object.
     */
    public SuggestionService(FlightDAO flightDAO, ReservationDAO reservationDAO, SeatDAO seatDAO, UsersDAO userDAO,
                             CityDAO cityDAO, FlightService flightService, ReservationService reservationService,
                             SeatService seatService, User currentUser) {
        this.flightDAO = flightDAO;
        this.reservationDAO = reservationDAO;
        this.seatDAO = seatDAO;
        this.userDAO = userDAO;
        this.cityDAO = cityDAO; // Asigna CityDAO
        this.flightService = flightService;
        this.reservationService = reservationService;
        this.seatService = seatService; // Asigna SeatService
        this.currentUser = currentUser; // Asigna el usuario actual
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
    System.out.println("DEBUG (SuggestionService): Vuelo Cancelado ID: " + cancelledFlight.getId() + ", Origen: " + cancelledFlight.getOrigin_city_FK() + ", Destino: " + cancelledFlight.getDestination_city_FK() + ", Salida: " + cancelledFlight.getDeparture_time() + ", Estado: " + cancelledFlight.getStatus_name());

    LocalDateTime searchStartTime = cancelledFlight.getDeparture_time().minusHours(24);
    LocalDateTime searchEndTime = cancelledFlight.getDeparture_time().plusHours(48);
    System.out.println("DEBUG (SuggestionService): Rango de tiempo de búsqueda: " + searchStartTime + " a " + searchEndTime);

    // Llamada al DAO: Presta atención al orden de los parámetros.
    // La firma del método DAO es getByDestinationAndOriginCity(int destinationCityId, int originCityId)
    System.out.println("DEBUG (SuggestionService): Llamando a getByDestinationAndOriginCity con ID Destino: " + cancelledFlight.getDestination_city_FK() + " y ID Origen: " + cancelledFlight.getOrigin_city_FK());
    List<Flight> potentialFlights = flightDAO.getByDestinationAndOriginCity(
            cancelledFlight.getDestination_city_FK(), // Este es el primer parámetro: Destination ID
            cancelledFlight.getOrigin_city_FK()       // Este es el segundo parámetro: Origin ID
    );

    System.out.println("DEBUG (SuggestionService): Vuelos potenciales encontrados por DAO: " + potentialFlights.size());
    for (Flight pFlight : potentialFlights) {
        System.out.println("DEBUG (SuggestionService): Vuelo potencial del DAO - ID: " + pFlight.getId() + ", Código: " + pFlight.getCode() +
                           ", Origen: " + pFlight.getOrigin_city_FK() + ", Destino: " + pFlight.getDestination_city_FK() +
                           ", Salida: " + pFlight.getDeparture_time() + ", Nombre Estado: " + pFlight.getStatus_name() + ", ID Estado: " + pFlight.getStatus_FK());
    }

    List<Flight> suitableAlternatives = new ArrayList<>();
    for (Flight flight : potentialFlights) {
        boolean passesTimeCheck = flight.getDeparture_time().isAfter(searchStartTime) && flight.getDeparture_time().isBefore(searchEndTime);
        boolean isNotCancelledFlight = flight.getId() != cancelledFlightId;
        // Asegúrate de que el nombre del estado no sea "CANCELLED". Verifica el valor real de status_FK en tu BD para "CANCELLED".
        // Si flight.getStatus_name() está viniendo nulo o incorrecto, esta parte podría ser el problema.
        boolean isNotCancelledStatus = flight.getStatus_name() != null && !"CANCELLED".equalsIgnoreCase(flight.getStatus_name());
        boolean isNotDelayedStatus = flight.getStatus_name() != null && !"DELAYED".equalsIgnoreCase(flight.getStatus_name());

        System.out.println("DEBUG (SuggestionService) - Filtrando Vuelo ID " + flight.getId() + " (Código: " + flight.getCode() + "):");
        System.out.println("  Chequeo de tiempo (" + flight.getDeparture_time() + " entre " + searchStartTime + " y " + searchEndTime + "): " + passesTimeCheck);
        System.out.println("  No es el Vuelo Cancelado ID (" + flight.getId() + " != " + cancelledFlightId + "): " + isNotCancelledFlight);
        System.out.println("  No es Estado 'CANCELLED' (Nombre Estado: '" + flight.getStatus_name() + "', ID Estado: " + flight.getStatus_FK() + "): " + isNotCancelledStatus);
        System.out.println("  No es Estado 'DELAYED' (Nombre Estado: '" + flight.getStatus_name() + "', ID Estado: " + flight.getStatus_FK() + "): " + isNotDelayedStatus);


        if (passesTimeCheck && isNotCancelledFlight && isNotCancelledStatus&&isNotDelayedStatus) {
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
     * @param newFlightId   The ID of the new flight accepted by the passenger.
     * @throws SQLException if a database access error occurs.
     * @throws IllegalArgumentException if the reservation or new flight is not found,
     * or if the new flight has no available seats.
     */
    public void processSuggestionAcceptance(int reservationId, int newFlightId) throws SQLException {
        Connection connection = null;
        try {
            connection = ConnectionDB.getConnection();
            connection.setAutoCommit(false); // Iniciar transacción

            Reservation reservation = reservationDAO.getById(reservationId);
            if (reservation == null) {
                throw new IllegalArgumentException("Reservation with ID " + reservationId + " not found.");
            }

            Flight newFlight = flightDAO.getById(newFlightId);
            if (newFlight == null) {
                throw new IllegalArgumentException("New flight with ID " + newFlightId + " not found.");
            }
            
            // Llama al método extendido en ReservationService para reasignar y gestionar asientos.
            // Asegúrate que reservationService.reassignPassenger maneje la lógica de liberar el asiento viejo
            // y asignar uno nuevo de forma transaccional.
            reservationService.reassignPassenger(reservationId, newFlightId);

            connection.commit(); // Confirmar transacción
            
            // Notificar al pasajero sobre la reasignación exitosa
            Optional<User> passenger = Optional.ofNullable(userDAO.getById(reservation.getUser_FK()));
            if (passenger.isPresent()) {
                sendNotification(passenger.get().getEmail(),
                        "Tu reserva ha sido reasignada exitosamente al vuelo " + newFlight.getCode() +
                        " con salida el " + newFlight.getDeparture_time() + ".");
            }

        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback(); // Revertir transacción en caso de error
                } catch (SQLException rollbackEx) {
                    System.err.println("Error rolling back transaction: " + rollbackEx.getMessage());
                }
            }
            throw new SQLException("Error processing suggestion acceptance: " + e.getMessage(), e);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true); // Restaurar auto-commit
                    connection.close();
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

        // Aquí podrías cambiar el estado de la reserva a "RECHAZADA" o "PENDIENTE_REEMBOLSO"
        // y/o notificar al personal de soporte para manejo manual.
        // Asumiendo que hay un status_FK para "REJECTED" o similar en la tabla de estados de reservas.
        // Por ejemplo: reservation.setStatus_FK(ID_STATUS_RECHAZADO);
        // reservationDAO.update(reservation.getId(), reservation);

        Optional<User> passenger = Optional.ofNullable(userDAO.getById(reservation.getUser_FK()));
        if (passenger.isPresent()) {
            sendNotification(passenger.get().getEmail(),
                    "Hemos recibido tu rechazo de las alternativas. Nos pondremos en contacto contigo para otras opciones o gestionar un reembolso.");
        }

        System.out.println("Suggestion rejected for reservation ID: " + reservationId);
    }

    /**
     * Sends a notification to the specified email address.
     * This is a placeholder for actual notification logic (e.g., email service integration).
     *
     * @param recipientEmail The email address of the recipient.
     * @param message        The message to send.
     */
    private void sendNotification(String recipientEmail, String message) {
        System.out.println("Sending notification to: " + recipientEmail);
        System.out.println("Message: " + message);
        // Aquí se integraría con un servicio de email o SMS real.
    }
}