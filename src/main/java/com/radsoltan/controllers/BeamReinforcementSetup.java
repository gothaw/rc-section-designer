package com.radsoltan.controllers;

import com.radsoltan.App;
import com.radsoltan.model.Project;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

// TODO: 17/07/2021 Class is not finished. See todos below for what these do at the moment.
public class BeamReinforcementSetup extends Controller {

    @FXML
    public VBox container;
    @FXML
    public VBox topReinforcementVerticalSpacingsTitle;
    @FXML
    public VBox topReinforcementVBox;
    @FXML
    public VBox topVerticalSpacingVBox;
    @FXML
    public Button addTopRowButton;
    @FXML
    public Button deleteTopRowButton;
    @FXML
    public VBox bottomReinforcementVerticalSpacingsTitle;
    @FXML
    public VBox bottomReinforcementVBox;
    @FXML
    public VBox bottomVerticalSpacingVBox;
    @FXML
    public Button addBottomRowButton;
    @FXML
    public Button deleteBottomRowButton;

    private final Project project;

    // TODO: 17/07/2021 This only gets instance of the project
    public BeamReinforcementSetup() {
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

    public void addReinforcementRow(ActionEvent actionEvent) {
    }

    public void deleteReinforcementRow(ActionEvent actionEvent) {
    }
}
