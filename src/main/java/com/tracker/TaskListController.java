package com.tracker;

import com.tracker.dao.TasksDAO;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
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
  private ArrayList<Task> tasks;

  @FXML
  public void initialize() {
    // get task list from DB and set to scene's task list
    try {
      tasks = getTaskList(SceneManager.currentUser);
    } catch (SQLException e) {
      tasks = null;
    }
    // populate the VBox with tasks
    try {
      populateTaskList();
    } catch (IOException e) {
      System.out.println("could not load file: " + e);
    }
  }

  public ArrayList<Task> getTaskList(User user) throws SQLException {
    return TasksDAO.getAllTasks(user);
  }

  public void populateTaskList() throws IOException {
    // check if the task list for the scene is empty
    if(tasks == null || tasks.isEmpty()) {
      return;
    }
    for (Task task : tasks) {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tracker/task-list-item.fxml"));
      HBox taskListItem = loader.load();
      TaskListItemController controller = loader.getController();
      controller.setItemData(
          task.getTitle(),
          task.getPriority().getText(),
          task.getTaskType(),
          task.getId()
        );
      taskList_vbox.getChildren().add(taskListItem);
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
}
