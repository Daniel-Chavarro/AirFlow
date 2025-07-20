package org.airflow.reservations.demo;

import org.airflow.reservations.model.Reservation;
import org.airflow.reservations.service.ReservationService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Demonstration class showing the waitlist functionality.
 * This is intended for documentation and manual testing purposes.
 * Note: This requires a database connection to work.
 */
public class WaitlistDemo {
    
    public static void main(String[] args) {
        try {
            // Initialize the service
            ReservationService reservationService = new ReservationService();
            
            System.out.println("=== Waitlist Management Demo ===\n");
            
            // Display current waitlist
            displayWaitlist(reservationService);
            
            // Assign next waiting user
            assignNextUser(reservationService);
            
            // Display updated waitlist
            displayWaitlist(reservationService);
            
            // Demonstrate flight-specific operations
            demonstrateFlightSpecific(reservationService);
            
        } catch (SQLException e) {
            System.err.println("Database connection error. This demo requires a database connection.");
            System.err.println("Error: " + e.getMessage());
            
            // Show conceptual example instead
            showConceptualExample();
        }
    }
    
    private static void displayWaitlist(ReservationService service) throws SQLException {
        System.out.println("Current Waitlist (ordered by reservation time):");
        ArrayList<Reservation> waitlist = service.getWaitlist();
        
        if (waitlist.isEmpty()) {
            System.out.println("  No users currently waiting.");
        } else {
            for (int i = 0; i < waitlist.size(); i++) {
                Reservation res = waitlist.get(i);
                System.out.printf("  %d. User %d - Flight %d - Reserved at: %s%n", 
                    i + 1, res.getUser_FK(), res.getFlight_FK(), 
                    res.getReserved_at().toString());
            }
        }
        System.out.println();
    }
    
    private static void assignNextUser(ReservationService service) throws SQLException {
        System.out.println("Assigning seat to next waiting user...");
        Reservation assigned = service.assignNextWaitingUser();
        
        if (assigned == null) {
            System.out.println("  No users in waitlist to assign.");
        } else {
            System.out.printf("  ✓ Assigned seat to User %d (Reservation ID: %d)%n", 
                assigned.getUser_FK(), assigned.getId());
            System.out.printf("  Status changed from waiting (1) to confirmed (2)%n");
        }
        System.out.println();
    }
    
    private static void demonstrateFlightSpecific(ReservationService service) throws SQLException {
        System.out.println("Flight-specific waitlist operations:");
        
        // Get waitlist for flight 1
        ArrayList<Reservation> flight1Waitlist = service.getWaitlistByFlight(1);
        System.out.printf("Waitlist for Flight 1: %d users%n", flight1Waitlist.size());
        
        // Assign next user for flight 1 specifically
        Reservation assigned = service.assignNextWaitingUserByFlight(1);
        if (assigned != null) {
            System.out.printf("  ✓ Assigned User %d to Flight 1%n", assigned.getUser_FK());
        } else {
            System.out.println("  No users waiting for Flight 1");
        }
        System.out.println();
    }
    
    private static void showConceptualExample() {
        System.out.println("\n=== Conceptual Example (Database not available) ===\n");
        
        System.out.println("The ReservationService provides the following waitlist functionality:\n");
        
        System.out.println("1. getWaitlist():");
        System.out.println("   - Retrieves all users with status_FK = 1 (waiting)");
        System.out.println("   - Orders by reserved_at ASC (oldest first)");
        System.out.println("   - Example result: [User 1 (2h ago), User 2 (1h ago), User 3 (30m ago)]\n");
        
        System.out.println("2. assignNextWaitingUser():");
        System.out.println("   - Takes the oldest waiting reservation");
        System.out.println("   - Changes status_FK from 1 (waiting) to 2 (confirmed)");
        System.out.println("   - Returns the assigned reservation");
        System.out.println("   - Returns null if waitlist is empty\n");
        
        System.out.println("3. getWaitlistByFlight(flightId):");
        System.out.println("   - Same as getWaitlist() but filtered by specific flight");
        System.out.println("   - Useful for flight-specific seat assignments\n");
        
        System.out.println("4. assignNextWaitingUserByFlight(flightId):");
        System.out.println("   - Assigns next waiting user for a specific flight");
        System.out.println("   - Ensures fairness within each flight's waitlist\n");
        
        System.out.println("Database Schema:");
        System.out.println("  reservations table: id_PK, user_FK, status_FK, flight_FK, reserved_at");
        System.out.println("  status_FK = 1: waiting (en espera)");
        System.out.println("  status_FK = 2: confirmed (confirmado)");
    }
}