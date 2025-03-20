package com.FinalProject.UMS;

/**
 * The EventStudent class represents a student registered for an event.
 */
public class EventStudent {
    private String name;  // Student's name
    private String email; // Student's email

    /**
     * Constructor to initialize an EventStudent object.
     */
    public EventStudent(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return name + " (" + email + ")";
    }
}