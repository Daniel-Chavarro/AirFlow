package org.airflow.reservations.DAO;

import org.airflow.reservations.model.User;
import org.airflow.reservations.utils.ConnectionDB;

import java.sql.*;
import java.util.ArrayList;

/**
 *
Data Access Object (DAO) class for managing User entities.
 * This class provides methods to perform CRUD operations on User objects in the database.
 * It implements the DAOMethods interface for generic DAO operations.
 *
 * @see DAOMethods
 * @see User */
public class UsersDAO implements DAOMethods<User> {
    private Connection connection;

    /**
     * Default constructor for UsersDAO class.
     * Initializes the UsersDAO with default values.
     */
    public UsersDAO() throws SQLException {
        connection = ConnectionDB.getConnection();
    }

    /**
     * Constructor for UsersDAO class.
     * Initializes the UsersDAO with a specific connection.
     *
     * @param connection the connection to be used by the DAO
     */
    public UsersDAO(Connection connection) {
        this.connection = connection;
    }

    /**
    * Returns all users from the database.
    *
    * @return an ArrayList of User objects representing all users in the database
    * @throws SQLException if a database access error occurs
    */
    @Override
    public ArrayList<User> getAll() throws SQLException{
        String query = "SELECT * FROM users";

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);


        ArrayList <User> users = transformResultsToClassArray(resultSet);
        statement.close();

        return users;
    }

    /**
     * Returns a User object based on the provided ID.
     *
     * @param id the unique identifier of the user to be retrieved
     * @return a User object with the specified ID
     * @throws SQLException if a database access error occurs
     */

    @Override
    public User getById(int id) throws SQLException {
        String query = "SELECT * FROM users WHERE id_PK = (?)";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, id);

        ResultSet resultSet = statement.executeQuery();

        User user = transformResultsToClass(resultSet);
        statement.close();
        return user;
    }

    /**
     * Inserts a new user into the database.
     *
     * @param object the User object to be created in the database
     * @throws SQLException if a database access error occurs
     */

    @Override
    public void create(User object) throws SQLException {
        String query = "INSERT INTO users (name, last_name, email, password, isSuperUser, created_at) " +
                "VALUES (?,?,?,?,?,?)";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, object.getName());
        statement.setString(2, object.getLast_name());
        statement.setString(3, object.getEmail());
        statement.setString(4, object.getPassword());
        statement.setBoolean(5, object.getSuperUser());
        statement.setTimestamp(6, Timestamp.valueOf(object.getCreated_at()));

        statement.executeUpdate();

        statement.close();

    }


    /**
     * Updates an existing user in the database.
     *
     * @param id       the unique identifier of the user to be updated
     * @param toUpdate the User object containing updated data
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void update(int id, User toUpdate) throws SQLException {
        String query =
                "UPDATE users " +
                "SET name = ?, last_name = ?, email = ?, password = ?, isSuperUser = ?, created_at = ? " +
                "WHERE id_PK = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, toUpdate.getName());
        statement.setString(2, toUpdate.getLast_name());
        statement.setString(3, toUpdate.getEmail());
        statement.setString(4, toUpdate.getPassword());
        statement.setBoolean(5, toUpdate.getSuperUser());
        statement.setTimestamp(6, Timestamp.valueOf(toUpdate.getCreated_at()));
        statement.setInt(7, id);

        statement.executeUpdate();

        statement.close();
    }


    /**
     * Deletes a user from the database based on the provided ID.
     *
     * @param id the unique identifier of the user to be deleted
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void delete(int id) throws SQLException {
        String query = "DELETE FROM users WHERE id_PK = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, id);

        statement.executeUpdate();

        statement.close();
    }


    /**
     * Transforms the results from a ResultSet into a User object.
     *
     * @param resultSet the ResultSet containing user data
     * @return a User object populated with data from the ResultSet
     * @throws SQLException if a database access error occurs
     */
    private User transformResultsToClass(ResultSet resultSet) throws SQLException {
        User user = new User();

        while (resultSet.next()) {
            user.setId(resultSet.getInt("id"));
            user.setEmail(resultSet.getString("email"));
            user.setSuperUser(resultSet.getBoolean("isSuperUser"));
            user.setCreated_at(resultSet.getTimestamp("created_at").toLocalDateTime());
        }

        return user;
    }


    /**
     * Transforms the results from a ResultSet into an ArrayList of User objects.
     *
     * @param resultSet the ResultSet containing user data
     * @return an ArrayList of User objects populated with data from the ResultSet
     * @throws SQLException if a database access error occurs
     */
    private ArrayList<User> transformResultsToClassArray(ResultSet resultSet) throws SQLException {
        ArrayList<User> users = new ArrayList<>();

        while (resultSet.next()) {
            User user = new User();
            user.setId(resultSet.getInt("id"));
            user.setEmail(resultSet.getString("email"));
            user.setSuperUser(resultSet.getBoolean("isSuperUser"));
            user.setCreated_at(resultSet.getTimestamp("created_at").toLocalDateTime());
        }

        return users;
    }


    //Getters and Setters
    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
