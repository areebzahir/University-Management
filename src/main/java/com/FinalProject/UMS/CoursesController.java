package com.FinalProject.UMS;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CoursesController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(CoursesController.class.getName());
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");

    // Model class for course data
    public static class CourseEntry {
        private final SimpleStringProperty courseCode;
        private final SimpleStringProperty courseName;
        private final SimpleStringProperty department;
        private final SimpleStringProperty credits;

        public CourseEntry(String courseCode, String courseName, String department, String credits) {
            this.courseCode = new SimpleStringProperty(courseCode);
            this.courseName = new SimpleStringProperty(courseName);
            this.department = new SimpleStringProperty(department);
            this.credits = new SimpleStringProperty(credits);
        }

        public String getCourseCode() { return courseCode.get(); }
        public String getCourseName() { return courseName.get(); }
        public String getDepartment() { return department.get(); }
        public String getCredits() { return credits.get(); }
    }

    @FXML private Label studentNameLabel;
    @FXML private Label studentIdLabel;
    @FXML private Label academicLevelLabel;
    @FXML private Label semesterLabel;
    @FXML private TableView<CourseEntry> coursesTableView;
    @FXML private TableColumn<CourseEntry, String> codeColumn;
    @FXML private TableColumn<CourseEntry, String> nameColumn;
    @FXML private TableColumn<CourseEntry, String> departmentColumn;
    @FXML private TableColumn<CourseEntry, String> creditsColumn;
    @FXML private VBox mainContainer;
    @FXML private Label totalCoursesLabel;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Button returnButton;
    @FXML private Button printButton;
    @FXML private Label lastUpdatedLabel;

    private Map<String, String> courseDatabase;
    private User currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Setup table columns
        setupTableColumns();

        // Load course data from the database
        loadCourseData();

        // Display courses for the logged-in user
        loadLoggedInUserCourses();

        // Update last updated time
        updateLastUpdatedTime();
    }

    /**
     * Load course database from Excel
     */
    private void loadCourseData() {
        // Show loading indicator
        showLoading(true);

        try {
            // Load subjects from Excel database
            courseDatabase = ExcelDatabase.loadSubjects();
            LOGGER.info("Loaded " + courseDatabase.size() + " courses from database");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading course database: " + e.getMessage(), e);
            showError("Failed to load course database: " + e.getMessage());
            courseDatabase = new HashMap<>();
        } finally {
            // Hide loading indicator
            showLoading(false);
        }
    }

    /**
     * Load and display courses for the logged-in user
     */
    private void loadLoggedInUserCourses() {
        // Get the logged-in user from GlobalState
        User loggedInUser = GlobalState.getInstance().getLoggedInUser();

        if (loggedInUser != null) {
            setStudentData(loggedInUser);
        } else {
            LOGGER.warning("No user is currently logged in");
            showError("Please log in to view your courses");
        }
    }

    /**
     * Setup the table columns
     */
    private void setupTableColumns() {
        codeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCourseCode()));
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCourseName()));
        departmentColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDepartment()));
        creditsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCredits()));

        // Set column widths
        codeColumn.prefWidthProperty().bind(coursesTableView.widthProperty().multiply(0.2));
        nameColumn.prefWidthProperty().bind(coursesTableView.widthProperty().multiply(0.4));
        departmentColumn.prefWidthProperty().bind(coursesTableView.widthProperty().multiply(0.25));
        creditsColumn.prefWidthProperty().bind(coursesTableView.widthProperty().multiply(0.15));
    }

    /**
     * Main method to set student data and display courses
     * @param user The logged-in user (student)
     */
    public void setStudentData(User user) {
        if (user == null) {
            showError("No user data provided");
            return;
        }

        this.currentUser = user;

        // Show loading indicator
        showLoading(true);

        try {
            // Update student info in the UI
            updateStudentInfo(user);

            // Make sure we have course data
            if (courseDatabase == null || courseDatabase.isEmpty()) {
                loadCourseData();
            }

            // Display the courses registered by this student
            displayCourses(user.getSubjectsRegistered());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error setting student data: " + e.getMessage(), e);
            showError("Failed to load student data: " + e.getMessage());
        } finally {
            // Hide loading indicator
            showLoading(false);
        }
    }

    /**
     * Update student information in the UI
     * @param user The user object
     */
    private void updateStudentInfo(User user) {
        studentNameLabel.setText(user.getName());
        studentIdLabel.setText("ID: " + user.getId());
        academicLevelLabel.setText(user.getAcademicLevel());
        semesterLabel.setText(user.getCurrentSemester());
    }

    /**
     * Display courses registered by the student
     * @param coursesRegistered Comma-separated list of course codes
     */
    private void displayCourses(String coursesRegistered) {
        ObservableList<CourseEntry> courseEntries = FXCollections.observableArrayList();

        if (coursesRegistered == null || coursesRegistered.trim().isEmpty()) {
            totalCoursesLabel.setText("Total: 0 courses");
            coursesTableView.setPlaceholder(new Label("No courses registered"));
            coursesTableView.setItems(courseEntries);
            return;
        }

        // Split courses by comma and process each one
        String[] courseCodes = coursesRegistered.split("\\s*,\\s*");
        LOGGER.info("Processing " + courseCodes.length + " courses for student " + currentUser.getId());

        for (String courseCode : courseCodes) {
            if (!courseCode.isEmpty()) {
                CourseEntry entry = createCourseEntry(courseCode.trim());
                if (entry != null) {
                    courseEntries.add(entry);
                }
            }
        }

        coursesTableView.setItems(courseEntries);
        totalCoursesLabel.setText("Total: " + courseEntries.size() + " course" + (courseEntries.size() != 1 ? "s" : ""));
    }

    /**
     * Create a course entry from course code
     * @param courseCode The course code
     * @return CourseEntry object
     */
    private CourseEntry createCourseEntry(String courseCode) {
        try {
            // Get course name from the Excel database
            String courseName = courseDatabase.getOrDefault(courseCode, "Unknown Course");
            String department = getDepartmentFromCode(courseCode);
            String credits = getCreditsByCode(courseCode);

            LOGGER.fine("Creating course entry: " + courseCode + " - " + courseName);
            return new CourseEntry(courseCode, courseName, department, credits);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error creating course entry for code " + courseCode + ": " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Extract department name from course code
     * @param code The course code
     * @return Department name
     */
    private String getDepartmentFromCode(String code) {
        // Extract department prefix (letters before numbers)
        String dept = code.replaceAll("[^A-Za-z]", "");

        // Map department codes to full names
        Map<String, String> departments = new HashMap<>();
        departments.put("CS", "Computer Science");
        departments.put("ENG", "English");
        departments.put("ENGG", "Engineering");
        departments.put("HIST", "History");
        departments.put("BIO", "Biology");
        departments.put("CHEM", "Chemistry");
        departments.put("MUSIC", "Music");
        departments.put("PSYCHO", "Psychology");
        departments.put("MATH", "Mathematics");
        departments.put("PHY", "Physics");

        return departments.getOrDefault(dept, "General Studies");
    }

    /**
     * Determine credit hours based on course code
     * @param code The course code
     * @return Credit hours as string
     */
    private String getCreditsByCode(String code) {
        // Extract the numeric part (course level)
        String level = code.replaceAll("[^0-9]", "");
        if (!level.isEmpty()) {
            int courseLevel = Integer.parseInt(level.substring(0, 1));
            return String.valueOf(Math.min(courseLevel + 2, 6)); // 100-level = 3, 200-level = 4, etc.
        }
        return "3"; // Default to 3 credits
    }

    /**
     * Update the last updated time label
     */
    private void updateLastUpdatedTime() {
        lastUpdatedLabel.setText("Last updated: " + LocalDateTime.now().format(DATE_FORMATTER));
    }

    /**
     * Show/hide loading indicator
     * @param visible Whether loading indicator should be visible
     */
    private void showLoading(boolean visible) {
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(visible);
        }
        if (mainContainer != null) {
            mainContainer.setDisable(visible);
        }
    }

    /**
     * Show error alert
     * @param message Error message
     */
    private void showError(String message) {
        LOGGER.log(Level.WARNING, message);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Refresh the course list from the database
     */
    @FXML
    private void refreshCourses() {
        loadCourseData();
        if (currentUser != null) {
            // Reload the user data to get the latest courses
            User refreshedUser = reloadUserData(currentUser.getId());
            if (refreshedUser != null) {
                displayCourses(refreshedUser.getSubjectsRegistered());
            } else {
                displayCourses(currentUser.getSubjectsRegistered());
            }
        }
        updateLastUpdatedTime();
    }

    /**
     * Reload user data from the database
     * @param userId User ID to reload
     * @return Updated User object or null if not found
     */
    private User reloadUserData(String userId) {
        Map<String, User> users = ExcelDatabase.loadUsers();
        return users.get(userId);
    }

    /**
     * Handle print button click
     */
    @FXML
    private void handlePrintButton() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Print");
        alert.setHeaderText(null);
        alert.setContentText("Printing course list...");
        alert.showAndWait();
    }
    // Add these to your CoursesController.java or create separate files

    // Model for academic progress by semester
    public static class AcademicProgress {
        private final String semester;
        private final ObservableList<CourseGrade> courses;
        private final double semesterGPA;
        private final int creditsEarned;
        private final int creditsAttempted;

        public AcademicProgress(String semester, ObservableList<CourseGrade> courses,
                                double semesterGPA, int creditsEarned, int creditsAttempted) {
            this.semester = semester;
            this.courses = courses;
            this.semesterGPA = semesterGPA;
            this.creditsEarned = creditsEarned;
            this.creditsAttempted = creditsAttempted;
        }

        // Getters
        public String getSemester() { return semester; }
        public ObservableList<CourseGrade> getCourses() { return courses; }
        public double getSemesterGPA() { return semesterGPA; }
        public int getCreditsEarned() { return creditsEarned; }
        public int getCreditsAttempted() { return creditsAttempted; }
    }

    // Model for course grades
    public static class CourseGrade {
        private final String courseCode;
        private final String courseName;
        private final int credits;
        private final String grade;
        private final boolean passed;

        public CourseGrade(String courseCode, String courseName, int credits, String grade) {
            this.courseCode = courseCode;
            this.courseName = courseName;
            this.credits = credits;
            this.grade = grade;
            this.passed = calculatePassed(grade);
        }

        private boolean calculatePassed(String grade) {
            // Define which grades are passing
            return !(grade.equals("F") || grade.equals("W") || grade.equals("U"));
        }

        // Getters
        public String getCourseCode() { return courseCode; }
        public String getCourseName() { return courseName; }
        public int getCredits() { return credits; }
        public String getGrade() { return grade; }
        public boolean isPassed() { return passed; }
    }

    // Model for graduation progress
    public static class GraduationProgress {
        private final int totalCreditsEarned;
        private final int totalCreditsRequired;
        private final double cumulativeGPA;
        private final Map<String, Boolean> requirements; // e.g., "Core Courses", "Electives", etc.

        public GraduationProgress(int totalCreditsEarned, int totalCreditsRequired,
                                  double cumulativeGPA, Map<String, Boolean> requirements) {
            this.totalCreditsEarned = totalCreditsEarned;
            this.totalCreditsRequired = totalCreditsRequired;
            this.cumulativeGPA = cumulativeGPA;
            this.requirements = requirements;
        }

        // Getters
        public int getTotalCreditsEarned() { return totalCreditsEarned; }
        public int getTotalCreditsRequired() { return totalCreditsRequired; }
        public double getCumulativeGPA() { return cumulativeGPA; }
        public Map<String, Boolean> getRequirements() { return requirements; }

        public double getProgressPercentage() {
            return (double) totalCreditsEarned / totalCreditsRequired * 100;
        }
    }

    /**
     *
     * Handle return button click
     */
    @FXML
    private void handleReturnButton() {
        // Implementation for return button if needed
    }
}