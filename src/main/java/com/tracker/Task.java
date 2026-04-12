package com.tracker;

import com.tracker.dao.SQLiteTaskTypeDAO;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Contains all the parts of a task independent of a user
 *
 * @author Eric Holm
 * @since 4/12/26
 */
public class Task {

  private String title;
  private String description;
  private LocalDate dueDate;
  private TaskPriority priority;
  private String taskType;
  private boolean completed = false;
//  protected Integer id;

  public Task() {
    setTitle(null);
    description = "";
    setDueDate(null);
    setPriority(TaskPriority.MEDIUM);
    taskType = null;
//    id = null;
  }

  public Task(
      String title,
      String description,
      LocalDate dueDate,
      TaskPriority priority,
      String taskType,
      boolean completed
  ) {

  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    if (title == null || title.isEmpty()) {
      title = "New title";
    }
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public LocalDate getDueDate() {
    return dueDate;
  }

  public void setDueDate(LocalDate dueDate) {
    if (dueDate == null) {
      this.dueDate = LocalDate.now().plusWeeks(1);
    }
    this.dueDate = dueDate;
  }

  public TaskPriority getPriority() {
    return priority;
  }

  public void setPriority(TaskPriority priority) {
    if (priority == null) {
      priority = TaskPriority.MEDIUM;
    }
    this.priority = priority;
  }

  public TaskType getTaskType() {
    return taskType;
  }

  public void setTaskType(TaskType taskType) throws SQLException {
    if (taskType == null) {
      SQLiteTaskTypeDAO dao = new SQLiteTaskTypeDAO();
      List<TaskType> taskTypes = dao.getAllTaskTypes();
      // if no task types are defined, assign null to task type
      if (taskTypes.isEmpty()) {
        this.taskType = null;
        return;
      }
      // else set to the first task type
      this.taskType = taskTypes.get(0);
      return;
    }
    this.taskType = taskType;
  }

  public boolean isCompleted() {
    return completed;
  }

  public void setToCompleted() {
    this.completed = true;
  }

  public void setToIncomplete() {
    this.completed = false;
  }
}
