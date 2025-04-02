package com.FinalProject.UMS;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class ExcelDatabase {

    private static final String FILE_PATH ="C:\\Users\\azahi\\OneDrive - University of Guelph\\1st Year\\Semester 2\\University-Management\\src\\main\\resources\\UMS_Data.xlsx";
    private static final String STUDENT_SHEET_NAME = "Students";
    private static final String SUBJECT_SHEET_NAME = "Subjects";
    private static final int ID_COLUMN = 0;
    private static final int NAME_COLUMN = 1;
    private static final int ADDRESS_COLUMN = 2;
    private static final int TELEPHONE_COLUMN = 3;
    private static final int EMAIL_COLUMN = 4;
    private static final int ACADEMIC_LEVEL_COLUMN = 5;
    private static final int CURRENT_SEMESTER_COLUMN = 6;
    private static final int PROFILE_PHOTO_COLUMN = 7;
    private static final int SUBJECTS_REGISTERED_COLUMN = 8;
    private static final int THESIS_TITLE_COLUMN = 9;
    private static final int PROGRESS_COLUMN = 10;
    private static final int PASSWORD_COLUMN = 11;
    private static final int ROLE_COLUMN = 12; // Adjust the index based on your Excel sheet
    private static final boolean HAS_HEADER_ROW = true;

    // Subject Sheet Column Definitions
    private static final int SUBJECT_CODE_COLUMN = 0;
    private static final int SUBJECT_NAME_COLUMN = 1;
    private static final boolean HAS_SUBJECT_HEADER_ROW = true;

    private static final int STUDENT_SHEET_INDEX = 2;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );

    private static final Logger LOGGER = Logger.getLogger(ExcelDatabase.class.getName());

    public static List<User> loadUsersAsList() {
        Map<String, User> userMap = ExcelDatabase.loadUsers();

        return (List)userMap.values();
    }

    public static void saveUser(User user) {
        File excelFile = new File(FILE_PATH); // Use constant for file path

        try (FileInputStream fis = new FileInputStream(excelFile);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(STUDENT_SHEET_NAME); //Use Constant for sheet name

            if (sheet == null) {
                LOGGER.severe("Sheet not found: " + STUDENT_SHEET_NAME);
                return; // Exit if the sheet is not found
            }

            int firstRow = HAS_HEADER_ROW ? 1 : 0; // Start from the first data row

            // Iterate through rows to find the user by ID
            for (int rowNum = firstRow; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null) {
                    continue; // Skip empty rows
                }

                Cell idCell = row.getCell(ID_COLUMN);
                if (idCell == null) {
                    continue; // Skip rows without an ID
                }

                String id = getCellValueAsString(idCell);
                if (id.equals(user.getStudentId())) {  // Compare with studentId, not getId()
                    // Found the user, update the row

                    try {
                        //Update each cell
                        updateCell(row, NAME_COLUMN, user.getName(), workbook);
                        updateCell(row, ADDRESS_COLUMN, user.getAddress(), workbook);
                        updateCell(row, TELEPHONE_COLUMN, user.getTelephone(), workbook);
                        updateCell(row, EMAIL_COLUMN, user.getEmail(), workbook);
                        updateCell(row, ACADEMIC_LEVEL_COLUMN, user.getAcademicLevel(), workbook);
                        updateCell(row, CURRENT_SEMESTER_COLUMN, user.getCurrentSemester(), workbook);
                        updateCell(row, PROFILE_PHOTO_COLUMN, user.getProfilePhoto(), workbook); // Update Profile Photo
                        updateCell(row, SUBJECTS_REGISTERED_COLUMN, user.getSubjectsRegistered(), workbook);
                        updateCell(row, THESIS_TITLE_COLUMN, user.getThesisTitle(), workbook);
                        updateCell(row, PROGRESS_COLUMN, String.valueOf(user.getProgress()), workbook);  // Use String.valueOf
                        updateCell(row, PASSWORD_COLUMN, user.getPassword(), workbook);

                        // Write the changes to the file
                        try (FileOutputStream fos = new FileOutputStream(FILE_PATH)) {
                            workbook.write(fos);
                            LOGGER.info("Excel file updated successfully for student ID: " + user.getStudentId());
                        }
                        return; // Exit after successful update
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Error updating row " + rowNum + ": " + e.getMessage(), e);
                        return;
                    }
                }
            }

            LOGGER.warning("Student with ID " + user.getStudentId() + " not found in the Excel sheet.");

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading or saving Excel file: " + e.getMessage(), e);
        }
    }
    //Helper method to update cell value
    private static void updateCell(Row row, int columnIndex, String value, Workbook workbook) {
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            cell = row.createCell(columnIndex);
        }
        cell.setCellValue(value);
    }

    public static Sheet getUserSheet(Workbook workbook) {

        Sheet sheet = workbook.getSheetAt(STUDENT_SHEET_INDEX);
        if (sheet == null) {
            LOGGER.warning("Sheet '" + STUDENT_SHEET_NAME + "' not found.");
            return null;
        }

        return sheet;

    }

    public static Workbook getWorkbook() {
        File excelFile = new File(FILE_PATH);

        FileInputStream fis = null;
        Workbook workbook = null;

        try {

            fis = new FileInputStream(excelFile);
            workbook = new XSSFWorkbook(fis);

            if (workbook == null) {
                return null;
            }

            return workbook;

        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error updating password: " + e.getMessage(), e);
        }

        return null;

    }

    public static boolean changePassword(String userId, String newPassword) {

        Workbook workbook = getWorkbook();
        if (workbook == null) return false;

        Sheet sheet = getUserSheet(workbook);
        if (sheet == null) return false;

        Iterator<Row> rowIterator = sheet.rowIterator();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();

            if (HAS_HEADER_ROW && row.getRowNum() == 0) continue;

            Cell idCell = row.getCell(ID_COLUMN);
            String id = (idCell != null) ? getCellValueAsString(idCell) : null;

            if (id != null && id.equals(userId)) {
                // Found user — update password
                Cell passwordCell = row.getCell(PASSWORD_COLUMN);
                if (passwordCell == null) {
                    passwordCell = row.createCell(PASSWORD_COLUMN);
                }
                passwordCell.setCellValue(newPassword);

                try (FileOutputStream fos = new FileOutputStream(FILE_PATH)) {
                    workbook.write(fos);
                    LOGGER.info("Password updated successfully for user ID: " + userId);
                    return true;
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Error updating password: " + e.getMessage(), e);
                }

            }
        }


        LOGGER.warning("User ID not found: " + userId);
        return false;
    }


    public static Map<String, User> loadUsers() {
        Map<String, User> users = new HashMap<>();
        File excelFile = new File(FILE_PATH);
        try (FileInputStream fis = new FileInputStream(excelFile);
             Workbook workbook = new XSSFWorkbook(fis)) {

            DataFormatter formatter = new DataFormatter();
            Iterator<Sheet> sheetIterator = workbook.sheetIterator();
            while (sheetIterator.hasNext()) {
                Sheet sheet = sheetIterator.next();
                String sheetName = sheet.getSheetName();
                LOGGER.log(Level.INFO, "Processing sheet: {0}", sheetName);

                if (sheetName.trim().equals(STUDENT_SHEET_NAME)) {
                    Iterator<Row> rowIterator = sheet.rowIterator();
                    while (rowIterator.hasNext()) {
                        Row row = rowIterator.next();

                        if (HAS_HEADER_ROW && row.getRowNum() == 0) continue;

                        try {

                            Cell idCell = row.getCell(ID_COLUMN);
                            String id = (idCell != null) ? getCellValueAsString(idCell) : null;

                            Cell nameCell = row.getCell(NAME_COLUMN);
                            String name = (nameCell != null) ? getCellValueAsString(nameCell) : null;

                            Cell addressCell = row.getCell(ADDRESS_COLUMN);
                            String address = (addressCell != null) ? getCellValueAsString(addressCell) : null;

                            Cell telephoneCell = row.getCell(TELEPHONE_COLUMN);
                            String telephone = (telephoneCell != null) ? getCellValueAsString(telephoneCell) : null;

                            Cell academicLevelCell = row.getCell(ACADEMIC_LEVEL_COLUMN);
                            String academicLevel = (academicLevelCell != null) ? getCellValueAsString(academicLevelCell) : null;

                            Cell currentSemesterCell = row.getCell(CURRENT_SEMESTER_COLUMN);
                            String currentSemester = (currentSemesterCell != null) ? getCellValueAsString(currentSemesterCell) : null;

                            Cell profilePhotoCell = row.getCell(PROFILE_PHOTO_COLUMN);
                            String profilePhoto = (profilePhotoCell != null) ? getCellValueAsString(profilePhotoCell) : null;

                            Cell subjectsRegisteredCell = row.getCell(SUBJECTS_REGISTERED_COLUMN);
                            String subjectsRegistered = (profilePhotoCell != null) ? getCellValueAsString(profilePhotoCell) : null;

                            Cell thesisTitleCell = row.getCell(THESIS_TITLE_COLUMN);
                            String thesisTitle = (thesisTitleCell != null) ? getCellValueAsString(thesisTitleCell) : null;

                            Cell progressCell = row.getCell(PROGRESS_COLUMN);
                            String progress = (progressCell != null) ? getCellValueAsString(progressCell) : null;

                            Cell emailCell = row.getCell(EMAIL_COLUMN);
                            String email = (emailCell != null) ? getCellValueAsString(emailCell) : null;

                            Cell passwordCell = row.getCell(PASSWORD_COLUMN);
                            String password = (passwordCell != null) ? getCellValueAsString(passwordCell) : null;

                            // Get the student ID
                            Cell studentIdCell = row.getCell(0); // Assuming student ID is in the first column
                            String studentId = (studentIdCell != null) ? getCellValueAsString(studentIdCell) : null;

                            Cell roleCell = row.getCell(ROLE_COLUMN);
                            String role = (roleCell != null) ? getCellValueAsString(roleCell) : "USER"; // Default role to "USER"
                            if ((id == null || id.isEmpty()) && (email == null || email.isEmpty())) {
                                LOGGER.warning("Skipping row " + row.getRowNum() + " due to missing ID and email.");
                                continue;
                            }
                            if (password == null || password.isEmpty()) {
                                LOGGER.warning("Skipping row " + row.getRowNum() + " due to missing password.");
                                continue;
                            }

                            if (email != null && !email.isEmpty() && !EMAIL_PATTERN.matcher(email).matches()) {
                                LOGGER.warning("Skipping row " + row.getRowNum() + " due to invalid email format: " + email);
                                continue;
                            }


                            User user = new User(id, email, password, role);

                            user.setName(name);
                            user.setAddress(address);
                            user.setTelephone(telephone);
                            user.setAcademicLevel(academicLevel);
                            user.setCurrentSemester(currentSemester);
                            user.setProfilePhoto(profilePhoto);
                            user.setSubjectsRegistered(subjectsRegistered);
                            user.setThesisTitle(thesisTitle);
                            user.setProgress(progress);

                            if (id != null && !id.isEmpty())
                                users.put(id, user);
                            if (email != null && !email.isEmpty())
                                users.put(email, user);
                        } catch (Exception e) {
                            LOGGER.log(Level.SEVERE, "Error processing row " + row.getRowNum() + ": " + e.getMessage(), e);
                        }
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading Excel file: " + e.getMessage(), e);
        }
        return users;
    }

    public static void updateStudent(Student student) {
        File excelFile = new File(FILE_PATH);
        Workbook workbook = null;
        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            fis = new FileInputStream(excelFile);
            workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheet(STUDENT_SHEET_NAME);

            if (sheet == null) {
                // Sheet doesn't exist, create it with headers
                sheet = workbook.createSheet(STUDENT_SHEET_NAME);
                Row headerRow = sheet.createRow(0);
                headerRow.createCell(ID_COLUMN).setCellValue("ID");
                headerRow.createCell(NAME_COLUMN).setCellValue("Name");
                headerRow.createCell(ADDRESS_COLUMN).setCellValue("Address");
                headerRow.createCell(TELEPHONE_COLUMN).setCellValue("Telephone");
                headerRow.createCell(EMAIL_COLUMN).setCellValue("Email");
                headerRow.createCell(ACADEMIC_LEVEL_COLUMN).setCellValue("Academic Level");
                headerRow.createCell(CURRENT_SEMESTER_COLUMN).setCellValue("Current Semester");
                headerRow.createCell(PROFILE_PHOTO_COLUMN).setCellValue("Profile Photo");
                headerRow.createCell(SUBJECTS_REGISTERED_COLUMN).setCellValue("Subjects Registered");
                headerRow.createCell(THESIS_TITLE_COLUMN).setCellValue("Thesis Title");
                headerRow.createCell(PROGRESS_COLUMN).setCellValue("Progress");
                headerRow.createCell(PASSWORD_COLUMN).setCellValue("Password");
            }

            // Check if student already exists
            int existingRow = -1;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    Cell idCell = row.getCell(ID_COLUMN);
                    if (idCell != null) {
                        String id = getCellValueAsString(idCell);
                        if (student.getStudentId().equals(id)) {
                            existingRow = i;
                            break;
                        }
                    }
                }
            }

            Row row;
            if (existingRow != -1) {
                // Update existing student
                row = sheet.getRow(existingRow);
            } else {
                // Add new student
                // Find the last row with actual data (not empty)
                int lastDataRow = 0;
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row existingRowData = sheet.getRow(i);
                    if (existingRowData != null) {
                        Cell idCell = existingRowData.getCell(ID_COLUMN);
                        if (idCell != null && !getCellValueAsString(idCell).trim().isEmpty()) {
                            lastDataRow = i;
                        }
                    }
                }

                // Next available row is right after the last data row
                int nextRow = lastDataRow + 1;

                // If no data rows found (only header), start at row 1
                if (nextRow == 1 && sheet.getRow(0) != null) {
                    nextRow = 1;
                }

                // Create the new row
                row = sheet.createRow(nextRow);
                row.createCell(ID_COLUMN).setCellValue(student.getStudentId());

                // Set default password for new students if not provided
                if (student.getPassword() == null || student.getPassword().isEmpty()) {
                    row.createCell(PASSWORD_COLUMN).setCellValue("default123");
                } else {
                    row.createCell(PASSWORD_COLUMN).setCellValue(student.getPassword());
                }
            }

            // Update all fields
            if (existingRow == -1 || row.getCell(NAME_COLUMN) == null) {
                row.createCell(NAME_COLUMN).setCellValue(student.getName());
            } else {
                row.getCell(NAME_COLUMN).setCellValue(student.getName());
            }

            if (existingRow == -1 || row.getCell(ADDRESS_COLUMN) == null) {
                row.createCell(ADDRESS_COLUMN).setCellValue(student.getAddress());
            } else {
                row.getCell(ADDRESS_COLUMN).setCellValue(student.getAddress());
            }

            if (existingRow == -1 || row.getCell(TELEPHONE_COLUMN) == null) {
                row.createCell(TELEPHONE_COLUMN).setCellValue(student.getTelephone());
            } else {
                row.getCell(TELEPHONE_COLUMN).setCellValue(student.getTelephone());
            }

            if (existingRow == -1 || row.getCell(EMAIL_COLUMN) == null) {
                row.createCell(EMAIL_COLUMN).setCellValue(student.getEmail());
            } else {
                row.getCell(EMAIL_COLUMN).setCellValue(student.getEmail());
            }

            if (existingRow == -1 || row.getCell(ACADEMIC_LEVEL_COLUMN) == null) {
                row.createCell(ACADEMIC_LEVEL_COLUMN).setCellValue(student.getAcademicLevel());
            } else {
                row.getCell(ACADEMIC_LEVEL_COLUMN).setCellValue(student.getAcademicLevel());
            }

            if (existingRow == -1 || row.getCell(CURRENT_SEMESTER_COLUMN) == null) {
                row.createCell(CURRENT_SEMESTER_COLUMN).setCellValue(student.getCurrentSemester());
            } else {
                row.getCell(CURRENT_SEMESTER_COLUMN).setCellValue(student.getCurrentSemester());
            }

            if (existingRow == -1 || row.getCell(PROFILE_PHOTO_COLUMN) == null) {
                row.createCell(PROFILE_PHOTO_COLUMN).setCellValue(student.getProfilePhoto());
            } else {
                row.getCell(PROFILE_PHOTO_COLUMN).setCellValue(student.getProfilePhoto());
            }

            if (existingRow == -1 || row.getCell(SUBJECTS_REGISTERED_COLUMN) == null) {
                row.createCell(SUBJECTS_REGISTERED_COLUMN).setCellValue(student.getSubjectsRegistered());
            } else {
                row.getCell(SUBJECTS_REGISTERED_COLUMN).setCellValue(student.getSubjectsRegistered());
            }

            if (existingRow == -1 || row.getCell(THESIS_TITLE_COLUMN) == null) {
                row.createCell(THESIS_TITLE_COLUMN).setCellValue(student.getThesisTitle());
            } else {
                row.getCell(THESIS_TITLE_COLUMN).setCellValue(student.getThesisTitle());
            }

            if (existingRow == -1 || row.getCell(PROGRESS_COLUMN) == null) {
                row.createCell(PROGRESS_COLUMN).setCellValue(student.getProgress());
            } else {
                row.getCell(PROGRESS_COLUMN).setCellValue(student.getProgress());
            }

            // Only update password during edits if explicitly provided
            if (existingRow != -1 && student.getPassword() != null && !student.getPassword().isEmpty()) {
                row.getCell(PASSWORD_COLUMN).setCellValue(student.getPassword());
            }

            fos = new FileOutputStream(FILE_PATH);
            workbook.write(fos);

            String action = (existingRow != -1) ? "updated" : "added";
            LOGGER.info("Student " + student.getName() + " " + action + " in Excel.");

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error updating student in Excel: " + e.getMessage(), e);
        } finally {
            closeResources(fis, fos, workbook);
        }}
    public static String generateUniqueStudentId() {
        Map<String, User> users = loadUsers();
        int highestId = 0;

        for (String key : users.keySet()) {
            User user = users.get(key);
            if (user.getId() != null && user.getId().startsWith("S")) {
                try {
                    // Extract the numeric part after 'S'
                    int id = Integer.parseInt(user.getId().substring(1));
                    if (id > highestId) {
                        highestId = id;
                    }
                } catch (NumberFormatException e) {
                    // Ignore non-numeric IDs
                }
            }
        }

        // Generate new ID with the next number
        return String.format("S%08d", highestId + 1);
    }
    public static boolean registerStudentForCourse(String studentId, String courseCode) {
        // Validate inputs
        if (studentId == null || studentId.isEmpty() || courseCode == null || courseCode.isEmpty()) {
            LOGGER.log(Level.WARNING, "Invalid student ID or course code provided");
            return false;
        }

        // Check if the course exists
        boolean courseExists = checkIfCourseExists(courseCode);
        if (!courseExists) {
            LOGGER.log(Level.WARNING, "Course " + courseCode + " does not exist");
            return false;
        }

        // Load current users
        Map<String, User> users = loadUsers();
        User student = null;

        // Find the student
        for (User user : users.values()) {
            if (user.getId() != null && user.getId().equals(studentId)) {
                student = user;
                break;
            }
        }

        if (student == null) {
            LOGGER.log(Level.WARNING, "Student with ID " + studentId + " not found");
            return false;
        }

        // Get current subjects registered
        String currentSubjects = student.getSubjectsRegistered();

        // Check if already registered for this course
        if (currentSubjects != null && !currentSubjects.isEmpty()) {
            String[] subjectArray = currentSubjects.split(",");
            for (String subject : subjectArray) {
                if (subject.trim().equals(courseCode)) {
                    LOGGER.log(Level.WARNING, "Student already registered for course " + courseCode);
                    return false;
                }
            }

            // Add the new course to existing courses
            currentSubjects = currentSubjects.trim() + "," + courseCode;
        } else {
            // First course for this student
            currentSubjects = courseCode;
        }

        // Update the subjects registered
        student.setSubjectsRegistered(currentSubjects);

        // Save the updated user information back to Excel
        saveUser(student);

        LOGGER.log(Level.INFO, "Student " + studentId + " successfully registered for course " + courseCode);
        return true;
    }

    /**
     * Checks if a course exists in the subjects sheet.
     *
     * @param courseCode The course code to check
     * @return true if the course exists, false otherwise
     */
    private static boolean checkIfCourseExists(String courseCode) {
        File excelFile = new File(FILE_PATH);
        try (FileInputStream fis = new FileInputStream(excelFile);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet subjectSheet = workbook.getSheet(SUBJECT_SHEET_NAME);
            if (subjectSheet == null) {
                LOGGER.log(Level.SEVERE, "Subject sheet not found");
                return false;
            }

            Iterator<Row> rowIterator = subjectSheet.rowIterator();
            // Skip header if exists
            if (HAS_SUBJECT_HEADER_ROW && rowIterator.hasNext()) {
                rowIterator.next();
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Cell codeCell = row.getCell(SUBJECT_CODE_COLUMN);

                if (codeCell != null) {
                    String code = getCellValueAsString(codeCell);
                    if (code != null && code.equals(courseCode)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error checking if course exists: " + e.getMessage(), e);
        }

        return false;
    }


    public static boolean deleteStudent(String studentId) {
        File excelFile = new File(FILE_PATH);
        Workbook workbook = null;
        FileInputStream fis = null;
        FileOutputStream fos = null;
        boolean success = false;

        try {
            fis = new FileInputStream(excelFile);
            workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheet(STUDENT_SHEET_NAME);

            if (sheet == null) {
                LOGGER.warning("Sheet " + STUDENT_SHEET_NAME + " not found in Excel file.");
                return false;
            }

            int rowToDelete = -1;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    Cell idCell = row.getCell(ID_COLUMN);
                    if (idCell != null) {
                        String id = getCellValueAsString(idCell);
                        if (studentId.equals(id)) {
                            rowToDelete = i;
                            break;
                        }
                    }
                }
            }

            if (rowToDelete != -1) {
                sheet.removeRow(sheet.getRow(rowToDelete));
                if (rowToDelete < sheet.getLastRowNum()) {
                    sheet.shiftRows(rowToDelete + 1, sheet.getLastRowNum(), -1);
                }

                fos = new FileOutputStream(FILE_PATH);
                workbook.write(fos);
                LOGGER.info("Student with ID " + studentId + " deleted from Excel.");
                success = true;
            } else {
                LOGGER.warning("Student with ID " + studentId + " not found in Excel for deletion.");
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error deleting student from Excel: " + e.getMessage(), e);
        } finally {
            closeResources(fis, fos, workbook);
        }

        return success;
    }
    public static boolean unregisterStudentFromCourse(String studentId, String courseCode) {
        // Validate inputs
        if (studentId == null || studentId.isEmpty() || courseCode == null || courseCode.isEmpty()) {
            LOGGER.log(Level.WARNING, "Invalid student ID or course code provided");
            return false;
        }

        // Load current users
        Map<String, User> users = loadUsers();
        User student = null;

        // Find the student
        for (User user : users.values()) {
            if (user.getId() != null && user.getId().equals(studentId)) {
                student = user;
                break;
            }
        }

        if (student == null) {
            LOGGER.log(Level.WARNING, "Student with ID " + studentId + " not found");
            return false;
        }

        // Get current subjects registered
        String currentSubjects = student.getSubjectsRegistered();

        if (currentSubjects == null || currentSubjects.isEmpty()) {
            LOGGER.log(Level.WARNING, "Student not registered for any courses");
            return false;
        }

        // Check if registered for this course and remove it
        String[] subjectArray = currentSubjects.split(",");
        StringBuilder updatedSubjects = new StringBuilder();
        boolean courseFound = false;

        for (String subject : subjectArray) {
            String trimmedSubject = subject.trim();
            if (!trimmedSubject.equals(courseCode)) {
                // Keep this course in the list
                if (updatedSubjects.length() > 0) {
                    updatedSubjects.append(",");
                }
                updatedSubjects.append(trimmedSubject);
            } else {
                courseFound = true;
            }
        }

        if (!courseFound) {
            LOGGER.log(Level.WARNING, "Student not registered for course " + courseCode);
            return false;
        }

        // Update the subjects registered
        student.setSubjectsRegistered(updatedSubjects.toString());

        // Save the updated user information back to Excel
        saveUser(student);

        LOGGER.log(Level.INFO, "Student " + studentId + " successfully unregistered from course " + courseCode);
        return true;
    }

    /**
     * Gets all courses a student is currently registered for.
     *
     * @param studentId The ID of the student
     * @return String array of course codes, or null if student not found
     */
    public static String[] getStudentCourses(String studentId) {
        if (studentId == null || studentId.isEmpty()) {
            return null;
        }

        // Load current users
        Map<String, User> users = loadUsers();

        // Find the student
        for (User user : users.values()) {
            if (user.getId() != null && user.getId().equals(studentId)) {
                String courses = user.getSubjectsRegistered();
                if (courses == null || courses.isEmpty()) {
                    return new String[0];
                }

                String[] courseArray = courses.split(",");
                for (int i = 0; i < courseArray.length; i++) {
                    courseArray[i] = courseArray[i].trim();
                }

                return courseArray;
            }
        }

        return null;
    }

    // Improve the deleteStudentFromExcel method for better integration


    public static void addStudentToExcel(Student student) {
        File excelFile = new File(FILE_PATH);
        Workbook workbook = null;
        Sheet sheet = null;
        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            fis = new FileInputStream(excelFile);
            workbook = new XSSFWorkbook(fis);
            sheet = workbook.getSheet(STUDENT_SHEET_NAME);

            if (sheet == null) {
                sheet = workbook.createSheet(STUDENT_SHEET_NAME);
                Row headerRow = sheet.createRow(0);
                headerRow.createCell(ID_COLUMN).setCellValue("ID");
                headerRow.createCell(NAME_COLUMN).setCellValue("Name");
                headerRow.createCell(ADDRESS_COLUMN).setCellValue("Address");
                headerRow.createCell(TELEPHONE_COLUMN).setCellValue("Telephone");
                headerRow.createCell(EMAIL_COLUMN).setCellValue("Email");
                headerRow.createCell(ACADEMIC_LEVEL_COLUMN).setCellValue("Academic Level");
                headerRow.createCell(CURRENT_SEMESTER_COLUMN).setCellValue("Current Semester");
                headerRow.createCell(PROFILE_PHOTO_COLUMN).setCellValue("Profile Photo");
                headerRow.createCell(SUBJECTS_REGISTERED_COLUMN).setCellValue("Subjects Registered");
                headerRow.createCell(THESIS_TITLE_COLUMN).setCellValue("Thesis Title");
                headerRow.createCell(PROGRESS_COLUMN).setCellValue("Progress");
                headerRow.createCell(PASSWORD_COLUMN).setCellValue("Password");
            }

            int nextRow = sheet.getLastRowNum() + 1;
            Row row = sheet.createRow(nextRow);

            row.createCell(ID_COLUMN).setCellValue(student.getStudentId());
            row.createCell(NAME_COLUMN).setCellValue(student.getName());
            row.createCell(ADDRESS_COLUMN).setCellValue(student.getAddress());
            row.createCell(TELEPHONE_COLUMN).setCellValue(student.getTelephone());
            row.createCell(EMAIL_COLUMN).setCellValue(student.getEmail());
            row.createCell(ACADEMIC_LEVEL_COLUMN).setCellValue(student.getAcademicLevel());
            row.createCell(CURRENT_SEMESTER_COLUMN).setCellValue(student.getCurrentSemester());
            row.createCell(PROFILE_PHOTO_COLUMN).setCellValue(student.getProfilePhoto());
            row.createCell(SUBJECTS_REGISTERED_COLUMN).setCellValue(student.getSubjectsRegistered());
            row.createCell(THESIS_TITLE_COLUMN).setCellValue(student.getThesisTitle());
            row.createCell(PROGRESS_COLUMN).setCellValue(student.getProgress());
            row.createCell(PASSWORD_COLUMN).setCellValue(student.getPassword());

            fos = new FileOutputStream(FILE_PATH);
            workbook.write(fos);
            LOGGER.info("Student " + student.getName() + " added to Excel successfully.");

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error adding student to Excel: " + e.getMessage(), e);
        } finally {
            closeResources(fis, fos, workbook);
        }
    }

    public static void editStudentInExcel(Student student) {
        File excelFile = new File(FILE_PATH);
        Workbook workbook = null;
        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            fis = new FileInputStream(excelFile);
            workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheet(STUDENT_SHEET_NAME);

            if (sheet == null) {
                LOGGER.warning("Sheet " + STUDENT_SHEET_NAME + " not found in Excel file.");
                return;
            }

            int rowToEdit = -1;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    Cell idCell = row.getCell(ID_COLUMN);
                    if (idCell != null) {
                        String id = getCellValueAsString(idCell);
                        if (student.getStudentId().equals(id)) {
                            rowToEdit = i;
                            break;
                        }
                    }
                }
            }

            if (rowToEdit != -1) {
                Row row = sheet.getRow(rowToEdit);
                row.getCell(NAME_COLUMN).setCellValue(student.getName());
                row.getCell(ADDRESS_COLUMN).setCellValue(student.getAddress());
                row.getCell(TELEPHONE_COLUMN).setCellValue(student.getTelephone());
                row.getCell(EMAIL_COLUMN).setCellValue(student.getEmail());
                row.getCell(ACADEMIC_LEVEL_COLUMN).setCellValue(student.getAcademicLevel());
                row.getCell(CURRENT_SEMESTER_COLUMN).setCellValue(student.getCurrentSemester());
                row.getCell(PROFILE_PHOTO_COLUMN).setCellValue(student.getProfilePhoto());
                row.getCell(SUBJECTS_REGISTERED_COLUMN).setCellValue(student.getSubjectsRegistered());
                row.getCell(THESIS_TITLE_COLUMN).setCellValue(student.getThesisTitle());
                row.getCell(PROGRESS_COLUMN).setCellValue(student.getProgress());

                // Optionally update password if provided
                if (student.getPassword() != null && !student.getPassword().isEmpty()) {
                    row.getCell(PASSWORD_COLUMN).setCellValue(student.getPassword());
                }

                fos = new FileOutputStream(FILE_PATH);
                workbook.write(fos);
                LOGGER.info("Student " + student.getName() + " updated in Excel.");
            } else {
                LOGGER.warning("Student " + student.getStudentId() + " not found in Excel for editing.");
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error editing student in Excel: " + e.getMessage(), e);
        } finally {
            closeResources(fis, fos, workbook);
        }
    }




    public static Map<String, String> loadSubjects() {
        Map<String, String> subjects = new HashMap<>();
        File excelFile = new File(FILE_PATH);

        try (FileInputStream fis = new FileInputStream(excelFile);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet subjectSheet = workbook.getSheet(SUBJECT_SHEET_NAME);
            if (subjectSheet == null) {
                LOGGER.warning("Sheet '" + SUBJECT_SHEET_NAME + "' not found in Excel file.");
                return subjects;
            }

            Iterator<Row> rowIterator = subjectSheet.rowIterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                if (HAS_SUBJECT_HEADER_ROW && row.getRowNum() == 0) continue;

                try {
                    Cell subjectCodeCell = row.getCell(SUBJECT_CODE_COLUMN);
                    String subjectCode = (subjectCodeCell != null) ? getCellValueAsString(subjectCodeCell) : null;

                    Cell subjectNameCell = row.getCell(SUBJECT_NAME_COLUMN);
                    String subjectName = (subjectNameCell != null) ? getCellValueAsString(subjectNameCell) : null;

                    if (subjectCode == null || subjectCode.isEmpty()) {
                        LOGGER.warning("Skipping row " + row.getRowNum() + " due to missing subject code.");
                        continue;
                    }
                    if (subjectName == null || subjectName.isEmpty()) {
                        LOGGER.warning("Skipping row " + row.getRowNum() + " due to missing subject name.");
                        continue;
                    }

                    subjects.put(subjectCode, subjectName);

                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error processing subject row " + row.getRowNum() + ": " + e.getMessage(), e);
                }
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading Excel file for subjects: " + e.getMessage(), e);
        }
        return subjects;
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        try {
            DataFormatter formatter = new DataFormatter();
            return formatter.formatCellValue(cell).trim();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error getting cell value: " + e.getMessage(), e);
            return null;
        }
    }

    private static void closeResources(FileInputStream fis, FileOutputStream fos, Workbook workbook) {
        try {
            if (fis != null) fis.close();
            if (fos != null) fos.close();
            if (workbook != null) workbook.close();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error closing resources: " + e.getMessage(), e);
        }
    }

    public static void addSubjectToExcel(Subject subject, String filePath, String sheetName) {
        File excelFile = new File(filePath);
        Workbook workbook = null;
        Sheet sheet = null;
        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            fis = new FileInputStream(excelFile);
            workbook = new XSSFWorkbook(fis);
            sheet = workbook.getSheet(sheetName);

            if (sheet == null) {
                sheet = workbook.createSheet(sheetName);
                Row headerRow = sheet.createRow(0);
                headerRow.createCell(SUBJECT_CODE_COLUMN).setCellValue("Subject Code");
                headerRow.createCell(SUBJECT_NAME_COLUMN).setCellValue("Subject Name");
            }

            int nextRow = sheet.getLastRowNum() + 1;
            Row row = sheet.createRow(nextRow);
            row.createCell(SUBJECT_CODE_COLUMN).setCellValue(subject.getCode());
            row.createCell(SUBJECT_NAME_COLUMN).setCellValue(subject.getName());

            fos = new FileOutputStream(excelFile);
            workbook.write(fos);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error adding subject to Excel: " + e.getMessage(), e);
        } finally {
            closeResources(fis, fos, workbook);
        }
    }


    public static void deleteSubjectFromExcel(Subject subject, String filePath, String sheetName) {
        File excelFile = new File(filePath);
        Workbook workbook = null;
        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            fis = new FileInputStream(excelFile);
            workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheet(sheetName);

            if (sheet == null) {
                LOGGER.warning("Sheet " + sheetName + " not found in Excel file.");
                return;
            }

            int rowToDelete = -1;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    String code = getCellValueAsString(row.getCell(SUBJECT_CODE_COLUMN));
                    String name = getCellValueAsString(row.getCell(SUBJECT_NAME_COLUMN));

                    if (subject.getCode().equals(code) && subject.getName().equals(name)) {
                        rowToDelete = i;
                        break;
                    }
                }
            }

            if (rowToDelete != -1) {
                // Delete the row and shift the rows up
                int lastRowNum = sheet.getLastRowNum();
                sheet.removeRow(sheet.getRow(rowToDelete));
                if (rowToDelete <= lastRowNum) {
                    sheet.shiftRows(rowToDelete + 1, lastRowNum + 1, -1);
                }
                // Ensure that the last row is actually removed
                if (lastRowNum >= 0) {
                    Row lastRow = sheet.getRow(lastRowNum);
                    if (lastRow != null) {
                        // Clear all cells in the last row
                        for (int i = 0; i < lastRow.getLastCellNum(); i++) {
                            Cell cell = lastRow.getCell(i);
                            if (cell != null) {
                                lastRow.removeCell(cell);
                            }
                        }
                    }
                }

                // Write changes to file
                fos = new FileOutputStream(excelFile);
                workbook.write(fos);
                LOGGER.info("Subject " + subject.getName() + " deleted from Excel.");
            } else {
                LOGGER.warning("Subject " + subject.getName() + " not found in Excel for deletion.");
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error deleting subject from Excel: " + e.getMessage(), e);
        } finally {
            closeResources(fis, fos, workbook);
        }
    }
    public static void editSubjectInExcel(Subject oldSubject, Subject newSubject, String filePath, String sheetName) {
        File excelFile = new File(filePath);
        Workbook workbook = null;
        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            fis = new FileInputStream(excelFile);
            workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheet(sheetName);

            if (sheet == null) {
                LOGGER.warning("Sheet " + sheetName + " not found in Excel file.");
                return;
            }

            int rowToEdit = -1;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    String code = getCellValueAsString(row.getCell(SUBJECT_CODE_COLUMN));
                    String name = getCellValueAsString(row.getCell(SUBJECT_NAME_COLUMN));

                    if (oldSubject.getCode().equals(code) && oldSubject.getName().equals(name)) {
                        rowToEdit = i;
                        break;
                    }
                }
            }

            if (rowToEdit != -1) {
                Row row = sheet.getRow(rowToEdit);
                row.getCell(SUBJECT_CODE_COLUMN).setCellValue(newSubject.getCode());
                row.getCell(SUBJECT_NAME_COLUMN).setCellValue(newSubject.getName());

                fos = new FileOutputStream(excelFile);
                workbook.write(fos);
                LOGGER.info("Subject " + oldSubject.getName() + " updated to " + newSubject.getName() + " in Excel.");
            } else {
                LOGGER.warning("Subject " + oldSubject.getName() + " not found in Excel for editing.");
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error editing subject in Excel: " + e.getMessage(), e);
        } finally {
            closeResources(fis, fos, workbook);
        }
    }
}