package com.FinalProject.UMS;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Subject {

    private StringProperty name;
    private StringProperty code;

    public Subject(String name, String code) {
        this.name = new SimpleStringProperty(name);
        this.code = new SimpleStringProperty(code);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getCode() {
        return code.get();
    }

    public StringProperty codeProperty() {
        return code;
    }

    public void setCode(String code) {
        this.code.set(code);
    }

    @Override
    public String toString() {
        return "Subject{" +
                "name=" + name.get() +
                ", code=" + code.get() +
                '}';
    }
}