package com.FinalProject.UMS;



public class Student {
    private String lastName;
    private String firstName;
    private String email;
    private String role;
    private String id;


    public Student(String lastName, String firstName, String email, String role,String id) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.id = id;
        this.email = email;
        this.role = role;
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
    public String getId(){
        return id;
    }
    public void setId(String id){
        this.id = id;
    }
}



