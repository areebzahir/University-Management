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

public class StudentInfoController {
    @FXML
    private TableView<Student> studentTable;

    private String userRole; // Add userRole field

    @FXML
    public void initialize() {
        // Load students from Excel
        List<Student> students = ExcelDatabase.loadStudents();
        ObservableList<Student> studentData = FXCollections.observableArrayList(students);
        studentTable.setItems(studentData);

        adjustVisibilityBasedOnRole(); // Adjust UI based on user role
    }

    public void setUserRole(String role) {
        this.userRole = role;
        adjustVisibilityBasedOnRole();
    }

    private void adjustVisibilityBasedOnRole() {
        if ("USER".equals(userRole)) {
            // Disable or hide features for USER role
            // Example: Disable editing or deletion options
        }
    }

    @FXML
    protected void onReturnButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("studentmanagecontroller.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) studentTable.getScene().getWindow();
            Scene scene = new Scene(root, 1366, 768);
            stage.setScene(scene);
            stage.setTitle("Hello");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}