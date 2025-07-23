package org.airflow.reservations.model;

/**
 * Represents a seat in the reservation system.
 * Contains details such as seat ID, airplane FK, reservation FK, seat number, seat class, and window status.
 * Seats are part of airplanes and can be reserved by passengers.
 */
public class Seat {
    /** The unique identifier for the seat */
    private int id;
    /** Foreign key reference to the airplane this seat belongs to */
    private int airplane_FK;
    /** Foreign key reference to the reservation (null if seat is available) */
    private Integer reservation_FK; // Can be null
    /** The seat number identifier (e.g., "12A", "15F") */
    private String seat_number;
    /** The class/category of the seat (ECONOMY, BUSINESS, FIRST) */
    private SeatClass seat_class;
    /** Flag indicating whether this is a window seat */
    private Boolean is_window;

    /**
     * Enum representing the different classes of seats.
     */
    public enum SeatClass {
        ECONOMY, BUSINESS, FIRST
    }

    /**
     * Constructor for Seat class.
     * Initializes the seat with specified values.
     *
     * @param id             the unique identifier of the seat
     * @param airplane_FK    foreign key to the airplane to which the seat belongs
     * @param reservation_FK foreign key to the reservation associated with the seat (can be null)
     * @param seat_number    the seat number
     * @param seat_class     the class of the seat (ECONOMY, BUSINESS, FIRST)
     * @param is_window      indicates if the seat is a window seat
     */
    public Seat(int id, int airplane_FK, Integer reservation_FK, String seat_number, SeatClass seat_class, Boolean is_window) {
        this.id = id;
        this.airplane_FK = airplane_FK;
        this.reservation_FK = reservation_FK;
        this.seat_number = seat_number;
        this.seat_class = seat_class;
        this.is_window = is_window;
    }

    /**
     * Default constructor for Seat class.
     * Initializes the seat with default values.
     * id = 0, airplane_FK = 0, reservation_FK = null, seat_number = "", seat_class = ECONOMY, is_window = false.
     */
    public Seat() {
        this.id = 0;
        this.airplane_FK = 0;
        this.reservation_FK = null;
        this.seat_number = "";
        this.seat_class = SeatClass.ECONOMY;
        this.is_window = false;
    }

    @Override
    public String toString() {
        String ventana = is_window?"YES":"NO";
        return (
                "------------------------------------------"+"\n " +
                "Seat: " + id + "\n " +
                "Airplane :" + airplane_FK + "\n " +
                "Number:" + seat_number +  "\n "+
                "Class :" + seat_class + "\n " +
                "is_window :" + ventana + "\n " +
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

    public Integer getReservation_FK() {
        return reservation_FK;
    }

    public void setReservation_FK(Integer reservation_FK) {
        this.reservation_FK = reservation_FK;
    }

    public String getSeat_number() {
        return seat_number;
    }

    public void setSeat_number(String seat_number) {
        this.seat_number = seat_number;
    }

    public SeatClass getSeat_class() {
        return seat_class;
    }

    public void setSeat_class(SeatClass seat_class) {
        this.seat_class = seat_class;
    }

    public Boolean getIs_window() {
        return is_window;
    }

    public void setIs_window(Boolean is_window) {
        this.is_window = is_window;
    }
}
