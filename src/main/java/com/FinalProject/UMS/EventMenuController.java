

package com.FinalProject.UMS;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;


/**
 * EventMenuController manages the event menu interface.
 * It allows users to navigate to either the Admin or User event views based on their role.
 */
public class EventMenuController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(EventMenuController.class.getName());
    private String userRole;


    // Buttons for navigation
    @FXML private Button adminButton; // Button to access the admin event view
    @FXML private Button userButton;  // Button to access the user event view
    @FXML private Button returnButton;  // Button to return to the main menu


    /**
     * Initializes the controller. This method is automatically called after FXML loading.
     * @param location The location used to resolve relative paths for the root object, or null if not known.
     * @param resources The resources used to localize the root object, or null if not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set initial visibility based on role if it's already set
        if (userRole != null) {
            adjustVisibilityBasedOnRole();
        }
    }


    /**
     * Sets the user role and adjusts button visibility accordingly.
     * @param role The role of the user (ADMIN or USER)
     */
    public void setUserRole(String role) {
        this.userRole = role;
        adjustVisibilityBasedOnRole();
        LOGGER.info("EventMenuController User role set to: " + role);
    }


    /**
     * Adjusts the visibility of buttons based on the user's role.
     */
    private void adjustVisibilityBasedOnRole() {
        if ("USER".equals(userRole)) {
            // Show only user button for regular users
            adminButton.setDisable(true);
            adminButton.setVisible(false);


            userButton.setDisable(false);
            userButton.setVisible(true);
        } else {
            // Show only admin button for admin users
            adminButton.setDisable(false);
            adminButton.setVisible(true);


            userButton.setDisable(true);
            userButton.setVisible(false);
        }


        // Return button should always be visible
        returnButton.setDisable(false);
        returnButton.setVisible(true);
    }


    /**
     * Handles the click event for the admin button.
     * Loads the admin-event-view.fxml file and switches the scene to the Admin View.
     */
    @FXML
    protected void onAdminButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("admin-event-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) adminButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Admin View");
            stage.show();
        } catch (IOException e) {
            LOGGER.severe("Failed to load admin event view: " + e.getMessage());
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("event-user-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) userButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("User View");
            stage.show();
        } catch (IOException e) {
            LOGGER.severe("Failed to load user event view: " + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * Handles the click event for the back button.
     * Loads the main menu view (menu-view.fxml) and switches the scene to the Main Menu.
     */
    @FXML
    protected void handleBackButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("menu-view.fxml"));
            Parent root = loader.load();


            // Pass the user role back to the main menu
            Object controller = loader.getController();
            if (controller instanceof EventMenuController) {  // Changed from EventMenuController to MainMenuController
                ((EventMenuController) controller).setUserRole(userRole);
            }


            Stage stage = (Stage) returnButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("University Management System - Main Screen");
            stage.show();
        } catch (IOException e) {
            LOGGER.severe("Failed to load main menu view: " + e.getMessage());
            e.printStackTrace();
        }
    }
}






