package com.tracker.dao;

import com.tracker.Database;
import com.tracker.Task;
import com.tracker.TaskPriority;
import com.tracker.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for CRUD operations related to the Tasks table. Public inputs are generally Task
 * objects.
 *
 * @author Eric Holm
 * @since 4/13/26
 */
public class TasksDAO {

  // last exception from anywhere in this class
  private static Exception lastException;
  // connection to database from Database
  private static final Connection conn = Database.getConnection();

  public static Exception getLastException() {
    return lastException;
  }

  private static void setLastException(Exception e) {
    lastException = e;
  }

  /**
   * Accepts a task object and inserts its data into the database.
   *
   * @param task task to add to db
   * @return - returns result of executeUpdate() as an Integer <br/> - <em>null</em> if there was an
   * error
   */
  public static Integer addTask(Task task) {
    try {
      PreparedStatement ps = conn.prepareStatement(
          "INSERT INTO tasks"
              + "(user_id, title, description, due_date, priority, task_type, done) "
              + "VALUES (?,?,?,?,?,?,?)"
      );
      ps.setInt(1, task.getUserId());
      ps.setString(2, task.getTitle());
      ps.setString(3, task.getDescription());
      ps.setString(4, task.getDueDate().toString());
      ps.setString(5, task.getPriority().getText());
      ps.setString(6, task.getTaskType());
      ps.setInt(7, task.isCompleted() ? 1 : 0);
      return ps.executeUpdate();

    } catch (SQLException e) {
      // unique constraint failed, username already taken
      setLastException(e);
      return null;
    }
  }

  /**
   * Fetches the record in the table task identified by its id value. Data from database is used to
   * create a new Task object
   *
   * @param taskId taskId to find and fetch from db
   * @return - <em>Task</em> object instantiated with data from db <br/> - <em>null</em> if no
   * object is found or there is an error
   */
  public static Task getTaskById(int taskId) {

    try {
      PreparedStatement ps = conn.prepareStatement(
          "SELECT * FROM tasks WHERE id=(?)"
      );
      ps.setInt(1, taskId);

      ResultSet rs = ps.executeQuery();
      // return null on empty result
      if (!rs.next()) {
        return null;
      }
      // return the results of the first row only (should only be one row)
      Task task = new Task(
          rs.getString("title"),
          rs.getString("description"),
          LocalDate.parse(rs.getString("due_date")),
          TaskPriority.fromText(rs.getString("priority")),
          rs.getString("task_type"),
          rs.getInt("done") == 1 ? true : false,
          rs.getInt("id"),
          rs.getInt("user_id")
      );

      return task;

    } catch (SQLException e) {
      // just return null on errors
      setLastException(e);
      return null;
    }


  }

  public static Integer updateTask(Task task) {
    try {
      PreparedStatement ps = conn.prepareStatement(
          "UPDATE tasks SET user_id = ?, title = ?, description = ?, due_date = ?, priority = ?, task_type = ?, done = ? WHERE id = ?"
      );
      ps.setInt(1, task.getUserId());
      ps.setString(2, task.getTitle());
      ps.setString(3, task.getDescription());
      ps.setString(4, task.getDueDate().toString());
      ps.setString(5, task.getPriority().getText());
      ps.setString(6, task.getTaskType());
      ps.setInt(7, task.isCompleted() ? 1 : 0);
      ps.setInt(8, task.getId()); // WHERE id = ?
      return ps.executeUpdate();

    } catch (SQLException e) {
      // any errors, return false
      setLastException(e);
      return null;
    }
  }

  // deleteTask
  public static Integer deleteTask(Task task) {
    // return result of deleteTaskById
    return deleteTaskById(task.getId());
  }

  // deleteTaskById
  public static Integer deleteTaskById(int taskId) {
    try {
      PreparedStatement ps = conn.prepareStatement(
          "DELETE FROM tasks WHERE id=?"
      );
      ps.setInt(1, taskId); // WHERE id = ?
      return ps.executeUpdate();
    } catch (SQLException e) {
      // any errors, return false
      setLastException(e);
      return null;
    }
  }

  public static ArrayList<Task> getAllTasks(User user) {
    ArrayList<Task> tasks = new ArrayList<>();
    try {
      PreparedStatement ps = conn.prepareStatement(
          "SELECT * FROM tasks WHERE user_id=(?) ORDER BY user_id ASC"
      );
      ps.setString(1, String.valueOf(user.getId()));

      ResultSet rs = ps.executeQuery();

      while (rs.next()) {
        Task task = new Task(
            rs.getString("title"),
            rs.getString("description"),
            LocalDate.parse(rs.getString("due_date")),
            TaskPriority.fromText(rs.getString("priority")),
            rs.getString("task_type"),
            rs.getInt("done") == 1 ? true : false,
            rs.getInt("id"),
            rs.getInt("user_id")
        );

        tasks.add(task);
      }
    } catch (SQLException e) {
      setLastException(e);
      return null;
    }

    return tasks;
  }
}
