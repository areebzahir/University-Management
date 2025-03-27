package com.FinalProject.UMS;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class PaymentController {
    @FXML private TextField amountField;
    @FXML private ChoiceBox<String> paymentMethod;
    @FXML private Label studentIdLabel;

    private String studentId;

    public void setStudentId(String studentId) {
        this.studentId = studentId;
        studentIdLabel.setText("Student ID: " + studentId);
    }

    @FXML
    private void initialize() {
        paymentMethod.getItems().addAll("Credit Card", "Debit Card", "Bank Transfer", "PayPal");
        paymentMethod.setValue("Credit Card");
    }

    @FXML
    private void handleSubmitPayment() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            String method = paymentMethod.getValue();

            // Process payment (in real app, connect to payment gateway)
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Payment Successful");
            alert.setHeaderText(String.format("Payment of $%.2f processed", amount));
            alert.setContentText("Method: " + method + "\nStudent ID: " + studentId);
            alert.showAndWait();

            // Close the window
            ((Stage) amountField.getScene().getWindow()).close();
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Amount");
            alert.setHeaderText("Please enter a valid payment amount");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleCancel() {
        ((Stage) amountField.getScene().getWindow()).close();
    }
}