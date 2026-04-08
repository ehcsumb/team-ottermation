package com.tracker.dao;

import java.sql.SQLException;

/**
 * Represents database operations for application settings
 * This interface defines methods for retrieving and updating application settings,
 * such as default task priority.
 *
 * @author David Renteria
 * @version 0.2.0
 * @since 4/8/2026
 */

public interface SettingsDAO {

    /**
     * Retrieves the default task priority from the database.
     *
     * @return the default priority value
     * @throws SQLException if a database access error occurs
     */
    String getDefaultPriority() throws SQLException;

    /**
     * Updates the default task priority in the database.
     *
     * @param priority the new default priority value
     * @throws SQLException if a database access error occurs
     */
    void setDefaultPriority(String priority) throws SQLException;
}

