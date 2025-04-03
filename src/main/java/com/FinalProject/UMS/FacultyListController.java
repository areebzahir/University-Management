package com.FinalProject.UMS;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.layout.HBox;
import java.io.IOException;

public class FacultyListController {
    @FXML private TableView<Faculty> facultyTable;
    @FXML private TableColumn<Faculty, String> idColumn, nameColumn, degreeColumn,
            researchColumn, emailColumn, officeColumn, coursesColumn;
    @FXML private TableColumn<Faculty, Void> actionsColumn;

    private final ObservableList<Faculty> facultyList = FXCollections.observableArrayList();
    private final FacultyExcelHandler excelHandler = new FacultyExcelHandler();

    @FXML
    public void initialize() {
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
        facultyTable.setItems(facultyList);
        facultyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Setup action buttons
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox buttons = new HBox(5, editBtn, deleteBtn);

            {
                editBtn.setOnAction(e -> {
                    Faculty faculty = getTableView().getItems().get(getIndex());
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/FinalProject/UMS/faculty-edit.fxml"));
                        Parent root = loader.load();
                        FacultyEditController controller = loader.getController();
                        controller.initData(faculty, FacultyListController.this);
                        Stage stage = new Stage();
                        stage.setScene(new Scene(root));
                        stage.setTitle("Edit Faculty");
                        stage.show();
                    } catch (IOException ex) {
                        showAlert("Error", "Failed to load edit window");
                    }
                });

                deleteBtn.setOnAction(e -> {
                    Faculty faculty = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirm Deletion");
                    alert.setHeaderText("Delete " + faculty.getName() + "?");
                    alert.setContentText("This cannot be undone.");
                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            facultyList.remove(faculty);
                            excelHandler.saveFaculty(facultyList);
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        });
    }

    @FXML
    private void handleAddFaculty() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/FinalProject/UMS/faculty-add.fxml"));
        Parent root = loader.load();
        FacultyController controller = loader.getController();
        controller.setFacultyListController(this);
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Add Faculty");
        stage.show();
    }

    @FXML
    private void handleBack() {
        try {
            // Get current stage and window dimensions
            Stage currentStage = (Stage) facultyTable.getScene().getWindow();
            double prevWidth = currentStage.getWidth();
            double prevHeight = currentStage.getHeight();

            // Load menu view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/FinalProject/UMS/menu-view.fxml"));
            Parent root = loader.load();


        } catch (IOException e) {
            System.err.println("Failed to load menu: " + e.getMessage());
            e.printStackTrace();

            // Fallback - close window if menu can't be loaded
            ((Stage) facultyTable.getScene().getWindow()).close();
        }
    }


    public void addFaculty(Faculty faculty) {
        if (faculty.getId() == null || faculty.getId().isEmpty()) {
            faculty.setId("FAC-" + (facultyList.size() + 1));
        }
        facultyList.add(faculty);
        excelHandler.saveFaculty(facultyList);
    }

    public void updateFaculty(Faculty faculty) {
        int index = facultyList.indexOf(faculty);
        if (index >= 0) {
            facultyList.set(index, faculty);
            excelHandler.saveFaculty(facultyList);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}