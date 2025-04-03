package com.FinalProject.UMS;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorMessageLabel;
    @FXML
    private Button loginButton;

    // Map to store user information from the Excel database
    private Map<String, User> users;

    // Logger for logging information and errors
    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    // Initialize method to load users from the Excel database
    public void initialize() {
        users = ExcelDatabase.loadUsers(); // Load users from external database
        if (users != null) {
            LOGGER.log(Level.INFO, "Loaded {0} users", users.size()); // Log number of users loaded
        } else {
            LOGGER.log(Level.SEVERE, "Failed to load users from Excel"); // Log an error if loading fails
        }
    }

    // Method to handle login logic when the login button is clicked
    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim(); // Get the entered username
        String password = passwordField.getText().trim(); // Get the entered password


        LOGGER.log(Level.INFO, "Attempting login for user: {0}", username); // Log the login attempt


        // Check for hardcoded admin login first
        if ("a".equals(username) && "a".equals(password)) {
            LOGGER.info("Admin login successful"); // Log admin login success

            // Create a User object for the admin
            User adminUser = new User();
            adminUser.setUsername("a");
            adminUser.setRole("ADMIN");

            // Store the admin User object in GlobalState
            GlobalState.getInstance().setLoggedInUser(adminUser);

            navigateToMenu("ADMIN", event); // Navigate to the admin dashboard setting the role
            return;
        }

        // Ensure users were loaded from the database before proceeding
        if (users == null) {
            showPopup("Error", "User database not available!", Alert.AlertType.ERROR); // Show error if users are null
            return;
        }

        // Check if the entered username exists in the user database
        User user = users.get(username);

        // If user exists, authenticate their password
        if (user != null) {
            if (user.authenticate(password)) { // If password is correct
                LOGGER.log(Level.INFO, "User {0} authenticated successfully", username); // Log successful authentication
                //  showPopup("Login Successful", "Welcome, " + username + "!", Alert.AlertType.INFORMATION); // Show success popup

                // Store the logged-in user in the global state
                GlobalState.getInstance().setLoggedInUser(user);
                System.out.println("User set in GlobalState: " + GlobalState.getInstance().getLoggedInUser());//DEBUG

                // After successful auth:
                navigateToMenu(user.getRole(), event); // Navigate to the user dashboard, passing the role
            } else {
                LOGGER.warning("Incorrect password for user: " + username); // Log incorrect password attempt
                errorMessageLabel.setText("Invalid password. Please try again."); // Show error message on the UI
                showPopup("Login Failed", "Incorrect password", Alert.AlertType.ERROR); // Show error popup
            }
        } else {
            LOGGER.warning("User ID not found: " + username); // Log if user is not found in the database
            errorMessageLabel.setText("User ID not found!"); // Show error message on the UI
            showPopup("Login Failed", "User ID not found", Alert.AlertType.ERROR); // Show error popup
        }
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    // Method to navigate to the  menu after successful login, passing the role
    private void navigateToMenu(String role, ActionEvent event) {
        LOGGER.log(Level.INFO, "Navigating to menu for role: {0}", role); // Log the navigation attempt
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("menu-view.fxml"));
            Parent root = fxmlLoader.load(); // Load the menu FXML file

            MenuController menuController = fxmlLoader.getController(); // Get controller for the menu
            menuController.setUserRole(role); // Set the user role in the menu controller


            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root)); // Set the new scene for the stage
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading menu FXML: " + e.getMessage(), e); // Log error if loading menu fails
            showPopup("Error", "Failed to load menu.", Alert.AlertType.ERROR); // Show error popup
        }
    }


    // Utility method to display pop-up messages for login success or failure
    private void showPopup(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType); // Create an alert with the specified type
        alert.setTitle(title); // Set the title of the alert
        alert.setHeaderText(null); // Set header text to null
        alert.setContentText(message); // Set the content message of the alert
        alert.showAndWait(); // Display the alert and wait for user interaction
    }

    // New method to navigate to the profile view and pass the student data
    @FXML
    private void navigateToProfileView(String studentId, ActionEvent event) {
        LOGGER.log(Level.INFO, "Navigating to ProfileView for student id: {0}", studentId);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("profile-view.fxml"));

            Parent root = loader.load();

            ProfileController profileController = loader.getController();
            profileController.setLoggedInStudentId(studentId);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Student Profile");
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading profile FXML: " + e.getMessage(), e);
            showPopup("Error", "Failed to load profile FXML.", Alert.AlertType.ERROR);
        }
    }
}