package com.FinalProject.UMS;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javafx.scene.Node; // <---- This import statement is NECESSARY

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProfileController {

    @FXML
    private TextField studentIdField;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField phoneField;

    @FXML
    private ImageView profileImageView;

    @FXML
    private Button uploadPictureButton;
    @FXML
    private Button saveChangesButton;

    @FXML
    private Label studentIdLabel; // Corrected: Adding FXML annotation

    private String loggedInStudentId;
    private Student currentStudent;

    private static final String IMAGE_STORAGE_DIRECTORY = "src/main/resources/profile_images/";
    private static final String DEFAULT_PROFILE_IMAGE = "/com/FinalProject/UMS/default_profile.png";
    private static final Logger LOGGER = Logger.getLogger(ProfileController.class.getName());

    // Initialize the controller
    public void initialize() {
        // No need to load student data here, it will be loaded when the student ID is set
        LOGGER.info("ProfileController initialized.");
    }

    public void setLoggedInStudentId(String studentId) {
        this.loggedInStudentId = studentId;
        LOGGER.log(Level.INFO, "Setting loggedInStudentId to: {0}", studentId);
        loadStudentData();
    }

    private void loadStudentData() {
        try {
            currentStudent = StudentDatabase.getStudentById(loggedInStudentId);
            if (currentStudent != null) {
                populateUIFields(currentStudent);
                loadProfilePicture();
                displayStudentInfo(); // Call displayStudentInfo after loading data

            } else {
                LOGGER.warning("Student not found with ID: " + loggedInStudentId);
                showAlert("Error", "Student not found!", Alert.AlertType.ERROR);
                displayStudentInfo();

            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading student data: " + e.getMessage(), e);
            showAlert("Error", "Failed to load student data.", Alert.AlertType.ERROR);
        }
    }

    private void populateUIFields(Student student) {
        studentIdField.setText(student.getStudentId());
        firstNameField.setText(student.getName().split(" ")[0]);
        lastNameField.setText(student.getName().split(" ").length > 1 ? student.getName().split(" ")[1] : "");
        emailField.setText(student.getEmail());
        addressField.setText(student.getAddress());
        phoneField.setText(student.getTelephone());

        // Make Student ID, First Name, and Last Name non-editable
        studentIdField.setEditable(false);
        firstNameField.setEditable(false);
        lastNameField.setEditable(false);
    }

    private void loadProfilePicture() {
        Image defaultImage = new Image(getClass().getResourceAsStream(DEFAULT_PROFILE_IMAGE));
        if (currentStudent != null && currentStudent.getProfilePhoto() != null && !currentStudent.getProfilePhoto().isEmpty()) {
            String imagePath = IMAGE_STORAGE_DIRECTORY + currentStudent.getProfilePhoto();
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                Image image = new Image(imageFile.toURI().toString());
                profileImageView.setImage(image);
            } else {
                // Display a default image if the profile picture is not found
                LOGGER.warning("Profile picture not found at: " + imagePath);
                profileImageView.setImage(defaultImage);
            }
        } else {
            // Display a default image if the student has no profile picture
            profileImageView.setImage(defaultImage);
        }
    }

    @FXML
    public void onUploadPictureButtonClick(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Picture");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(uploadPictureButton.getScene().getWindow());

        if (selectedFile != null) {
            try {
                // Store the image and update the student's profile photo
                String newFileName = storeImage(selectedFile);
                currentStudent.setProfilePhoto(newFileName);

                // Save the updated student information to the database
                List<Student> students = StudentDatabase.loadStudentsFromExcel();
                for (int i = 0; i < students.size(); i++) {
                    if (students.get(i).getStudentId().equals(currentStudent.getStudentId())) {
                        students.set(i, currentStudent);
                        break;
                    }
                }
                StudentDatabase.saveStudentsToExcel(students);

                // Load the new profile picture
                loadProfilePicture();

            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error storing image: " + e.getMessage(), e);
                showAlert("Error", "Failed to store image.", Alert.AlertType.ERROR);
            }
        }
    }

    private String storeImage(File selectedFile) throws IOException {
        // Create the image storage directory if it doesn't exist
        File directory = new File(IMAGE_STORAGE_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Generate a unique file name
        String fileExtension = getFileExtension(selectedFile.getName());
        String newFileName = currentStudent.getStudentId() + "_" + System.currentTimeMillis() + "." + fileExtension;
        Path destination = Paths.get(IMAGE_STORAGE_DIRECTORY, newFileName);

        // Copy the file to the storage directory
        Files.copy(selectedFile.toPath(), destination);

        return newFileName;
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1);
        }
        return "png"; // Default extension
    }

    @FXML
    protected void onSaveChangesButtonClick() {
        // Save changes logic
        if (currentStudent != null) {
            currentStudent.setEmail(emailField.getText());
            currentStudent.setAddress(addressField.getText());
            currentStudent.setTelephone(phoneField.getText());

            // Save the updated student information to the database
            List<Student> students = StudentDatabase.loadStudentsFromExcel();
            for (int i = 0; i < students.size(); i++) {
                if (students.get(i).getStudentId().equals(currentStudent.getStudentId())) {
                    students.set(i, currentStudent);
                    break;
                }
            }
            StudentDatabase.saveStudentsToExcel(students);
        }
    }

    @FXML
    protected void onChangePasswordButtonClick() {
        // Change password logic
    }

    private void displayStudentInfo() {
        if (loggedInStudentId != null) {
            // Load student data based on studentId (e.g., from UserService)
            // For now, just display the ID
            studentIdLabel.setText("Student ID: " + loggedInStudentId); //Corrected concatenation
        } else {
            studentIdLabel.setText("No student ID available.");
        }
    }

    @FXML
    protected void onReturnButtonClick(ActionEvent actionEvent) { //Use ActionEvent
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("studentmanagecontroller.fxml"));
            Parent root = loader.load();

            StudentManagementMenuController studentManagementController = loader.getController();
            studentManagementController.setLoggedInStudentId(loggedInStudentId);

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();  //Use ActionEvent
            Scene scene = new Scene(root, 1366, 768);
            stage.setScene(scene);
            stage.setTitle("Hello");
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading student management menu: " + e.getMessage(), e);
            showAlert("Error", "Failed to load student management menu.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}