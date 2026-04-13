
package com.tracker;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Represents the admin dashboard controller, which manages the admin dashboard view
 * and its interactions.
 * The admin dashboard provides an overview of task statistics
 * and allows the admin to navigate to other sections of the application,
 * such as admin settings.
 *
 * @author David Renteria
 * @version 0.1.0
 * @since 4/6/2026
 */
public class AdminDashboardController {

    @FXML private Label totalTasksLabel;
    @FXML private Label urgentCountLabel;
    @FXML private Label highCountLabel;
    @FXML private Label mediumCountLabel;
    @FXML private Label lowCountLabel;

    /**
     * Initializes the admin dashboard view.
     * Automatically called by JavaFX after the FXML contents have been loaded.
     * Sets up the task statistics labels with mock data for now.
     */
    @FXML
    public void initialize() {
        // represents our mockup
        totalTasksLabel.setText("Total tasks: 13");
        urgentCountLabel.setText("2");
        highCountLabel.setText("4");
        mediumCountLabel.setText("2");
        lowCountLabel.setText("5");
    }


    /**
     * Handles the action of the logout button.  Routes back to log-in scene
     */
    @FXML
    private void handleLogout() {
        SceneManager.showLogin();
    }

    /**
     * Handles the action of the Admin Settings button.  Routes to Admin Settings scene
     */
    @FXML
    private void handleAdminSettings() {
        SceneManager.showAdminSettings();
    }

    /**
     * Handles the action of the View Task Lists button.  Routes to user dashboard scene.
     */
    @FXML
    private void handleViewTaskList() {
        SceneManager.showTaskList();
    }
}