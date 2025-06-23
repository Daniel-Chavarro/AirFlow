package org.airflow.reservations.service;

import org.airflow.reservations.DAO.*;
import org.airflow.reservations.model.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;

public class manualTesting {
    public static void main(String[] args) throws Exception {
        manualTesting test = new manualTesting();
        test.mainMenu();
    }

    private AirplaneDAO airplaneDAO;
    private CityDAO cityDAO;
    private FlightDAO flightDAO;
    private SeatDAO seatDAO;
    private UsersDAO usersDAO;
    private User testUser;
    private ReservationService reservationService;
    private ReservationDAO reservationDAO;


    public void setUp() throws Exception {
        airplaneDAO = new AirplaneDAO();
        cityDAO = new CityDAO();
        flightDAO = new FlightDAO();
        seatDAO = new SeatDAO();
        usersDAO = new UsersDAO();
        reservationDAO = new ReservationDAO();

        testUser = new User(0, "Test", "User", "testuser@example.com",
                "password", true, LocalDateTime.now());
        usersDAO.create(testUser);
        reservationService = new ReservationService(usersDAO.getByEmail("testuser@example.com"));

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


    public void tearDown() throws SQLException {
        // Delete test data
        ArrayList<Seat> testSeats = seatDAO.getByAirplaneId(airplaneDAO.getByCode("TSTCDE").getId());
        for (Seat seat : testSeats) {
            seatDAO.delete(seat.getId());
            if (seat.getReservation_FK() != null){
                reservationDAO.delete(seat.getReservation_FK());
            }
        }

        usersDAO.delete(usersDAO.getByEmail("testuser@example.com").getId());
        flightDAO.delete(flightDAO.getByOriginCity(cityDAO.getByName("City1").getId()).get(0).getId());
        cityDAO.delete(cityDAO.getByName("City1").getId());
        cityDAO.delete(cityDAO.getByName("City2").getId());
        cityDAO.delete(cityDAO.getByName("City3").getId());

        airplaneDAO.delete(airplaneDAO.getByCode("TSTCDE").getId());
    }

    public void mainMenu() throws Exception {
        setUp();
        reservationService.mainMenu();
        tearDown();
    }
}


