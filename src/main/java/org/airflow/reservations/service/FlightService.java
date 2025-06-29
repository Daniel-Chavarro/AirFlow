package org.airflow.reservations.service;

import org.airflow.reservations.DAO.FlightDAO;
import org.airflow.reservations.model.Flight;

import java.sql.SQLException;
import java.util.ArrayList;

public class FlightService {
    private FlightDAO flightDAO;

    public FlightService() throws Exception {
        this.flightDAO = new FlightDAO();
    }
    public FlightService(FlightDAO flightDAO) {
        this.flightDAO = flightDAO;
    }
    /**
     * Function to get all the available flights between two cities.
     *@param Destiny_id: id of the destination city
     *@param Origin_id: id of the origin city
     *@return: an arraylist of flights that are available between the two cities.
     *@throws SQLException: if a database access error occurs.
     */

    public ArrayList<String> availableFlights(int Destiny_id, int Origin_id) throws SQLException{
        ArrayList<Flight> flights = flightDAO.getByDestinationAndOriginCity(Destiny_id,Origin_id);
        return avilableFlightsToString(flights);
    }

    /**
     * Function to return all the codes of the available flights.
     * @param flights : ArrayList<Flight> with the flights to be checked.
     * @return ArrayList<String> with the codes of the available flights.
     * @throws SQLException : if a database access error occurs.
     * @throws IllegalArgumentException : if there are no flights to be checked.
     */
    private ArrayList<String> avilableFlightsToString(ArrayList<Flight> flights) throws SQLException{
        try {
            ArrayList<String> availableFlights = new ArrayList<>();
            for (Flight flight : flights) {
                availableFlights.add(flight.getCode());
            }
            return availableFlights;
        }
        catch(Exception e){
            throw new IllegalArgumentException("No hay vuelos disponibles");
        }
    }
}
