package com.tracker;

import javafx.fxml.FXML;

/**
 * Initializes the Add Task scene.  Populates the priority dropdown menu and
 * the task type dropdown menu.
 *
 * @author Eric Holm
 * @since 4/6/26
 */
public class AddTaskController {

  public void cancelAction() {
    SceneManager.showTaskList();
  }
}
