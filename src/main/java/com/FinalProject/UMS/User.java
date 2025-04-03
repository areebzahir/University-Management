package com.FinalProject.UMS;

// User class representing a basic user model
public class User {
    private String id;         // Unique identifier for the user
    private String email;      // User's email address
    private String password;   // User's password (should be securely stored in a real application)

    private String name;
    private String address;
    private String telephone;
    private String academicLevel;
    private String currentSemester;
    private String profilePhoto;
    private String subjectsRegistered;
    private String thesisTitle;
    private String progress;
    private String role;

    public User(String id, String email, String password, String role) { //Overload the constructor
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public User() {

    }

    // Getters and setters
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    public String getStudentId() {
        return this.id;
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

    public void setEmail(String email) {
        this.email = email;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAcademicLevel() {
        return academicLevel;
    }

    public void setAcademicLevel(String academicLevel) {
        this.academicLevel = academicLevel;
    }

    public String getCurrentSemester() {
        return currentSemester;
    }

    public void setCurrentSemester(String currentSemester) {
        this.currentSemester = currentSemester;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getSubjectsRegistered() {
        return subjectsRegistered;
    }

    public void setSubjectsRegistered(String subjectsRegistered) {
        this.subjectsRegistered = subjectsRegistered;
    }

    public String getThesisTitle() {
        return thesisTitle;
    }

    public void setThesisTitle(String thesisTitle) {
        this.thesisTitle = thesisTitle;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    // Add this method to set the password
    public void setPassword(String password) {
        this.password = password;
    }


    public void setUsername(String a) {
    }
}