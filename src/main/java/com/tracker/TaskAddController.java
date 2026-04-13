package com.tracker;

import com.tracker.dao.TasksDAO;
import java.util.ArrayList;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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
public class TaskAddController {

  @FXML private TextField txtField_taskTitle;
  @FXML private DatePicker datePicker_dueDate;
  @FXML private MenuButton dropdown_priority;
  @FXML private MenuButton dropdown_taskType;
  @FXML private TextArea textArea_description;

  private ArrayList<TaskType> taskTypes;

  @FXML public void initialize() {

    // TODO: get list of task types and populate task type dropdown

    // TODO: populate priority dropdown

  }


  public void handleAddTask() {
    // TODO: if title, date, priority, or task type are blank, inform user
    if (txtField_taskTitle.getText().isEmpty() ||
        datePicker_dueDate.getValue().toString().isEmpty() ||
        dropdown_priority.getText().isEmpty() ||
        dropdown_taskType.getText().isEmpty()
    ) {
      System.out.println("handleAddTask: missing required fields");
      // TODO: inform user in UI
    }
    // create a new task object
    Task task = new Task(
        txtField_taskTitle.getText(),
        textArea_description.getText(),
        datePicker_dueDate.getValue(),
        TaskPriority.fromText(dropdown_priority.getText()),
        dropdown_taskType.getText(),
        false,
        null,
        SceneManager.currentUser.getId()
    );
    // add task to database
    if (TasksDAO.addTask(task)) {
      cancelAction();
    } else {
      System.out.printf("handleAddTask:  error adding task to db");
      // TODO: Inform user of error via UI
    }
  }

  public void cancelAction() {
    SceneManager.showTaskList();
  }
}
