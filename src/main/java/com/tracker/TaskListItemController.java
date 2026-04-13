package com.tracker;

import com.tracker.dao.TasksDAO;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;

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
  @FXML private CheckBox checkbox_completed;
  private Task task;
  private Integer taskId;
  private boolean complete;

  public void setItemData(Task task) {
    this.task = task;
    taskTitle.setText(task.getTitle());
    taskPriority.setText(task.getPriority().toString());
    taskType.setText(task.getTaskType());
    checkbox_completed.setSelected(task.isCompleted());

    btn_editTask.setOnAction(e -> handleEditBtn());
    checkbox_completed.setOnAction(e -> handleCheckboxChange());
  }

  @FXML public void handleEditBtn() {
    System.out.println("Edit item id: " + task.getId());
    // TODO: navigate to editTask.fxml
  }

  @FXML public void handleCheckboxChange() {
    // TODO: update database first, then do the rest
    System.out.println("handleCheckboxChange: changing to " + !task.isCompleted());
    try {
      if (task.isCompleted()) {
        task.setToIncomplete();
      } else {
        task.setToCompleted();
      }
      TasksDAO.updateT2ask(task);
      // update UI
      checkbox_completed.setSelected(task.isCompleted());
    } catch (SQLException e) {
      System.out.println("couldn't update database.");
    }


  }
}


