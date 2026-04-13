package com.tracker;

import com.tracker.dao.SQLiteTaskTypeDAO;
import com.tracker.dao.TaskTypeDAO;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.lang.classfile.Label;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
    @FXML private Button handleDeleteButton;
    @FXML private Button handleSaveButton;
    private final TaskTypeDAO taskTypeDAO = new SQLiteTaskTypeDAO();


    public void initialize()
    {
        //Grab the taskType from the database.
        try {
            taskTypeDropDown.getItems().addAll(taskTypeDAO.getAllTaskTypes());
        } catch (SQLException e) {
            System.out.println("Error while getting task types" + e.getMessage());
        }
        //Preset the priority list for now.
        priorityDropDown.getItems().addAll("Low", "Medium", "High", "Urgent");
    }

    @FXML
    private void handleDeleteButton()
    {}

    @FXML
    private void handleSaveButton()
    {}

}
