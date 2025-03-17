package com.FinalProject.UMS;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController {
    // FXML elements representing the input fields and buttons in the login form
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

        // Check for admin login first (hardcoded check for admin credentials)
        if ("admin@uoguelph.ca".equals(username) && "admin123".equals(password)) {
            LOGGER.info("Admin login successful"); // Log admin login success
            showPopup("Login Successful", "Welcome, Admin!", Alert.AlertType.INFORMATION); // Show success popup
            navigateToAdminDashboard(event); // Navigate to the admin dashboard
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
                showPopup("Login Successful", "Welcome, " + username + "!", Alert.AlertType.INFORMATION); // Show success popup
                navigateToDashboard(user, event); // Navigate to the user dashboard
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

    // Method to navigate to the user dashboard after successful login
    private void navigateToDashboard(User user, ActionEvent event) {
        LOGGER.log(Level.INFO, "Navigating to dashboard for user id: {0}", user.getId()); // Log the navigation attempt
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("menu-view.fxml"));
            Parent root = fxmlLoader.load(); // Load the menu FXML file

            MenuController menuController = fxmlLoader.getController(); // Get controller for the menu
            String userRole = determineUserRole(user.getId()); // Determine the role of the user (e.g., admin, faculty)
            menuController.setUserRole(userRole); // Set the user role in the menu controller

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root)); // Set the new scene for the stage
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading menu FXML: " + e.getMessage(), e); // Log error if loading menu fails
            showPopup("Error", "Failed to load menu.", Alert.AlertType.ERROR); // Show error popup
        }
    }

    // Method to navigate to the admin dashboard after successful admin logjin
    private void navigateToAdminDashboard(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("menu-view.fxml"));
            Parent root = fxmlLoader.load(); // Load the menu FXML file
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root)); // Set the new scene for the stage
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading Admin Dashboard: " + e.getMessage(), e); // Log error if loading fails
            showPopup("Error", "Failed to load admin dashboard.", Alert.AlertType.ERROR); // Show error popup
        }
    }

    // Method to determine the user role based on the username
    private String determineUserRole(String username) {
        return switch (username) { // Switch to determine the user role
            case "admin" -> "ADMIN"; // If username is "admin", return "ADMIN"
            case "faculty" -> "FACULTY"; // If username is "faculty", return "FACULTY"
            default -> "USER"; // Default case for regular users
        };
    }

    // Utility method to display pop-up messages for login success or failure
    private void showPopup(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType); // Create an alert with the specified type
        alert.setTitle(title); // Set the title of the alert
        alert.setHeaderText(null); // Set header text to null
        alert.setContentText(message); // Set the content message of the alert
        alert.showAndWait(); // Display the alert and wait for user interaction
    }
}
