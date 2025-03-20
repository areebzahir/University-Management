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
    private int capacity;           // Maximum number of attendees
    private double cost;            // Cost to attend the event
    private String headerImage;     // URL for the header image
    private String registeredStudents; // Comma-separated list of registered students

    /**
     * Constructor to initialize an EventController object with given details.
     */
    public EventController(String eventCode, String title, String description, String location, String date, int capacity, double cost) {
        this(eventCode, title, description, location, date, capacity, cost, "", ""); // Call the main constructor with default values
    }

    public EventController(String eventCode, String title, String description, String location, String date, int capacity, double cost, String headerImage, String registeredStudents) {
        this.eventCode = eventCode;
        this.title = title;
        this.description = description;
        this.date = date;
        this.location = location;
        this.capacity = capacity;
        this.cost = cost;
        this.headerImage = headerImage;
        this.registeredStudents = registeredStudents;
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

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }

    public String getHeaderImage() { return headerImage; }
    public void setHeaderImage(String headerImage) { this.headerImage = headerImage; }

    public String getRegisteredStudents() { return registeredStudents; }
    public void setRegisteredStudents(String registeredStudents) { this.registeredStudents = registeredStudents; }


    /**
     * Returns a string representation of the event.
     * @return A formatted string containing the event title, date, and location.
     */
    @Override
    public String toString() {
        return eventCode + " - " + title + " (" + date + " at " + location + ") - Capacity: " + capacity + ", Cost: " + (cost == 0 ? "Free" : "$" + cost);
    }
}