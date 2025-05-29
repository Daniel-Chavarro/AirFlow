package org.airflow.reservations.DAO;

import org.airflow.reservations.model.ReservationStatus;
import org.airflow.reservations.utils.ConnectionDB;

import java.sql.*;
import java.util.ArrayList;

/**
 * Data Access Object (DAO) class for managing ReservationStatus entities.
 * This class provides methods to perform CRUD operations on ReservationStatus objects in the database.
 * It implements the DAOMethods interface for generic DAO operations.
 *
 * @see DAOMethods
 * @see ReservationStatus
 */
public class ReservationStatusDAO implements DAOMethods<ReservationStatus> {
    private Connection connection;

    /**
     * Default constructor for ReservationStatusDAO class.
     * Initializes the ReservationStatusDAO with a new database connection.
     *
     * @throws SQLException if a database access error occurs
     */
    public ReservationStatusDAO() throws SQLException {
        connection = ConnectionDB.getConnection();
    }

    /**
     * Constructor for ReservationStatusDAO class.
     * Initializes the ReservationStatusDAO with a specific connection.
     *
     * @param connection the connection to be used by the DAO
     */
    public ReservationStatusDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Returns all reservation statuses from the database.
     *
     * @return an ArrayList of ReservationStatus objects representing all reservation statuses in the database
     * @throws SQLException if a database access error occurs
     */
    @Override
    public ArrayList<ReservationStatus> getAll() throws SQLException {
        String query = "SELECT * FROM reservations_status";

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        ArrayList<ReservationStatus> statuses = transformResultsToClassArray(resultSet);
        statement.close();

        return statuses;
    }

    /**
     * Returns a ReservationStatus object based on the provided ID.
     *
     * @param id the unique identifier of the reservation status to be retrieved
     * @return a ReservationStatus object with the specified ID
     * @throws SQLException if a database access error occurs
     */
    @Override
    public ReservationStatus getById(int id) throws SQLException {
        String query = "SELECT * FROM reservations_status WHERE id_PK = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, id);

        ResultSet resultSet = statement.executeQuery();

        ReservationStatus status = transformResultsToClass(resultSet);
        statement.close();
        return status;
    }

    /**
     * Inserts a new reservation status into the database.
     *
     * @param object the ReservationStatus object to be created in the database
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void create(ReservationStatus object) throws SQLException {
        String query = "INSERT INTO reservations_status (id_PK, name, description) VALUES (?, ?, ?)";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, object.getId());
        statement.setString(2, object.getName());
        statement.setString(3, object.getDescription());

        statement.executeUpdate();
        statement.close();
    }

    /**
     * Updates an existing reservation status in the database.
     *
     * @param id       the unique identifier of the reservation status to be updated
     * @param toUpdate the ReservationStatus object containing updated data
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void update(int id, ReservationStatus toUpdate) throws SQLException {
        String query = "UPDATE reservations_status SET name = ?, description = ? WHERE id_PK = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, toUpdate.getName());
        statement.setString(2, toUpdate.getDescription());
        statement.setInt(3, id);

        statement.executeUpdate();
        statement.close();
    }

    /**
     * Deletes a reservation status from the database based on the provided ID.
     *
     * @param id the unique identifier of the reservation status to be deleted
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void delete(int id) throws SQLException {
        String query = "DELETE FROM reservations_status WHERE id_PK = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, id);

        statement.executeUpdate();
        statement.close();
    }

    /**
     * Transforms the results from a ResultSet into a ReservationStatus object.
     *
     * @param resultSet the ResultSet containing reservation status data
     * @return a ReservationStatus object populated with data from the ResultSet
     * @throws SQLException if a database access error occurs
     */
    private ReservationStatus transformResultsToClass(ResultSet resultSet) throws SQLException {
        ReservationStatus status = new ReservationStatus();

        if (resultSet.next()) {
            status.setId(resultSet.getInt("id_PK"));
            status.setName(resultSet.getString("name"));
            status.setDescription(resultSet.getString("description"));
        }

        return status;
    }

    /**
     * Transforms the results from a ResultSet into an ArrayList of ReservationStatus objects.
     *
     * @param resultSet the ResultSet containing reservation status data
     * @return an ArrayList of ReservationStatus objects populated with data from the ResultSet
     * @throws SQLException if a database access error occurs
     */
    private ArrayList<ReservationStatus> transformResultsToClassArray(ResultSet resultSet) throws SQLException {
        ArrayList<ReservationStatus> statuses = new ArrayList<>();

        while (resultSet.next()) {
            ReservationStatus status = new ReservationStatus();
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
