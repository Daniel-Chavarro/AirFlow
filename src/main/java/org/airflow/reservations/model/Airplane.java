package org.airflow.reservations.model;

import java.time.Year;

/**
 * Represents an airplane in the reservation system.
 * Contains details such as airplane ID, airline, model, code, capacity, and year.
 * Airplanes are assigned to flights and contain seats for passenger reservations.
 */
public class Airplane {
    /** The unique identifier for the airplane */
    private int id;
    /** The airline company that operates this airplane */
    private String airline;
    /** The aircraft model (e.g., "Boeing 737", "Airbus A320") */
    private String model;
    /** The unique airplane code identifier within the airline fleet */
    private String code;
    /** The total passenger capacity of the airplane */
    private int capacity;
    /** The year the airplane was manufactured */
    private Year year;

    /**
     * Constructor for Airplane class.
     * Initializes the airplane with specified values.
     *
     * @param id       the unique identifier of the airplane
     * @param airline  the airline that operates the airplane
     * @param model    the model of the airplane
     * @param code     the code of the airplane
     * @param capacity the passenger capacity of the airplane
     * @param year     the manufacturing year of the airplane
     */
    public Airplane(int id, String airline, String model, String code, int capacity, Year year) {
        this.id = id;
        this.airline = airline;
        this.model = model;
        this.code = code;
        this.capacity = capacity;
        this.year = year;
    }

    /**
     * Default constructor for Airplane class.
     * Initializes the airplane with default values.
     * id = 0, airline = "", model = "", code = "", capacity = 0, year = current year.
     */
    public Airplane() {
        this.id = 0;
        this.airline = "";
        this.model = "";
        this.code = "";
        this.capacity = 0;
        this.year = Year.now();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAirline() {
        return airline;
    }

    public void setAirline(String airline) {
        this.airline = airline;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Year getYear() {
        return year;
    }

    public void setYear(Year year) {
        this.year = year;
    }
}
