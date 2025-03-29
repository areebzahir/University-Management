package com.FinalProject.UMS;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class EventUserController {
    @FXML private TableView<EventController> eventTableView;
    @FXML private Button registerButton;
    @FXML private Button returnButton;

    private EventManagementExcel excelManager = new EventManagementExcel();

    @FXML
    public void initialize() {
        loadEventsFromExcel();
    }

    private void loadEventsFromExcel() {
        List<EventController> events = excelManager.readEvents();
        ObservableList<EventController> observableList = FXCollections.observableArrayList(events);
        eventTableView.setItems(observableList);
    }

    @FXML
    protected void registerForEvent() {
        EventController selectedEvent = eventTableView.getSelectionModel().getSelectedItem();
        if (selectedEvent != null) {
            // In a real application, you would add the current student to the registered students list
            showAlert("Registration Successful",
                    "You have successfully registered for: " + selectedEvent.getTitle());
        } else {
            showAlert("No Event Selected", "Please select an event to register.");
        }
    }

    @FXML
    protected void onReturnButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("event-menu-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) returnButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Event Management System");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
 }

