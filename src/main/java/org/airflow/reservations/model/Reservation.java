package org.airflow.reservations.model;

import java.time.LocalDateTime;

/**
 * Represents a reservation in the reservation system.
 * Contains details such as reservation ID, user FK, status FK, flight FK, and reservation time.
 * Reservations link users to specific flights and track the booking status.
 */
public class Reservation {
    /** The unique identifier for the reservation */
    private int id;
    /** Foreign key reference to the user who made this reservation */
    private int user_FK;
    /** Foreign key reference to the current status of the reservation */
    private int status_FK;
    /** Foreign key reference to the flight being reserved */
    private int flight_FK;
    /** The timestamp when the reservation was created */
    private LocalDateTime reserved_at;
    
    // Status information retrieved from joins
    /** The human-readable name of the reservation status (retrieved from database joins) */
    private String status_name;
    /** Detailed description of the reservation status (retrieved from database joins) */
    private String status_description;

    /**
     * Constructor for Reservation class.
     * Initializes the reservation with specified values.
     *
     * @param id         the unique identifier of the reservation
     * @param user_FK    foreign key to the user who made the reservation
     * @param status_FK  foreign key to the status of the reservation
     * @param flight_FK  foreign key to the flight for which the reservation is made
     * @param reserved_at the date and time when the reservation was made
     */
    public Reservation(int id, int user_FK, int status_FK, int flight_FK, LocalDateTime reserved_at) {
        this.id = id;
        this.user_FK = user_FK;
        this.status_FK = status_FK;
        this.flight_FK = flight_FK;
        this.reserved_at = reserved_at;
        this.status_name = "";
        this.status_description = "";
    }

    /**
     * Default constructor for Reservation class.
     * Initializes the reservation with default values.
     * id = 0, user_FK = 0, status_FK = 0, flight_FK = 0, reserved_at = current time.
     */
    public Reservation() {
        this.id = 0;
        this.user_FK = 0;
        this.status_FK = 0;
        this.flight_FK = 0;
        this.reserved_at = LocalDateTime.now();
        this.status_name = "";
        this.status_description = "";
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_FK() {
        return user_FK;
    }

    public void setUser_FK(int user_FK) {
        this.user_FK = user_FK;
    }

    public int getStatus_FK() {
        return status_FK;
    }

    public void setStatus_FK(int status_FK) {
        this.status_FK = status_FK;
    }

    public int getFlight_FK() {
        return flight_FK;
    }

    public void setFlight_FK(int flight_FK) {
        this.flight_FK = flight_FK;
    }

    public LocalDateTime getReserved_at() {
        return reserved_at;
    }

    public void setReserved_at(LocalDateTime reserved_at) {
        this.reserved_at = reserved_at;
    }
    
    public String getStatus_name() {
        return status_name;
    }

    public void setStatus_name(String status_name) {
        this.status_name = status_name;
    }

    public String getStatus_description() {
        return status_description;
    }

    public void setStatus_description(String status_description) {
        this.status_description = status_description;
    }
}
