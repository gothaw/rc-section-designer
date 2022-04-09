package com.radsoltan.controllers;

import com.radsoltan.App;
import com.radsoltan.util.Constants;
import com.radsoltan.util.UIText;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.util.List;

/**
 * Abstract class for controllers. Includes functionality to be shared between all controllers.
 * It also handles events for top menu.
 */
public abstract class Controller {

    /**
     * Shows an alert box with a default width and height.
     *
     * @param message Text to be shown as an alert message.
     * @param kind    Type of alert box as a AlertKind enum.
     */
    protected void showAlertBox(String message, AlertKind kind) {
        showAlertBox(message, kind, Constants.DEFAULT_ALERT_WIDTH, Constants.DEFAULT_ALERT_HEIGHT);
    }

    /**
     * Shows an alert box with custom preferred width and preferred height.
     * It uses JavaFX alert with type of NONE and OK button.
     *
     * @param message    Text to be shown as an alert message.
     * @param kind       Type of alert box as a AlertKind enum.
     * @param prefWidth  Width in pixels.
     * @param prefHeight Height in pixels.
     */
    protected void showAlertBox(String message, AlertKind kind, double prefWidth, double prefHeight) {
        Alert alert = new Alert(Alert.AlertType.NONE, message, new ButtonType(UIText.OK));
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        // Setting the icon
        stage.getIcons().add(new Image(getClass().getResource(kind.getUrl()).toExternalForm()));
        // Setting css stylesheet
        loadAlertStylesheets(alert);
        alert.getButtonTypes().set(0, new ButtonType(UIText.OK, ButtonBar.ButtonData.LEFT));
        alert.setTitle(kind.getTitle());
        alert.getDialogPane().setPrefSize(prefWidth, prefHeight);
        centerAlertBox(alert);
        alert.showAndWait();
    }

    /**
     * Centers the alert box within window.
     *
     * @param alert alert to be centered.
     */
    private void centerAlertBox(Alert alert) {
        alert.setX(App.getStage().getX() + App.getStage().getWidth() / 2 - alert.getDialogPane().getPrefWidth() / 2);
        alert.setY(App.getStage().getY() + App.getStage().getHeight() / 2 - alert.getDialogPane().getPrefHeight() / 2);
    }

    /**
     * Loads alert stylesheets. Loads main css file to the alert box.
     *
     * @param alert alert that requires loading css.
     */
    private void loadAlertStylesheets(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());
    }

    /**
     * Method that should check for empty fields in view and add a message to a List about each empty field.
     *
     * @return List of validation messages
     */
    protected abstract List<String> getValidationMessagesForEmptyFields();

    /**
     * Method that handles click on the "File -> New" top menu item.
     *
     * @param actionEvent top menu item click event
     */
    public void onNewMenuItemClickedHandler(ActionEvent actionEvent) {
        System.out.println("New");
    }

    /**
     * Method that handles click on the "File -> Open" top menu item.
     *
     * @param actionEvent top menu item click event
     */
    public void onOpenMenuItemClickedHandler(ActionEvent actionEvent) {
        System.out.println("Open");
    }

    /**
     * Method that handles click on the "File -> Save" top menu item.
     *
     * @param actionEvent top menu item click event
     */
    public void onSaveMenuItemClickedHandler(ActionEvent actionEvent) {
        System.out.println("Save");
    }

    /**
     * Method that handles click on the "File -> Save As" top menu item.
     *
     * @param actionEvent top menu item click event
     */
    public void onSaveAsMenuItemClickedHandler(ActionEvent actionEvent) {
        System.out.println("Save As");
    }

    /**
     * Method that handles click on the "About" top menu item.
     *
     * @param mouseEvent top menu item click event
     */
    public void onAboutMenuItemClickedHandler(MouseEvent mouseEvent) {
        System.out.println("About");
    }

    /**
     * Method that handles click on the "Help" top menu item.
     *
     * @param mouseEvent top menu item click event
     */
    public void onHelpMenuItemClickedHandler(MouseEvent mouseEvent) {
        System.out.println("Help");
    }
}
