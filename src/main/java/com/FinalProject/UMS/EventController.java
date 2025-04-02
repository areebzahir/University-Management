package com.FinalProject.UMS;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;


public class EventController {
    private String eventCode;
    private String title;
    private String description;
    private String date;
    private String location;
    private int capacity;
    private double cost;
    private String headerImage;
    private String registeredStudents;


    public EventController(String eventCode, String title, String description,
                           String location, String date, int capacity, double cost) {
        this(eventCode, title, description, location, date, capacity, cost, "", "");
    }


    public EventController(String eventCode, String title, String description,
                           String location, String date, int capacity,
                           double cost, String headerImage, String registeredStudents) {
        setEventCode(eventCode);
        setTitle(title);
        setDescription(description);
        setLocation(location);
        setDate(date);
        setCapacity(capacity);
        setCost(cost);
        setHeaderImage(headerImage);
        setRegisteredStudents(registeredStudents);
    }


    // Getters
    public String getEventCode() { return eventCode; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDate() { return date; }
    public String getLocation() { return location; }
    public int getCapacity() { return capacity; }
    public double getCost() { return cost; }
    public String getHeaderImage() { return headerImage; }
    public String getRegisteredStudents() { return registeredStudents; }


    // Setters with validation
    public void setEventCode(String eventCode) {
        if (eventCode == null || eventCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Event code cannot be null or empty");
        }
        this.eventCode = eventCode.trim();
    }


    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        this.title = title.trim();
    }


    public void setDescription(String description) {
        this.description = description != null ? description.trim() : "";
    }


    public void setDate(String date) {
        if (date == null || date.trim().isEmpty()) {
            throw new IllegalArgumentException("Date cannot be null or empty");
        }
        this.date = date.trim();
    }


    public void setLocation(String location) {
        if (location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException("Location cannot be null or empty");
        }
        this.location = location.trim();
    }


    public void setCapacity(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity cannot be negative");
        }
        this.capacity = capacity;
    }


    public void setCost(double cost) {
        if (cost < 0) {
            throw new IllegalArgumentException("Cost cannot be negative");
        }
        this.cost = cost;
    }


    public void setHeaderImage(String headerImage) {
        this.headerImage = headerImage != null ? headerImage.trim() : "";
    }


    public void setRegisteredStudents(String registeredStudents) {
        this.registeredStudents = registeredStudents != null ? registeredStudents.trim() : "";
    }


    @Override
    public String toString() {
        return String.format("%s - %s (%s at %s) - Capacity: %d, Cost: %s",
                eventCode, title, date, location, capacity,
                cost == 0 ? "Free" : String.format("$%.2f", cost));
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventController that = (EventController) o;
        return capacity == that.capacity &&
                Double.compare(that.cost, cost) == 0 &&
                eventCode.equals(that.eventCode) &&
                title.equals(that.title) &&
                description.equals(that.description) &&
                date.equals(that.date) &&
                location.equals(that.location) &&
                headerImage.equals(that.headerImage) &&
                registeredStudents.equals(that.registeredStudents);
    }


    @Override
    public int hashCode() {
        return Objects.hash(eventCode, title, description, date, location,
                capacity, cost, headerImage, registeredStudents);
    }


    public void registerStudent(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be null or empty");
        }


        if (registeredStudents.isEmpty()) {
            registeredStudents = studentId.trim();
        } else {
            registeredStudents += "," + studentId.trim();
        }
    }


    public boolean isStudentRegistered(String studentId) {
        if (studentId == null || studentId.trim().isEmpty() || registeredStudents.isEmpty()) {
            return false;
        }
        String[] students = registeredStudents.split(",");
        for (String id : students) {
            if (id.trim().equals(studentId.trim())) {
                return true;
            }
        }
        return false;
    }


    public boolean unregisterStudent(String studentId) {
        if (studentId == null || studentId.trim().isEmpty() || registeredStudents.isEmpty()) {
            return false;
        }


        String[] students = registeredStudents.split(",");
        StringBuilder newList = new StringBuilder();


        boolean removed = false;


        for (String id : students) {
            if (!id.trim().equals(studentId.trim())) {
                if (newList.length() > 0) {
                    newList.append(",");
                }
                newList.append(id.trim());
            } else {
                removed = true;
            }
        }


        if (removed) {
            registeredStudents = newList.toString();
        }
        return removed;
    }


    public LocalDate getLocalDate() {
        try {
            return LocalDate.parse(this.date.split(" ")[0]);
        } catch (Exception e) {
            return null;
        }
    }


    public String getTime() {
        try {
            return this.date.split(" ")[1];
        } catch (Exception e) {
            return "";
        }
    }




    public int getRegisteredStudentsCount() {
        if (registeredStudents.isEmpty()) {
            return 0;
        }
        return registeredStudents.split(",").length;
    }


    public boolean hasAvailableSpots() {
        return getRegisteredStudentsCount() < capacity;
    }
}
