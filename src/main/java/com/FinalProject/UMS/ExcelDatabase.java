package com.FinalProject.UMS;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExcelDatabase {
    private static final String FILE_PATH = "src/main/resources/UMS_Data.xlsx";
    private static final String STUDENT_SHEET_NAME = "Students";
    private static final String SUBJECT_SHEET_NAME = "Subjects"; // Added subject sheet name
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

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }
        try {
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue();
                case NUMERIC:
                    DateUtil DateUtil = null;
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return cell.getDateCellValue().toString();
                    } else {
                        return String.valueOf(cell.getNumericCellValue());
                    }
                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                case FORMULA:
                    return cell.getCellFormula(); // Or evaluate the formula
                case BLANK:
                    return null;
                default:
                    return "";
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error getting cell value: " + e.getMessage(), e);
            return null;
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