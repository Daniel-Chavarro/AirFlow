
package org.airflow.reservations.service;

import org.airflow.reservations.DAO.FlightDAO;
import org.airflow.reservations.model.Flight;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Service class for managing flights using the FlightDAO.
 * Provides methods to perform business logic and validations before interacting with the DAO.
 */
public class FlightService {
    private final FlightDAO flightDAO;

    /**
     * Constructor for FlightService.
     * Initializes the service with a FlightDAO instance and handles SQL exceptions.
     *
     * @throws SQLException if an error occurs while connecting to the database
     */
    public FlightService() throws SQLException {
        this.flightDAO = new FlightDAO();
    }

    public FlightService(FlightDAO flightDAO) {
        this.flightDAO = flightDAO;
    }

    /**
     * Registers a new flight after validating that the flight code is unique
     * and that the departure time is earlier than the arrival time.
     *
     * @param flight the flight to be registered
     * @throws SQLException if a database access error occurs
     * @throws IllegalArgumentException if the flight code already exists or if the time logic is invalid
     */
    public void registerFlight(Flight flight) throws SQLException {
        if (existsFlightWithCode(flight.getCode())) {
            throw new IllegalArgumentException("A flight with this code already exists.");
        }
        if (!flight.getDeparture_time().isBefore(flight.getArrival_time())) {
            throw new IllegalArgumentException("Departure time must be before arrival time.");
        }
        flightDAO.create(flight);
    }

    /**
     * Checks if a flight with the specified code already exists in the database.
     *
     * @param code the flight code to check
     * @return true if a flight with the code exists, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean existsFlightWithCode(String code) throws SQLException {
        ArrayList<Flight> flights = flightDAO.getByCode(code);
        return !flights.isEmpty();
    }

    /**
     * Returns a list of all flights in the system.
     *
     * @return an ArrayList of Flight objects
     * @throws SQLException if a database access error occurs
     */
    public ArrayList<Flight> getAllFlights() throws SQLException {
        return flightDAO.getAll();
    }

    /**
     * Returns the flight with the specified ID.
     *
     * @param id the flight ID
     * @return the Flight object with the specified ID, or null if not found
     * @throws SQLException if a database access error occurs
     */
    public Flight getFlightById(int id) throws SQLException {
        return flightDAO.getById(id);
    }

    /**
     * Returns a list of flights departing from the specified origin city.
     *
     * @param cityId the origin city ID
     * @return an ArrayList of Flight objects
     * @throws SQLException if a database access error occurs
     */
    public ArrayList<Flight> getFlightsByOrigin(int cityId) throws SQLException {
        return flightDAO.getByOriginCity(cityId);
    }

    /**
     * Returns a list of flights arriving at the specified destination city.
     *
     * @param cityId the destination city ID
     * @return an ArrayList of Flight objects
     * @throws SQLException if a database access error occurs
     */
    public ArrayList<Flight> getFlightsByDestination(int cityId) throws SQLException {
        return flightDAO.getByDestinationCity(cityId);
    }

    /**
     * Updates an existing flight with the given ID.
     *
     * @param id the ID of the flight to update
     * @param flight the new flight data
     * @throws SQLException if a database access error occurs
     */
    public void updateFlight(int id, Flight flight) throws SQLException {
        flightDAO.update(id, flight);
    }

    /**
     * Deletes the flight with the given ID.
     *
     * @param id the ID of the flight to delete
     * @throws SQLException if a database access error occurs
     */
    public void deleteFlight(int id) throws SQLException {
        flightDAO.delete(id);
    }

    public ArrayList<Flight> getBydepartureTimeRange(LocalDateTime bottomRange, LocalDateTime TopRange) throws SQLException {
        return flightDAO.getByDepartureTimeRange(bottomRange, TopRange);
    }
}
