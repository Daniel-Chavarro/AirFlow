package org.airflow.reservations.service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WaitingListService {
    private final String jdbcUrl = "jdbc:mysql://localhost:3306/airflow";
    private final String username = "root";
    private final String password = "root";

    // Obtiene todos los usuarios en la lista de espera (por vuelo opcional)
    public List<Integer> getWaitingListUserIds(int waitlistStatus, Integer flightId) throws SQLException {
        String sql = "SELECT user_FK FROM reservations WHERE status_FK = ?"
                + (flightId != null ? " AND flight_FK = ?" : "")
                + " ORDER BY reserved_at ASC";
        List<Integer> userIds = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, waitlistStatus);
            if (flightId != null) {
                stmt.setInt(2, flightId);
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                userIds.add(rs.getInt("user_FK"));
            }
        }
        return userIds;
    }

    // Registrar usuario en la lista de espera
    public void registerUserToWaitingList(int userId, int flightId, int waitlistStatus) throws SQLException {
        String sql = "INSERT INTO reservations (user_FK, flight_FK, status_FK, reserved_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, flightId);
            stmt.setInt(3, waitlistStatus);
            stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            stmt.executeUpdate();
        }
    }

    // Asignar siguiente usuario en espera (cambiar estado a confirmado)
    public void assignNextUser(int waitlistStatus, int confirmedStatus, Integer flightId) throws SQLException {
        String selectSql = "SELECT id_PK FROM reservations WHERE status_FK = ?"
                + (flightId != null ? " AND flight_FK = ?" : "")
                + " ORDER BY reserved_at ASC LIMIT 1";
        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
            selectStmt.setInt(1, waitlistStatus);
            if (flightId != null) {
                selectStmt.setInt(2, flightId);
            }
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                int reservationId = rs.getInt("id_PK");
                String updateSql = "UPDATE reservations SET status_FK = ? WHERE id_PK = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, confirmedStatus);
                    updateStmt.setInt(2, reservationId);
                    updateStmt.executeUpdate();
                    System.out.println("Usuario asignado (id reserva): " + reservationId);
                }
            } else {
                System.out.println("No hay usuarios en la lista de espera.");
            }
        }
    }
}