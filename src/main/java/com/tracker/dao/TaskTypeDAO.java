package com.tracker.dao;

import com.tracker.TaskType;

import java.sql.SQLException;
import java.util.List;

/**
 * Defines database operations for TaskType entities.
 * Provides the contract for retrieving, creating, updating,
 * deleting, and validating task types.
 *
 * @author David Renteria
 * @version 0.2.0
 * @since 4/7/2026
 */
public interface TaskTypeDAO {

    /**
     * Retrieves all task types from the database.
     *
     * @return a list of all task types
     * @throws SQLException if a database access error occurs
     */
    List<TaskType> getAllTaskTypes() throws SQLException;

    /**
     * Inserts a new task type into the database.
     *
     * @param name the name of the task type to add
     * @throws SQLException if a database access error occurs
     */
    void addTaskType(String name) throws SQLException;

    /**
     * Updates the name of an existing task type.
     *
     * @param id the ID of the task type to update
     * @param name the new task type name
     * @throws SQLException if a database access error occurs
     */
    void updateTaskType(int id, String name) throws SQLException;

    /**
     * Deletes a task type from the database.
     *
     * @param id the ID of the task type to delete
     * @throws SQLException if a database access error occurs
     */
    void deleteTaskType(int id) throws SQLException;

    /**
     * Checks whether a task type with the given name exists.
     *
     * @param name the task type name to check
     * @return true if the task type exists, false otherwise
     * @throws SQLException if a database access error occurs
     */
    boolean existsByName(String name) throws SQLException;
}