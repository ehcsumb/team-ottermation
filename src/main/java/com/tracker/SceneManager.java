package com.tracker;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

// handles all scene switching
// every scene change goes through here
public class SceneManager {

    private static Stage stage;

    // logged in user — set after login, read by any controller
    public static User currentUser;

    public static void setStage(Stage s) {
        stage = s;
    }

    // member 1 scenes
    public static void showLogin() {
        load("login.fxml");
    }

    public static void showRegister() {
        load("register.fxml");
    }

    // member 2 scenes
    public static void showDashboard() {
        load("dashboard.fxml");
    }

    public static void showTaskList() { load("task-list.fxml"); }

    public static void showAddTask() { load("task-add.fxml"); }

    // member 3 scenes
    public static void showEditTask() { load("task-edit.fxml"); }

    // member 4 scenes
    public static void showAdminDashboard() {
        load("admin_dashboard.fxml");
    }

    public static void showAdminSettings() {
        load("admin_settings.fxml");
    }

    // loads an fxml file and sets it as the current scene
    private static void load(String filename) {
        try {
            Parent root = FXMLLoader.load(
                    SceneManager.class.getResource("/com/tracker/" + filename)
            );
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            System.out.println("could not load " + filename + ": " + e.getMessage());
            System.out.println(e.getCause().toString());
        }
    }
}