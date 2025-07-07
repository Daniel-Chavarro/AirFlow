package org.airflow.reservations.service;

import org.airflow.reservations.model.User;

public interface IAuthService {
    /**
     * Auths the user via name or email and password.
     * @param usernameOrEmail String with already implemented logic the user enters either the username or the email.
     * @param password String with respective passowrd for the username or email.
     * @return UUID session token.
     * @throws AuthenticationException in case of database access error occurs.
     */
    String login(String usernameOrEmail, String password) throws AuthenticationException;

    /**
     * Registers a new user.
     * @return created user.
     */
    User register(RegistrationRequest request) throws ValidationException;

    /**
     * Closes active session by id.
     */
    void logout(String sessionId);
    /**
     * Starts password recovery process sending a token via email.
     */
    void initiatePasswordRecovery(String email) throws NotFoundException;

    /**
     * Resets password using recovery token.
     */
    void resetPassword(String token, String newPassword, String newPasswordConfirm) throws ValidationException, InvalidTokenException;
}



