package com.FinalProject.UMS;

import java.util.ArrayList;
import java.util.List;

/**
 * The EventController class represents an event in the University Management System.
 * It stores details about an event such as title, description, date, location, and more.
 */
public class EventController {

    // Private fields to store event details
    private String eventCode;       // Unique code for the event
    private String title;           // Event title
    private String description;     // Event description
    private String date;            // Event date
    private String location;        // Event location
    private String imageUrl;       // URL for the event image
    private int capacity;           // Maximum number of attendees
    private double cost;            // Cost to attend the event
    private List<EventStudent> registeredStudents; // List of registered students

    /**
     * Constructor to initialize an EventController object with given details.
     */
    public EventController(String eventCode, String title, String description, String date, String location, String imageUrl, int capacity, double cost) {
        this.eventCode = eventCode;
        this.title = title;
        this.description = description;
        this.date = date;
        this.location = location;
        this.imageUrl = imageUrl;
        this.capacity = capacity;
        this.cost = cost;
        this.registeredStudents = new ArrayList<>();
    }

    // Getters and Setters
    public String getEventCode() { return eventCode; }
    public void setEventCode(String eventCode) { this.eventCode = eventCode; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }

    public List<EventStudent> getRegisteredStudents() { return registeredStudents; }
    public void setRegisteredStudents(List<EventStudent> registeredStudents) { this.registeredStudents = registeredStudents; }

    /**
     * Checks if the event is full.
     * @return true if the event is full, false otherwise.
     */
    public boolean isFull() {
        return registeredStudents.size() >= capacity;
    }

    /**
     * Registers a student for the event if there is capacity.
     * @param student The student to register.
     */
    public void registerStudent(EventStudent student) {
        if (!isFull()) {
            registeredStudents.add(student);
        }
    }

    /**
     * Returns a string representation of the event.
     * @return A formatted string containing the event title, date, and location.
     */
    @Override
    public String toString() {
        return title + " - " + date + " (" + location + ")";
    }
}