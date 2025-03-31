package com.FinalProject.UMS;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StudentDatabase {

    private static final String EXCEL_FILE_PATH = "UMS_Data.xlsx"; // Replace with your actual file path
    private static final String STUDENT_SHEET_NAME = "Students";
    private static final String COURSE_SHEET_NAME = "Courses";



    // Method to read student data from Excel
    public static List<Student> loadStudentsFromExcel() {
        List<Student> students = new ArrayList<>();
        try (FileInputStream fileInputStream = new FileInputStream(new File(EXCEL_FILE_PATH));
             Workbook workbook = new XSSFWorkbook(fileInputStream)) {

            Sheet sheet = workbook.getSheet(STUDENT_SHEET_NAME);
            if (sheet == null) {
                System.out.println("Sheet not found: " + STUDENT_SHEET_NAME);
                return students; // Return empty list
            }

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) { // Start from row 1 (skip header)
                Row row = sheet.getRow(rowIndex);
                if (row != null) {
                    try {
                        String studentId = getStringCellValue(row.getCell(0));
                        String name = getStringCellValue(row.getCell(1));
                        String address = getStringCellValue(row.getCell(2));
                        String telephone = getStringCellValue(row.getCell(3));
                        String email = getStringCellValue(row.getCell(4));
                        String academicLevel = getStringCellValue(row.getCell(5));
                        String currentSemester = getStringCellValue(row.getCell(6));
                        String profilePhoto = getStringCellValue(row.getCell(7));
                        String subjectsRegistered = getStringCellValue(row.getCell(8));
                        String thesisTitle = getStringCellValue(row.getCell(9));
                        String progress = getStringCellValue(row.getCell(10));
                        String password = getStringCellValue(row.getCell(11));

                        Student student = new Student(studentId, name, address, telephone, email, academicLevel, currentSemester, profilePhoto, subjectsRegistered, thesisTitle, progress, password);
                        students.add(student);
                    } catch (Exception e) {
                        System.err.println("Error reading row " + rowIndex + ": " + e.getMessage());
                        e.printStackTrace(); // Print the full stack trace for debugging
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Error loading students from Excel: " + e.getMessage());
            e.printStackTrace();
        }
        return students;
    }

    // Method to read course data from Excel
    public static List<CoursesController> loadCoursesFromExcel() {
        List<CoursesController> courses = new ArrayList<>();
        try (FileInputStream fileInputStream = new FileInputStream(new File(EXCEL_FILE_PATH));
             Workbook workbook = new XSSFWorkbook(fileInputStream)) {

            Sheet sheet = workbook.getSheet(COURSE_SHEET_NAME);
            if (sheet == null) {
                System.out.println("Sheet not found: " + COURSE_SHEET_NAME);
                return courses; // Return empty list
            }

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) { // Start from row 1 (skip header)
                Row row = sheet.getRow(rowIndex);
                if (row != null) {
                    try {
                        String courseCode = getStringCellValue(row.getCell(0));
                        String courseName = getStringCellValue(row.getCell(1));
                        String subjectName = getStringCellValue(row.getCell(2));
                        String sectionNumber = getStringCellValue(row.getCell(3));
                        String teacherName = getStringCellValue(row.getCell(4));
                        int capacity = (int) Double.parseDouble(getStringCellValue(row.getCell(5)));  //getStringCellValue(row.getCell(5));
                        String lectureTime = getStringCellValue(row.getCell(6));
                        String finalExamDateTime = getStringCellValue(row.getCell(7));
                        String location = getStringCellValue(row.getCell(8));


                        //CoursesController course = new CoursesController(courseCode, courseName, subjectName, sectionNumber, teacherName, capacity, lectureTime, finalExamDateTime, location);
                        //courses.add(course);
                    } catch (Exception e) {
                        System.err.println("Error reading row " + rowIndex + ": " + e.getMessage());
                        e.printStackTrace(); // Print the full stack trace for debugging
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Error loading courses from Excel: " + e.getMessage());
            e.printStackTrace();
        }
        return courses;
    }

    // Helper method to safely get string cell value
    private static String getStringCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        try {
            cell.setCellType(CellType.STRING);
            return cell.getStringCellValue();
        } catch (Exception e) {
            System.err.println("Error getting string cell value: " + e.getMessage());
            return "";
        }
    }

    // Method to write student data to Excel
    public static void saveStudentsToExcel(List<Student> students) {
        try (FileInputStream fileInputStream = new FileInputStream(new File(EXCEL_FILE_PATH));
             Workbook workbook = new XSSFWorkbook(fileInputStream)) {

            Sheet sheet = workbook.getSheet(STUDENT_SHEET_NAME);
            if (sheet == null) {
                System.out.println("Sheet not found: " + STUDENT_SHEET_NAME);
                return;
            }

            // Clear existing data (except header row)
            int rowCount = sheet.getLastRowNum();
            for (int i = rowCount; i > 0; i--) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    sheet.removeRow(row);
                }
            }

            // Create new rows and cells for each student
            int rowIndex = 1;
            for (Student student : students) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(student.getStudentId());
                row.createCell(1).setCellValue(student.getName());
                row.createCell(2).setCellValue(student.getAddress());
                row.createCell(3).setCellValue(student.getTelephone());
                row.createCell(4).setCellValue(student.getEmail());
                row.createCell(5).setCellValue(student.getAcademicLevel());
                row.createCell(6).setCellValue(student.getCurrentSemester());
                row.createCell(7).setCellValue(student.getProfilePhoto());
                row.createCell(8).setCellValue(student.getSubjectsRegistered());
                row.createCell(9).setCellValue(student.getThesisTitle());
                row.createCell(10).setCellValue(student.getProgress());
                row.createCell(11).setCellValue(student.getPassword());
            }

            // Write the output to the Excel file
            try (FileOutputStream fileOutputStream = new FileOutputStream(EXCEL_FILE_PATH)) {
                workbook.write(fileOutputStream);
            }
//
        } catch (IOException e) {
            System.err.println("Error saving students to Excel: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to get a student by ID
    public static Student getStudentById(String studentId) {
        List<Student> students = loadStudentsFromExcel();
        for (Student student : students) {
            if (student.getStudentId().equals(studentId)) {
                return student;
            }
        }
        return null; // Student not found
    }

    // Method to get courses for a specific student

}