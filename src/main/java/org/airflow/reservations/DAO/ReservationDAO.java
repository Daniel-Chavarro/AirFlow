package org.airflow.reservations.DAO;

import org.airflow.reservations.model.*;
import org.airflow.reservations.utils.ConnectionDB;

import java.sql.*;
import java.util.ArrayList;

/**
 * Data Access Object (DAO) class for managing Reservation entities.
 * This class provides methods to perform CRUD operations on Reservation objects in the database.
 * It implements the DAOMethods interface for generic DAO operations.
 *
 * @see DAOMethods
 * @see Reservation
 */
public class ReservationDAO implements DAOMethods<Reservation> {
    private Connection connection;

    /**
     * Default constructor for ReservationDAO class.
     * Initializes the ReservationDAO with a new database connection.
     *
     * @throws SQLException if a database access error occurs
     */
    public ReservationDAO() throws SQLException {
        connection = ConnectionDB.getConnection();
    }

    /**
     * Constructor for ReservationDAO class.
     * Initializes the ReservationDAO with a specific connection.
     *
     * @param connection the connection to be used by the DAO
     */
    public ReservationDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Returns all reservations from the database.
     *
     * @return an ArrayList of Reservation objects representing all reservations in the database
     * @throws SQLException if a database access error occurs
     */
    @Override
    public ArrayList<Reservation> getAll() throws SQLException {
        String query = "SELECT r.*, rs.name as status_name, rs.description as status_description " +
                "FROM reservations r " +
                "JOIN reservations_status rs ON r.status_FK = rs.id_PK";

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        ArrayList<Reservation> reservations = transformResultsToClassArray(resultSet);
        statement.close();

        return reservations;
    }

    /**
     * Returns a Reservation object based on the provided ID.
     *
     * @param id the unique identifier of the reservation to be retrieved
     * @return a Reservation object with the specified ID
     * @throws SQLException if a database access error occurs
     */
    @Override
    public Reservation getById(int id) throws SQLException {
        String query = "SELECT r.*, rs.name as status_name, rs.description as status_description " +
                "FROM reservations r " +
                "JOIN reservations_status rs ON r.status_FK = rs.id_PK " +
                "WHERE r.id_PK = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, id);

        ResultSet resultSet = statement.executeQuery();

        Reservation reservation = transformResultsToClass(resultSet);
        statement.close();
        return reservation;
    }

    /**
     * Inserts a new reservation into the database.
     *
     * @param object the Reservation object to be created in the database
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void create(Reservation object) throws SQLException {
        String query = "INSERT INTO reservations (user_FK, status_FK, flight_FK, reserved_at) VALUES (?, ?, ?, ?)";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, object.getUser_FK());
        statement.setInt(2, object.getStatus_FK());
        statement.setInt(3, object.getFlight_FK());
        statement.setTimestamp(4, Timestamp.valueOf(object.getReserved_at()));

        statement.executeUpdate();
        statement.close();
    }

    /**
     * Updates an existing reservation in the database.
     *
     * @param id       the unique identifier of the reservation to be updated
     * @param toUpdate the Reservation object containing updated data
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void update(int id, Reservation toUpdate) throws SQLException {
        String query = "UPDATE reservations SET user_FK = ?, status_FK = ?, flight_FK = ?, reserved_at = ? WHERE id_PK = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, toUpdate.getUser_FK());
        statement.setInt(2, toUpdate.getStatus_FK());
        statement.setInt(3, toUpdate.getFlight_FK());
        statement.setTimestamp(4, Timestamp.valueOf(toUpdate.getReserved_at()));
        statement.setInt(5, id);

        statement.executeUpdate();
        statement.close();
    }

    /**
     * Deletes a reservation from the database based on the provided ID.
     *
     * @param id the unique identifier of the reservation to be deleted
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void delete(int id) throws SQLException {
        String query = "DELETE FROM reservations WHERE id_PK = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, id);

        statement.executeUpdate();
        statement.close();
    }

    /**
     * Transforms the results from a ResultSet into a Reservation object.
     *
     * @param resultSet the ResultSet containing reservation data
     * @return a Reservation object populated with data from the ResultSet
     * @throws SQLException if a database access error occurs
     */
    private Reservation transformResultsToClass(ResultSet resultSet) throws SQLException {
        Reservation reservation = new Reservation();

        if (resultSet.next()) {
            reservation.setId(resultSet.getInt("id_PK"));
            reservation.setUser_FK(resultSet.getInt("user_FK"));
            reservation.setStatus_FK(resultSet.getInt("status_FK"));
            reservation.setFlight_FK(resultSet.getInt("flight_FK"));
            reservation.setReserved_at(resultSet.getTimestamp("reserved_at").toLocalDateTime());
            
            // Set status information from join
            reservation.setStatus_name(resultSet.getString("status_name"));
            reservation.setStatus_description(resultSet.getString("status_description"));
        }

        return reservation;
    }

    /**
     * Transforms the results from a ResultSet into an ArrayList of Reservation objects.
     *
     * @param resultSet the ResultSet containing reservation data
     * @return an ArrayList of Reservation objects populated with data from the ResultSet
     * @throws SQLException if a database access error occurs
     */
    private ArrayList<Reservation> transformResultsToClassArray(ResultSet resultSet) throws SQLException {
        ArrayList<Reservation> reservations = new ArrayList<>();

        while (resultSet.next()) {
            Reservation reservation = new Reservation();
            reservation.setId(resultSet.getInt("id_PK"));
            reservation.setUser_FK(resultSet.getInt("user_FK"));
            reservation.setStatus_FK(resultSet.getInt("status_FK"));
            reservation.setFlight_FK(resultSet.getInt("flight_FK"));
            reservation.setReserved_at(resultSet.getTimestamp("reserved_at").toLocalDateTime());
            
            // Set status information from join
            reservation.setStatus_name(resultSet.getString("status_name"));
            reservation.setStatus_description(resultSet.getString("status_description"));
            
            reservations.add(reservation);
        }

        return reservations;
    }

    /**
     * Returns reservations by user ID.
     *
     * @param userId the ID of the user
     * @return an ArrayList of Reservation objects for the specified user
     * @throws SQLException if a database access error occurs
     */
    public ArrayList<Reservation> getByUserId(int userId) throws SQLException {
        String query = "SELECT r.*, rs.name as status_name, rs.description as status_description " +
                "FROM reservations r " +
                "JOIN reservations_status rs ON r.status_FK = rs.id_PK " +
                "WHERE r.user_FK = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, userId);

        ResultSet resultSet = statement.executeQuery();

        ArrayList<Reservation> reservations = transformResultsToClassArray(resultSet);
        statement.close();
        return reservations;
    }

    /**
     * Returns reservations by flight ID.
     *
     * @param flightId the ID of the flight
     * @return an ArrayList of Reservation objects for the specified flight
     * @throws SQLException if a database access error occurs
     */
    public ArrayList<Reservation> getByFlightId(int flightId) throws SQLException {
        String query = "SELECT r.*, rs.name as status_name, rs.description as status_description " +
                "FROM reservations r " +
                "JOIN reservations_status rs ON r.status_FK = rs.id_PK " +
                "WHERE r.flight_FK = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, flightId);

        ResultSet resultSet = statement.executeQuery();

        ArrayList<Reservation> reservations = transformResultsToClassArray(resultSet);
        statement.close();
        return reservations;
    }

    // Getters and Setters
    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
