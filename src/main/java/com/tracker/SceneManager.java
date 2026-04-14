package com.tracker;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * handles all scene switching for the app.
 * every scene change goes through here — no controller should load fxml directly.
 * also holds the currently logged in user and the current task being edited.
 *
 * @author Khiem Vo
 * @since 4/6/2026
 */
public class SceneManager {

    private static Stage stage;

    /** the currently logged in user. set after login, cleared on logout. */
    public static User currentUser;

    /** the task currently being edited. set before navigating to the edit scene. */
    public static Task currentTask;

    /**
     * stores the primary stage so scenes can be swapped later.
     *
     * @param s the main javafx stage
     */
    public static void setStage(Stage s) {
        stage = s;
    }

    // --- member 1 scenes ---

    /** shows the login scene. */
    public static void showLogin() { load("login.fxml"); }

    /** shows the register scene. */
    public static void showRegister() { load("register.fxml"); }

    // --- member 2 scenes ---

    /** shows the user dashboard. */
    public static void showDashboard() { load("dashboard.fxml"); }

    /** shows the task list scene. */
    public static void showTaskList() { load("task-list.fxml"); }

    /** shows the add task scene. */
    public static void showAddTask() { load("task-add.fxml"); }

    // --- member 3 scenes ---

    /** shows the edit task scene. */
    public static void showEditTask() { load("task-edit.fxml"); }

    // --- member 4 scenes ---

    /** shows the admin dashboard. */
    public static void showAdminDashboard() { load("admin_dashboard.fxml"); }

    /** shows the admin settings scene. */
    public static void showAdminSettings() { load("admin_settings.fxml"); }

    /**
     * loads an fxml file and sets it as the current scene.
     * prints an error message if the file can't be found.
     *
     * @param filename the name of the fxml file in /com/tracker/
     */
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
