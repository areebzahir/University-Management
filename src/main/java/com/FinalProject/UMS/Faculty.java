package com.FinalProject.UMS;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class Faculty {
    private final StringProperty id;
    private final StringProperty name;
    private final StringProperty degree;
    private final StringProperty researchInterest;
    private final StringProperty email;
    private final StringProperty officeLocation;
    private final StringProperty coursesOffered;
    private final StringProperty password;


    public Faculty(String id, String name, String degree, String researchInterest,
                   String email, String officeLocation, String coursesOffered, String password) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.degree = new SimpleStringProperty(degree);
        this.researchInterest = new SimpleStringProperty(researchInterest);
        this.email = new SimpleStringProperty(email);
        this.officeLocation = new SimpleStringProperty(officeLocation);
        this.coursesOffered = new SimpleStringProperty(coursesOffered);
        this.password = new SimpleStringProperty(password);
    }


    // Property getters
    public StringProperty idProperty() { return id; }
    public StringProperty nameProperty() { return name; }
    public StringProperty degreeProperty() { return degree; }
    public StringProperty researchInterestProperty() { return researchInterest; }
    public StringProperty emailProperty() { return email; }
    public StringProperty officeLocationProperty() { return officeLocation; }
    public StringProperty coursesOfferedProperty() { return coursesOffered; }
    public StringProperty passwordProperty() { return password; }


    // Value getters
    public String getId() { return id.get(); }
    public String getName() { return name.get(); }
    public String getDegree() { return degree.get(); }
    public String getResearchInterest() { return researchInterest.get(); }
    public String getEmail() { return email.get(); }
    public String getOfficeLocation() { return officeLocation.get(); }
    public String getCoursesOffered() { return coursesOffered.get(); }
    public String getPassword() { return password.get(); }


    // Value setters
    public void setId(String id) { this.id.set(id); }
    public void setName(String name) { this.name.set(name); }
    public void setDegree(String degree) { this.degree.set(degree); }
    public void setResearchInterest(String researchInterest) { this.researchInterest.set(researchInterest); }
    public void setEmail(String email) { this.email.set(email); }
    public void setOfficeLocation(String officeLocation) { this.officeLocation.set(officeLocation); }
    public void setCoursesOffered(String coursesOffered) { this.coursesOffered.set(coursesOffered); }
    public void setPassword(String password) { this.password.set(password); }
}


