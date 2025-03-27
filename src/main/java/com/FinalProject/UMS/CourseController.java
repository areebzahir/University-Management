package com.FinalProject.UMS;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class CourseController {

    @FXML
    private TableView<Course> courseTable;

    @FXML
    private TableColumn<Course, String> courseCodeColumn;

    @FXML
    private TableColumn<Course, String> courseNameColumn;

    @FXML
    private TableColumn<Course, String> subjectNameColumn;

    @FXML
    private TableColumn<Course, String> teacherNameColumn;

    private String loggedInStudentId;

    public void setLoggedInStudentId(String studentId) {
        this.loggedInStudentId = studentId;
        loadCourseData();
    }

    @FXML
    public void initialize() {
        // Initialize the table columns
        courseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        subjectNameColumn.setCellValueFactory(new PropertyValueFactory<>("subjectName"));
        teacherNameColumn.setCellValueFactory(new PropertyValueFactory<>("teacherName"));
    }

    private void loadCourseData() {
        List<Course> courses = StudentDatabase.getCoursesForStudent(loggedInStudentId);
        ObservableList<Course> courseList = FXCollections.observableArrayList(courses);
        courseTable.setItems(courseList);
    }
}