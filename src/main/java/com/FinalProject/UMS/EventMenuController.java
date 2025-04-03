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

public class EventMenuController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(EventMenuController.class.getName());
    private String userRole;

    @FXML
    private Button adminButton;
    @FXML
    private Button userButton;
    @FXML
    private Button returnButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Get role from GlobalState if available
        if (GlobalState.getInstance().getLoggedInUser() != null) {
            this.userRole = GlobalState.getInstance().getLoggedInUser().getRole();
            adjustVisibilityBasedOnRole();
       }
    }

    public void setUserRole(String role) {
        this.userRole = role;
        adjustVisibilityBasedOnRole();
        LOGGER.info("EventMenuController User role set to: " + role);
    }

    private void adjustVisibilityBasedOnRole() {
        LOGGER.info("Adjusting visibility for role: " + userRole); // Add this line

        if ("USER".equals(userRole)) {
            LOGGER.info("Hiding admin button, showing user button"); // Add this line
            adminButton.setDisable(true);
            adminButton.setVisible(false);
            userButton.setDisable(false);
            userButton.setVisible(true);
        } else if ("ADMIN".equals(userRole)) {
            LOGGER.info("Showing admin button, hiding user button"); // Add this line
            adminButton.setDisable(false);
            adminButton.setVisible(true);
            userButton.setDisable(true);
            userButton.setVisible(false);
        }
        returnButton.setDisable(false);
        returnButton.setVisible(true);
    }

    @FXML
    protected void onAdminButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("admin-event-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) adminButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Admin View");
            stage.show();
        } catch (IOException e) {
            LOGGER.severe("Failed to load admin event view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    protected void onUserButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("event-user-view.fxml"));
            Parent root = loader.load();
            EventUserController controller = loader.getController();
            controller.setCurrentUser(GlobalState.getInstance().getLoggedInUser());

            Stage stage = (Stage) userButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("User View - " + GlobalState.getInstance().getLoggedInUser().getName());
            stage.show();
        } catch (IOException e) {
            LOGGER.severe("Failed to load user event view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleBackButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("menu-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) returnButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("University Management System - Main Screen");
            stage.show();
        } catch (IOException e) {
            LOGGER.severe("Failed to load main menu view: " + e.getMessage());
            e.printStackTrace();
        }
    }
}