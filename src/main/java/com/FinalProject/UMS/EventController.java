package com.FinalProject.UMS; // Declares the package where this class is located

/**
 * The EventController class represents an event in the University Management System.
 * It stores details about an event such as title, description, date, and location.
 * This class also provides getter and setter methods to access and modify event details.
 */
public class EventController {

    // Private fields to store event details
    private String title;       // Event title
    private String description; // Event description
    private String date;        // Event date
    private String location;    // Event location

    /**
     * Constructor to initialize an EventController object with given details.
     * @param title The title of the event.
     * @param description A brief description of the event.
     * @param date The date on which the event will be held.
     * @param location The venue where the event will take place.
     */
    public EventController(String title, String description, String date, String location) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.location = location;
    }

    // Getter and Setter methods for each field

    /**
     * Gets the title of the event.
     * @return The event title.
     */
    public String getTitle() { return title; }

    /**
     * Sets a new title for the event.
     * @param title The new title to be assigned.
     */
    public void setTitle(String title) { this.title = title; }

    /**
     * Gets the description of the event.
     * @return The event description.
     */
    public String getDescription() { return description; }

    /**
     * Sets a new description for the event.
     * @param description The new description to be assigned.
     */
    public void setDescription(String description) { this.description = description; }

    /**
     * Gets the date of the event.
     * @return The event date.
     */
    public String getDate() { return date; }

    /**
     * Sets a new date for the event.
     * @param date The new date to be assigned.
     */
    public void setDate(String date) { this.date = date; }

    /**
     * Gets the location of the event.
     * @return The event location.
     */
    public String getLocation() { return location; }

    /**
     * Sets a new location for the event.
     * @param location The new location to be assigned.
     */
    public void setLocation(String location) { this.location = location; }

    /**
     * Returns a string representation of the event.
     * @return A formatted string containing the event title, date, and location.
     */
    @Override
    public String toString() {
        return title + " - " + date + " (" + location + ")";
    }
}
