package com.FinalProject.UMS;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

public class EventAdminController {
    @FXML
    private TableView<EventController> eventTableView;
    @FXML
    private TableColumn<EventController, String> eventCodeColumn;
    @FXML
    private TableColumn<EventController, String> eventNameColumn;
    @FXML
    private TableColumn<EventController, String> descriptionColumn;
    @FXML
    private TableColumn<EventController, String> locationColumn;
    @FXML
    private TableColumn<EventController, String> dateColumn;
    @FXML
    private TableColumn<EventController, Integer> capacityColumn;
    @FXML
    private TableColumn<EventController, Double> costColumn;
    @FXML
    private TableColumn<EventController, String> headerImageColumn;
    @FXML
    private TableColumn<EventController, String> registeredStudentsColumn;

    @FXML
    private TextField eventCodeField;
    @FXML
    private TextField eventTitle;
    @FXML
    private TextField eventDescription;
    @FXML
    private DatePicker eventDatePicker;
    @FXML
    private TextField eventTimeField;
    @FXML
    private TextField eventLocation;
    @FXML
    private TextField eventImageUrl;
    @FXML
    private TextField eventCapacity;
    @FXML
    private TextField eventCost;

    @FXML
    private Button addEventButton;
    @FXML
    private Button editEventButton;
    @FXML
    private Button deleteEventButton;
    @FXML
    private Button returnButton;
    @FXML
    private Button timePickerButton;

    private EventManagementExcel excelManager = new EventManagementExcel();
    private DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy 'at' HH:mm");
    private DateTimeFormatter excelFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    public void initialize() {
        setupTableColumns();
        loadEventsFromExcel();
        setupDatePicker();
    }

    private void setupDatePicker() {
        eventDatePicker.setConverter(new StringConverter<LocalDate>() {
            private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });
    }

