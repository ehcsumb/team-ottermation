package com.tracker;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit test for Task class
 *
 * @author Eric Holm
 * @since 4/14/26
 */
class TaskTest {

  int taskId = 1;
  int userId = 10;
  String title = "title";
  String desc = "this is a description";
  LocalDate dueDate = LocalDate.parse("2026-04-01");
  TaskPriority priority = TaskPriority.MEDIUM;
  String type = "Appointment";
  boolean complete = false;

  Task taskA;
  Task taskB;
  Task taskC;


  @BeforeEach
  void setUp() {
    taskA = new Task(title,desc,dueDate,priority,type,complete,taskId,userId);
    taskB = new Task(taskA);
    taskC = new Task();

  }

  @Test
  void getTitle() {
    assertEquals(title,taskA.getTitle());
  }

  @Test
  void setTitle() {
    String newTitle = "new title";
    assertNotEquals(newTitle, taskA.getTitle());
    taskA.setTitle(newTitle);
    assertEquals(newTitle, taskA.getTitle());
  }

  @Test
  void getDescription() {
    assertEquals("", taskC.getDescription());
    assertEquals(desc,taskA.getDescription());
  }

  @Test
  void setDescription() {
    String newDesc = "yup";
    assertNotEquals(newDesc, taskA.getDescription());
    taskA.setDescription(newDesc);
    assertEquals(newDesc,taskA.getDescription());
  }

  @Test
  void getDueDate() {
    assertEquals(dueDate, taskA.getDueDate());
  }

  @Test
  void setDueDate() {
    LocalDate newDate = LocalDate.parse("2026-05-01");
    assertNotEquals(newDate, taskA.getDueDate());
    taskA.setDueDate(newDate);
    assertEquals(newDate,taskA.getDueDate());
  }

  @Test
  void getPriority() {
    assertEquals(priority,taskA.getPriority());
  }

  @Test
  void setPriority() {
    taskA.setPriority(TaskPriority.URGENT);
    assertEquals(TaskPriority.URGENT, taskA.getPriority());
  }

  @Test
  void isCompleted() {
    taskC.setToIncomplete();
    assertNotEquals(true,taskC.isCompleted());
    taskC.setToCompleted();
    assertTrue(taskC.isCompleted());
  }


  @Test
  void testTaskCopy() {
    assertTrue(taskA.equals(taskB));
    assertEquals(taskA, taskB);
  }
}