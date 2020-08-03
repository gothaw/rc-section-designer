package com.radsoltan.controllers;

import com.radsoltan.App;
import com.radsoltan.model.Project;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.util.List;

public class BeamReinforcementSetup extends Controller {

    private final Project project;

    public BeamReinforcementSetup() {
        project = Project.getInstance();
    }

    public void cancel(ActionEvent actionEvent) throws IOException {
        App.setRoot("primary");
    }

    public void applyChanges(ActionEvent actionEvent) throws IOException {
        App.setRoot("primary");
    }

    @Override
    protected List<String> getValidationMessagesForEmptyFields() {
        return null;
    }
}
