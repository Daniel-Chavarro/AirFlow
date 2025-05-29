package org.airflow.reservations.model;

/**
 * Represents a reservation status in the reservation system.
 * Contains details such as status ID, name, and description.
 */
public class ReservationStatus {
    private int id;
    private String name;
    private String description;

    /**
     * Constructor for ReservationStatus class.
     * Initializes the reservation status with specified values.
     *
     * @param id          the unique identifier of the reservation status
     * @param name        the name of the reservation status
     * @param description the description of the reservation status
     */
    public ReservationStatus(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    /**
     * Default constructor for ReservationStatus class.
     * Initializes the reservation status with default values.
     * id = 0, name = "", description = "".
     */
    public ReservationStatus() {
        this.id = 0;
        this.name = "";
        this.description = "";
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
