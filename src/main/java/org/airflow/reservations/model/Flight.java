package org.airflow.reservations.model;

import java.time.LocalDateTime;

/**
 * Represents a flight in the reservation's system.
 * Contains details such as flight ID, airplane, status, origin and destination cities,
 * flight code, departure and arrival times, and ticket price.
 */
public class Flight {
    private int id;
    private int airplane_FK;
    private String status;
    private int origin_city_FK;
    private int destination_city_FK;
    private String code;
    private LocalDateTime departure_time;
    private LocalDateTime arrival_time;
    private float price;

    /**
     * Constructor for Flight class.
     * Initializes the flight with specified values.
     *
     * @param id                the unique identifier of the flight
     * @param airplane_FK        foreign key to the airplane
     * @param status            the current status of the flight
     * @param origin_city_FK      foreign key to the origin city
     * @param destination_city_FK foreign key to the destination city
     * @param code              the flight code
     * @param departure_time     the scheduled departure time
     * @param arrival_time       the scheduled arrival time
     * @param price             the ticket price for the flight
     */
    public Flight(int id, int airplane_FK, String status, int origin_city_FK, int destination_city_FK,
                  String code, LocalDateTime departure_time, LocalDateTime arrival_time, float price) {
        this.id = id;
        this.airplane_FK = airplane_FK;
        this.status = status;
        this.origin_city_FK = origin_city_FK;
        this.destination_city_FK = destination_city_FK;
        this.code = code;
        this.departure_time = departure_time;
        this.arrival_time = arrival_time;
        this.price = price;
    }

    /**
     * Default constructor for Flight class.
     * Initializes the flight with default values.
     * id = 0, airplane_FK = 0, status = "", origin_city_FK = 0,
     * destination_city_FK = 0, code = "", departure_time = current time,
     * arrival_time = current time, price = 0.0f.
     */
    public Flight() {
        this.id = 0;
        this.airplane_FK = 0;
        this.status = "";
        this.origin_city_FK = 0;
        this.destination_city_FK = 0;
        this.code = "";
        this.departure_time = LocalDateTime.now();
        this.arrival_time = LocalDateTime.now();
        this.price = 0.0f;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAirplane_FK() {
        return airplane_FK;
    }

    public void setAirplane_FK(int airplane_FK) {
        this.airplane_FK = airplane_FK;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getOrigin_city_FK() {
        return origin_city_FK;
    }

    public void setOrigin_city_FK(int origin_city_FK) {
        this.origin_city_FK = origin_city_FK;
    }

    public int getDestination_city_FK() {
        return destination_city_FK;
    }

    public void setDestination_city_FK(int destination_city_FK) {
        this.destination_city_FK = destination_city_FK;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDateTime getDeparture_time() {
        return departure_time;
    }

    public void setDeparture_time(LocalDateTime departure_time) {
        this.departure_time = departure_time;
    }

    public LocalDateTime getArrival_time() {
        return arrival_time;
    }

    public void setArrival_time(LocalDateTime arrival_time) {
        this.arrival_time = arrival_time;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
