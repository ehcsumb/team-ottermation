package com.tracker;

import com.tracker.dao.SQLiteTaskTypeDAO;
import com.tracker.dao.TasksDAO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.ChoiceBox;
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
  @FXML private ChoiceBox<TaskPriority> choiceBox_priority;
  @FXML private ChoiceBox<TaskType> choiceBox_taskType;
  @FXML private TextArea textArea_description;

  private List<TaskType> taskTypes;

  @FXML public void initialize() {

    // get list of task types and populate task type dropdown
    SQLiteTaskTypeDAO dao = new SQLiteTaskTypeDAO();
    try {
      taskTypes = dao.getAllTaskTypes();
      // add each item to the choicebox
      for (TaskType t : taskTypes) {
        choiceBox_taskType.getItems().add(t);
      }
      // set first taskType as starting value
      choiceBox_taskType.setValue(taskTypes.get(0));

    } catch (SQLException e) {
      System.out.println("getAllTaskTypes: " + e.getCause());
      System.out.println("getAllTaskTypes: " + e.getMessage());

      // TODO: show error in UI
    }

    // populate priority dropdown
    for (TaskPriority p : TaskPriority.values()) {
      choiceBox_priority.getItems().add(p);
    }
    // set default item
    choiceBox_priority.setValue(TaskPriority.MEDIUM);

  }


  public void handleAddTask() {
    // TODO: if title, date, priority, or task type are blank, inform user
    if (txtField_taskTitle.getText().isEmpty() ||
        datePicker_dueDate.getValue().toString().isEmpty()
    ) {
      System.out.println("handleAddTask: missing required fields");
      // TODO: inform user in UI
    }
    // create a new task object
    Task task = new Task(
        txtField_taskTitle.getText(),
        textArea_description.getText(),
        datePicker_dueDate.getValue(),
        choiceBox_priority.getValue(),
        choiceBox_taskType.getValue().toString(),
        false,
        null,
        SceneManager.currentUser.getId()
    );
    // add task to database
    if (TasksDAO.addTask(task) != null) {
      cancelAction();  // used here to navigate back to task list scene
    } else {
      System.out.printf("handleAddTask:  error adding task to db");
      // TODO: Inform user of error via UI
    }
  }

  public void cancelAction() {
    SceneManager.showTaskList();
  }
}
