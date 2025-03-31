package com.FinalProject.UMS;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MenuController {

    private static final Logger LOGGER = Logger.getLogger(MenuController.class.getName());

    @FXML
    private VBox menuVBox;

    @FXML
    private Button subjectManagementButton;

    @FXML
    private Button courseManagementButton;

    @FXML
    private Button studentManagementButton;

    @FXML
    private Button facultyManagementButton;

    @FXML
    private Button eventManagementButton;

    private String userRole;
    private String loggedInStudentId;
    private User loggedInUser;


    public void initialize() {
        // Initialization code, if needed
    }

    public void setUserRole(String role) {
        this.userRole = role;
        // You can update the UI based on the user role here
    }
    public void setLoggedInStudentId(String studentId) { // Add this method
        this.loggedInStudentId = studentId;
    }

    // Add this method to receive the logged-in user
    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
        LOGGER.log(Level.INFO, "Logged in user received in MenuController: {0}", user.getId());

        //  Determine if the user is a student and get their student ID
        if (user.getStudentId() != null && !user.getStudentId().isEmpty()) {
            loggedInStudentId = user.getStudentId();
            LOGGER.log(Level.INFO, "User is a student with ID: {0}", loggedInStudentId);
        } else {
            LOGGER.warning("User is not a student or student ID is missing.");
            loggedInStudentId = null; // Or handle the non-student case as needed
        }
    }



    @FXML
    private void handleDashboard(ActionEvent event) {
        loadScene("Dashboard-view.fxml", "Dashboard", event);
    }

    @FXML
    private void handleSubjectManagement(ActionEvent event) {
        LOGGER.log(Level.INFO, "Subject Management button clicked");
        loadScene("/com/FinalProject/UMS/SubjectManagement.fxml", "Subject Management", event);  // Corrected path and using loadScene
    }

    @FXML
    private void handleAddSubject(ActionEvent event) {
        loadScene("addSubject.fxml", "Add Subject", event);
    }

    @FXML
    private void handleEditSubject(ActionEvent event) {
        loadScene("editSubject.fxml", "Edit Subject", event);
    }

    @FXML
    private void handleDeleteSubject(ActionEvent event) {
        loadScene("deleteSubject.fxml", "Delete Subject", event);
    }

    @FXML
    private void handleViewSubject(ActionEvent event) {
        loadScene("viewSubject.fxml", "View Subject", event);
    }
    @FXML
    private void handleCourseManagement(ActionEvent event) {
        loadScene("CourseManagement-view.fxml", "Course Management", event);
    }

    @FXML
    private void handleAddCourse(ActionEvent event) {
        loadScene("addCourse.fxml", "Add Course", event);
    }

    @FXML
    private void handleEditCourse(ActionEvent event) {
        loadScene("editCourse.fxml", "Edit Course", event);
    }

    @FXML
    private void handleDeleteCourse(ActionEvent event) {
        loadScene("deleteCourse.fxml", "Delete Course", event);
    }

    @FXML
    private void handleViewCourse(ActionEvent event) {
        loadScene("viewCourse.fxml", "View Course", event);
    }

    @FXML
    private void handleAssignFaculty(ActionEvent event) {
        loadScene("assignFaculty.fxml", "Assign Faculty Courses", event);
    }

    @FXML
    private void handleManageEnrollments(ActionEvent event) {
        loadScene("manageEnrollments.fxml", "Manage Enrollments", event);
    }

    @FXML
    private void handleStudentManage(ActionEvent event) {
        // Load student data and print to console
        List<Student> students = StudentDatabase.loadStudentsFromExcel();
        System.out.println("Loaded Students:");
        for (Student student : students) {
            System.out.println(student.getStudentId() + " " + student.getName() + " " + student.getAddress() + " " + student.getEmail());
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("studentmanagecontroller.fxml"));
            Parent root = loader.load();

            // Get the controller for the student management view
            StudentManagementMenuController studentManagementController = loader.getController();

            // Pass the logged-in student ID to the student management controller
            studentManagementController.setLoggedInStudentId(loggedInStudentId);

            Stage stage = (Stage) studentManagementButton.getScene().getWindow();
            Scene scene = new Scene(root, 1920, 1080);
            stage.setScene(scene);
            stage.setTitle("Student Management");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void handleFacultyManagement(ActionEvent event) {
        loadScene("faculty-view.fxml", "Faculty Management", event);
    }

    @FXML
    private void handleAssignFacultyCourses(ActionEvent event) {
        loadScene("assignFacultyCourses.fxml", "Assign Faculty Courses", event);
    }

    @FXML
    private void handleEventManagement(ActionEvent event) {
        loadScene("event-menu-view.fxml", "Event Management", event);
    }

    @FXML
    private void handleEditEvent(ActionEvent event) {
        loadScene("editEvent.fxml", "Edit Event", event);
    }

    @FXML
    private void handleDeleteEvent(ActionEvent event) {
        loadScene("deleteEvent.fxml", "Delete Event", event);
    }

    @FXML
    private void handleViewEvents(ActionEvent event) {
        loadScene("viewEvents.fxml", "View Events", event);
    }

    @FXML
    private void handleManageEventRegistrations(ActionEvent event) {
        loadScene("manageEventRegistrations.fxml", "ManageEventRegistrations", event);
    }

    @FXML
    private void handleViewProfile(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("profile-view.fxml"));
            Parent root = loader.load();
            //ProfileController profileController = loader.getController();
            //profileController.setLoggedInStudentId(loggedInUser.getId()); // Remove this line!!

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("User Profile");
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading profile FXML: " + e.getMessage(), e);
            showError("Error", "Failed to load profile FXML.");
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        showAlert("Logout", "Logging out...");
        // Add code here to log the user out and return to the login screen
    }

    @FXML
    private void handleOpenChatbot() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("chatbot-popup.fxml"));
            Parent root = loader.load();

            Stage chatbotStage = new Stage();
            chatbotStage.initModality(Modality.APPLICATION_MODAL);
            chatbotStage.initOwner(menuVBox.getScene().getWindow());
            chatbotStage.setTitle("UMS Assistant");
            chatbotStage.setScene(new Scene(root, 400, 600));
            chatbotStage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to open chatbot", e);
            showError("Error", "Failed to open chatbot: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadScene(String fxmlFile, String title, ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = fxmlLoader.load();

            Stage stage;
            if (event != null) {
                stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            } else {
                stage = (Stage) menuVBox.getScene().getWindow();
            }

            stage.setTitle(title);
            stage.setScene(new Scene(root, 1366, 768));
            stage.show();

            // Close the current window
            if (event != null) {
                ((Node) (event.getSource())).getScene().getWindow().hide();
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading " + fxmlFile + ": " + e.getMessage(), e);
            showAlert("Error", "Failed to load " + title + " window.");
        }
    }
}