package com.FinalProject.UMS;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TuitionController {

    // FXML elements
    @FXML private Label currentBalanceLabel;
    @FXML private Label dueDateLabel;
    @FXML private ProgressBar paymentProgress;
    @FXML private Button makePaymentButton;
    @FXML private ComboBox<String> termComboBox;
    @FXML private Button viewDetailsButton;
    @FXML private VBox alertBox;
    @FXML private TableView<TuitionRecord> tuitionTable;
    @FXML private BarChart<String, Number> paymentChart;
    @FXML private PieChart breakdownChart;
    @FXML private Button returnButton;

    // Hardcoded test data
    private static final double TOTAL_TUITION = 8000.00; // Updated to match table data
    private static final double PAID_AMOUNT = 2500.00;
    private static final LocalDate DUE_DATE = LocalDate.now().plusDays(30);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    public void initialize() {
        // Initialize UI components with test data
        setupCurrentBalance();
        setupPaymentProgress();
        setupTermComboBox();
        setupAlerts();
        setupTuitionTable();
        setupCharts();
    }

    private void setupCurrentBalance() {
        double balance = TOTAL_TUITION - PAID_AMOUNT;
        currentBalanceLabel.setText(String.format("$%.2f", balance));
        dueDateLabel.setText("Due: " + DUE_DATE.format(DATE_FORMATTER));
    }

    private void setupPaymentProgress() {
        double progress = PAID_AMOUNT / TOTAL_TUITION;
        paymentProgress.setProgress(progress);
    }

    private void setupTermComboBox() {
        ObservableList<String> terms = FXCollections.observableArrayList(
                "Winter 2025", "Fall 2024"
        );
        termComboBox.setItems(terms);
        termComboBox.getSelectionModel().selectFirst();
    }

    private void setupAlerts() {
        // Clear existing alerts
        alertBox.getChildren().clear();

        // Add alert title
        Label title = new Label("Alerts and Notifications");
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: #856404;");
        alertBox.getChildren().add(title);

        // Add test alerts
        addAlert("Payment Due", "Payment of $2500.00 due by " + DUE_DATE.format(DATE_FORMATTER), "warning");
        addAlert("Payment Received", "Payment of $1000.00 received on " + LocalDate.now().minusDays(5).format(DATE_FORMATTER), "success");
        addAlert("Upcoming Deadline", "Winter 2025 registration deadline: Dec 15, 2024", "info");
    }

    private void addAlert(String title, String message, String type) {
        Label alertLabel = new Label(title + ": " + message);
        String style = "";
        switch (type) {
            case "warning":
                style = "-fx-text-fill: #d35400;";
                break;
            case "success":
                style = "-fx-text-fill: #27ae60;";
                break;
            case "info":
                style = "-fx-text-fill: #2980b9;";
                break;
        }
        alertLabel.setStyle(style);
        alertBox.getChildren().add(alertLabel);
    }

    private void setupTuitionTable() {
        // Set up columns
        TableColumn<TuitionRecord, String> semesterCol = new TableColumn<>("Semester");
        semesterCol.setCellValueFactory(new PropertyValueFactory<>("semester"));

        TableColumn<TuitionRecord, Double> dueCol = new TableColumn<>("Amount Due");
        dueCol.setCellValueFactory(new PropertyValueFactory<>("amountDue"));

        TableColumn<TuitionRecord, Double> paidCol = new TableColumn<>("Amount Paid");
        paidCol.setCellValueFactory(new PropertyValueFactory<>("amountPaid"));

        TableColumn<TuitionRecord, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Add columns to table
        tuitionTable.getColumns().setAll(semesterCol, dueCol, paidCol, statusCol);

        // Create test data
        ObservableList<TuitionRecord> data = FXCollections.observableArrayList(
                new TuitionRecord("Winter 2025", 2500.00, 1500.00, "Partial"),
                new TuitionRecord("Fall 2024", 5500.00, 1000.00, "Partial")
        );

        tuitionTable.setItems(data);
    }

    private void setupCharts() {
        // Payment Status Chart
        XYChart.Series<String, Number> paymentSeries = new XYChart.Series<>();
        paymentSeries.getData().add(new XYChart.Data<>("Paid", PAID_AMOUNT));
        paymentSeries.getData().add(new XYChart.Data<>("Due", TOTAL_TUITION - PAID_AMOUNT));
        paymentChart.getData().add(paymentSeries);

        // Customize chart appearance
        paymentChart.setCategoryGap(50);
        paymentChart.setBarGap(10);

        // Fee Breakdown Chart
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                new PieChart.Data("Tuition", 5000),
                new PieChart.Data("Course Fees", 1500),
                new PieChart.Data("Lab Fees", 800),
                new PieChart.Data("Library", 300),
                new PieChart.Data("Athletics", 200),
                new PieChart.Data("Technology", 200)
        );
        breakdownChart.setData(pieData);

        // Add labels to pie chart
        pieData.forEach(data -> {
            String percentage = String.format("%.1f%%", (data.getPieValue() / TOTAL_TUITION * 100));
            data.nameProperty().set(data.getName() + " (" + percentage + ")");
        });
    }

    @FXML
    private void handleReturnButton() {
        try {
            // Get the current stage
            Stage stage = (Stage) returnButton.getScene().getWindow();

            // Load the dashboard FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("studentmanagecontroller.fxml"));
            Parent root = loader.load();

            // Create new scene and set it on the stage
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            // Show error alert if navigation fails
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText("Failed to return to dashboard");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    // Model class for tuition records
    public static class TuitionRecord {
        private final String semester;
        private final double amountDue;
        private final double amountPaid;
        private final String status;

        public TuitionRecord(String semester, double amountDue, double amountPaid, String status) {
            this.semester = semester;
            this.amountDue = amountDue;
            this.amountPaid = amountPaid;
            this.status = status;
        }

        // Getters
        public String getSemester() { return semester; }
        public double getAmountDue() { return amountDue; }
        public double getAmountPaid() { return amountPaid; }
        public String getStatus() { return status; }
    }
}