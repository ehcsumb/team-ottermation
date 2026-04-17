package com.tracker;

import com.tracker.dao.SQLiteTaskTypeDAO;
import com.tracker.dao.TaskTypeDAO;
import com.tracker.dao.TasksDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.SQLException;
import java.util.Optional;

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
          private Task currentTask;
          private final TaskTypeDAO taskTypeDAO = new SQLiteTaskTypeDAO();

    public void initialize()
    {
        handleSaveButton.setOnAction(event -> handleSaveButton());
        handleCancelButton.setOnAction(event -> handleCancelButton());

        //Grab the taskType from the database. Try catch is need in case there's an issue with the database.
        try {
            taskTypeDropDown.getItems().addAll(taskTypeDAO.getAllTaskTypes());
        } catch (SQLException e) {
            System.out.println("Error while getting task types" + e.getMessage());
        }

        // Load priority dropdown. Loop through each enum value and add it to the dropdown.
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
    }


    @FXML
    private void handleCancelButton()
    {
        // Go back to the showTaskList without saving if yesCancelButton was selected.
        SceneManager.showTaskList();
    }

    @FXML
    private void handleSaveButton()
    {
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
