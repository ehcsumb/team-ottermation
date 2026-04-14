package com.tracker;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * controls the register scene.
 * validates user input and creates a new account in the database.
 *
 * @author Khiem Vo
 * @since 4/7/2026
 */
public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmField;
    @FXML private Label messageLabel;

    /**
     * called when the register button is clicked.
     * validates username, password length, and matching passwords
     * before inserting the new user into the database.
     */
    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirm  = confirmField.getText();

        if (username.isEmpty()) {
            showError("please enter a username");
            return;
        }
        if (password.length() < 4) {
            showError("password must be at least 4 characters");
            return;
        }
        if (!password.equals(confirm)) {
            showError("passwords do not match");
            return;
        }

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

    /**
     * called when the return to start button is clicked.
     * navigates back to the login scene.
     */
    @FXML
    private void handleReturnToStart() {
        SceneManager.showLogin();
    }

    /**
     * shows a red error message on the screen.
     *
     * @param message the error text to display
     */
    private void showError(String message) {
        messageLabel.setStyle("-fx-text-fill: #cc0000;");
        messageLabel.setText(message);
    }

    /**
     * shows a green success message on the screen.
     *
     * @param message the success text to display
     */
    private void showSuccess(String message) {
        messageLabel.setStyle("-fx-text-fill: #2a8c4a;");
        messageLabel.setText(message);
    }
}
