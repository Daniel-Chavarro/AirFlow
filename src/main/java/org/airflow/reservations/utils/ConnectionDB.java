package org.airflow.reservations.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class for establishing a connection to a MySQL database.
 * This class provides a static method to get a connection object
 * to the specified database using JDBC.
 */
public class ConnectionDB {
    /** The base URL for the MySQL database connection */
    private final static String URL = "jdbc:mysql://localhost:3306/";
    /** The name of the database to connect to */
    private final static String DATABASE = "airflow";
    /** The username for database authentication */
    private final static String USER = "root";
    /** The password for database authentication */
    private final static String PASSWORD = "root";
    /** Additional connection parameters for MySQL configuration */
    private final static String PARAMS = "?useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowPublicKeyRetrieval=true";

    /**
     * Establishes a connection to the MySQL database.
     *
     * @return a Connection object to the database
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL + DATABASE + PARAMS, USER, PASSWORD);
    }
}
