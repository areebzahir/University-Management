package com.FinalProject.UMS;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

import javafx.collections.ObservableList;

public class FacultyEditController {

    @FXML
    private TextField nameField, emailField, departmentField;

    private static String selectedFacultyId; // Stores the ID of the faculty being edited

    // Sets faculty data in the input fields when editing
    public void setFacultyData(String id, String name, String email, String department) {
        selectedFacultyId = id;
        nameField.setText(name);
        emailField.setText(email);
        departmentField.setText(department);
    }

    // Saves the updated faculty details
    @FXML
    private void saveFaculty() throws IOException {
        String updatedName = nameField.getText();
        String updatedEmail = nameField.getText();
        String updatedDepartment = departmentField.getText();

        System.out.println("Updated Faculty: " + updatedName);

        // Find and update the faculty in the list
        ObservableList<Faculty> facultyList = FacultyListController.facultyList;
        for (Faculty faculty : facultyList) {
            if (String.valueOf(faculty.getId()).equals(selectedFacultyId)) {
                faculty.setName(updatedName);
                faculty.setEmail(updatedEmail);
                faculty.setDepartment(updatedDepartment);
                break;
            }
        }

        // Refresh the faculty list table
        FacultyListController.getInstance().refreshTable();

        // Return to the faculty list page
        goBack();
    }

    // Navigates back to the faculty list page
    @FXML
    private void goBack() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fmp/views/faculty-list.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.setScene(new Scene(root, 400, 300));
    }
}