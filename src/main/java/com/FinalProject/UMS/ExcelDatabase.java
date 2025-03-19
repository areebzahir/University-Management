package com.FinalProject.UMS;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class ExcelDatabase {
    private static final String FILE_PATH = "src/main/resources/UMS_Data.xlsx";
    private static final String STUDENT_SHEET_NAME = "Students";
    private static final String SUBJECT_SHEET_NAME = "Subjects";
    private static final String TUITION_SHEET_NAME = "Tuition"; //Sheet name for tuition
    private static final int ID_COLUMN = 0;
    private static final int NAME_COLUMN = 1;
    private static final int ADDRESS_COLUMN = 2;
    private static final int TELEPHONE_COLUMN = 3;
    private static final int EMAIL_COLUMN = 4;
    private static final int ACADEMIC_LEVEL_COLUMN = 5;
    private static final int CURRENT_SEMESTER_COLUMN = 6;
    private static final int PROFILE_PHOTO_COLUMN = 7;
    private static final int SUBJECT_REGISTERED_COLUMN = 8;
    private static final int THESIS_TITLE_COLUMN = 9;
    private static final int PROGRESS_COLUMN = 10;
    private static final int PASSWORD_COLUMN = 11;
    private static final boolean HAS_HEADER_ROW = true;

    // Tuition Sheet Column Definitions
    private static final int TUITION_SEMESTER_COLUMN = 0;
    private static final int TUITION_AMOUNT_DUE_COLUMN = 1;
    private static final int TUITION_AMOUNT_PAID_COLUMN = 2;
    private static final int TUITION_STATUS_COLUMN = 3;
    private static final int TUITION_STUDENT_ID_COLUMN = 4; //Column for student ID in Tuition sheet
    private static final boolean HAS_TUITION_HEADER_ROW = true;

    // Subject Sheet Column Definitions
    private static final int SUBJECT_CODE_COLUMN = 0;
    private static final int SUBJECT_NAME_COLUMN = 1;
    private static final boolean HAS_SUBJECT_HEADER_ROW = true; //Header row for subject

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );

    private static final Logger LOGGER = Logger.getLogger(ExcelDatabase.class.getName());

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

                // Check if this is the "Students" sheet
                if (sheetName.trim().equals(STUDENT_SHEET_NAME)) {
                    // Process the "Students" sheet
                    Iterator<Row> rowIterator = sheet.rowIterator();
                    while (rowIterator.hasNext()) {
                        Row row = rowIterator.next();

                        if (HAS_HEADER_ROW && row.getRowNum() == 0) continue; // Skip header

                        try {
                            Cell idCell = row.getCell(ID_COLUMN);
                            String id = (idCell != null) ? getCellValueAsString(idCell) : null;

                            Cell emailCell = row.getCell(EMAIL_COLUMN);
                            String email = (emailCell != null) ? getCellValueAsString(emailCell) : null;

                            Cell passwordCell = row.getCell(PASSWORD_COLUMN);
                            String password = (passwordCell != null) ? getCellValueAsString(passwordCell) : null;

                            LOGGER.info(id + " " + email + " " + password);

                            // Basic validation
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

                            LOGGER.log(Level.FINE, "Loaded user: id={0}, email={1}, password={2}", new Object[]{id, email, password});

                            User user = new User(id, email, password); // Store both ID and email
                            if (id != null && !id.isEmpty())
                                users.put(id, user); // Store the user by ID
                            if (email != null && !email.isEmpty())
                                users.put(email, user); // Store the user by email
                        } catch (Exception e) {
                            LOGGER.log(Level.SEVERE, "Error processing row " + row.getRowNum() + ": " + e.getMessage(), e);
                        }
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading Excel file: " + e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading the file object: " + e.getMessage(), e);
        }
        return users;
    }

    // Method to load student data from the Excel file
    public static List<Student> loadStudents() {
        List<Student> students = new ArrayList<>();
        File excelFile = new File(FILE_PATH);
        try (FileInputStream fis = new FileInputStream(excelFile);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(STUDENT_SHEET_NAME);
            if (sheet == null) {
                LOGGER.warning("Sheet '" + STUDENT_SHEET_NAME + "' not found in Excel file.");
                return students; // Return an empty list if the sheet is not found
            }

            Iterator<Row> rowIterator = sheet.rowIterator();
            if (HAS_HEADER_ROW && rowIterator.hasNext()) {
                rowIterator.next(); // Skip header row
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                try {
                    String studentId = getCellValueAsString(row.getCell(ID_COLUMN));
                    String name = getCellValueAsString(row.getCell(NAME_COLUMN));
                    String address = getCellValueAsString(row.getCell(ADDRESS_COLUMN));
                    String telephone = getCellValueAsString(row.getCell(TELEPHONE_COLUMN));
                    String email = getCellValueAsString(row.getCell(EMAIL_COLUMN));
                    String academicLevel = getCellValueAsString(row.getCell(ACADEMIC_LEVEL_COLUMN));
                    String currentSemester = getCellValueAsString(row.getCell(CURRENT_SEMESTER_COLUMN));
                    String profilePhoto = getCellValueAsString(row.getCell(PROFILE_PHOTO_COLUMN));
                    String subjectsRegistered = getCellValueAsString(row.getCell(SUBJECT_REGISTERED_COLUMN));
                    String thesisTitle = getCellValueAsString(row.getCell(THESIS_TITLE_COLUMN));


                    // Handle the progress value
                    String progressStr = getCellValueAsString(row.getCell(PROGRESS_COLUMN));
                    double progress = 0.0; // Default value
                    if (progressStr != null && !progressStr.isEmpty()) {
                        try {
                            // Remove any non-numeric characters except the decimal point and percentage sign
                            progressStr = progressStr.replaceAll("[^\\d.%]", "");
                            // Remove the percentage sign if it exists
                            progressStr = progressStr.replace("%", "");
                            progress = Double.parseDouble(progressStr) / 100.0; // Convert to decimal
                        } catch (NumberFormatException e) {
                            LOGGER.log(Level.WARNING, "Invalid progress format: " + progressStr + ". Setting progress to 0.0");
                        }
                    }

                    String password = getCellValueAsString(row.getCell(PASSWORD_COLUMN));

                    // Split the name into first and last name
                    String[] names = name.split(" ", 2);
                    String firstName = names.length > 0 ? names[0] : "";
                    String lastName = names.length > 1 ? names[1] : "";

                    Student student = new Student(studentId, firstName, lastName, address, telephone, email,
                            academicLevel, currentSemester, profilePhoto, subjectsRegistered,
                            thesisTitle, progress, password);
                    students.add(student);

                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Error processing row: " + row.getRowNum() + " - " + e.getMessage(), e);
                }
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading Excel file: " + e.getMessage(), e);
        }
        return students;
    }
    public static List<Tuition> loadTuitionRecords(String studentId) {
        List<Tuition> tuitionRecords = new ArrayList<>();
        File excelFile = new File(FILE_PATH);

        try (FileInputStream fis = new FileInputStream(excelFile);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(TUITION_SHEET_NAME);
            if (sheet == null) {
                LOGGER.warning("Sheet " + TUITION_SHEET_NAME + " not found in Excel file.");
                return tuitionRecords;
            }

            Iterator<Row> rowIterator = sheet.rowIterator();

            // Skip header row if it exists
            if (HAS_TUITION_HEADER_ROW && rowIterator.hasNext()) {
                rowIterator.next();
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                try {
                    String semester = getCellValueAsString(row.getCell(TUITION_SEMESTER_COLUMN));
                    String amountDue = getCellValueAsString(row.getCell(TUITION_AMOUNT_DUE_COLUMN));
                    String amountPaid = getCellValueAsString(row.getCell(TUITION_AMOUNT_PAID_COLUMN));
                    String status = getCellValueAsString(row.getCell(TUITION_STATUS_COLUMN));
                    String tuitionStudentId = getCellValueAsString(row.getCell(TUITION_STUDENT_ID_COLUMN));

                    // Only add tuition records that match the given student ID
                    if (studentId.equals(tuitionStudentId)) {
                        Tuition tuition = new Tuition(semester, amountDue, amountPaid, status);
                        tuitionRecords.add(tuition);
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Error processing tuition row: " + row.getRowNum() + " - " + e.getMessage(), e);
                }
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading Excel file: " + e.getMessage(), e);
        }

        return tuitionRecords;
    }

    // Method to load a student by their ID
    public static Student loadStudentById(String studentId) {
        File excelFile = new File(FILE_PATH);
        try (FileInputStream fis = new FileInputStream(excelFile);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(STUDENT_SHEET_NAME);
            if (sheet == null) {
                LOGGER.warning("Sheet '" + STUDENT_SHEET_NAME + "' not found in Excel file.");
                return null;
            }

            Iterator<Row> rowIterator = sheet.rowIterator();
            if (HAS_HEADER_ROW && rowIterator.hasNext()) {
                rowIterator.next(); // Skip header row
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                try {
                    String currentStudentId = getCellValueAsString(row.getCell(ID_COLUMN));
                    if (studentId.equals(currentStudentId)) {
                        String name = getCellValueAsString(row.getCell(NAME_COLUMN));
                        String address = getCellValueAsString(row.getCell(ADDRESS_COLUMN));
                        String telephone = getCellValueAsString(row.getCell(TELEPHONE_COLUMN));
                        String email = getCellValueAsString(row.getCell(EMAIL_COLUMN));
                        String academicLevel = getCellValueAsString(row.getCell(ACADEMIC_LEVEL_COLUMN));
                        String currentSemester = getCellValueAsString(row.getCell(CURRENT_SEMESTER_COLUMN));
                        String profilePhoto = getCellValueAsString(row.getCell(PROFILE_PHOTO_COLUMN));
                        String subjectsRegistered = getCellValueAsString(row.getCell(SUBJECT_REGISTERED_COLUMN));
                        String thesisTitle = getCellValueAsString(row.getCell(THESIS_TITLE_COLUMN));

                        // Handle the progress value
                        String progressStr = getCellValueAsString(row.getCell(PROGRESS_COLUMN));
                        double progress = 0.0; // Default value
                        if (progressStr != null && !progressStr.isEmpty()) {
                            try {
                                // Remove any non-numeric characters except the decimal point and percentage sign
                                progressStr = progressStr.replaceAll("[^\\d.%]", "");
                                // Remove the percentage sign if it exists
                                progressStr = progressStr.replace("%", "");
                                progress = Double.parseDouble(progressStr) / 100.0; // Convert to decimal
                            } catch (NumberFormatException e) {
                                LOGGER.log(Level.WARNING, "Invalid progress format: " + progressStr + ". Setting progress to 0.0");
                            }
                        }

                        String password = getCellValueAsString(row.getCell(PASSWORD_COLUMN));

                        // Split the name into first and last name
                        String[] names = name.split(" ", 2);
                        String firstName = names.length > 0 ? names[0] : "";
                        String lastName = names.length > 1 ? names[1] : "";

                        return new Student(studentId, firstName, lastName, address, telephone, email,
                                academicLevel, currentSemester, profilePhoto, subjectsRegistered,
                                thesisTitle, progress, password);
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Error processing row: " + row.getRowNum() + " - " + e.getMessage(), e);
                }
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading Excel file: " + e.getMessage(), e);
        }
        return null; // Student not found
    }

    public static List<String> loadEnrolledCourses(String studentId) {
        List<String> enrolledCourses = new ArrayList<>();
        File excelFile = new File(FILE_PATH);
        
        if (!excelFile.exists()) {
            LOGGER.severe("Excel file not found at path: " + FILE_PATH);
            return enrolledCourses;
        }

        try (FileInputStream fis = new FileInputStream(excelFile);
             Workbook workbook = new XSSFWorkbook(fis)) {

            LOGGER.info("Loading Excel file: " + FILE_PATH);
            Sheet sheet = workbook.getSheet(STUDENT_SHEET_NAME);
            if (sheet == null) {
                LOGGER.warning("Sheet '" + STUDENT_SHEET_NAME + "' not found in Excel file.");
                return enrolledCourses;
            }

            Iterator<Row> rowIterator = sheet.rowIterator();
            if (HAS_HEADER_ROW && rowIterator.hasNext()) {
                rowIterator.next(); // Skip header row
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                try {
                    String currentStudentId = getCellValueAsString(row.getCell(ID_COLUMN));
                    LOGGER.info("Processing Student ID: " + currentStudentId);
                    if (studentId.equals(currentStudentId)) {
                        String subjectsRegistered = getCellValueAsString(row.getCell(SUBJECT_REGISTERED_COLUMN));
                        LOGGER.info("Subjects Registered: " + subjectsRegistered);
                        if (subjectsRegistered != null && !subjectsRegistered.isEmpty()) {
                            String[] subjects = subjectsRegistered.split(",");
                            for (String subject : subjects) {
                                enrolledCourses.add(subject.trim());
                            }
                        }
                        return enrolledCourses; // Return the list once the student is found
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Error processing row: " + row.getRowNum() + " - " + e.getMessage(), e);
                }
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading Excel file: " + e.getMessage(), e);
        }
        return enrolledCourses; // Return an empty list if student not found
    }
    // Method to add a course to a student's enrolled courses in Excel
    public static void addCourseToStudent(String studentId, String newCourse) {
        File excelFile = new File(FILE_PATH);
        try (FileInputStream fis = new FileInputStream(excelFile);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(STUDENT_SHEET_NAME);
            if (sheet == null) {
                LOGGER.warning("Sheet '" + STUDENT_SHEET_NAME + "' not found in Excel file.");
                return;
            }

            Iterator<Row> rowIterator = sheet.rowIterator();
            if (HAS_HEADER_ROW && rowIterator.hasNext()) {
                rowIterator.next(); // Skip header row
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                try {
                    String currentStudentId = getCellValueAsString(row.getCell(ID_COLUMN));
                    if (studentId.equals(currentStudentId)) {
                        String subjectsRegistered = getCellValueAsString(row.getCell(SUBJECT_REGISTERED_COLUMN));
                        String updatedSubjectsRegistered;
                        if (subjectsRegistered == null || subjectsRegistered.isEmpty()) {
                            updatedSubjectsRegistered = newCourse;
                        } else {
                            updatedSubjectsRegistered = subjectsRegistered + ", " + newCourse;
                        }
                        row.getCell(SUBJECT_REGISTERED_COLUMN).setCellValue(updatedSubjectsRegistered);

                        // Write the workbook changes to the file
                        try (FileOutputStream fileOut = new FileOutputStream(excelFile)) {
                            workbook.write(fileOut);
                            LOGGER.info("Course '" + newCourse + "' added to student ID: " + studentId);
                        } catch (IOException e) {
                            LOGGER.log(Level.SEVERE, "Error writing to Excel file: " + e.getMessage(), e);
                        }
                        return; // Exit after updating the student
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Error processing row: " + row.getRowNum() + " - " + e.getMessage(), e);
                }
            }

            LOGGER.warning("Student with ID " + studentId + " not found in Excel file.");

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading Excel file: " + e.getMessage(), e);
        }
    }

    //Method to load subject
    public static Map<String, String> loadSubjects() {
        Map<String, String> subjects = new HashMap<>();
        File excelFile = new File(FILE_PATH);

        try (FileInputStream fis = new FileInputStream(excelFile);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet subjectSheet = workbook.getSheet(SUBJECT_SHEET_NAME);
            if (subjectSheet == null) {
                LOGGER.warning("Sheet '" + SUBJECT_SHEET_NAME + "' not found in Excel file.");
                return subjects; // Return empty map if sheet doesn't exist
            }

            Iterator<Row> rowIterator = subjectSheet.rowIterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                if (HAS_SUBJECT_HEADER_ROW && row.getRowNum() == 0) continue; // Skip header row

                try {
                    Cell subjectCodeCell = row.getCell(SUBJECT_CODE_COLUMN);
                    String subjectCode = (subjectCodeCell != null) ? getCellValueAsString(subjectCodeCell) : null;

                    Cell subjectNameCell = row.getCell(SUBJECT_NAME_COLUMN);
                    String subjectName = (subjectNameCell != null) ? getCellValueAsString(subjectNameCell) : null;

                    // Basic validation
                    if (subjectCode == null || subjectCode.isEmpty()) {
                        LOGGER.warning("Skipping row " + row.getRowNum() + " due to missing subject code.");
                        continue;
                    }
                    if (subjectName == null || subjectName.isEmpty()) {
                        LOGGER.warning("Skipping row " + row.getRowNum() + " due to missing subject name.");
                        continue;
                    }

                    subjects.put(subjectCode, subjectName);
                    LOGGER.log(Level.FINE, "Loaded subject: code={0}, name={1}", new Object[]{subjectCode, subjectName});

                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error processing subject row " + row.getRowNum() + ": " + e.getMessage(), e);
                }
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading Excel file for subjects: " + e.getMessage(), e);
        }
        return subjects;
    }

    // Method to add a subject to the Excel file
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
                // If the sheet is newly created, add headers
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

    // Delete subject from Excel
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

            // Find the row to delete
            int rowToDelete = -1;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Start from index 1 to skip header row
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
                // Remove the row
                sheet.removeRow(sheet.getRow(rowToDelete));

                // Shift rows up to fill the gap
                int lastRowNum = sheet.getLastRowNum();
                if (rowToDelete < lastRowNum) {
                    sheet.shiftRows(rowToDelete + 1, lastRowNum, -1);
                }

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

    //Edit Subject Method
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

            // Find the row to edit
            int rowToEdit = -1;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Start from index 1 to skip header row
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
                // Update the row with new subject information
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

    // Method to add the "Subjects" sheet if it doesn't exist.  Helpful for initial setup.
    public static void addSubjectsSheetIfMissing() {
        File excelFile = new File(FILE_PATH);
        try (FileInputStream fis = new FileInputStream(excelFile);
             Workbook workbook = new XSSFWorkbook(fis)) {

            if (workbook.getSheet(SUBJECT_SHEET_NAME) == null) {
                Sheet subjectSheet = workbook.createSheet(SUBJECT_SHEET_NAME);

                // Create header row
                Row headerRow = subjectSheet.createRow(0);
                headerRow.createCell(SUBJECT_CODE_COLUMN).setCellValue("Subject Code");
                headerRow.createCell(SUBJECT_NAME_COLUMN).setCellValue("Subject Name");

                // Write the changes back to the Excel file
                try (FileOutputStream fileOut = new FileOutputStream(excelFile)) {
                    workbook.write(fileOut);
                    LOGGER.info("Added 'Subjects' sheet to " + FILE_PATH);
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Error writing to Excel file: " + e.getMessage(), e);
                }
            } else {
                LOGGER.info("Sheet '" + SUBJECT_SHEET_NAME + "' already exists.");
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading Excel file: " + e.getMessage(), e);
        }
    }
    // Method to add the "Tuition" sheet if it doesn't exist.  Helpful for initial setup.
    public static void addTuitionSheetIfMissing() {
        File excelFile = new File(FILE_PATH);
        try (FileInputStream fis = new FileInputStream(excelFile);
             Workbook workbook = new XSSFWorkbook(fis)) {

            if (workbook.getSheet(TUITION_SHEET_NAME) == null) {
                Sheet subjectSheet = workbook.createSheet(TUITION_SHEET_NAME);

                // Create header row
                Row headerRow = subjectSheet.createRow(0);
                headerRow.createCell(TUITION_SEMESTER_COLUMN).setCellValue("Semester");
                headerRow.createCell(TUITION_AMOUNT_DUE_COLUMN).setCellValue("Amount Due");
                headerRow.createCell(TUITION_AMOUNT_PAID_COLUMN).setCellValue("Amount Paid");
                headerRow.createCell(TUITION_STATUS_COLUMN).setCellValue("Status");
                headerRow.createCell(TUITION_STUDENT_ID_COLUMN).setCellValue("Student ID");

                // Write the changes back to the Excel file
                try (FileOutputStream fileOut = new FileOutputStream(excelFile)) {
                    workbook.write(fileOut);
                    LOGGER.info("Added 'Tuition' sheet to " + FILE_PATH);
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Error writing to Excel file: " + e.getMessage(), e);
                }
            } else {
                LOGGER.info("Sheet '" + TUITION_SHEET_NAME + "' already exists.");
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading Excel file: " + e.getMessage(), e);
        }
    }
}