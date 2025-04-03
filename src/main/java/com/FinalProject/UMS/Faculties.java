package com.FinalProject.UMS;

public class Faculties {
    private String facultyId;
    private String name;
    private String degree;
    private String researchInterest;
    private String email;
    private String officeLocation;
    private String coursesOffered;
    private String password;

    public Faculties() {
        // Default constructor
    }

    public Faculties(String facultyId, String name, String degree, String researchInterest, String email, String officeLocation, String coursesOffered, String password) {
        this.facultyId = facultyId;
        this.name = name;
        this.degree = degree;
        this.researchInterest = researchInterest;
        this.email = email;
        this.officeLocation = officeLocation;
        this.coursesOffered = coursesOffered;
        this.password = password;
    }

    // Getters and Setters for each field
    public String getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(String facultyId) {
        this.facultyId = facultyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getResearchInterest() {
        return researchInterest;
    }

    public void setResearchInterest(String researchInterest) {
        this.researchInterest = researchInterest;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOfficeLocation() {
        return officeLocation;
    }

    public void setOfficeLocation(String officeLocation) {
        this.officeLocation = officeLocation;
    }

    public String getCoursesOffered() {
        return coursesOffered;
    }

    public void setCoursesOffered(String coursesOffered) {
        this.coursesOffered = coursesOffered;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Faculties{" +
                "facultyId='" + facultyId + '\'' +
                ", name='" + name + '\'' +
                ", degree='" + degree + '\'' +
                ", researchInterest='" + researchInterest + '\'' +
                ", email='" + email + '\'' +
                ", officeLocation='" + officeLocation + '\'' +
                ", coursesOffered='" + coursesOffered + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}