package com.radsoltan.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.InputMethodEvent;

public class Primary {
    @FXML
    private TextField numberField;

    private StringProperty text;
    private TextFormatter<Double> numerical;

    public Primary(){
        text = new SimpleStringProperty();
        System.out.println("hello");
    }

    public String getText() {
        return text.get();
    }

    public StringProperty textProperty() {
        return text;
    }

    public void setText(String text) {
        this.text.set(text);
    }


}
