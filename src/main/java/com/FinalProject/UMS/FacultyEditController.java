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

    private static String selectedFacultyId;

    public void setFacultyData(String id, String name, String email, String department) {
        selectedFacultyId = id;
        nameField.setText(name);
        emailField.setText(email);
        departmentField.setText(department);
    }

    @FXML
    private void saveFaculty() throws IOException {
        String updatedName = nameField.getText();
        String updatedEmail = emailField.getText();
        String updatedDepartment = departmentField.getText();

        System.out.println("Updated Faculty: " + updatedName);

        ObservableList<Faculty> facultyList = FacultyListController.facultyList;
        for (Faculty faculty : facultyList) {
            if (String.valueOf(faculty.getId()).equals(selectedFacultyId)) {
                faculty.setName(updatedName);
                faculty.setEmail(updatedEmail);
                faculty.setDepartment(updatedDepartment);
                break;
            }
        }

        FacultyListController.getInstance().refreshTable();

        goBack();
    }

    @FXML
    private void goBack() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fmp/views/faculty-list.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.setScene(new Scene(root, 1366, 768));
    }
}