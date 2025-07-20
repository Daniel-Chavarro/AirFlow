package org.airflow.reservations.service;

import org.airflow.reservations.DAO.PriorityDAO;
import org.airflow.reservations.model.Priority;

import java.sql.SQLException;

public class PriorityService {

    private final PriorityDAO priorityDAO;

    public PriorityService(PriorityDAO priorityDAO) {
        this.priorityDAO = priorityDAO;
    }

    // Asignar prioridad a usuario
    public void assignPriority(int userId, Priority priority) throws SQLException {
        priorityDAO.assignPriorityToUser(userId, priority);
    }

    // Obtener prioridad de usuario
    public Priority getUserPriority(int userId) throws SQLException {
        return priorityDAO.getPriorityByUserId(userId);
    }

    // Algoritmo de decisión basado en prioridad (ejemplo: ¿entra primero a la lista de espera?)
    public int comparePriority(int userAId, int userBId) throws SQLException {
        Priority priorityA = getUserPriority(userAId);
        Priority priorityB = getUserPriority(userBId);
        // PREMIUM > EMERGENCIA > REGULAR
        return Integer.compare(priorityOrder(priorityA), priorityOrder(priorityB));
    }

    private int priorityOrder(Priority p) {
        switch (p) {
            case PREMIUM: return 2;
            case EMERGENCIA: return 3;
            case REGULAR: return 1;
            default: return 0;
        }
    }
}