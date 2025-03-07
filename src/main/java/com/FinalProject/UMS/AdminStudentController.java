package com.FinalProject.UMS; // Declaring the package for this class

// Importing necessary JavaFX classes for the GUI
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class AdminStudentController {

    // FXML annotation marks this variable as linked to a Button in the FXML layout
    @FXML
    private Button backButton;

    // Method to handle adding a new student
    @FXML
    private void handleAddStudent() {
        // Calls the method to load the "add student" view
        loadPage("add-student-view.fxml", "Add Student");
    }

    // Method to handle editing a student's information
    @FXML
    private void handleEditStudent() {
        // Loads the "edit student" view
        loadPage("edit-student-view.fxml", "Edit Student");
    }

    // Method to handle deleting a student
    @FXML
    private void handleDeleteStudent() {
        // Loads the "delete student" view
        loadPage("delete-student-view.fxml", "Delete Student");
    }

    // Method to handle viewing a student's profile
    @FXML
    private void handleViewStudentProfile() {
        // Loads the "view student profile" view
        loadPage("view-student-profile.fxml", "View Student Profile");
    }

    // Method to handle managing student enrollments
    @FXML
    private void handleManageEnrollments() {
        // Loads the "manage enrollments" view
        loadPage("manage-enrollments-view.fxml", "Manage Enrollments");
    }

    // Method to handle tracking academic progress
    @FXML
    private void handleAcademicProgressTracking() {
        // Loads the "academic progress tracking" view
        loadPage("academic-progress-view.fxml", "Academic Progress Tracking");
    }

    // Method to handle managing tuition fees
    @FXML
    private void handleTuitionManagement() {
        // Loads the "tuition management" view
        loadPage("tuition-management-view.fxml", "Tuition Management");
    }

    // Method to handle the back button action
    @FXML
    private void handleBackButton() {
        try {
            // Loads the main menu view when back button is clicked
            Parent root = FXMLLoader.load(getClass().getResource("menu-view.fxml"));
            // Retrieves the current window (stage) from the back button's scene
            Stage stage = (Stage) backButton.getScene().getWindow();
            // Sets a new scene with the main menu and updates the stage
            stage.setScene(new Scene(root));
            stage.setTitle("University Management System - Main Screen");
        } catch (Exception e) {
            // Handles any potential exceptions (e.g., file not found)
            e.printStackTrace();
        }
    }

    // Helper method to load a new view page dynamically
    private void loadPage(String fxmlFile, String title) {
        try {
            // Loads the specified FXML file for the view
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            // Creates the root node from the FXML file
            Parent root = loader.load();
            // Creates a new stage (window) for the new view
            Stage stage = new Stage();
            // Sets the scene and title of the new window
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            // Displays the new stage (window)
            stage.show();
        } catch (Exception e) {
            // Handles any potential exceptions (e.g., file not found)
            e.printStackTrace();
        }
    }
}
