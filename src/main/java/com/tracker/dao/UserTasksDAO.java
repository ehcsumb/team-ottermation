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

/**
 * //TODO:  add desc
 *
 * @author Eric Holm
 * @since 4/12/26
 */
public class UserTasksDAO {

  private static Connection conn = Database.getConnection();

  //TODO: addTask
  public static boolean addTask(Task task) {
    try {
      PreparedStatement ps = conn.prepareStatement(
          "INSERT INTO tasks (user_id, title, description, due_date, priority, task_type, done) "
              + "VALUES (?,?,?,?,?,?,?)"
      );
      ps.setInt(1, task.getUserId());
      ps.setString(2, task.getTitle());
      ps.setString(3,task.getDescription());
      ps.setString(4, task.getDueDate().toString());
      ps.setString(5,task.getPriority().getText());
      ps.setString(6, task.getTaskType());
      ps.setInt(7, task.isCompleted() ? 1 : 0);
      ps.executeUpdate();

      // insert worked, registration successful
      return true;
    } catch (SQLException e) {
      // unique constraint failed, username already taken
      return false;
    }
  }

  //TODO: getTaskById
  public static Task getTaskById(int taskId) throws SQLException {
    PreparedStatement ps = conn.prepareStatement(
        "SELECT * FROM tasks WHERE id=(?)"
    );
    ps.setInt(1, taskId );

    try {
      ResultSet rs = ps.executeQuery();
      // empty result?
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
      return null;
    }


  }

  // TODO:  updateTask
  public static boolean updateTask(Task task) {
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
      ps.executeUpdate();

      // must've worked!  return true
      return true;
    } catch (SQLException e) {
      // any errors, return false
      return false;
    }
  }

  // TODO:  deleteTask
  public static boolean deleteTask(Task task) {
    return deleteTaskById(task.getId());
  }

  // TODO:  deleteTaskById
  public static boolean deleteTaskById(int taskId) {
    try {
      PreparedStatement ps = conn.prepareStatement(
          "DELETE FROM tasks WHERE id=?"
      );
      ps.setInt(1, taskId); // WHERE id = ?
      ps.executeUpdate();

      // must've worked!  return true
      return true;
    } catch (SQLException e) {
      // any errors, return false
      return false;
    }
  }

  public static ArrayList<Task> getAllTasks(User user) throws SQLException {
    ArrayList<Task> tasks = new ArrayList<>();
    PreparedStatement ps = conn.prepareStatement(
        "SELECT * FROM tasks WHERE user_id=(?) ORDER BY user_id ASC"
    );
    ps.setString(1, String.valueOf(user.getId()) );


    try {
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
      // TODO: probably a better way to handle this
      return null;
    }

    return tasks;
  }
}
