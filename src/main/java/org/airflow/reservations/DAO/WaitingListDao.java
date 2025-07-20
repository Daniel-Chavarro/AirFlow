package org.airflow.reservations.dao;

import org.airflow.reservations.model.WaitingListEntry;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WaitingListDao {
    private final String jdbcUrl = "jdbc:mysql://localhost:3306/airflow";
    private final String username = "root";
    private final String password = "root";

    public void addEntry(WaitingListEntry entry) throws SQLException {
        String sql = "INSERT INTO waiting_list (user_id, priority, timestamp) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entry.getUserId());
            stmt.setInt(2, entry.getPriority());
            stmt.setLong(3, entry.getTimestamp());
            stmt.executeUpdate();
        }
    }

    public List<WaitingListEntry> getAllEntries() throws SQLException {
        String sql = "SELECT user_id, priority, timestamp FROM waiting_list";
        List<WaitingListEntry> entries = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                entries.add(new WaitingListEntry(
                        rs.getString("user_id"),
                        rs.getInt("priority"),
                        rs.getLong("timestamp")
                ));
            }
        }
        return entries;
    }

    public WaitingListEntry pollNext() throws SQLException {
        String sql = "SELECT user_id, priority, timestamp FROM waiting_list ORDER BY priority DESC, timestamp ASC LIMIT 1";
        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                WaitingListEntry entry = new WaitingListEntry(
                        rs.getString("user_id"),
                        rs.getInt("priority"),
                        rs.getLong("timestamp")
                );
                // Elimina la entrada después de leerla
                removeEntry(entry.getUserId());
                return entry;
            }
        }
        return null;
    }

    public void removeEntry(String userId) throws SQLException {
        String sql = "DELETE FROM waiting_list WHERE user_id = ?";
        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.executeUpdate();
        }
    }

    public boolean userExists(String userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM waiting_list WHERE user_id = ?";
        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
}