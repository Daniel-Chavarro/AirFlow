package org.airflow.reservations.DAO;

import org.airflow.reservations.model.Flight;
import org.airflow.reservations.utils.ConnectionDB;

import java.sql.*;
import java.util.ArrayList;

/**
 * Data Access Object (DAO) class for managing Flight entities.
 * This class provides methods to perform CRUD operations on Flight objects in the database.
 * It implements the DAOMethods interface for generic DAO operations.
 *
 * @see DAOMethods
 * @see Flight
 */
public class FlightDAO implements DAOMethods<Flight> {
    private Connection connection;

    /**
     * Default constructor for FlightDAO class.
     * Initializes the FlightDAO with a new database connection.
     *
     * @throws SQLException if a database access error occurs
     */
    public FlightDAO() throws SQLException {
        connection = ConnectionDB.getConnection();
    }

    /**
     * Constructor for FlightDAO class.
     * Initializes the FlightDAO with a specific connection.
     *
     * @param connection the connection to be used by the DAO
     */
    public FlightDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Returns all flights from the database.
     *
     * @return an ArrayList of Flight objects representing all flights in the database
     * @throws SQLException if a database access error occurs
     */
    @Override
    public ArrayList<Flight> getAll() throws SQLException {
        String query = "SELECT f.*, fs.name as status_name, fs.description as status_description " +
                "FROM flights f " +
                "JOIN flight_status fs ON f.status_FK = fs.id_PK";

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        ArrayList<Flight> flights = transformResultsToClassArray(resultSet);
        statement.close();

        return flights;
    }

    /**
     * Returns a Flight object based on the provided ID.
     *
     * @param id the unique identifier of the flight to be retrieved
     * @return a Flight object with the specified ID
     * @throws SQLException if a database access error occurs
     */
    @Override
    public Flight getById(int id) throws SQLException {
        String query = "SELECT f.*, fs.name as status_name, fs.description as status_description " +
                "FROM flights f " +
                "JOIN flight_status fs ON f.status_FK = fs.id_PK " +
                "WHERE f.id_PK = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, id);

        ResultSet resultSet = statement.executeQuery();

        Flight flight = transformResultsToClass(resultSet);
        statement.close();
        return flight;
    }

    /**
     * Inserts a new flight into the database.
     *
     * @param object the Flight object to be created in the database
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void create(Flight object) throws SQLException {
        String query = "INSERT INTO flights (airplane_FK, status_FK, origin_city_FK, destination_city_FK, " +
                "code, departure_time, arrival_time, price_base) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, object.getAirplane_FK());
        statement.setInt(2, object.getStatus_FK());
        statement.setInt(3, object.getOrigin_city_FK());
        statement.setInt(4, object.getDestination_city_FK());
        statement.setString(5, object.getCode());
        statement.setTimestamp(6, Timestamp.valueOf(object.getDeparture_time()));
        statement.setTimestamp(7, Timestamp.valueOf(object.getArrival_time()));
        statement.setFloat(8, object.getPrice_base());

        statement.executeUpdate();
        statement.close();
    }

    /**
     * Updates an existing flight in the database.
     *
     * @param id       the unique identifier of the flight to be updated
     * @param toUpdate the Flight object containing updated data
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void update(int id, Flight toUpdate) throws SQLException {
        String query = "UPDATE flights SET airplane_FK = ?, status_FK = ?, origin_city_FK = ?, " +
                "destination_city_FK = ?, code = ?, departure_time = ?, arrival_time = ?, price_base = ? " +
                "WHERE id_PK = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, toUpdate.getAirplane_FK());
        statement.setInt(2, toUpdate.getStatus_FK());
        statement.setInt(3, toUpdate.getOrigin_city_FK());
        statement.setInt(4, toUpdate.getDestination_city_FK());
        statement.setString(5, toUpdate.getCode());
        statement.setTimestamp(6, Timestamp.valueOf(toUpdate.getDeparture_time()));
        statement.setTimestamp(7, Timestamp.valueOf(toUpdate.getArrival_time()));
        statement.setFloat(8, toUpdate.getPrice_base());
        statement.setInt(9, id);

        statement.executeUpdate();
        statement.close();
    }

    /**
     * Deletes a flight from the database based on the provided ID.
     *
     * @param id the unique identifier of the flight to be deleted
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void delete(int id) throws SQLException {
        String query = "DELETE FROM flights WHERE id_PK = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, id);

        statement.executeUpdate();
        statement.close();
    }

    /**
     * Returns flights by destination city.
     *
     * @param cityId the ID of the destination city
     * @return an ArrayList of Flight objects with the specified destination city
     * @throws SQLException if a database access error occurs
     */
    public ArrayList<Flight> getByDestinationCity(int cityId) throws SQLException {
        String query = "SELECT f.*, fs.name as status_name, fs.description as status_description " +
                "FROM flights f " +
                "JOIN flight_status fs ON f.status_FK = fs.id_PK " +
                "WHERE f.destination_city_FK = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, cityId);

        ResultSet resultSet = statement.executeQuery();

        ArrayList<Flight> flights = transformResultsToClassArray(resultSet);
        statement.close();
        return flights;
    }

