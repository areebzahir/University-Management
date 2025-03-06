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
    private Button loginButton;

    private Map<String, User> users;

    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    public void initialize() {
        users = ExcelDatabase.loadUsers();
        LOGGER.log(Level.INFO, "Loaded users: {0}", users);
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        LOGGER.log(Level.INFO, "Attempting login for user: " + username);

        User user = users.get(username);

        if (user != null) {
            if (user.authenticate(password)) {
                showAlert("Login Successful", "Welcome, " + username + "!"); // Show success message
                navigateToDashboard(user, event);
            } else {
                showAlert("Login Failed", "Incorrect password");
            }
        } else {
            showAlert("Login Failed", "User ID not found");
        }
    }

    private void navigateToDashboard(User user, ActionEvent event) {
        LOGGER.log(Level.INFO, "Navigating to dashboard for user id: " + user.getId());
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("menu-view.fxml"));
            Parent root = fxmlLoader.load();

            MenuController menuController = fxmlLoader.getController();
            String userRole = determineUserRole(user.getId());
            menuController.setUserRole(userRole);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading menu FXML: " + e.getMessage(), e);
            showAlert("Error", "Failed to load menu.");
        }
    }

    private String determineUserRole(String username) {
        if ("admin".equals(username)) {
            return "ADMIN";
        } else if ("faculty".equals(username)) {
            return "FACULTY";
        }
        else {
            return "USER";
        }
    }

    private void showAlert (String title, String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null); // Optional: Remove header text
        alert.setContentText(message);
        alert.showAndWait();
    }
}