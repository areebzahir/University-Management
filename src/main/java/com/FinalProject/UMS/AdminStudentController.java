package com.FinalProject.UMS;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class AdminStudentController {

    @FXML
    private Button backButton;

    @FXML
    private void handleAddStudent() {
        loadPage("add-student-view.fxml", "Add Student");
    }

    @FXML
    private void handleEditStudent() {
        loadPage("edit-student-view.fxml", "Edit Student");
    }

    @FXML
    private void handleDeleteStudent() {
        loadPage("delete-student-view.fxml", "Delete Student");
    }

    @FXML
    private void handleViewStudentProfile() {
        loadPage("view-student-profile.fxml", "View Student Profile");
    }

    @FXML
    private void handleManageEnrollments() {
        loadPage("manage-enrollments-view.fxml", "Manage Enrollments");
    }

    @FXML
    private void handleAcademicProgressTracking() {
        loadPage("academic-progress-view.fxml", "Academic Progress Tracking");
    }

    @FXML
    private void handleTuitionManagement() {
        loadPage("tuition-management-view.fxml", "Tuition Management");
    }

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

    private void loadPage(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}