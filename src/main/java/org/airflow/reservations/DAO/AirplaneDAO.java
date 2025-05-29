package org.airflow.reservations.DAO;

import org.airflow.reservations.model.Airplane;
import org.airflow.reservations.utils.ConnectionDB;

import java.sql.*;
import java.time.Year;
import java.util.ArrayList;

/**
 * Data Access Object (DAO) class for managing Airplane entities.
 * This class provides methods to perform CRUD operations on Airplane objects in the database.
 * It implements the DAOMethods interface for generic DAO operations.
 *
 * @see DAOMethods
 * @see Airplane
 */
public class AirplaneDAO implements DAOMethods<Airplane> {
    private Connection connection;

    /**
     * Default constructor for AirplaneDAO class.
     * Initializes the AirplaneDAO with a new database connection.
     *
     * @throws SQLException if a database access error occurs
     */
    public AirplaneDAO() throws SQLException {
        connection = ConnectionDB.getConnection();
    }

    /**
     * Constructor for AirplaneDAO class.
     * Initializes the AirplaneDAO with a specific connection.
     *
     * @param connection the connection to be used by the DAO
     */
    public AirplaneDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Returns all airplanes from the database.
     *
     * @return an ArrayList of Airplane objects representing all airplanes in the database
     * @throws SQLException if a database access error occurs
     */
    @Override
    public ArrayList<Airplane> getAll() throws SQLException {
        String query = "SELECT * FROM airplanes";

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        ArrayList<Airplane> airplanes = transformResultsToClassArray(resultSet);
        statement.close();

        return airplanes;
    }

    /**
     * Returns an Airplane object based on the provided ID.
     *
     * @param id the unique identifier of the airplane to be retrieved
     * @return an Airplane object with the specified ID
     * @throws SQLException if a database access error occurs
     */
    @Override
    public Airplane getById(int id) throws SQLException {
        String query = "SELECT * FROM airplanes WHERE id_PK = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, id);

        ResultSet resultSet = statement.executeQuery();

        Airplane airplane = transformResultsToClass(resultSet);
        statement.close();
        return airplane;
    }

    /**
     * Inserts a new airplane into the database.
     *
     * @param object the Airplane object to be created in the database
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void create(Airplane object) throws SQLException {
        String query = "INSERT INTO airplanes (airline, model, code, capacity, year) VALUES (?, ?, ?, ?, ?)";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, object.getAirline());
        statement.setString(2, object.getModel());
        statement.setString(3, object.getCode());
        statement.setInt(4, object.getCapacity());
        statement.setInt(5, object.getYear().getValue());

        statement.executeUpdate();
        statement.close();
    }

    /**
     * Updates an existing airplane in the database.
     *
     * @param id       the unique identifier of the airplane to be updated
     * @param toUpdate the Airplane object containing updated data
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void update(int id, Airplane toUpdate) throws SQLException {
        String query = "UPDATE airplanes SET airline = ?, model = ?, code = ?, capacity = ?, year = ? WHERE id_PK = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, toUpdate.getAirline());
        statement.setString(2, toUpdate.getModel());
        statement.setString(3, toUpdate.getCode());
        statement.setInt(4, toUpdate.getCapacity());
        statement.setInt(5, toUpdate.getYear().getValue());
        statement.setInt(6, id);

        statement.executeUpdate();
        statement.close();
    }

    /**
     * Deletes an airplane from the database based on the provided ID.
     *
     * @param id the unique identifier of the airplane to be deleted
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void delete(int id) throws SQLException {
        String query = "DELETE FROM airplanes WHERE id_PK = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, id);

        statement.executeUpdate();
        statement.close();
    }

    /**
     * Transforms the results from a ResultSet into an Airplane object.
     *
     * @param resultSet the ResultSet containing airplane data
     * @return an Airplane object populated with data from the ResultSet
     * @throws SQLException if a database access error occurs
     */
    private Airplane transformResultsToClass(ResultSet resultSet) throws SQLException {
        Airplane airplane = new Airplane();

        if (resultSet.next()) {
            airplane.setId(resultSet.getInt("id_PK"));
            airplane.setAirline(resultSet.getString("airline"));
            airplane.setModel(resultSet.getString("model"));
            airplane.setCode(resultSet.getString("code"));
            airplane.setCapacity(resultSet.getInt("capacity"));
            airplane.setYear(Year.of(resultSet.getInt("year")));
        }

        return airplane;
    }

    /**
     * Transforms the results from a ResultSet into an ArrayList of Airplane objects.
     *
     * @param resultSet the ResultSet containing airplane data
     * @return an ArrayList of Airplane objects populated with data from the ResultSet
     * @throws SQLException if a database access error occurs
     */
    private ArrayList<Airplane> transformResultsToClassArray(ResultSet resultSet) throws SQLException {
        ArrayList<Airplane> airplanes = new ArrayList<>();

        while (resultSet.next()) {
            Airplane airplane = new Airplane();
            airplane.setId(resultSet.getInt("id_PK"));
            airplane.setAirline(resultSet.getString("airline"));
            airplane.setModel(resultSet.getString("model"));
            airplane.setCode(resultSet.getString("code"));
            airplane.setCapacity(resultSet.getInt("capacity"));
            airplane.setYear(Year.of(resultSet.getInt("year")));
            airplanes.add(airplane);
        }

        return airplanes;
    }

    // Getters and Setters
    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
