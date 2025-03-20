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


public class GradesController {


    @FXML
    private TableView<Grade> gradesTable;


    @FXML
    public void initialize() {
        // Example data for grades
        ObservableList<Grade> grades = FXCollections.observableArrayList(
                new Grade("CHBM*1040*0355", "General Chemistry I", "0.50", "077"),
                new Grade("ENGG*1100*0211", "Engineering and Design I", "0.75", "084"),
                new Grade("ENGG*1410*01034", "Intro Program for Engineers", "0.50", "070"),
                new Grade("MATH*1200*0302", "Calculus I", "0.50", "088"),
                new Grade("PHYS*1130*0109", "Intr Physics With Applications", "0.50", "079")
        );


        // Add data to the table
        gradesTable.setItems(grades);
    }


    @FXML
    protected void onReturnButtonClick() {
        try {
            // Load the main page (studentmanagecontroller.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("studentmanagecontroller.fxml"));
            Parent root = loader.load();


            // Get the current stage (window)
            Stage stage = (Stage) gradesTable.getScene().getWindow();


            // Set the new scene
            Scene scene = new Scene(root, 1366, 768);
            stage.setScene(scene);
            stage.setTitle("Main Page");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


