package com.tracker;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Just the individual "card" items that represent a task.
 * These are inserted into the ScrollPane of the TaskList
 *
 * @author Eric Holm
 * @since 4/12/26
 */
public class TaskListItemController {

  @FXML private Label taskTitle;
  @FXML private Label taskPriority;
  @FXML private Label taskType;
  @FXML private Button btn_editTask;
  private Integer taskId;

  public void setItemData(String title, String priority, String type, Integer taskId) {
    taskTitle.setText(title);
    taskPriority.setText(priority);
    taskType.setText(type);
    this.taskId = taskId;

    btn_editTask.setOnAction(e -> {
      // TODO: call Loi's EditItem method
      System.out.println("Edit item id: " + taskId);
    });
  }
}


