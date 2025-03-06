package com.FinalProject.UMS;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class FacultyController {

    @FXML
    private TextField nameField, emailField, departmentField;

    @FXML
    private Button saveButton;

    @FXML
    private void saveFaculty() {
        // Fetch input values
        String name = nameField.getText();
        String email = emailField.getText();
        String department = departmentField.getText();

        // Check if fields are empty
        if (name.isEmpty() || email.isEmpty() || department.isEmpty()) {
            showAlert("Error", "All fields are required!");
            return;
        }

        // Create new Faculty object
        Faculty faculty = new Faculty(FacultyListController.facultyList.size() + 1, name, email, department);
        FacultyListController.facultyList.add(faculty);
        System.out.println("Faculty Added: " + faculty.getName());

        // ✅ Ensure FacultyListController is initialized before calling refreshTable()
        FacultyListController facultyListController = FacultyListController.getInstance();
        if (facultyListController != null) {
            facultyListController.refreshTable();
        } else {
            System.out.println("ERROR: FacultyListController is NULL!");
        }

        // Show success message
        showAlert("Success", "Faculty saved successfully!");

        // Switch to Faculty List Scene
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fmp/views/faculty-list.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.setScene(new Scene(root, 1366, 768));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method to show an alert
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}