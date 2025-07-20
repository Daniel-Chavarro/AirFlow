package org.airflow.reservations.DAO;

import org.airflow.reservations.model.*;
import org.airflow.reservations.utils.ConnectionDB;

import java.sql.*;
import java.time.LocalDateTime;
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
                "code, departure_time, scheduled_arrival_time, arrival_time, price_base) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, object.getAirplane_FK());
        statement.setInt(2, object.getStatus_FK());
        statement.setInt(3, object.getOrigin_city_FK());
        statement.setInt(4, object.getDestination_city_FK());
        statement.setString(5, object.getCode());
        statement.setTimestamp(6, Timestamp.valueOf(object.getDeparture_time()));
        statement.setTimestamp(7, Timestamp.valueOf(object.getScheduled_arrival_time()));
        // Manejo de posibles valores NULL para arrival_time
        if (object.getArrival_time() != null) {
            statement.setTimestamp(8, Timestamp.valueOf(object.getArrival_time()));
        } else {
            statement.setNull(8, Types.TIMESTAMP);
        }
        statement.setFloat(9, object.getPrice_base());

        statement.executeUpdate();
        statement.close();
    }

    /**
     * Updates an existing flight in the database.
     *
     * @param id the unique identifier of the flight to be updated
     * @param toUpdate the Flight object containing updated data
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void update(int id, Flight toUpdate) throws SQLException {
        String query = "UPDATE flights SET airplane_FK = ?, status_FK = ?, origin_city_FK = ?, " +
                "destination_city_FK = ?, code = ?, departure_time = ?, scheduled_arrival_time = ?, arrival_time = ?, price_base = ? " +
                "WHERE id_PK = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, toUpdate.getAirplane_FK());
        statement.setInt(2, toUpdate.getStatus_FK());
        statement.setInt(3, toUpdate.getOrigin_city_FK());
        statement.setInt(4, toUpdate.getDestination_city_FK());
        statement.setString(5, toUpdate.getCode());
        statement.setTimestamp(6, Timestamp.valueOf(toUpdate.getDeparture_time()));
        statement.setTimestamp(7, Timestamp.valueOf(toUpdate.getScheduled_arrival_time()));
        // Manejo de posibles valores NULL para arrival_time
        if (toUpdate.getArrival_time() != null) {
            statement.setTimestamp(8, Timestamp.valueOf(toUpdate.getArrival_time()));
        } else {
            statement.setNull(8, Types.TIMESTAMP);
        }
        statement.setFloat(9, toUpdate.getPrice_base());
        statement.setInt(10, id);

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
     * Returns an ArrayList of Flights based on the provided code.
     *
     * @param code the code of the flight to be retrieved
     * @return an ArrayList of Flights with the specified code
     * @throws SQLException if a database access error occurs
     */
    public ArrayList<Flight> getByCode(String code) throws SQLException {
        String query = "SELECT f.*, fs.name as status_name, fs.description as status_description " +
                "FROM flights f " +
                "JOIN flight_status fs ON f.status_FK = fs.id_PK " +
                "WHERE f.code LIKE ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, code);

        ResultSet resultSet = statement.executeQuery();

        ArrayList<Flight> flights = transformResultsToClassArray(resultSet);
        statement.close();
        return flights;
    }

    /**
     * Returns a flight by its code.
     * This is the method causing the error if not correctly implemented.
     *
     * @param code the code of the flight
     * @return the Flight object with the specified code, or null if not found
     * @throws SQLException if a database access error occurs
     */
    public Flight getByCodeOb(String code) throws SQLException {
        String query = "SELECT f.*, fs.name as status_name, fs.description as status_description " +
                "FROM flights f " +
                "JOIN flight_status fs ON f.status_FK = fs.id_PK " +
                "WHERE f.code = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, code);
        ResultSet resultSet = statement.executeQuery();

        Flight flight = transformResultsToClass(resultSet); // Llama a transformResultsToClass directamente
        statement.close();

        // Si no se encontró un vuelo, transformResultsToClass devolverá un objeto Flight con id=0
        if (flight != null && flight.getId() == 0) {
            return null;
        }
        return flight;
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

            // Manejo de posibles valores NULL para columnas Timestamp
            Timestamp departureTimestamp = resultSet.getTimestamp("departure_time");
            flight.setDeparture_time((departureTimestamp != null) ? departureTimestamp.toLocalDateTime() : null);

            Timestamp scheduledArrivalTimestamp = resultSet.getTimestamp("scheduled_arrival_time");
            flight.setScheduled_arrival_time((scheduledArrivalTimestamp != null) ? scheduledArrivalTimestamp.toLocalDateTime() : null);

            // CORRECCIÓN: Usar arrivalTimestamp para la verificación de nulidad
            Timestamp arrivalTimestamp = resultSet.getTimestamp("arrival_time");
            flight.setArrival_time((arrivalTimestamp != null) ? arrivalTimestamp.toLocalDateTime() : null);

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

            // Manejo de posibles valores NULL para columnas Timestamp
            Timestamp departureTimestamp = resultSet.getTimestamp("departure_time");
            flight.setDeparture_time((departureTimestamp != null) ? departureTimestamp.toLocalDateTime() : null);

            Timestamp scheduledArrivalTimestamp = resultSet.getTimestamp("scheduled_arrival_time");
            flight.setScheduled_arrival_time((scheduledArrivalTimestamp != null) ? scheduledArrivalTimestamp.toLocalDateTime() : null);

            // CORRECCIÓN: Usar arrivalTimestamp para la verificación de nulidad
            Timestamp arrivalTimestamp = resultSet.getTimestamp("arrival_time");
            flight.setArrival_time((arrivalTimestamp != null) ? arrivalTimestamp.toLocalDateTime() : null);

            flight.setPrice_base(resultSet.getFloat("price_base"));

            // Set status information from join
            flight.setStatus_name(resultSet.getString("status_name"));
            flight.setStatus_description(resultSet.getString("status_description"));
            // Add the flight to the list
            flights.add(flight);
        }

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
     * Returns flights whose origin and destination cities are the same.
     * @param originId the ID of the origin city
     * @param destinyId the ID of the destination city
     * @return an ArrayList of Flight objects with the specified origin and destination cities.
     * @throws SQLException if a database access error occurs.
     */

    /**
     * Returns flights with the specified destination and origin cities.
     *
     * @param destinationCityId the ID of the destination city
     * @param originCityId the ID of the origin city
     * @return an ArrayList of Flight objects matching the criteria
     * @throws SQLException if a database access error occurs
     */
    public ArrayList<Flight> getByDestinationAndOriginCity(int destinationCityId, int originCityId) throws SQLException {
        String query = "SELECT f.*, fs.name as status_name, fs.description as status_description " +
                "FROM flights f " +
                "JOIN flight_status fs ON f.status_FK = fs.id_PK " +
                "WHERE f.destination_city_FK = ? AND f.origin_city_FK = ?";

        System.out.println("DEBUG (FlightDAO): Ejecutando consulta SQL en getByDestinationAndOriginCity: " + query);
        System.out.println("DEBUG (FlightDAO): Parámetros - ID Destino: " + destinationCityId + ", ID Origen: " + originCityId);

        // ¡CAMBIO CLAVE AQUÍ! Crear un PreparedStatement con un ResultSet desplazable
        PreparedStatement statement = connection.prepareStatement(
            query,
            ResultSet.TYPE_SCROLL_INSENSITIVE, // Permite mover el cursor hacia adelante y hacia atrás
            ResultSet.CONCUR_READ_ONLY         // Indica que el ResultSet es de solo lectura (más eficiente)
        );
        statement.setInt(1, destinationCityId);
        statement.setInt(2, originCityId);

        ResultSet resultSet = statement.executeQuery();

        // Debug: Imprimir contenido del ResultSet (esto ahora funcionará y luego se podrá reposicionar)
        System.out.println("DEBUG (FlightDAO): Contenido del ResultSet para getByDestinationAndOriginCity:");
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        int rowCount = 0;
        while (resultSet.next()) { // Primera iteración para depuración
            rowCount++;
            StringBuilder row = new StringBuilder();
            for (int i = 1; i <= columnCount; i++) {
                row.append(metaData.getColumnName(i)).append(": ").append(resultSet.getObject(i)).append(" | ");
            }
            System.out.println("  Fila " + rowCount + ": " + row.toString());
        }
        System.out.println("DEBUG (FlightDAO): Total de filas en ResultSet antes de transformar: " + rowCount);

        // Mover el cursor del ResultSet de nuevo al principio antes de pasarlo a transformResultsToClassArray
        // Esto ahora es posible porque el ResultSet es desplazable.
        resultSet.beforeFirst();

        ArrayList<Flight> flights = transformResultsToClassArray(resultSet);
        System.out.println("DEBUG (FlightDAO): Número de vuelos después de transformResultsToClassArray: " + flights.size());

        statement.close(); // Asegúrate de cerrar el statement, que también cerrará el resultSet
        return flights;
}

    /**
     * Returns flights whose departure time is equal or less than or equal to the specified date.
     * @param bottomRange LocalDateTime object with the date to be compared with the flight departure time.
     * @param topRange LocalDateTime object with the date to be compared with the flight departure time.
     * @return an ArrayList of Flight object with departure times less than or equal to the specified date.
     * @throws SQLException if a database access error occurs.
     */
    public ArrayList<Flight> getByDepartureTimeRange(LocalDateTime bottomRange , LocalDateTime topRange)throws SQLException{
        String query = "SELECT f.*, fs.name as status_name, fs.description as status_description " +
                "FROM flights f " +
                "JOIN flight_status fs ON f.status_FK = fs.id_PK " +
                "WHERE f.departure_time <= ? AND f.departure_time >= ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setTimestamp(1, Timestamp.valueOf(topRange));
        statement.setTimestamp(2,Timestamp.valueOf(bottomRange));
        ResultSet resultSet = statement.executeQuery();
        ArrayList<Flight> flights = transformResultsToClassArray(resultSet);
        statement.close();
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