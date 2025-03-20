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
import java.time.LocalDate;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class WCourseController {

    @FXML
    private TextField txtCourseCode;

    @FXML
    private TextField txtCourseName;

    @FXML
    private TextField txtSubject;

    @FXML
    private Spinner<Integer> spnSection;

    @FXML
    private TextField txtTeacher;

    @FXML
    private Spinner<Integer> spnCapacity;

    @FXML
    private DatePicker dpExamDate;

    @FXML
    private TextField txtLocation;

    @FXML
    private ListView<WCourse> courseListView;

    private String userRole;

    private ObservableList<WCourse> courseData = FXCollections.observableArrayList();

    private static final String EXCEL_FILE_PATH = "\"C:\\Users\\haazi\\OneDrive\\Documents\\FINALPROJECT 1420\\University-Management\\courses.xlsx\"";

    @FXML
    public void initialize() {
        // Initialize Spinners
        SpinnerValueFactory<Integer> sectionValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10);
        spnSection.setValueFactory(sectionValueFactory);

        SpinnerValueFactory<Integer> capacityValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100);
        spnCapacity.setValueFactory(capacityValueFactory);

        loadCoursesFromExcel();
        courseListView.setItems(courseData);
        adjustVisibilityBasedOnRole();

        // Set the cell factory to display course information in the ListView
        courseListView.setCellFactory(param -> new ListCell<WCourse>() {
            @Override
            protected void updateItem(WCourse course, boolean empty) {
                super.updateItem(course, empty);
                if (empty || course == null || course.getCourseName() == null) {
                    setText(null);
                } else {
                    setText(course.getCourseName() + " (" + course.getCourseCode() + ")");
                }
            }
        });
    }

    public void setUserRole(String role) {
        this.userRole = role;
        adjustVisibilityBasedOnRole();
    }

    private void adjustVisibilityBasedOnRole() {
        if ("USER".equals(userRole)) {
            txtCourseCode.setDisable(true);
            txtCourseName.setDisable(true);
            txtSubject.setDisable(true);
            spnSection.setDisable(true);
            txtTeacher.setDisable(true);
            spnCapacity.setDisable(true);
            dpExamDate.setDisable(true);
            txtLocation.setDisable(true);
        }
    }

    @FXML
    private void addCourse() {
        try {
            String courseCode = txtCourseCode.getText();
            String courseName = txtCourseName.getText();
            String subjectCode = txtSubject.getText();
            String sectionNumber = String.valueOf(spnSection.getValue());
            int capacity = spnCapacity.getValue();
            LocalDate examDate = dpExamDate.getValue();
            String location = txtLocation.getText();
            String teacherName = txtTeacher.getText();

            if (courseCode.isEmpty() || courseName.isEmpty() || subjectCode.isEmpty() ||
                    sectionNumber.isEmpty() || location.isEmpty() || teacherName.isEmpty() || examDate == null) {
                showAlert("Error", "Please fill in all fields.");
                return;
            }

            String examDateStr = examDate.toString(); // Format LocalDate to String

            WCourse newCourse = new WCourse(courseCode, courseName, subjectCode, sectionNumber, capacity, "To be scheduled", examDateStr, location, teacherName);
            courseData.add(newCourse);
            updateCoursesInExcel(); // Update Excel file
            clearFields();
        } catch (Exception e) {
            showAlert("Error", "An error occurred while adding the course.");
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteCourse() {
        WCourse selectedCourse = courseListView.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            courseData.remove(selectedCourse);
            updateCoursesInExcel(); // Update Excel file
            clearFields();
        } else {
            showAlert("Error", "Please select a course to delete.");
        }
    }


    @FXML
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("menu-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) courseListView.getScene().getWindow();
            Scene scene = new Scene(root, 1366, 768);
            stage.setScene(scene);
            stage.setTitle("Main Menu");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadCoursesFromExcel() {
        try (FileInputStream file = new FileInputStream(new File(EXCEL_FILE_PATH));
             Workbook workbook = new XSSFWorkbook(file)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            // Skip header row
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (row.getCell(0) == null || row.getCell(0).getStringCellValue().isEmpty()) {
                    continue; // Skip empty rows
                }

                String courseCode = row.getCell(0).getStringCellValue();
                String courseName = row.getCell(1).getStringCellValue();
                String subjectCode = row.getCell(2).getStringCellValue();
                String sectionNumber = String.valueOf((int) row.getCell(3).getNumericCellValue());
                int capacity = (int) row.getCell(4).getNumericCellValue();
                String lectureTime = row.getCell(5).getStringCellValue();
                String examDate = row.getCell(6).getStringCellValue();
                String location = row.getCell(7).getStringCellValue();
                String teacherName = row.getCell(8).getStringCellValue();

                WCourse course = new WCourse(courseCode, courseName, subjectCode, sectionNumber, capacity, lectureTime, examDate, location, teacherName);
                courseData.add(course);
            }
        } catch (IOException e) {
            showAlert("Error", "Error loading data from Excel file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateCoursesInExcel() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Courses");

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Course Code", "Course Name", "Subject Code", "Section Number", "Capacity", "Lecture Time", "Exam Date", "Location", "Teacher Name"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Create data rows
            int rowNum = 1;
            for (WCourse course : courseData) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(course.getCourseCode());
                row.createCell(1).setCellValue(course.getCourseName());
                row.createCell(2).setCellValue(course.getSubjectCode());
                row.createCell(3).setCellValue(course.getSectionNumber());
                row.createCell(4).setCellValue(course.getCapacity());
                row.createCell(5).setCellValue(course.getLectureTime());
                row.createCell(6).setCellValue(course.getExamDate());
                row.createCell(7).setCellValue(course.getLocation());
                row.createCell(8).setCellValue(course.getTeacherName());
            }

            // Write the workbook to the file system
            try (FileOutputStream outputStream = new FileOutputStream(EXCEL_FILE_PATH)) {
                workbook.write(outputStream);
            }
        } catch (IOException e) {
            showAlert("Error", "Error updating Excel file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearFields() {
        txtCourseCode.clear();
        txtCourseName.clear();
        txtSubject.clear();
        spnSection.getValueFactory().setValue(1);
        txtTeacher.clear();
        spnCapacity.getValueFactory().setValue(1);
        dpExamDate.setValue(null);
        txtLocation.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Getter for courseListView (added for testing)
    public ListView<WCourse> getCourseListView() {
        return courseListView;
    }

    // Getter for userRole (added for testing)
    public String getUserRole() {
        return userRole;
    }
}