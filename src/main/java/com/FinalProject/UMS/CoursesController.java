package com.FinalProject.UMS;


import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;


import java.io.IOException;


public class CoursesController {
    @FXML
    private ListView<String> coursesList;


    @FXML
    public void initialize() {
        // Sample data
        coursesList.getItems().addAll("Course 1", "Course 2", "Course 3");
    }


    @FXML
    protected void onReturnButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("studentmanagecontroller.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) coursesList.getScene().getWindow();
            Scene scene = new Scene(root, 1366, 768);
            stage.setScene(scene);
            stage.setTitle("Hello");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


//this
