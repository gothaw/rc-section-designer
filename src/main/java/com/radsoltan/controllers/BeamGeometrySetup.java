package com.radsoltan.controllers;

import com.radsoltan.App;
import com.radsoltan.model.Project;

import javafx.event.ActionEvent;

import java.io.IOException;
import java.util.List;

// TODO: 17/07/2021 Class is not finished. See todos below for what these do at the moment.
public class BeamGeometrySetup extends Controller {

    private final Project project;

    // TODO: 17/07/2021 This only gets instance of the project
    public BeamGeometrySetup() {
        project = Project.getInstance();
    }

    // TODO: 17/07/2021 Navigation to primary controller only
    public void applyChanges(ActionEvent actionEvent) throws IOException {
        App.setRoot("primary");
    }

    // TODO: 17/07/2021 Navigation to primary controller only
    public void cancel(ActionEvent actionEvent) throws IOException {
        App.setRoot("primary");
    }

    // TODO: 17/07/2021 Needs to be implemented to validate fields
    @Override
    protected List<String> getValidationMessagesForEmptyFields() {
        return null;
    }
}
