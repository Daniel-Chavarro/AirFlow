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

    @BeforeAll
    void globalSetup() {
        // This method can be used for very heavy, one-time setup if needed.
        // For this case, @BeforeEach is sufficient.
    }

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

        // Después de crear el usuario, recupéralo de la BD para obtener su ID generado
        // Asumiendo que usersDAO tiene un método como getByEmail()
        User userWithRealId = userDAO.getByEmail(testUser.getEmail());
        if (userWithRealId != null) {
            testUser.setId(userWithRealId.getId()); // Actualiza el ID del objeto testUser
        } else {
            // Manejar el error si el usuario no se encuentra
            throw new RuntimeException("El usuario de prueba no se encontró después de la creación.");
        }

        reservationService = new ReservationService(testUser, reservationDAO, flightDAO, seatDAO, cityDAO, seatService);
        suggestionService = new SuggestionService(flightDAO, reservationDAO, seatDAO, userDAO, cityDAO, flightService, reservationService, seatService, testUser);

        createTestCities();
        createTestAirplanes();
        createTestFlights(); // Ahora FL001 (testFlightCancelledId) se crea como SCHEDULED (1)
        createTestSeats();
        createTestReservation(); // <<< ESTA LLAMADA AHORA FUNCIONARÁ, YA QUE FL001 ES RESERVABLE

        // AHORA, actualiza el estado del vuelo a CANCELLED para el escenario de prueba
        Flight flightToUpdate = flightDAO.getById(testFlightCancelledId);
        flightToUpdate.setStatus_FK(3); // Establece a CANCELLED (3)
        flightDAO.update(flightToUpdate.getId(), flightToUpdate);
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.rollback(); // Rollback transaction for each test
        connection.setAutoCommit(true); // Restore auto-commit
        connection.close();
    }

    private void cleanDB() throws SQLException {
        // Delete in reverse order of creation and respecting foreign key constraints
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

    private void createTestCities() throws SQLException {
        City city1 = new City(0, "Bogota", "Colombia", "BOG");
        City city2 = new City(0, "Miami", "USA", "MIA");
        cityDAO.create(city1);
        cityDAO.create(city2);
        testCityOriginId = cityDAO.getByName("Bogota").getId();
        testCityDestinationId = cityDAO.getByName("Miami").getId();
    }

    private void createTestAirplanes() throws SQLException {
        Airplane airplane = new Airplane(0, "Avianca", "A320", "AV123", 180, Year.of(2015));
        airplaneDAO.create(airplane);
        testAirplaneId = airplaneDAO.getByCode("AV123").getId();
    }

    private void createTestFlights() throws SQLException {
        // Flight that will *become* cancelled. Initial status must be reservable.
        // Set to SCHEDULED (1) or DELAYED (2) initially.
        // DO NOT set to CANCELLED (3) here.
        Flight flightToBeCancelled = new Flight(0, testAirplaneId, 1, // Inicializar como SCHEDULED (1)
                testCityOriginId, testCityDestinationId,
                "FL001", LocalDateTime.now().plusHours(5), LocalDateTime.now().plusHours(8),
                LocalDateTime.now().plusHours(8), 250.0f);
        flightDAO.create(flightToBeCancelled);
        testFlightCancelledId = flightDAO.getByCodeOb("FL001").getId();


        // Alternative Flight 1 (suitable)
        Flight alternativeFlight1 = new Flight(0, testAirplaneId, 1, testCityOriginId, testCityDestinationId,
                "FL002", LocalDateTime.now().plusHours(6), LocalDateTime.now().plusHours(9),
                LocalDateTime.now().plusHours(9), 260.0f);
        flightDAO.create(alternativeFlight1);
        testFlightAlternative1Id = flightDAO.getByCodeOb("FL002").getId();

        // Alternative Flight 2 (not suitable - different destination)
        Flight alternativeFlight2 = new Flight(0, testAirplaneId, 1, testCityOriginId, cityDAO.getByName("Bogota").getId(), // Different destination
                "FL003", LocalDateTime.now().plusHours(7), LocalDateTime.now().plusHours(10),
                LocalDateTime.now().plusHours(10), 270.0f);
        flightDAO.create(alternativeFlight2);
        testFlightAlternative2Id = flightDAO.getByCodeOb("FL003").getId();
    }

    private void createTestSeats() throws SQLException {
        // Seat for Cancelled Flight
        Seat seatCancelled = new Seat(0, testAirplaneId, null, "10A", Seat.SeatClass.ECONOMY, true);
        seatDAO.create(seatCancelled);
        testSeat_Economy_Seat_CancelledFlight = seatDAO.getByAirplaneIdSeatNumber(testAirplaneId, "10A").getId();

        // Seat for Alternative Flight 1
        Seat seatAlternative = new Seat(0, testAirplaneId, null, "11B", Seat.SeatClass.ECONOMY, false);
        seatDAO.create(seatAlternative);
        testSeat_Economy_Seat_AlternativeFlight = seatDAO.getByAirplaneIdSeatNumber( testAirplaneId, "11B").getId();
    }

    private void createTestReservation() throws SQLException {
        // Create a reservation for the flight that will later be cancelled
        Reservation reservation = reservationService.createReservation(testFlightCancelledId, new int[]{testSeat_Economy_Seat_CancelledFlight});
        testReservationId = reservation.getId();
    }

    // --- Test Methods for SuggestionService ---

    @Test
    @DisplayName("Should suggest alternative flights when available")
    void testSuggestAlternativeFlights_foundAlternatives() throws SQLException {
        List<Flight> suggestedFlights = suggestionService.suggestAlternativeFlights(testFlightCancelledId);

        // ****** AÑADE ESTAS LÍNEAS DE DEPURACIÓN ******
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
        // ****** FIN DE LÍNEAS DE DEPURACIÓN ******

        assertNotNull(suggestedFlights);
        assertFalse(suggestedFlights.isEmpty());
        assertEquals(1, suggestedFlights.size(), "Should find exactly one suitable alternative flight");
        assertEquals(testFlightAlternative1Id, suggestedFlights.get(0).getId(), "The suggested flight should be the expected alternative");
    }

    @Test
    @DisplayName("Should return empty list if no suitable alternative flights are found")
    void testSuggestAlternativeFlights_noAlternatives() throws SQLException {
        // Cancel the only alternative flight, or make it unsuitable
        Flight altFlight = flightDAO.getById(testFlightAlternative1Id);
        altFlight.setStatus_FK(2); // Set to DELAYED (or another unsuitable status like CANCELLED if 2 is reservable)
        flightDAO.update(altFlight.getId(), altFlight);

        List<Flight> suggestedFlights = suggestionService.suggestAlternativeFlights(testFlightCancelledId);

        assertNotNull(suggestedFlights);
        assertTrue(suggestedFlights.isEmpty(), "Should not find any suitable alternative flights");
    }

    @Test
    @DisplayName("Should successfully reassign passenger to a new flight")
    void testProcessSuggestionAcceptance_successfulReassignment() throws SQLException {
        // Get initial state
        Reservation originalReservation = reservationDAO.getById(testReservationId);
        Seat oldSeat = seatDAO.getById(testSeat_Economy_Seat_CancelledFlight);
        Seat newFlightSeatBeforeReassignment = seatDAO.getById(testSeat_Economy_Seat_AlternativeFlight);

        assertEquals(testFlightCancelledId, originalReservation.getFlight_FK(), "Reservation should initially be on cancelled flight");
        assertEquals(testReservationId, oldSeat.getReservation_FK(), "Old seat should be reserved by original reservation");
        assertNull(newFlightSeatBeforeReassignment.getReservation_FK(), "New flight seat should be free before reassignment (null)");


        suggestionService.processSuggestionAcceptance(testReservationId, testFlightAlternative1Id);

        // Verify state after reassignment
        Reservation updatedReservation = reservationDAO.getById(testReservationId);
        Seat freedOldSeat = seatDAO.getById(testSeat_Economy_Seat_CancelledFlight);

        // Fix for SQLException: Get newFlightAirplaneId outside the lambda
        int newFlightAirplaneId = flightDAO.getById(testFlightAlternative1Id).getAirplane_FK();

        // Find the newly assigned seat on the alternative flight for the reservation
        ArrayList<Seat> seatsForUpdatedReservation = seatDAO.getByReservationId(testReservationId);
        Seat newlyAssignedSeat = seatsForUpdatedReservation.stream()
            .filter(s -> s.getAirplane_FK() == newFlightAirplaneId) // Use the pre-calculated variable
            .findFirst()
            .orElse(null);


        assertNotNull(updatedReservation, "Reservation should still exist");
        assertEquals(testFlightAlternative1Id, updatedReservation.getFlight_FK(), "Reservation flight should be updated to new flight");
        assertEquals(1, updatedReservation.getStatus_FK(), "Reservation status should be confirmed/reassigned (assuming 1 is Confirmed)");

        // The old seat should now be free (reservation_FK = 0 or NULL)
        assertNull(freedOldSeat.getReservation_FK(), "Old seat should be freed (reservation_FK = null)");

        // A new seat on the alternative flight should be assigned to the reservation
        assertNotNull(newlyAssignedSeat, "A new seat should have been assigned on the alternative flight");
        assertEquals(testReservationId, newlyAssignedSeat.getReservation_FK(), "New seat should be reserved by the updated reservation");

    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if no seats are available on the new flight during reassignment")
    void testProcessSuggestionAcceptance_noNewSeatAvailable() throws SQLException {
        // Make all seats on the alternative flight unavailable
        Seat seatOnAlternativeFlight = seatDAO.getById(testSeat_Economy_Seat_AlternativeFlight);
        // The updateSeatStatus takes 0 for null, and positive ID for reservation.
        // Let's create a dummy reservation to occupy it.
        Reservation dummyReservation = new Reservation();
        dummyReservation.setFlight_FK(testFlightAlternative1Id);
        dummyReservation.setUser_FK(testUser.getId());
        dummyReservation.setStatus_FK(1);
        reservationDAO.create(dummyReservation);
        dummyReservation = reservationDAO.getByFlightIdAndUserId(testFlightAlternative1Id, testUser.getId()).get(0); // Get with ID
        seatService.updateSeatStatus(seatOnAlternativeFlight.getId(), dummyReservation.getId());


        // Attempt to reassign passenger
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            suggestionService.processSuggestionAcceptance(testReservationId, testFlightAlternative1Id);
        });

        assertTrue(exception.getMessage().contains("No available seats on the new flight"), "Exception message should indicate no available seats");

        // Verify that the original reservation remains unchanged (due to rollback)
        Reservation originalReservation = reservationDAO.getById(testReservationId);
        assertEquals(testFlightCancelledId, originalReservation.getFlight_FK(), "Reservation should remain on cancelled flight due to rollback");
        Seat oldSeat = seatDAO.getById(testSeat_Economy_Seat_CancelledFlight);
        assertEquals(testReservationId, oldSeat.getReservation_FK(), "Old seat should still be reserved by original reservation due to rollback");
    }

    @Test
    @DisplayName("Should process suggestion rejection correctly")
    void testProcessSuggestionRejection_successful() throws SQLException {
        // For this test, since sendNotification is a print statement, we just ensure no exception is thrown.
        // In a real application, you would mock the notification service and verify its invocation,
        // or check for a status update on the reservation if 'rejection' implies a status change.

        // As per the SuggestionService code, processSuggestionRejection simply prints a message
        // and doesn't change reservation status. If it were to change status, we'd assert on that.
        assertDoesNotThrow(() -> {
            suggestionService.processSuggestionRejection(testReservationId);
        });

        // If 'processSuggestionRejection' were to change the reservation status, for example, to 'REJECTED' (ID 6),
        // you would add an assertion like:
        // Reservation updatedReservation = reservationDAO.getById(connection, testReservationId);
        // assertEquals(6, updatedReservation.getStatus_FK(), "Reservation status should be 'Rejected'");
    }
}