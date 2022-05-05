package com.radsoltan.controllers;

import com.radsoltan.App;
import com.radsoltan.util.Constants;
import com.radsoltan.util.Events;
import com.radsoltan.util.FileEvent;
import com.radsoltan.util.UIText;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Abstract class for controllers. Includes functionality to be shared between all controllers.
 * It also handles events for top menu.
 */
public abstract class Controller {

    public static final EventType<?> newFileEvent = new EventType<>(Events.NEW_FILE);
    public static final EventType<?> saveFileEvent = new EventType<>(Events.SAVE_FILE);
    public static final EventType<?> saveAsFileEvent = new EventType<>(Events.SAVE_AS_FILE);
    public static final EventType<?> openFileEvent = new EventType<>(Events.OPEN_FILE);

    private static File mainFile;

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
     * Shows an alert box with custom preferred width and preferred height. The alert includes one OK button.
     *
     * @param message    Text to be shown as an alert message.
     * @param kind       Type of alert box as a AlertKind enum.
     * @param prefWidth  Width in pixels.
     * @param prefHeight Height in pixels.
     */
    protected void showAlertBox(String message, AlertKind kind, double prefWidth, double prefHeight) {
        Alert alert = createAlertBox(message, kind, prefWidth, prefHeight, false);
        alert.showAndWait();
    }

    /**
     * It creates an alert box with custom preferred width and preferred height.
     * It uses JavaFX alert with type of NONE, OK button and optionally can include Cancel button.
     *
     * @param message             Text to be shown as an alert message.
     * @param kind                Type of alert box as a AlertKind enum.
     * @param prefWidth           Width in pixels.
     * @param prefHeight          Height in pixels.
     * @param includeCancelButton Boolean used to include a second button - cancel button.
     * @return alert to be shown
     */
    private Alert createAlertBox(String message, AlertKind kind, double prefWidth, double prefHeight, boolean includeCancelButton) {
        Alert alert = includeCancelButton
                ? new Alert(Alert.AlertType.NONE, message, ButtonType.OK, ButtonType.CANCEL)
                : new Alert(Alert.AlertType.NONE, message, new ButtonType(UIText.OK, ButtonBar.ButtonData.LEFT));
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        // Setting the icon
        stage.getIcons().add(new Image(getClass().getResource(kind.getUrl()).toExternalForm()));
        // Setting css stylesheet
        loadAlertStylesheets(alert);
        alert.setTitle(kind.getTitle());
        alert.getDialogPane().setPrefSize(prefWidth, prefHeight);
        centerAlertBox(alert);

        return alert;
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
     * This redirects to primary controller and fires an event for new file which is handled in primary controller.
     *
     * @param actionEvent top menu item click event
     */
    public void onNewMenuItemClickedHandler(ActionEvent actionEvent) {
        Alert alert = createAlertBox(UIText.ALERT_NEW_ITEM, AlertKind.INFO, Constants.LARGE_ALERT_WIDTH, Constants.DEFAULT_ALERT_HEIGHT, true);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                App.getStage().fireEvent(new Event(newFileEvent));
                App.setRoot("primary");
            } catch (IOException e) {
                showAlertBox(UIText.SOMETHING_WENT_WRONG, AlertKind.ERROR);
            }
        }
    }

    /**
     * Method that handles click on the "File -> Open" top menu item.
     * It uses file chooser to show an open dialog. If file is created it fires open file event.
     *
     * @param actionEvent top menu item click event
     */
    public void onOpenMenuItemClickedHandler(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(Constants.PROJECT_FILE, Constants.PROJECT_FILE_EXTENSION));
        File file = fileChooser.showOpenDialog(App.getStage());
        if (file != null) {
            App.getStage().fireEvent(new FileEvent(file, openFileEvent));
        }
    }

    /**
     * Method that handles click on the "File -> Save" top menu item.
     * If main project file is already set up, it passes that file to an event and fires save file event.
     * Otherwise it invokes method for save as functionality.
     *
     * @param actionEvent top menu item click event
     */
    public void onSaveMenuItemClickedHandler(ActionEvent actionEvent) {
        if (mainFile != null) {
            App.getStage().fireEvent(new FileEvent(mainFile, saveFileEvent));
        } else {
            onSaveAsMenuItemClickedHandler(actionEvent);
        }
    }

    /**
     * Method that handles click on the "File -> Save As" top menu item.
     * It uses file chooser to show a save dialog. If file object is created it fires save as file event.
     *
     * @param actionEvent top menu item click event
     */
    public void onSaveAsMenuItemClickedHandler(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(Constants.PROJECT_FILE, Constants.PROJECT_FILE_EXTENSION));
        File file = fileChooser.showSaveDialog(App.getStage());
        if (file != null) {
            // Saving file to the static field for future use
            mainFile = file;
            App.getStage().fireEvent(new FileEvent(file, saveAsFileEvent));
        }
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
