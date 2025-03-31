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

import java.io.IOException;
import java.util.Map;

public class AZAdminaddstudent {


    // Table components
    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, String> idCol;
    @FXML private TableColumn<Student, String> nameCol;
    @FXML private TableColumn<Student, String> emailCol;
    @FXML private TableColumn<Student, String> levelCol;
    @FXML private TableColumn<Student, String> semesterCol;
    @FXML private Button returnButton;

    // Form fields
    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextField addressField;
    @FXML private TextField telephoneField;
    @FXML private TextField emailField;
    @FXML private ComboBox<String> academicLevelCombo;
    @FXML private ComboBox<String> semesterCombo;
    @FXML private TextField subjectsField;
    @FXML private TextField thesisField;
    @FXML private TextField progressField;

    // Data management
    private ObservableList<Student> studentList = FXCollections.observableArrayList();
    private ExcelDatabase studentDatabase = new ExcelDatabase();
    private static final String FILE_PATH = "C:\\Users\\azahi\\OneDrive - University of Guelph\\1st Year\\Semester 2\\University-Management\\src\\main\\resources\\UMS_Data.xlsx";
    private static final String STUDENT_SHEET_NAME = "Students";

    @FXML
    public void initialize() {
        setupTableColumns();
        loadStudentsFromExcel();
        initializeComboBoxes();
        setupTableSelectionListener();
    }

    private void setupTableColumns() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        levelCol.setCellValueFactory(new PropertyValueFactory<>("academicLevel"));
        semesterCol.setCellValueFactory(new PropertyValueFactory<>("currentSemester"));

