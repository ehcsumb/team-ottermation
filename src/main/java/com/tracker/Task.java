package com.tracker;

import java.time.LocalDate;
import java.util.Objects;

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
  private Integer id;
  private Integer userId;

  /**
   * new Task object with null or blank values.
   * TaskPriority is set to MEDIUM
   * Description = ""
   * Everything else is null
   */
  public Task() {
    setTitle(null);
    description = "";
    setDueDate(null);
    setPriority(TaskPriority.MEDIUM);
    taskType = null;
    id = null;
    userId = null;
  }

  /**
   * New Task object with all parameters going to a class field
   * @param title task title
   * @param description task description
   * @param dueDate due date as a LocalDate
   * @param priority priority as an enum TaskPriority
   * @param taskType task type as a string
   * @param completed boolean for completion
   * @param id unique task ID; NOTE: don't set this manually, this should come from db
   * @param userId unique user ID from application or db
   */
  public Task(
      String title,
      String description,
      LocalDate dueDate,
      TaskPriority priority,
      String taskType,
      boolean completed,
      Integer id,
      Integer userId
  ) {
      setTitle(title);
      setDescription(description);
      setDueDate(dueDate);
      setPriority(priority);
      setTaskType(taskType);
      this.completed = completed;
      this.id = id;
      this.userId = userId;
  }

  public Task(Task t) {
      this.setTitle(t.getTitle());
      this.setDescription(t.getDescription());
      this.setDueDate(t.getDueDate());
      this.setPriority(t.getPriority());
      this.setTaskType(t.getTaskType());
      if (t.isCompleted()) {
        this.setToCompleted();
      } else {
        this.setToIncomplete();
      }
      this.setId(t.getId());
      this.setUserId(t.getUserId());

  }

  public String getTitle() {
    return title;
  }

  /**
   * Set the title of the task
   * If title is empty or null, default title is "New Title"
   * @param title
   */
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

  /**
   * Set dueDate
   * If null, default due date will be one week from LocalDate.now()
   * @param dueDate
   */
  public void setDueDate(LocalDate dueDate) {
    if (dueDate == null) {
      this.dueDate = LocalDate.now().plusWeeks(1);
      return;
    }
    this.dueDate = dueDate;
  }

  public TaskPriority getPriority() {
    return priority;
  }

  /**
   * Set the task priority to a TaskPriority enum value
   * if null, default will be MEDIUM
   * @param priority the TaskPriority enum value
   */
  public void setPriority(TaskPriority priority) {
    if (priority == null) {
      priority = TaskPriority.MEDIUM;
    }
    this.priority = priority;
  }

  public String getTaskType() {
    return taskType;
  }

  public void setTaskType(String taskType) {
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

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  @Override
  public final boolean equals(Object o) {
    if (!(o instanceof Task task)) {
      return false;
    }

    return isCompleted() == task.isCompleted() && Objects.equals(getTitle(),
        task.getTitle()) && Objects.equals(getDescription(), task.getDescription())
        && Objects.equals(getDueDate(), task.getDueDate())
        && getPriority() == task.getPriority() && Objects.equals(getTaskType(),
        task.getTaskType()) && Objects.equals(getId(), task.getId())
        && Objects.equals(getUserId(), task.getUserId());
  }

  @Override
  public int hashCode() {
    int result = Objects.hashCode(getTitle());
    result = 31 * result + Objects.hashCode(getDescription());
    result = 31 * result + Objects.hashCode(getDueDate());
    result = 31 * result + Objects.hashCode(getPriority());
    result = 31 * result + Objects.hashCode(getTaskType());
    result = 31 * result + Boolean.hashCode(isCompleted());
    result = 31 * result + Objects.hashCode(getId());
    result = 31 * result + Objects.hashCode(getUserId());
    return result;
  }
}
