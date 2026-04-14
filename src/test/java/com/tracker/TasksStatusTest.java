package com.tracker;

import com.tracker.dao.TasksDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.*;

class TasksStatusTest {

    @BeforeEach
    void setUp() {
        Database.connect();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testTaskMarkedCompleted() throws Exception {
        // Create and insert a task.
        Task task = new Task();
        // Set it to first user.
        task.setUserId(1);
        // Add task.
        TasksDAO.addTask(task);

        // Set up a query.
        PreparedStatement ps = Database.getConnection().prepareStatement(
                // Get the last inserted id from database is used in case there seems to be an issue with addTask.
                "SELECT id FROM tasks ORDER BY id DESC LIMIT 1"
        );
        // Call the query and get the output back.
        ResultSet rs = ps.executeQuery();
        // Start at row 1.
        rs.next();
        // Fetch id
        int id = rs.getInt("id");
        task.setId(id);

        // Mark task complete and push to database.
        task.setToCompleted();
        TasksDAO.updateTask(task);

        // Fetch back and check
        Task fetched = TasksDAO.getTaskById(id);
        assertTrue(fetched.isCompleted());
    }
}