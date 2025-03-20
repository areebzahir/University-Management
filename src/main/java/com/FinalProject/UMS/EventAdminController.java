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

/**
 * EventAdminController manages the event administration interface.
 * This class allows administrators to add, edit, delete, and view events.
 */
public class EventAdminController {

    // Input fields for event details
    @FXML private TextField eventCodeField;
    @FXML private TextField eventTitle;
    @FXML private TextField eventDescription;
    @FXML private TextField eventDate;
    @FXML private TextField eventLocation;
    @FXML private TextField eventImageUrl;
    @FXML private TextField eventCapacity;
    @FXML private TextField eventCost;

    // TableView for displaying events
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

    // Buttons
    @FXML private Button addEventButton;
    @FXML private Button editEventButton;
    @FXML private Button deleteEventButton;
    @FXML private Button returnButton;

    private ObservableList<EventController> eventList = FXCollections.observableArrayList(); // Observable list to hold events

    /**
     * Initializes the TableView and sets up the columns.
     */
    @FXML
    public void initialize() {
        // Set up the TableView columns
        eventCodeColumn.setCellValueFactory(new PropertyValueFactory<>("eventCode"));
        eventNameColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        capacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        costColumn.setCellValueFactory(new PropertyValueFactory<>("cost"));
        headerImageColumn.setCellValueFactory(new PropertyValueFactory<>("headerImage"));
        registeredStudentsColumn.setCellValueFactory(new PropertyValueFactory<>("registeredStudents"));

        // Load example data
        loadExampleEvents();

        // Set the items of the TableView to the observable list
        eventTableView.setItems(eventList);
    }

    /**
     * Loads example events into the TableView.
     */
    private void loadExampleEvents() {
        eventList.addAll(
                new EventController("EV001", "Welcome Seminar", "Orientation week", "Auditorium", "2025-09-01 10:00", 100, 0.0, "default.jpg", "Alice Smith, Bob Johnson, Jennifer Davis, Helen Jones"),
                new EventController("EV002", "Research Workshop", "Graduate workshop", "Lab 301", "2025-10-05 14:00", 50, 20.0, "default.jpg", "Alice Smith, Bob Johnson, Lucka Racki, Helen Jones, David Lee")
        );
    }

    /**
     * Adds a new event to the TableView.
     */
    @FXML
    protected void addEvent() {
        try {
            // Retrieve values from input fields
            String eventCode = eventCodeField.getText();
            String title = eventTitle.getText();
            String description = eventDescription.getText();
            String date = eventDate.getText();
            String location = eventLocation.getText();
            String headerImage = eventImageUrl.getText();
            int capacity = Integer.parseInt(eventCapacity.getText());
            double cost = Double.parseDouble(eventCost.getText());

            // Validate input fields
            if (eventCode.isEmpty() || title.isEmpty() || description.isEmpty() || date.isEmpty() || location.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Please fill in all fields.");
                return;
            }

            // Create a new EventController object
            EventController newEvent = new EventController(eventCode, title, description, location, date, capacity, cost, headerImage, "");

            // Add the new event to the TableView
            eventList.add(newEvent);

            // Clear fields after adding the event
            clearFields();

            // Show success message
            showAlert(Alert.AlertType.INFORMATION, "Success", "Event added successfully: " + newEvent.getTitle());
        } catch (NumberFormatException e) {
            // Handle invalid number input for capacity or cost
            showAlert(Alert.AlertType.ERROR, "Input Error", "Please enter valid numbers for capacity and cost.");
        }
    }

    /**
     * Edits the selected event in the TableView.
     */
    @FXML
    protected void editEvent() {
        EventController selectedEvent = eventTableView.getSelectionModel().getSelectedItem();
        if (selectedEvent != null) {
            // Populate input fields with selected event details
            eventCodeField.setText(selectedEvent.getEventCode());
            eventTitle.setText(selectedEvent.getTitle());
            eventDescription.setText(selectedEvent.getDescription());
            eventDate.setText(selectedEvent.getDate());
            eventLocation.setText(selectedEvent.getLocation());
            eventImageUrl.setText(selectedEvent.getHeaderImage());
            eventCapacity.setText(String.valueOf(selectedEvent.getCapacity()));
            eventCost.setText(String.valueOf(selectedEvent.getCost()));

            // Remove the selected event from the TableView
            eventList.remove(selectedEvent);
        } else {
            showAlert(Alert.AlertType.WARNING, "No Event Selected", "Please select an event to edit.");
        }
    }

    /**
     * Deletes the selected event from the TableView.
     */
    @FXML
    protected void deleteEvent() {
        EventController selectedEvent = eventTableView.getSelectionModel().getSelectedItem();
        if (selectedEvent != null) {
            eventList.remove(selectedEvent);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Event deleted successfully: " + selectedEvent.getTitle());
        } else {
            showAlert(Alert.AlertType.WARNING, "No Event Selected", "Please select an event to delete.");
        }
    }

    /**
     * Clears all input fields.
     */
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

    /**
     * Handles the return button click event, navigating back to the event menu.
     */
    @FXML
    protected void onReturnButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("event-menu-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) returnButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Event Management System");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays an alert dialog with the specified title and message.
     *
     * @param alertType The type of alert (e.g., INFORMATION, WARNING, ERROR).
     * @param title   The title of the alert dialog.
     * @param message The message to display in the alert dialog.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
