package org.airflow.reservations.service;

import org.airflow.reservations.DAO.*;
import org.airflow.reservations.model.*;
import org.airflow.reservations.utils.ConnectionDB;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the SuggestionService class.
 * Tests functionalities related to suggesting and processing alternative flight reassignments.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SuggestionServiceTest {
    private Connection connection;
    private AirplaneDAO airplaneDAO;
    private CityDAO cityDAO;
    private FlightDAO flightDAO;
    private SeatDAO seatDAO;
    private UsersDAO userDAO;
    private ReservationDAO reservationDAO;

    private SeatService seatService;
    private FlightService flightService;
    private ReservationService reservationService;
    private SuggestionService suggestionService;

    private User testUser;

    // Test Data IDs
    private int testCityOriginId;
    private int testCityDestinationId;
    private int testAirplaneId;
    private int testFlightCancelledId;
    private int testFlightAlternative1Id;
    private int testFlightAlternative2Id;
    private int testSeat_Economy_Seat_CancelledFlight;
    private int testSeat_Economy_Seat_AlternativeFlight;
    private int testReservationId; // Reservation on the cancelled flight



    /**
     * Initializes a database connection, DAOs, and services before each test.
     * Sets up test data including users, cities, airplanes, flights, seats, and a reservation.
     *
     * @throws Exception if an error occurs during setup or test data creation.
     */
    @BeforeEach
    void setUp() throws Exception {
        connection = ConnectionDB.getConnection();
        connection.setAutoCommit(false); // Start transaction for each test

        airplaneDAO = new AirplaneDAO(connection);
        cityDAO = new CityDAO(connection);
        flightDAO = new FlightDAO(connection);
        seatDAO = new SeatDAO(connection);
        userDAO = new UsersDAO(connection);
        reservationDAO = new ReservationDAO(connection);

        seatService = new SeatService(seatDAO);
        flightService = new FlightService(flightDAO);

        cleanDB();
        // Create a test user for ReservationService and SuggestionService
        testUser = new User(0, "Test", "User", "test@example.com", "password", false, LocalDateTime.now());
        userDAO.create(testUser);

        // Retrieve the user from the DB after creation to get its generated ID.
        // Assuming userDAO has a getByEmail() method.
        User userWithRealId = userDAO.getByEmail(testUser.getEmail());
        if (userWithRealId != null) {
            testUser.setId(userWithRealId.getId()); // Update the ID of the testUser object.
        } else {
            // Handle error if the user is not found.
            throw new RuntimeException("El usuario de prueba no se encontró después de la creación.");
        }

        reservationService = new ReservationService(testUser, reservationDAO, flightDAO, seatDAO, cityDAO, seatService);
        suggestionService = new SuggestionService(flightDAO, reservationDAO, seatDAO, userDAO, cityDAO, flightService, reservationService, seatService, testUser);

        createTestCities();
        createTestAirplanes();
        // FL001 (testFlightCancelledId) is initially created as SCHEDULED (1).
        createTestFlights();
        createTestSeats();
        // This call will now work as FL001 is reservable.
        createTestReservation();

        // Now, update the flight status to CANCELLED for the test scenario.
        Flight flightToUpdate = flightDAO.getById(testFlightCancelledId);
        flightToUpdate.setStatus_FK(3); // Set to CANCELLED (3).
        flightDAO.update(flightToUpdate.getId(), flightToUpdate);
    }

    /**
     * Cleans up the test environment after each test by rolling back the transaction.
     *
     * @throws SQLException if a database access error occurs.
     */
    @AfterEach
    void tearDown() throws SQLException {
        connection.rollback(); // Rollback transaction for each test
        connection.setAutoCommit(true); // Restore auto-commit
        connection.close(); // Close connection
    }

    /**
     * Deletes all test data from the database in reverse order of creation
     * to respect foreign key constraints.
     *
     * @throws SQLException if a database access error occurs.
     */
    private void cleanDB() throws SQLException {
        // Deletes data in reverse order of creation to respect foreign key constraints.
        // 1. Delete all seats (depend on Reservations and Airplanes)
        ArrayList<Seat> allSeats = seatDAO.getAll();
        for (Seat s : allSeats) {
            seatDAO.delete(s.getId());
        }

        // 2. Delete all reservations (depend on Users and Flights)
        ArrayList<Reservation> allReservations = reservationDAO.getAll();
        for (Reservation r : allReservations) {
            reservationDAO.delete(r.getId());
        }

        // 3. Delete all flights (depend on Airplanes and Cities)
        ArrayList<Flight> allFlights = flightDAO.getAll();
        for (Flight f : allFlights) {
            flightDAO.delete(f.getId());
        }

        // 4. Delete all airplanes (no direct foreign keys to other main tables here)
        ArrayList<Airplane> allAirplanes = airplaneDAO.getAll();
        for (Airplane a : allAirplanes) {
            airplaneDAO.delete(a.getId());
        }

        // 5. Delete all cities (no direct foreign keys to other main tables here)
        ArrayList<City> allCities = cityDAO.getAll();
        for (City c : allCities) {
            cityDAO.delete(c.getId());
        }

        // 6. Delete all users (no direct foreign keys to other main tables here)
        ArrayList<User> allUsers = userDAO.getAll();
        for (User u : allUsers) {
            userDAO.delete(u.getId());
        }
    }

    /**
     * Creates test city entries in the database.
     *
     * @throws SQLException if a database access error occurs.
     */
    private void createTestCities() throws SQLException {
        City city1 = new City(0, "Bogota", "Colombia", "BOG");
        City city2 = new City(0, "Miami", "USA", "MIA");
        cityDAO.create(city1);
        cityDAO.create(city2);
        testCityOriginId = cityDAO.getByName("Bogota").getId();
        testCityDestinationId = cityDAO.getByName("Miami").getId();
    }

    /**
     * Creates test airplane entries in the database.
     *
     * @throws SQLException if a database access error occurs.
     */
    private void createTestAirplanes() throws SQLException {
        Airplane airplane = new Airplane(0, "Avianca", "A320", "AV123", 180, Year.of(2015));
        airplaneDAO.create(airplane);
        testAirplaneId = airplaneDAO.getByCode("AV123").getId();
    }

    /**
     * Creates test flight entries in the database, including a flight to be cancelled
     * and suitable/unsuitable alternative flights.
     *
     * @throws SQLException if a database access error occurs.
     */
    private void createTestFlights() throws SQLException {
        // Flight that will later be cancelled. Its initial status must be reservable.
        // Do NOT set to CANCELLED (3) here.
        Flight flightToBeCancelled = new Flight(0, testAirplaneId, 1, // Initialize as SCHEDULED (1).
                testCityOriginId, testCityDestinationId,
                "FL001", LocalDateTime.now().plusHours(5), LocalDateTime.now().plusHours(8),
                LocalDateTime.now().plusHours(8), 250.0f);
        flightDAO.create(flightToBeCancelled);
        testFlightCancelledId = flightDAO.getByCodeOb("FL001").getId();


        // Alternative Flight 1 (suitable).
        Flight alternativeFlight1 = new Flight(0, testAirplaneId, 1, testCityOriginId, testCityDestinationId,
                "FL002", LocalDateTime.now().plusHours(6), LocalDateTime.now().plusHours(9),
                LocalDateTime.now().plusHours(9), 260.0f);
        flightDAO.create(alternativeFlight1);
        testFlightAlternative1Id = flightDAO.getByCodeOb("FL002").getId();

        // Alternative Flight 2 (not suitable - different destination).
        Flight alternativeFlight2 = new Flight(0, testAirplaneId, 1, testCityOriginId, cityDAO.getByName("Bogota").getId(), // Different destination.
                "FL003", LocalDateTime.now().plusHours(7), LocalDateTime.now().plusHours(10),
                LocalDateTime.now().plusHours(10), 270.0f);
        flightDAO.create(alternativeFlight2);
        testFlightAlternative2Id = flightDAO.getByCodeOb("FL003").getId();
    }

    /**
     * Creates test seat entries in the database for the cancelled and alternative flights.
     *
     * @throws SQLException if a database access error occurs.
     */
    private void createTestSeats() throws SQLException {
        // Seat for the cancelled flight.
        Seat seatCancelled = new Seat(0, testAirplaneId, null, "10A", Seat.SeatClass.ECONOMY, true);
        seatDAO.create(seatCancelled);
        testSeat_Economy_Seat_CancelledFlight = seatDAO.getByAirplaneIdSeatNumber(testAirplaneId, "10A").getId();

        // Seat for the first alternative flight.
        Seat seatAlternative = new Seat(0, testAirplaneId, null, "11B", Seat.SeatClass.ECONOMY, false);
        seatDAO.create(seatAlternative);
        testSeat_Economy_Seat_AlternativeFlight = seatDAO.getByAirplaneIdSeatNumber(testAirplaneId, "11B").getId();
    }

    /**
     * Creates a test reservation for the flight that will later be cancelled.
     *
     * @throws SQLException if a database access error occurs.
     */
    private void createTestReservation() throws SQLException {
        // Creates a reservation for the flight that will later be cancelled.
        Reservation reservation = reservationService.createReservation(testFlightCancelledId, new int[]{testSeat_Economy_Seat_CancelledFlight});
        testReservationId = reservation.getId();
    }

    // --- Test Methods for SuggestionService ---

    /**
     * Tests that `suggestAlternativeFlights` returns suitable alternative flights when available.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    @DisplayName("Should suggest alternative flights when available")
    void testSuggestAlternativeFlights_foundAlternatives() throws SQLException {
        List<Flight> suggestedFlights = suggestionService.suggestAlternativeFlights(testFlightCancelledId);

        System.out.println("DEBUG: Número de vuelos sugeridos: " + suggestedFlights.size());
        if (suggestedFlights.isEmpty()) {
            System.out.println("DEBUG: La lista de vuelos sugeridos está vacía.");
        } else {
            System.out.println("DEBUG: Vuelos sugeridos encontrados:");
            for (Flight f : suggestedFlights) {
                System.out.println("  ID: " + f.getId() +
                                   ", Código: " + f.getCode() +
                                   ", Origen: " + f.getOrigin_city_FK() +
                                   ", Destino: " + f.getDestination_city_FK() +
                                   ", Salida: " + f.getDeparture_time() +
                                   ", Estado: " + f.getStatus_FK());
            }
        }

        assertNotNull(suggestedFlights);
        assertFalse(suggestedFlights.isEmpty());
        assertEquals(1, suggestedFlights.size(), "Should find exactly one suitable alternative flight");
        assertEquals(testFlightAlternative1Id, suggestedFlights.get(0).getId(), "The suggested flight should be the expected alternative");
    }

    /**
     * Tests that `suggestAlternativeFlights` returns an empty list if no suitable alternative flights are found.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    @DisplayName("Should return empty list if no suitable alternative flights are found")
    void testSuggestAlternativeFlights_noAlternatives() throws SQLException {
        // Cancel the only alternative flight, or make it unsuitable.
        Flight altFlight = flightDAO.getById(testFlightAlternative1Id);
        altFlight.setStatus_FK(2); // Set to DELAYED (or another unsuitable status if 2 is reservable).
        flightDAO.update(altFlight.getId(), altFlight);

        List<Flight> suggestedFlights = suggestionService.suggestAlternativeFlights(testFlightCancelledId);

        assertNotNull(suggestedFlights);
        assertTrue(suggestedFlights.isEmpty(), "Should not find any suitable alternative flights");
    }

    /**
     * Tests that `processSuggestionAcceptance` successfully reassigns a passenger to a new flight.
     * Verifies the reservation's flight is updated, the old seat is freed, and a new seat is assigned.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    @DisplayName("Should successfully reassign passenger to a new flight")
    void testProcessSuggestionAcceptance_successfulReassignment() throws SQLException {
        // Get the initial state of entities.
        Reservation originalReservation = reservationDAO.getById(testReservationId);
        Seat oldSeat = seatDAO.getById(testSeat_Economy_Seat_CancelledFlight);
        Seat newFlightSeatBeforeReassignment = seatDAO.getById(testSeat_Economy_Seat_AlternativeFlight);

        assertEquals(testFlightCancelledId, originalReservation.getFlight_FK(), "Reservation should initially be on cancelled flight");
        assertEquals(testReservationId, oldSeat.getReservation_FK(), "Old seat should be reserved by original reservation");
        assertNull(newFlightSeatBeforeReassignment.getReservation_FK(), "New flight seat should be free before reassignment (null)");


        suggestionService.processSuggestionAcceptance(testReservationId, testFlightAlternative1Id);

        // Verify the state after reassignment.
        Reservation updatedReservation = reservationDAO.getById(testReservationId);
        Seat freedOldSeat = seatDAO.getById(testSeat_Economy_Seat_CancelledFlight);

        // Obtain newFlightAirplaneId outside the lambda to avoid SQLException.
        int newFlightAirplaneId = flightDAO.getById(testFlightAlternative1Id).getAirplane_FK();

        // Find the newly assigned seat on the alternative flight for the reservation.
        ArrayList<Seat> seatsForUpdatedReservation = seatDAO.getByReservationId(testReservationId);
        Seat newlyAssignedSeat = seatsForUpdatedReservation.stream()
            .filter(s -> s.getAirplane_FK() == newFlightAirplaneId) // Use the pre-calculated variable.
            .findFirst()
            .orElse(null);


        assertNotNull(updatedReservation, "Reservation should still exist");
        assertEquals(testFlightAlternative1Id, updatedReservation.getFlight_FK(), "Reservation flight should be updated to new flight");
        assertEquals(1, updatedReservation.getStatus_FK(), "Reservation status should be confirmed/reassigned (assuming 1 is Confirmed)");

        // The old seat should now be free (reservation_FK should be null).
        assertNull(freedOldSeat.getReservation_FK(), "Old seat should be freed (reservation_FK = null)");

        // A new seat on the alternative flight should be assigned to the reservation.
        assertNotNull(newlyAssignedSeat, "A new seat should have been assigned on the alternative flight");
        assertEquals(testReservationId, newlyAssignedSeat.getReservation_FK(), "New seat should be reserved by the updated reservation");

    }

    /**
     * Tests that `processSuggestionRejection` is handled correctly.
     * Ensures no unexpected exceptions are thrown during the rejection process.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    @DisplayName("Should process suggestion rejection correctly")
    void testProcessSuggestionRejection_successful() throws SQLException {
        // For this test, as sendNotification is a print statement, we only ensure no exception is thrown.
        // In a real application, the notification service would be mocked and its invocation verified,
        // or a status update on the reservation would be checked if 'rejection' implies a status change.

        // As per the SuggestionService code, processSuggestionRejection merely prints a message
        // and does not change reservation status. If it were to change status, an assertion would be added.
        assertDoesNotThrow(() -> {
            suggestionService.processSuggestionRejection(testReservationId);
        });

        // If 'processSuggestionRejection' were to change the reservation status to 'REJECTED' (e.g., ID 6),
        // an assertion such as the following would be added:
        // Reservation updatedReservation = reservationDAO.getById(connection, testReservationId);
        // assertEquals(6, updatedReservation.getStatus_FK(), "Reservation status should be 'Rejected'");
    }
}