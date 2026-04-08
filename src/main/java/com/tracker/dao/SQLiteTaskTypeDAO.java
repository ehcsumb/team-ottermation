package com.tracker.dao;

import com.tracker.Database;
import com.tracker.TaskType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * SQLite implementation of the TaskTypeDAO interface.
 * Handles all task type persistence operations using the
 * application's SQLite database connection.
 *
 * @author David Renteria
 * @version 0.2.0
 * @since 4/7/2026
 */
public class SQLiteTaskTypeDAO implements TaskTypeDAO {

    /**
     * Retrieves all task types from the database.
     *
     * @return a list of all task types
     * @throws SQLException if a database access error occurs
     */
    @Override
    public List<TaskType> getAllTaskTypes() throws SQLException {
        List<TaskType> taskTypes = new ArrayList<>();
        String sql = "SELECT id, name FROM task_types ORDER BY name ASC";

        Connection conn = Database.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                taskTypes.add(new TaskType(
                        rs.getInt("id"),
                        rs.getString("name")
                ));
            }
        }

        return taskTypes;
    }

    /**
     * Inserts a new task type into the database.
     *
     * @param name the name of the task type to add
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void addTaskType(String name) throws SQLException {
        String sql = "INSERT INTO task_types (name) VALUES (?)";

        Connection conn = Database.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.executeUpdate();
        }
    }

    /**
     * Updates the name of an existing task type.
     *
     * @param id the ID of the task type to update
     * @param name the new task type name
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void updateTaskType(int id, String name) throws SQLException {
        String sql = "UPDATE task_types SET name = ? WHERE id = ?";

        Connection conn = Database.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Deletes a task type from the database.
     *
     * @param id the ID of the task type to delete
     * @throws SQLException if a database access error occurs
     */
    @Override
    public void deleteTaskType(int id) throws SQLException {
        String sql = "DELETE FROM task_types WHERE id = ?";

        Connection conn = Database.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Checks whether a task type with the given name exists.
     *
     * @param name the task type name to check
     * @return true if the task type exists, false otherwise
     * @throws SQLException if a database access error occurs
     */
    @Override
    public boolean existsByName(String name) throws SQLException {
        String sql = "SELECT COUNT(*) FROM task_types WHERE LOWER(name) = LOWER(?)";

        Connection conn = Database.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
}