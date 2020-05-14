package com.radsoltan.controllers;

import com.radsoltan.App;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Controller {





    protected void showAlertBox(String message, AlertKind kind, double prefWidth, double prefHeight){
        Alert alert = new Alert(Alert.AlertType.NONE, message, new ButtonType("OK"));
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResource(kind.getUrl()).toExternalForm()));
        loadAlertStylesheets(alert);
        alert.getButtonTypes().set(0, new ButtonType("OK", ButtonBar.ButtonData.LEFT));
        alert.setTitle(kind.getTitle());
        alert.getDialogPane().setPrefSize(250, 100);
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

}
