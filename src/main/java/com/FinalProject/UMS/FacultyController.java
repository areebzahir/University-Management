package com.FinalProject.UMS;


import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class FacultyController {
    @FXML private TextField nameField, degreeField, researchField,
            emailField, officeField, coursesField, passwordField;
    private FacultyListController listController;


    public void setFacultyListController(FacultyListController controller) {
        this.listController = controller;
    }


    @FXML
    private void saveFaculty() {
        Faculty faculty = new Faculty(
                "", // ID will be auto-generated
                nameField.getText(),
                degreeField.getText(),
                researchField.getText(),
                emailField.getText(),
                officeField.getText(),
                coursesField.getText(),
                passwordField.getText()
        );
        listController.addFaculty(faculty);
        ((Stage) nameField.getScene().getWindow()).close();
    }


    @FXML
    private void cancel() {
        ((Stage) nameField.getScene().getWindow()).close();
    }
}
