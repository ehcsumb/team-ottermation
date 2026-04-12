package com.tracker;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
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


    public void initialize()
    {}

    @FXML
    public void handleLogout()
    {
        //Clear previous users
        SceneManager.currentUser = null;
        //Return to the login scene.
        SceneManager.showLogin();
    }

    @FXML
    public void handleViewTasksList()
    {/*SceneManager.showTaskList();*/}

    @FXML
    public void handleAddTask()
    {/*SceneManager.AddTask();*/}
}
