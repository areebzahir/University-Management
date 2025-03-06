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




