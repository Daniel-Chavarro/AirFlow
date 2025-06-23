package org.airflow.reservations.service;

import org.airflow.reservations.DAO.*;
import org.airflow.reservations.model.*;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReservationServiceTest {
    private AirplaneDAO airplaneDAO;
    private CityDAO cityDAO;
    private FlightDAO flightDAO;
    private SeatDAO seatDAO;
    private UsersDAO usersDAO;
    private User testUser;
    private ReservationService reservationService;
    private ReservationDAO reservationDAO;

    @BeforeEach
    void setUp() throws Exception {
        airplaneDAO = new AirplaneDAO();
        cityDAO = new CityDAO();
        flightDAO = new FlightDAO();
        seatDAO = new SeatDAO();
        usersDAO = new UsersDAO();
        reservationDAO = new ReservationDAO();

        testUser = new User(0, "Test", "User", "testuser@example.com",
                "password", true, LocalDateTime.now());
        usersDAO.create(testUser);

        reservationService = new ReservationService(testUser);

        // Insert test cities
        cityDAO.create(new City(0, "City1", "testCountry1", "testCode1"));
        cityDAO.create(new City(0, "City2", "testCountry2", "testCode2"));
        cityDAO.create(new City(0, "City3", "testCountry3", "testCode3"));
        // Inset test airplanes
        airplaneDAO.create(new Airplane(0, "testAirline", "testModel", "TSTCDE", 10, Year.now()));
        // Insert test flights
        flightDAO.create(new Flight(0, airplaneDAO.getByCode("TSTCDE").getId(), 1,
                cityDAO.getByName("City1").getId(), cityDAO.getByName("City2").getId(),
                "testCode", LocalDateTime.now(), LocalDateTime.now(), 0)); // Valid flight

        // Insert test seats
        seatDAO.create(new Seat(0, airplaneDAO.getByCode("TSTCDE").getId(), null,
                "T00", Seat.SeatClass.BUSINESS, true)); // Available seat
        seatDAO.create(new Seat(0, airplaneDAO.getByCode("TSTCDE").getId(), null,
                "T01", Seat.SeatClass.BUSINESS, false));// Another seat
        seatDAO.create(new Seat(0, airplaneDAO.getByCode("TSTCDE").getId(), null,
                "T02", Seat.SeatClass.FIRST, true));
        seatDAO.create(new Seat(0, airplaneDAO.getByCode("TSTCDE").getId(), null,
                "T03", Seat.SeatClass.FIRST, false));
        seatDAO.create(new Seat(0, airplaneDAO.getByCode("TSTCDE").getId(), null,
                "T04", Seat.SeatClass.ECONOMY, true));
        seatDAO.create(new Seat(0, airplaneDAO.getByCode("TSTCDE").getId(), null,
                "T05", Seat.SeatClass.ECONOMY, false));
    }

    @AfterEach
    void tearDown() throws SQLException {
        // Delete test data
        ArrayList<Seat> testSeats = seatDAO.getByAirplaneId(airplaneDAO.getByCode("TSTCDE").getId());
        for (Seat seat : testSeats) {
            seatDAO.delete(seat.getId());
        }
        usersDAO.delete(usersDAO.getByEmail("testuser@example.com").getId());
        flightDAO.delete(flightDAO.getByOriginCity(cityDAO.getByName("City1").getId()).get(0).getId());
        cityDAO.delete(cityDAO.getByName("City1").getId());
        cityDAO.delete(cityDAO.getByName("City2").getId());
        cityDAO.delete(cityDAO.getByName("City3").getId());

        airplaneDAO.delete(airplaneDAO.getByCode("TSTCDE").getId());
    }

    @Test
    void testAvailableFlights_shouldReturnValidFlights() throws SQLException {
        // Arrange
        int originCityId = cityDAO.getByName("City1").getId();
        int destCityId = cityDAO.getByName("City2").getId();

        // Act
        ArrayList<Flight> result = reservationService.availableFlights(destCityId, originCityId);

        // Assert
        Assertions.assertNotNull(result, "El resultado no debería ser null");
        Assertions.assertFalse(result.isEmpty(), "Debe haber al menos un vuelo disponible");

        // Verificar que todos los vuelos tienen el origen y destino correctos
        for (Flight flight : result) {
            Assertions.assertEquals(originCityId, flight.getOrigin_city_FK(),
                    "El vuelo debe tener el origen correcto");
            Assertions.assertEquals(destCityId, flight.getDestination_city_FK(),
                    "El vuelo debe tener el destino correcto");
            Assertions.assertEquals(1, flight.getStatus_FK(),
                    "El vuelo debe tener estado activo");
        }
    }

    @Test
    void testAvailableSeats_returnsCorrectSeats() throws SQLException {
        // Arrange
        Flight testFlight = flightDAO.getByOriginCity(cityDAO.getByName("City1").getId()).get(0);
        int airplaneId = testFlight.getAirplane_FK();

        // Verifica que hay asientos de clase BUSINESS y que son de ventana
        List<Seat> allSeats = seatDAO.getByAirplaneId(airplaneId);
        Assertions.assertFalse(allSeats.isEmpty(), "Debe haber asientos para el avión de prueba.");

        // Act
        ArrayList<Seat> availableBusinessWindowSeats = reservationService.availableSeats(
                testFlight,
                Seat.SeatClass.BUSINESS,
                true
        );

        // Assert
        Assertions.assertFalse(availableBusinessWindowSeats.isEmpty(), "Debe retornar al menos un asiento disponible.");
        for (Seat seat : availableBusinessWindowSeats) {
            Assertions.assertEquals(Seat.SeatClass.BUSINESS, seat.getSeat_class(), "Clase del asiento incorrecta.");
            Assertions.assertTrue(seat.getIs_window(), "El asiento debería ser de ventana.");
            Assertions.assertEquals(null, seat.getReservation_FK(), "El asiento ya está reservado.");
        }
    }
}