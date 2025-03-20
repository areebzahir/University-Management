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

public class TuitionController {
    @FXML
    private TableView<Tuition> tuitionTable;

    private String userRole; // Add userRole field

    @FXML
    public void initialize() {
        // Hardcoded test values (no arrays or external data)
        Tuition tuition1 = new Tuition("Fall 2023", 5000.0, 2500.0, "Partially Paid");
        Tuition tuition2 = new Tuition("Spring 2024", 4000.0, 4000.0, "Fully Paid");

        // Add test values directly to the table
        ObservableList<Tuition> tuitionData = FXCollections.observableArrayList();
        tuitionData.add(tuition1);
        tuitionData.add(tuition2);
        tuitionTable.setItems(tuitionData);

        adjustVisibilityBasedOnRole(); // Adjust UI based on user role
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