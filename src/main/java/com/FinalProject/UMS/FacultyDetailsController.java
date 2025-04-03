package com.FinalProject.UMS;


import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;


public class FacultyDetailsController {
    @FXML private Label nameLabel;
    @FXML private Label researchLabel;
    @FXML private Label advisingLabel;


    public void setFacultyData(Faculty faculty) {
        nameLabel.setText(faculty.getName());


        // Hardcoded details for first 3 faculties
        switch(faculty.getId()) {
            case "FAC-1":
                researchLabel.setText("Machine Learning, Computer Vision, AI Ethics");
                advisingLabel.setText("Available for PhD supervision in AI applications\nOffice hours: Mon/Wed 2-4pm");
                break;
            case "FAC-2":
                researchLabel.setText("Renewable Energy Systems, Power Electronics");
                advisingLabel.setText("Accepting MSc students for 2024-25\nOffice hours: Tue/Thu 10am-12pm");
                break;
            case "FAC-3":
                researchLabel.setText("Biomedical Engineering, Neural Interfaces");
                advisingLabel.setText("Undergraduate research opportunities available\nOffice hours: Fri 1-3pm");
                break;
            default:
                researchLabel.setText("General research interests: " + faculty.getResearchInterest());
                advisingLabel.setText("Please contact for advising information");
        }
    }


    @FXML
    private void handleClose() {
        ((Stage) nameLabel.getScene().getWindow()).close();
    }
}

