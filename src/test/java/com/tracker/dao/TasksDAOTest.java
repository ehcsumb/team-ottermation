package com.tracker.dao;

import static org.junit.jupiter.api.Assertions.*;

import com.tracker.Database;
import com.tracker.SceneManager;
import com.tracker.Task;
import com.tracker.TaskPriority;
import com.tracker.User;
import java.time.LocalDate;
import javax.xml.crypto.Data;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 *
 * @author Eric Holm
 * @since 4/19/26
 */
class TasksDAOTest {

  static User user;

  static Task taskA;
  static Task taskB;

  @BeforeAll
  static void setUpAll() {
    Database.connect();
    user = Database.login("test","test");
    if (user == null) {
      Database.register("test","test");
      user = Database.login("test","test");
    }


    taskA = new Task(
        "title",
        "desc",
        LocalDate.now().plusWeeks(2),
        TaskPriority.LOW,
        "unit-test",
        false,
        null,
        user.getId()
    );


    taskB = new Task(
        "other title",
        "notes on usage",
        LocalDate.now().plusWeeks(2),
        TaskPriority.LOW,
        "unit-test",
        false,
        null,
        user.getId()
    );
  }

  @BeforeEach
  void setUp() {
    // clear all tasks
    for (Task task : TasksDAO.getAllTasks(user)) {
      TasksDAO.deleteTask(task);
    }
    TasksDAO.addTask(taskA);
    TasksDAO.addTask(taskB);
  }

  @Test
  void addTask() {
    int taskCount = TasksDAO.getAllTasks(user).size();
    assertNotEquals(taskCount+1,taskCount);
    TasksDAO.addTask(taskA);
    assertEquals(taskCount+1, TasksDAO.getAllTasks(user).size());
  }

  @Test
  void getTaskById() {
    Task taskC = TasksDAO.getAllTasks(user).get(0);
    Task taskD = TasksDAO.getTaskById(taskC.getId());
    assertEquals(taskC,taskD);
  }

  @Test
  void updateTask() {
    Task taskC = TasksDAO.getAllTasks(user).get(0);
    String newTitle = "brand new title";
    taskC.setTitle(newTitle);
    TasksDAO.updateTask(taskC);
    Task taskCupdated = TasksDAO.getTaskById(taskC.getId());
    assertEquals(newTitle, taskCupdated.getTitle());

  }

  @Test
  void deleteTask() {
    TasksDAO.addTask(taskA);
    Task taskE = TasksDAO.getAllTasks(user).getLast();
    int taskCount = TasksDAO.getAllTasks(user).size();
    TasksDAO.deleteTask(taskE);
    assertEquals(taskCount-1,TasksDAO.getAllTasks(user).size());
    assertNull(TasksDAO.getTaskById(taskE.getId()));
  }

}