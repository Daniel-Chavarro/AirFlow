package org.airflow.reservations.DAO;


import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Interface defining the methods for Data Access Object (DAO) operations.
 * This interface provides generic methods for CRUD operations on objects of type T.
 *
 * @param <T> the type of the object that this DAO will manage
 */
public interface DAOMethods<T> {

    /**
     * Generic method to get all objects of type T.
     *
     * @return a list containing all objects of type T.
     * @throws SQLException if a database access error occurs.
     */
    ArrayList<T> getAll() throws SQLException;

    /**
     * Generic method to get an object of type T by its ID.
     *
     * @param id the ID of the object to be retrieved.
     * @return the object of type T with the specified ID.
     * @throws SQLException if a database access error occurs.
     */
        T getById(int id) throws SQLException;


    /**
     * Generic method to create a new object of type T.
     *
     * @param object the object to be created.
     * @throws SQLException if a database access error occurs.
     */
    void create(T object) throws SQLException;

    /**
     * Generic method to update an existing object of type T by its ID.
     *
     * @param id       the ID of the object to be updated.
     * @param toUpdate the object containing updated data.
     * @throws SQLException if a database access error occurs.
     */
    void update(T toUpdate) throws SQLException;

    /**
     * Generic method to delete an object of type T by its ID.
     *
     * @param id the ID of the object to be deleted.
     * @throws SQLException if a database access error occurs.
     */
    void delete(int id) throws SQLException;
}
