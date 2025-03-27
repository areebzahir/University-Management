package com.FinalProject.UMS;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class TuitionController {
    @FXML
    private TableView<Tuition> tuitionTable;

    @FXML
    private TableColumn<Tuition, String> semesterColumn;

    @FXML
    private TableColumn<Tuition, Double> amountDueColumn;

    @FXML
    private TableColumn<Tuition, Double> amountPaidColumn;

    @FXML
    private TableColumn<Tuition, String> statusColumn;

    private String userRole; // Add userRole field
    private String loggedInStudentId;

    public void setLoggedInStudentId(String studentId) {
        this.loggedInStudentId = studentId;
        loadTuitionData();
    }

    @FXML
    public void initialize() {
        // Initialize the table columns
        semesterColumn.setCellValueFactory(new PropertyValueFactory<>("semester"));
        amountDueColumn.setCellValueFactory(new PropertyValueFactory<>("amountDue"));
        amountPaidColumn.setCellValueFactory(new PropertyValueFactory<>("amountPaid"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        adjustVisibilityBasedOnRole(); // Adjust UI based on user role
    }

    private void loadTuitionData() {
        // Load tuition data from Excel
        List<Tuition> tuitionList = StudentDatabase.loadTuitionDataFromExcel();
        ObservableList<Tuition> tuitionData = FXCollections.observableArrayList(tuitionList);
        tuitionTable.setItems(tuitionData);
    }

    public void setUserRole(String role) {
        this.userRole = role;
        adjustVisibilityBasedOnRole();
    }

    private void adjustVisibilityBasedOnRole() {
        if ("USER".equals(userRole)) {
            // Disable editing for USER role
            tuitionTable.setEditable(false);
        }
    }

    @FXML
    protected void onReturnButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("studentmanagecontroller.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) tuitionTable.getScene().getWindow();
            Scene scene = new Scene(root, 1366, 768);
            stage.setScene(scene);
            stage.setTitle("Hello");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Getter for tuitionTable (added for testing)
    public TableView<Tuition> getTuitionTable() {
        return tuitionTable;
    }

    // Getter for userRole (added for testing)
    public String getUserRole() {
        return userRole;
    }
}