package com.radsoltan.components;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.NumberStringConverter;

public class NumericalTextField extends TextField {

    private final String pattern;

    public NumericalTextField() {
        this.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));
        this.pattern = "-?(\\d*|0)?(\\.\\d*)?";
    }

    @Override
    public void replaceText(int start, int end, String text) {
        if (start == end){
            if (validateTextInput(text)) {
                super.replaceText(start, end, text);
           }
        } else {
            super.replaceText(start, end, text);
        }
    }

    @Override
    public void replaceSelection(String replacement) {
        if(validateReplacement(replacement)){
            super.replaceSelection(replacement);
        }
    }

    @Override
    public void paste() {
        super.paste();
    }

    private boolean validateTextInput(String input) {
        String newText = this.getText() + input;
        return newText.matches(pattern);
    }

    private boolean validateReplacement(String replacement) {
        return replacement.matches(pattern);
    }
}
