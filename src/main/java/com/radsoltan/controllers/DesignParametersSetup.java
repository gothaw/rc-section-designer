package com.radsoltan.controllers;

import com.radsoltan.App;
import javafx.event.ActionEvent;

import java.io.IOException;

public class DesignParametersSetup {

    public void cancel(ActionEvent actionEvent) throws IOException {
        App.setRoot("primary");
    }

    public void applyChanges(ActionEvent actionEvent) {
        System.out.println("Apply changes");
    }
}
