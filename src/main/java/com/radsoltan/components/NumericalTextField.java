package com.radsoltan.components;

import javafx.scene.control.IndexRange;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.NumberStringConverter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Text Input FXML component that allows for entering valid floating-point values.
 * It extends TextField.
 */
public class NumericalTextField extends TextField {

    private final String pattern = "-?(\\d*|0)?(\\.\\d*)?";

    /**
     * Constructor. Sets text formatter to use NumberStringConverter.
     * Also sets a regex pattern to find floating point numbers.
     */
    public NumericalTextField() {
        DecimalFormat format = new DecimalFormat();
        format.setGroupingUsed(false);
        this.setTextFormatter(new TextFormatter<>(new NumberStringConverter(format)));
    }

    /**
     * Method override.
     * @param start
     * @param end
     * @param text
     */
    @Override
    public void replaceText(int start, int end, String text) {
        if (start == end){
            if (validateTextInput(text)) {
                super.replaceText(start, end, text);
           }
        } else {
            super.replaceText(start, end, text);
        }
        System.out.println(this.getText());
    }

    @Override
    public void replaceText(IndexRange range, String text) {
        if(validateReplacement(text)){
            super.replaceText(range, text);
        }
    }

    private boolean validateTextInput(String input) {
        String newText = this.getText() + input;
        return newText.matches(pattern);
    }

    private boolean validateReplacement(String replacement) {
        return replacement.matches(pattern);
    }
}
