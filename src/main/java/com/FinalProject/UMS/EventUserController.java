package com.FinalProject.UMS;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the Event User view.
 * This class handles user interactions with the Event User interface,
 * including registering for events and returning to the Event Menu.
 */
public class EventUserController {
    // FXML components injected from the corresponding FXML file
    @FXML private TableView<EventController> eventTableView; // TableView to display available events
    @FXML private Button registerButton; // Button to register for a selected event
    @FXML private Button returnButton; // Button to return to the Event Menu

    // List to keep track of registered events
    private List<EventController> registeredEventControllers = new ArrayList<>();

    /**
     * Initializes the TableView with example events when the view is loaded.
     * This method is automatically called by JavaFX after the FXML file is loaded.
     */
    @FXML
    public void initialize() {
        // Create a list of EventController objects
        ObservableList<EventController> events = FXCollections.observableArrayList(
                new EventController("EV001", "Welcome Seminar", "Orientation week", "Auditorium", "2025-09-01 10:00", 100, 0.0),
                new EventController("EV002", "Research Workshop", "Graduate workshop", "Lab 301", "2025-10-05 14:00", 50, 20.0)
        );

        // Set the items in the TableView
        eventTableView.setItems(events);
    }

    /**
     * Handles the event when the "Register" button is clicked.
     * Registers the user for the selected event and displays a confirmation message.
     */
    @FXML
    protected void registerForEvent() {
        // Get the selected event from the TableView
        EventController selectedEventController = eventTableView.getSelectionModel().getSelectedItem();
        if (selectedEventController != null) {
            // Check if the user is already registered for the event
            if (!registeredEventControllers.contains(selectedEventController)) {
                // Add the event to the registered events list
                registeredEventControllers.add(selectedEventController);
                // Show a success message
                showAlert("Registration Successful", "You have successfully registered for: " + selectedEventController.getTitle());
            } else {
                // Show a message if the user is already registered
                showAlert("Already Registered", "You are already registered for this event.");
            }
        } else {
            // Show a message if no event is selected
            showAlert("No Event Selected", "Please select an event to register.");
        }
    }

    /**
     * Handles the event when the "Return" button is clicked.
     * Returns the user to the Event Menu view (event-menu-view.fxml).
     */
    @FXML
    protected void onReturnButtonClick() {
        try {
            // Load the Event Menu view (event-menu-view.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("event-menu-view.fxml"));
            Parent root = loader.load();

            // Get the current stage (window) using the returnButton's scene
            Stage stage = (Stage) returnButton.getScene().getWindow();

            // Set the new scene with the Event Menu view
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Event Management System"); // Set the window title
            stage.show(); // Display the new scene
        } catch (IOException e) {
            e.printStackTrace(); // Print the stack trace if an error occurs
        }
    }

    /**
     * Displays an alert dialog with the specified title and message.
     *
     * @param title   The title of the alert dialog.
     * @param message The message to display in the alert dialog.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title); // Set the alert title
        alert.setHeaderText(null); // Remove the header text
        alert.setContentText(message); // Set the alert message
        alert.showAndWait(); // Display the alert and wait for user interaction
    }
}