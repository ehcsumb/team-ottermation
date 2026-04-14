package com.tracker;

import com.tracker.dao.TasksDAO;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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
  @FXML private CheckBox checkbox_completed;
  @FXML private Label label_dueDate;
  private Task task;

  public void setItemData(Task task) {
    this.task = task;
    taskTitle.setText(task.getTitle());
    taskPriority.setText(task.getPriority().toString());
    taskType.setText(task.getTaskType());
    checkbox_completed.setSelected(task.isCompleted());
    label_dueDate.setText(task.getDueDate().toString());

    btn_editTask.setOnAction(e -> handleEditBtn());
    checkbox_completed.setOnAction(e -> handleCheckboxChange());
  }

  @FXML public void handleEditBtn() {
    System.out.println("Edit item id: " + task.getId());
    // set this task to the currentTask in SceneManager
    SceneManager.currentTask = task;
    // navigate to editTask.fxml
    SceneManager.showEditTask();

  }

  @FXML public void handleCheckboxChange() {
    // TODO: update database first, then do the rest
    System.out.println("handleCheckboxChange: changing to " + !task.isCompleted());

      if (task.isCompleted()) {
        task.setToIncomplete();
      } else {
        task.setToCompleted();
      }
      TasksDAO.updateTask(task);
      // update UI
      checkbox_completed.setSelected(task.isCompleted());



  }
}


