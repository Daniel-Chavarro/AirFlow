package org.airflow.reservations.model;

import org.airflow.reservations.utils.PasswordUtils;

import java.time.LocalDateTime;

/**
 * Represents a user in the AirFlow reservation system.
 * This class contains all user information including personal details,
 * authentication credentials, and system privileges.
 */
public class User {
    /** The unique identifier for the user */
    private int id;
    /** The first name of the user */
    private String name;
    /** The last name of the user */
    private String last_name;
    /** The email address of the user (used for login and notifications) */
    private String email;
    /** The hashed password for user authentication */
    private String password;
    /** Flag indicating whether the user has administrative privileges */
    private Boolean isSuperUser;
    /** The timestamp when the user account was created */
    private LocalDateTime created_at;


    /**
     * Constructor for User class.
     * Initializes the user with specified values.
     *
     * @param id          the unique identifier of the user
     * @param name        the name of the user
     * @param last_name    the last name of the user
     * @param email       the email of the user
     * @param password    the password of the user
     * @param isSuperUser indicates if the user has super-user privileges
     * @param created_at  the date when the user was created
     */
    public User(int id, String name, String last_name, String email, String password, Boolean isSuperUser, LocalDateTime created_at) {
        this.id = id;
        this.name = name;
        this.last_name = last_name;
        this.email = email;
        this.password = password;
        this.isSuperUser = isSuperUser;
        this.created_at = created_at;
    }

    /**
     * Default constructor for User class.
     * Initializes the user with default values.
     * id = 0, name = "", email = "", isSuperUser = false, created_at = current date.
     */
    public User() {
        id = 0;
        name = "";
        last_name = "";
        email = "";
        password = PasswordUtils.hashPassword("");
        isSuperUser = false;
        created_at = LocalDateTime.now();
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getSuperUser() {
        return isSuperUser;
    }

    public void setSuperUser(Boolean superUser) {
        isSuperUser = superUser;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLastName(String lastName) {
    }
}
