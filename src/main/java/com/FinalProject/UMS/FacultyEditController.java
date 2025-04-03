package com.FinalProject.UMS;


import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class FacultyEditController {
    @FXML private TextField idField, nameField, degreeField, researchField,
            emailField, officeField, coursesField, passwordField;
    private Faculty faculty;
    private FacultyListController listController;


    public void initData(Faculty faculty, FacultyListController listController) {
        this.faculty = faculty;
        this.listController = listController;


        // Populate fields
        idField.setText(faculty.getId());
        nameField.setText(faculty.getName());
        degreeField.setText(faculty.getDegree());
        researchField.setText(faculty.getResearchInterest());
        emailField.setText(faculty.getEmail());
        officeField.setText(faculty.getOfficeLocation());
        coursesField.setText(faculty.getCoursesOffered());
        passwordField.setText(faculty.getPassword());
    }


    @FXML
    private void saveChanges() {
        faculty.setName(nameField.getText());
        faculty.setDegree(degreeField.getText());
        faculty.setResearchInterest(researchField.getText());
        faculty.setEmail(emailField.getText());
        faculty.setOfficeLocation(officeField.getText());
        faculty.setCoursesOffered(coursesField.getText());
        faculty.setPassword(passwordField.getText());


        listController.updateFaculty(faculty);
        ((Stage) idField.getScene().getWindow()).close();
    }


    @FXML
    private void cancel() {
        ((Stage) idField.getScene().getWindow()).close();
    }
}


