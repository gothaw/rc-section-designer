package com.radsoltan.components;

import javafx.scene.control.TextFormatter;
import javafx.util.converter.NumberStringConverter;

import java.text.DecimalFormat;

public class PositiveIntegerField extends NumericalTextField {

    /**
     * Constructor. Sets text formatter to use NumberStringConverter.
     * Uses decimal format without thousands separator.
     * Also sets a regex pattern to find integer numbers.
     */
    public PositiveIntegerField() {
        DecimalFormat format = new DecimalFormat();
        format.setGroupingUsed(false);
        this.setTextFormatter(new TextFormatter<>(new NumberStringConverter(format)));
        this.pattern = "\\d*";
    }
}
