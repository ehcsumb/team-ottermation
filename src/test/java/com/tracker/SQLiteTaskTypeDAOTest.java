package com.tracker;

import com.tracker.dao.SQLiteTaskTypeDAO;
import com.tracker.dao.TaskTypeDAO;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Represents unit test for SQLiteTaskTypeDAO class,
 * which handles all database operations related to task types.
 *
 * @author David Renteria
 * @version 0.1.0
 * @since 4/8/2026
 */

class SQLiteTaskTypeDAOTest {

    private static TaskTypeDAO taskTypeDAO;
    private static final String TEST_NAME = "Test task type";
    private static final String UPDATED_NAME = "Test updated task type";

    @BeforeAll
    static void setUp() {
        Database.connect();
        taskTypeDAO = new SQLiteTaskTypeDAO();
    }

    @AfterEach
    void cleanUpAfterEach() throws SQLException {
        deleteIfExists(TEST_NAME);
        deleteIfExists(UPDATED_NAME);
    }

    @AfterAll
    static void tearDown() {
        Database.close();
    }

    @Test
    void testAddTaskType() throws SQLException {
        taskTypeDAO.addTaskType(TEST_NAME);

        assertTrue(taskTypeDAO.existsByName(TEST_NAME));
    }

    @Test
    void testGetAllTaskTypes() throws SQLException {
        taskTypeDAO.addTaskType(TEST_NAME);

        List<TaskType> taskTypes = taskTypeDAO.getAllTaskTypes();

        assertNotNull(taskTypes);
        assertTrue(taskTypes.stream().anyMatch(taskType -> taskType.getName().equals(TEST_NAME)));
    }

    @Test
    void testUpdateTaskType() throws SQLException {
        taskTypeDAO.addTaskType(TEST_NAME);

        TaskType taskType = findByName(TEST_NAME);
        assertNotNull(taskType);

        taskTypeDAO.updateTaskType(taskType.getId(), UPDATED_NAME);

        assertFalse(taskTypeDAO.existsByName(TEST_NAME));
        assertTrue(taskTypeDAO.existsByName(UPDATED_NAME));
    }

    @Test
    void testDeleteTaskType() throws SQLException {
        taskTypeDAO.addTaskType(TEST_NAME);

        TaskType taskType = findByName(TEST_NAME);
        assertNotNull(taskType);

        taskTypeDAO.deleteTaskType(taskType.getId());

        assertFalse(taskTypeDAO.existsByName(TEST_NAME));
    }

    @Test
    void testExistsByNameReturnsFalseWhenMissing() throws SQLException {
        assertFalse(taskTypeDAO.existsByName("DoesNotExistType"));
    }

    private TaskType findByName(String name) throws SQLException {
        String sql = "SELECT id, name FROM task_types WHERE name = ?";
        Connection conn = Database.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new TaskType(
                            rs.getInt("id"),
                            rs.getString("name")
                    );
                }
            }
        }

        return null;
    }

    private void deleteIfExists(String name) throws SQLException {
        String sql = "DELETE FROM task_types WHERE name = ?";
        Connection conn = Database.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.executeUpdate();
        }
    }
}