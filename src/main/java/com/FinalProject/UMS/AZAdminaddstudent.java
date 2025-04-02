package com.FinalProject.UMS;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.util.*;
import java.util.Optional;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.IOException;
import java.util.Map;
import javafx.application.Platform;

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

        // Process only unique students
        for (User user : users.values()) {
            if (user.getId() != null && user.getId().startsWith("S")) {
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

        // Create a new ObservableList and set it to the table
        ObservableList<Student> observableStudentList = FXCollections.observableArrayList(studentList);
        studentTable.setItems(observableStudentList);
        studentTable.refresh(); // Force a refresh of the table view
    }

    @FXML
    public void addStudent() {
        try {
            // Validate required fields
            if (nameField.getText().isEmpty() || emailField.getText().isEmpty() ||
                    academicLevelCombo.getValue() == null || semesterCombo.getValue() == null) {
                showAlert("Input Error", "Please fill in all required fields (Name, Email, Academic Level, Semester)");
                return;
            }

            // Generate unique student ID if empty
            String studentId = idField.getText();
            if (studentId == null || studentId.isEmpty()) {
                studentId = ExcelDatabase.generateUniqueStudentId();
            }

            // Create student object with default password
            Student newStudent = new Student(
                    studentId,
                    nameField.getText(),
                    addressField.getText(),
                    telephoneField.getText(),
                    emailField.getText(),
                    academicLevelCombo.getValue(),
                    semesterCombo.getValue(),
                    "default", // Default profile photo
                    subjectsField.getText(),
                    thesisField.getText(),
                    progressField.getText().isEmpty() ? "0.0" : progressField.getText(),
                    "default123" // Default password
            );

            // Add student to database
            ExcelDatabase.updateStudent(newStudent);

            // Add the new student to the current list
            studentList.add(newStudent);

            // Update the table - create a new ObservableList from our updated studentList
            ObservableList<Student> observableStudentList = FXCollections.observableArrayList(studentList);
            studentTable.setItems(observableStudentList);

            // Force a refresh of the table to show the new data
            studentTable.refresh();

            // Clear the form
            clearForm();
            showAlert("Success", "Student added successfully");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add student: " + e.getMessage());

            // If there was an error, reload from Excel to ensure consistency
            loadStudentsFromExcel();
        }
    }



    @FXML
    private void editStudent() {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            showAlert("No Selection", "Please select a student to edit.");
            return;
        }

        try {
            // Validate required fields
            if (nameField.getText().isEmpty() || emailField.getText().isEmpty() ||
                    academicLevelCombo.getValue() == null || semesterCombo.getValue() == null) {
                showAlert("Input Error", "Please fill in all required fields (Name, Email, Academic Level, Semester)");
                return;
            }

            // Create updated student object
            // Keep the same ID and password
            Student updatedStudent = new Student(
                    selectedStudent.getStudentId(), // Keep original ID
                    nameField.getText(),
                    addressField.getText(),
                    telephoneField.getText(),
                    emailField.getText(),
                    academicLevelCombo.getValue(),
                    semesterCombo.getValue(),
                    selectedStudent.getProfilePhoto(), // Keep original profile photo
                    subjectsField.getText(),
                    thesisField.getText(),
                    progressField.getText().isEmpty() ? "0.0" : progressField.getText(),
                    selectedStudent.getPassword() // Keep original password
            );

            // Update student in database
            ExcelDatabase.updateStudent(updatedStudent);

            // Refresh the table
            loadStudentsFromExcel();
            clearForm();
            showAlert("Success", "Student updated successfully");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to update student: " + e.getMessage());
        }
    }

    private void deleteStudent() {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            showAlert("No Selection", "Please select a student to delete.");
            return;
        }

        // Confirm deletion
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete Student");
        alert.setContentText("Are you sure you want to delete " + selectedStudent.getName() + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Delete from database
                boolean success = ExcelDatabase.deleteStudent(selectedStudent.getStudentId());

                if (success) {
                    // Remove from table and clear form
                    studentList.remove(selectedStudent);
                    clearForm();
                    showAlert("Success", "Student deleted successfully");
                } else {
                    showAlert("Error", "Failed to delete student from database");
                }
            }
        });
    }

    // Helper method to generate a unique student ID
    private String generateUniqueStudentId() {
        // Get current timestamp for unique ID base
        String timeStamp = String.valueOf(System.currentTimeMillis());
        return "STU" + timeStamp.substring(timeStamp.length() - 6);
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

    // Add these fields to your class
    @FXML private Button tuitionManagementButton;
    private Map<String, TuitionRecord> tuitionRecords = new HashMap<>();

    // Add this class inside your file or as a separate file
    public class TuitionRecord {
        private String studentId;
        private String semester;
        private double totalFee;
        private double paidAmount;
        private double outstandingAmount;
        private double scholarshipAmount;
        private double grantAmount;
        private double osapAmount;
        private boolean isPaid;
        private List<PaymentHistory> paymentHistory;

        public TuitionRecord(String studentId, String semester, double totalFee) {
            this.studentId = studentId;
            this.semester = semester;
            this.totalFee = totalFee;
            this.paidAmount = 0.0;
            this.outstandingAmount = totalFee;
            this.scholarshipAmount = 0.0;
            this.grantAmount = 0.0;
            this.osapAmount = 0.0;
            this.isPaid = false;
            this.paymentHistory = new ArrayList<>();
        }

        // Getters and setters
        public String getStudentId() { return studentId; }
        public String getSemester() { return semester; }
        public double getTotalFee() { return totalFee; }
        public double getPaidAmount() { return paidAmount; }
        public double getOutstandingAmount() { return outstandingAmount; }
        public double getScholarshipAmount() { return scholarshipAmount; }
        public double getGrantAmount() { return grantAmount; }
        public double getOsapAmount() { return osapAmount; }
        public boolean isPaid() { return isPaid; }
        public List<PaymentHistory> getPaymentHistory() { return paymentHistory; }

        public void addPayment(double amount, String paymentType, String date) {
            this.paidAmount += amount;
            this.outstandingAmount -= amount;

            if (this.outstandingAmount <= 0) {
                this.isPaid = true;
            }

            this.paymentHistory.add(new PaymentHistory(amount, paymentType, date));
        }

        public void addScholarship(double amount, String name, String date) {
            this.scholarshipAmount += amount;
            this.outstandingAmount -= amount;

            if (this.outstandingAmount <= 0) {
                this.isPaid = true;
            }

            this.paymentHistory.add(new PaymentHistory(amount, "Scholarship: " + name, date));
        }

        public void addGrant(double amount, String name, String date) {
            this.grantAmount += amount;
            this.outstandingAmount -= amount;

            if (this.outstandingAmount <= 0) {
                this.isPaid = true;
            }

            this.paymentHistory.add(new PaymentHistory(amount, "Grant: " + name, date));
        }

        public void addOSAP(double amount, String date) {
            this.osapAmount += amount;
            this.outstandingAmount -= amount;

            if (this.outstandingAmount <= 0) {
                this.isPaid = true;
            }

            this.paymentHistory.add(new PaymentHistory(amount, "OSAP", date));
        }
    }

    public class PaymentHistory {
        private double amount;
        private String paymentType;
        private String date;

        public PaymentHistory(double amount, String paymentType, String date) {
            this.amount = amount;
            this.paymentType = paymentType;
            this.date = date;
        }

        public double getAmount() { return amount; }
        public String getPaymentType() { return paymentType; }
        public String getDate() { return date; }
    }
    @FXML
    private void handleAcademicProgress() {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            showAlert("No Selection", "Please select a student to view academic progress.");
            return;
        }

        // Create the dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Academic Progress - " + selectedStudent.getName());
        dialog.setHeaderText("Student ID: " + selectedStudent.getStudentId() + " | Semester: " + selectedStudent.getCurrentSemester());

        // Set the button types
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);

        // Create the academic progress display
        VBox content = new VBox(10);
        content.setPadding(new javafx.geometry.Insets(20, 20, 10, 10));

        // Hardcoded academic data
        double termGPA = 3.75;
        double overallGPA = 3.62;

        // Add GPA summary
        Label gpaSummary = new Label("GPA Summary");
        gpaSummary.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        content.getChildren().add(gpaSummary);
        content.getChildren().add(new Label("Current Term GPA: " + String.format("%.2f", termGPA)));
        content.getChildren().add(new Label("Overall GPA: " + String.format("%.2f", overallGPA)));

        // Add grades table
        Label gradesLabel = new Label("\nCurrent Grades");
        gradesLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        content.getChildren().add(gradesLabel);

        // Ensure CourseGrade class is correctly defined
        class CourseGrade {
            private final SimpleStringProperty courseCode;
            private final SimpleStringProperty courseName;
            private final SimpleStringProperty grade;
            private final SimpleDoubleProperty gpaValue;

            public CourseGrade(String courseCode, String courseName, String grade, double gpaValue) {
                this.courseCode = new SimpleStringProperty(courseCode);
                this.courseName = new SimpleStringProperty(courseName);
                this.grade = new SimpleStringProperty(grade);
                this.gpaValue = new SimpleDoubleProperty(gpaValue);
            }

            public String getCourseCode() { return courseCode.get(); }
            public String getCourseName() { return courseName.get(); }
            public String getGrade() { return grade.get(); }
            public double getGpaValue() { return gpaValue.get(); }

            // Property accessors for JavaFX binding
            public SimpleStringProperty courseCodeProperty() { return courseCode; }
            public SimpleStringProperty courseNameProperty() { return courseName; }
            public SimpleStringProperty gradeProperty() { return grade; }
            public SimpleDoubleProperty gpaValueProperty() { return gpaValue; }
        }

        TableView<CourseGrade> gradesTable = new TableView<>();

        // Use the correct property bindings
        TableColumn<CourseGrade, String> courseCodeColumn = new TableColumn<>("Course Code");
        courseCodeColumn.setCellValueFactory(cellData -> cellData.getValue().courseCodeProperty());
        courseCodeColumn.setPrefWidth(100);

        TableColumn<CourseGrade, String> courseNameColumn = new TableColumn<>("Course Name");
        courseNameColumn.setCellValueFactory(cellData -> cellData.getValue().courseNameProperty());
        courseNameColumn.setPrefWidth(200);

        TableColumn<CourseGrade, String> gradeColumn = new TableColumn<>("Grade");
        gradeColumn.setCellValueFactory(cellData -> cellData.getValue().gradeProperty());
        gradeColumn.setPrefWidth(80);

        TableColumn<CourseGrade, Number> gpaColumn = new TableColumn<>("GPA");
        gpaColumn.setCellValueFactory(cellData -> cellData.getValue().gpaValueProperty());
        gpaColumn.setPrefWidth(80);
        gpaColumn.setCellFactory(column -> new TableCell<CourseGrade, Number>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.1f", item.doubleValue()));
                }
            }
        });

        // Make sure to add columns in the correct order
        gradesTable.getColumns().clear();
        gradesTable.getColumns().addAll(courseCodeColumn, courseNameColumn, gradeColumn, gpaColumn);

        // Create and add sample data
        ObservableList<CourseGrade> grades = FXCollections.observableArrayList(
                new CourseGrade("CS2210", "Data Structures and Algorithms", "A+", 4.0),
                new CourseGrade("CS2211", "Software Engineering", "A", 3.9),
                new CourseGrade("MATH2020", "Discrete Mathematics", "A-", 3.7),
                new CourseGrade("STAT2040", "Statistics for Computer Science", "B+", 3.3),
                new CourseGrade("ENG2003", "Technical Communication", "A", 3.9)
        );

        // Make sure to set the items
        gradesTable.setItems(grades);

        // Set table properties for better display
        gradesTable.setPrefHeight(200);
        gradesTable.setMinHeight(200);
        gradesTable.setMaxWidth(Double.MAX_VALUE);
        gradesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Add CSS to make sure the table is visible
        gradesTable.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1px;");

        // Make sure table rows are visible
        gradesTable.setFixedCellSize(30); // Set fixed height for rows

        content.getChildren().add(gradesTable);

        // Add progress note
        Label progressNoteLabel = new Label("\nAcademic Progress Notes");
        progressNoteLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        content.getChildren().add(progressNoteLabel);

        TextArea progressNotes = new TextArea();
        progressNotes.setText("Student is making excellent progress and is on track to complete all " +
                "degree requirements. Recommendation: Consider honors program application " +
                "for the next academic year based on current GPA performance.");
        progressNotes.setWrapText(true);
        progressNotes.setPrefHeight(100);
        progressNotes.setEditable(false);
        content.getChildren().add(progressNotes);

        // Configure dialog sizing
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setPrefWidth(500);
        dialog.getDialogPane().setPrefHeight(600);
        dialog.setResizable(true);

        // Show the dialog
        dialog.showAndWait();
    }
    @FXML
    private void handleTuitionManagement() {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            showAlert("No Selection", "Please select a student to manage tuition.");
            return;
        }

        // Initialize tuition data if this is the first time
        initializeTuitionData();

        // Get student's tuition record or create if it doesn't exist
        String studentId = selectedStudent.getStudentId();
        String semester = selectedStudent.getCurrentSemester();

        // Create a key to store in our map
        String recordKey = studentId + "-" + semester;

        if (!tuitionRecords.containsKey(recordKey)) {
            // Calculate tuition fee based on academic level
            double tuitionFee = "Undergraduate".equals(selectedStudent.getAcademicLevel()) ? 5000.0 : 4000.0;
            tuitionRecords.put(recordKey, new TuitionRecord(studentId, semester, tuitionFee));
        }

        TuitionRecord record = tuitionRecords.get(recordKey);

        // Create and show the tuition management dialog
        showTuitionManagementDialog(selectedStudent, record);
    }

    private void initializeTuitionData() {
        // For demonstration purposes, let's create some sample tuition data
        // In a real application, this would be loaded from a database

        // If the map is already initialized, don't do it again
        if (!tuitionRecords.isEmpty()) {
            return;
        }

        // For each student in our table, create sample tuition records for past semesters
        for (Student student : studentList) {
            String studentId = student.getStudentId();
            String currentSemester = student.getCurrentSemester();
            boolean isUndergrad = "Undergraduate".equals(student.getAcademicLevel());
            double tuitionFee = isUndergrad ? 5000.0 : 4000.0;

            // Create records for the past 3 semesters
            String[] pastSemesters = {"Fall 2024", "Spring 2025", "Summer 2025"};
            for (String semester : pastSemesters) {
                String recordKey = studentId + "-" + semester;
                TuitionRecord record = new TuitionRecord(studentId, semester, tuitionFee);

                // For demonstration, make some of them paid and some unpaid
                if (Math.random() > 0.3) {  // 70% chance to be paid
                    if (Math.random() > 0.5) {  // 50% chance for scholarship
                        record.addScholarship(Math.random() * 2000, "Merit Scholarship", "2024-09-15");
                    }

                    if (Math.random() > 0.7) {  // 30% chance for grant
                        record.addGrant(Math.random() * 1000, "Need-based Grant", "2024-09-20");
                    }

                    if (Math.random() > 0.5) {  // 50% chance for OSAP
                        record.addOSAP(Math.random() * 3000, "2024-08-30");
                    }

                    // If still not fully paid, add payment
                    if (record.getOutstandingAmount() > 0) {
                        record.addPayment(record.getOutstandingAmount(), "Credit Card", "2024-10-05");
                    }
                }

                tuitionRecords.put(recordKey, record);
            }

            // Add current semester with unpaid status
            String currentRecordKey = studentId + "-" + currentSemester;
            tuitionRecords.put(currentRecordKey, new TuitionRecord(studentId, currentSemester, tuitionFee));
        }
    }

    private void showTuitionManagementDialog(Student student, TuitionRecord record) {
        // Create the dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Tuition Management - " + student.getName());
        dialog.setHeaderText("Student ID: " + student.getStudentId() + " | Semester: " + record.getSemester());

        // Set the button types
        dialog.getDialogPane().getButtonTypes().addAll(
                new ButtonType("Add Payment", ButtonBar.ButtonData.LEFT),
                new ButtonType("Add Scholarship", ButtonBar.ButtonData.LEFT),
                new ButtonType("Add Grant", ButtonBar.ButtonData.LEFT),
                new ButtonType("Add OSAP", ButtonBar.ButtonData.LEFT),
                ButtonType.CLOSE
        );

        // Create the tuition information display
        VBox content = new VBox(10);
        content.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        // Add tuition summary
        content.getChildren().add(new Label("Tuition Summary:"));
        content.getChildren().add(new Label("Total Fee: $" + String.format("%.2f", record.getTotalFee())));
        content.getChildren().add(new Label("Paid Amount: $" + String.format("%.2f", record.getPaidAmount())));
        content.getChildren().add(new Label("Outstanding Amount: $" + String.format("%.2f", record.getOutstandingAmount())));
        content.getChildren().add(new Label("Status: " + (record.isPaid() ? "PAID" : "UNPAID")));
        content.getChildren().add(new Label("\nFinancial Aid:"));
        content.getChildren().add(new Label("Scholarships: $" + String.format("%.2f", record.getScholarshipAmount())));
        content.getChildren().add(new Label("Grants: $" + String.format("%.2f", record.getGrantAmount())));
        content.getChildren().add(new Label("OSAP: $" + String.format("%.2f", record.getOsapAmount())));

        // Add payment history table
        content.getChildren().add(new Label("\nPayment History:"));

        TableView<PaymentHistory> historyTable = new TableView<>();

        TableColumn<PaymentHistory, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<PaymentHistory, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("paymentType"));

        TableColumn<PaymentHistory, Double> amountColumn = new TableColumn<>("Amount");
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountColumn.setCellFactory(column -> new TableCell<PaymentHistory, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", item));
                }
            }
        });

        historyTable.getColumns().addAll(dateColumn, typeColumn, amountColumn);

        // Add the history data
        ObservableList<PaymentHistory> historyData = FXCollections.observableArrayList(record.getPaymentHistory());
        historyTable.setItems(historyData);

        historyTable.setPrefHeight(200);
        content.getChildren().add(historyTable);

        dialog.getDialogPane().setContent(content);

        // Show the dialog and handle button clicks
        dialog.showAndWait().ifPresent(buttonType -> {
            if (buttonType.getText().equals("Add Payment")) {
                handleAddPayment(student, record, historyTable);
            } else if (buttonType.getText().equals("Add Scholarship")) {
                handleAddScholarship(student, record, historyTable);
            } else if (buttonType.getText().equals("Add Grant")) {
                handleAddGrant(student, record, historyTable);
            } else if (buttonType.getText().equals("Add OSAP")) {
                handleAddOSAP(student, record, historyTable);
            }
        });
    }

    private void handleAddPayment(Student student, TuitionRecord record, TableView<PaymentHistory> historyTable) {
        // Create payment dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Payment");
        dialog.setHeaderText("Add a new payment for " + student.getName());

        // Set the button types
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Create the payment form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField amountField = new TextField();
        ComboBox<String> paymentTypeCombo = new ComboBox<>();
        paymentTypeCombo.getItems().addAll("Credit Card", "Debit Card", "Cash", "Wire Transfer", "Check");
        DatePicker datePicker = new DatePicker(java.time.LocalDate.now());

        grid.add(new Label("Amount:"), 0, 0);
        grid.add(amountField, 1, 0);
        grid.add(new Label("Payment Type:"), 0, 1);
        grid.add(paymentTypeCombo, 1, 1);
        grid.add(new Label("Date:"), 0, 2);
        grid.add(datePicker, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Enable/Disable OK button depending on whether a amount was entered
        javafx.scene.Node okButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);

        // Do validation
        amountField.textProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(newValue.trim().isEmpty() || paymentTypeCombo.getValue() == null);
        });

        paymentTypeCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(amountField.getText().trim().isEmpty() || newValue == null);
        });

        // Show the dialog and wait for the user's response
        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                double amount = Double.parseDouble(amountField.getText());
                String paymentType = paymentTypeCombo.getValue();
                String date = datePicker.getValue().toString();

                // Add the payment
                record.addPayment(amount, paymentType, date);

                // Update the history table
                historyTable.getItems().clear();
                historyTable.getItems().addAll(record.getPaymentHistory());

                // Show success message
                showAlert("Payment Added", "Payment of $" + String.format("%.2f", amount) + " has been recorded.");

                // Refresh the tuition management dialog
                showTuitionManagementDialog(student, record);

            } catch (NumberFormatException e) {
                showAlert("Error", "Please enter a valid amount.");
            }
        }
    }

    @FXML
    public void handleCourseRegistration() {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {

            return;
        }

        // Fetch current courses from database to ensure we have the most up-to-date information
        String[] currentCourses = ExcelDatabase.getStudentCourses(selectedStudent.getStudentId());
        if (currentCourses == null) {

            return;
        }

        // Create dialog for course management
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Course Enrollment Management");
        dialog.setHeaderText("Manage Enrollments for " + selectedStudent.getName());

        // Define button types
        ButtonType registerButtonType = new ButtonType("Register", ButtonBar.ButtonData.OK_DONE);
        ButtonType unregisterButtonType = new ButtonType("Unregister", ButtonBar.ButtonData.LEFT);
        dialog.getDialogPane().getButtonTypes().addAll(registerButtonType, unregisterButtonType, ButtonType.CANCEL);

        // Create the layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Display current courses
        Label currentCoursesLabel = new Label("Current Courses:");
        ListView<String> currentCoursesListView = new ListView<>(FXCollections.observableArrayList(currentCourses));
        currentCoursesListView.setPrefHeight(100);

        // Get all available courses from the database
        Map<String, String> subjectsMap = ExcelDatabase.loadSubjects();
        List<String> allCourses = new ArrayList<>(subjectsMap.keySet());

        // Remove already registered courses from available courses
        List<String> availableCourses = new ArrayList<>(allCourses);
        for (String course : currentCourses) {
            availableCourses.remove(course.trim());
        }

        // Set up the available courses combo box
        Label availableCoursesLabel = new Label("Available Courses:");
        ComboBox<String> availableCourseCombo = new ComboBox<>(FXCollections.observableArrayList(availableCourses));

        // Add descriptions to make the UI more informative
        Label coursesDescriptionLabel = new Label("Select a course to register, or select a current course and click Unregister");
        coursesDescriptionLabel.setWrapText(true);

        // Add components to grid
        grid.add(currentCoursesLabel, 0, 0);
        grid.add(currentCoursesListView, 0, 1);
        grid.add(availableCoursesLabel, 0, 2);
        grid.add(availableCourseCombo, 0, 3);
        grid.add(coursesDescriptionLabel, 0, 4);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the combo box
        Platform.runLater(availableCourseCombo::requestFocus);

        // Set up result converter
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == registerButtonType && availableCourseCombo.getValue() != null) {
                return "register:" + availableCourseCombo.getValue();
            } else if (dialogButton == unregisterButtonType && currentCoursesListView.getSelectionModel().getSelectedItem() != null) {
                return "unregister:" + currentCoursesListView.getSelectionModel().getSelectedItem();
            }
            return null;
        });

        // Show dialog and process result
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(action -> {
            String[] parts = action.split(":");
            if (parts.length == 2) {
                String actionType = parts[0];
                String courseCode = parts[1].trim();

                boolean success = false;
                if ("register".equals(actionType)) {
                    success = ExcelDatabase.registerStudentForCourse(selectedStudent.getStudentId(), courseCode);
                } else if ("unregister".equals(actionType)) {
                    success = ExcelDatabase.unregisterStudentFromCourse(selectedStudent.getStudentId(), courseCode);
                }

                if (success) {

                    // Refresh the student list to show updated data
                    loadStudentsFromExcel();
                    // Update the selected student object to reflect changes
                    selectedStudent.setSubjectsRegistered(String.join(",", ExcelDatabase.getStudentCourses(selectedStudent.getStudentId())));
                } else {

                }
            }
        });
    }
    private void handleAddScholarship(Student student, TuitionRecord record, TableView<PaymentHistory> historyTable) {
        // Create scholarship dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Scholarship");
        dialog.setHeaderText("Add a scholarship for " + student.getName());

        // Set the button types
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Create the scholarship form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField amountField = new TextField();
        TextField nameField = new TextField();
        DatePicker datePicker = new DatePicker(java.time.LocalDate.now());

        grid.add(new Label("Amount:"), 0, 0);
        grid.add(amountField, 1, 0);
        grid.add(new Label("Scholarship Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Date:"), 0, 2);
        grid.add(datePicker, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Enable/Disable OK button depending on validation
        javafx.scene.Node okButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);

        // Do validation
        amountField.textProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(newValue.trim().isEmpty() || nameField.getText().trim().isEmpty());
        });

        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(amountField.getText().trim().isEmpty() || newValue.trim().isEmpty());
        });

        // Show the dialog and wait for the user's response
        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                double amount = Double.parseDouble(amountField.getText());
                String name = nameField.getText();
                String date = datePicker.getValue().toString();

                // Add the scholarship
                record.addScholarship(amount, name, date);

                // Update the history table
                historyTable.getItems().clear();
                historyTable.getItems().addAll(record.getPaymentHistory());

                // Show success message
                showAlert("Scholarship Added", "Scholarship of $" + String.format("%.2f", amount) + " has been recorded.");

                // Refresh the tuition management dialog
                showTuitionManagementDialog(student, record);

            } catch (NumberFormatException e) {
                showAlert("Error", "Please enter a valid amount.");
            }
        }
    }

    private void handleAddGrant(Student student, TuitionRecord record, TableView<PaymentHistory> historyTable) {
        // Create grant dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Grant");
        dialog.setHeaderText("Add a grant for " + student.getName());

        // Set the button types
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Create the grant form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField amountField = new TextField();
        TextField nameField = new TextField();
        DatePicker datePicker = new DatePicker(java.time.LocalDate.now());

        grid.add(new Label("Amount:"), 0, 0);
        grid.add(amountField, 1, 0);
        grid.add(new Label("Grant Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Date:"), 0, 2);
        grid.add(datePicker, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Enable/Disable OK button depending on validation
        javafx.scene.Node okButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);

        // Do validation
        amountField.textProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(newValue.trim().isEmpty() || nameField.getText().trim().isEmpty());
        });

        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(amountField.getText().trim().isEmpty() || newValue.trim().isEmpty());
        });

        // Show the dialog and wait for the user's response
        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                double amount = Double.parseDouble(amountField.getText());
                String name = nameField.getText();
                String date = datePicker.getValue().toString();

                // Add the grant
                record.addGrant(amount, name, date);

                // Update the history table
                historyTable.getItems().clear();
                historyTable.getItems().addAll(record.getPaymentHistory());

                // Show success message
                showAlert("Grant Added", "Grant of $" + String.format("%.2f", amount) + " has been recorded.");

                // Refresh the tuition management dialog
                showTuitionManagementDialog(student, record);

            } catch (NumberFormatException e) {
                showAlert("Error", "Please enter a valid amount.");
            }
        }
    }

    private void handleAddOSAP(Student student, TuitionRecord record, TableView<PaymentHistory> historyTable) {
        // Create OSAP dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add OSAP");
        dialog.setHeaderText("Add OSAP funding for " + student.getName());

        // Set the button types
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Create the OSAP form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField amountField = new TextField();
        DatePicker datePicker = new DatePicker(java.time.LocalDate.now());

        grid.add(new Label("Amount:"), 0, 0);
        grid.add(amountField, 1, 0);
        grid.add(new Label("Date:"), 0, 1);
        grid.add(datePicker, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Enable/Disable OK button depending on validation
        javafx.scene.Node okButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);

        // Do validation
        amountField.textProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(newValue.trim().isEmpty());
        });

        // Show the dialog and wait for the user's response
        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                double amount = Double.parseDouble(amountField.getText());
                String date = datePicker.getValue().toString();

                // Add the OSAP
                record.addOSAP(amount, date);

                // Update the history table
                historyTable.getItems().clear();
                historyTable.getItems().addAll(record.getPaymentHistory());

                // Show success message
                showAlert("OSAP Added", "OSAP funding of $" + String.format("%.2f", amount) + " has been recorded.");

                // Refresh the tuition management dialog
                showTuitionManagementDialog(student, record);

            } catch (NumberFormatException e) {
                showAlert("Error", "Please enter a valid amount.");
            }
        }
    }
}