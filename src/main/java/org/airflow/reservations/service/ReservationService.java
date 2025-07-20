package org.airflow.reservations.service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationService {
    private final String jdbcUrl = "jdbc:mysql://localhost:3306/airflow";
    private final String username = "root";
    private final String password = "root";

    // Obtener usuarios en lista de espera
    public List<Integer> getWaitlistUserIds(int waitlistStatus) throws SQLException {
        String sql = "SELECT user_FK FROM reservations WHERE status_FK = ? ORDER BY reserved_at ASC";
        List<Integer> userIds = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, waitlistStatus);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                userIds.add(rs.getInt("user_FK"));
            }
        }
        return userIds;
    }

    // Asignar asiento al primer usuario en lista de espera
    public void assignSeat(int confirmedStatus) throws SQLException {
        // Busca el primer usuario en lista de espera
        String selectSql = "SELECT id_PK FROM reservations WHERE status_FK = 1 ORDER BY reserved_at ASC LIMIT 1";
        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                int reservationId = rs.getInt("id_PK");
                // Actualiza el estado de la reserva a "confirmed"
                String updateSql = "UPDATE reservations SET status_FK = ? WHERE id_PK = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, confirmedStatus);
                    updateStmt.setInt(2, reservationId);
                    updateStmt.executeUpdate();
                    System.out.println("Reserva confirmada para id: " + reservationId);
                }
            } else {
                System.out.println("No hay usuarios en la lista de espera.");
            }
        }
    }
}