    /**
     * Returns flights by origin city.
     *
     * @param cityId the ID of the origin city
     * @return an ArrayList of Flight objects with the specified origin city
     * @throws SQLException if a database access error occurs
     */
    public ArrayList<Flight> getByOriginCity(int cityId) throws SQLException {
        String query = "SELECT f.*, fs.name as status_name, fs.description as status_description " +
                "FROM flights f " +
                "JOIN flight_status fs ON f.status_FK = fs.id_PK " +
                "WHERE f.origin_city_FK = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, cityId);

        ResultSet resultSet = statement.executeQuery();

        ArrayList<Flight> flights = transformResultsToClassArray(resultSet);
        statement.close();
        return flights;
    }

    /**
     * Returns flights by flight code.
     *
     * @param code the flight code to search for
     * @return an ArrayList of Flight objects matching the specified code
     * @throws SQLException if a database access error occurs
     */
    public ArrayList<Flight> getByCode(String code) throws SQLException {
        String query = "SELECT f.*, fs.name as status_name, fs.description as status_description " +
                "FROM flights f " +
                "JOIN flight_status fs ON f.status_FK = fs.id_PK " +
                "WHERE f.code LIKE ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, "%" + code + "%");

        ResultSet resultSet = statement.executeQuery();

        ArrayList<Flight> flights = transformResultsToClassArray(resultSet);
        statement.close();
        return flights;
    }

    /**
     * Transforms the results from a ResultSet into a Flight object.
     *
     * @param resultSet the ResultSet containing flight data
     * @return a Flight object populated with data from the ResultSet
     * @throws SQLException if a database access error occurs
     */
    private Flight transformResultsToClass(ResultSet resultSet) throws SQLException {
        Flight flight = new Flight();

        if (resultSet.next()) {
            flight.setId(resultSet.getInt("id_PK"));
            flight.setAirplane_FK(resultSet.getInt("airplane_FK"));
            flight.setStatus_FK(resultSet.getInt("status_FK"));
            flight.setOrigin_city_FK(resultSet.getInt("origin_city_FK"));
            flight.setDestination_city_FK(resultSet.getInt("destination_city_FK"));
            flight.setCode(resultSet.getString("code"));
            flight.setDeparture_time(resultSet.getTimestamp("departure_time").toLocalDateTime());
            flight.setArrival_time(resultSet.getTimestamp("arrival_time").toLocalDateTime());
            flight.setPrice_base(resultSet.getFloat("price_base"));

            // Set status information from join
            flight.setStatus_name(resultSet.getString("status_name"));
            flight.setStatus_description(resultSet.getString("status_description"));
        }

        return flight;
    }

    /**
     * Transforms the results from a ResultSet into an ArrayList of Flight objects.
     *
     * @param resultSet the ResultSet containing flight data
     * @return an ArrayList of Flight objects populated with data from the ResultSet
     * @throws SQLException if a database access error occurs
     */
    private ArrayList<Flight> transformResultsToClassArray(ResultSet resultSet) throws SQLException {
        ArrayList<Flight> flights = new ArrayList<>();

        while (resultSet.next()) {
            Flight flight = new Flight();
            flight.setId(resultSet.getInt("id_PK"));
            flight.setAirplane_FK(resultSet.getInt("airplane_FK"));
            flight.setStatus_FK(resultSet.getInt("status_FK"));
            flight.setOrigin_city_FK(resultSet.getInt("origin_city_FK"));
            flight.setDestination_city_FK(resultSet.getInt("destination_city_FK"));
            flight.setCode(resultSet.getString("code"));
            flight.setDeparture_time(resultSet.getTimestamp("departure_time").toLocalDateTime());
            flight.setArrival_time(resultSet.getTimestamp("arrival_time").toLocalDateTime());
            flight.setPrice_base(resultSet.getFloat("price_base"));

            // Set status information from join
            flight.setStatus_name(resultSet.getString("status_name"));
            flight.setStatus_description(resultSet.getString("status_description"));

            flights.add(flight);
        }

        return flights;
    }


    // Getters and Setters
    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
