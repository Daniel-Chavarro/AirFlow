package org.airflow.reservations.DAO;

import org.airflow.reservations.model.*;
import org.airflow.reservations.utils.ConnectionDB;

import java.sql.*;
import java.util.ArrayList;

/**
 * Data Access Object (DAO) class for managing Seat entities.
 * This class provides methods to perform CRUD operations on Seat objects in the database.
 * It implements the DAOMethods interface for generic DAO operations.
 *
 * @see DAOMethods
 * @see Seat
 */
public class SeatDAO implements DAOMethods<Seat> {
    private Connection connection;

    /**
     * Default constructor for SeatDAO class.
     * Initializes the SeatDAO with a new database connection.
     *
     * @throws SQLException if a database access error occurs
     */
    public SeatDAO() throws SQLException {
        connection = ConnectionDB.getConnection();
    }

    /**
     * Constructor for SeatDAO class.
     * Initializes the SeatDAO with a specific connection.
     *
     * @param connection the connection to be used by the DAO
     */
    public SeatDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Returns all seats from the database.
     *
     * @return an ArrayList of Seat objects representing all seats in the database
     * @throws SQLException if a database access error occurs
     */
    @Override
    public ArrayList<Seat> getAll() throws SQLException {
        String query = "SELECT * FROM seats";

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        ArrayList<Seat> seats = transformResultsToClassArray(resultSet);
        statement.close();

        return seats;
    }

    /**
     * Returns a Seat object based on the provided ID.
     *
     * @param id the unique identifier of the seat to be retrieved
     * @return a Seat object with the specified ID
     * @throws SQLException if a database access error occurs
     */
    @Override
    public Seat getById(int id) throws SQLException {
        String query = "SELECT * FROM seats WHERE id_PK = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, id);

        ResultSet resultSet = statement.executeQuery();

        Seat seat = transformResultsToClass(resultSet);
        statement.close();
        return seat;
    }

    /**
     * Inserts a new seat into the database.
     *
     * @param object the Seat object to be created in the database
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void create(Seat object) throws SQLException {
        String query = "INSERT INTO seats (airplane_FK, reservation_FK, seat_number, seat_class, is_window) " +
                "VALUES (?, ?, ?, ?, ?)";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, object.getAirplane_FK());
        
        if (object.getReservation_FK() != null) {
            statement.setInt(2, object.getReservation_FK());
        } else {
            statement.setNull(2, Types.INTEGER);
        }
        
        statement.setString(3, object.getSeat_number());
        statement.setString(4, object.getSeat_class().toString());
        statement.setBoolean(5, object.getIs_window());

        statement.executeUpdate();
        statement.close();
    }

    /**
     * Updates an existing seat in the database.
     *
     * @param id       the unique identifier of the seat to be updated
     * @param toUpdate the Seat object containing updated data
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void update(int id, Seat toUpdate) throws SQLException {
        String query = "UPDATE seats SET airplane_FK = ?, reservation_FK = ?, seat_number = ?, seat_class = ?, is_window = ? " +
                "WHERE id_PK = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, toUpdate.getAirplane_FK());
        
        if (toUpdate.getReservation_FK() != null) {
            statement.setInt(2, toUpdate.getReservation_FK());
        } else {
            statement.setNull(2, Types.INTEGER);
        }
        
        statement.setString(3, toUpdate.getSeat_number());
        statement.setString(4, toUpdate.getSeat_class().toString());
        statement.setBoolean(5, toUpdate.getIs_window());
        statement.setInt(6, id);

        statement.executeUpdate();
        statement.close();
    }

    /**
     * Deletes a seat from the database based on the provided ID.
     *
     * @param id the unique identifier of the seat to be deleted
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void delete(int id) throws SQLException {
        String query = "DELETE FROM seats WHERE id_PK = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, id);

        statement.executeUpdate();
        statement.close();
    }

    /**
     * Transforms the results from a ResultSet into a Seat object.
     *
     * @param resultSet the ResultSet containing seat data
     * @return a Seat object populated with data from the ResultSet
     * @throws SQLException if a database access error occurs
     */
    private Seat transformResultsToClass(ResultSet resultSet) throws SQLException {
        Seat seat = new Seat();

        if (resultSet.next()) {
            seat.setId(resultSet.getInt("id_PK"));
            seat.setAirplane_FK(resultSet.getInt("airplane_FK"));
            
            int reservationFK = resultSet.getInt("reservation_FK");
            if (!resultSet.wasNull()) {
                seat.setReservation_FK(reservationFK);
            } else {
                seat.setReservation_FK(null);
            }
            
            seat.setSeat_number(resultSet.getString("seat_number"));
            seat.setSeat_class(Seat.SeatClass.valueOf(resultSet.getString("seat_class")));
            seat.setIs_window(resultSet.getBoolean("is_window"));
        }

        return seat;
    }

