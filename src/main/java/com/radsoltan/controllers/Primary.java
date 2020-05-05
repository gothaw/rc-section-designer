package com.radsoltan.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.InputMethodEvent;

import java.util.ArrayList;

public class Primary {
    @FXML
    private TextField numberField;
    @FXML
    private ChoiceBox structureType;

    private StringProperty text;
    private TextFormatter<Double> numerical;

    public Primary(){
        text = new SimpleStringProperty();
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

    public void test(ActionEvent actionEvent) {
        System.out.println("click");
        System.out.println(structureType.getValue());
    }
}
