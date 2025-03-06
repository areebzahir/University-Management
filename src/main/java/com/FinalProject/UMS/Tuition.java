package com.FinalProject.UMS;

public class Tuition {
    private String semester;
    private String amountDue;
    private String amountPaid;
    private String status;


    public Tuition(String semester, String amountDue, String amountPaid, String status) {
        this.semester = semester;
        this.amountDue = amountDue;
        this.amountPaid = amountPaid;
        this.status = status;
    }


    // Getters and Setters
    public String getSemester() {
        return semester;
    }


    public void setSemester(String semester) {
        this.semester = semester;
    }


    public String getAmountDue() {
        return amountDue;
    }


    public void setAmountDue(String amountDue) {
        this.amountDue = amountDue;
    }


    public String getAmountPaid() {
        return amountPaid;
    }


    public void setAmountPaid(String amountPaid) {
        this.amountPaid = amountPaid;
    }


    public String getStatus() {
        return status;
    }


    public void setStatus(String status) {
        this.status = status;
    }
}