        studentTable.setItems(studentList);
    }

    private void loadStudentsFromExcel() {
        studentList.clear();

        // Get all users from the database
        Map<String, User> users = ExcelDatabase.loadUsers();

        // Process only unique students (users may be in the map twice - once by ID and once by email)
        for (User user : users.values()) {
            // Check if this is a student by ID format (assuming student IDs start with 'S')
            if (user.getId() != null && user.getId().startsWith("S")) {
                // Check if this student is already in our list
                boolean exists = false;
                for (Student existingStudent : studentList) {
                    if (existingStudent.getStudentId().equals(user.getId())) {
                        exists = true;
                        break;
                    }
                }

                if (!exists) {
                    Student student = new Student(
                            user.getId(),
                            user.getName(),
                            user.getAddress(),
                            user.getTelephone(),
                            user.getEmail(),
                            user.getAcademicLevel(),
                            user.getCurrentSemester(),
                            user.getProfilePhoto(),
                            user.getSubjectsRegistered(),
                            user.getThesisTitle(),
                            user.getProgress(),
                            user.getPassword()
                    );
                    studentList.add(student);
                }
            }
        }
    }
    @FXML
    public void addStudent() {
        try {
            Student newStudent = createStudentFromFields();
            if (newStudent != null) {
                // Use the new updateStudent method which handles both add and edit
                ExcelDatabase.updateStudent(newStudent);

                // Refresh the table
                loadStudentsFromExcel();
                clearForm();
                showAlert("Success", "Student added successfully");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add student: " + e.getMessage());
        }
    }

    @FXML
    private void editStudent() {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            try {
                Student updatedStudent = createStudentFromFields();
                if (updatedStudent != null) {
                    // Use the updateStudent method which handles both add and edit
                    ExcelDatabase.updateStudent(updatedStudent);

                    // Refresh the table
                    loadStudentsFromExcel();
                    clearForm();
                    showAlert("Success", "Student updated successfully");
                }
            } catch (Exception e) {
                showAlert("Error", "Failed to update student: " + e.getMessage());
            }
        } else {
            showAlert("No Selection", "Please select a student to edit.");
        }
    }

    @FXML
    private void deleteStudent() {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            // Confirm deletion
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Delete Student");
            alert.setContentText("Are you sure you want to delete " + selectedStudent.getName() + "?");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // Use the new deleteStudent method from ExcelDatabase
                    boolean success = ExcelDatabase.deleteStudent(selectedStudent.getStudentId());

                    if (success) {
                        // Remove from list and refresh
                        studentList.remove(selectedStudent);
                        clearForm();
                        showAlert("Success", "Student deleted successfully");
                    } else {
                        showAlert("Error", "Failed to delete student from database");
                    }
                }
            });
        } else {
            showAlert("No Selection", "Please select a student to delete.");
        }
    }
    private void initializeComboBoxes() {
        academicLevelCombo.getItems().addAll("Undergraduate", "Graduate");
        semesterCombo.getItems().addAll("Fall 2025", "Spring 2026", "Summer 2026");
    }

    private void setupTableSelectionListener() {
        studentTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        populateForm(newSelection);
                    }
                });
    }


    // Helper method to add a new student to Excel
    private void addStudentToExcel(User user) {
        try {
            org.apache.poi.ss.usermodel.Workbook workbook = null;
            java.io.FileInputStream fis = null;
            java.io.FileOutputStream fos = null;

            try {
                java.io.File excelFile = new java.io.File(FILE_PATH);
                fis = new java.io.FileInputStream(excelFile);
                workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook(fis);

                org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheet(STUDENT_SHEET_NAME);
                if (sheet == null) {
                    throw new Exception("Student sheet not found in Excel file");
                }

                // Find the next available row
                int nextRow = sheet.getLastRowNum() + 1;
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(nextRow);

                // Create cells and set values
                row.createCell(0).setCellValue(user.getId()); // ID
                row.createCell(1).setCellValue(user.getName()); // Name
                row.createCell(2).setCellValue(user.getAddress()); // Address
                row.createCell(3).setCellValue(user.getTelephone()); // Telephone
                row.createCell(4).setCellValue(user.getEmail()); // Email
                row.createCell(5).setCellValue(user.getAcademicLevel()); // Academic Level
                row.createCell(6).setCellValue(user.getCurrentSemester()); // Current Semester
                row.createCell(7).setCellValue(user.getProfilePhoto()); // Profile Photo
                row.createCell(8).setCellValue(user.getSubjectsRegistered()); // Subjects
                row.createCell(9).setCellValue(user.getThesisTitle()); // Thesis
                row.createCell(10).setCellValue(user.getProgress()); // Progress
                row.createCell(11).setCellValue(user.getPassword()); // Password

                // Write changes to file
                fos = new java.io.FileOutputStream(FILE_PATH);
                workbook.write(fos);

            } finally {
                if (fis != null) fis.close();
                if (fos != null) fos.close();
                if (workbook != null) workbook.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to save student to Excel: " + e.getMessage());
        }
    }

    @FXML
    private void handleReturnToDashboard() {
        try {
            // Return to main dashboard in the current window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("admin-student-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) returnButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Admin Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not return to dashboard");
        }
    }

    private void populateForm(Student student) {
        idField.setText(student.getStudentId());
        nameField.setText(student.getName());
        addressField.setText(student.getAddress());
        telephoneField.setText(student.getTelephone());
        emailField.setText(student.getEmail());
        academicLevelCombo.setValue(student.getAcademicLevel());
        semesterCombo.setValue(student.getCurrentSemester());
        subjectsField.setText(student.getSubjectsRegistered());
        thesisField.setText(student.getThesisTitle());
        progressField.setText(student.getProgress());
    }


    // Helper method to delete a student from Excel
    private void deleteStudentFromExcel(Student student) {
        try {
            org.apache.poi.ss.usermodel.Workbook workbook = null;
            java.io.FileInputStream fis = null;
            java.io.FileOutputStream fos = null;

            try {
                java.io.File excelFile = new java.io.File(FILE_PATH);
                fis = new java.io.FileInputStream(excelFile);
                workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook(fis);

                org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheet(STUDENT_SHEET_NAME);
                if (sheet == null) {
                    throw new Exception("Student sheet not found in Excel file");
                }

                // Find the row to delete
                int rowToDelete = -1;
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    org.apache.poi.ss.usermodel.Row row = sheet.getRow(i);
                    if (row != null) {
                        org.apache.poi.ss.usermodel.Cell idCell = row.getCell(0);
                        if (idCell != null) {
                            String id = idCell.getStringCellValue();
                            if (id.equals(student.getStudentId())) {
                                rowToDelete = i;
                                break;
                            }
                        }
                    }
                }

                if (rowToDelete != -1) {
                    // Remove the row
                    sheet.removeRow(sheet.getRow(rowToDelete));

                    // Shift up all rows below the deleted one
                    if (rowToDelete < sheet.getLastRowNum()) {
                        sheet.shiftRows(rowToDelete + 1, sheet.getLastRowNum(), -1);
                    }

                    // Write changes to file
                    fos = new java.io.FileOutputStream(FILE_PATH);
                    workbook.write(fos);
                } else {
                    showAlert("Warning", "Student not found in Excel file");
                }

            } finally {
                if (fis != null) fis.close();
                if (fos != null) fos.close();
                if (workbook != null) workbook.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to delete student from Excel: " + e.getMessage());
        }
    }

    @FXML
    private void viewProfile() {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Student Profile");
            alert.setHeaderText(selectedStudent.getName() + " (" + selectedStudent.getStudentId() + ")");
            alert.setContentText(formatStudentDetails(selectedStudent));
            alert.setResizable(true);
            alert.getDialogPane().setPrefSize(400, 300);
            alert.showAndWait();
        } else {
            showAlert("No Selection", "Please select a student to view.");
        }
    }

    private Student createStudentFromFields() {
        // Validate required fields
        if (nameField.getText().isEmpty() || emailField.getText().isEmpty() ||
                academicLevelCombo.getValue() == null || semesterCombo.getValue() == null) {
            showAlert("Input Error", "Please fill in all required fields (Name, Email, Academic Level, Semester)");
            return null;
        }

        // Generate ID if new student
        String studentId = idField.getText().isEmpty() ?
                generateNewStudentId() : idField.getText();

        return new Student(
                studentId,
                nameField.getText(),
                addressField.getText(),
                telephoneField.getText(),
                emailField.getText(),
                academicLevelCombo.getValue(),
                semesterCombo.getValue(),
                "default", // profile photo
                subjectsField.getText(),
                thesisField.getText(),
                progressField.getText().isEmpty() ? "0.0" : progressField.getText(),
                "default123" // password
        );
    }

    private String generateNewStudentId() {
        if (studentList.isEmpty()) {
            return "S20250001";
        }

        try {
            String lastId = studentList.get(studentList.size() - 1).getStudentId();
            int num = Integer.parseInt(lastId.substring(1));
            return String.format("S%08d", num + 3);
        } catch (NumberFormatException e) {
            return "S20250001";
        }
    }

    private String formatStudentDetails(Student student) {
        return String.format(
                "Address: %s\nTelephone: %s\nEmail: %s\nAcademic Level: %s\n" +
                        "Current Semester: %s\nSubjects Registered: %s\n" +
                        "Thesis Title: %s\nProgress: %s",
                student.getAddress(), student.getTelephone(), student.getEmail(),
                student.getAcademicLevel(), student.getCurrentSemester(),
                student.getSubjectsRegistered(), student.getThesisTitle(),
                student.getProgress()
        );
    }

    private void clearForm() {
        idField.clear();
        nameField.clear();
        addressField.clear();
        telephoneField.clear();
        emailField.clear();
        academicLevelCombo.getSelectionModel().clearSelection();
        semesterCombo.getSelectionModel().clearSelection();
        subjectsField.clear();
        thesisField.clear();
        progressField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}