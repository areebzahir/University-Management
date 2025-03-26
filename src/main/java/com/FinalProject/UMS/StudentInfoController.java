package com.FinalProject.UMS;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class StudentInfoController {
    @FXML private TextField studentIdField;
    @FXML private TextField nameField;
    @FXML private TextField addressField;
    @FXML private TextField telephoneField;
    @FXML private TextField emailField;
    @FXML private ComboBox<String> academicLevelComboBox;
    @FXML private TextField currentSemesterField;
    @FXML private TextField profilePhotoField;
    @FXML private TextArea subjectsRegisteredArea;
    @FXML private TextField thesisTitleField;
    @FXML private TextField progressField;
    @FXML private PasswordField passwordField;
    @FXML private Button saveButton;
    @FXML private Button deleteButton;
    @FXML private Button addButton;

    private String currentStudentId;
    private String userRole;
    private static final String EXCEL_FILE_PATH = "UMS_data.xlsx";

    public void initializeWithStudent(String studentId, String role) {
        this.currentStudentId = studentId;
        this.userRole = role;
        loadAcademicLevels();
        loadStudentData();
        setupFieldRestrictions();
        adjustUIForRole();
    }

    private void loadAcademicLevels() {
        academicLevelComboBox.setItems(FXCollections.observableArrayList(
                "Undergraduate", "Graduate", "PhD", "Postdoc"
        ));
    }

    private void adjustUIForRole() {
        if ("ADMIN".equals(userRole)) {
            deleteButton.setVisible(true);
            addButton.setVisible(true);
            studentIdField.setEditable(true);
        } else {
            deleteButton.setVisible(false);
            addButton.setVisible(false);
            studentIdField.setEditable(false);
        }
    }

    private void loadStudentData() {
        try (FileInputStream file = new FileInputStream(new File(EXCEL_FILE_PATH));
             Workbook workbook = new XSSFWorkbook(file)) {

            Sheet sheet = workbook.getSheetAt(0);

            if (currentStudentId == null || currentStudentId.isEmpty()) {
                // New student mode - clear fields
                clearFields();
                return;
            }

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header

                String id = getSafeCellValue(row, 0);
                if (id.equals(currentStudentId)) {
                    studentIdField.setText(id);
                    nameField.setText(getSafeCellValue(row, 1));
                    addressField.setText(getSafeCellValue(row, 2));
                    telephoneField.setText(getSafeCellValue(row, 3));
                    emailField.setText(getSafeCellValue(row, 4));
                    academicLevelComboBox.setValue(getSafeCellValue(row, 5));
                    currentSemesterField.setText(getSafeCellValue(row, 6));
                    profilePhotoField.setText(getSafeCellValue(row, 7));
                    subjectsRegisteredArea.setText(getSafeCellValue(row, 8));
                    thesisTitleField.setText(getSafeCellValue(row, 9));
                    progressField.setText(getSafeCellValue(row, 10));
                    passwordField.setText(getSafeCellValue(row, 11));
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load student data", Alert.AlertType.ERROR);
        }
    }

    private void clearFields() {
        studentIdField.clear();
        nameField.clear();
        addressField.clear();
        telephoneField.clear();
        emailField.clear();
        academicLevelComboBox.getSelectionModel().clearSelection();
        currentSemesterField.clear();
        profilePhotoField.clear();
        subjectsRegisteredArea.clear();
        thesisTitleField.clear();
        progressField.clear();
        passwordField.clear();
    }

    private void setupFieldRestrictions() {
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Z\\s]*")) {
                nameField.setText(oldValue);
            }
        });

        emailField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                if (!emailField.getText().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                    emailField.setStyle("-fx-text-fill: red;");
                } else {
                    emailField.setStyle("-fx-text-fill: black;");
                }
            }
        });
    }

    @FXML
    private void handleSaveChanges() {
        if (!validateFields()) {
            return;
        }

        try (FileInputStream fis = new FileInputStream(new File(EXCEL_FILE_PATH));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            boolean isNewStudent = true;

            // Check if this is an existing student
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                if (getSafeCellValue(row, 0).equals(currentStudentId)) {
                    // Update existing student
                    updateStudentRow(row);
                    isNewStudent = false;
                    break;
                }
            }

            if (isNewStudent) {
                // Add new student
                Row newRow = sheet.createRow(sheet.getLastRowNum() + 1);
                populateStudentRow(newRow);
                currentStudentId = studentIdField.getText();
            }

            try (FileOutputStream fileOut = new FileOutputStream(EXCEL_FILE_PATH)) {
                workbook.write(fileOut);
                showAlert("Success", "Changes saved successfully", Alert.AlertType.INFORMATION);
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not save changes", Alert.AlertType.ERROR);
        }
    }

    private boolean validateFields() {
        if (studentIdField.getText().isEmpty() || nameField.getText().isEmpty() ||
                emailField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            showAlert("Error", "Please fill in all required fields", Alert.AlertType.ERROR);
            return false;
        }

        if (!emailField.getText().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            showAlert("Error", "Please enter a valid email address", Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    private void updateStudentRow(Row row) {
        row.getCell(0).setCellValue(studentIdField.getText());
        row.getCell(1).setCellValue(nameField.getText());
        row.getCell(2).setCellValue(addressField.getText());
        row.getCell(3).setCellValue(telephoneField.getText());
        row.getCell(4).setCellValue(emailField.getText());
        row.getCell(5).setCellValue(academicLevelComboBox.getValue());
        row.getCell(6).setCellValue(currentSemesterField.getText());
        row.getCell(7).setCellValue(profilePhotoField.getText());
        row.getCell(8).setCellValue(subjectsRegisteredArea.getText());
        row.getCell(9).setCellValue(thesisTitleField.getText());
        row.getCell(10).setCellValue(progressField.getText());
        row.getCell(11).setCellValue(passwordField.getText());
    }

    private void populateStudentRow(Row row) {
        row.createCell(0).setCellValue(studentIdField.getText());
        row.createCell(1).setCellValue(nameField.getText());
        row.createCell(2).setCellValue(addressField.getText());
        row.createCell(3).setCellValue(telephoneField.getText());
        row.createCell(4).setCellValue(emailField.getText());
        row.createCell(5).setCellValue(academicLevelComboBox.getValue());
        row.createCell(6).setCellValue(currentSemesterField.getText());
        row.createCell(7).setCellValue(profilePhotoField.getText());
        row.createCell(8).setCellValue(subjectsRegisteredArea.getText());
        row.createCell(9).setCellValue(thesisTitleField.getText());
        row.createCell(10).setCellValue(progressField.getText());
        row.createCell(11).setCellValue(passwordField.getText());
    }

    @FXML
    private void handleDeleteStudent() {
        if (currentStudentId == null || currentStudentId.isEmpty()) {
            showAlert("Error", "No student selected to delete", Alert.AlertType.ERROR);
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete Student Record");
        confirmAlert.setContentText("Are you sure you want to delete this student? This action cannot be undone.");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                deleteStudentFromExcel();
            }
        });
    }
    private void deleteStudentFromExcel() {
        try (FileInputStream fis = new FileInputStream(new File(EXCEL_FILE_PATH));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            int rowToDelete = -1;

            // Find the row to delete
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header

                if (getSafeCellValue(row, 0).equals(currentStudentId)) {
                    rowToDelete = row.getRowNum();
                    break;
                }
            }

            if (rowToDelete != -1) {
                // Remove the row
                sheet.removeRow(sheet.getRow(rowToDelete));

                // Shift rows up to fill the gap
                int lastRowNum = sheet.getLastRowNum();
                if (rowToDelete < lastRowNum) {
                    sheet.shiftRows(rowToDelete + 1, lastRowNum, -1);
                }

                try (FileOutputStream fileOut = new FileOutputStream(EXCEL_FILE_PATH)) {
                    workbook.write(fileOut);
                    showAlert("Success", "Student deleted successfully", Alert.AlertType.INFORMATION);
                    clearFields();
                    currentStudentId = null;
                }
            } else {
                showAlert("Error", "Student not found", Alert.AlertType.ERROR);
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not delete student", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleAddNewStudent() {
        clearFields();
        currentStudentId = null;
        studentIdField.setEditable(true);
    }

    @FXML
    private void handleChangePassword() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Enter new password");
        dialog.setContentText("Password:");

        dialog.showAndWait().ifPresent(newPassword -> {
            if (newPassword.length() < 6) {
                showAlert("Error", "Password must be at least 6 characters", Alert.AlertType.ERROR);
            } else {
                passwordField.setText(newPassword);
            }
        });
    }

    @FXML
    private void handleUploadPhoto() {
        profilePhotoField.setText("path/to/uploaded/photo.jpg");
    }

    private String getSafeCellValue(Row row, int cellIndex) {
        if (row == null) return "";
        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        return cell != null ? getStringCellValue(cell) : "";
    }

    private String getStringCellValue(Cell cell) {
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell).trim();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleReturn() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("studentdashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) studentIdField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}