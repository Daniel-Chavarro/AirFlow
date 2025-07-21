package org.airflow.reservations.service;

import org.airflow.reservations.DAO.AirplaneDAO;
import org.airflow.reservations.model.Airplane;

import java.sql.SQLException;

public class AirplaneService {
    private final AirplaneDAO airplaneDAO;

    public AirplaneService() throws SQLException {
        this.airplaneDAO = new AirplaneDAO();
    }

    public Airplane getAirplaneById(int id) throws SQLException {
        return airplaneDAO.getById(id);
    }
}
