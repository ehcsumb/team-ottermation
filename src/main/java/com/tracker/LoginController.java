package com.tracker;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

// controls the login scene
// handles login button and register button clicks
public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // check if fields are empty
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("please fill in all fields");
            return;
        }

        // check credentials against database
        User user = Database.login(username, password);

        // if null, login failed
        if (user == null) {
            errorLabel.setText("incorrect username or password");
            passwordField.clear();
            return;
        }

        // store logged in user so other scenes can access it
        SceneManager.currentUser = user;

        // route to correct dashboard based on role
        if (user.isAdmin()) {
            SceneManager.showAdminSettings();
        } else {
            SceneManager.showDashboard();
        }
    }

    @FXML
    private void handleRegister() {
        SceneManager.showRegister();
    }
}