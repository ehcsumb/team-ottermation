package com.tracker;

import com.tracker.dao.SQLiteTaskTypeDAO;
import com.tracker.dao.TaskTypeDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Represents the admin settings controller and manages the admin settings view and its interactions.
 *
 * @author David Renteria
 * @version 0.2.0
 * @since 4/7/2026
 */

public class AdminSettingsController {

    @FXML
    private ComboBox<String> priorityComboBox;

    @FXML
    private TableView<TaskType> taskTypeTable;

    @FXML
    private TableColumn<TaskType, String> taskTypeNameColumn;

    @FXML
    private TableColumn<TaskType, Void> editColumn;

    @FXML
    private TableColumn<TaskType, Void> deleteColumn;

    private final ObservableList<TaskType> taskTypes = FXCollections.observableArrayList();
    private final TaskTypeDAO taskTypeDAO = new SQLiteTaskTypeDAO();

    /**
     * initializes the Admin Settings view.
     * Automatically called by JavaFX after the FXML contents have been loaded.  Sets up priority dropdown,
     * configures the task, type table, and loads task types from the database.
     */
    @FXML
    public void initialize() {
        setupPriorityBox();
        setupTable();
        loadTaskTypes();
    }

    /**
     * Initializes the priority ComboBox with the predefined priority levels
     * and sets the default selection to "Medium".
     */
    private void setupPriorityBox() {
        priorityComboBox.setItems(FXCollections.observableArrayList(
                "Low", "Medium", "High", "Urgent!"
        ));
        priorityComboBox.setValue("Medium");
    }

    /**
     * Configures the Task Type table
     * Sets the column resize policy, binds the task type name column
     * to the takType model, adds an edit and delete button column,
     * and assigns the data list to the table.
     */
    private void setupTable() {
        taskTypeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        taskTypeNameColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getName())
        );

        addEditButton();
        addDeleteButton();

        taskTypeTable.setItems(taskTypes);
    }

    /**
     * Loads task types from the database using the TaskTypeDAO and populates the taskTypes list.
     */
    private void loadTaskTypes() {
        taskTypes.clear();

        try {
            taskTypes.addAll(taskTypeDAO.getAllTaskTypes());
        } catch (SQLException e) {
            showError("Failed to load task types.");
        }
    }

    /**
     * Handles the action of adding a new task type.
     * Displays a TextInputDialog to get the new task type name from the user,
     * validates the input, checks for duplicates, and if valid, adds the new task type to the
     * database and refreshes the task type list.
     */
    @FXML
    private void handleAddTaskType() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Task Type");
        dialog.setHeaderText("Create a new task type");
        dialog.setContentText("Task type name:");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) {
            return;
        }

        String name = result.get().trim();
        if (name.isEmpty()) {
            showWarning("Task type name cannot be empty.");
            return;
        }

        try {
            if (taskTypeDAO.existsByName(name)) {
                showWarning("That task type already exists.");
                return;
            }

            taskTypeDAO.addTaskType(name);
            loadTaskTypes();

        } catch (SQLException e) {
            showError("Failed to add task type.");
        }
    }

    /**
     * Handles editing an existing task type.
     * Displays a TextInputDialog pre-filled with the current task type name, allowing the user to update it.
     * Validates the input, checks for duplicates, and if valid,
     * updates the task type in the database and refreshes the task type list.
     * @param taskType the task type being edited
     */
    private void handleEdit(TaskType taskType) {
        TextInputDialog dialog = new TextInputDialog(taskType.getName());
        dialog.setTitle("Edit Task Type");
        dialog.setHeaderText("Update task type");
        dialog.setContentText("Task type name:");

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) {
            return;
        }

        String updatedName = result.get().trim();
        if (updatedName.isEmpty()) {
            showWarning("Task type name cannot be empty.");
            return;
        }

        try {
            taskTypeDAO.updateTaskType(taskType.getId(), updatedName);
            loadTaskTypes();

        } catch (SQLException e) {
            showError("Failed to update task type.");
        }
    }

    /**
     * Handles deleting a task type
     * Prompts the user with a confirmation dialog to confirm deletion of the selected task type.
     * If the user confirms, deletes the task type from the database and refreshes the task type list.
     * @param taskType the task type being deleted
     */
    private void handleDelete(TaskType taskType) {
        Alert confirm = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Delete \"" + taskType.getName() + "\"?",
                ButtonType.OK,
                ButtonType.CANCEL
        );

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        try {
            taskTypeDAO.deleteTaskType(taskType.getId());
            loadTaskTypes();

        } catch (SQLException e) {
            showError("Failed to delete task type.");
        }
    }

    /**
     * Adds an "Edit" button to each row of the task type table.
     * When clicked, it calls the handleEdit method for the corresponding task type.
     */
    private void addEditButton() {
        editColumn.setCellFactory(col -> new TableCell<>() {
            private final Button button = new Button("Edit");

            {
                button.setOnAction(event -> {
                    TaskType taskType = getTableView().getItems().get(getIndex());
                    handleEdit(taskType);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : button);
            }
        });
    }

    /**
     * Adds a delete button to each row in the table.
     * Allows user to remove task type.
     */
    private void addDeleteButton() {
        deleteColumn.setCellFactory(col -> new TableCell<>() {
            private final Button button = new Button("Delete");

            {
                button.setStyle("-fx-background-color: #f4a6a6;");
                button.setOnAction(event -> {
                    TaskType taskType = getTableView().getItems().get(getIndex());
                    handleDelete(taskType);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : button);
            }
        });
    }

    /**
     * Handles the action of returning to the admin dashboard.
     */
    @FXML
    private void handleBackToDashboard() {
        SceneManager.showAdminDashboard();
    }

    /**
     * Displays a warning alert with specified message
     *
     * @param message the warning message to display
     */
    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays an error alert with specified message
     *
     * @param message the error message to display
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}