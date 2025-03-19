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

public class CoursesController {
    @FXML
    private ListView<String> coursesList; // ListView to display enrolled courses

    @FXML
    private Label selectedCourseLabel; // Label to display the selected course

    private String studentId; // Field to store the student ID

    @FXML
    public void initialize() {
        // Add event listener for course selection
        coursesList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedCourseLabel.setText("Selected Course: " + newValue);
            }
        });
    }

    // Set the student ID dynamically
    public void setStudentId(String studentId) {
        this.studentId = studentId;
        loadEnrolledCourses(); // Load courses when the student ID is set
    }

    // Load enrolled courses for the student from Excel
    private void loadEnrolledCourses() {
        if (studentId != null && !studentId.isEmpty()) {
            List<String> enrolledCourses = ExcelDatabase.loadEnrolledCourses(studentId);
            coursesList.getItems().clear(); // Clear existing items
            coursesList.getItems().addAll(enrolledCourses); // Add enrolled courses to the ListView
        } else {
            selectedCourseLabel.setText("Error: Student ID is not set.");
        }
    }

    // Handle return button click
    @FXML
    protected void onReturnButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("studentmanagecontroller.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) coursesList.getScene().getWindow();
            Scene scene = new Scene(root, 1366, 768);
            stage.setScene(scene);
            stage.setTitle("Student Management");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            selectedCourseLabel.setText("Error: Failed to load the student management page.");
        }
    }
}