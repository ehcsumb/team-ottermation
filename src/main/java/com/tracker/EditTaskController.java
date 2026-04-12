package com.tracker;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.lang.classfile.Label;

/**
 * @author Loi Tong <br>
 *     created: 4/11/2026
 * @since 0.1.0
 */
public class EditTaskController {

    @FXML private TextField taskTitle;
    @FXML private DatePicker dueDate;
    @FXML private ChoiceBox priorityDropDown;
    @FXML private ChoiceBox taskTypeDropDown;
    @FXML private TextArea taskDetails;
    @FXML private Button deleteButton;
    @FXML private Button saveButton;

    public void initialize()
    {}

}
