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

import static org.testfx.assertions.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
class NumericalTextFieldTest {
    private TextField textField;
    private NumericalTextField numericalTextField;

    @Start
    private void start(Stage primaryStage) {
        VBox vBox = new VBox();
        textField = new TextField();
        textField.setPromptText("Text field");
        numericalTextField = new NumericalTextField();
        numericalTextField.setPromptText("Numerical text field");
        vBox.getChildren().addAll(textField, numericalTextField);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(25.0));
        vBox.setSpacing(15.0);
        primaryStage.setScene(new Scene(vBox, 200, 100));
        primaryStage.show();
    }

    @Test
    void typingIntegerIsAllowed(FxRobot robot) {
        robot.clickOn(numericalTextField).write("423");
        assertThat(numericalTextField).hasText("423");
    }

    @Test
    void typingLargeNumberDoesNotAddThousandsSeparatorWhenUserClicksOtherNode(FxRobot robot) {
        robot.clickOn(numericalTextField).write("1000000000000.05");
        robot.clickOn(textField);
        assertThat(numericalTextField).hasText("1000000000000.05");
    }

    @Test
    void typingNegativeIntegerIsAllowed(FxRobot robot) {
        robot.clickOn(numericalTextField).write("-2354");
        assertThat(numericalTextField).hasText("-2354");
    }

    @Test
    void typingDecimalNumberIsAllowed(FxRobot robot) {
        robot.clickOn(numericalTextField).write("-2354.034");
        assertThat(numericalTextField).hasText("-2354.034");
    }

    @Test
    void decimalNumbersHavePrecisionOfThreeDecimalPlacesWhenUserClicksOtherNode(FxRobot robot) {
        robot.clickOn(numericalTextField).write("12.4367");
        robot.clickOn(textField);
        assertThat(numericalTextField).hasText("12.437");
    }

    @Test
    void typingOtherCharactersIsNotAllowed(FxRobot robot) {
        robot.clickOn(numericalTextField).write("dasj2kxm./()1");
        assertThat(numericalTextField).hasText("2.1");
    }

    @Test
    void typingMoreThanOneMinusSignIsNotAllowed(FxRobot robot) {
        robot.clickOn(numericalTextField).write("--2-1--");
        assertThat(numericalTextField).hasText("-21");
    }

    @Test
    void typingMoreThanOneDecimalSignIsNotAllowed(FxRobot robot) {
        robot.clickOn(numericalTextField).write("0.3.4.2.");
        assertThat(numericalTextField).hasText("0.342");
    }

    @Test
    void zeroIsAutomaticallyAddedBeforeDecimalFractionsWhenUserClicksOtherNode(FxRobot robot) {
        robot.clickOn(numericalTextField).write(".25");
        robot.clickOn(textField);
        assertThat(numericalTextField).hasText("0.25");
    }

    @Test
    void decimalPointIsRemovedWhenNoDigitsAreEnteredAfterAndWhenUserClicksOtherNode(FxRobot robot) {
        robot.clickOn(numericalTextField).write("33.vfd-");
        robot.clickOn(textField);
        assertThat(numericalTextField).hasText("33");
    }

    @Test
    void typingMinusSignInInvalidLocationIsNotAllowed(FxRobot robot) {
        robot.clickOn(numericalTextField).write("3-4");
        assertThat(numericalTextField).hasText("34");
    }

    @Test
    void pastingNumbersIsAllowed(FxRobot robot) {
        robot.clickOn(textField).write("-21.409");
        numericalTextField.setText(textField.getText());
        assertThat(numericalTextField).hasText("-21.409");
    }

    @Test
    void pastingInvalidNumbersIsNotAllowed(FxRobot robot) {
        robot.clickOn(textField).write("--21.405");
        numericalTextField.setText(textField.getText());
        assertThat(numericalTextField).hasText("");
    }

    @Test
    void numberIsTrimmedWhenPastingIfInvalidCharactersAreEncountered(FxRobot robot) {
        robot.clickOn(textField).write("-1.a43");
        numericalTextField.setText(textField.getText());
        assertThat(numericalTextField).hasText("-1");
    }

    @Test
    void pastingOtherCharactersIsNotAllowed(FxRobot robot) {
        robot.clickOn(textField).write("kxlsa9-{}/.x");
        numericalTextField.setText(textField.getText());
        assertThat(numericalTextField).hasText("");
    }

    @Test
    void lastValidEntryIsRememberedWhenInvalidCharactersArePasted(FxRobot robot) {
        robot.clickOn(numericalTextField).write("3.14");
        robot.clickOn(textField).write("-sa23.1");
        numericalTextField.setText(textField.getText());
        assertThat(numericalTextField).hasText("3.14");
    }

    @Test
    void editingNumberIsAllowed(FxRobot robot) {
        robot.clickOn(numericalTextField).write("312");
        robot.clickOn(textField);
        robot.moveTo(numericalTextField, Pos.CENTER_LEFT, Point2D.ZERO, Motion.DIRECT).clickOn(MouseButton.PRIMARY).write("-1");
        robot.clickOn(textField);
        robot.moveTo(numericalTextField, Pos.CENTER_LEFT, new Point2D(30,0), Motion.DIRECT).clickOn(MouseButton.PRIMARY).write(".31");
        assertThat(numericalTextField).hasText("-131.312");
    }

    @Test
    void addingMinusSignInInvalidLocationIsNotAllowed(FxRobot robot) {
        robot.clickOn(numericalTextField).write("312");
        robot.clickOn(textField);
        robot.moveTo(numericalTextField, Pos.CENTER_LEFT, new Point2D(20,0), Motion.DIRECT).clickOn(MouseButton.PRIMARY).write("-");
        assertThat(numericalTextField).hasText("312");
    }
}