package org.airflow.reservations.service;

import java.sql.SQLException;
import org.airflow.reservations.DAO.PasswordResetTokenDAO;
import org.airflow.reservations.DAO.UsersDAO;
import org.airflow.reservations.model.User;
import org.airflow.reservations.utils.PasswordUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {
    private UsersDAO usersDAO;
    private SessionService sessionService;
    private UserService userService;
    private PasswordResetTokenDAO tokenDAO;
    private EmailService emailService;
    private AuthService auth;

    @BeforeEach
    void setup() throws SQLException {
        // Stub de UsersDAO
        usersDAO = new UsersDAO() {
            @Override
            public User getByEmail(String email) {
                if ("me@example.com".equals(email)) {
                    User u = new User();
                    u.setPassword(PasswordUtils.hashPassword("Password1"));
                    return u;
                }
                return null;
            }
            @Override
            public User getByUsername(String username) {
                return null;
            }
            @Override
            public void create(User user) {
                // no-op
            }
        };
        // Sesión in-memory real
        sessionService = new InMemorySessionService();
        userService = new UserService(usersDAO);
        auth = new AuthService(usersDAO, sessionService, userService, tokenDAO, emailService);
    }

    @Test
    void loginSuccess() {
        String token = auth.login("me@example.com", "Password1");
        assertNotNull(token);
        assertNotNull(sessionService.getUserBySession(token));
    }

    @Test
    void loginFailBadCreds() {
        assertThrows(AuthenticationException.class,
            () -> auth.login("foo", "bar"));
    }

    @Test
    void registerSuccess() {
        RegistrationRequest req = new RegistrationRequest();
        req.setName("user"); req.setLastName("last"); req.setEmail("a@b.com");
        req.setPassword("Abcd1234"); req.setPasswordConfirm("Abcd1234");
        User u = auth.register(req);
        assertNotNull(u);
        assertEquals("a@b.com", u.getEmail());
    }

    @Test
    void registerFailPasswordMismatch() {
        RegistrationRequest req = new RegistrationRequest();
        req.setPassword("A1"); req.setPasswordConfirm("B2");
        assertThrows(ValidationException.class,
            () -> auth.register(req));
    }
}
