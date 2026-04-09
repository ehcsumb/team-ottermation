package com.tracker;

import com.tracker.dao.SettingsDAO;
import com.tracker.dao.SQLiteSettingsDAO;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Represents the unit tests for SQLiteSettingsDAO.
 * This class contains test cases to verify the correct functionality
 * of SQLiteSettingsDAO's methods for retrieving and
 * updating the default priority setting in the database.
 *
 * @author David Renteria
 * @version 0.2.0
 * @since 4/8/2026
 */

class SQLiteSettingsDAOTest {

    private static SettingsDAO settingsDAO;
    private static String originalPriority;

    @BeforeAll
    static void setUp() throws SQLException {
        Database.connect();
        settingsDAO = new SQLiteSettingsDAO();
        originalPriority = settingsDAO.getDefaultPriority();
    }

    @AfterAll
    static void tearDown() throws SQLException {
        settingsDAO.setDefaultPriority(originalPriority);
        Database.close();
    }

    @Test
    void testGetDefaultPriority() throws SQLException {
        String priority = settingsDAO.getDefaultPriority();

        assertNotNull(priority);
        assertEquals(readPriorityFromDatabase(), priority);
    }

    @Test
    void testSetDefaultPriority() throws SQLException {
        settingsDAO.setDefaultPriority("High");

        String updatedPriority = settingsDAO.getDefaultPriority();

        assertEquals("High", updatedPriority);
        assertEquals("High", readPriorityFromDatabase());
    }

    private static String readPriorityFromDatabase() throws SQLException {
        String sql = "SELECT default_priority FROM settings WHERE id = 1";
        Connection conn = Database.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getString("default_priority");
            }
        }

        return null;
    }
}

