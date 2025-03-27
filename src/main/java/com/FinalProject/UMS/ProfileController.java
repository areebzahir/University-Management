package com.FinalProject.UMS;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

public class ProfileController {
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField emailField;

    private String loggedInStudentId;

    public void setLoggedInStudentId(String studentId) {
        this.loggedInStudentId = studentId;
        loadStudentData();
    }

    private void loadStudentData() {
        Student student = StudentDatabase.getStudentById(loggedInStudentId);
        if (student != null) {
            // Populate the text fields with the student's data
            firstNameField.setText(student.getName().split(" ")[0]); // Assuming first name is the first word
            lastNameField.setText(student.getName().split(" ").length > 1 ? student.getName().split(" ")[1] : ""); // Assuming last name is the last word
            emailField.setText(student.getEmail());
        } else {
            // Handle the case where the student is not found
            System.out.println("Student not found with ID: " + loggedInStudentId);
        }
    }

    @FXML
    protected void onSaveChangesButtonClick() {
        // Save changes logic
    }

    @FXML
    protected void onChangePasswordButtonClick() {
        // Change password logic
    }

    @FXML
    protected void onUploadPictureButtonClick() {
        // Upload picture logic
    }

    @FXML
    protected void onReturnButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("studentmanagecontroller.fxml"));
            Parent root = loader.load();

            // Get the controller for the student management view
            StudentManagementMenuController studentManagementController = loader.getController();

            // Pass the logged-in student ID to the student management controller
            studentManagementController.setLoggedInStudentId(loggedInStudentId);

            Stage stage = (Stage) firstNameField.getScene().getWindow();
            Scene scene = new Scene(root, 1366, 768);
            stage.setScene(scene);
            stage.setTitle("Hello");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



