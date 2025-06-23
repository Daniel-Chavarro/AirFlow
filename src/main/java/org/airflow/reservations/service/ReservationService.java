package org.airflow.reservations.service;

import org.airflow.reservations.DAO.*;
import org.airflow.reservations.model.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;

public class ReservationService {
    private ReservationDAO reservationDAO;
    private FlightDAO flightDAO;
    private SeatDAO SeatDAO;
    private CityDAO cityDAO;
    private User User;

    public ReservationService(User User) throws Exception {
        this.flightDAO = new FlightDAO();
        this.reservationDAO = new ReservationDAO();
        this.SeatDAO = new SeatDAO();
        this.cityDAO = new CityDAO();
        this.User = User;
    }

    /**
     * Function to get all the available flights between two cities.
     *@param Destiny_id: id of the destination city
     *@param Origin_id: id of the origin city
     *@return: an arraylist of flights that are available between the two cities.
     *@throws SQLException: if a database access error occurs.
     */

    public ArrayList<Flight> availableFlights( int Destiny_id , int Origin_id) throws SQLException {
        ArrayList<Flight> avDestinies = flightDAO.getByDestinationCity(Destiny_id);
        ArrayList<Flight> avOrigins = flightDAO.getByOriginCity(Origin_id);

        ArrayList<Flight> avFlights = new ArrayList<>();
        if (avDestinies == null || avOrigins == null) {
            return avFlights;
        }

        ArrayList<Flight> Min = avOrigins.size()<avDestinies.size()? avOrigins:avDestinies;
        ArrayList<Flight> Max = avOrigins.size()>=avDestinies.size()? avOrigins:avDestinies;

        for (Flight f : Min){
            for (Flight g : Max){
                if (f.getId() == g.getId()){
                    if (f.getStatus_FK() == 1) avFlights.add(f);
                    break;
                }
            }
        }
        return avFlights;
    }

    /**
     * Function to get all the available seats for a flight.
     * @param selectedFlight: the flight to be checked
     * @param wantedClass: the class of the seat to be checked
     * @param wantedWindow: true if the seat is a window seat, false otherwise
     * @return: an arraylist of seats that are available for the flight
     * @throws SQLException: if a database access error occurs.
    */

    public ArrayList<Seat> availableSeats(Flight selectedFlight, Seat.SeatClass wantedClass , boolean wantedWindow) throws SQLException{
        ArrayList<Seat> allSeats = SeatDAO.getByAirplaneId(selectedFlight.getAirplane_FK());
        ArrayList<Seat> avSeats = new ArrayList<>();

        for ( Seat s : allSeats ){
            Seat.SeatClass seatClass = s.getSeat_class();
            boolean Window = s.getIs_window();
            if (seatClass == wantedClass && Window == wantedWindow){
                avSeats.add(s);
            }
        }
        return avSeats;
    }

    /**
     * Function to select a seat from a list of available seats.
     * @param avSeats : the list of available seats to be selected from.
     * @return: the selected seat
     * @throws SQLException: if a database access error occurs.
     */

    public Seat selectseat(ArrayList<Seat> avSeats) throws SQLException{
        System.out.println("Available seats: ");
        for (Seat s : avSeats){
            System.out.println(s.toString());
        }
        int seatNumber = 0;
        while (true) {
            try {
                System.out.println("Input the seat Id you want to book: ");
                seatNumber = Integer.parseInt(System.console().readLine());
                for (Seat s : avSeats) {
                    if (s.getId()== seatNumber){
                        return s;
                    }
                }
                System.out.println("Error: Please put a ID of the provided list of seats.");
            } catch (NumberFormatException e) {
                System.out.println("Error: Please put a valid ID.");
            }
        }
    }

    /**
     * Function to select a flight from a list of available flights.
     * @param avFlights : the list of available flights to be selected from.
     * @return the selected flight.
     * @throws SQLException: if a database access error occurs.
     */

