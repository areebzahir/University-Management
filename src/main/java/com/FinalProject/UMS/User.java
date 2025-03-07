package com.FinalProject.UMS;

// User class representing a basic user model
public class User {
    private String id;         // Unique identifier for the user
    private String email;      // User's email address
    private String password;   // User's password (should be securely stored in a real application)

    // Constructor to initialize user attributes
    public User(String id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }

    // Getter method to retrieve the user's ID
    public String getId() {
        return id;
    }

    // Getter method to retrieve the user's email
    public String getEmail() {
        return email;
    }

    // Getter method to retrieve the user's password
    public String getPassword() {
        return password;
    }

    // Method to authenticate the user by comparing passwords
    public boolean authenticate(String password) {
        if (this.password == null || password == null) {
            return false; // Prevents null pointer exceptions
        }
        return this.password.equals(password); // Checks if the provided password matches the stored one
    }

    // Overridden toString method to return user details as a string
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' + // Note: Password should not be displayed in real-world scenarios
                '}';
    }
}
