package org.airflow.reservations.utils;

import org.airflow.reservations.service.Controller;

/**
 * Main application runner class.
 * This class serves as the entry point for the AirFlow Reservation System.
 * It delegates all startup logic to the Controller's runner method for better organization.
 */
public class Runner {
    /**
     * Main method - entry point of the application.
     *
     * @param args command line arguments (currently not used)
     */
    public static void main(String[] args) {
        System.out.println("Initializing AirFlow Reservation System...");

        try {
            // Get the controller instance and run the application
            Controller controller = Controller.getInstance();
            controller.runner();

        } catch (Exception e) {
            System.err.println("Fatal error during application startup: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
