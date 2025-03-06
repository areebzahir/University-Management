package com.FinalProject.UMS;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

public class SubjectManagementController implements Initializable {

    @FXML
    private TextField subjectNameField;

    @FXML
    private TextField subjectCodeField;

    @FXML
    private Button addButton;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private TableView subjectTable;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Initialization logic here
    }
    @FXML
    void addSubject(ActionEvent event) {
        // Code to handle adding a subject
        System.out.println("Add Subject button clicked");
    }

    @FXML
    void searchSubject(ActionEvent event) {
        // Code to handle searching for a subject
        System.out.println("Search Subject button clicked");
    }

    @FXML
    void deleteSubject(ActionEvent event) {
        // Code to handle deleting a subject
        System.out.println("Delete Subject button clicked");
    }
}
