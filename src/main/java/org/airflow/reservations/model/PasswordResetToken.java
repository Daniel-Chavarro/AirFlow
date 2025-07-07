package org.airflow.reservations.model;

import java.time.LocalDateTime;

public class PasswordResetToken {
    private String token;
    private int userId;
    private LocalDateTime expiresAt;
    // getters & setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
}
