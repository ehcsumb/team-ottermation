package com.tracker;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * entry point for the javafx application.
 * connects to the database, sets up the stage, and shows the login scene first.
 *
 * @author Khiem Vo
 * @since 4/6/2026
 */
public class Main extends Application {

    /**
     * called by javafx when the app starts.
     * sets up the database connection, window size, and loads the login scene.
     *
     * @param stage the primary window provided by javafx
     */
    @Override
    public void start(Stage stage) {
        Database.connect();
        SceneManager.setStage(stage);
        stage.setTitle("Task Manager");
        stage.setWidth(460);
        stage.setHeight(500);
        stage.setResizable(false);
        SceneManager.showLogin();
        stage.show();
    }

    /**
     * called when the app closes.
     * closes the database connection cleanly.
     */
    @Override
    public void stop() {
        Database.close();
    }

    /**
     * launches the javafx application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
