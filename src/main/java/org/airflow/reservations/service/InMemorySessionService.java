package org.airflow.reservations.service;

import org.airflow.reservations.model.User;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.logging.Level;

public class InMemorySessionService implements SessionService {
    private static final Logger logger = Logger.getLogger(InMemorySessionService.class.getName());
    private final ConcurrentHashMap<String, User> sessions = new ConcurrentHashMap<>();

    @Override
    public String createSession(User user) {
        String token = UUID.randomUUID().toString();
        sessions.put(token, user);
        logger.log(Level.INFO, "Sesión creada para usuario {0}: token={1}", new Object[]{user.getEmail(), token});
        return token;
    }

    @Override
    public void invalidate(String sessionId) {
        User removed = sessions.remove(sessionId);
        if (removed != null) {
            logger.log(Level.INFO, "Sesión invalidada: token={0}", sessionId);
        } else {
            logger.log(Level.WARNING, "Intento de invalidar sesión desconocida: token={0}", sessionId);
        }
    }

    @Override
    public User getUserBySession(String sessionId) {
        return sessions.get(sessionId);
    }
}
