package org.airflow.reservations.service;

import org.airflow.reservations.DAO.*;
import org.airflow.reservations.model.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance;


import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

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
        int testUserId = usersDAO.getByEmail("testuser@example.com").getId();
        int testFlightId = flightDAO.getByOriginCity(cityDAO.getByName("City1").getId()).get(0).getId();
        ArrayList<Seat> testSeats = seatDAO.getByAirplaneId(airplaneDAO.getByCode("TSTCDE").getId());
        int TestSeat_Bussiness_Window ;
        int TestSeat_First_Window ;
        int TestSeat_Economy_Window ;
        int TestSeat_Bussiness_Seat ;
        int TestSeat_First_Seat ;
        int TestSeat_Economy_Seat ;
        for (Seat s:testSeats){
            if (Objects.equals(s.getSeat_number(), "T00")) TestSeat_Bussiness_Window = s.getId();
            if (Objects.equals(s.getSeat_number(), "T02")) TestSeat_First_Window = s.getId();
            if (Objects.equals(s.getSeat_number(), "T04")) TestSeat_Economy_Window = s.getId();
            if (Objects.equals(s.getSeat_number(), "T01")) TestSeat_Bussiness_Seat = s.getId();
            if (Objects.equals(s.getSeat_number(), "T03")) TestSeat_First_Seat = s.getId();
            if (Objects.equals(s.getSeat_number(), "T05")) TestSeat_Economy_Seat = s.getId();
        }

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


}