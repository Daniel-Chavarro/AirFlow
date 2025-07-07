package org.airflow.reservations.DAO;

import org.airflow.reservations.model.PasswordResetToken;

public interface PasswordResetTokenDAO {
    void save(PasswordResetToken token);
    PasswordResetToken findByToken(String token);
    void delete(String token);
}