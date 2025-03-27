package com.FinalProject.UMS;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.print.PrinterJob;
import javafx.scene.Node;

import java.net.URL;
import java.util.*;

public class GradesController implements Initializable {

    @FXML private TableView<Grade> gradesTable;
    @FXML private Label termGPALabel;
    @FXML private Label termDateLabel;
    @FXML private ComboBox<String> termComboBox;
    @FXML private VBox gradesContainer;
    @FXML private Button returnButton;
    @FXML private Button printButton;
    @FXML private Slider progressSlider;
    @FXML private Label progressLabel;
    @FXML private BarChart<String, Number> gradeChart;
    @FXML private HBox chartContainer;

    private Map<String, TermGrades> termGradesMap = new LinkedHashMap<>();
    private String loggedInStudentId;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTermComboBox();
        setupTableColumns();
        setupGradeChart();
        loadSampleData();

        // Initialize progress slider
        progressSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            progressLabel.setText(String.format("Progress: %.0f%%", newVal));
        });
    }

    private void setupGradeChart() {
        gradeChart.setLegendVisible(false);
        gradeChart.setAnimated(false);
        gradeChart.setCategoryGap(10);
    }

    public void setLoggedInStudentId(String studentId) {
        this.loggedInStudentId = studentId;
    }

    @FXML
    private void handlePrintButton() {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(gradesContainer.getScene().getWindow())) {
            boolean success = job.printPage(gradesContainer);
            if (success) {
                job.endJob();
                showAlert("Print Successful", "Transcript Printed", "Your transcript has been sent to the printer.");
            } else {
                showAlert("Print Error", "Print Failed", "Could not print the transcript.");
            }
        }
    }

    @FXML
    private void handleReturnButton() {
        try {
            Stage stage = (Stage) returnButton.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("studentmanagecontroller.fxml"));
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showAlert("Navigation Error", "Failed to return to dashboard", e.getMessage());
        }
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void setupTermComboBox() {
        termComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                displayTermGrades(newVal);
            }
        });
    }

    private void setupTableColumns() {
        TableColumn<Grade, String> courseCol = new TableColumn<>("Course Section");
        courseCol.setCellValueFactory(new PropertyValueFactory<>("courseSection"));
        courseCol.setPrefWidth(150);

        TableColumn<Grade, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(250);

        TableColumn<Grade, String> creditsCol = new TableColumn<>("Credits");
        creditsCol.setCellValueFactory(new PropertyValueFactory<>("credits"));
        creditsCol.setPrefWidth(80);

        TableColumn<Grade, String> gradeCol = new TableColumn<>("Final Grade");
        gradeCol.setCellValueFactory(new PropertyValueFactory<>("finalGrade"));
        gradeCol.setPrefWidth(80);

        gradesTable.getColumns().setAll(courseCol, titleCol, creditsCol, gradeCol);
    }

    private void loadSampleData() {
        termGradesMap.put("Fall 2024", new TermGrades(
                "Fall 2024 (9/5/2024-12/13/2024)",
                "80.00",
                Arrays.asList(
                        new Grade("CHEM*1040*0355", "General Chemistry I", "0.50", "077"),
                        new Grade("ENGG*1100*0211", "Engineering and Design I", "0.75", "084"),
                        new Grade("ENGG*1410*01034", "Intro Program for Engineers", "0.50", "070"),
                        new Grade("MATH*1200*0302", "Calculus I", "0.50", "087"),
                        new Grade("PHYS*1130*0109", "Intr Physics With Applications", "0.50", "080")
                )
        ));

        termGradesMap.put("Winter 2024", new TermGrades(
                "Winter 2024 (1/8/2024-4/12/2024)",
                "85.50",
                Arrays.asList(
                        new Grade("CHEM*1050*0355", "General Chemistry II", "0.50", "082"),
                        new Grade("ENGG*1500*0211", "Engineering and Design II", "0.75", "088"),
                        new Grade("MATH*1210*0302", "Calculus II", "0.50", "090")
                )
        ));

        termGradesMap.put("Fall 2023", new TermGrades(
                "Fall 2023 (9/6/2023-12/14/2023)",
                "78.25",
                Arrays.asList(
                        new Grade("PHYS*1010*0109", "Introductory Physics", "0.50", "075"),
                        new Grade("MATH*1100*0302", "Pre-Calculus", "0.50", "081")
                )
        ));

        termComboBox.setItems(FXCollections.observableArrayList(termGradesMap.keySet()));
        termComboBox.getSelectionModel().selectFirst();
    }

    private void displayTermGrades(String term) {
        TermGrades termGrades = termGradesMap.get(term);
        termDateLabel.setText(termGrades.getTermDates());
        termGPALabel.setText("Term GPA: " + termGrades.getGpa());
        gradesTable.setItems(FXCollections.observableArrayList(termGrades.getGrades()));

        // Update progress slider with GPA
        try {
            double gpa = Double.parseDouble(termGrades.getGpa());
            progressSlider.setValue(gpa);
            progressLabel.setText(String.format("Progress: %.0f%%", gpa));
        } catch (NumberFormatException e) {
            progressSlider.setValue(0);
            progressLabel.setText("Progress: N/A");
        }

        // Update chart
        updateGradeChart(termGrades.getGrades());
    }

    private void updateGradeChart(List<Grade> grades) {
        gradeChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        for (Grade grade : grades) {
            try {
                int gradeValue = Integer.parseInt(grade.getFinalGrade());
                series.getData().add(new XYChart.Data<>(grade.getCourseSection(), gradeValue));
            } catch (NumberFormatException e) {
                // Skip if grade is not a number
            }
        }

        gradeChart.getData().add(series);
    }

    private static class TermGrades {
        private final String termDates;
        private final String gpa;
        private final List<Grade> grades;

        public TermGrades(String termDates, String gpa, List<Grade> grades) {
            this.termDates = termDates;
            this.gpa = gpa;
            this.grades = grades;
        }

        public String getTermDates() { return termDates; }
        public String getGpa() { return gpa; }
        public List<Grade> getGrades() { return grades; }
    }

}