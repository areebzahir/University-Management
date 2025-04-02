
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
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.Node;
import javafx.scene.control.Label;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
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
    private Label studentIdLabel;

    @FXML
    private CheckBox showPasswordCheckBox;

    private String loggedInStudentId;
    private User currentStudent;

    private static final String IMAGE_STORAGE_DIRECTORY = "src/main/resources/profile_images/";
    private static final String DEFAULT_PROFILE_IMAGE = "default_profile.png";
    private static final Logger LOGGER = Logger.getLogger(ProfileController.class.getName());

    // Initialize the controller
    public void initialize() {
        User loggedInUser = GlobalState.getInstance().getLoggedInUser();

        currentStudent = loggedInUser;
        loggedInStudentId = loggedInUser.getStudentId();
        loadStudentData();

        // No need to load student data here, it will be loaded when the student ID is set
        LOGGER.info("ProfileController initialized.");
    }

    public void setLoggedInStudentId(String studentId) {
        this.loggedInStudentId = studentId;
        LOGGER.log(Level.INFO, "Setting loggedInStudentId to: {0}", studentId);
        loadStudentData();
        try {
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

        }
    }

    private void loadStudentData() {
        try {
            if (currentStudent != null) {
                populateUIFields(currentStudent);
                loadProfilePicture();
//                displayStudentInfo(); // Call displayStudentInfo after loading data

            } else {
                LOGGER.warning("Student not found with ID: " + loggedInStudentId);
                showAlert("Error", "Student not found!", Alert.AlertType.ERROR);
//                displayStudentInfo();

            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading student data: " + e.getMessage(), e);
            showAlert("Error", "Failed to load student data.", Alert.AlertType.ERROR);
        }
    }

    private void populateUIFields(User student) {
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

        // Fetch default image file and load into memory
        String defaultImagePath = IMAGE_STORAGE_DIRECTORY + DEFAULT_PROFILE_IMAGE;
        File defaultImageFile = new File(defaultImagePath);
        Image defaultImage = new Image(defaultImageFile.toURI().toString());

        System.out.println("Default image: " + defaultImage);

        Image image = null;

        if (currentStudent != null && currentStudent.getProfilePhoto() != null && !currentStudent.getProfilePhoto().isEmpty()) {
            String imagePath = IMAGE_STORAGE_DIRECTORY + currentStudent.getProfilePhoto();
            File imageFile = new File(imagePath);

            if (imageFile.exists()) {
                try {
                    image = new Image(imageFile.toURI().toString());
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Error loading profile picture from file: " + imagePath, e);
                    image = defaultImage;
                }
            } else {
                LOGGER.warning("Profile picture not found at: " + imagePath);
                image = defaultImage;
            }
        } else {
            image = defaultImage;
        }

        profileImageView.setImage(image);
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
                //  Delete previous profile picture if it exists and is not the default
                if (currentStudent.getProfilePhoto() != null && !currentStudent.getProfilePhoto().isEmpty()) {
                    Path oldImagePath = Paths.get(IMAGE_STORAGE_DIRECTORY, currentStudent.getProfilePhoto());
                    File oldImageFile = oldImagePath.toFile();
                    if (oldImageFile.exists()) {
                        boolean deleted = oldImageFile.delete();
                        if (!deleted) {
                            LOGGER.warning("Failed to delete old profile picture: " + oldImagePath);
                        }
                    }
                }

                // Store the image and update the student's profile photo
                String newFileName = storeImage(selectedFile);

                System.out.println(newFileName);

                currentStudent.setProfilePhoto(newFileName);
                // Save the updated student information to the database
                ExcelDatabase.saveUser(currentStudent); // Use the saveUser method to update
                // Load the new profile picture
                loadProfilePicture();

            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error storing image: " + e.getMessage(), e);
                showAlert("Error", "Failed to store image.", Alert.AlertType.ERROR);
            }
        }
    }

    private String storeImage(File selectedFile) throws IOException {

        File directory = new File(IMAGE_STORAGE_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Generate a unique file name
        String fileExtension = getFileExtension(selectedFile.getName());
        String newFileName = currentStudent.getStudentId() + "." + fileExtension;
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
            currentStudent.setName(firstNameField.getText() + " " + lastNameField.getText());
            currentStudent.setAddress(addressField.getText());
            currentStudent.setTelephone(phoneField.getText());

            ExcelDatabase.saveUser(currentStudent);

        }
    }
    @FXML
    protected void onChangePasswordButtonClick() {
        System.out.println("Current password from currentStudent: " + currentStudent.getPassword());

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Enter your current and new password:");

        // Custom dialog pane
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Create password fields
        PasswordField currentPasswordField = new PasswordField();
        currentPasswordField.setPromptText("Current Password");
        currentPasswordField.setText(currentStudent.getPassword());
        currentPasswordField.setEditable(false);

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New Password (8+ characters)");

        PasswordField confirmNewPasswordField = new PasswordField();
        confirmNewPasswordField.setPromptText("CONFIRM New Password (8+ characters)");

        // Create layout
        VBox content = new VBox(10);
        content.getChildren().addAll(
                new Label("Current Password:"), currentPasswordField,
                new Label("New Password:"), newPasswordField,
                new Label("Confirm New Password:"), confirmNewPasswordField
        );

        dialogPane.setContent(content);

        // Enable/Disable OK button based on password length
        final Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setDisable(true);

        newPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(newValue.trim().length() < 8);
        });

        // Convert the result to update password
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return ButtonType.OK;
            }
            return null;
        });

        // Process the result
        Optional<ButtonType> result = dialog.showAndWait();
        result.ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                String currentPassword = currentPasswordField.getText();
                String newPassword = newPasswordField.getText();
                String confirmNewPassword = confirmNewPasswordField.getText();


                // Check if the current password matches
                if (!currentStudent.getPassword().equals(currentPassword)) {
                    showAlert("Error", "Incorrect current password.", Alert.AlertType.ERROR);
                    return;
                }

                // Check if the new password meets the length criteria
                if (newPassword.length() < 8) {
                    showAlert("Error", "New password must be at least 8 characters long.", Alert.AlertType.ERROR);
                    return;
                }
                if (!confirmNewPassword.equals(newPassword)) {
                    showAlert("Error", "New passwords do not match.", Alert.AlertType.ERROR);
                    return;
                }

                // Update the password in the database
                boolean success = ExcelDatabase.changePassword(currentStudent.getId(), newPassword);
                if (success) {
                    currentStudent.setPassword(newPassword);
                    showAlert("Success", "Password updated successfully!", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Error", "Failed to update password.", Alert.AlertType.ERROR);
                }
            }
        });
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

//            StudentManagementMenuController studentManagementController = loader.getController();
//            studentManagementController.setLoggedInStudentId(loggedInStudentId);

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

    @FXML
    private void handleChangePassword(ActionEvent event) {
        if (currentStudent == null) {
            showAlert("Error", "No logged-in user found.", Alert.AlertType.ERROR);
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Enter your new password:");
        dialog.setContentText("New password:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newPassword -> {
            boolean success = ExcelDatabase.changePassword(currentStudent.getId(), newPassword);
            if (success) {
                showAlert("Success", "Password updated successfully!", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Error", "Failed to update password.", Alert.AlertType.ERROR);
            }
        });
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}