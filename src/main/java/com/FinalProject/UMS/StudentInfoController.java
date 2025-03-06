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


public class StudentInfoController {
    @FXML
    private TableView<Student> studentTable;


    @FXML
    public void initialize() {
        // Create sample data with Student IDs
        ObservableList<Student> students = FXCollections.observableArrayList(
                new Student("Abadeh", "Parya", "pabadeh@uoguelph.ca", "TA-build-grade", "132021"),
                new Student("Adeniran", "Oluwatamilore", "oaden101@uoguelph.ca", "Student", "322002"),
                new Student("Alkhateeb", "Mohamad", "alkhatem@uoguelph.ca", "Student", "346303"),
                new Student("Ashraf", "Aryan", "aashra02@uoguelph.ca", "Student", "234304"),
                new Student("Hijazi", "Saleh", "shijazi@uoguelph.ca", "Student", "233245"),
                new Student("Abadeh", "Parya", "pabadeh@uoguelph.ca", "TA-build-grade", "132021"),
                new Student("Adeniran", "Oluwatamilore", "oaden101@uoguelph.ca", "Student", "322002"),
                new Student("Alkhateeb", "Mohamad", "alkhatem@uoguelph.ca", "Student", "346303"),
                new Student("Ashraf", "Aryan", "aashra02@uoguelph.ca", "Student", "234304"),
                new Student("Hijazi", "Saleh", "shijazi@uoguelph.ca", "Student", "233245"),
                new Student("Abadeh", "Parya", "pabadeh@uoguelph.ca", "TA-build-grade", "132021"),
                new Student("Adeniran", "Oluwatamilore", "oaden101@uoguelph.ca", "Student", "322002"),
                new Student("Alkhateeb", "Mohamad", "alkhatem@uoguelph.ca", "Student", "346303"),
                new Student("Ashraf", "Aryan", "aashra02@uoguelph.ca", "Student", "234304"),
                new Student("Hijazi", "Saleh", "shijazi@uoguelph.ca", "Student", "233245"),
                new Student("Abadeh", "Parya", "pabadeh@uoguelph.ca", "TA-build-grade", "132021"),
                new Student("Adeniran", "Oluwatamilore", "oaden101@uoguelph.ca", "Student", "322002"),
                new Student("Alkhateeb", "Mohamad", "alkhatem@uoguelph.ca", "Student", "346303"),
                new Student("Ashraf", "Aryan", "aashra02@uoguelph.ca", "Student", "234304"),
                new Student("Hijazi", "Saleh", "shijazi@uoguelph.ca", "Student", "233245"),
                new Student("Abadeh", "Parya", "pabadeh@uoguelph.ca", "TA-build-grade", "132021"),
                new Student("Adeniran", "Oluwatamilore", "oaden101@uoguelph.ca", "Student", "322002"),
                new Student("Alkhateeb", "Mohamad", "alkhatem@uoguelph.ca", "Student", "346303"),
                new Student("Ashraf", "Aryan", "aashra02@uoguelph.ca", "Student", "234304"),
                new Student("Hijazi", "Saleh", "shijazi@uoguelph.ca", "Student", "233245")
        );


        // Add data to the table
        studentTable.setItems(students);
    }


    @FXML
    protected void onReturnButtonClick() {
        try {
            // Load the studentmanagecontroller.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("studentmanagecontroller.fxml"));
            Parent root = loader.load();


            // Get the current stage (window)
            Stage stage = (Stage) studentTable.getScene().getWindow();


            // Set the new scene
            Scene scene = new Scene(root, 1366, 768);
            stage.setScene(scene);
            stage.setTitle("Hello");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



