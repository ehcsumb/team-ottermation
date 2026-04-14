package com.tracker;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * controls the login scene.
 * validates credentials against the database and routes the user
 * to the correct dashboard based on their role.
 *
 * @author Khiem Vo
 * @since 4/6/2026
 */
public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    /**
     * called when the login button is clicked.
     * checks that fields aren't empty, then validates credentials against the database.
     * routes to admin dashboard if role is "admin", user dashboard otherwise.
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("please fill in all fields");
            return;
        }

        User user = Database.login(username, password);

        if (user == null) {
            errorLabel.setText("incorrect username or password");
            passwordField.clear();
            return;
        }

        SceneManager.currentUser = user;

        if (user.isAdmin()) {
            SceneManager.showAdminDashboard();
        } else {
            SceneManager.showDashboard();
        }
    }

    /**
     * called when the register button is clicked.
     * navigates to the register scene.
     */
    @FXML
    private void handleRegister() {
        SceneManager.showRegister();
    }
}
