package com.radsoltan.components;

import javafx.scene.control.TextFormatter;
import javafx.util.converter.NumberStringConverter;

import java.text.DecimalFormat;

public class PositiveIntegerField extends NumericalTextField {

    public PositiveIntegerField() {
        DecimalFormat format = new DecimalFormat();
        format.setGroupingUsed(false);
        this.setTextFormatter(new TextFormatter<>(new NumberStringConverter(format)));
        this.pattern = "\\d*";
    }
}
