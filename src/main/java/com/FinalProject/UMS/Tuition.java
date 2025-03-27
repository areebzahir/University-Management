package com.FinalProject.UMS;

import java.time.LocalDate;

public class Tuition {
    private String semester;
    private double amountDue;
    private double amountPaid;
    private String status;
    private LocalDate dueDate;
    private String paymentMethod;
    private String breakdown;

    public Tuition(String semester, double amountDue, double amountPaid, String status,
                   LocalDate dueDate, String paymentMethod, String breakdown) {
        this.semester = semester;
        this.amountDue = amountDue;
        this.amountPaid = amountPaid;
        this.status = status;
        this.dueDate = dueDate;
        this.paymentMethod = paymentMethod;
        this.breakdown = breakdown;
    }

    // Getters
    public String getSemester() { return semester; }
    public double getAmountDue() { return amountDue; }
    public double getAmountPaid() { return amountPaid; }
    public String getStatus() { return status; }
    public LocalDate getDueDate() { return dueDate; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getBreakdown() { return breakdown; }
    public double getBalance() { return amountDue - amountPaid; }
}
