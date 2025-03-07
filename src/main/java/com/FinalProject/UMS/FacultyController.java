package com.FinalProject.UMS;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
//import com.FinalProject.UMS.models.Faculty;

public class FacultyController implements Initializable {

    @FXML
    private Label pageTitle;

    @FXML
    private TextField nameField, emailField, departmentField;

    @FXML
    private Button saveButton, backButton, addFacultyButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showHomePage(); // Load the home screen initially
    }

    // Home Page Setup (Only shows the "Add Faculty" button)
    private void showHomePage() {
        pageTitle.setText("Faculty Management");
        nameField.setVisible(false);
        emailField.setVisible(false);
        departmentField.setVisible(false);
        saveButton.setVisible(false);
        backButton.setVisible(false);
        addFacultyButton.setVisible(true);
    }

    // Switch to the "Add Faculty" form (Hides Add button, shows input fields)
    @FXML
    private void showAddFacultyForm() {
        pageTitle.setText("Add Faculty");
        nameField.setVisible(true);
        emailField.setVisible(true);
        departmentField.setVisible(true);
        saveButton.setVisible(true);
        backButton.setVisible(true);
        addFacultyButton.setVisible(false);
    }

    // Saves a new faculty and switches to Faculty List Page
    @FXML
    private void saveFaculty() throws IOException {
        String name = nameField.getText();
        String email = emailField.getText();
        String department = departmentField.getText();

        // Validate inputs
        if (name.isEmpty() || email.isEmpty() || department.isEmpty()) {
            System.out.println("Error: All fields are required!");
            return;
        }

        //  Create new Faculty object and add to list
        Faculty newFaculty = new Faculty(FacultyListController.facultyList.size() + 1, name, email, department);
        FacultyListController.facultyList.add(newFaculty);
        System.out.println("Faculty Added: " + newFaculty.getName());

        // Load Faculty List Scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fmp/views/faculty-list.fxml"));
        Parent root = loader.load();

        // Refresh Faculty List Table
        FacultyListController listController = loader.getController();
        listController.refreshTable();

        // Switch to Faculty List Scene
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.setScene(new Scene(root, 600, 400)); // Adjust size if needed
    }

    // Handle Back Button (Returns to Home Page)
    @FXML
    private void handleBack() {
        showHomePage();
    }
}