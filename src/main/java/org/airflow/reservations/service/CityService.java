package org.airflow.reservations.service;

import org.airflow.reservations.DAO.CityDAO;
import org.airflow.reservations.model.City;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Service class for managing city-related operations.
 * This class acts as a bridge between the controller and the CityDAO,
 * providing business logic for city operations and handling data retrieval.
 */
public class CityService {
    /** Data Access Object for city operations */
    private final CityDAO cityDAO;

    /**
     * Constructor that initializes the CityService with a new CityDAO.
     *
     * @throws SQLException if there's an error connecting to the database
     */
    public CityService() throws SQLException {
        this.cityDAO = new CityDAO();
    }

    /**
     * Retrieves a city by its name.
     *
     * @param name The name of the city to search for
     * @return The City object if found, or an empty City object if not found
     * @throws SQLException if there's an error executing the database query
     */
    public City getCityByName(String name) throws SQLException {
        return cityDAO.getByName(name);
    }

    /**
     * Retrieves all cities from the database.
     *
     * @return ArrayList containing all City objects in the database
     * @throws SQLException if there's an error executing the database query
     */
    public ArrayList<City> getAllCities() throws SQLException {
        return cityDAO.getAll();
    }

    /**
     * Retrieves a city by its unique identifier.
     *
     * @param id The unique identifier of the city
     * @return The City object if found, or an empty City object if not found
     * @throws SQLException if there's an error executing the database query
     */
    public City getCityById(int id) throws SQLException {
        return cityDAO.getById(id);
    }
}
