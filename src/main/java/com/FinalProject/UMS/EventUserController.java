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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class EventUserController {
    @FXML private TableView<EventController> eventTableView;
    @FXML private TableColumn<EventController, String> eventCodeColumn;
    @FXML private TableColumn<EventController, String> eventNameColumn;
    @FXML private TableColumn<EventController, String> descriptionColumn;
    @FXML private TableColumn<EventController, String> locationColumn;
    @FXML private TableColumn<EventController, String> dateColumn;
    @FXML private TableColumn<EventController, Integer> capacityColumn;

    @FXML private Button registerButton;
    @FXML private Button calendarViewButton;
    @FXML private Button returnButton;
    @FXML private Button myEventsButton;
    @FXML private Button allEventsButton;
    @FXML private Button userButton;
    private EventManagementExcel excelManager = new EventManagementExcel();
    private DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy 'at' HH:mm");
    private DateTimeFormatter excelFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @FXML
    public void initialize() {

        // Prevent unauthorized access
        //   userButton.setDisable(true);
        // userButton.setVisible(false);
        if (GlobalState.getInstance().getLoggedInUser() == null ||
                !GlobalState.getInstance().getLoggedInUser().getRole().equals("USER")) {
            showAlert("Access Denied", "You don't have permission to access this view.");
            onReturnButtonClick();
            return;
        }

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

        dateColumn.setCellFactory(column -> new TableCell<EventController, String>() {
            @Override
            protected void updateItem(String date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null || date.isEmpty()) {
                    setText(null);
                } else {
                    try {
                        LocalDateTime dateTime = LocalDateTime.parse(date, excelFormatter);
                        setText(dateTime.format(displayFormatter));
                    } catch (DateTimeParseException e) {
                        setText(date);
                    }
                }
            }
        });

        capacityColumn.setCellFactory(column -> new TableCell<EventController, Integer>() {
            @Override
            protected void updateItem(Integer capacity, boolean empty) {
                super.updateItem(capacity, empty);
                if (empty || capacity == null || capacity == 0) {
                    setText(null);
                } else {
                    setText(String.valueOf(capacity));
                }
            }
        });
    }

    private void loadEventsFromExcel() {
        List<EventController> events = excelManager.readEvents();
        ObservableList<EventController> observableList = FXCollections.observableArrayList(events);
        eventTableView.setItems(observableList);
    }

    @FXML
    protected void registerForEvent() {
        if (currentUser == null) {
            showAlert("Error", "No user logged in");
            return;
        }

        EventController selectedEvent = eventTableView.getSelectionModel().getSelectedItem();
        if (selectedEvent == null) {
            showAlert("Error", "Please select an event to register");
            return;
        }

        if (selectedEvent.isStudentRegistered(currentUser.getId())) {
            showAlert("Info", "You are already registered for this event");
            return;
        }

        if (!selectedEvent.hasAvailableSpots()) {
            showAlert("Error", "Event is full");
            return;
        }

        selectedEvent.registerStudent(currentUser.getId());
        excelManager.updateEvent(selectedEvent.getEventCode(), selectedEvent);

        showAlert("Success", "Registered for event: " + selectedEvent.getTitle());
    }

    @FXML
    protected void showMyEvents() {
        if (currentUser == null) {
            showAlert("Error", "No user logged in");
            return;
        }

        List<EventController> allEvents = excelManager.readEvents();
        ObservableList<EventController> myEvents = FXCollections.observableArrayList();

        for (EventController event : allEvents) {
            if (event.isStudentRegistered(currentUser.getId())) {
                myEvents.add(event);
            }
        }

        eventTableView.setItems(myEvents);
    }

    @FXML
    protected void showAllEvents() {
        loadEventsFromExcel();
    }

    @FXML
    protected void showCalendarView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("calendar-view.fxml"));
            Parent root = loader.load();
            CalendarViewController controller = loader.getController();
            controller.setIsAdminView(false);

            Stage stage = (Stage) eventTableView.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Event Calendar");
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Failed to open calendar view: " + e.getMessage());
            e.printStackTrace();
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
            showAlert("Error", "Failed to return to menu: " + e.getMessage());
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