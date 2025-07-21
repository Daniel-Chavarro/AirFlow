package org.airflow.reservations.service;

import org.airflow.reservations.DAO.CityDAO;
import org.airflow.reservations.model.City;

import java.sql.SQLException;
import java.util.ArrayList;

public class CityService {
    private final CityDAO cityDAO;

    public CityService() throws SQLException {
        this.cityDAO = new CityDAO();
    }

    public City getCityByName(String name) throws SQLException {
        return cityDAO.getByName(name);
    }

    public ArrayList<City> getAllCities() throws SQLException {
        return cityDAO.getAll();
    }

    public City getCityById(int id) throws SQLException {
        return cityDAO.getById(id);
    }
}
