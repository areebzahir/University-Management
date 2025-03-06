package com.FinalProject.UMS;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private Label totalStudentsLabel;

    @FXML
    private Label totalCoursesLabel;

    @FXML
    private Label totalFacultiesLabel;

    @FXML
    private Label totalEventsLabel;

    @FXML
    private VBox recentActivitiesBox;

    @FXML
    private VBox upcomingEventsBox;

    @FXML
    private Button backButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load data from the dataset
        loadSummaryData();
        loadRecentActivities();
        loadUpcomingEvents();
    }

    private void loadSummaryData() {
        // Count total students
        int totalStudents = 10; // From your dataset

        // Count total courses
        int totalCourses = 10; // From your dataset

        // Count total faculties
        int totalFaculties = 5; // From your dataset

        // Count total events
        int totalEvents = 2; // From your dataset

        // Update labels
        totalStudentsLabel.setText(String.valueOf(totalStudents));
        totalCoursesLabel.setText(String.valueOf(totalCourses));
        totalFacultiesLabel.setText(String.valueOf(totalFaculties));
        totalEventsLabel.setText(String.valueOf(totalEvents));
    }

    private void loadRecentActivities() {
        // Add recent activities from your dataset
        recentActivitiesBox.getChildren().add(new Text("New student registered: Alice Smith"));
        recentActivitiesBox.getChildren().add(new Text("Course 'Calculus I' added"));
        recentActivitiesBox.getChildren().add(new Text("Event 'Welcome Seminar' scheduled"));
    }

    private void loadUpcomingEvents() {
        // Add upcoming events from your dataset
        upcomingEventsBox.getChildren().add(new Text("Welcome Seminar - 9/1/2025 10:00"));
        upcomingEventsBox.getChildren().add(new Text("Research Workshop - 10/5/2025 14:00"));
    }

    @FXML
    private void handleBackButton() {
        try {
            // Load the Main Screen FXML file
            Parent root = FXMLLoader.load(getClass().getResource("menu-view.fxml"));
           // FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuController.fxml"));

            // Get the current stage
            Stage stage = (Stage) backButton.getScene().getWindow();

            // Set the new scene
            stage.setScene(new Scene(root));
            stage.setTitle("University Management System - Main Screen");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}