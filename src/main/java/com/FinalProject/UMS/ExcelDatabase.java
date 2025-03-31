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

    private static final boolean HAS_HEADER_ROW = true;

    // Subject Sheet Column Definitions
    private static final int SUBJECT_CODE_COLUMN = 0;
    private static final int SUBJECT_NAME_COLUMN = 1;
    private static final boolean HAS_SUBJECT_HEADER_ROW = true;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );

    private static final Logger LOGGER = Logger.getLogger(ExcelDatabase.class.getName());

    public static List<User> loadUsersAsList() {
        Map<String, User> userMap = ExcelDatabase.loadUsers();

        return (List)userMap.values();
    }

    public static void saveUser(User user) {

        File excelFile = new File(FILE_PATH);
        try (FileInputStream fis = new FileInputStream(excelFile); Workbook workbook = new XSSFWorkbook(fis)) {

            Iterator<Sheet> sheetIterator = workbook.sheetIterator();
            while (sheetIterator.hasNext()) {
                Sheet sheet = sheetIterator.next();
                String sheetName = sheet.getSheetName();

                if (!sheetName.trim().equals(STUDENT_SHEET_NAME)) {
                    continue;
                }

                LOGGER.log(Level.INFO, "Processing sheet: {0}", sheetName);

                Iterator<Row> rowIterator = sheet.rowIterator();
                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();

                    if (HAS_HEADER_ROW && row.getRowNum() == 0) continue;

                    Cell idCell = row.getCell(ID_COLUMN);
                    String id = (idCell != null) ? getCellValueAsString(idCell) : null;

                    if (id == null) continue;
                    if (!id.equals(user.getId())) continue;

                    try {

                        System.out.println("entered " + user.getTelephone());
                        System.out.println("entered " + row.getCell(TELEPHONE_COLUMN).getStringCellValue());

                        row.getCell(NAME_COLUMN).setCellValue(user.getName());
                        row.getCell(ADDRESS_COLUMN).setCellValue(user.getAddress());
                        row.getCell(TELEPHONE_COLUMN).setCellValue(user.getTelephone());
                        row.getCell(EMAIL_COLUMN).setCellValue(user.getEmail());
                        row.getCell(ACADEMIC_LEVEL_COLUMN).setCellValue(user.getAcademicLevel());
                        row.getCell(CURRENT_SEMESTER_COLUMN).setCellValue(user.getCurrentSemester());
                        row.getCell(PROFILE_PHOTO_COLUMN).setCellValue(user.getProfilePhoto());
                        row.getCell(SUBJECTS_REGISTERED_COLUMN).setCellValue(user.getSubjectsRegistered());
                        row.getCell(THESIS_TITLE_COLUMN).setCellValue(user.getThesisTitle());
                        row.getCell(PROGRESS_COLUMN).setCellValue(user.getProgress());
                        row.getCell(PASSWORD_COLUMN).setCellValue(user.getPassword());

                        try (FileOutputStream fos = new FileOutputStream(FILE_PATH)) {
                            workbook.write(fos);
                            System.out.println("Excel file updated successfully!");
                        }

                        break;

                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Error processing row " + row.getRowNum() + ": " + e.getMessage(), e);
                    }

                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading Excel file: " + e.getMessage(), e);
        }
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
                            String subjectsRegistered = (subjectsRegisteredCell != null) ? getCellValueAsString(subjectsRegisteredCell) : null;

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

                            User user = new User(id, email, password); // Updated constructor

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
        // This method will handle both adding new students and updating existing ones
        Map<String, User> users = loadUsers();
        boolean studentExists = false;

        // Check if student already exists
        for (User user : users.values()) {
            if (user.getId() != null && user.getId().equals(student.getStudentId())) {
                studentExists = true;
                break;
            }
        }

        if (studentExists) {
            // Update existing student
            User userToUpdate = new User(
                    student.getStudentId(),
                    student.getEmail(),
                    student.getPassword()
            );

            // Copy all student properties to the user
            userToUpdate.setName(student.getName());
            userToUpdate.setAddress(student.getAddress());
            userToUpdate.setTelephone(student.getTelephone());
            userToUpdate.setAcademicLevel(student.getAcademicLevel());
            userToUpdate.setCurrentSemester(student.getCurrentSemester());
            userToUpdate.setProfilePhoto(student.getProfilePhoto());
            userToUpdate.setSubjectsRegistered(student.getSubjectsRegistered());
            userToUpdate.setThesisTitle(student.getThesisTitle());
            userToUpdate.setProgress(student.getProgress());

            // Save the updated user
            saveUser(userToUpdate);
            LOGGER.info("Student " + student.getName() + " updated in Excel database.");
        } else {
            // Add new student
            addStudentToExcel(student, FILE_PATH, STUDENT_SHEET_NAME);
            LOGGER.info("New student " + student.getName() + " added to Excel database.");
        }
    }

    // Improve the deleteStudentFromExcel method for better integration
    public static boolean deleteStudent(String studentId) {
        Map<String, User> users = loadUsers();
        Student studentToDelete = null;

        // Find the student to delete
        for (User user : users.values()) {
            if (user.getId() != null && user.getId().equals(studentId)) {
                // Create a Student object from User
                studentToDelete = new Student(
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
                break;
            }
        }

        if (studentToDelete != null) {
            deleteStudentFromExcel(studentToDelete, FILE_PATH, STUDENT_SHEET_NAME);
            LOGGER.info("Student " + studentToDelete.getName() + " deleted from Excel database.");
            return true;
        } else {
            LOGGER.warning("Student with ID " + studentId + " not found for deletion.");
            return false;
        }
    }

    public static void addStudentToExcel(Student student, String filePath, String sheetName) {
        File excelFile = new File(filePath);
        Workbook workbook = null;
        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            fis = new FileInputStream(excelFile);
            workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheet(sheetName);

            if (sheet == null) {
                sheet = workbook.createSheet(sheetName);
                // Create header row if needed
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

            int newRowNum = sheet.getLastRowNum() + 1;
            Row row = sheet.createRow(newRowNum);

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

            fos = new FileOutputStream(excelFile);
            workbook.write(fos);
            LOGGER.info("Student " + student.getName() + " added to Excel.");

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error adding student to Excel: " + e.getMessage(), e);
        } finally {
            closeResources(fis, fos, workbook);
        }
    }

    public static void editStudentInExcel(Student student, String filePath, String sheetName) {
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

            // Find the row with matching student ID
            int rowToEdit = -1;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    String id = getCellValueAsString(row.getCell(ID_COLUMN));
                    if (student.getStudentId().equals(id)) {
                        rowToEdit = i;
                        break;
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
                row.getCell(PASSWORD_COLUMN).setCellValue(student.getPassword());

                fos = new FileOutputStream(excelFile);
                workbook.write(fos);
                LOGGER.info("Student " + student.getName() + " updated in Excel.");
            } else {
                LOGGER.warning("Student with ID " + student.getStudentId() + " not found in Excel for editing.");
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
                sheet.removeRow(sheet.getRow(rowToDelete));
                if (rowToDelete < sheet.getLastRowNum()) {
                    sheet.shiftRows(rowToDelete + 1, sheet.getLastRowNum(), -1);
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
    public static void deleteStudentFromExcel(Student student, String filePath, String sheetName) {
        File excelFile = new File(filePath);

        try (FileInputStream fis = new FileInputStream(excelFile);
             Workbook workbook = new XSSFWorkbook(fis);
             FileOutputStream fos = new FileOutputStream(excelFile)) {

            Sheet sheet = workbook.getSheet(sheetName);

            if (sheet == null) {
                LOGGER.warning("Sheet " + sheetName + " not found.");
                return;
            }

            // Find the row with matching student ID
            int rowToDelete = -1;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    String id = getCellValueAsString(row.getCell(ID_COLUMN));
                    if (student.getStudentId().equals(id)) {
                        rowToDelete = i;
                        break;
                    }
                }
            }

            if (rowToDelete != -1) {
                sheet.removeRow(sheet.getRow(rowToDelete));

                // Shift remaining rows up
                if (rowToDelete < sheet.getLastRowNum()) {
                    sheet.shiftRows(rowToDelete + 1, sheet.getLastRowNum(), -1);
                }

                workbook.write(fos);
                LOGGER.info("Student " + student.getName() + " deleted from Excel.");
            } else {
                LOGGER.warning("Student with ID " + student.getStudentId() + " not found.");
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error deleting student: " + e.getMessage(), e);
            throw new RuntimeException("Failed to delete student from Excel", e);
        }
    }
    public static void editSubjectInExcel(Subject oldSubject, Subject newSubject, String filePath, String sheetName) {
        File excelFile = new File(filePath);

        System.out.println("File exists: " + excelFile.exists());


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