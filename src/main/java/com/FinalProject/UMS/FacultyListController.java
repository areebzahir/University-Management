package com.FinalProject.UMS;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.layout.HBox;
import java.io.IOException;

public class FacultyListController {

    @FXML
    private TableView<Faculty> facultyTable;
    @FXML
    private TableColumn<Faculty, Integer> idColumn;
    @FXML
    private TableColumn<Faculty, String> nameColumn, emailColumn, departmentColumn;
    @FXML
    private TableColumn<Faculty, Void> actionsColumn;

    public static ObservableList<Faculty> facultyList = FXCollections.observableArrayList();

    // Singleton instance to ensure only one controller exists
    private static FacultyListController instance;

    public FacultyListController() {
        instance = this;
    }

    // Returns the instance of FacultyListController
    public static FacultyListController getInstance() {
        if (instance == null) {
            System.out.println("ERROR: FacultyListController instance is null!");
        }
        return instance;
    }

    @FXML
    public void initialize() {
        System.out.println("Initializing Faculty List...");
        instance = this; // Assign instance inside initialize()

        if (facultyTable == null) {
            System.out.println("facultyTable is NULL! Make sure FXML is properly linked.");
            return;
        }

        // Set TableView column mappings
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));

        // Add sample data if the list is empty
        if (facultyList.isEmpty()) {
            facultyList.add(new Faculty(1, "John Doe", "john@example.com", "Computer Science"));
            facultyList.add(new Faculty(2, "Jane Smith", "jane@example.com", "Mathematics"));
        }

        facultyTable.setItems(facultyList);

        // Add "Edit" & "Delete" buttons to each row
        actionsColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Faculty, Void> call(final TableColumn<Faculty, Void> param) {
                return new TableCell<>() {
                    private final Button editButton = new Button("Edit");
                    private final Button deleteButton = new Button("Delete");

                    {
                        // Set action for edit button
                        editButton.setOnAction(event -> {
                            Faculty faculty = getTableView().getItems().get(getIndex());
                            try {
                                editFaculty(faculty);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });

                        // Set action for delete button
                        deleteButton.setOnAction(event -> {
                            Faculty faculty = getTableView().getItems().get(getIndex());
                            deleteFaculty(faculty);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox buttons = new HBox(5);
                            buttons.getChildren().addAll(editButton, deleteButton);
                            setGraphic(buttons);
                        }
                    }
                };
            }
        });
    }

    // Refreshes the table to reflect updated data
    public void refreshTable() {
        if (facultyTable != null) {
            facultyTable.refresh();
            System.out.println("Faculty Table refreshed.");
        } else {
            System.out.println("facultyTable is NULL, cannot refresh.");
        }
    }

    // Opens the edit faculty form
    private void editFaculty(Faculty faculty) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fmp/views/faculty-edit.fxml"));
        Parent root = loader.load();

        FacultyEditController editController = loader.getController();
        editController.setFacultyData(
                String.valueOf(faculty.getId()), faculty.getName(), faculty.getEmail(), faculty.getDepartment()
        );

        Stage stage = (Stage) facultyTable.getScene().getWindow();
        stage.setScene(new Scene(root, 400, 300));
    }

    // Deletes a faculty member from the list
    private void deleteFaculty(Faculty faculty) {
        facultyList.remove(faculty);
        refreshTable();
        System.out.println("Deleted Faculty: " + faculty.getName());
    }

    // Navigates back to the Faculty Form page
    @FXML
    private void goBack() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fmp/views/faculty-view.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) facultyTable.getScene().getWindow();
        stage.setScene(new Scene(root, 400, 300));
    }
}