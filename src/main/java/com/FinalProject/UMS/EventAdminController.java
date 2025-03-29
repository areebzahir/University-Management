package com.FinalProject.UMS;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

public class EventAdminController {
    @FXML private TextField eventCodeField;
    @FXML private TextField eventTitle;
    @FXML private TextField eventDescription;
    @FXML private TextField eventDate;
    @FXML private TextField eventLocation;
    @FXML private TextField eventImageUrl;
    @FXML private TextField eventCapacity;
    @FXML private TextField eventCost;

    @FXML private TableView<EventController> eventTableView;
    @FXML private TableColumn<EventController, String> eventCodeColumn;
    @FXML private TableColumn<EventController, String> eventNameColumn;
    @FXML private TableColumn<EventController, String> descriptionColumn;
    @FXML private TableColumn<EventController, String> locationColumn;
    @FXML private TableColumn<EventController, String> dateColumn;
    @FXML private TableColumn<EventController, Integer> capacityColumn;
    @FXML private TableColumn<EventController, Double> costColumn;
    @FXML private TableColumn<EventController, String> headerImageColumn;
    @FXML private TableColumn<EventController, String> registeredStudentsColumn;

    @FXML private Button addEventButton;
    @FXML private Button editEventButton;
    @FXML private Button deleteEventButton;
    @FXML private Button returnButton;

    private ObservableList<EventController> eventList = FXCollections.observableArrayList();
    private EventManagementExcel excelManager = new EventManagementExcel();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadEventsFromExcel();
    }

    private void setupTableColumns() {
        eventCodeColumn.setCellValueFactory(new PropertyValueFactory<>("eventCode"));
        eventNameColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        capacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        costColumn.setCellValueFactory(new PropertyValueFactory<>("cost"));
        headerImageColumn.setCellValueFactory(new PropertyValueFactory<>("headerImage"));
        registeredStudentsColumn.setCellValueFactory(new PropertyValueFactory<>("registeredStudents"));

        eventTableView.setItems(eventList);
    }

    private void loadEventsFromExcel() {
        eventList.clear();
        eventList.addAll(excelManager.readEvents());
    }

    @FXML
    protected void addEvent() {
        try {
            EventController newEvent = createEventFromFields();
            if (newEvent != null) {
                excelManager.addEvent(newEvent);
                eventList.add(newEvent);
                clearFields();
                showAlert("Success", "Event added successfully!");
            }
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Please enter valid numbers for capacity and cost.");
        }
    }

    @FXML
    protected void editEvent() {
        EventController selectedEvent = eventTableView.getSelectionModel().getSelectedItem();
        if (selectedEvent != null) {
            try {
                EventController updatedEvent = createEventFromFields();
                if (updatedEvent != null) {
                    excelManager.updateEvent(selectedEvent.getEventCode(), updatedEvent);
                    eventList.set(eventList.indexOf(selectedEvent), updatedEvent);
                    clearFields();
                    showAlert("Success", "Event updated successfully!");
                }
            } catch (NumberFormatException e) {
                showAlert("Input Error", "Please enter valid numbers for capacity and cost.");
            }
        } else {
            showAlert("No Selection", "Please select an event to edit.");
        }
    }

    @FXML
    protected void deleteEvent() {
        EventController selectedEvent = eventTableView.getSelectionModel().getSelectedItem();
        if (selectedEvent != null) {
            excelManager.deleteEvent(selectedEvent.getEventCode());
            eventList.remove(selectedEvent);
            showAlert("Success", "Event deleted successfully!");
        } else {
            showAlert("No Selection", "Please select an event to delete.");
        }
    }

    private EventController createEventFromFields() {
        String eventCode = eventCodeField.getText();
        String title = eventTitle.getText();
        String description = eventDescription.getText();
        String date = eventDate.getText();
        String location = eventLocation.getText();
        String headerImage = eventImageUrl.getText();

        if (eventCode.isEmpty() || title.isEmpty() || description.isEmpty() ||
                date.isEmpty() || location.isEmpty()) {
            showAlert("Input Error", "Please fill in all required fields.");
            return null;
        }

        try {
            int capacity = Integer.parseInt(eventCapacity.getText());
            double cost = Double.parseDouble(eventCost.getText());

            return new EventController(eventCode, title, description, location,
                    date, capacity, cost, headerImage, "");
        } catch (NumberFormatException e) {
            throw e;
        }
    }

    private void clearFields() {
        eventCodeField.clear();
        eventTitle.clear();
        eventDescription.clear();
        eventDate.clear();
        eventLocation.clear();
        eventImageUrl.clear();
        eventCapacity.clear();
        eventCost.clear();
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

