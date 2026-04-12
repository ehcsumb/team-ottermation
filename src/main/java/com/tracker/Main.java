package com.tracker;

import javafx.application.Application;
import javafx.stage.Stage;

// entry point for the javafx application
public class Main extends Application {

    @Override
    public void start(Stage stage) {
        // connect to database first
        Database.connect();

        // give the stage to SceneManager so it can switch scenes
        SceneManager.setStage(stage);

        // set window size
        stage.setTitle("Task Manager");
        stage.setWidth(460);
        stage.setHeight(500);
        stage.setResizable(false);

        // show the login screen first
        SceneManager.showLogin();
        stage.show();
    }

    @Override
    public void stop() {
        // close database connection when app closes
        Database.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}