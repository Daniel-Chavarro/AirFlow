package org.airflow.reservations.service;

import org.airflow.reservations.DAO.UsersDAO;
import org.airflow.reservations.DAO.PriorityDAO;
import org.airflow.reservations.model.User;
import org.airflow.reservations.model.Priority;

import java.sql.SQLException;
import java.util.Optional;

public class UserService {
    private final UsersDAO usersDAO;
    private final PriorityDAO priorityDAO;

    public UserService(UsersDAO usersDAO, PriorityDAO priorityDAO) {
        this.usersDAO = usersDAO;
        this.priorityDAO = priorityDAO;
    }


    // Modificar la prioridad de un usuario existente
    public boolean updateUserPriority(int userId, Priority newPriority) throws SQLException {
        Optional<User> userOpt = usersDAO.getUserById(userId);
        if (!userOpt.isPresent()) {
            return false;
        }
        priorityDAO.assignPriorityToUser(userId, newPriority);
        return true;
    }

    // Obtener prioridad del usuario
    public Priority getUserPriority(int userId) throws SQLException {
        return priorityDAO.getPriorityByUserId(userId);
    }

    // Ejemplo de lógica: obtener usuario con mayor prioridad entre dos
    public Optional<User> getUserWithHigherPriority(int userAId, int userBId) throws SQLException {
        Priority priorityA = priorityDAO.getPriorityByUserId(userAId);
        Priority priorityB = priorityDAO.getPriorityByUserId(userBId);
        if (priorityOrder(priorityA) > priorityOrder(priorityB)) {
            return usersDAO.getUserById(userAId);
        } else if (priorityOrder(priorityB) > priorityOrder(priorityA)) {
            return usersDAO.getUserById(userBId);
        }
        return Optional.empty(); // Empate
    }

    private int priorityOrder(Priority p) {
        // Emergencia > PREMIUM > REGULAR
        switch (p) {
            case EMERGENCIA: return 3;
            case PREMIUM: return 2;
            case REGULAR: return 1;
            default: return 0;
        }
    }
}