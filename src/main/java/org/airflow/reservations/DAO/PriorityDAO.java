package org.airflow.reservations.DAO;

import org.airflow.reservations.model.Priority;

import java.sql.*;

public class PriorityDAO {

    private final Connection connection;

    public PriorityDAO(Connection connection) {
        this.connection = connection;
    }

    public void assignPriorityToUser(int userId, Priority priority) throws SQLException {
        String sql = "UPDATE users SET priority = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, priority.name());
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }

    public Priority getPriorityByUserId(int userId) throws SQLException {
        String sql = "SELECT priority FROM users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Priority.fromString(rs.getString("priority"));
            }
        }
        return Priority.REGULAR; // Default
    }
}