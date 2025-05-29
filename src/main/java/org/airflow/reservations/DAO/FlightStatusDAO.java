package org.airflow.reservations.DAO;

import org.airflow.reservations.model.FlightStatus;
import org.airflow.reservations.utils.ConnectionDB;

import java.sql.*;
import java.util.ArrayList;

/**
 * Data Access Object (DAO) class for managing FlightStatus entities.
 * This class provides methods to perform CRUD operations on FlightStatus objects in the database.
 * It implements the DAOMethods interface for generic DAO operations.
 *
 * @see DAOMethods
 * @see FlightStatus
 */
public class FlightStatusDAO implements DAOMethods<FlightStatus> {
    private Connection connection;

    /**
     * Default constructor for FlightStatusDAO class.
     * Initializes the FlightStatusDAO with a new database connection.
     *
     * @throws SQLException if a database access error occurs
     */
    public FlightStatusDAO() throws SQLException {
        connection = ConnectionDB.getConnection();
    }

    /**
     * Constructor for FlightStatusDAO class.
     * Initializes the FlightStatusDAO with a specific connection.
     *
     * @param connection the connection to be used by the DAO
     */
    public FlightStatusDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Returns all flight statuses from the database.
     *
     * @return an ArrayList of FlightStatus objects representing all flight statuses in the database
     * @throws SQLException if a database access error occurs
     */
    @Override
    public ArrayList<FlightStatus> getAll() throws SQLException {
        String query = "SELECT * FROM flight_status";

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        ArrayList<FlightStatus> statuses = transformResultsToClassArray(resultSet);
        statement.close();

        return statuses;
    }

    /**
     * Returns a FlightStatus object based on the provided ID.
     *
     * @param id the unique identifier of the flight status to be retrieved
     * @return a FlightStatus object with the specified ID
     * @throws SQLException if a database access error occurs
     */
    @Override
    public FlightStatus getById(int id) throws SQLException {
        String query = "SELECT * FROM flight_status WHERE id_PK = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, id);

        ResultSet resultSet = statement.executeQuery();

        FlightStatus status = transformResultsToClass(resultSet);
        statement.close();
        return status;
    }

    /**
     * Inserts a new flight status into the database.
     *
     * @param object the FlightStatus object to be created in the database
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void create(FlightStatus object) throws SQLException {
        String query = "INSERT INTO flight_status (id_PK, name, description) VALUES (?, ?, ?)";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, object.getId());
        statement.setString(2, object.getName());
        statement.setString(3, object.getDescription());

        statement.executeUpdate();
        statement.close();
    }

    /**
     * Updates an existing flight status in the database.
     *
     * @param id       the unique identifier of the flight status to be updated
     * @param toUpdate the FlightStatus object containing updated data
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void update(int id, FlightStatus toUpdate) throws SQLException {
        String query = "UPDATE flight_status SET name = ?, description = ? WHERE id_PK = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, toUpdate.getName());
        statement.setString(2, toUpdate.getDescription());
        statement.setInt(3, id);

        statement.executeUpdate();
        statement.close();
    }

    /**
     * Deletes a flight status from the database based on the provided ID.
     *
     * @param id the unique identifier of the flight status to be deleted
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void delete(int id) throws SQLException {
        String query = "DELETE FROM flight_status WHERE id_PK = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, id);

        statement.executeUpdate();
        statement.close();
    }

    /**
     * Transforms the results from a ResultSet into a FlightStatus object.
     *
     * @param resultSet the ResultSet containing flight status data
     * @return a FlightStatus object populated with data from the ResultSet
     * @throws SQLException if a database access error occurs
     */
    private FlightStatus transformResultsToClass(ResultSet resultSet) throws SQLException {
        FlightStatus status = new FlightStatus();

        if (resultSet.next()) {
            status.setId(resultSet.getInt("id_PK"));
            status.setName(resultSet.getString("name"));
            status.setDescription(resultSet.getString("description"));
        }

        return status;
    }

    /**
     * Transforms the results from a ResultSet into an ArrayList of FlightStatus objects.
     *
     * @param resultSet the ResultSet containing flight status data
     * @return an ArrayList of FlightStatus objects populated with data from the ResultSet
     * @throws SQLException if a database access error occurs
     */
    private ArrayList<FlightStatus> transformResultsToClassArray(ResultSet resultSet) throws SQLException {
        ArrayList<FlightStatus> statuses = new ArrayList<>();

        while (resultSet.next()) {
            FlightStatus status = new FlightStatus();
            status.setId(resultSet.getInt("id_PK"));
            status.setName(resultSet.getString("name"));
            status.setDescription(resultSet.getString("description"));
            statuses.add(status);
        }

        return statuses;
    }

    // Getters and Setters
    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
