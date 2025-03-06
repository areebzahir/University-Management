package com.FinalProject.UMS;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class StudentManagementMenuController {
    @FXML
    private Label welcomeText;

    @FXML
    private Button backButton; // Add this line

    @FXML
    protected void onOpenSecondPageButtonClick() {
        loadPage("student-info-view.fxml", "Student Information");
    }

    @FXML
    protected void onOpenProfileButtonClick() {
        loadPage("profile-view.fxml", "Profile");
    }

    @FXML
    protected void onOpenCoursesButtonClick() {
        loadPage("courses-view.fxml", "Enrolled Courses");
    }

    @FXML
    protected void onOpenGradesButtonClick() {
        loadPage("grades-view.fxml", "Grades");
    }

    @FXML
    protected void onOpenTuitionButtonClick() {
        loadPage("tuition-view.fxml", "Tuition Status");
    }

    @FXML
    private void handleBackButton() {
        try {
            // Load the Main Screen FXML file
            Parent root = FXMLLoader.load(getClass().getResource("menu-view.fxml"));
            // Get the current stage
            Stage stage = (Stage) backButton.getScene().getWindow();
            // Set the new scene
            stage.setScene(new Scene(root));
            stage.setTitle("University Management System - Main Screen");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPage(String fxmlFile, String title) {
        try {
            System.out.println("Loading FXML file: " + fxmlFile); // Debug statement
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) welcomeText.getScene().getWindow();
            Scene scene = new Scene(root, 1920, 1080);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load FXML file: " + fxmlFile); // Debug statement
        }
    }
}