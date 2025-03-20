package com.FinalProject.UMS;

public class Student {
    private String studentId;
    private String name;
    private String address;
    private String telephone;
    private String email;
    private String academicLevel;
    private String currentSemester;
    private String profilePhoto;
    private String subjectsRegistered;
    private String thesisTitle;
    private String progress;
    private String password;

    public Student(String studentId, String name, String address, String telephone, String email, String academicLevel, String currentSemester, String profilePhoto, String subjectsRegistered, String thesisTitle, String progress, String password) {
        this.studentId = studentId;
        this.name = name;
        this.address = address;
        this.telephone = telephone;
        this.email = email;
        this.academicLevel = academicLevel;
        this.currentSemester = currentSemester;
        this.profilePhoto = profilePhoto;
        this.subjectsRegistered = subjectsRegistered;
        this.thesisTitle = thesisTitle;
        this.progress = progress;
        this.password = password;
    }

    // Getters for all fields
    public String getStudentId() { return studentId; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getTelephone() { return telephone; }
    public String getEmail() { return email; }
    public String getAcademicLevel() { return academicLevel; }
    public String getCurrentSemester() { return currentSemester; }
    public String getProfilePhoto() { return profilePhoto; }
    public String getSubjectsRegistered() { return subjectsRegistered; }
    public String getThesisTitle() { return thesisTitle; }
    public String getProgress() { return progress; }
    public String getPassword() { return password; }
}