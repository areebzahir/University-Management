package com.FinalProject.UMS;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the Event User view.
 */
public class EventUserController {
    @FXML private ListView<EventController> eventListView;
    @FXML private Button registerButton;
    @FXML private Button returnButton;

    private List<EventController> registeredEvents = new ArrayList<>();

    /**
     * Initializes the ListView with example events.
     */
    @FXML
    public void initialize() {
        eventListView.getItems().addAll(
                new EventController("E001", "Tech Conference 2023", "Annual technology conference", "2023-11-15", "San Francisco, CA", "image1.jpg", 100, 50.0),
                new EventController("E002", "Music Festival", "Outdoor music festival", "2023-12-01", "Austin, TX", "image2.jpg", 200, 75.0)
        );
    }

    /**
     * Registers a student for the selected event.
     */
    @FXML
    protected void registerForEvent() {
        EventController selectedEvent = eventListView.getSelectionModel().getSelectedItem();
        if (selectedEvent != null) {
            if (!registeredEvents.contains(selectedEvent)) {
                registeredEvents.add(selectedEvent);
                showAlert("Registration Successful", "You have registered for: " + selectedEvent.getTitle());
            } else {
                showAlert("Already Registered", "You are already registered for this event.");
            }
        } else {
            showAlert("No Event Selected", "Please select an event to register.");
        }
    }

    /**
     * Handles the return button click event.
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

    /**
     * Displays an alert dialog.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}