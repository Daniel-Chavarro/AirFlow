package org.airflow.reservations.service;

import java.sql.SQLException;
import org.airflow.reservations.DAO.PasswordResetTokenDAO;
import org.airflow.reservations.DAO.UsersDAO;
import org.airflow.reservations.model.PasswordResetToken;
import org.airflow.reservations.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.airflow.reservations.utils.PasswordUtils;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceRecoveryTest {
    private UsersDAO usersDAO;
    private PasswordResetTokenDAO tokenDAO;
    private SessionService sessionService;
    private UserService userService;
    private EmailService emailService;
    private AuthService auth;
    
    // In-memory stub for PasswordResetTokenDAO
    private Map<String, PasswordResetToken> tokenStore;

    @BeforeEach
    void setup() throws SQLException {
        // Stub UsersDAO
        usersDAO = new UsersDAO() {
            private Map<String, User> users = new HashMap<>();
            {
                User u = new User();
                u.setId(1);
                u.setEmail("test@example.com");
                u.setPassword(PasswordUtils.hashPassword("OldPass1"));
                users.put(u.getEmail(), u);
            }
            @Override
            public User getByEmail(String email) {
                return users.get(email);
            }
            @Override
            public User getByUsername(String username) { return null; }
            @Override
            public User getById(int id) {
                return users.values().stream().filter(u -> u.getId() == id).findFirst().orElse(null);
            }
            @Override
            public void create(User user) {}
            @Override
            public void update(User user) { users.put(user.getEmail(), user); }
        };
        // In-memory token DAO
        tokenStore = new HashMap<>();
        tokenDAO = new PasswordResetTokenDAO() {
            @Override
            public void save(PasswordResetToken token) {
                tokenStore.put(token.getToken(), token);
            }
            @Override
            public PasswordResetToken findByToken(String token) {
                return tokenStore.get(token);
            }
            @Override
            public void delete(String token) {
                tokenStore.remove(token);
            }
        };
        // Other dependencies
        sessionService = new InMemorySessionService();
        userService = new UserService(usersDAO);
        emailService = new EmailService() {
            String lastTo, lastSubject, lastBody;
            @Override
            public void send(String to, String subject, String body) {
                this.lastTo = to; this.lastSubject = subject; this.lastBody = body;
            }
        };
        auth = new AuthService(usersDAO, sessionService, userService, tokenDAO, emailService);
    }

    @Test
    void testInitiatePasswordRecovery() {
        auth.initiatePasswordRecovery("test@example.com");
        assertEquals(1, tokenStore.size());
        PasswordResetToken prt = tokenStore.values().iterator().next();
        assertEquals(1, prt.getUserId());
        assertTrue(prt.getExpiresAt().isAfter(LocalDateTime.now()));
    }

    @Test
    void testResetPasswordSuccess() throws SQLException {
        // Setup a token
        PasswordResetToken prt = new PasswordResetToken();
        prt.setToken("tok123");
        prt.setUserId(1);
        prt.setExpiresAt(LocalDateTime.now().plusMinutes(30));
        tokenDAO.save(prt);
        // Reset password
        auth.resetPassword("tok123", "NewPass1", "NewPass1");
        User u = usersDAO.getById(1);
        assertTrue(PasswordUtils.checkPassword("NewPass1", u.getPassword()));
        assertNull(tokenStore.get("tok123"));
    }

    @Test
    void testResetPasswordInvalidToken() {
        assertThrows(InvalidTokenException.class,
            () -> auth.resetPassword("bad", "P1aaaaaa", "P1aaaaaa"));
    }

    @Test
    void testResetPasswordMismatch() {
        PasswordResetToken prt = new PasswordResetToken();
        prt.setToken("tok321"); prt.setUserId(1);
        prt.setExpiresAt(LocalDateTime.now().plusMinutes(30));
        tokenDAO.save(prt);
        assertThrows(ValidationException.class,
            () -> auth.resetPassword("tok321", "NewPass1", "Wrong1"));
    }

    @Test
    void testResetPasswordPolicyFail() {
        PasswordResetToken prt = new PasswordResetToken();
        prt.setToken("tok999"); prt.setUserId(1);
        prt.setExpiresAt(LocalDateTime.now().plusMinutes(30));
        tokenDAO.save(prt);
        assertThrows(ValidationException.class,
            () -> auth.resetPassword("tok999", "short", "short"));
    }
}