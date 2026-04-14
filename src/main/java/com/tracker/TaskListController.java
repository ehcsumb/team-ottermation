package com.tracker;

import com.tracker.dao.TasksDAO;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;

/**
 * Initializes the Task List scene.
 * Gets task data for this user from the database and
 * populates the UI elements.
 *
 * @author Eric Holm
 * @since 4/12/26
 */
public class TaskListController {


  @FXML public Button btn_backToDashboard;
  @FXML public Button btn_addTask;
  @FXML private VBox taskList_vbox;
  @FXML private CheckBox checkbox_showCompleted;
  private ArrayList<Task> tasks;
  boolean showCompleted = false;

  @FXML
  public void initialize() {
    // get task list from DB and set to scene's task list
    try {
      tasks = getTaskList(SceneManager.currentUser);
    } catch (SQLException e) {
      tasks = null;
    }
    // populate the VBox with tasks
    populateTaskList();

    // show/hide completed
    toggleCompletedVisibility();
  }

  public ArrayList<Task> getTaskList(User user) throws SQLException {
    return TasksDAO.getAllTasks(user);
  }

  public void populateTaskList() {
    // check if the task list for the scene is empty
    if(tasks == null || tasks.isEmpty()) {
      return;
    }
    for (Task task : tasks) {
      addTaskToList(task);
    }

  }

  private void addTaskToList(Task task) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tracker/task-list-item.fxml"));
      VBox taskListItem = loader.load();
      TaskListItemController controller = loader.getController();
      controller.setItemData(task);
      taskList_vbox.getChildren().add(taskListItem);
    } catch (IOException e) {
      System.out.println("addToTaskList: couldn't load fxml");
    }
  }

  @FXML
  public void handleAddTask() {
    SceneManager.showAddTask();
  }

  @FXML
  public void handleBackToDashboard() {
    if (SceneManager.currentUser.isAdmin()) {
      SceneManager.showAdminDashboard();
    } else {
      SceneManager.showDashboard();
    }
  }

  @FXML
  public void toggleCompletedVisibility() {
    // update local state
    showCompleted = checkbox_showCompleted.isSelected();
    // clear the vbox first
    taskList_vbox.getChildren().clear();
    // go through all the tasks and show or hide depending on showCompleted
    for (Task task : tasks) {
      if (!showCompleted && task.isCompleted()) {
        continue;
      }
      addTaskToList(task);
    }
  }
}
