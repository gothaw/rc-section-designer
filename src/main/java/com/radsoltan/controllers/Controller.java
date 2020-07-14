package com.radsoltan.controllers;

import com.radsoltan.App;
import com.radsoltan.util.Constants;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.List;

public abstract class Controller {

    protected List<String> validationMessages;

    protected void showAlertBox(String message, AlertKind kind){
        showAlertBox(message, kind, Constants.DEFAULT_ALERT_WIDTH, Constants.DEFAULT_ALERT_HEIGHT);
    }

    protected void showAlertBox(String message, AlertKind kind, double prefWidth, double prefHeight){
        Alert alert = new Alert(Alert.AlertType.NONE, message, new ButtonType("OK"));
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResource(kind.getUrl()).toExternalForm()));
        loadAlertStylesheets(alert);
        alert.getButtonTypes().set(0, new ButtonType("OK", ButtonBar.ButtonData.LEFT));
        alert.setTitle(kind.getTitle());
        alert.getDialogPane().setPrefSize(prefWidth, prefHeight);
        centerAlertBox(alert);
        alert.showAndWait();
    }

    private void centerAlertBox(Alert alert) {
        alert.setX(App.getStage().getX() + App.getStage().getWidth() / 2 - alert.getDialogPane().getPrefWidth() / 2);
        alert.setY(App.getStage().getY() + App.getStage().getHeight() / 2 - alert.getDialogPane().getPrefHeight() / 2);
    }

    private void loadAlertStylesheets(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());
    }

    protected abstract void validateForEmptyFields();
}
