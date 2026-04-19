package com.tracker;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import com.tracker.dao.TasksDAO;
import java.util.ArrayList;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Button;
/*
 * This class is for the Main dashboard the user will see after the login page.
 * The User dashboard display a count of each priority.
 * This page have 3 buttons. A logout, view all the task and a shortcut to add a task.
 */
/**
 * @author Loi Tong
 * <br>
 * created: 4/8/2026
 * @since 0.1.0
 */
public class UserDashboardController {

    @FXML private Label totalTasksLabel;
    @FXML private Label urgentCountLabel;
    @FXML private Label highCountLabel;
    @FXML private Label mediumCountLabel;
    @FXML private Label lowCountLabel;
    @FXML private Button viewTasksList;

    private ObservableList<Task> tasksObservableList = FXCollections.observableArrayList();

    public void initialize()
    {

        loadDashboardStats();

        //Bind my viewTaskList button to an Observable list and check if it's empty. If empty disable button.
        viewTasksList.disableProperty().bind(
                Bindings.createBooleanBinding(
                        () -> tasksObservableList.isEmpty(),
                        tasksObservableList
                )
        );

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

            //Also add a tasks to the observable list to check if it's empty.
            tasksObservableList.addAll(tasks);

            DashboardStats stats = DashboardStatsUtil.calculateStats(tasks);
            //Load values.
            totalTasksLabel.setText("Total tasks: " + stats.getTotal());
            urgentCountLabel.setText(String.valueOf(stats.getUrgent()));
            highCountLabel.setText(String.valueOf(stats.getHigh()));
            mediumCountLabel.setText(String.valueOf(stats.getMedium()));
            lowCountLabel.setText(String.valueOf(stats.getLow()));

        } catch (Exception e) {
            System.out.println("Error loading stats: " + e.getMessage());
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

    @FXML
    public void handleLogout() {
        //Return to the login scene.
        SceneManager.showLogin();
    }

    @FXML
    public void handleViewTasksList() {
        //Go to show Task list scene.
        SceneManager.showTaskList();
    }

    @FXML
    public void handleAddTask() {
        //Go to add Task scene.
        SceneManager.showAddTask();
    }
}
