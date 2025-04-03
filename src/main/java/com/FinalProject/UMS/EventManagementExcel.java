package com.FinalProject.UMS;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EventManagementExcel {
    private static final String FILE_PATH = "src/main/resources/UMS_Data.xlsx";
    private static final String SHEET_NAME = "Events";
    private static final int SHEET_INDEX = 3;
    private static final DateTimeFormatter excelDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

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

    private EventController createEventFromRow(Row row) {
        try {
            String eventCode = getStringCellValue(row.getCell(0));
            String title = getStringCellValue(row.getCell(1));
            String description = getStringCellValue(row.getCell(2));
            String location = getStringCellValue(row.getCell(3));

            String dateTimeStr = getStringCellValue(row.getCell(4));
            if (dateTimeStr.matches("\\d+\\.?\\d*")) {
                double excelDate = Double.parseDouble(dateTimeStr);
                dateTimeStr = convertExcelDateToString(excelDate);
            }

            int capacity = (int) getNumericCellValue(row.getCell(5));

            double cost = 0.0;
            String costStr = getStringCellValue(row.getCell(6));

            if (costStr != null) {
                costStr = costStr.trim();
                if (costStr.equalsIgnoreCase("Free")) {
                    cost = 0.0;
                } else if (costStr.startsWith("Paid")) {
                    int startIndex = costStr.indexOf('(');
                    int endIndex = costStr.indexOf(')');
                    if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
                        try {
                            cost = Double.parseDouble(costStr.substring(startIndex + 2, endIndex));
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
                    dateTimeStr, capacity, cost, headerImage, registeredStudents);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String convertExcelDateToString(double excelDate) {
        // Excel's epoch is 1900-01-01 (with 1900 incorrectly treated as leap year)
        // Subtract 2 days to get correct dates (because of Excel's bug)
        LocalDate baseDate = LocalDate.of(1899, 12, 30);
        LocalDate date = baseDate.plusDays((long) excelDate);

        // Get time fraction (Excel stores time as fraction of a day)
        double timeFraction = excelDate - Math.floor(excelDate);
        int totalSeconds = (int) (timeFraction * 24 * 60 * 60);
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;

        return String.format("%04d-%02d-%02d %02d:%02d",
                date.getYear(), date.getMonthValue(), date.getDayOfMonth(),
                hours, minutes);
    }

    public void addEvent(EventController event) {
        try (FileInputStream fis = new FileInputStream(FILE_PATH);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(SHEET_INDEX);
            if (sheet == null) {
                sheet = workbook.createSheet(SHEET_NAME);
                createHeaderRow(sheet);
            }

            int nextRow = findNextAvailableRow(sheet);
            Row newRow = sheet.createRow(nextRow);
            populateEventRow(newRow, event);

            writeWorkbook(workbook);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateEvent(String eventCode, EventController updatedEvent) {
        try (FileInputStream fis = new FileInputStream(FILE_PATH);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(SHEET_INDEX);
            if (sheet == null)
                return;

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null && eventCode.equals(getStringCellValue(row.getCell(0)))) {
                    populateEventRow(row, updatedEvent);
                    break;
                }
            }

            writeWorkbook(workbook);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteEvent(String eventCode) {
        try (FileInputStream fis = new FileInputStream(FILE_PATH);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(SHEET_INDEX);
            if (sheet == null)
                return;

            int rowToDelete = -1;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null && eventCode.equals(getStringCellValue(row.getCell(0)))) {
                    rowToDelete = i;
                    break;
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

                writeWorkbook(workbook);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    private void createHeaderRow(Sheet sheet) {
        String[] headers = { "Event Code", "Event Name", "Description", "Location",
                "Date and Time", "Capacity", "Cost", "Header Image", "Registered Students" };
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }
    }

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

            Row lastRow = sheet.getRow(endRow);
            if (lastRow != null) {
                sheet.removeRow(lastRow);
            }
        }
    }

    private void copyRow(Sheet sheet, Row sourceRow, Row destinationRow) {
        for (int i = sourceRow.getFirstCellNum(); i < sourceRow.getLastCellNum(); i++) {
            Cell oldCell = sourceRow.getCell(i);
            Cell newCell = destinationRow.createCell(i);

            if (oldCell != null) {
                copyCell(oldCell, newCell);
            }
        }
    }

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
                newCell.setBlank();
                break;
            default:
                newCell.setBlank();
                break;
        }
    }

    private int findNextAvailableRow(Sheet sheet) {
        int rowNum = 1;
        while (sheet.getRow(rowNum) != null) {
            rowNum++;
        }
        return rowNum;
    }

    private void writeWorkbook(Workbook workbook) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(FILE_PATH)) {
            workbook.write(fos);
        }
    }

    private String getStringCellValue(Cell cell) {
        if (cell == null)
            return "";
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

    private double getNumericCellValue(Cell cell) {
        if (cell == null)
            return 0;
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

    public boolean eventCodeExists(String eventCode) {
        try (FileInputStream fis = new FileInputStream(FILE_PATH);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(SHEET_INDEX);
            if (sheet == null)
                return false;

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null && eventCode.equals(getStringCellValue(row.getCell(0)))) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
