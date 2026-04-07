package com.tracker;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

// controls the register scene
// handles new account creation
public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmField;
    @FXML private Label messageLabel;

    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirm  = confirmField.getText();

        // check if fields are empty
        if (username.isEmpty()) {
            showError("please enter a username");
            return;
        }

        // check password length
        if (password.length() < 4) {
            showError("password must be at least 4 characters");
            return;
        }

        // check if passwords match
        if (!password.equals(confirm)) {
            showError("passwords do not match");
            return;
        }

        // try to register in database
        boolean success = Database.register(username, password);

        if (success) {
            showSuccess("account created! you can now log in");
            usernameField.clear();
            passwordField.clear();
            confirmField.clear();
        } else {
            showError("username already taken");
        }
    }

    @FXML
    private void handleReturnToStart() {
        SceneManager.showLogin();
    }

    private void showError(String message) {
        messageLabel.setStyle("-fx-text-fill: #cc0000;");
        messageLabel.setText(message);
    }

    private void showSuccess(String message) {
        messageLabel.setStyle("-fx-text-fill: #2a8c4a;");
        messageLabel.setText(message);
    }
}