package com.FinalProject.UMS;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.io.IOException;

public class WCourseController {

    @FXML
    private TextField txtCourseCode;

    @FXML
    private TextField txtCourseName;

    @FXML
    private TextField txtSubject;

    @FXML
    private Spinner<Integer> spnSection;

    @FXML
    private TextField txtTeacher;

    @FXML
    private Spinner<Integer> spnCapacity;

    @FXML
    private DatePicker dpExamDate;

    @FXML
    private TextField txtLocation;

    @FXML
    private ListView<WCourse> courseListView;

    private String userRole; // Add userRole field

    @FXML
    public void initialize() {
        // Hardcoded test values (no arrays or external data)
        WCourse course1 = new WCourse("C001", "Calculus I", "MATH001", "Section 1", 30, "Mon/Wed 9-11 AM", "2025-12-15", "Room 101", "Dr. Alan Turing");
        WCourse course2 = new WCourse("C002", "Literature Basics", "ENG101", "Section 1", 25, "Tue/Thu 10-12 PM", "2025-12-16", "Room 102", "Prof. Emily Brontë");

        // Add test values directly to the list
        ObservableList<WCourse> courseData = FXCollections.observableArrayList();
        courseData.add(course1);
        courseData.add(course2);
        courseListView.setItems(courseData);

        adjustVisibilityBasedOnRole(); // Adjust UI based on user role
    }

    public void setUserRole(String role) {
        this.userRole = role;
        adjustVisibilityBasedOnRole();
    }

    private void adjustVisibilityBasedOnRole() {
        if ("USER".equals(userRole)) {
            // Disable editing for USER role
            courseListView.setEditable(false);
        }
    }

    @FXML
    private void addCourse() {
        // Add course logic here
    }

    @FXML
    private void deleteCourse() {
        // Delete course logic here
    }

    @FXML
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("menu-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) courseListView.getScene().getWindow();
            Scene scene = new Scene(root, 1366, 768);
            stage.setScene(scene);
            stage.setTitle("Main Menu");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Getter for courseListView (added for testing)
    public ListView<WCourse> getCourseListView() {
        return courseListView;
    }

    // Getter for userRole (added for testing)
    public String getUserRole() {
        return userRole;
    }
}