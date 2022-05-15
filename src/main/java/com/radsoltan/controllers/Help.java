package com.radsoltan.controllers;

import com.radsoltan.App;
import javafx.event.ActionEvent;

/**
 * Controller for the help menu item window.
 */
public class Help {

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
