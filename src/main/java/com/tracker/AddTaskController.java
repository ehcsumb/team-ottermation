package com.tracker;

import java.awt.TextArea;
import java.awt.TextField;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuButton;

/**
 * Initializes the Add Task scene.  Populates the priority dropdown menu and
 * the task type dropdown menu.
 *
 * @author Eric Holm
 * @since 4/6/26
 */
public class AddTaskController {

  @FXML private TextField txtField_taskTitle;
  @FXML private DatePicker datePicker_dueDate;
  @FXML private MenuButton dropdown_priority;
  @FXML private MenuButton dropdown_taskType;
  @FXML private TextArea textArea_description;

  // TODO: get list of task types and populate task type dropdown
  // who is doing task types?

  // TODO: populate priority dropdown


  public void addTaskAction() {
    // TODO: if title, date, priority, or task type are blank, inform user
    if (txtField_taskTitle.getText().isEmpty() ||
        datePicker_dueDate.getValue().toString().isEmpty() ||
        dropdown_priority.getText().isEmpty() ||
        dropdown_taskType.getText().isEmpty()
    ) {
      return;
    }

  }

  public void cancelAction() {
    SceneManager.showTaskList();
  }
}
