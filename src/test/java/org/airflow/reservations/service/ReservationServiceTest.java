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

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReservationServiceTest {
    private Connection connection;
    private AirplaneDAO airplaneDAO;
    private CityDAO cityDAO;
    private FlightDAO flightDAO;
    private SeatDAO seatDAO;
    private UsersDAO usersDAO;
    private User testUser;
    private ReservationService reservationService;
    private ReservationDAO reservationDAO;
    private SeatService seatService;


    private int testSeat_Bussiness_Window ;
    private int testSeat_First_Window ;
    private int testSeat_Economy_Window ;
    private int testSeat_Bussiness_Seat ;
    private int testSeat_First_Seat ;
    private int testSeat_Economy_Seat ;
    private int testFlight1Id;
    private int testFlight2Id;
    private int userId;


    @BeforeEach
    void setUp() throws Exception {
        connection = ConnectionDB.getConnection();
        airplaneDAO = new AirplaneDAO(connection);
        cityDAO = new CityDAO(connection);
        flightDAO = new FlightDAO(connection);
        seatDAO = new SeatDAO(connection);
        usersDAO = new UsersDAO(connection);
        reservationDAO = new ReservationDAO(connection);

        testUser = new User(0, "Test", "User", "testuser@example.com",
                "password", true, LocalDateTime.now());
        usersDAO.create(testUser);
        testUser = usersDAO.getByEmail("testuser@example.com");

        seatService = new SeatService(seatDAO);
        reservationService = new ReservationService(testUser,reservationDAO,flightDAO,seatDAO,cityDAO,seatService);


        // Insert test cities
        cityDAO.create(new City(0, "City1", "testCountry1", "testCode1"));
        cityDAO.create(new City(0, "City2", "testCountry2", "testCode2"));
        // Inset test airplanes
        airplaneDAO.create(new Airplane(0, "testAirline", "testModel", "TSTCDE1", 10, Year.now()));
        airplaneDAO.create(new Airplane(0, "testAirline", "testModel", "TSTCDE2", 10, Year.now()));
        // Insert test flights

        flightDAO.create(new Flight(0, airplaneDAO.getByCode("TSTCDE2").getId(), 1,
                cityDAO.getByName("City2").getId(), cityDAO.getByName("City1").getId(),
                "testCode", LocalDateTime.now().plusDays(3), LocalDateTime.now(),
                LocalDateTime.now(), 0));
        flightDAO.create(new Flight(0, airplaneDAO.getByCode("TSTCDE1").getId(), 1,
                cityDAO.getByName("City1").getId(), cityDAO.getByName("City2").getId(),
                "testCode", LocalDateTime.now().plusDays(3), LocalDateTime.now(),
                LocalDateTime.now(), 0));

        // Insert test seats
        seatDAO.create(new Seat(0, airplaneDAO.getByCode("TSTCDE1").getId(), null,
                "T00", Seat.SeatClass.BUSINESS, true)); // Available seat
        seatDAO.create(new Seat(0, airplaneDAO.getByCode("TSTCDE2").getId(), null,
                "T01", Seat.SeatClass.BUSINESS, false));// Another seat
        seatDAO.create(new Seat(0, airplaneDAO.getByCode("TSTCDE1").getId(), null,
                "T02", Seat.SeatClass.FIRST, true));
        seatDAO.create(new Seat(0, airplaneDAO.getByCode("TSTCDE2").getId(), null,
                "T03", Seat.SeatClass.FIRST, false));
        seatDAO.create(new Seat(0, airplaneDAO.getByCode("TSTCDE1").getId(), null,
                "T04", Seat.SeatClass.ECONOMY, true));
        seatDAO.create(new Seat(0, airplaneDAO.getByCode("TSTCDE2").getId(), null,
                "T05", Seat.SeatClass.ECONOMY, false));

         userId = usersDAO.getByEmail("testuser@example.com").getId();
         testFlight1Id = flightDAO.getByOriginCity(cityDAO.getByName("City2").getId()).get(0).getId();
         testFlight2Id = flightDAO.getByOriginCity(cityDAO.getByName("City1").getId()).get(0).getId();

        ArrayList<Seat> testSeats = seatDAO.getByAirplaneId(airplaneDAO.getByCode("TSTCDE1").getId());
        ArrayList<Seat> testSeats1 = seatDAO.getByAirplaneId(airplaneDAO.getByCode("TSTCDE2").getId());
        testSeats.addAll(testSeats1);


        for (Seat s:testSeats){
            switch (s.getSeat_number()) {
                case "T00" -> testSeat_Bussiness_Window = s.getId();
                case "T01" -> testSeat_Bussiness_Seat = s.getId();
                case "T02" -> testSeat_First_Window = s.getId();
                case "T03" -> testSeat_First_Seat = s.getId();
                case "T04" -> testSeat_Economy_Window = s.getId();
                case "T05" -> testSeat_Economy_Seat = s.getId();
            }
        }

    }

    @AfterEach
    void tearDown() throws SQLException {
        // Delete test data
        ArrayList<Seat> testSeats = seatDAO.getByAirplaneId(airplaneDAO.getByCode("TSTCDE1").getId());
        ArrayList<Seat> testSeats1 = seatDAO.getByAirplaneId(airplaneDAO.getByCode("TSTCDE2").getId());
        testSeats.addAll(testSeats1);

        for (Seat seat : testSeats) {
            seatDAO.delete(seat.getId());
        }
        for (Reservation reservation : reservationDAO.getByUserId(testUser.getId())) {
            reservationDAO.delete(reservation.getId());
        }

        usersDAO.delete(usersDAO.getByEmail("testuser@example.com").getId());
        flightDAO.delete(flightDAO.getByOriginCity(cityDAO.getByName("City1").getId()).get(0).getId());
        flightDAO.delete(flightDAO.getByOriginCity(cityDAO.getByName("City2").getId()).get(0).getId());
        cityDAO.delete(cityDAO.getByName("City1").getId());
        cityDAO.delete(cityDAO.getByName("City2").getId());

        airplaneDAO.delete(airplaneDAO.getByCode("TSTCDE1").getId());
        airplaneDAO.delete(airplaneDAO.getByCode("TSTCDE2").getId());
    }


    @Test
    void test1_createReservationLessThanThreeHoursBeforeFlight_shouldFail()throws SQLException {
        Flight flight = flightDAO.getById(testFlight1Id);
        flight.setDeparture_time(LocalDateTime.now().plusHours(2));
        flightDAO.update(flight.getId(), flight);

        int[] seatIds = new int[]{testSeat_Economy_Seat};

        Exception ex = assertThrows(IllegalArgumentException.class, () -> reservationService.createReservation(flight.getId(), seatIds));
        assertTrue(ex.getMessage().contains("No se puede reservar este asiento"));
    }

    @Test
    void test2_createReservationMoreThanThreeHoursBeforeFlight_shouldSucceed() throws SQLException {
        Flight flight = flightDAO.getById(testFlight1Id);
        flight.setDeparture_time(LocalDateTime.now().plusHours(4));
        flightDAO.update(flight.getId(), flight);

        int[] seatIds = new int[]{testSeat_Economy_Seat};
        reservationService.createReservation(flight.getId(), seatIds);

        List<Reservation> reservations =  reservationService.FindReservation_byUserId();
        assertEquals(1, reservations.size());
        assertEquals(3, reservations.get(0).getStatus_FK());
    }

    @Test
    void test3_createReservationOnCancelledFlight_shouldFail()throws SQLException {
        Flight flight = flightDAO.getById(testFlight1Id);
        flight.setStatus_FK(3);
        flightDAO.update(flight.getId(), flight);

        int[] seatIds = new int[]{testSeat_Economy_Seat};
        Exception ex = assertThrows(IllegalArgumentException.class, () -> reservationService.createReservation(flight.getId(), seatIds));
        assertTrue(ex.getMessage().contains("No se puede reservar este asiento"));
    }

    @Test
    void test4_reserveSeatFromDifferentAirplane_shouldFail()throws SQLException {
        Flight flight = flightDAO.getById(testFlight1Id);
        flight.setDeparture_time(LocalDateTime.now().plusHours(5));
        flightDAO.update(flight.getId(), flight);

        int[] seatIds = new int[]{testSeat_Economy_Window}; // seat from TSTCDE1
        Exception ex = assertThrows(IllegalArgumentException.class, () -> reservationService.createReservation(flight.getId(), seatIds));
        assertTrue(ex.getMessage().contains("No se puede reservar este asiento"));
    }

    @Test
    void test5_createAndFindReservation_shouldSucceed() throws SQLException {
        Flight flight = flightDAO.getById(testFlight1Id);
        flight.setDeparture_time(LocalDateTime.now().plusHours(4));
        flightDAO.update(flight.getId(), flight);

        int[] seatIds = new int[]{testSeat_Economy_Seat};
        reservationService.createReservation(flight.getId(), seatIds);
        ArrayList<Reservation> reservations = reservationService.FindReservation_byUserId();
        assertFalse(reservations.isEmpty());
    }

    @Test
    void test6_cancelReservationWithLessThan12Hours_shouldFail() throws SQLException {
        Flight flight = flightDAO.getById(testFlight1Id);
        flight.setDeparture_time(LocalDateTime.now().plusHours(8));
        flightDAO.update(flight.getId(), flight);
        int[] seatIds = new int[]{testSeat_Economy_Seat};

        reservationService.createReservation(flight.getId(), seatIds);
        Reservation reservation = reservationService.FindReservation_byUserId().get(0);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> reservationService.cancelReservation(reservation.getId()));
        assertTrue(ex.getMessage().contains("No se puede cancelar la reserva"));
    }

    @Test
    void test7_cancelReservationMoreThan12Hours() throws SQLException {
        Flight flight = flightDAO.getById(testFlight1Id);
        flight.setDeparture_time(LocalDateTime.now().plusHours(24));
        flightDAO.update(flight.getId(), flight);
        int[] seatIds = new int[]{testSeat_Bussiness_Seat, testSeat_First_Seat};

        reservationService.createReservation(flight.getId(), seatIds);
        Reservation reservation = reservationService.FindReservation_byUserId().get(0);

        reservationService.cancelReservation(reservation.getId());
        ArrayList<Reservation> updated = reservationService.FindReservation_byUserId();
        assertTrue(updated.isEmpty());
    }

    @Test
    void test8_Deleting2Of3Seats_shouldSucceed() throws SQLException {
        Flight flight = flightDAO.getById(testFlight1Id);
        flight.setDeparture_time(LocalDateTime.now().plusHours(20));
        flightDAO.update(flight.getId(), flight);
        int[] seats = new int[]{testSeat_Bussiness_Seat, testSeat_First_Seat, testSeat_Economy_Seat};

        reservationService.createReservation(flight.getId(), seats);
        Reservation reservation = reservationService.FindReservation_byUserId().get(0);

        ArrayList<Integer> delete = new ArrayList<>(List.of(testSeat_Bussiness_Seat, testSeat_First_Seat));
        reservationService.deleteSeatsfromReservation(reservation.getId(), delete);
        reservation = reservationService.FindReservation_byUserId().get(0);
        assertEquals(3, reservation.getStatus_FK());
        assertEquals(1,seatService.getSeatsByReservationId(reservation.getId()).size());
    }

    @Test
    void test9_cancelReservationAfterDeletingAllSeats_shouldSucceed() throws SQLException {
        Flight flight = flightDAO.getById(testFlight1Id);
        flight.setDeparture_time(LocalDateTime.now().plusHours(20));
        flightDAO.update(flight.getId(), flight);
        int[] seats = new int[]{testSeat_Bussiness_Seat, testSeat_First_Seat, testSeat_Economy_Seat};

        reservationService.createReservation(flight.getId(), seats);
        Reservation reservation = reservationService.FindReservation_byUserId().get(0);
        ArrayList<Integer> delete = new ArrayList<>(List.of(testSeat_Bussiness_Seat, testSeat_First_Seat,
                testSeat_Economy_Seat));

        reservationService.deleteSeatsfromReservation(reservation.getId(), delete);

        assertEquals(0, reservationService.FindByReservationId(reservation.getId()).getId());

    }

    @Test
    void test10_deleteSeatsFromNonCancelableReservation_shouldFail() throws SQLException {
        Flight flight = flightDAO.getById(testFlight1Id);
        flight.setDeparture_time(LocalDateTime.now().plusHours(6));
        flightDAO.update(flight.getId(), flight);
        int[] seats = new int[]{testSeat_Bussiness_Seat, testSeat_First_Seat, testSeat_Economy_Seat};

        reservationService.createReservation(flight.getId(), seats);
        Reservation reservation = reservationService.FindReservation_byUserId().get(0);
        ArrayList<Integer> delete = new ArrayList<>(List.of(testSeat_Bussiness_Seat));

        Exception ex = assertThrows(IllegalArgumentException.class, () -> reservationService.deleteSeatsfromReservation(reservation.getId(), delete));
        assertTrue(ex.getMessage().contains("Error al cancelar asientos"));
    }

    @Test
    void test11_confirmReservation() throws Exception {
        Reservation reservation = reservationService.createReservation(
                flightDAO.getById(testFlight1Id).getId(),
                new int[]{testSeat_Economy_Seat});

        reservationService.confirmReservation(reservation.getId());

        Reservation confirmed = reservationService.FindReservation_byUserId().get(0);
        assertEquals(1, confirmed.getStatus_FK());
    }
    @Test
    void test12_departureTimeChanged_thenCancelledByControl() throws Exception {
        Reservation reservation = reservationService.createReservation(
                flightDAO.getById(testFlight1Id).getId(),
                new int[]{testSeat_Economy_Seat});

        Flight flight = flightDAO.getById(reservation.getFlight_FK());
        flight.setDeparture_time(LocalDateTime.now());
        flightDAO.update(flight.getId(), flight);

        reservationService.reservations_control();

        Reservation updated = reservationService.FindReservation_byUserId().get(0);
        assertEquals(2, updated.getStatus_FK());
    }

    @Test
    void test13_cancelThenTryToConfirm() throws Exception {
        Reservation reservation = reservationService.createReservation(
                flightDAO.getById(testFlight1Id).getId(),
                new int[]{testSeat_Economy_Seat});

        Flight flight = flightDAO.getById(reservation.getFlight_FK());
        flight.setDeparture_time(LocalDateTime.now());
        flightDAO.update(flight.getId(), flight);

        reservationService.reservations_control();
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                reservationService.confirmReservation(reservation.getId()));
        assertTrue(ex.getMessage().contains("No es posible confirmar"));
    }

    @Test
    void test14_checkInWithNoConfirmation() throws Exception {
        Reservation reservation = reservationService.createReservation(
                flightDAO.getById(testFlight1Id).getId(),
                new int[]{testSeat_Economy_Seat});

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                reservationService.check_inReservation(reservation.getId()));
        assertTrue(ex.getMessage().contains("No se puede confirmar el check in"));
    }
    @Test
    void test15_confirmThenCancelViaControlThenTryCheckIn() throws Exception {
        Reservation reservation = reservationService.createReservation(
                flightDAO.getById(testFlight1Id).getId(),
                new int[]{testSeat_Economy_Seat});

        reservationService.confirmReservation(reservation.getId());

        Flight flight = flightDAO.getById(reservation.getFlight_FK());
        flight.setDeparture_time(LocalDateTime.now());
        flightDAO.update(flight.getId(), flight);

        reservationService.reservations_control();

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                reservationService.check_inReservation(reservation.getId()));
        assertTrue(ex.getMessage().contains("No se puede confirmar el check in"));
    }
    @Test
    void test16_confirmAndCheckIn() throws Exception {
        Reservation reservation = reservationService.createReservation(
                flightDAO.getById(testFlight1Id).getId(),
                new int[]{testSeat_Economy_Seat});

        reservationService.confirmReservation(reservation.getId());
        reservationService.check_inReservation(reservation.getId());

        Reservation checkedIn = reservationService.FindReservation_byUserId().get(0);
        assertEquals(4, checkedIn.getStatus_FK());
    }
    @Test
    void test17_completeThreeReservations() throws Exception {
        List<Reservation> reservations = new ArrayList<>();
        Reservation r;

         r = reservationService.createReservation(
                flightDAO.getById(testFlight1Id).getId(),
                new int[]{testSeat_Economy_Seat});
        reservationService.confirmReservation(r.getId());
        reservationService.check_inReservation(r.getId());
        reservations.add(r);

         r = reservationService.createReservation(
                flightDAO.getById(testFlight1Id).getId(),
                new int[]{testSeat_Bussiness_Seat});
        reservationService.confirmReservation(r.getId());
        reservationService.check_inReservation(r.getId());
        reservations.add(r);

        r = reservationService.createReservation(
                flightDAO.getById(testFlight1Id).getId(),
                new int[]{testSeat_First_Seat});
        reservationService.confirmReservation(r.getId());
        reservationService.check_inReservation(r.getId());
        reservations.add(r);


        Flight flight = flightDAO.getById(reservations.get(0).getFlight_FK());
        flight.setStatus_FK(7); // Completed
        flightDAO.update(flight.getId(), flight);

        reservationService.completed_reservations(flight.getId());

        for (Reservation s :  reservationService.FindReservation_byUserId()) {
            assertEquals(5, s.getStatus_FK());
        }
    }
    @Test
    void test18_incompleteReservationsShouldNotBeCompleted() throws Exception {
        reservationService.createReservation(
                flightDAO.getById(testFlight1Id).getId(),
                new int[]{testSeat_Economy_Seat});

        Flight flight = flightDAO.getAll().get(0);
        flight.setStatus_FK(7); // Completed
        flightDAO.update(flight.getId(), flight);

        reservationService.completed_reservations(flight.getId());

        Reservation r =  reservationService.FindReservation_byUserId().get(0);
        assertNotEquals(5, r.getStatus_FK());
    }
    @Test
    void test19_checkedInShouldNotBeCancelledByControl() throws Exception {
        Reservation reservation = reservationService.createReservation(
                flightDAO.getById(testFlight1Id).getId(),
                new int[]{testSeat_Economy_Seat});

        reservationService.confirmReservation(reservation.getId());
        reservationService.check_inReservation(reservation.getId());

        Flight flight = flightDAO.getById(reservation.getFlight_FK());
        flight.setDeparture_time(LocalDateTime.now());
        flightDAO.update(flight.getId(), flight);

        reservationService.reservations_control();

        Reservation r = reservationService.FindReservation_byUserId().get(0);
        assertNotEquals(2, r.getStatus_FK());
        assertEquals(4, r.getStatus_FK());
    }






}