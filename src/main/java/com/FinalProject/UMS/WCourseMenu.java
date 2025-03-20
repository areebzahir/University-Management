package com.FinalProject.UMS;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class WCourseMenu {

    @FXML
    private void openCourseManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/FinalProject/UMS/CourseManagement-view.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Course Management");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}