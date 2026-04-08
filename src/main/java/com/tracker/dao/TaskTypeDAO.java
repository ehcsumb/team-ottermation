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
 * Represents the Task type data access object and
 * how it handles all database operations related to task types
 *
 * @author David Renteria
 * @version 0.2.0
 * @since 4/7/2026
 */
public class TaskTypeDAO {

    /**
     * Retrieves all task types from the database, ordered alphabetically by name.
     * @return taskTypes
     * @throws SQLException if a database access error occurs during the query
     */
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
     * Insert new task into the database
     *
     * @param name the task type name
     * @throws SQLException if the insert operation fails
     */
    public void addTaskType(String name) throws SQLException {
        String sql = "INSERT INTO task_types (name) VALUES (?)";

        Connection conn = Database.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.executeUpdate();
        }
    }

    /**
     * Updates the name of an existing task type in the database based on its ID.
     *
     * @param id unique identifier of the task type to be updated
     * @param name new name for the task type
     * @throws SQLException if a database access error occurs or update fails
     */
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
     * Deletes a task type from the database based on its ID.
     *
     * @param id unique identifier of the task type to delete
     * @throws SQLException if a database access error occurs or the deletion fails
     */
    public void deleteTaskType(int id) throws SQLException {
        String sql = "DELETE FROM task_types WHERE id = ?";

        Connection conn = Database.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Checks if a task type with a specified name exists
     * Conducts a case-insensitive search in database to confirm
     *
     * @param name name of the task type to be checked
     * @return true if a matching task type exists, false otherwise
     * @throws SQLException if a database access error occurs during the query
     */
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