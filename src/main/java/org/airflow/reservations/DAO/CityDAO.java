package org.airflow.reservations.DAO;

import org.airflow.reservations.model.City;
import org.airflow.reservations.utils.ConnectionDB;

import java.sql.*;
import java.util.ArrayList;

/**
 * Data Access Object (DAO) class for managing City entities.
 * This class provides methods to perform CRUD operations on City objects in the database.
 * It implements the DAOMethods interface for generic DAO operations.
 *
 * @see DAOMethods
 * @see City
 */
public class CityDAO implements DAOMethods<City> {
    private Connection connection;

    /**
     * Default constructor for CityDAO class.
     * Initializes the CityDAO with a new database connection.
     *
     * @throws SQLException if a database access error occurs
     */
    public CityDAO() throws SQLException {
        connection = ConnectionDB.getConnection();
    }

    /**
     * Constructor for CityDAO class.
     * Initializes the CityDAO with a specific connection.
     *
     * @param connection the connection to be used by the DAO
     */
    public CityDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Returns all cities from the database.
     *
     * @return an ArrayList of City objects representing all cities in the database
     * @throws SQLException if a database access error occurs
     */
    @Override
    public ArrayList<City> getAll() throws SQLException {
        String query = "SELECT * FROM cities";

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        ArrayList<City> cities = transformResultsToClassArray(resultSet);
        statement.close();

        return cities;
    }

    /**
     * Returns a City object based on the provided ID.
     *
     * @param id the unique identifier of the city to be retrieved
     * @return a City object with the specified ID
     * @throws SQLException if a database access error occurs
     */
    @Override
    public City getById(int id) throws SQLException {
        String query = "SELECT * FROM cities WHERE id_PK = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, id);

        ResultSet resultSet = statement.executeQuery();

        City city = transformResultsToClass(resultSet);
        statement.close();
        return city;
    }

    /**
     * Inserts a new city into the database.
     *
     * @param object the City object to be created in the database
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void create(City object) throws SQLException {
        String query = "INSERT INTO cities (name, country, code) VALUES (?, ?, ?)";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, object.getName());
        statement.setString(2, object.getCountry());
        statement.setString(3, object.getCode());

        statement.executeUpdate();
        statement.close();
    }

    /**
     * Updates an existing city in the database.
     *
     * @param id       the unique identifier of the city to be updated
     * @param toUpdate the City object containing updated data
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void update(int id, City toUpdate) throws SQLException {
        String query = "UPDATE cities SET name = ?, country = ?, code = ? WHERE id_PK = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, toUpdate.getName());
        statement.setString(2, toUpdate.getCountry());
        statement.setString(3, toUpdate.getCode());
        statement.setInt(4, id);

        statement.executeUpdate();
        statement.close();
    }

    /**
     * Deletes a city from the database based on the provided ID.
     *
     * @param id the unique identifier of the city to be deleted
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void delete(int id) throws SQLException {
        String query = "DELETE FROM cities WHERE id_PK = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, id);

        statement.executeUpdate();
        statement.close();
    }

    /**
     * Returns a list of cities that match the provided name.
     *
     * @param name the name or part of the name of the city to search for
     * @return an ArrayList of City objects that match the provided name
     * @throws SQLException if a database access error occurs
     */
    public ArrayList<City> getByName(String name) throws SQLException {
        String query = "SELECT * FROM cities WHERE name LIKE ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, "%" + name + "%");

        ResultSet resultSet = statement.executeQuery();

        ArrayList<City> cities = transformResultsToClassArray(resultSet);
        statement.close();
        return cities;
    }

    /**
     * Returns a list of cities that match the provided country.
     *
     * @param country the name or part of the name of the country to search for
     * @return an ArrayList of City objects that match the provided country
     * @throws SQLException if a database access error occurs
     */
    public ArrayList<City> getByCountry(String country) throws SQLException {
        String query = "SELECT * FROM cities WHERE country LIKE ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, "%" + country + "%");

        ResultSet resultSet = statement.executeQuery();

        ArrayList<City> cities = transformResultsToClassArray(resultSet);
        statement.close();
        return cities;
    }

    /**
     * Returns a list of cities that match the provided code.
     *
     * @param code the code or part of the code of the city to search for
     * @return an ArrayList of City objects that match the provided code
     * @throws SQLException if a database access error occurs
     */
    public ArrayList<City> getByCode(String code) throws SQLException {
        String query = "SELECT * FROM cities WHERE code LIKE ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, "%" + code + "%");

        ResultSet resultSet = statement.executeQuery();

        ArrayList<City> cities = transformResultsToClassArray(resultSet);
        statement.close();
        return cities;
    }

    /**
     * Transforms the results from a ResultSet into a City object.
     *
     * @param resultSet the ResultSet containing city data
     * @return a City object populated with data from the ResultSet
     * @throws SQLException if a database access error occurs
     */
    private City transformResultsToClass(ResultSet resultSet) throws SQLException {
        City city = new City();

        if (resultSet.next()) {
            city.setId(resultSet.getInt("id_PK"));
            city.setName(resultSet.getString("name"));
            city.setCountry(resultSet.getString("country"));
            city.setCode(resultSet.getString("code"));
        }

        return city;
    }

    /**
     * Transforms the results from a ResultSet into an ArrayList of City objects.
     *
     * @param resultSet the ResultSet containing city data
     * @return an ArrayList of City objects populated with data from the ResultSet
     * @throws SQLException if a database access error occurs
     */
    private ArrayList<City> transformResultsToClassArray(ResultSet resultSet) throws SQLException {
        ArrayList<City> cities = new ArrayList<>();

        while (resultSet.next()) {
            City city = new City();
            city.setId(resultSet.getInt("id_PK"));
            city.setName(resultSet.getString("name"));
            city.setCountry(resultSet.getString("country"));
            city.setCode(resultSet.getString("code"));
            cities.add(city);
        }

        return cities;
    }

    // Getters and Setters
    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
