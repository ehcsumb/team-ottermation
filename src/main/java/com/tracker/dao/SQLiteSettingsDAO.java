package com.tracker.dao;

import com.tracker.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * SQLite implementation of the SettingsDAO interface.
 * Handles all database interactions related to app settings.
 *
 * @author David Renteria
 * @version 0.2.0
 * @since 4/8/2026
 */

public class SQLiteSettingsDAO implements SettingsDAO {

    /**
     * Retrieves the default task priority from the settings table.
     *
     * @return the saved default priority, or "Medium" if no valid value is found
     * @throws SQLException if a database access error occurs
     */
    @Override
    public String getDefaultPriority() throws SQLException {
        String sql = "SELECT default_priority FROM settings WHERE id = 1";

        Connection conn = Database.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getString("default_priority");
            }

        }

        return "Medium"; // fallback
    }

    /**
     * Updates the default task priority stored in the settings table.
     *
     * @param priority the new default priority value
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void setDefaultPriority(String priority) throws SQLException {
        String sql = "UPDATE settings SET default_priority = ? WHERE id = 1";

        Connection conn = Database.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, priority);
            ps.executeUpdate();
        }
    }
}
