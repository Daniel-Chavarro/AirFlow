package org.airflow.reservations.model;

import java.time.LocalDateTime;

/**
 * Represents a reservation in the reservation system.
 * Contains details such as reservation ID, user FK, status FK, flight FK, and reservation time.
 */
public class Reservation {
    private int id;
    private int user_FK;
    private int status_FK;
    private int flight_FK;
    private LocalDateTime reserved_at;

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
}
