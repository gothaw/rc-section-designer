package com.radsoltan.model;

import com.radsoltan.model.reinforcement.SlabReinforcement;
import com.radsoltan.constants.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * ValidateSlab class that implements Validation. It checks if a slab is geometrically valid.
 * It also checks if reinforcement spacings satisfy minimum rebar spacings required by Eurocode 2.
 */
public class ValidateSlab implements Validation {

    private final List<String> validationMessages;

    /**
     * Constructor. It invokes method to validate both horizontal and vertical bar spacings and method to validate slab thickness.
     * These methods add messages to the validationMessages list if some of the spacings or slab geometry is invalid.
     *
     * @param slabThickness     slab thickness in mm as an integer
     * @param slabReinforcement slab reinforcement defined using SlabReinforcement object
     * @param designParameters  design parameters used in project defined in DesignParameters object
     */
    public ValidateSlab(int slabThickness, SlabReinforcement slabReinforcement, DesignParameters designParameters) {
        this.validationMessages = new ArrayList<>();
        setValidateMessagesForSlabReinforcementHorizontalAndVerticalSpacings(slabReinforcement, designParameters);
        setValidateMessagesForSlabThickness(slabThickness, slabReinforcement, designParameters);
    }

    /**
     * Method that sets validation messages for horizontal bar spacings and vertical bar spacings between layers.
     * It invokes getValidationMessagesForSlabReinforcementHorizontalSpacings and getValidateMessagesForSlabReinforcementVerticalSpacings on top and bottom reinforcement layers.
     * It adds all validation messages to the validationMessages field.
     *
     * @param slabReinforcement slab reinforcement defined using SlabReinforcement object
     * @param designParameters  design parameters used in project defined in DesignParameters object
     */
    private void setValidateMessagesForSlabReinforcementHorizontalAndVerticalSpacings(SlabReinforcement slabReinforcement, DesignParameters designParameters) {
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

    /**
     * Gets validation messages for clear horizontal spacings between reinforcement bars in each layer.
     * The method calculates the clear spacing based on spacing between bar centers. It than checks it against minimum spacing required by Eurocode 2.
     * The minimum spacing is maximum of 20 mm, aggregate size + 5mm or bar diameter.
     * If spacing is less than minimum spacing a String message is added to the list that is returned.
     *
     * @param location            layer location "top" or "bottom"
     * @param diameters           main bar diameters in subsequent layers as a list
     * @param additionalDiameters additional bar diameters in subsequent layers as a list
     * @param spacings            spacings between main bar centers in subsequent layers as a list
     * @param aggregateSize       aggregate size in mm
     * @return List of validation messages for horizontal spacing
     */
    private List<String> getValidationMessagesForSlabReinforcementHorizontalSpacings(String location, List<Integer> diameters, List<Integer> additionalDiameters,
                                                                                     List<Integer> spacings, int aggregateSize) {
        List<String> validationMessages = new ArrayList<>();

        for (int i = 0; i < diameters.size(); i++) {
            int minSpacing = IntStream.of(20, aggregateSize + 5, diameters.get(i), additionalDiameters.get(i))
                    .max()
                    .orElse(0);

            double clearSpacing = (additionalDiameters.get(i) == 0) ?
                    spacings.get(i) - diameters.get(i) :
                    0.5 * spacings.get(i) - 0.5 * diameters.get(i) - 0.5 * additionalDiameters.get(i);

            if (clearSpacing < minSpacing) {
                validationMessages.add(String.format("Reinforcement spacing for the %s %s layer is less than minimum required - %d mm.", Constants.LAYERS_ORDINAL_LABELS.get(i), location.toLowerCase(), minSpacing));
            }
        }

        return validationMessages;
    }

    /**
     * Gets validation messages for clear vertical spacings between reinforcement layers.
     * The method checks if clear vertical spacings are greater than minimum spacing required by Eurocode 2.
     * The minimum spacing is maximum of 20 mm, aggregate size + 5mm or bar diameter.
     * If spacing is less than minimum spacing a String message is added to the list that is returned.
     *
     * @param location            layer location "top" or "bottom"
     * @param diameters           main bar diameters in subsequent layers as a list
     * @param additionalDiameters additional bar diameters in subsequent layers as a list
     * @param verticalSpacings    clear vertical spacing between layers
     * @param aggregateSize       aggregate size in mm
     * @return List of validation messages for vertical spacing
     */
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

    /**
     * Method that sets validation message for slab thickness.
     * It checks whether slab defined in Geometry object is thick enough to accommodate vertical spacing between bars, bar diameters, nominal cover etc.
     * If slab is not thick enough, a message is added to the validationMessages field to inform the user.
     *
     * @param slabThickness     slab thickness in mm as an integer
     * @param slabReinforcement slab reinforcement defined using SlabReinforcement object
     * @param designParameters  design parameters used in project defined in DesignParameters object
     */
    private void setValidateMessagesForSlabThickness(int slabThickness, SlabReinforcement slabReinforcement, DesignParameters designParameters) {
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

    /**
     * Getter for validationMessages.
     *
     * @return List of validation messages
     */
    public List<String> getValidationMessages() {
        return validationMessages;
    }
}
