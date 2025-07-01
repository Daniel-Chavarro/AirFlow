package org.airflow.reservations.utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class for handling password hashing and verification using BCrypt.
 * This class provides methods to hash a plain text password and to check if
 * a plain text password matches a hashed password.
 */
public class PasswordUtils {

    /**
     * Hashes a plain text password using BCrypt.
     * This method generates a salt and hashes the provided password.
     *
     * @param plainTextPassword the plain text password to be hashed
     * @return the hashed password
     */
    public static String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    /**
     * Checks if a plain text password matches a hashed password.
     * This method uses BCrypt to verify the provided plain text password
     * against the stored hashed password.
     *
     * @param plainTextPassword the plain text password to be checked
     * @param hashedPassword    the hashed password to check against
     * @return true if the passwords match, false otherwise
     */
    public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }
}
