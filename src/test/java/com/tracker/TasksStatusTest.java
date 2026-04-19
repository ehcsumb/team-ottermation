package com.tracker;

import com.tracker.dao.TasksDAO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.*;

class TasksStatusTest {

    @BeforeAll
    static void setup() {
        Database.connect();
    }

    @Test
    void testTaskMarkedCompleted() throws Exception {
        //Create and insert a task.
        Task task = new Task();
        //Set it to first user.
        task.setUserId(1);
        //Add task.
        TasksDAO.addTask(task);

        //Set up a query.
        PreparedStatement ps = Database.getConnection().prepareStatement(
                // Get the last inserted id from database. Sort by the highest id.
                "SELECT id FROM tasks ORDER BY id DESC LIMIT 1"
        );

        //Call the query and get the output back.
        ResultSet rs = ps.executeQuery();
        //Start at row top row.
        rs.next();
        //Fetch id
        int id = rs.getInt("id");
        //Set id
        task.setId(id);

        //Mark task complete and push to database.
        task.setToCompleted();
        TasksDAO.updateTask(task);

        //Fetch back and check
        Task fetched = TasksDAO.getTaskById(id);
        assertTrue(fetched.isCompleted());
    }

    @Test
    void testReopenTask() throws Exception {
        Task task = new Task();
        task.setUserId(1);
        TasksDAO.addTask(task);

        PreparedStatement ps = Database.getConnection().prepareStatement(
                "SELECT id FROM tasks ORDER BY id DESC LIMIT 1"
        );
        ResultSet rs = ps.executeQuery();
        rs.next();
        int id = rs.getInt("id");
        task.setId(id);

        task.setToCompleted();
        TasksDAO.updateTask(task);
        //Uncheck complete box.
        task.setToIncomplete();
        TasksDAO.updateTask(task);
        //Check if it's still complete in the database.
        Task fetched = TasksDAO.getTaskById(id);
        assertFalse(fetched.isCompleted());
    }

    @Test
    void testSaveUpdatesTitle() throws Exception {
        Task task = new Task();
        task.setUserId(1);
        TasksDAO.addTask(task);

        PreparedStatement ps = Database.getConnection().prepareStatement(
                "SELECT id FROM tasks ORDER BY id DESC LIMIT 1"
        );
        ResultSet rs = ps.executeQuery();
        rs.next();
        int id = rs.getInt("id");
        task.setId(id);

        //Set title
        task.setTitle("New Title");
        TasksDAO.updateTask(task);

        //Fetch title from database.
        Task fetched = TasksDAO.getTaskById(id);
        assertEquals("New Title", fetched.getTitle());
    }

}