    public Flight selectFlight(ArrayList<Flight> avFlights) throws Exception {
        System.out.println("Available flights: ");
        for (Flight f : avFlights){
            System.out.println(f.toString());
        }

        while (true) {
            System.out.println("input the flight Id you want to book: ");
            String selection = System.console().readLine();
            try {
                int flightNumberInt = Integer.parseInt(selection);
                boolean flightFound = false;
                for (Flight f : avFlights) {
                    if (f.getId() == flightNumberInt) {
                        flightFound = true;
                        return f;
                    }
                }
                if (!flightFound) {
                    System.out.println("Error: Please put a ID of the provided list of flights.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Please put a valid ID.");
            }
        }
    }

    /**
     * Function to make a reservation.
     * @param selectedFlight : the flight to be reserved.
     * @param selectedSeat : the seat to be reserved.
     * @param User : the user who is making the reservation(PREVIOSLY LOGGED IN).
     * @throws SQLException : if a database access error occurs.
     */

    public void makeReservation(Flight selectedFlight, Seat selectedSeat, User User) throws SQLException{
        Reservation newReservation = new Reservation();
        newReservation.setUser_FK(User.getId());
        newReservation.setStatus_FK(3);
        newReservation.setFlight_FK(selectedFlight.getId());
        reservationDAO.create(newReservation);
        newReservation.setId(reservationDAO.getByFlightIdAndUserId(selectedFlight.getId(),User.getId()).getId());

        System.out.println("Dear user, your reservation is just complete, please confirm that the data of your reservation is correct.");
        System.out.println(selectedFlight.toString() + "\n" + selectedSeat.toString());
        System.out.println("Press 0 to cancel the reservation...or any key to continue .");

        //Aquí se puede introducir una sección de checkout o pago

        String decision = System.console().readLine();
        if (decision != null && !decision.trim().isEmpty()) {
            if (decision.charAt(0) == '0'){
                System.out.println("Reservation canceled successfully!");
                reservationDAO.delete(newReservation.getId());
                return;}
        }
        System.out.println("Confirming reservation...please wait...");

        newReservation.setStatus_FK(1);
        reservationDAO.update(newReservation.getId(), newReservation);
        selectedSeat.setReservation_FK(newReservation.getId());
        SeatDAO.update(selectedSeat.getId(), selectedSeat);

        System.out.println("Reservation created successfully!");
    }

    /**
     * @return : ArrayList of available flights depending on the origin and destiny cities
     * @throws SQLException :  if a database access error occurs.
     */
    public ArrayList<Flight> select_cities() throws SQLException , Exception{
        cityDAO.getAll().forEach(c -> System.out.println(c.getId() + " - " + c.getName()));
        System.out.println("TO SELECT A CITY, PUT THE ID OF THE CITY YOU WANT TO BOOK: ");

        int originId = 0;
        int destinyId = 0;
        boolean inputValid = false;

        while (!inputValid) {
            try {
                System.out.println("Choose the city of origin: ");
                originId = Integer.parseInt(System.console().readLine());
                System.out.println("Choose the city of destination: ");
                destinyId = Integer.parseInt(System.console().readLine());
                inputValid = true;
            } catch (NumberFormatException e) {
                System.out.println("Error: Please put a valid ID.");
            }
        }

        ArrayList<Flight> flights = availableFlights(destinyId, originId);
        if (flights.isEmpty()) {
            System.out.println("No available flights to required cities");
            System.out.println("Press any key to look for another city...or 0 to exit of the reservation system.");

            String decision = System.console().readLine();
            if(decision != null && !decision.trim().isEmpty()) {
                if (decision.charAt(0) == '0') throw new ExitMenuException();
            }
            return select_cities();
        }
        return flights;
    }

    /**
     *
     * @return Seat class that user prefers
     * @throws SQLException if database access error occurs
     */
    public Seat.SeatClass selectClass() throws SQLException{
        System.out.println("""
                To filter the available seats, please select the class of the seat you want to book:
                1 - Economy
                2 - Business
                3 - First Class
                """);
        int selectedSeatClass = 0;
        boolean inputValid2 = false;
        Seat.SeatClass seatClass = null;
        while (!inputValid2) {
            try {
                selectedSeatClass = Integer.parseInt(System.console().readLine());
                if (selectedSeatClass == 1) {seatClass = Seat.SeatClass.ECONOMY;inputValid2 = true;}
                else if (selectedSeatClass == 2){seatClass = Seat.SeatClass.BUSINESS;inputValid2 = true;}
                else if (selectedSeatClass == 3) {seatClass = Seat.SeatClass.FIRST;inputValid2 = true;}
                else {System.out.println("Error: Please select a valid class."); return selectClass();}
            } catch (IllegalArgumentException e) {
                System.out.println("Error: Please put a valid class.");
                return selectClass();
            }
        }
        return seatClass;
    }

    /**
     *
     * @return true if user wants window seat false otherwise
     * @throws SQLException :if database access error occurs
     */
    public boolean selectWindow() throws SQLException{
        System.out.println("To select a window seat, please put 1. To select a normal seat, please put 0.");
        int selectedWindow = 0;
        boolean inputValid3 = false;
        while (!inputValid3) {
            try {
                selectedWindow = Integer.parseInt(System.console().readLine());
                if (selectedWindow == 1) {selectedWindow = 1; inputValid3 = true;}
                else if (selectedWindow == 0) {selectedWindow = 0; inputValid3 = true;}
                else {System.out.println("Error: Please select a valid window."); return selectWindow();}
            }
            catch (IllegalArgumentException e) {
                System.out.println("Error: Please put a valid window.");
                return selectWindow();
            }
        }
        return selectedWindow == 1;
    }

    /**
     *
     * @param selectedFlight : Flight selected
     * @return Selected Seat
     * @throws SQLException :if database access error occurs
     */

    public ArrayList<Seat> avSeatsMenu(Flight selectedFlight) throws SQLException, Exception{
        Seat.SeatClass seatClass = selectClass();
        boolean selectedWindow = selectWindow();

        ArrayList<Seat> avSeats = availableSeats(selectedFlight, seatClass, selectedWindow );
        if (avSeats.isEmpty()) {
            System.out.println("No available seats to required class and window");
            System.out.println("""
                    Press 1 to look for another class or window seat
                    Press any other key to exit of the reservation system.""");
            String decision = System.console().readLine();
            if (decision != null && !decision.trim().isEmpty()) {
                if (decision.charAt(0) == '1') avSeats = avSeatsMenu(selectedFlight);
                else throw new ExitMenuException();
            }
            else throw new ExitMenuException();

        }
        return avSeats;
    };
    /**
     * Function to display the main menu.
     * @throws Exception : if a database access error occurs.
     */

    public void mainMenu() throws Exception {
        try {
            while (true) {
                System.out.println("¡Welcome to reservation system! this is the cities available for booking:");
                ArrayList<Flight> flights = select_cities();
                Flight selectedFlight = selectFlight(flights);
                ArrayList<Seat> avSeats = avSeatsMenu(selectedFlight);

                Seat selectedSeat = selectseat(avSeats);
                makeReservation(selectedFlight, selectedSeat, User);
                System.out.println("Thank you for using our reservation system!");
                System.out.println("Press 1 to look for another flight...or any other key to exit of the reservation system.");
                String decision = System.console().readLine();
                if (decision != null && !decision.trim().isEmpty()) {
                    if (decision.charAt(0) != '1') {
                        throw new ExitMenuException();
                    }
                    else mainMenu();
                }
                else throw new ExitMenuException();
            }
        } catch (ExitMenuException e) {
            System.out.println("Exiting reservation system. Thank you!");
        }
    }
}

 class ExitMenuException extends Exception {
    public ExitMenuException() {
        super("Exit signal");
    }
}