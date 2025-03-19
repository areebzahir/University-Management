package com.FinalProject.UMS;

public class Student {
    private String lastName;
    private String firstName;
    private String email;
    private String role;
    private String id;
    private String address;
    private String telephone;
    private String profilePhoto;
    private String subjectsRegistered;
    private String thesisTitle;
    private double progress; // Change progress to double
    private String password;

    public Student(String id, String firstName, String lastName, String address, String telephone, String email,
                   String academicLevel, String currentSemester, String profilePhoto, String subjectsRegistered,
                   String thesisTitle, double progress, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.telephone = telephone;
        this.email = email;
        this.role = academicLevel; // Assuming academicLevel is the role
        this.profilePhoto = profilePhoto;
        this.subjectsRegistered = subjectsRegistered;
        this.thesisTitle = thesisTitle;
        this.progress = progress;
        this.password = password;
    }

    // Getters and Setters
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}