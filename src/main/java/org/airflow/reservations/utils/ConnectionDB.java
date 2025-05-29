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
    private final static String URL = "jdbc:mysql://localhost:3306/";
    private final static String DATABASE = "airflow";
    private final static String USER = "root";
    private final static String PASSWORD = "root";

    /**
     * Establishes a connection to the MySQL database.
     *
     * @return a Connection object to the database
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL + DATABASE, USER, PASSWORD);
    }


}
