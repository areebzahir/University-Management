package com.FinalProject.UMS;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class TuitionController {
    @FXML
    private TableView<Tuition> tuitionTable;

    private String userRole; // Add userRole field

    @FXML
    public void initialize() {
        // Load tuition records from Excel
        List<Tuition> tuitionRecords = ExcelDatabase.loadTuitionRecords("132021"); // Replace with dynamic ID
        ObservableList<Tuition> tuitionData = FXCollections.observableArrayList(tuitionRecords);
        tuitionTable.setItems(tuitionData);

        adjustVisibilityBasedOnRole(); // Adjust UI based on user role
    }

    public void setUserRole(String role) {
        this.userRole = role;
        adjustVisibilityBasedOnRole();
    }

    private void adjustVisibilityBasedOnRole() {
        if ("USER".equals(userRole)) {
            // Disable or hide features for USER role
            // Example: Disable editing of tuition records
        }
    }

    @FXML
    protected void onReturnButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("studentmanagecontroller.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) tuitionTable.getScene().getWindow();
            Scene scene = new Scene(root, 1920, 1080);
            stage.setScene(scene);
            stage.setTitle("Main Page");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}