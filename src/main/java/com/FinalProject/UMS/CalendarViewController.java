

package com.FinalProject.UMS;




import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;




public class CalendarViewController {
    @FXML private VBox calendarContainer;
    @FXML private Label monthYearLabel;
    @FXML private TableView<EventController> eventsTable;
    @FXML private Button prevMonthButton;
    @FXML private Button nextMonthButton;




    private YearMonth currentYearMonth;
    private EventManagementExcel excelManager = new EventManagementExcel();
    private boolean isAdminView;




    public void initialize() {
        // Initialization will be done through setter
    }




    public void setIsAdminView(boolean isAdmin) {
        this.isAdminView = isAdmin;
        currentYearMonth = YearMonth.now();
        updateCalendar();
    }




    private void updateCalendar() {
        calendarContainer.getChildren().clear();




        // Set month/year label
        monthYearLabel.setText(currentYearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")));




        // Create calendar grid
        GridPane calendarGrid = new GridPane();
        calendarGrid.setHgap(5);
        calendarGrid.setVgap(5);
        calendarGrid.setPadding(new Insets(10));
        calendarGrid.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-width: 1;");




        // Add day names header
        String[] dayNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (int i = 0; i < 7; i++) {
            Label dayLabel = new Label(dayNames[i]);
            dayLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center; -fx-padding: 5 10;");
            calendarGrid.add(dayLabel, i, 0);
        }




        List<EventController> allEvents = excelManager.readEvents();




        LocalDate firstOfMonth = currentYearMonth.atDay(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7; // Sunday = 0
        int daysInMonth = currentYearMonth.lengthOfMonth();




        // Fill calendar grid
        int day = 1;
        for (int row = 1; row < 7; row++) {
            for (int col = 0; col < 7; col++) {
                if ((row == 1 && col < dayOfWeek) || day > daysInMonth) {
                    // Empty cell for days not in current month
                    Pane spacer = new Pane();
                    spacer.setMinSize(40, 40);
                    calendarGrid.add(spacer, col, row);
                } else {
                    LocalDate currentDate = currentYearMonth.atDay(day);
                    long eventCount = allEvents.stream()
                            .filter(e -> currentDate.equals(LocalDate.parse(e.getDate().split(" ")[0])))
                            .count();




                    // Create day button with flip animation
                    Button dayButton = new Button(String.valueOf(day));
                    dayButton.setMinSize(40, 40);
                    dayButton.setMaxSize(40, 40);
                    dayButton.setStyle("-fx-background-radius: 20;");




                    if (eventCount > 0) {
                        dayButton.setStyle("-fx-background-color: #e3f2fd; -fx-font-weight: bold; -fx-background-radius: 20;");
                        Tooltip tooltip = new Tooltip(eventCount + " event" + (eventCount > 1 ? "s" : ""));
                        Tooltip.install(dayButton, tooltip);
                    }




                    // Add flip animation on click
                    dayButton.setOnAction(e -> {
                        dayButton.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-background-radius: 20;");
                        showEventsForDate(currentDate);
                    });




                    calendarGrid.add(dayButton, col, row);
                    day++;
                }
            }
        }




        calendarContainer.getChildren().add(calendarGrid);
    }




    private void showEventsForDate(LocalDate date) {
        List<EventController> events = excelManager.readEvents();
        eventsTable.getItems().clear();




        eventsTable.getItems().addAll(events.stream()
                .filter(e -> date.equals(LocalDate.parse(e.getDate().split(" ")[0])))
                .toList());
    }




    @FXML
    private void previousMonth() {
        currentYearMonth = currentYearMonth.minusMonths(1);
        updateCalendar();
    }




    @FXML
    private void nextMonth() {
        currentYearMonth = currentYearMonth.plusMonths(1);
        updateCalendar();
    }




    @FXML
    private void returnToTableView() {
        try {
            String fxmlFile = isAdminView ? "admin-event-view.fxml" : "event-user-view.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();




            // Set the controller properties if needed
            if (isAdminView) {
                EventAdminController controller = loader.getController();
                controller.initialize();
            } else {
                EventUserController controller = loader.getController();
                controller.initialize();
            }




            Stage stage = (Stage) calendarContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to return: " + e.getMessage());
        }
    }




    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
