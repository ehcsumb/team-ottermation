
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
        loadDashboardStats();
    }


    private void loadDashboardStats() {
        User currentUser = SceneManager.currentUser;

        if (currentUser != null) {
            showNoTasksMessage();
            return;
        }

        try{
            ArrayList<Task> tasks = TasksDAO.getAllTasks(currentUser);

            if (currentUser != null) {
                showNoTasksMessage();
                return;
            }


        int total = tasks.size();
        int urgent = 0;
        int high = 0;
        int medium = 0;
        int low = 0;

        for(Task task : tasks) {
            String priority = task.getPriority().getText();

            switch(priority){
                case "Urgent!" -> urgent++;
                case "High" -> high++;
                case "Medium" -> medium++;
                case "Low" -> low++;
            }
        }

        totalTasksLabel.setText(String.valueOf(total));
        urgentCountLabel.setText(String.valueOf(urgent));
        highCountLabel.setText(String.valueOf(high));
        mediumCountLabel.setText(String.valueOf(medium));
        lowCountLabel.setText(String.valueOf(low));

        }catch(SQLException e){
            System.out.println("Error loading Dashboard Stats: " + e.getMessage());
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