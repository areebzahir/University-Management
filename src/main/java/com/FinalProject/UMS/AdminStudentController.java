package com.FinalProject.UMS;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminStudentController {

    @FXML
    private Button backButton;

    @FXML
    private void handleAddStudent() {
        switchToView("add-student-view.fxml", "Add Student");
    }

    @FXML
    private void handleViewStudentProfile() {
        switchToView("view-student-profile.fxml", "View Student Profile");
    }

    @FXML
    private void handleManageEnrollments() {
        switchToView("manage-enrollments-view.fxml", "Manage Enrollments");
    }

    @FXML
    private void handleAcademicProgressTracking() {
        switchToView("academic-progress-view.fxml", "Academic Progress Tracking");
    }

    @FXML
    private void handleTuitionManagement() {
        switchToView("tuition-management-view.fxml", "Tuition Management");
    }

    @FXML
    private void handleBackButton() {
        switchToView("menu-view.fxml", "University Management System - Main Screen");
    }

    private void switchToView(String fxmlFile, String title) {
        try {
            // Get reference to current stage
            Stage currentStage = (Stage) backButton.getScene().getWindow();

            // Load new view
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));

            // Replace current scene content
            currentStage.setScene(new Scene(root));
            currentStage.setTitle(title);

        } catch (IOException e) {
            System.err.println("Error loading view: " + fxmlFile);
            e.printStackTrace();
        }
    }
}