package com.FinalProject.UMS;

public class Grade {//
    private String courseSection;
    private String title;
    private String credits;
    private String finalGrade;


    public Grade(String courseSection, String title, String credits, String finalGrade) {
        this.courseSection = courseSection;
        this.title = title;
        this.credits = credits;
        this.finalGrade = finalGrade;
    }


    // Getters and Setters
    public String getCourseSection() {
        return courseSection;
    }


    public void setCourseSection(String courseSection) {
        this.courseSection = courseSection;
    }


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public String getCredits() {
        return credits;
    }


    public void setCredits(String credits) {
        this.credits = credits;
    }


    public String getFinalGrade() {
        return finalGrade;
    }


    public void setFinalGrade(String finalGrade) {
        this.finalGrade = finalGrade;
    }
}




