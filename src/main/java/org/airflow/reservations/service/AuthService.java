package org.airflow.reservations.service;

import java.sql.SQLException;
import org.airflow.reservations.DAO.PasswordResetTokenDAO;
import org.airflow.reservations.DAO.UsersDAO;
import org.airflow.reservations.model.PasswordResetToken;
import org.airflow.reservations.model.User;
import org.airflow.reservations.utils.PasswordUtils;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthService implements IAuthService {
    private static final Logger logger = Logger.getLogger(AuthService.class.getName());
    private final UsersDAO usersDAO;
    private final SessionService sessionService;
    private final UserService userService;
    private final PasswordResetTokenDAO tokenDAO;
    private final EmailService emailService;

    public AuthService(UsersDAO usersDAO, SessionService sessionService, UserService userService, PasswordResetTokenDAO tokenDAO,EmailService emailService) {
        this.usersDAO = usersDAO;
        this.sessionService = sessionService;
        this.userService = userService;
        this.tokenDAO = tokenDAO;
        this.emailService = emailService;
    }

    /**
     *
     * @param email
     */
    @Override
    public void initiatePasswordRecovery(String email) {
        logger.log(Level.FINE, "Iniciando recuperación de contraseña para {0}", email);
        User user = null;
        try {
            user = usersDAO.getByEmail(email);
        } catch (SQLException ex) {
            Logger.getLogger(AuthService.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (user == null) {
            throw new NotFoundException("Usuario no encontrado para email: " + email);
        }
        String token = UUID.randomUUID().toString();
        PasswordResetToken prt = new PasswordResetToken();
        prt.setToken(token);
        prt.setUserId(user.getId());
        prt.setExpiresAt(LocalDateTime.now().plusHours(1));
        tokenDAO.save(prt);
        // Enviar correo con enlace
        String link = "https://mi-app/reset-password?token=" + token;
        emailService.send(email, "Recuperación de contraseña", "Para resetear tu contraseña haz clic: " + link);
        logger.log(Level.INFO, "Token de recuperación enviado a {0}", email);
    }

    @Override
    public void resetPassword(String token, String newPassword, String newPasswordConfirm) {
        logger.log(Level.FINE, "Reset de contraseña con token {0}", token);
        PasswordResetToken prt = tokenDAO.findByToken(token);
        if (prt == null || prt.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Token inválido o expirado");
        }
        if (!newPassword.equals(newPasswordConfirm)) {
            throw new ValidationException("Las contraseñas no coinciden");
        }
        if (!newPassword.matches("^(?=.*[A-Z])(?=.*\\d).{8,}$")) {
            throw new ValidationException("La contraseña debe tener al menos 8 caracteres, una mayúscula y un número");
        }
        User user = null;
        try {
            user = usersDAO.getById(prt.getUserId());
        } catch (SQLException ex) {
            Logger.getLogger(AuthService.class.getName()).log(Level.SEVERE, null, ex);
        }
        user.setPassword(PasswordUtils.hashPassword(newPassword));
        try {
            usersDAO.update(user);
        } catch (SQLException ex) {
            Logger.getLogger(AuthService.class.getName()).log(Level.SEVERE, null, ex);
        }
        tokenDAO.delete(token);
        logger.log(Level.INFO, "Contraseña reseteada para usuario id={0}", prt.getUserId());
    }

    @Override
    public String login(String usernameOrEmail, String password) throws AuthenticationException {
        logger.log(Level.FINE, "Intento de login para {0}", usernameOrEmail);
        User user;
        try {
            user = usersDAO.getByEmail(usernameOrEmail);
        } catch (Exception e) {
            throw new AuthenticationException("Error al acceder a datos de usuario: " + e.getMessage());
        }
        if (user == null) {
            try {
                user = usersDAO.getByUsername(usernameOrEmail);
            } catch (SQLException ex) {
                Logger.getLogger(AuthService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (user == null || !PasswordUtils.checkPassword(password, user.getPassword())) {
            logger.log(Level.WARNING, "Credenciales inválidas para {0}", usernameOrEmail);
            throw new AuthenticationException("Credenciales inválidas");
        }
        String token = sessionService.createSession(user);
        logger.log(Level.INFO, "Login exitoso para {0}", usernameOrEmail);
        return token;
    }

    @Override
    public User register(RegistrationRequest request) throws ValidationException {
        logger.log(Level.FINE, "Registrando usuario {0}", request.getEmail());
        userService.validateRegistration(request);
        String hash = PasswordUtils.hashPassword(request.getPassword());
        User u = new User();
        u.setName(request.getName());
        u.setLast_name(request.getLastName());
        u.setEmail(request.getEmail());
        u.setPassword(hash);
        u.setSuperUser(false);
        try {
            usersDAO.create(u);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al crear usuario {0}: {1}", new Object[]{request.getEmail(), e.getMessage()});
            throw new ValidationException("No se pudo completar el registro");
        }
        logger.log(Level.INFO, "Registro exitoso de usuario {0}", request.getEmail());
        return u;
    }

    @Override
    public void logout(String sessionId) {
        logger.log(Level.FINE, "Logout sessionId={0}", sessionId);
        sessionService.invalidate(sessionId);
    }
}
