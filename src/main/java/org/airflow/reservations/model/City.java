package org.airflow.reservations.model;

/**
 * Represents a city in the reservation system.
 * Contains details such as city ID, name, country, and code.
 */
public class City {
    private int id;
    private String name;
    private String country;
    private String code;

    /**
     * Constructor for City class.
     * Initializes the city with specified values.
     *
     * @param id      the unique identifier of the city
     * @param name    the name of the city
     * @param country the country where the city is located
     * @param code    the airport code of the city
     */
    public City(int id, String name, String country, String code) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.code = code;
    }

    /**
     * Default constructor for City class.
     * Initializes the city with default values.
     * id = 0, name = "", country = "", code = "".
     */
    public City() {
        this.id = 0;
        this.name = "";
        this.country = "";
        this.code = "";
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
