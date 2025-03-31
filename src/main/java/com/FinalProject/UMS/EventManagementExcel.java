package com.FinalProject.UMS;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EventManagementExcel {
    private static final String FILE_PATH = "src/main/resources/UMS_Data.xlsx";
    private static final String SHEET_NAME = "Events";
    private static final int SHEET_INDEX = 4; // Correct sheet index (5th sheet)

    // Reads events from the Excel sheet
    public List<EventController> readEvents() {
        List<EventController> events = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(FILE_PATH);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(SHEET_INDEX);
            if (sheet == null) {
                System.out.println("Sheet not found at index: " + SHEET_INDEX);
                return events;
            }

            Iterator<Row> rowIterator = sheet.iterator();
            if (rowIterator.hasNext()) {
                rowIterator.next(); // Skip header row
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                EventController event = createEventFromRow(row);
                if (event != null) {
                    events.add(event);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return events;
    }

    // Creates an EventController object from a row in the Excel sheet
    private EventController createEventFromRow(Row row) {
        try {
            String eventCode = getStringCellValue(row.getCell(0));
            String title = getStringCellValue(row.getCell(1));
            String description = getStringCellValue(row.getCell(2));
            String location = getStringCellValue(row.getCell(3));
            String date = getStringCellValue(row.getCell(4));
            int capacity = (int) getNumericCellValue(row.getCell(5));

            double cost = 0.0;
            String costStr = getStringCellValue(row.getCell(6));

            if (costStr != null) {
                costStr = costStr.trim(); // Remove leading/trailing spaces

                if (costStr.equalsIgnoreCase("Free")) {
                    cost = 0.0;
                } else if (costStr.startsWith("Paid")) {
                    // Extract the number within parentheses
                    int startIndex = costStr.indexOf('(');
                    int endIndex = costStr.indexOf(')');
                    if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
                        try {
                            cost = Double.parseDouble(costStr.substring(startIndex + 2, endIndex)); // +2 to skip "($"
                        } catch (NumberFormatException e) {
                            System.err.println("Error parsing cost: " + costStr);
                            e.printStackTrace();
                        }
                    }
                }
            }

            String headerImage = getStringCellValue(row.getCell(7));
            String registeredStudents = getStringCellValue(row.getCell(8));

            return new EventController(eventCode, title, description, location,
                    date, capacity, cost, headerImage, registeredStudents);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Adds a new event to the Excel sheet
    public void addEvent(EventController event) {
        try (FileInputStream fis = new FileInputStream(FILE_PATH);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(SHEET_INDEX);
            if (sheet == null) {
                sheet = workbook.createSheet(SHEET_NAME);
                createHeaderRow(sheet);
            }

            // Find the next available row
            int nextRow = findNextAvailableRow(sheet);
            Row newRow = sheet.createRow(nextRow);
            populateEventRow(newRow, event);

            writeWorkbook(workbook);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Updates an existing event in the Excel sheet
    public void updateEvent(String eventCode, EventController updatedEvent) {
        try (FileInputStream fis = new FileInputStream(FILE_PATH);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(SHEET_INDEX);
            if (sheet == null) return;

            // Iterate through rows to find the event to update
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null && eventCode.equals(getStringCellValue(row.getCell(0)))) {
                    populateEventRow(row, updatedEvent);
                    break; // Event found and updated
                }
            }

            writeWorkbook(workbook);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Deletes an event from the Excel sheet
    public void deleteEvent(String eventCode) {
        try (FileInputStream fis = new FileInputStream(FILE_PATH);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(SHEET_INDEX);
            if (sheet == null) return;

            // Iterate through rows to find the event to delete
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null && eventCode.equals(getStringCellValue(row.getCell(0)))) {
                    // Remove the row and shift rows up
                    sheet.removeRow(row);
                    shiftRowsUp(sheet, i + 1, sheet.getLastRowNum());
                    break; // Event found and deleted
                }
            }

            writeWorkbook(workbook);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Populates a row with event data
    private void populateEventRow(Row row, EventController event) {
        row.createCell(0).setCellValue(event.getEventCode());
        row.createCell(1).setCellValue(event.getTitle());
        row.createCell(2).setCellValue(event.getDescription());
        row.createCell(3).setCellValue(event.getLocation());
        row.createCell(4).setCellValue(event.getDate());
        row.createCell(5).setCellValue(event.getCapacity());
        row.createCell(6).setCellValue(event.getCost() == 0 ? "Free" : "Paid ($" + event.getCost() + ")");
        row.createCell(7).setCellValue(event.getHeaderImage());
        row.createCell(8).setCellValue(event.getRegisteredStudents());
    }

    // Creates the header row in the Excel sheet
    private void createHeaderRow(Sheet sheet) {
        String[] headers = {"Event Code", "Event Name", "Description", "Location",
                "Date and Time", "Capacity", "Cost", "Header Image", "Registered Students"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }
    }

    // Shifts rows up after deleting a row
    private void shiftRowsUp(Sheet sheet, int startRow, int endRow) {
        if (startRow <= endRow) {
            for (int i = startRow; i <= endRow; i++) {
                Row currentRow = sheet.getRow(i);
                if (currentRow != null) {
                    Row previousRow = sheet.getRow(i - 1);
                    if (previousRow == null) {
                        previousRow = sheet.createRow(i - 1);
                    }
                    copyRow(sheet, currentRow, previousRow);
                }
            }

            // Remove the last row
            Row lastRow = sheet.getRow(endRow);
            if (lastRow != null) {
                sheet.removeRow(lastRow);
            }
        }
    }

    // Copies data from one row to another
    private void copyRow(Sheet sheet, Row sourceRow, Row destinationRow) {
        for (int i = sourceRow.getFirstCellNum(); i < sourceRow.getLastCellNum(); i++) {
            Cell oldCell = sourceRow.getCell(i);
            Cell newCell = destinationRow.createCell(i);

            if (oldCell != null) {
                copyCell(oldCell, newCell);
            }
        }
    }

    // Copies data from one cell to another
    private void copyCell(Cell oldCell, Cell newCell) {
        if (oldCell == null) {
            newCell.setBlank();
            return;
        }

        switch (oldCell.getCellType()) {
            case STRING:
                newCell.setCellValue(oldCell.getStringCellValue());
                break;
            case NUMERIC:
                newCell.setCellValue(oldCell.getNumericCellValue());
                break;
            case BOOLEAN:
                newCell.setCellValue(oldCell.getBooleanCellValue());
                break;
            case FORMULA:
                newCell.setCellValue(oldCell.getCellFormula());
                break;
            case BLANK:
                newCell.setBlank(); // Ensure blank cells are copied as blank
                break;
            default:
                newCell.setBlank();
                break;
        }
    }

    // Finds the next available row in the sheet
    private int findNextAvailableRow(Sheet sheet) {
        int rowNum = 1; // Start after the header row
        while (sheet.getRow(rowNum) != null) {
            rowNum++;
        }
        return rowNum;
    }


    // Writes the workbook to the file
    private void writeWorkbook(Workbook workbook) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(FILE_PATH)) {
            workbook.write(fos);
        }
    }

    // Gets the string value of a cell
    private String getStringCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return "";
        }
    }

    // Gets the numeric value of a cell
    private double getNumericCellValue(Cell cell) {
        if (cell == null) return 0;
        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
                try {
                    return Double.parseDouble(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    return 0;
                }
            default:
                return 0;
        }
    }
}