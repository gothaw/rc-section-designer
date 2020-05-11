package com.radsoltan.components;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.robot.Motion;

import static org.testfx.assertions.api.Assertions.assertThat;

@ExtendWith(ApplicationExtension.class)
class PositiveIntegerFieldTest {
    private TextField textField;
    private PositiveIntegerField positiveIntegerField;

    @Start
    private void start(Stage primaryStage) {
        VBox vBox = new VBox();
        textField = new TextField();
        textField.setPromptText("Text field");
        positiveIntegerField = new PositiveIntegerField();
        positiveIntegerField.setPromptText("Positive Integer Field");
        vBox.getChildren().addAll(textField, positiveIntegerField);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(25.0));
        vBox.setSpacing(15.0);
        primaryStage.setScene(new Scene(vBox, 200, 100));
        primaryStage.show();
    }

    @Test
    void typingPositiveIntegerIsAllowed(FxRobot robot) {
        robot.clickOn(positiveIntegerField).write("423");
        assertThat(positiveIntegerField).hasText("423");
    }

    @Test
    void typingLargeNumberDoesNotAddThousandsSeparatorWhenUserClicksOtherNode(FxRobot robot) {
        robot.clickOn(positiveIntegerField).write("10000000000000");
        robot.clickOn(textField);
        assertThat(positiveIntegerField).hasText("10000000000000");
    }

    @Test
    void typingDecimalNumberIsNotAllowed(FxRobot robot) {
        robot.clickOn(positiveIntegerField).write("2354.034");
        assertThat(positiveIntegerField).hasText("2354034");
    }

    @Test
    void typingNegativeNumberIsNotAllowed(FxRobot robot) {
        robot.clickOn(positiveIntegerField).write("-652");
        assertThat(positiveIntegerField).hasText("652");
    }

    @Test
    void typingOtherCharactersIsNotAllowed(FxRobot robot) {
        robot.clickOn(positiveIntegerField).write("dasj2kxm./()1");
        assertThat(positiveIntegerField).hasText("21");
    }

    @Test
    void pastingNumbersIsAllowed(FxRobot robot) {
        robot.clickOn(textField).write("212");
        positiveIntegerField.setText(textField.getText());
        assertThat(positiveIntegerField).hasText("212");
    }

    @Test
    void numberIsTrimmedWhenPastingIfInvalidCharactersAreEncountered(FxRobot robot) {
        robot.clickOn(textField).write("1.a43");
        positiveIntegerField.setText(textField.getText());
        assertThat(positiveIntegerField).hasText("1");
    }

    @Test
    void pastingOtherCharactersIsNotAllowed(FxRobot robot) {
        robot.clickOn(textField).write("kxlsa9-{}/.x");
        positiveIntegerField.setText(textField.getText());
        assertThat(positiveIntegerField).hasText("");
    }

    @Test
    void lastValidEntryIsRememberedWhenInvalidCharactersArePasted(FxRobot robot) {
        robot.clickOn(positiveIntegerField).write("312");
        robot.clickOn(textField).write("-sa23.1");
        positiveIntegerField.setText(textField.getText());
        assertThat(positiveIntegerField).hasText("312");
    }

    @Test
    void editingNumberIsAllowed(FxRobot robot) {
        robot.clickOn(positiveIntegerField).write("432");
        robot.clickOn(textField);
        robot.moveTo(positiveIntegerField, Pos.CENTER_LEFT, Point2D.ZERO, Motion.DIRECT).clickOn(MouseButton.PRIMARY).write("1");
        robot.clickOn(textField);
        robot.moveTo(positiveIntegerField, Pos.CENTER_LEFT, new Point2D(30,0), Motion.DIRECT).clickOn(MouseButton.PRIMARY).write("28");
        assertThat(positiveIntegerField).hasText("143282");
    }



}