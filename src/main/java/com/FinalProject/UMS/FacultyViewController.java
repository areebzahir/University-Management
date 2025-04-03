package com.FinalProject.UMS;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


public class FacultyViewController implements Initializable {


    private static final Logger LOGGER = Logger.getLogger(FacultyViewController.class.getName());


    @FXML private TableView<Faculty> facultyTableView;
    @FXML private TableColumn<Faculty, String> idColumn;
    @FXML private TableColumn<Faculty, String> nameColumn;
    @FXML private TableColumn<Faculty, String> degreeColumn;
    @FXML private TableColumn<Faculty, String> researchColumn;
    @FXML private TableColumn<Faculty, String> emailColumn;
    @FXML private TableColumn<Faculty, String> officeColumn;
    @FXML private TableColumn<Faculty, String> coursesColumn;


    private final ObservableList<Faculty> facultyList = FXCollections.observableArrayList();
    private final FacultyExcelHandler excelHandler = new FacultyExcelHandler();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Setup columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        degreeColumn.setCellValueFactory(new PropertyValueFactory<>("degree"));
        researchColumn.setCellValueFactory(new PropertyValueFactory<>("researchInterest"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        officeColumn.setCellValueFactory(new PropertyValueFactory<>("officeLocation"));
        coursesColumn.setCellValueFactory(new PropertyValueFactory<>("coursesOffered"));


        // Load data
        facultyList.setAll(excelHandler.readFaculty());
        facultyTableView.setItems(facultyList);
        facultyTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


        // Add row click handler
        facultyTableView.setRowFactory(tv -> {
            TableRow<Faculty> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && !row.isEmpty()) {
                    Faculty faculty = row.getItem();
                    showFacultyDetails(faculty);
                }
            });
            return row;
        });
    }


    private void showFacultyDetails(Faculty faculty) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/FinalProject/UMS/facultyDetailsPopup.fxml"));
            Parent root = loader.load();


            FacultyDetailsController controller = loader.getController();
            controller.setFacultyData(faculty);


            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Faculty Details - " + faculty.getName());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load faculty details popup", e);
            showAlert("Error", "Failed to load faculty details");
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    @FXML
    private void handleBack() {
        ((Stage) facultyTableView.getScene().getWindow()).close();
    }
}

