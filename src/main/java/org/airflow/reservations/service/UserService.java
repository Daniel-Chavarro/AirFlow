package org.airflow.reservations.service;

import org.airflow.reservations.DAO.UsersDAO;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserService {
    private static final Logger logger = Logger.getLogger(UserService.class.getName());
    private final UsersDAO usersDAO;
    public UserService(UsersDAO usersDAO) {
        this.usersDAO = usersDAO;
    }

    public void validateRegistration(RegistrationRequest req) throws ValidationException {
        if (!req.getPassword().equals(req.getPasswordConfirm())) {
            logger.log(Level.WARNING, "Password mismatch: {0} vs {1}", new Object[]{req.getPassword(), req.getPasswordConfirm()});
            throw new ValidationException("Las contraseñas no coinciden");
        }
        if (!req.getPassword().matches("^(?=.*[A-Z])(?=.*\\d).{8,}$")) {
            logger.log(Level.WARNING, "Password policy violation for {0}", req.getEmail());
            throw new ValidationException("La contraseña debe tener al menos 8 caracteres, una mayúscula y un número");
        }
    }
}
