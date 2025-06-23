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
    private int status_FK;
    private int origin_city_FK;
    private int destination_city_FK;
    private String code;
    private LocalDateTime departure_time;
    private LocalDateTime arrival_time;
    private float price_base;
    
    // Status information retrieved from joins
    private String status_name;
    private String status_description;

    /**
     * Constructor for Flight class.
     * Initializes the flight with specified values.
     *
     * @param id                the unique identifier of the flight
     * @param airplane_FK        foreign key to the airplane
     * @param status_FK          foreign key to the flight status
     * @param origin_city_FK      foreign key to the origin city
     * @param destination_city_FK foreign key to the destination city
     * @param code              the flight code
     * @param departure_time     the scheduled departure time
     * @param arrival_time       the scheduled arrival time
     * @param price_base         the ticket price for the flight
     */
    public Flight(int id, int airplane_FK, int status_FK, int origin_city_FK, int destination_city_FK,
                  String code, LocalDateTime departure_time, LocalDateTime arrival_time, float price_base) {
        this.id = id;
        this.airplane_FK = airplane_FK;
        this.status_FK = status_FK;
        this.origin_city_FK = origin_city_FK;
        this.destination_city_FK = destination_city_FK;
        this.code = code;
        this.departure_time = departure_time;
        this.arrival_time = arrival_time;
        this.price_base = price_base;
        this.status_name = "";
        this.status_description = "";
    }

    /**
     * Default constructor for Flight class.
     * Initializes the flight with default values.
     */
    public Flight() {
        this.id = 0;
        this.airplane_FK = 0;
        this.status_FK = 0;
        this.origin_city_FK = 0;
        this.destination_city_FK = 0;
        this.code = "";
        this.departure_time = LocalDateTime.now();
        this.arrival_time = LocalDateTime.now();
        this.price_base = 0.0f;
        this.status_name = "";
        this.status_description = "";
    }
    @Override
    public String toString() {
        return (
                "------------------------------------------"+"\n " +
                "Flight: " + id + "\n " +
                "Code :" + code + "\n " +
                "Departure time :" + departure_time +  "\n "+
                "Arrival time :" + arrival_time + "\n " +
                "Base Price :" + price_base+ "\n " +
                "------------------------------------------"+"\n ");
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

    public int getStatus_FK() {
        return status_FK;
    }

    public void setStatus_FK(int status_FK) {
        this.status_FK = status_FK;
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

    public float getPrice_base() {
        return price_base;
    }

    public void setPrice_base(float price_base) {
        this.price_base = price_base;
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
