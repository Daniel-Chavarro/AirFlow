package org.airflow.reservations.service;

import org.airflow.reservations.model.User;

public interface SessionService {
    String createSession(User user);
    void invalidate(String sessionId);
    User getUserBySession(String sessionId);
}