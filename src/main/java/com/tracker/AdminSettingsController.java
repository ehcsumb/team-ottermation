package com.tracker;

import com.tracker.dao.TaskTypeDAO;
import com.tracker.TaskType;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Represents the admin settings controller.
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
    private final TaskTypeDAO taskTypeDAO = new TaskTypeDAO();

    @FXML
    public void initialize() {
        setupPriorityBox();
        setupTable();
        loadTaskTypes();
    }

    private void setupPriorityBox() {
        priorityComboBox.setItems(FXCollections.observableArrayList(
                "Low", "Medium", "High", "Critical"
        ));
        priorityComboBox.setValue("Medium");
    }

    private void setupTable() {
        taskTypeNameColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getName())
        );

        addEditButton();
        addDeleteButton();

        taskTypeTable.setItems(taskTypes);
    }

    private void loadTaskTypes() {
        taskTypes.clear();

        try {
            taskTypes.addAll(taskTypeDAO.getAllTaskTypes());
        } catch (SQLException e) {
            showError("Failed to load task types.");
        }
    }

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

    @FXML
    private void handleBackToDashboard() {
        // SceneManager.switchScene("admin_dashboard.fxml");
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}