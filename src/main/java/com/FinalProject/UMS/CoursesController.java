package com.FinalProject.UMS;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CoursesController {
    @FXML
    private TableView<CourseRegistration> coursesTable; // TableView to display enrolled courses

    @FXML
    private TableColumn<CourseRegistration, String> studentIdColumn; // Column for Student ID
    @FXML
    private TableColumn<CourseRegistration, String> courseCodeColumn; // Column for Course Code
    @FXML
    private TableColumn<CourseRegistration, String> courseNameColumn; // Column for Course Name
    @FXML
    private TableColumn<CourseRegistration, String> semesterColumn; // Column for Semester
    @FXML
    private TableColumn<CourseRegistration, String> academicLevelColumn; // Column for Academic Level
    @FXML
    private TableColumn<CourseRegistration, String> currentSemesterColumn; // Column for Current Semester

    private String studentId; // Field to store the student ID

    private static final Logger LOGGER = Logger.getLogger(CoursesController.class.getName());

    @FXML
    public void initialize() {
        // Set up the TableView columns
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        courseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        semesterColumn.setCellValueFactory(new PropertyValueFactory<>("semester"));
        academicLevelColumn.setCellValueFactory(new PropertyValueFactory<>("academicLevel"));
        currentSemesterColumn.setCellValueFactory(new PropertyValueFactory<>("currentSemester"));
    }

    /**
     * Loads test data into the TableView for demonstration purposes.
     */
    public void loadTestData() {
        // Create test values
        List<CourseRegistration> testCourses = List.of(
                new CourseRegistration("S20250001", "ENG101", "English 101", "Fall 2025", "Undergraduate", "Fall 2025")
        );

        // Convert the list to an ObservableList
        ObservableList<CourseRegistration> courseData = FXCollections.observableArrayList(testCourses);

        // Set the data to the TableView
        coursesTable.setItems(courseData);
    }

    // Handle return button click
    @FXML
    protected void onReturnButtonClick() {
        try {
            LOGGER.info("Return button clicked. Loading student management page.");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("studentmanagecontroller.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) coursesTable.getScene().getWindow();
            Scene scene = new Scene(root, 1366, 768);
            stage.setScene(scene);
            stage.setTitle("Student Management");
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading student management page: " + e.getMessage(), e);
        }
    }
}