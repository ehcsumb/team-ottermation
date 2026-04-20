package com.tracker.dao;

import static org.junit.jupiter.api.Assertions.*;

import com.tracker.SceneManager;
import com.tracker.Task;
import com.tracker.TaskPriority;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 *
 * @author Eric Holm
 * @since 4/19/26
 */
class TasksDAOTest {

  Task taskA = new Task(
      "title",
      "desc",
      LocalDate.now().plusWeeks(2),
      TaskPriority.LOW,
      "unit-test",
      false,
      30,
      SceneManager.currentUser.getId()
  );

  Task taskB = new Task(
      "other title",
      "notes on usage",
      LocalDate.now().plusWeeks(2),
      TaskPriority.LOW,
      "unit-test",
      false,
      30,
      SceneManager.currentUser.getId()
  );

  @BeforeEach
  void setUp() {
  }

  @Test
  void addTask() {
  }

  @Test
  void getTaskById() {
  }

  @Test
  void updateTask() {
  }

  @Test
  void deleteTask() {
  }

  @Test
  void deleteTaskById() {
  }

  @Test
  void getAllTasks() {
  }
}