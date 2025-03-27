package com.FinalProject.UMS;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

// Controller for managing student-related views in the University Management System
public class StudentManagementMenuController {

    @FXML
    private Label welcomeText;

    @FXML
    private Button backButton;

    private String loggedInStudentId; // Store the logged-in student's ID

    public void setLoggedInStudentId(String studentId) {
        this.loggedInStudentId = studentId;
        // You can also load the student's name and display a personalized welcome message
        // For example:
        // Student student = ExcelDataManager.getStudentById(studentId);
        // if (student != null) {
        //     welcomeText.setText("Welcome, " + student.getName() + "!");
        // } else {
        //     welcomeText.setText("Welcome!");
        // }
    }

    // Handles the button click to open the second page (Student Information)
    @FXML
    protected void onOpenSecondPageButtonClick() {
        loadPage("student-info-view.fxml", "Student Information");
    }

    // Handles the button click to open the profile view
    @FXML
    protected void onOpenProfileButtonClick() {
        loadPage("profile-view.fxml", "Profile");
    }

    // Handles the button click to open the courses view
    @FXML
    protected void onOpenCoursesButtonClick() {
        loadPage("courses-view.fxml", "Enrolled Courses");
    }

    // Handles the button click to open the grades view
    @FXML
    protected void onOpenGradesButtonClick() {
        loadPage("grades-view.fxml", "Grades");
    }

    // Handles the button click to open the tuition status view
    @FXML
    protected void onOpenTuitionButtonClick() { loadPage("tuition-view.fxml", "Tuition"); }

        @FXML
    protected void onOpenAdminDashboardButtonClick() {
        loadPage("admin-student-view.fxml", "Admin Dashboard");
    }

    // Handles the back button action to go back to the main menu
    @FXML
    private void handleBackButton() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("menu-view.fxml"));
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("University Management System - Main Screen");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper method to load different pages based on the FXML file and title
    private void loadPage(String fxmlFile, String title) {
        try {
            System.out.println("Loading FXML file: " + fxmlFile); // Debug statement
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            // Pass the loggedInStudentId to the controllers of the loaded pages
            Object controller = loader.getController();
            if (controller instanceof ProfileController) {
                ((ProfileController) controller).setLoggedInStudentId(loggedInStudentId);
            } else if (controller instanceof CourseController) {
                ((CourseController) controller).setLoggedInStudentId(loggedInStudentId);
            }


            Stage stage = (Stage) welcomeText.getScene().getWindow();
            Scene scene = new Scene(root, 1920, 1080); // Set scene size to 1920, 1080
            stage.setScene(scene);
            stage.setTitle(title); // Set the window title based on the page
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load FXML file: " + fxmlFile); // Debug statement for errors
        }
    }
}//