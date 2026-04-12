package com.tracker;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuButton;

/**
 * Initializes the Task List scene.
 * Gets task data for this user from the database and
 * populates the UI elements.
 *
 * @author Eric Holm
 * @since 4/12/26
 */
public class TaskListController {




  public void handleAddTask() {
    SceneManager.showAddTask();
  }

  public void handleBackToDashboard() {
    if (SceneManager.currentUser.isAdmin()) {
      SceneManager.showAdminDashboard();
    }
    SceneManager.showDashboard();
  }
}
