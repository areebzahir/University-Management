package com.FinalProject.UMS;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TableColumn;

public class SubjectManagementController implements Initializable {

    @FXML
    private TextField subjectNameField;

    @FXML
    private TextField subjectCodeField;

    @FXML
    private Button addButton;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private Button deleteButton;

    @FXML
    private TableView<Subject> subjectTable;

    @FXML
    private TableColumn<Subject, String> nameColumn;

    @FXML
    private TableColumn<Subject, String> codeColumn;

    @FXML
    private Button backButton; // Added Back Button

    private ObservableList<Subject> subjectList = FXCollections.observableArrayList();

    private static final String FILE_PATH = "src/main/resources/UMS_Data.xlsx";
    private static final String SUBJECT_SHEET_NAME = "Subjects";
    private static final int SUBJECT_CODE_COLUMN = 0, SUBJECT_NAME_COLUMN = 1;
    private static final boolean HAS_SUBJECT_HEADER_ROW = true;
    private static final Logger LOGGER = Logger.getLogger(SubjectManagementController.class.getName());

    private String userRole; // Add userRole field

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        subjectTable.setItems(subjectList);

        loadSubjectsFromExcel();
        adjustVisibilityBasedOnRole(); // Call adjustVisibilityBasedOnRole in initialize
    }

    public void setUserRole(String role) {
        this.userRole = role;
        adjustVisibilityBasedOnRole();
        LOGGER.info("SubjectManagementController User role set to: " + role);
    }

    private void adjustVisibilityBasedOnRole() {
        if ("USER".equals(userRole)) {
            // Disable and hide buttons/fields for the USER role
            addButton.setDisable(true);
            addButton.setVisible(false);

            deleteButton.setDisable(true);
            deleteButton.setVisible(false);

            subjectNameField.setDisable(true);
            subjectNameField.setVisible(false);

            subjectCodeField.setDisable(true);
            subjectCodeField.setVisible(false);
        } else {
            // Enable and show buttons/fields for other roles (e.g., ADMIN)
            addButton.setDisable(false);
            addButton.setVisible(true);

            deleteButton.setDisable(false);
            deleteButton.setVisible(true);

            subjectNameField.setDisable(false);
            subjectNameField.setVisible(true);

            subjectCodeField.setDisable(false);
            subjectCodeField.setVisible(true);

            searchButton.setDisable(false);
            searchButton.setVisible(true);
        }
    }
    // Load Subjects from Excel
    private void loadSubjectsFromExcel() {
        Map<String, String> subjects = ExcelDatabase.loadSubjects();
        subjects.forEach((code, name) -> subjectList.add(new Subject(name, code)));
    }

    // Add Subject
    @FXML
    void addSubject(ActionEvent event) {
        String name = subjectNameField.getText();
        String code = subjectCodeField.getText();

        if (name != null && !name.isEmpty() && code != null && !code.isEmpty()) {
            Subject newSubject = new Subject(name, code);
            subjectList.add(newSubject);
            ExcelDatabase.addSubjectToExcel(newSubject, FILE_PATH, SUBJECT_SHEET_NAME);
            subjectNameField.clear();
            subjectCodeField.clear();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter both subject name and code.");
        }
    }

    // Search Subject
    @FXML
    void searchSubject(ActionEvent event) {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            subjectTable.setItems(subjectList); // Show all subjects
            return;
        }

        ObservableList<Subject> filteredList = FXCollections.observableArrayList();
        for (Subject subject : subjectList) {
            if (subject.getName().toLowerCase().contains(searchText) ||
                    subject.getCode().toLowerCase().contains(searchText)) {
                filteredList.add(subject);
            }
        }
        subjectTable.setItems(filteredList);
    }

    // Delete Subject
    @FXML
    void deleteSubject(ActionEvent event) {
        Subject selectedSubject = subjectTable.getSelectionModel().getSelectedItem();
        if (selectedSubject != null) {
            subjectList.remove(selectedSubject);
            ExcelDatabase.deleteSubjectFromExcel(selectedSubject, FILE_PATH, SUBJECT_SHEET_NAME);
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a subject to delete.");
        }
    }

    @FXML
    void backToMenu(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("menu-view.fxml"));
            Parent root = fxmlLoader.load();


            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading menu.fxml: " + e.getMessage(), e);
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load menu.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}