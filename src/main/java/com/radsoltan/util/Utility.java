package com.radsoltan.util;

import java.util.List;

public class Utility {
    public static double findMaxValueInList(List<Double> list) {
        return list
                .stream()
                .max(Double::compare)
                .orElse(0.0);
    }
}
