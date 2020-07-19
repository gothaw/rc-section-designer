package com.radsoltan.util;

import java.util.List;
import java.util.stream.IntStream;

public class Validate {


    // TODO: 18/07/2020 Create ValidateSlab and ValidateBeam classes that take slab or beam object and validate spacings, depth etc

    public static String getValidateMessagesForSlabReinforcementSpacings() {
        return null;
    }

    public static List<String> getValidateMessagesForSlabReinforcementVerticalSpacings(List<Integer> diameters, List<Integer> additionalDiameters, List<Integer> verticalSpacing) {
        List<String> validationMessages;
        int numberOfLayers = diameters.size();
        IntStream.range(0, numberOfLayers - 1).forEach(i -> {

        });

        return null;
    }

    public static String getValidateMessagesForSlabDepth() {
        return null;
    }

}
