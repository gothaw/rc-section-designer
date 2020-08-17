package com.radsoltan.components;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.NumberStringConverter;
import java.text.DecimalFormat;

/**
 * Text Input FXML component that allows for entering valid decimal number.
 * It extends TextField.
 */
public class NumericalTextField extends TextField {

    protected String pattern;

    /**
     * Constructor. Sets text formatter to use NumberStringConverter.
     * Uses decimal format without thousands separator.
     * Also sets a regex pattern to find decimal numbers.
     */
    public NumericalTextField() {
        DecimalFormat format = new DecimalFormat();
        format.setGroupingUsed(false);
        this.setTextFormatter(new TextFormatter<>(new NumberStringConverter(format)));
        this.pattern = "-?(\\d*|0)?(\\.\\d*)?";
    }

    /**
     * Method override. Replaces a range of characters with the given text.
     * Input validation carried out by invoking private method {@code validateTextInput}.
     * @param start The starting index in the range, inclusive.
     * @param end The ending index in the range, exclusive.
     * @param text The text that is to replace the range. This must not be null.
     */
    @Override
    public void replaceText(int start, int end, String text) {
        if (validateTextInput(start, end, text)) {
            super.replaceText(start, end, text);
        }
    }

    /**
     * Method to validate if String created after text replacement is a valid decimal number.
     * It uses StringBuilder and replace method to create a new String that is checked against {@code pattern}.
     * @param start The starting index in the range, inclusive.
     * @param end The ending index in the range, exclusive.
     * @param text The text that is to replace the range. This must not be null.
     * @return true if new text after replacement is still a valid decimal number.
     */
    private boolean validateTextInput(int start, int end, String text) {
        String currentText = this.getText();
        StringBuilder currentTextStringBuilder = new StringBuilder(currentText);
        StringBuilder newText = currentTextStringBuilder.replace(start, end, text);
        return newText.toString().matches(pattern);
    }
}
