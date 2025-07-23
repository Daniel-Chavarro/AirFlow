package org.airflow.reservations.service;

import org.airflow.reservations.DAO.AirplaneDAO;
import org.airflow.reservations.model.Airplane;

import java.sql.SQLException;

/**
 * Service class for managing airplane-related operations.
 * This class acts as a bridge between the controller and the AirplaneDAO,
 * providing business logic for airplane operations.
 */
public class AirplaneService {
    /** Data Access Object for airplane operations */
    private final AirplaneDAO airplaneDAO;

    /**
     * Constructor that initializes the AirplaneService with a new AirplaneDAO.
     *
     * @throws SQLException if there's an error connecting to the database
     */
    public AirplaneService() throws SQLException {
        this.airplaneDAO = new AirplaneDAO();
    }

    /**
     * Retrieves an airplane by its unique identifier.
     *
     * @param id The unique identifier of the airplane
     * @return The Airplane object if found, or an empty Airplane object if not found
     * @throws SQLException if there's an error executing the database query
     */
    public Airplane getAirplaneById(int id) throws SQLException {
        return airplaneDAO.getById(id);
    }
}
