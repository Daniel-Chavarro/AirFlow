package org.airflow.reservations.utils;

import org.airflow.reservations.DAO.*;
import org.airflow.reservations.model.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.Random;

/**
 * Utility class for generating and inserting fake data into the database.
 * This class populates all tables with realistic test data to facilitate
 * development and testing.
 */
public class DataGenerator {
    private final Random random = new Random();
    private final Connection connection;

    // DAOs
    private final UsersDAO usersDAO;
    private final AirplaneDAO airplaneDAO;
    private final CityDAO cityDAO;
    private final FlightDAO flightDAO;
    private final ReservationDAO reservationDAO;
    private final SeatDAO seatDAO;

    // Lists to store generated entities for relationships
    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<Airplane> airplanes = new ArrayList<>();
    private ArrayList<City> cities = new ArrayList<>();
    private ArrayList<Flight> flights = new ArrayList<>();

    /**
     * Constructor initializes connection and DAOs
     */
    public DataGenerator() throws SQLException {
        connection = ConnectionDB.getConnection();
        usersDAO = new UsersDAO();
        airplaneDAO = new AirplaneDAO();
        cityDAO = new CityDAO();
        flightDAO = new FlightDAO();
        reservationDAO = new ReservationDAO();
        seatDAO = new SeatDAO();
    }

    /**
     * Main method to generate all fake data and insert into database
     */
    public void generateAllData() {
        try {
            // Ensure recovery tokens table exists.
            initializePasswordResetTokensTable();
            
            // Clean up existing data if needed
            cleanupDatabase();

            // Initialize reference data
            initializeFlightStatus();
            initializeReservationStatus();

            // Generate and insert main data
            generateUsers(20);
            generateAirplanes(10);
            generateCities(30);
            generateFlights(50);
            generateReservationsAndSeats(100);

            System.out.println("Data generation completed successfully!");
        } catch (SQLException e) {
            System.err.println("Error generating data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Clean all tables in the database
     */
    private void cleanupDatabase() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Disable foreign key checks to allow deletion
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");

            // Delete data from all tables
            stmt.execute("TRUNCATE TABLE seats");
            stmt.execute("TRUNCATE TABLE reservations");
            stmt.execute("TRUNCATE TABLE flights");
            stmt.execute("TRUNCATE TABLE cities");
            stmt.execute("TRUNCATE TABLE airplanes");
            stmt.execute("TRUNCATE TABLE users");
            stmt.execute("TRUNCATE TABLE flight_status");
            stmt.execute("TRUNCATE TABLE reservations_status");
            stmt.execute("TRUNCATE TABLE password_reset_tokens");

            // Re-enable foreign key checks
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }
    
    /**
    * Ensures password recovery tokens table exists.
    */
    private void initializePasswordResetTokensTable() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS password_reset_tokens (" +
                "  token VARCHAR(255) PRIMARY KEY, " +
                "  user_id INT NOT NULL, " +
                "  expires_at TIMESTAMP NOT NULL, " +
                "  FOREIGN KEY (user_id) REFERENCES users(id_PK)" +
                ")"
            );
        }
    }


    /**
     * Initialize flight status reference data
     */
    private void initializeFlightStatus() throws SQLException {
        String[] statusNames = {"SCHEDULED", "DELAYED", "CANCELLED", "BOARDING", "IN_FLIGHT", "LANDED", "COMPLETED"};
        String[] descriptions = {
            "Flight is scheduled as planned",
            "Flight is delayed",
            "Flight has been cancelled",
            "Boarding in progress",
            "Flight is currently in the air",
            "Flight has landed at destination",
            "Flight has completed all processes"
        };

        try (PreparedStatement pstmt = connection.prepareStatement(
                "INSERT INTO flight_status (id_PK, name, description) VALUES (?, ?, ?)")) {

            for (int i = 0; i < statusNames.length; i++) {
                pstmt.setInt(1, i + 1);
                pstmt.setString(2, statusNames[i]);
                pstmt.setString(3, descriptions[i]);
                pstmt.executeUpdate();
            }
        }
    }

    /**
     * Initialize reservation status reference data
     */
    private void initializeReservationStatus() throws SQLException {
        String[] statusNames = {"CONFIRMED", "CANCELLED", "PENDING", "CHECKED_IN", "COMPLETED"};
        String[] descriptions = {
            "Reservation is confirmed",
            "Reservation has been cancelled",
            "Reservation is pending confirmation",
            "Passenger has checked in",
            "Travel has been completed"
        };

        try (PreparedStatement pstmt = connection.prepareStatement(
                "INSERT INTO reservations_status (id_PK, name, description) VALUES (?, ?, ?)")) {

            for (int i = 0; i < statusNames.length; i++) {
                pstmt.setInt(1, i + 1);
                pstmt.setString(2, statusNames[i]);
                pstmt.setString(3, descriptions[i]);
                pstmt.executeUpdate();
            }
        }
    }