    @FXML
    protected void showTimePickerDialog() {
        Dialog<LocalTime> dialog = new Dialog<>();
        dialog.setTitle("Select Time");
        dialog.setHeaderText("Choose the event time");

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Spinner<Integer> hourSpinner = new Spinner<>(0, 23, 12);
        Spinner<Integer> minuteSpinner = new Spinner<>(0, 59, 0);

        hourSpinner.setEditable(true);
        minuteSpinner.setEditable(true);

        HBox content = new HBox(10);
        content.getChildren().addAll(
                new Label("Hour:"), hourSpinner,
                new Label("Minute:"), minuteSpinner);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(20));

        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return LocalTime.of(hourSpinner.getValue(), minuteSpinner.getValue());
            }
            return null;
        });

        Optional<LocalTime> result = dialog.showAndWait();
        result.ifPresent(time -> {
            eventTimeField.setText(time.format(timeFormatter));
        });
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

        dateColumn.setCellFactory(column -> new TableCell<EventController, String>() {
            @Override
            protected void updateItem(String date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null || date.isEmpty()) {
                    setText(null);
                } else {
                    try {
                        // First try parsing as our standard format
                        LocalDateTime dateTime = LocalDateTime.parse(date, excelFormatter);
                        setText(dateTime.format(displayFormatter));
                    } catch (DateTimeParseException e) {
                        // If parsing fails, try to handle as Excel numeric date
                        try {
                            double excelDate = Double.parseDouble(date);
                            String formattedDate = convertExcelDateToString(excelDate);
                            LocalDateTime dateTime = LocalDateTime.parse(formattedDate, excelFormatter);
                            setText(dateTime.format(displayFormatter));
                        } catch (Exception e2) {
                            // If all parsing fails, just show the raw value
                            setText(date);
                        }
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

        costColumn.setCellFactory(column -> new TableCell<EventController, Double>() {
            @Override
            protected void updateItem(Double cost, boolean empty) {
                super.updateItem(cost, empty);
                if (empty || cost == null) {
                    setText(null);
                } else {
                    setText(cost == 0.0 ? "Free" : String.format("$%.2f", cost));
                }
            }
        });
    }

    private String convertExcelDateToString(double excelDate) {
        LocalDate baseDate = LocalDate.of(1899, 12, 30);
        LocalDate date = baseDate.plusDays((long) excelDate);

        double timeFraction = excelDate - Math.floor(excelDate);
        int totalSeconds = (int) (timeFraction * 24 * 60 * 60);
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;

        return String.format("%04d-%02d-%02d %02d:%02d",
                date.getYear(), date.getMonthValue(), date.getDayOfMonth(),
                hours, minutes);
    }

    private void loadEventsFromExcel() {
        List<EventController> events = excelManager.readEvents();
        ObservableList<EventController> observableList = FXCollections.observableArrayList(events);
        eventTableView.setItems(observableList);
    }

    @FXML
    protected void addEvent() {
        try {
            String eventCode = eventCodeField.getText().trim();
            String title = eventTitle.getText().trim();
            String description = eventDescription.getText().trim();
            String location = eventLocation.getText().trim();
            String imageUrl = eventImageUrl.getText().trim();
            int capacity = Integer.parseInt(eventCapacity.getText().trim());
            double cost = Double.parseDouble(eventCost.getText().trim());

            // Check if event code already exists
            if (excelManager.eventCodeExists(eventCode)) {
                showAlert("Error", "Event code already exists. Please use a different event code.");
                return;
            }

            LocalDate date = eventDatePicker.getValue();
            String time = eventTimeField.getText().trim();

            if (date == null || time.isEmpty()) {
                showAlert("Error", "Please select both date and time");
                return;
            }

            String dateTime = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " " + time;

            EventController newEvent = new EventController(
                    eventCode, title, description, location, dateTime, capacity, cost, imageUrl, "");

            excelManager.addEvent(newEvent);
            loadEventsFromExcel();
            clearFormFields();
            showAlert("Success", "Event added successfully");

        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter valid numbers for capacity and cost");
        } catch (IllegalArgumentException e) {
            showAlert("Error", e.getMessage());
        } catch (Exception e) {
            showAlert("Error", "Failed to add event: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    protected void editEvent() {
        EventController selectedEvent = eventTableView.getSelectionModel().getSelectedItem();
        if (selectedEvent == null) {
            showAlert("Error", "Please select an event to edit");
            return;
        }

        try {
            String eventCode = eventCodeField.getText().trim();
            String title = eventTitle.getText().trim();
            String description = eventDescription.getText().trim();
            String location = eventLocation.getText().trim();
            String imageUrl = eventImageUrl.getText().trim();
            int capacity = Integer.parseInt(eventCapacity.getText().trim());
            double cost = Double.parseDouble(eventCost.getText().trim());

            LocalDate date = eventDatePicker.getValue();
            String time = eventTimeField.getText().trim();

            if (date == null || time.isEmpty()) {
                showAlert("Error", "Please select both date and time");
                return;
            }

            String dateTime = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " " + time;

            EventController updatedEvent = new EventController(
                    eventCode, title, description, location, dateTime, capacity, cost,
                    imageUrl, selectedEvent.getRegisteredStudents());

            excelManager.updateEvent(selectedEvent.getEventCode(), updatedEvent);
            loadEventsFromExcel();
            clearFormFields();
            showAlert("Success", "Event updated successfully");

        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter valid numbers for capacity and cost");
        } catch (IllegalArgumentException e) {
            showAlert("Error", e.getMessage());
        } catch (Exception e) {
            showAlert("Error", "Failed to update event: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    protected void deleteEvent() {
        EventController selectedEvent = eventTableView.getSelectionModel().getSelectedItem();
        if (selectedEvent == null) {
            showAlert("Error", "Please select an event to delete");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText("Delete Event");
        confirmation.setContentText("Are you sure you want to delete this event?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                excelManager.deleteEvent(selectedEvent.getEventCode());
                loadEventsFromExcel();
                clearFormFields();
                showAlert("Success", "Event deleted successfully");
            } catch (Exception e) {
                showAlert("Error", "Failed to delete event: " + e.getMessage());
                e.printStackTrace();
            }
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

    @FXML
    protected void showCalendarView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("calendar-view.fxml"));
            Parent root = loader.load();
            CalendarViewController controller = loader.getController();
            controller.setIsAdminView(true); // true for admin view

            Stage stage = (Stage) eventTableView.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Event Calendar - Admin");
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Failed to open calendar view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearFormFields() {
        eventCodeField.clear();
        eventTitle.clear();
        eventDescription.clear();
        eventLocation.clear();
        eventImageUrl.clear();
        eventCapacity.clear();
        eventCost.clear();
        eventDatePicker.setValue(null);
        eventTimeField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
