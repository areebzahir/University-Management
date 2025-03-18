package com.FinalProject.UMS;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Subject {

    // Properties to store subject name and code
    private StringProperty name;
    private StringProperty code;

    // Constructor to initialize subject with name and code
    public Subject(String name, String code) {
        this.name = new SimpleStringProperty(name);
        this.code = new SimpleStringProperty(code);
    }

    // Getter for name
    public String getName() {
        return name.get();
    }

    // Property getter for JavaFX bindings
    public StringProperty nameProperty() {
        return name;
    }

    // Setter for name
    public void setName(String name) {
        this.name.set(name);
    }

    // Getter for code
    public String getCode() {
        return code.get();
    }

    // Property getter for JavaFX bindings
    public StringProperty codeProperty() {
        return code;
    }

    // Setter for code
    public void setCode(String code) {
        this.code.set(code);
    }

    // Overriding toString method to return subject details as a string
    @Override
    public String toString() {
        return "Subject{" +
                "name=" + name.get() +
                ", code=" + code.get() +
                '}';
    }
}

