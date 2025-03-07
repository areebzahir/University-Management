package com.FinalProject.UMS; // Declares the package for this class

// Importing required JavaFX classes
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

/**
 * DashboardController is responsible for managing the main dashboard screen of the University Management System.
 * It displays statistical data, recent activities, and upcoming events.
 */
public class DashboardController implements Initializable {

    // Labels to display summary statistics
    @FXML
    private Label totalStudentsLabel;

    @FXML
    private Label totalCoursesLabel;

    @FXML
    private Label totalFacultiesLabel;

    @FXML
    private Label totalEventsLabel;

    // VBox containers to display dynamic lists of recent activities and upcoming events
    @FXML
    private VBox recentActivitiesBox;

    @FXML
    private VBox upcomingEventsBox;

    // Back button to navigate to the previous menu
    @FXML
    private Button backButton;

    /**
     * The initialize method is automatically called when the FXML file is loaded.
     * It loads summary data, recent activities, and upcoming events into the dashboard.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadSummaryData(); // Load total students, courses, faculties, and events
        loadRecentActivities(); // Populate the recent activities section
        loadUpcomingEvents(); // Populate the upcoming events section
    }

    /**
     * Loads summary data such as total students, courses, faculties, and events.
     * In a real application, this data would typically be retrieved from a database.
     */
    private void loadSummaryData() {
        // Hardcoded values (replace with database retrieval logic if necessary)
        int totalStudents = 10;
        int totalCourses = 10;
        int totalFaculties = 5;
        int totalEvents = 2;

        // Update UI labels with retrieved values
        totalStudentsLabel.setText(String.valueOf(totalStudents));
        totalCoursesLabel.setText(String.valueOf(totalCourses));
        totalFacultiesLabel.setText(String.valueOf(totalFaculties));
        totalEventsLabel.setText(String.valueOf(totalEvents));
    }

    /**
     * Loads recent activities dynamically and adds them to the recent activities section.
     * This would normally be retrieved from a database or event log.
     */
    private void loadRecentActivities() {
        recentActivitiesBox.getChildren().add(new Text("New student registered: Alice Smith"));
        recentActivitiesBox.getChildren().add(new Text("Course 'Calculus I' added"));
        recentActivitiesBox.getChildren().add(new Text("Event 'Welcome Seminar' scheduled"));
    }

    /**
     * Loads upcoming events dynamically and adds them to the upcoming events section.
     * Events would typically be retrieved from a calendar or event management system.
     */
    private void loadUpcomingEvents() {
        upcomingEventsBox.getChildren().add(new Text("Welcome Seminar - 9/1/2025 10:00"));
        upcomingEventsBox.getChildren().add(new Text("Research Workshop - 10/5/2025 14:00"));
    }

    /**
     * Handles the back button click event, returning the user to the main menu.
     */
    @FXML
    private void handleBackButton() {
        try {
            // Load the Main Menu screen from its FXML file
            Parent root = FXMLLoader.load(getClass().getResource("menu-view.fxml"));

            // Retrieve the current stage (window) from the back button
            Stage stage = (Stage) backButton.getScene().getWindow();

            // Set a new scene with the main menu layout
            stage.setScene(new Scene(root));
            stage.setTitle("University Management System - Main Screen");
        } catch (Exception e) {
            // Print error details if the file cannot be loaded
            e.printStackTrace();
        }
    }
}
