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
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_USER = "USER";

    @FXML private VBox menuVBox;
    @FXML private Button subjectManagementButton;
    @FXML private Button courseManagementButton;
    @FXML private Button studentManagementButton;
    @FXML private Button facultyManagementButton;
    @FXML private Button eventManagementButton;
    @FXML private Button adminStudentViewButton;
    @FXML private Button viewFacultyButton;

    private String loggedInStudentId;
    private String userRole;

    @FXML
    public void initialize() {
        LOGGER.info("MenuController initialized");
        User loggedInUser = GlobalState.getInstance().getLoggedInUser();
        if (loggedInUser != null) {
            this.userRole = loggedInUser.getRole();
            System.out.println("Initializing MenuController - Current role: " + userRole);
        } else {
            System.out.println("Initializing MenuController - No user logged in");
            this.userRole = null;
        }
        updateButtonVisibility();
    }

    public void setUserRole(String role) {
        this.userRole = role != null ? role.toUpperCase() : null;
        System.out.println("Setting user role to: " + this.userRole);
        LOGGER.info("User role set to: " + this.userRole);

        User loggedInUser = GlobalState.getInstance().getLoggedInUser();
        if (loggedInUser != null) {
            loggedInUser.setRole(this.userRole);
            GlobalState.getInstance().setLoggedInUser(loggedInUser);
        }
        updateButtonVisibility();
    }

    private void updateButtonVisibility() {
        if (userRole == null) {
            System.out.println("No role set - hiding all buttons");
            adminStudentViewButton.setVisible(false);
            adminStudentViewButton.setManaged(false);
            studentManagementButton.setVisible(false);
            studentManagementButton.setManaged(false);
            viewFacultyButton.setVisible(false);
            viewFacultyButton.setManaged(false);
            return;
        }

        boolean isAdmin = ROLE_ADMIN.equals(userRole);
        boolean isUser = ROLE_USER.equals(userRole);

        System.out.println("Updating button visibility - Admin: " + isAdmin + ", User: " + isUser);

        adminStudentViewButton.setVisible(isAdmin);
        adminStudentViewButton.setManaged(isAdmin);
        studentManagementButton.setDisable(isAdmin);
        studentManagementButton.setVisible(isUser);
        studentManagementButton.setManaged(isUser);
        adminStudentViewButton.setDisable(isUser);
        viewFacultyButton.setVisible(true); // Visible to both roles
        viewFacultyButton.setManaged(true);

        LOGGER.info("Button visibility updated");
    }

    // Scene Navigation Handlers
    @FXML private void handleDashboard(ActionEvent event) {
        loadScene("Dashboard-view.fxml", "Dashboard", event);
    }

    @FXML private void handleSubjectManagement(ActionEvent event) {
        loadScene("/com/FinalProject/UMS/SubjectManagement.fxml", "Subject Management", event);
    }

    @FXML private void handleAddSubject(ActionEvent event) {
        loadScene("addSubject.fxml", "Add Subject", event);
    }

    @FXML private void handleEditSubject(ActionEvent event) {
        loadScene("editSubject.fxml", "Edit Subject", event);
    }

    @FXML private void handleDeleteSubject(ActionEvent event) {
        loadScene("deleteSubject.fxml", "Delete Subject", event);
    }

    @FXML private void handleViewSubject(ActionEvent event) {
        loadScene("viewSubject.fxml", "View Subject", event);
    }

    @FXML private void handleCourseManagement(ActionEvent event) {
        loadScene("CourseManagement-view.fxml", "Course Management", event);
    }

    @FXML private void handleAddCourse(ActionEvent event) {
        loadScene("addCourse.fxml", "Add Course", event);
    }

    @FXML private void handleEditCourse(ActionEvent event) {
        loadScene("editCourse.fxml", "Edit Course", event);
    }

    @FXML private void handleDeleteCourse(ActionEvent event) {
        loadScene("deleteCourse.fxml", "Delete Course", event);
    }

    @FXML private void handleViewCourse(ActionEvent event) {
        loadScene("viewCourse.fxml", "View Course", event);
    }

    @FXML private void handleAssignFaculty(ActionEvent event) {
        loadScene("assignFaculty.fxml", "Assign Faculty Courses", event);
    }

    @FXML private void handleManageEnrollments(ActionEvent event) {
        loadScene("manageEnrollments.fxml", "Manage Enrollments", event);
    }

    @FXML private void handleStudentManage(ActionEvent event) {
        System.out.println("Student management button clicked by user with role: " + userRole);
        List<Student> students = StudentDatabase.loadStudentsFromExcel();
        System.out.println("Loaded Students:");
        for (Student student : students) {
            System.out.println(student.getStudentId() + " " + student.getName());
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("studentmanagecontroller.fxml"));
            Parent root = loader.load();
            StudentManagementMenuController controller = loader.getController();
            controller.setLoggedInStudentId(loggedInStudentId);

            Stage stage = (Stage) studentManagementButton.getScene().getWindow();
            Scene scene = new Scene(root, 1920, 1080);
            stage.setScene(scene);
            stage.setTitle("Student Management");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void handleAdminStudentView(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("admin-student-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) adminStudentViewButton.getScene().getWindow();
            stage.setScene(new Scene(root, 1920, 1080));
            stage.setTitle("Admin Student View");
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading admin student view", e);
            showError("Error", "Failed to load admin student view: " + e.getMessage());
        }
    }

    // Faculty Management Section
    @FXML private void handleFacultyManagement(ActionEvent event) {
        loadScene("faculty-view.fxml", "Faculty Management", event);
    }

    @FXML private void handleAssignFacultyCourses(ActionEvent event) {
        loadScene("assignFacultyCourses.fxml", "Assign Faculty Courses", event);
    }

    @FXML private void handleViewFaculty(ActionEvent event) {
        loadScene("/com/FinalProject/UMS/facultyStudentView.fxml", "View Faculty", event);
    }

    // Event Management Section
    @FXML private void handleEventManagement(ActionEvent event) {
        loadScene("event-menu-view.fxml", "Event Management", event);
    }

    @FXML private void handleEditEvent(ActionEvent event) {
        loadScene("editEvent.fxml", "Edit Event", event);
    }

    @FXML private void handleDeleteEvent(ActionEvent event) {
        loadScene("deleteEvent.fxml", "Delete Event", event);
    }

    @FXML private void handleViewEvents(ActionEvent event) {
        loadScene("viewEvents.fxml", "View Events", event);
    }

    @FXML private void handleManageEventRegistrations(ActionEvent event) {
        loadScene("manageEventRegistrations.fxml", "Manage Event Registrations", event);
    }

    // User Profile and System
    @FXML private void handleViewProfile(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("profile-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("User Profile");
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading profile FXML", e);
            showError("Error", "Failed to load profile");
        }
    }

    @FXML private void handleLogout(ActionEvent event) {
        try {
            // Close the current window
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

            // Load and show the login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            Parent root = loader.load();

            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(root));
            loginStage.setTitle("University Management System - Login");
            loginStage.show();

            LOGGER.info("User logged out successfully");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error during logout", e);
            showError("Logout Error", "Failed to return to login screen");
        }
    }

    @FXML private void handleOpenChatbot() {
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
            showError("Error", "Failed to open chatbot");
        }
    }

    // Utility Methods
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

            Stage stage = (event != null) ?
                    (Stage) ((Button) event.getSource()).getScene().getWindow() :
                    (Stage) menuVBox.getScene().getWindow();

            stage.setTitle(title);
            stage.setScene(new Scene(root, 1366, 768));
            stage.show();

            Object controller = fxmlLoader.getController();
            if (controller instanceof SubjectManagementController) {
                ((SubjectManagementController)controller).setUserRole(
                        GlobalState.getInstance().getLoggedInUser().getRole());
            } else if (controller instanceof EventMenuController) {
                ((EventMenuController)controller).setUserRole(
                        GlobalState.getInstance().getLoggedInUser().getRole());
            } else if (controller instanceof MenuController) {
                ((MenuController)controller).setUserRole(
                        GlobalState.getInstance().getLoggedInUser().getRole());
            }

            if (event != null) {
                ((Node) event.getSource()).getScene().getWindow().hide();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading " + fxmlFile, e);
            showAlert("Error", "Failed to load " + title);
        }
    }
}