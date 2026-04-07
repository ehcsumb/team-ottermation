
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

    @FXML
    public void initialize() {
        // represents our mockup
        totalTasksLabel.setText("Total tasks: 13");
        urgentCountLabel.setText("2");
        highCountLabel.setText("4");
        mediumCountLabel.setText("2");
        lowCountLabel.setText("5");
    }


    @FXML
    private void handleLogout() {
        SceneManager.showLogin();
    }

    @FXML
    private void handleAdminSettings() {
        SceneManager.showAdminSettings();
    }

    @FXML
    private void handleViewTaskList() {
        SceneManager.showDashboard();
    }
}