package com.FinalProject.UMS;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CoursesController {
    @FXML
    private ListView<String> coursesList; // ListView to display enrolled courses

    @FXML
    private Label selectedCourseLabel; // Label to display the selected course

    private String studentId; // Field to store the student ID

    private static final Logger LOGGER = Logger.getLogger(CoursesController.class.getName());

    @FXML
    public void initialize() {
        // Add event listener for course selection
        coursesList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedCourseLabel.setText("Selected Course: " + newValue);
            }
        });
    }



    // Handle return button click
    @FXML
    protected void onReturnButtonClick() {
        try {
            LOGGER.info("Return button clicked. Loading student management page.");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("studentmanagecontroller.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) coursesList.getScene().getWindow();
            Scene scene = new Scene(root, 1366, 768);
            stage.setScene(scene);
            stage.setTitle("Student Management");
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading student management page: " + e.getMessage(), e);
            selectedCourseLabel.setText("Error: Failed to load the student management page.");
        }
    }
}