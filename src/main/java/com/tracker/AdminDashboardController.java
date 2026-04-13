
package com.tracker;

import com.tracker.dao.TasksDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.sql.SQLException;
import java.util.ArrayList;


/**
 * Represents the admin dashboard controller, which manages the admin dashboard view
 * and its interactions.
 * The admin dashboard provides an overview of task statistics
 * and allows the admin to navigate to other sections of the application,
 * such as admin settings.
 *
 * @author David Renteria
 * @version 0.2.0
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
        loadDashboardStats();

    }

    private void loadDashboardStats() {
        User currentUser = SceneManager.currentUser;

        if (currentUser == null) {
            showNoTasksMessage();
            return;
        }

        try {
            ArrayList<Task> tasks = TasksDAO.getAllTasks(currentUser);

            if (tasks == null || tasks.isEmpty()) {
                showNoTasksMessage();
                return;
            }

            DashboardStats stats = DashboardStatsUtil.calculateStats(tasks);

            totalTasksLabel.setText("Total tasks: " + stats.getTotal());
            urgentCountLabel.setText(String.valueOf(stats.getUrgent()));
            highCountLabel.setText(String.valueOf(stats.getHigh()));
            mediumCountLabel.setText(String.valueOf(stats.getMedium()));
            lowCountLabel.setText(String.valueOf(stats.getLow()));

        } catch (SQLException e) {
            System.out.println("Error loading dashboard stats: " + e.getMessage());
            showNoTasksMessage();
        }
    }


    private void showNoTasksMessage(){
        totalTasksLabel.setText("No tasks found");
        urgentCountLabel.setText("-");
        highCountLabel.setText("-");
        mediumCountLabel.setText("-");
        lowCountLabel.setText("-");
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