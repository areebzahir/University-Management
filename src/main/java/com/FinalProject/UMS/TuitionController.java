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


public class TuitionController {


    @FXML
    private TableView<Tuition> tuitionTable;


    @FXML
    public void initialize() {
        // Example data for tuition
        ObservableList<Tuition> tuitionRecords = FXCollections.observableArrayList(
                new Tuition("Fall 2023", "$5,000", "$4,500", "Partially Paid"),
                new Tuition("Spring 2024", "$5,000", "$0", "Unpaid"),
                new Tuition("Summer 2024", "$3,000", "$0", "Unpaid")
        );


        // Add data to the table
        tuitionTable.setItems(tuitionRecords);
    }


    @FXML
    protected void onReturnButtonClick() {
        try {
            // Load the main page (studentmanagecontroller.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("studentmanagecontroller.fxml"));
            Parent root = loader.load();


            // Get the current stage (window)
            Stage stage = (Stage) tuitionTable.getScene().getWindow();


            // Set the new scene
            Scene scene = new Scene(root, 1920, 1080);
            stage.setScene(scene);
            stage.setTitle("Main Page");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

