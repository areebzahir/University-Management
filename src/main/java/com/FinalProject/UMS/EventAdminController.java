package com.FinalProject.UMS;

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
 */
public class EventAdminController {

    // Input fields for event details
    @FXML private TextField eventCodeField;
    @FXML private TextField eventTitle;
    @FXML private TextField eventDescription;
    @FXML private TextField eventDate;
    @FXML private TextField eventLocation;
    @FXML private TextField eventImageUrl;
    @FXML private TextField eventCapacity;
    @FXML private TextField eventCost;

    // Button to trigger event addition
    @FXML private Button addEventButton;

    // ListView to display added events
    @FXML private ListView<EventController> eventListView;

    // Button to return to the previous menu
    @FXML private Button returnButton;

    /**
     * Adds a new event to the event list.
     */
    @FXML
    protected void addEvent() {
        // Retrieve values from input fields
        String eventCode = eventCodeField.getText();
        String title = eventTitle.getText();
        String description = eventDescription.getText();
        String date = eventDate.getText();
        String location = eventLocation.getText();
        String imageUrl = eventImageUrl.getText();
        int capacity = Integer.parseInt(eventCapacity.getText());
        double cost = Double.parseDouble(eventCost.getText());

        // Create a new EventController object
        EventController newEvent = new EventController(eventCode, title, description, date, location, imageUrl, capacity, cost);

        // Add the new event to the ListView
        eventListView.getItems().add(newEvent);

        // Clear fields after adding the event
        clearFields();
    }

    /**
     * Clears all input fields.
     */
    private void clearFields() {
        eventCodeField.clear();
        eventTitle.clear();
        eventDescription.clear();
        eventDate.clear();
        eventLocation.clear();
        eventImageUrl.clear();
        eventCapacity.clear();
        eventCost.clear();
    }

    /**
     * Handles the return button click event, navigating back to the event menu.
     */
    @FXML
    protected void onReturnButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("event-menu-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) returnButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Event Management System");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}