    /**
     * Transforms the results from a ResultSet into an ArrayList of Seat objects.
     *
     * @param resultSet the ResultSet containing seat data
     * @return an ArrayList of Seat objects populated with data from the ResultSet
     * @throws SQLException if a database access error occurs
     */
    private ArrayList<Seat> transformResultsToClassArray(ResultSet resultSet) throws SQLException {
        ArrayList<Seat> seats = new ArrayList<>();

        while (resultSet.next()) {
            Seat seat = new Seat();
            seat.setId(resultSet.getInt("id_PK"));
            seat.setAirplane_FK(resultSet.getInt("airplane_FK"));
            
            int reservationFK = resultSet.getInt("reservation_FK");
            if (!resultSet.wasNull()) {
                seat.setReservation_FK(reservationFK);
            } else {
                seat.setReservation_FK(null);
            }
            
            seat.setSeat_number(resultSet.getString("seat_number"));
            seat.setSeat_class(Seat.SeatClass.valueOf(resultSet.getString("seat_class")));
            seat.setIs_window(resultSet.getBoolean("is_window"));
            seats.add(seat);
        }

        return seats;
    }

    /**
     * Returns seats by airplane ID.
     *
     * @param airplaneId the ID of the airplane
     * @return an ArrayList of Seat objects for the specified airplane
     * @throws SQLException if a database access error occurs
     */
    public ArrayList<Seat> getByAirplaneId(int airplaneId) throws SQLException {
        String query = "SELECT * FROM seats WHERE airplane_FK = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, airplaneId);

        ResultSet resultSet = statement.executeQuery();

        ArrayList<Seat> seats = transformResultsToClassArray(resultSet);
        statement.close();
        return seats;
    }

    /**
     * Returns seats by reservation ID.
     *
     * @param reservationId the ID of the reservation
     * @return an ArrayList of Seat objects for the specified reservation
     * @throws SQLException if a database access error occurs
     */
    public ArrayList<Seat> getByReservationId(int reservationId) throws SQLException {
        String query = "SELECT * FROM seats WHERE reservation_FK = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, reservationId);

        ResultSet resultSet = statement.executeQuery();

        ArrayList<Seat> seats = transformResultsToClassArray(resultSet);
        statement.close();
        return seats;
    }

    /**
     * Returns available seats (not reserved) for a specific airplane.
     *
     *
     * @param airplaneId the ID of the airplane
     * @return an ArrayList of available Seat objects for the specified airplane
     * @throws SQLException if a database access error occurs
     */
    public ArrayList<Seat> getAvailableSeatsByAirplaneId(int airplaneId) throws SQLException {
        String query = "SELECT * FROM seats WHERE airplane_FK = ? AND reservation_FK IS NULL";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, airplaneId);

        ResultSet resultSet = statement.executeQuery();

        ArrayList<Seat> seats = transformResultsToClassArray(resultSet);
        statement.close();
        return seats;
    }

    // Getters and Setters
    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
