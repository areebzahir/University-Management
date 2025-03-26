package com.FinalProject.UMS;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


public class StudentExcelManager {
    private static final String FILE_PATH = "src/main/resources/UMS_Data.xlsx";
    private static final String STUDENT_SHEET_NAME = "Students";
    private static final Logger LOGGER = Logger.getLogger(StudentExcelManager.class.getName());

    // Column indices
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
    // Add this setter method if missing

    // Make sure you have this getter too

    public static void initializeStudentSheet() {
        File excelFile = new File(FILE_PATH);
        try {
            Workbook workbook;
            if (excelFile.exists()) {
                try (FileInputStream fis = new FileInputStream(excelFile)) {
                    workbook = new XSSFWorkbook(fis);
                }
            } else {
                workbook = new XSSFWorkbook();
            }

            if (workbook.getSheet(STUDENT_SHEET_NAME) == null) {
                Sheet sheet = workbook.createSheet(STUDENT_SHEET_NAME);
                createStudentHeaderRow(sheet);

                try (FileOutputStream fileOut = new FileOutputStream(excelFile)) {
                    workbook.write(fileOut);
                    LOGGER.info("Created new student sheet");
                }
            }
            workbook.close();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error initializing student sheet", e);
        }
    }

    public static List<Student> loadAllStudents() {
        List<Student> students = new ArrayList<>();
        File excelFile = new File(FILE_PATH);

        if (!excelFile.exists()) {
            LOGGER.log(Level.SEVERE, "Excel file not found");
            return students;
        }

        try (FileInputStream fis = new FileInputStream(excelFile);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(STUDENT_SHEET_NAME);
            if (sheet == null) {
                LOGGER.warning("Student sheet not found");
                return students;
            }

            Iterator<Row> rowIterator = sheet.rowIterator();
            if (HAS_HEADER_ROW && rowIterator.hasNext()) {
                rowIterator.next();
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                try {
                    Student student = createStudentFromRow(row);
                    if (student != null) {
                        students.add(student);
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Error processing row", e);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading students", e);
        }
        return students;
    }

    public static Student getStudentById(String studentId) {
        File excelFile = new File(FILE_PATH);

        try (FileInputStream fis = new FileInputStream(excelFile);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(STUDENT_SHEET_NAME);
            if (sheet == null) return null;

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                String currentId = getCellStringValue(row.getCell(ID_COLUMN));
                if (studentId.equals(currentId)) {
                    return createStudentFromRow(row);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error finding student", e);
        }
        return null;
    }

    public static boolean addStudent(Student student) {
        File excelFile = new File(FILE_PATH);
        File tempFile = null;

        try {
            tempFile = createTempBackup(excelFile);

            try (FileInputStream fis = new FileInputStream(excelFile);
                 Workbook workbook = new XSSFWorkbook(fis)) {

                Sheet sheet = workbook.getSheet(STUDENT_SHEET_NAME);
                if (sheet == null) {
                    sheet = workbook.createSheet(STUDENT_SHEET_NAME);
                    createStudentHeaderRow(sheet);
                }

                int newRowNum = sheet.getLastRowNum() + 1;
                Row row = sheet.createRow(newRowNum);
                populateStudentRow(row, student);

                File tempOutput = File.createTempFile("UMS_temp", ".xlsx");
                try (FileOutputStream fos = new FileOutputStream(tempOutput)) {
                    workbook.write(fos);
                }

                Files.move(tempOutput.toPath(), excelFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
                return true;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error adding student", e);
            try {
                if (tempFile != null && tempFile.exists()) {
                    Files.copy(tempFile.toPath(), excelFile.toPath(),
                            StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Failed to restore backup", ex);
            }
            return false;
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    public static boolean updateStudent(Student student) {
        File excelFile = new File(FILE_PATH);
        File tempFile = null;

        try {
            tempFile = createTempBackup(excelFile);

            try (FileInputStream fis = new FileInputStream(excelFile);
                 Workbook workbook = new XSSFWorkbook(fis)) {

                Sheet sheet = workbook.getSheet(STUDENT_SHEET_NAME);
                if (sheet == null) return false;

                for (Row row : sheet) {
                    if (row.getRowNum() == 0) continue;

                    String currentId = getCellStringValue(row.getCell(ID_COLUMN));
                    if (student.getStudentId().equals(currentId)) {
                        populateStudentRow(row, student);
                        break;
                    }
                }

                File tempOutput = File.createTempFile("UMS_temp", ".xlsx");
                try (FileOutputStream fos = new FileOutputStream(tempOutput)) {
                    workbook.write(fos);
                }

                Files.move(tempOutput.toPath(), excelFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
                return true;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error updating student", e);
            try {
                if (tempFile != null && tempFile.exists()) {
                    Files.copy(tempFile.toPath(), excelFile.toPath(),
                            StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Failed to restore backup", ex);
            }
            return false;
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    public static boolean deleteStudent(String studentId) {
        File excelFile = new File(FILE_PATH);
        File tempFile = null;

        try {
            tempFile = createTempBackup(excelFile);

            try (FileInputStream fis = new FileInputStream(excelFile);
                 Workbook workbook = new XSSFWorkbook(fis)) {

                Sheet sheet = workbook.getSheet(STUDENT_SHEET_NAME);
                if (sheet == null) return false;

                int rowToDelete = -1;
                for (Row row : sheet) {
                    if (row.getRowNum() == 0) continue;

                    String currentId = getCellStringValue(row.getCell(ID_COLUMN));
                    if (studentId.equals(currentId)) {
                        rowToDelete = row.getRowNum();
                        break;
                    }
                }

                if (rowToDelete != -1) {
                    sheet.removeRow(sheet.getRow(rowToDelete));
                    if (rowToDelete < sheet.getLastRowNum()) {
                        sheet.shiftRows(rowToDelete + 1, sheet.getLastRowNum(), -1);
                    }

                    File tempOutput = File.createTempFile("UMS_temp", ".xlsx");
                    try (FileOutputStream fos = new FileOutputStream(tempOutput)) {
                        workbook.write(fos);
                    }

                    Files.move(tempOutput.toPath(), excelFile.toPath(),
                            StandardCopyOption.REPLACE_EXISTING);
                    return true;
                }
                return false;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error deleting student", e);
            try {
                if (tempFile != null && tempFile.exists()) {
                    Files.copy(tempFile.toPath(), excelFile.toPath(),
                            StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Failed to restore backup", ex);
            }
            return false;
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    public static List<String> getEnrolledCourses(String studentId) {
        List<String> courses = new ArrayList<>();
        Student student = getStudentById(studentId);
        if (student != null && student.getSubjectsRegistered() != null) {
            String[] subjects = student.getSubjectsRegistered().split(",");
            for (String subject : subjects) {
                String trimmed = subject.trim();
                if (!trimmed.isEmpty()) {
                    courses.add(trimmed);
                }
            }
        }
        return courses;
    }

    public static boolean addCourseToStudent(String studentId, String courseCode) {
        Student student = getStudentById(studentId);
        if (student == null) {
            LOGGER.warning("Student not found: " + studentId);
            return false;
        }

        // Get current subjects or initialize empty string if null
        String currentSubjects = student.getSubjectsRegistered() != null
                ? student.getSubjectsRegistered()
                : "";

        // Add new course (with comma separator if needed)
        String newSubjects = currentSubjects.isEmpty()
                ? courseCode
                : currentSubjects + "," + courseCode;


        // Save to Excel
        return updateStudent(student);
    }

    // Helper methods
    private static Student createStudentFromRow(Row row) {
        if (row == null) return null;

        return new Student(
                getCellStringValue(row.getCell(ID_COLUMN)),
                getCellStringValue(row.getCell(NAME_COLUMN)),
                getCellStringValue(row.getCell(ADDRESS_COLUMN)),
                getCellStringValue(row.getCell(TELEPHONE_COLUMN)),
                getCellStringValue(row.getCell(EMAIL_COLUMN)),
                getCellStringValue(row.getCell(ACADEMIC_LEVEL_COLUMN)),
                getCellStringValue(row.getCell(CURRENT_SEMESTER_COLUMN)),
                getCellStringValue(row.getCell(PROFILE_PHOTO_COLUMN)),
                getCellStringValue(row.getCell(SUBJECTS_REGISTERED_COLUMN)),
                getCellStringValue(row.getCell(THESIS_TITLE_COLUMN)),
                getCellStringValue(row.getCell(PROGRESS_COLUMN)),
                getCellStringValue(row.getCell(PASSWORD_COLUMN))
        );
    }

    private static void populateStudentRow(Row row, Student student) {
        if (row == null || student == null) return;

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
    }

    private static void createStudentHeaderRow(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        String[] headers = {
                "Student ID", "Name", "Address", "Telephone", "Email",
                "Academic Level", "Current Semester", "Profile Photo",
                "Subjects Registered", "Thesis Title", "Progress", "Password"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);

            CellStyle headerStyle = sheet.getWorkbook().createCellStyle();
            Font font = sheet.getWorkbook().createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            cell.setCellStyle(headerStyle);
        }
    }

    private static String getCellStringValue(Cell cell) {
        if (cell == null) return "";
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell).trim();
    }

    private static File createTempBackup(File original) throws IOException {
        File backup = File.createTempFile("UMS_backup", ".xlsx");
        Files.copy(original.toPath(), backup.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return backup;
    }
}