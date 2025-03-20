package com.FinalProject.UMS;

public class Tuition {
    private String semester;
    private double amountDue;
    private double amountPaid;
    private String status;

    public Tuition(String semester, double amountDue, double amountPaid, String status) {
        this.semester = semester;
        this.amountDue = amountDue;
        this.amountPaid = amountPaid;
        this.status = status;
    }

    // Getters
    public String getSemester() { return semester; }
    public double getAmountDue() { return amountDue; }
    public double getAmountPaid() { return amountPaid; }
    public String getStatus() { return status; }
}