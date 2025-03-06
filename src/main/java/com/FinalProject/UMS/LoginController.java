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
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorMessageLabel;
    @FXML
    private Button loginButton;

    private Map<String, User> users;
    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    public void initialize() {
        users = ExcelDatabase.loadUsers();
        if (users != null) {
            LOGGER.log(Level.INFO, "Loaded {0} users", users.size());
        } else {
            LOGGER.log(Level.SEVERE, "Failed to load users from Excel");
        }
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        LOGGER.log(Level.INFO, "Attempting login for user: {0}", username);

        // Check for admin login first
        if ("admin@uoguelph.ca".equals(username) && "admin123".equals(password)) {
            LOGGER.info("Admin login successful");
            showPopup("Login Successful", "Welcome, Admin!", Alert.AlertType.INFORMATION);
            navigateToAdminDashboard(event);
            return;
        }

        // Ensure users were loaded
        if (users == null) {
            showPopup("Error", "User database not available!", Alert.AlertType.ERROR);
            return;
        }

        User user = users.get(username);

        if (user != null) {
            if (user.authenticate(password)) {
                LOGGER.log(Level.INFO, "User {0} authenticated successfully", username);
                showPopup("Login Successful", "Welcome, " + username + "!", Alert.AlertType.INFORMATION);
                navigateToDashboard(user, event);
            } else {
                LOGGER.warning("Incorrect password for user: " + username);
                errorMessageLabel.setText("Invalid password. Please try again.");
                showPopup("Login Failed", "Incorrect password", Alert.AlertType.ERROR);
            }
        } else {
            LOGGER.warning("User ID not found: " + username);
            errorMessageLabel.setText("User ID not found!");
            showPopup("Login Failed", "User ID not found", Alert.AlertType.ERROR);
        }
    }

    private void navigateToDashboard(User user, ActionEvent event) {
        LOGGER.log(Level.INFO, "Navigating to dashboard for user id: {0}", user.getId());
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("menu-view.fxml"));
            Parent root = fxmlLoader.load();

            MenuController menuController = fxmlLoader.getController();
            String userRole = determineUserRole(user.getId());
            menuController.setUserRole(userRole);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading menu FXML: " + e.getMessage(), e);
            showPopup("Error", "Failed to load menu.", Alert.AlertType.ERROR);
        }
    }

    private void navigateToAdminDashboard(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("menu-view.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading Admin Dashboard: " + e.getMessage(), e);
            showPopup("Error", "Failed to load admin dashboard.", Alert.AlertType.ERROR);
        }
    }

    private String determineUserRole(String username) {
        return switch (username) {
            case "admin" -> "ADMIN";
            case "faculty" -> "FACULTY";
            default -> "USER";
        };
    }

    private void showPopup(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}