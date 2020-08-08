package com.radsoltan.util;

import com.radsoltan.model.DesignParameters;
import com.radsoltan.model.reinforcement.SlabReinforcement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Validate {

    public static List<String> getValidateMessagesForSlabReinforcementHorizontalAndVerticalSpacings(SlabReinforcement slabReinforcement, DesignParameters designParameters) {
        List<String> validationMessages = new ArrayList<>();
        List<Integer> topDiameters = slabReinforcement.getTopDiameters();
        List<Integer> additionalTopDiameters = slabReinforcement.getAdditionalTopDiameters();
        List<Integer> topSpacings = slabReinforcement.getTopSpacings();
        List<Integer> topVerticalSpacings = slabReinforcement.getTopVerticalSpacings();
        List<Integer> bottomDiameters = slabReinforcement.getBottomDiameters();
        List<Integer> additionalBottomDiameters = slabReinforcement.getAdditionalBottomDiameters();
        List<Integer> bottomSpacings = slabReinforcement.getBottomSpacings();
        List<Integer> bottomVerticalSpacings = slabReinforcement.getBottomVerticalSpacings();
        int aggregateSize = designParameters.getAggregateSize();

        validationMessages.addAll(getValidationMessagesForSlabReinforcementHorizontalSpacings("top", topDiameters, additionalTopDiameters, topSpacings, aggregateSize));
        validationMessages.addAll(getValidationMessagesForSlabReinforcementHorizontalSpacings("bottom", bottomDiameters, additionalBottomDiameters, bottomSpacings, aggregateSize));
        validationMessages.addAll(getValidateMessagesForSlabReinforcementVerticalSpacings("top", topDiameters, additionalTopDiameters, topVerticalSpacings, aggregateSize));
        validationMessages.addAll(getValidateMessagesForSlabReinforcementVerticalSpacings("bottom", bottomDiameters, additionalBottomDiameters, bottomVerticalSpacings, aggregateSize));

        return validationMessages;
    }

    public static List<String> getValidationMessagesForSlabReinforcementHorizontalSpacings(String location, List<Integer> diameters, List<Integer> additionalDiameters,
                                                                                           List<Integer> spacings, int aggregateSize) {
        List<String> validationMessages = new ArrayList<>();

        for (int i = 0; i < diameters.size(); i++) {
            int minSpacing = IntStream.of(20, aggregateSize + 5, diameters.get(i), additionalDiameters.get(i))
                    .max()
                    .orElse(0);

            double clearSpacing = (additionalDiameters.get(i) == 0) ?
                    spacings.get(i) - diameters.get(i) :
                    spacings.get(i) - 0.5 * diameters.get(i) - 0.5 * additionalDiameters.get(i);

            if (clearSpacing <= minSpacing) {
                validationMessages.add(String.format("Reinforcement spacing for %s %s layer is less than minimum required - %d mm.", Constants.LAYERS_ORDINAL_LABELS.get(i), location, minSpacing));
            }
        }

        return validationMessages;
    }

    public static List<String> getValidateMessagesForSlabReinforcementVerticalSpacings(String location, List<Integer> diameters, List<Integer> additionalDiameters,
                                                                           List<Integer> verticalSpacings, int aggregateSize) {
        List<String> validationMessages = new ArrayList<>();
        for (int i = 1; i < diameters.size(); i++) {
            int minSpacing = IntStream.of(20, aggregateSize + 5, diameters.get(i - 1), diameters.get(i), additionalDiameters.get(i - 1), additionalDiameters.get(i))
                    .max()
                    .orElse(0);
            if (verticalSpacings.get(i - 1) <= minSpacing) {
                validationMessages.add(String.format("%s vertical spacing between %s and %s layer is less than minimum required spacing - %d mm.",
                        Utility.capitalize(location), Constants.LAYERS_ORDINAL_LABELS.get(i -1), Constants.LAYERS_ORDINAL_LABELS.get(i), minSpacing));
            }
        }
        return validationMessages;
    }

    public static String getValidateMessagesForSlabDepth(DesignParameters designParameters) {
        return null;
    }

}
