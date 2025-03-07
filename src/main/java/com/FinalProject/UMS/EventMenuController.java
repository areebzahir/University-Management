package com.FinalProject.UMS; // Declares the package where this class is located

// Import necessary JavaFX classes
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * EventMenuController manages the event menu interface.
 * It allows users to navigate to either the Admin or User event views.
 */
public class EventMenuController {

    // Buttons for navigation
    @FXML private Button adminButton; // Button to access the admin event view
    @FXML private Button userButton;  // Button to access the user event view

    /**
     * Handles the click event for the admin button.
     * Loads the admin-event-view.fxml file and switches the scene to the Admin View.
     */
    @FXML
    protected void onAdminButtonClick() {
        try {
            // Load the Admin Event View from its FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("admin-event-view.fxml"));
            Parent root = loader.load();

            // Retrieve the current stage (window) from the admin button
            Stage stage = (Stage) adminButton.getScene().getWindow();

            // Set a new scene with the admin event view layout
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Admin View"); // Set the title for the new window
            stage.show();
        } catch (IOException e) {
            // Print error details if loading fails
            e.printStackTrace();
        }
    }

    /**
     * Handles the click event for the user button.
     * Loads the event-user-view.fxml file and switches the scene to the User View.
     */
    @FXML
    protected void onUserButtonClick() {
        try {
            // Load the User Event View from its FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("event-user-view.fxml"));
            Parent root = loader.load();

            // Retrieve the current stage (window) from the user button
            Stage stage = (Stage) userButton.getScene().getWindow();

            // Set a new scene with the user event view layout
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("User View"); // Set the title for the new window
            stage.show();
        } catch (IOException e) {
            // Print error details if loading fails
            e.printStackTrace();
        }
    }
}
