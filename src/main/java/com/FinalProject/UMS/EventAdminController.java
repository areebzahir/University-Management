package com.FinalProject.UMS; // Declares the package where this class belongs

// Import necessary JavaFX classes
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * EventAdminController manages the event administration interface.
 * It allows administrators to add events and navigate between screens.
 */
public class EventAdminController {

    // Input fields for event details
    @FXML private TextField eventTitle;
    @FXML private TextField eventDescription;
    @FXML private TextField eventDate;
    @FXML private TextField eventLocation;

    // Button to trigger event addition
    @FXML private Button addEventButton;

    // ListView to display added events
    @FXML private ListView<EventController> eventListView;

    // Button to return to the previous menu
    @FXML private Button returnButton;

    /**
     * Adds a new event to the event list.
     * Retrieves input from text fields and creates an EventController object.
     * The new event is then added to the ListView for display.
     */
    @FXML
    protected void addEvent() {
        // Retrieve values from input fields
        String title = eventTitle.getText();
        String description = eventDescription.getText();
        String date = eventDate.getText();
        String location = eventLocation.getText();

        // Create a new EventController object using the provided details
        EventController newEventController = new EventController(title, description, date, location);

        // Add the new event to the ListView
        eventListView.getItems().add(newEventController);
    }

    /**
     * Handles the return button click event, navigating back to the event menu.
     * Loads the event-menu-view.fxml file and sets it as the new scene.
     */
    @FXML
    protected void onReturnButtonClick() {
        try {
            // Load the event menu screen from its FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("event-menu-view.fxml"));
            Parent root = loader.load();

            // Retrieve the current stage (window) from the return button
            Stage stage = (Stage) returnButton.getScene().getWindow();

            // Set a new scene with the event menu layout
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Event Management System");
            stage.show();
        } catch (IOException e) {
            // Print error details if loading fails
            e.printStackTrace();
        }
    }
}
