package com.FinalProject.UMS;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;


public class FacultyExcelHandler {
    private static final String FILE_PATH = "C:\\Users\\azahi\\OneDrive - University of Guelph\\1st Year\\Semester 2\\University-Management\\src\\main\\resources\\UMS_Data.xlsx";
    private static final String SHEET_NAME = "Faculties";
    private static final ReentrantLock fileLock = new ReentrantLock();


    public List<Faculty> readFaculty() {
        fileLock.lock();
        try (InputStream is = new FileInputStream(FILE_PATH);
             Workbook workbook = new XSSFWorkbook(is)) {


            Sheet sheet = workbook.getSheet(SHEET_NAME);
            if (sheet == null) {
                System.out.println("[ERROR] Faculty sheet not found - creating new sheet");
                return new ArrayList<>();
            }


            List<Faculty> facultyList = new ArrayList<>();
            for (Row row : sheet) {
                if (row == null || row.getRowNum() == 0) continue; // Skip header


                Faculty faculty = new Faculty(
                        getStringValue(row.getCell(0)),
                        getStringValue(row.getCell(1)),
                        getStringValue(row.getCell(2)),
                        getStringValue(row.getCell(3)),
                        getStringValue(row.getCell(4)),
                        getStringValue(row.getCell(5)),
                        getStringValue(row.getCell(6)),
                        getStringValue(row.getCell(7))
                );


                if (!faculty.getId().isEmpty()) {
                    facultyList.add(faculty);
                }
            }
            System.out.println("[SUCCESS] Loaded " + facultyList.size() + " faculty members");
            return facultyList;
        } catch (FileNotFoundException e) {
            System.out.println("[WARNING] Excel file not found - creating new file");
            return new ArrayList<>();
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to read faculty data: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            fileLock.unlock();
        }
    }


    public void saveFaculty(List<Faculty> facultyList) {
        fileLock.lock();
        File tempFile = new File(FILE_PATH + ".tmp");


        try {
            // 1. Create or load workbook
            Workbook workbook;
            if (new File(FILE_PATH).exists()) {
                try (InputStream is = new FileInputStream(FILE_PATH)) {
                    workbook = new XSSFWorkbook(is);
                }
            } else {
                workbook = new XSSFWorkbook();
            }


            // 2. Remove existing sheet if exists
            int sheetIndex = workbook.getSheetIndex(SHEET_NAME);
            if (sheetIndex >= 0) {
                workbook.removeSheetAt(sheetIndex);
            }


            // 3. Create fresh sheet
            Sheet sheet = workbook.createSheet(SHEET_NAME);


            // 4. Create headers
            String[] headers = {"Faculty ID", "Name", "Degree", "Research Interest",
                    "Email", "Office Location", "Courses Offered", "Password"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }


            // 5. Write all faculty data
            int rowNum = 1;
            for (Faculty faculty : facultyList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(faculty.getId());
                row.createCell(1).setCellValue(faculty.getName());
                row.createCell(2).setCellValue(faculty.getDegree());
                row.createCell(3).setCellValue(faculty.getResearchInterest());
                row.createCell(4).setCellValue(faculty.getEmail());
                row.createCell(5).setCellValue(faculty.getOfficeLocation());
                row.createCell(6).setCellValue(faculty.getCoursesOffered());
                row.createCell(7).setCellValue(faculty.getPassword());
            }


            // 6. Write to temp file
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                workbook.write(fos);
                fos.getFD().sync(); // Force write to disk
            }


            // 7. Close workbook
            workbook.close();


            // 8. Atomic replace
            File originalFile = new File(FILE_PATH);
            if (originalFile.exists() && !originalFile.delete()) {
                throw new IOException("Failed to delete original file");
            }
            if (!tempFile.renameTo(originalFile)) {
                throw new IOException("Failed to rename temp file");
            }


            System.out.println("[SUCCESS] Saved " + facultyList.size() + " faculty members to " + FILE_PATH);


        } catch (Exception e) {
            System.out.println("[ERROR] Failed to save faculty data: " + e.getMessage());
            e.printStackTrace();


            // Emergency recovery
            if (tempFile.exists() && !tempFile.delete()) {
                System.out.println("[WARNING] Failed to delete temp file");
            }
        } finally {
            fileLock.unlock();
        }
    }


    private String getStringValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue().trim();
            case NUMERIC: return cell.getNumericCellValue() % 1 == 0 ?
                    String.valueOf((int)cell.getNumericCellValue()) :
                    String.valueOf(cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                switch (cell.getCachedFormulaResultType()) {
                    case STRING: return cell.getStringCellValue();
                    case NUMERIC: return String.valueOf(cell.getNumericCellValue());
                    case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
                    default: return "";
                }
            default: return "";
        }
    }
}
