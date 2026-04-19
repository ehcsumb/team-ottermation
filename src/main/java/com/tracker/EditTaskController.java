package com.tracker;

import com.tracker.dao.SQLiteTaskTypeDAO;
import com.tracker.dao.TaskTypeDAO;
import com.tracker.dao.TasksDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.SQLException;
import java.util.Optional;

/**
 * This is the controller for the EditTask scene.
 * This scene load the current task information which is pass in by the task list scene.
 * There is an alert for confirmation of deleting the task.
 * There is also a bind to disable the save button if there isn't a task title.
 */

/**
 * @author Loi Tong <br>
 *     created: 4/11/2026
 * @since 0.1.0
 */
public class EditTaskController {

    @FXML private TextField taskTitle;
    @FXML private DatePicker dueDate;
    @FXML private ChoiceBox<String> priorityDropDown;
    @FXML private ChoiceBox<TaskType> taskTypeDropDown;
    @FXML private TextArea taskDetails;
    @FXML private CheckBox markComplete;
    @FXML private Button handleCancelButton;
    @FXML private Button handleSaveButton;
    @FXML private Button handleDeleteButton;
          private Task currentTask;
          private final TaskTypeDAO taskTypeDAO = new SQLiteTaskTypeDAO();

    public void initialize() {
        handleSaveButton.setOnAction(event -> handleSaveButton());
        handleCancelButton.setOnAction(event -> handleCancelButton());
        handleDeleteButton.setOnAction(event -> handleDeleteButton());

        //Grab the taskType from the database. Try catch is need in case there's an issue with the database.
        try {
            taskTypeDropDown.getItems().addAll(taskTypeDAO.getAllTaskTypes());
        } catch (SQLException e) {
            System.out.println("Error while getting task types" + e.getMessage());
        }

        //Load priority dropdown. Loop through each enum value and add it to the dropdown.
        for (TaskPriority priority : TaskPriority.values()) {
            priorityDropDown.getItems().add(priority.getText());
        }

        //Grab the task from SceneManager
        currentTask = SceneManager.currentTask;
        //Load values into the fields.
        taskTitle.setText(currentTask.getTitle());
        dueDate.setValue(currentTask.getDueDate());
        priorityDropDown.setValue(currentTask.getPriority().getText());
        markComplete.setSelected(currentTask.isCompleted());
        taskDetails.setText(currentTask.getDescription());

        //Compare each item from dropdown to see if it's equal to the currentTask.
        for (TaskType type : taskTypeDropDown.getItems()) {
            //If matched, set value.
            if (type.getName().equals(currentTask.getTaskType())) {
                taskTypeDropDown.setValue(type);
                break;
            }
        }

        //Disable save button if task tittle is empty.
        handleSaveButton.disableProperty().bind(
                taskTitle.textProperty().isEmpty()
        );
    }

    @FXML
    private void handleDeleteButton() {
        //Add two button for the alert box.
        ButtonType yesDeleteButton = new ButtonType("Yes, delete task.");
        ButtonType noDeleteButton = new ButtonType("No! Cancel delete.");
        //Ask for confirmation
        Alert confirmDelete = new Alert(
            Alert.AlertType.CONFIRMATION,
            "Are you sure you want to delete this task?",
            yesDeleteButton,
            noDeleteButton
        );
        //Return to page if not Yes.
        Optional<ButtonType> result = confirmDelete.showAndWait();
        if (result.isEmpty() || result.get() != yesDeleteButton) {
            return;
        }

        try {
            //Call TasksDAO to delete current Task.
            TasksDAO.deleteTask(currentTask);
            //Return to show task list scene.
            SceneManager.showTaskList();
        } catch (Exception e) {
            System.out.println("Error while deleting" + e.getMessage());
        }
    }

    @FXML
    private void handleCancelButton() {
        // Go back to the showTaskList without saving if yesCancelButton was selected.
        ButtonType yesCancelButton = new ButtonType("Yes, I want to cancel.");
        ButtonType noCancelButton = new ButtonType("No, I want to save.");
        // Alert box to confirm cancel button.
        Alert confirm = new Alert(
                Alert.AlertType.CONFIRMATION,"Are you sure you want to cancel? Changes will not be saved.",
                yesCancelButton, noCancelButton
        );
        // Read the button input
        Optional<ButtonType> result = confirm.showAndWait();
        //Check for noCancelButton
        if (result.isEmpty() || result.get() != yesCancelButton) {
            return;
        }
        SceneManager.showTaskList();
    }

    @FXML
    private void handleSaveButton() {
        //Set all the value to the methods.
        currentTask.setTitle(taskTitle.getText());
        currentTask.setDueDate(dueDate.getValue());
        currentTask.setDescription(taskDetails.getText());
        currentTask.setPriority(TaskPriority.fromText(priorityDropDown.getValue()));
        currentTask.setTaskType(taskTypeDropDown.getValue().getName());
        if (markComplete.isSelected()) {
            currentTask.setToCompleted();
        } else {
            currentTask.setToIncomplete();
        }

        //Push to database.
        try {
            TasksDAO.updateTask(currentTask);
            SceneManager.showTaskList();
        } catch (Exception e) {
            System.out.println("Error while saving" + e.getMessage());
        }
    }
}
