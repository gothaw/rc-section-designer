package com.radsoltan.controllers;

import com.radsoltan.App;
import com.radsoltan.constants.UIText;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.time.LocalDate;

/**
 * Controller for the about menu item window.
 */
public class About {

    @FXML
    public Label buildLabel;
    @FXML
    public Label appTitle;
    @FXML
    public Label year;
    @FXML
    public Label author;

    /**
     * Initializes fields.
     */
    @FXML
    public void initialize() {
        appTitle.setText(UIText.APP_TITLE);
        author.setText(UIText.APP_AUTHOR);
        year.setText(Integer.toString(LocalDate.now().getYear()));

        String version = getClass().getPackage().getImplementationVersion();

        buildLabel.setText(String.format("Build %s", version == null ? "DEV" : version));
    }

    /**
     * Handler for the ok button. Closes the window.
     *
     * @param actionEvent button click event
     */
    public void close(ActionEvent actionEvent) {
        App.getSecondaryWindow().getStage().close();
        App.setSecondaryWindow(null);
    }
}
