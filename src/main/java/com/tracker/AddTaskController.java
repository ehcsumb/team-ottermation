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

  // TODO: get list of task types and populate task type dropdown

  public void cancelAction() {
    SceneManager.showTaskList();
  }
}
