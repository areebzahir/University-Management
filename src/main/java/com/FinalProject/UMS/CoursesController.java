package com.FinalProject.UMS;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

import java.io.IOException;

public class CoursesController {
    @FXML
    private ListView<String> coursesList;

    @FXML
    private TextField courseInput; // Input field to add new courses

    @FXML
    private Button addCourseButton; // Button to trigger course addition

    @FXML
    private Label selectedCourseLabel; // Label to display selected course

    @FXML
    public void initialize() {
        // Sample data
        coursesList.getItems().addAll("Course 1", "Course 2", "Course 3");

        // Add event listener to handle course selection
        coursesList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedCourseLabel.setText("Selected Course: " + newValue);
            }
        });
    }

    @FXML
    protected void onReturnButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("studentmanagecontroller.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) coursesList.getScene().getWindow();
            Scene scene = new Scene(root, 1366, 768);
            stage.setScene(scene);
            stage.setTitle("Hello");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to handle adding a new course dynamically
    @FXML
    protected void onAddCourseButtonClick() {
        String newCourse = courseInput.getText().trim();
        if (!newCourse.isEmpty()) {
            coursesList.getItems().add(newCourse);
            courseInput.clear(); // Clear the input field after adding
        }
    }
}
