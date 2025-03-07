package com.FinalProject.UMS;

public class Faculty {
    private Integer id;  // Faculty ID (integer for consistency)
    private String name;  // Faculty name
    private String email;  // Faculty email
    private String department;  // Faculty department

    // Constructor to initialize faculty details
    public Faculty(Integer id, String name, String email, String department) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.department = department;
    }

    // Getters for accessing faculty attributes (used in TableView)
    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getDepartment() { return department; }

    // Setters for updating faculty attributes
    public void setId(Integer id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setDepartment(String department) { this.department = department; }
}