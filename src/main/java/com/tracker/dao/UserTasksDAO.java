package com.tracker.dao;

import com.tracker.Database;
import com.tracker.Task;
import com.tracker.TaskPriority;
import com.tracker.TaskType;
import com.tracker.User;
import com.tracker.UserTask;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * //TODO:  add desc
 *
 * @author Eric Holm
 * @since 4/12/26
 */
public class UserTasksDAO {

  private static Connection conn = Database.getConnection();

  //TODO: addTask
  public static boolean addTask(UserTask task) {
    try {
      PreparedStatement ps = conn.prepareStatement(
          "INSERT INTO tasks (user_id, title, description, due_date, priority, task_type, done) "
              + "VALUES (?,?,?,?,?,?,?)"
      );
      ps.setInt(1, task.getUser().getId());
      ps.setString(2, task.getTitle());
      ps.setString(3,task.getDescription());
      ps.setString(4, task.getDueDate().toString());
      ps.setString(5,task.getPriority().getText());
      ps.setString(6, task.getTaskType().getName());
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
  public static UserTask getTaskById(int taskId) throws SQLException {
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
          rs.getInt("done") ? true : false
      );

      return new UserTask(
          ,
          task,
          rs.getInt("id")
      );

    } catch (SQLException e) {
      return null;
    }


  }

  // TODO:  updateTask
  public static boolean updateTask(UserTask task) {

  }

  // TODO:  deleteTask
  public static boolean deleteTask(UserTask task) {

  }

  // TODO:  deleteTaskById
  public static boolean deleteTaskById(int taskId) {

  }

  public static ArrayList<UserTask> getAllTasks(User user) throws SQLException {
    ArrayList<UserTask> userTasks = new ArrayList<>();
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
            rs.getInt("done") == 1 ? true : false
        );
        UserTask userTask = new UserTask(
            user,
            task,
            rs.getInt("id")
        );

        userTasks.add(userTask);
      }
    } catch (SQLException e) {
      // TODO: probably a better way to handle this
      return null;
    }

    return userTasks;
  }
}