    /**
     * Generate fake users
     */
    private void generateUsers(int count) throws SQLException {
        String[] firstNames = {"Juan", "María", "Carlos", "Ana", "Luis", "Sofia", "Miguel", "Laura", "Pedro", "Elena"};
        String[] lastNames = {"García", "Rodríguez", "López", "Martínez", "González", "Pérez", "Sánchez", "Fernández"};

        try (PreparedStatement pstmt = connection.prepareStatement(
                "INSERT INTO users (name, last_name, email, password, isSuperUser, created_at) VALUES (?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {

            for (int i = 0; i < count; i++) {
                String firstName = firstNames[random.nextInt(firstNames.length)];
                String lastName = lastNames[random.nextInt(lastNames.length)];
                String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + random.nextInt(100) + "@example.com";

                pstmt.setString(1, firstName);
                pstmt.setString(2, lastName);
                pstmt.setString(3, email);
                pstmt.setString(4, PasswordUtils.hashPassword("password123")); // Usando hash para password
                pstmt.setBoolean(5, random.nextInt(10) < 1); // 10% de probabilidad de ser superuser
                pstmt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));

                pstmt.executeUpdate();

                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        User user = new User();
                        user.setId(generatedKeys.getInt(1));
                        user.setName(firstName);
                        user.setLastName(lastName);
                        user.setEmail(email);
                        users.add(user);
                    }
                }
            }
        }

        System.out.println("Generated " + users.size() + " users");
    }

    /**
     * Generate fake airplanes
     */
    private void generateAirplanes(int count) throws SQLException {
        String[] airlines = {"Aeroméxico", "Volaris", "Interjet", "Viva Aerobus", "American Airlines", "Delta", "United", "Iberia"};
        String[] models = {"Boeing 737", "Airbus A320", "Boeing 787", "Airbus A350", "Embraer E190", "Boeing 777"};

        try (PreparedStatement pstmt = connection.prepareStatement(
                "INSERT INTO airplanes (airline, model, code, capacity, year) VALUES (?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {

            for (int i = 0; i < count; i++) {
                String airline = airlines[random.nextInt(airlines.length)];
                String model = models[random.nextInt(models.length)];
                String code = "AC" + (1000 + random.nextInt(9000));
                int capacity = 150 + random.nextInt(250);
                int year = 2000 + random.nextInt(23); // Entre 2000 y 2022

                pstmt.setString(1, airline);
                pstmt.setString(2, model);
                pstmt.setString(3, code);
                pstmt.setInt(4, capacity);
                pstmt.setInt(5, year);

                pstmt.executeUpdate();

                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        Airplane airplane = new Airplane();
                        airplane.setId(generatedKeys.getInt(1));
                        airplane.setAirline(airline);
                        airplane.setModel(model);
                        airplane.setCode(code);
                        airplane.setCapacity(capacity);
                        airplane.setYear(Year.of(year));
                        airplanes.add(airplane);

                        // Generate seats for this airplane
                        generateSeatsForAirplane(airplane);
                    }
                }
            }
        }

        System.out.println("Generated " + airplanes.size() + " airplanes with seats");
    }

    /**
     * Generate seats for an airplane
     */
    private void generateSeatsForAirplane(Airplane airplane) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(
                "INSERT INTO seats (airplane_FK, seat_number, seat_class, is_window) VALUES (?, ?, ?, ?)")) {

            int firstClassSeats = airplane.getCapacity() / 10;
            int businessSeats = airplane.getCapacity() / 5;
            int economySeats = airplane.getCapacity() - firstClassSeats - businessSeats;

            // First class seats
            for (int i = 1; i <= firstClassSeats; i++) {
                String seatNumber = "F" + i;
                pstmt.setInt(1, airplane.getId());
                pstmt.setString(2, seatNumber);
                pstmt.setString(3, "FIRST");
                pstmt.setBoolean(4, i % 2 == 0);
                pstmt.executeUpdate();
            }

            // Business class seats
            for (int i = 1; i <= businessSeats; i++) {
                String seatNumber = "B" + i;
                pstmt.setInt(1, airplane.getId());
                pstmt.setString(2, seatNumber);
                pstmt.setString(3, "BUSINESS");
                pstmt.setBoolean(4, i % 3 == 0);
                pstmt.executeUpdate();
            }

            // Economy class seats
            for (int i = 1; i <= economySeats; i++) {
                String seatNumber = "E" + i;
                pstmt.setInt(1, airplane.getId());
                pstmt.setString(2, seatNumber);
                pstmt.setString(3, "ECONOMY");
                pstmt.setBoolean(4, i % 6 == 0 || i % 6 == 5);
                pstmt.executeUpdate();
            }
        }
    }

    /**
     * Generate fake cities
     */
    private void generateCities(int count) throws SQLException {
        String[][] cityData = {
            {"Ciudad de México", "México", "MEX"},
            {"Guadalajara", "México", "GDL"},
            {"Monterrey", "México", "MTY"},
            {"Cancún", "México", "CUN"},
            {"Los Ángeles", "Estados Unidos", "LAX"},
            {"Nueva York", "Estados Unidos", "JFK"},
            {"Miami", "Estados Unidos", "MIA"},
            {"Chicago", "Estados Unidos", "ORD"},
            {"Toronto", "Canadá", "YYZ"},
            {"Vancouver", "Canadá", "YVR"},
            {"Madrid", "España", "MAD"},
            {"Barcelona", "España", "BCN"},
            {"París", "Francia", "CDG"},
            {"Londres", "Reino Unido", "LHR"},
            {"Roma", "Italia", "FCO"},
            {"Amsterdam", "Países Bajos", "AMS"},
            {"Berlín", "Alemania", "BER"},
            {"Tokio", "Japón", "NRT"},
            {"Sídney", "Australia", "SYD"},
            {"Río de Janeiro", "Brasil", "GIG"},
            {"Buenos Aires", "Argentina", "EZE"},
            {"Lima", "Perú", "LIM"},
            {"Bogotá", "Colombia", "BOG"},
            {"Santiago", "Chile", "SCL"},
            {"Dubai", "Emiratos Árabes Unidos", "DXB"},
            {"Singapur", "Singapur", "SIN"},
            {"Hong Kong", "China", "HKG"},
            {"Johannesburgo", "Sudáfrica", "JNB"},
            {"El Cairo", "Egipto", "CAI"},
            {"Moscú", "Rusia", "SVO"}
        };

        try (PreparedStatement pstmt = connection.prepareStatement(
                "INSERT INTO cities (name, country, code) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {

            for (int i = 0; i < Math.min(count, cityData.length); i++) {
                pstmt.setString(1, cityData[i][0]);
                pstmt.setString(2, cityData[i][1]);
                pstmt.setString(3, cityData[i][2]);

                pstmt.executeUpdate();

                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        City city = new City();
                        city.setId(generatedKeys.getInt(1));
                        city.setName(cityData[i][0]);
                        city.setCountry(cityData[i][1]);
                        city.setCode(cityData[i][2]);
                        cities.add(city);
                    }
                }
            }
        }

        System.out.println("Generated " + cities.size() + " cities");
    }

    /**
     * Generate fake flights
     */
    private void generateFlights(int count) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(
                "INSERT INTO flights (airplane_FK, status_FK, origin_city_FK, destination_city_FK, code, " +
                "departure_time, scheduled_arrival_time, arrival_time, price_base) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {

            for (int i = 0; i < count; i++) {
                Airplane airplane = airplanes.get(random.nextInt(airplanes.size()));

                // Ensure origin and destination are different
                int originIndex = random.nextInt(cities.size());
                int destIndex;
                do {
                    destIndex = random.nextInt(cities.size());
                } while (destIndex == originIndex);

                City origin = cities.get(originIndex);
                City destination = cities.get(destIndex);

                // Generate flight code
                String flightCode = origin.getCode() + destination.getCode() + (100 + random.nextInt(900));

                // Generate random flight status (1-7)
                int statusId = 1 + random.nextInt(7);

                // Generate departure time (between now and 6 months in the future)
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime departureTime = now.plusDays(random.nextInt(180));
                departureTime = departureTime.withHour(random.nextInt(24)).withMinute(random.nextInt(60));

                // Flight duration between 1 and 12 hours
                int durationHours = 1 + random.nextInt(12);
                int durationMinutes = random.nextInt(60);

                // Calculate scheduled arrival time
                LocalDateTime scheduledArrivalTime = departureTime.plusHours(durationHours).plusMinutes(durationMinutes);

                // For actual arrival time, add some randomness for realism (some flights arrive early, some late)
                int arrivalOffset = random.nextInt(61) - 30; // Between -30 and +30 minutes
                LocalDateTime arrivalTime = scheduledArrivalTime.plusMinutes(arrivalOffset);

                // Convert to java.sql.Timestamp
                Timestamp departureTimestamp = Timestamp.valueOf(departureTime);
                Timestamp scheduledArrivalTimestamp = Timestamp.valueOf(scheduledArrivalTime);
                Timestamp arrivalTimestamp = Timestamp.valueOf(arrivalTime);

                // Base price between $100 and $1000
                float basePrice = (100 + random.nextInt(900));

                pstmt.setInt(1, airplane.getId());
                pstmt.setInt(2, statusId);
                pstmt.setInt(3, origin.getId());
                pstmt.setInt(4, destination.getId());
                pstmt.setString(5, flightCode);
                pstmt.setTimestamp(6, departureTimestamp);
                pstmt.setTimestamp(7, scheduledArrivalTimestamp);
                pstmt.setTimestamp(8, arrivalTimestamp);
                pstmt.setFloat(9, basePrice);

                pstmt.executeUpdate();

                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        Flight flight = new Flight();
                        flight.setId(generatedKeys.getInt(1));
                        flight.setAirplane_FK(airplane.getId());
                        flight.setStatus_FK(statusId);
                        flight.setOrigin_city_FK(origin.getId());
                        flight.setDestination_city_FK(destination.getId());
                        flight.setCode(flightCode);
                        flight.setDeparture_time(departureTimestamp.toLocalDateTime());
                        flight.setScheduled_arrival_time(scheduledArrivalTimestamp.toLocalDateTime());
                        flight.setArrival_time(arrivalTimestamp.toLocalDateTime());
                        flight.setPrice_base(basePrice);
                        flights.add(flight);
                    }
                }
            }
        }

        System.out.println("Generated " + flights.size() + " flights");
    }

    /**
     * Generate fake reservations and assign seats
     */
    private void generateReservationsAndSeats(int count) throws SQLException {
        try (
            PreparedStatement reservationStmt = connection.prepareStatement(
                "INSERT INTO reservations (user_FK, status_FK, flight_FK, reserved_at) VALUES (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS);
            PreparedStatement updateSeatStmt = connection.prepareStatement(
                "UPDATE seats SET reservation_FK = ? WHERE id_PK = ? AND reservation_FK IS NULL")
        ) {
            for (int i = 0; i < count && i < flights.size() * users.size(); i++) {
                User user = users.get(random.nextInt(users.size()));
                Flight flight = flights.get(random.nextInt(flights.size()));

                // Random reservation status (1-5)
                int statusId = 1 + random.nextInt(5);

                // Reservation time (before departure time)
                LocalDateTime departureTime = flight.getDeparture_time();
                LocalDateTime reservationTime = departureTime.minusDays(1 + random.nextInt(30));
                Timestamp reservationTimestamp = Timestamp.valueOf(reservationTime);

                reservationStmt.setInt(1, user.getId());
                reservationStmt.setInt(2, statusId);
                reservationStmt.setInt(3, flight.getId());
                reservationStmt.setTimestamp(4, reservationTimestamp);

                reservationStmt.executeUpdate();

                // Get the generated reservation ID
                try (ResultSet generatedKeys = reservationStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int reservationId = generatedKeys.getInt(1);

                        // Find and assign a free seat for this airplane
                        try (PreparedStatement seatQuery = connection.prepareStatement(
                                "SELECT id_PK FROM seats WHERE airplane_FK = ? AND reservation_FK IS NULL LIMIT 1")) {
                            seatQuery.setInt(1, flight.getAirplane_FK());

                            try (ResultSet seatRs = seatQuery.executeQuery()) {
                                if (seatRs.next()) {
                                    int seatId = seatRs.getInt("id_PK");

                                    // Update the seat with the reservation ID
                                    updateSeatStmt.setInt(1, reservationId);
                                    updateSeatStmt.setInt(2, seatId);
                                    updateSeatStmt.executeUpdate();
                                }
                            }
                        }
                    }
                }
            }
        }

        System.out.println("Generated reservations and assigned seats");
    }

    /**
     * Main method to run the data generator
     */
    public static void main(String[] args) {
        try {
            DataGenerator generator = new DataGenerator();
            generator.generateAllData();
        } catch (SQLException e) {
            System.err.println("Error in data generation: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
