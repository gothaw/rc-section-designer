package com.radsoltan.model;

import com.radsoltan.App;
import com.radsoltan.controllers.AlertKind;
import com.radsoltan.model.DesignParameters;
import com.radsoltan.model.reinforcement.SlabReinforcement;
import com.radsoltan.util.Constants;
import com.radsoltan.util.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ValidateSlab implements Validation {

    private final List<String> validationMessages;

    public ValidateSlab(int slabThickness, SlabReinforcement slabReinforcement, DesignParameters designParameters) {
        this.validationMessages = new ArrayList<>();
        setValidateMessagesForSlabReinforcementHorizontalAndVerticalSpacings(slabReinforcement, designParameters);
        setValidateMessagesForSlabThickness(slabThickness, slabReinforcement, designParameters);
    }

    public void setValidateMessagesForSlabReinforcementHorizontalAndVerticalSpacings(SlabReinforcement slabReinforcement, DesignParameters designParameters) {
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
    }

    private List<String> getValidationMessagesForSlabReinforcementHorizontalSpacings(String location, List<Integer> diameters, List<Integer> additionalDiameters,
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
                validationMessages.add(String.format("Reinforcement spacing for %s %s layer is less than minimum required - %d mm.", Constants.LAYERS_ORDINAL_LABELS.get(i), location.toLowerCase(), minSpacing));
            }
        }

        return validationMessages;
    }

    private List<String> getValidateMessagesForSlabReinforcementVerticalSpacings(String location, List<Integer> diameters, List<Integer> additionalDiameters,
                                                                                 List<Integer> verticalSpacings, int aggregateSize) {
        List<String> validationMessages = new ArrayList<>();
        for (int i = 1; i < diameters.size(); i++) {
            int minSpacing = IntStream.of(20, aggregateSize + 5, diameters.get(i - 1), diameters.get(i), additionalDiameters.get(i - 1), additionalDiameters.get(i))
                    .max()
                    .orElse(0);
            if (verticalSpacings.get(i - 1) < minSpacing) {
                validationMessages.add(String.format("Vertical spacing between %s and %s %s layer is less than minimum required - %d mm.",
                        Constants.LAYERS_ORDINAL_LABELS.get(i - 1), Constants.LAYERS_ORDINAL_LABELS.get(i), location.toLowerCase(), minSpacing));
            }
        }
        return validationMessages;
    }

    public void setValidateMessagesForSlabThickness(int slabThickness, SlabReinforcement slabReinforcement, DesignParameters designParameters) {
        List<Integer> clearSpacings = new ArrayList<>();
        clearSpacings.addAll(slabReinforcement.getTopVerticalSpacings());
        clearSpacings.addAll(slabReinforcement.getBottomVerticalSpacings());
        List<Integer> topDiameters = slabReinforcement.getTopDiameters();
        List<Integer> additionalTopDiameters = slabReinforcement.getAdditionalTopDiameters();
        List<Integer> bottomDiameters = slabReinforcement.getBottomDiameters();
        List<Integer> additionalBottomDiameters = slabReinforcement.getAdditionalBottomDiameters();
        List<Integer> diameters = new ArrayList<>();
        diameters.addAll(topDiameters);
        diameters.addAll(bottomDiameters);
        List<Integer> additionalDiameters = new ArrayList<>();
        additionalDiameters.addAll(additionalTopDiameters);
        additionalDiameters.addAll(additionalBottomDiameters);

        List<Integer> reinforcementZonesHeights = IntStream.range(0, diameters.size())
                .mapToObj(i -> Math.max(diameters.get(i), additionalDiameters.get(i)))
                .collect(Collectors.toList());

        int sumOfReinforcementZoneHeights = reinforcementZonesHeights.stream().reduce(Integer::sum).orElse(0);

        int sumOfClearSpacings = clearSpacings.stream()
                .reduce(Integer::sum)
                .orElse(0);

        int minimumSpacingBetweenTopAndBottomLayers = IntStream.of(20, designParameters.getAggregateSize() + 5,
                topDiameters.get(topDiameters.size() - 1), additionalTopDiameters.get(additionalTopDiameters.size() - 1),
                bottomDiameters.get(bottomDiameters.size() - 1), additionalBottomDiameters.get(additionalBottomDiameters.size() - 1))
                .max().orElse(0);

        int minimumSlabThickness = designParameters.getNominalCoverBottom() + sumOfReinforcementZoneHeights + sumOfClearSpacings + minimumSpacingBetweenTopAndBottomLayers + designParameters.getNominalCoverTop();

        if (slabThickness < minimumSlabThickness) {
            validationMessages.add(String.format("Invalid slab thickness. Minimum slab thickness: %d mm.", minimumSlabThickness));
        }
    }

    public List<String> getValidationMessages() {
        return validationMessages;
    }
}
