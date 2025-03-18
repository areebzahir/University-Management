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
        if (cell == null) return null;
        try {
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue();
                case NUMERIC:
                    return DateUtil.isCellDateFormatted(cell) ? cell.getDateCellValue().toString() : String.valueOf(cell.getNumericCellValue());
                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                case FORMULA:
                    return cell.getCellFormula();
                default:
                    return "";
            }
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
}
