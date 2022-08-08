package com.radsoltan.model;

import com.radsoltan.constants.Constants;
import com.radsoltan.model.geometry.Geometry;
import com.radsoltan.model.reinforcement.BeamReinforcement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * ValidateBeam class that implements Validation. It checks if beam geometry is valid.
 * It also checks if reinforcement spacings satisfy minimum rebar spacings required by Eurocode 2.
 */
public class ValidateBeam implements Validation {

    private final List<String> validationMessages;

    /**
     * Constructor. It invokes method to validate both horizontal and vertical bar spacings and method to validate beam depth.
     * These methods add messages to the validationMessages list if some of the spacings or beam geometry is invalid.
     *
     * @param geometry beam geometry object
     * @param beamReinforcement beam reinforcement object
     * @param designParameters design parameters object
     */
    public ValidateBeam(Geometry geometry, BeamReinforcement beamReinforcement, DesignParameters designParameters) {
        this.validationMessages = new ArrayList<>();
        setValidationMessagesForSpacings(geometry, beamReinforcement, designParameters);
        setValidationMessagesForBeamDepth(geometry, beamReinforcement, designParameters);
    }

    /**
     * Method that sets validation messages for horizontal bar spacings and vertical bar spacings between rows.
     * It invokes getValidationMessagesForVerticalSpacings and getValidationMessagesForHorizontalSpacings on top and bottom reinforcement rows.
     * It adds all validation messages to the validationMessages field.
     *
     * @param geometry          beam geometry
     * @param beamReinforcement beam reinforcement object
     * @param designParameters  design parameters object
     */
    private void setValidationMessagesForSpacings(Geometry geometry, BeamReinforcement beamReinforcement, DesignParameters designParameters) {
        List<List<Integer>> topDiameters = beamReinforcement.getTopDiameters();
        List<List<Integer>> bottomDiameters = beamReinforcement.getBottomDiameters();
        List<Integer> topVerticalSpacings = beamReinforcement.getTopVerticalSpacings();
        List<Integer> bottomVerticalSpacings = beamReinforcement.getBottomVerticalSpacings();
        int width = geometry.getWidth();
        int aggregateSize = designParameters.getAggregateSize();
        int nominalCoverSides = designParameters.getNominalCoverSides();
        int shearLinksDiameter = beamReinforcement.getShearLinks().getDiameter();

        validationMessages.addAll(getValidationMessagesForHorizontalSpacings(Constants.BEAM_TOP_FACE, width, aggregateSize, nominalCoverSides, shearLinksDiameter, topDiameters));
        validationMessages.addAll(getValidationMessagesForHorizontalSpacings(Constants.BEAM_BOTTOM_FACE, width, aggregateSize, nominalCoverSides, shearLinksDiameter, bottomDiameters));
        if (topVerticalSpacings.size() != 0) {
            validationMessages.addAll(getValidationMessagesForVerticalSpacings(Constants.BEAM_TOP_FACE, aggregateSize, topVerticalSpacings, topDiameters));
        }
        if (bottomVerticalSpacings.size() != 0) {
            validationMessages.addAll(getValidationMessagesForVerticalSpacings(Constants.BEAM_BOTTOM_FACE, aggregateSize, bottomVerticalSpacings, bottomDiameters));
        }
    }

    /**
     * Gets validation messages for clear horizontal spacings between reinforcement bars in each row.
     * The method calculates the clear spacing based on reinforcement, nominal cover and beam geometry. It than checks it against minimum spacing required by Eurocode 2.
     * The minimum spacing is maximum of 20 mm, aggregate size + 5mm or bar diameter.
     * If spacing is less than minimum spacing a String message is added to the list that is returned.
     *
     * @param face               beam face top or bottom
     * @param width              beam width in mm
     * @param aggregateSize      aggregate size in mm
     * @param nominalCoverSides  nominal cover for beam side in mm
     * @param shearLinksDiameter shear links diameter in mm
     * @param diameters          main bar diameters for given beam face
     * @return List of validation messages for horizontal spacings
     */
    private List<String> getValidationMessagesForHorizontalSpacings(String face,
                                                                    int width,
                                                                    int aggregateSize,
                                                                    int nominalCoverSides,
                                                                    int shearLinksDiameter,
                                                                    List<List<Integer>> diameters) {
        List<String> validationMessages = new ArrayList<>();
        String location = face.equals(Constants.BEAM_TOP_FACE) ? "top" : "bottom";
        double availableWidth = width - 2 * shearLinksDiameter - 2 * nominalCoverSides;

        IntStream.range(0, diameters.size()).forEach(i -> {
            List<Integer> rowDiameters = diameters.get(i);
            int numberOfBars = rowDiameters.size();
            int sumOfDiameters = rowDiameters.stream().reduce(Integer::sum).orElse(0);
            int maxDiameterInRow = Collections.max(rowDiameters);

            // clear spacing between main bars
            double clearSpacing = (availableWidth - sumOfDiameters) / (numberOfBars - 1);

            int minSpacing = IntStream.of(20, aggregateSize + 5, maxDiameterInRow)
                    .max()
                    .orElse(0);

            if (clearSpacing < minSpacing) {
                validationMessages.add(String.format(
                        "Horizontal spacing between bars in %s %s row is less than minimum required - %d mm.",
                        Constants.ORDINAL_LABELS.get(i), location.toLowerCase(), minSpacing));
            }
        });


        return validationMessages;
    }

    /**
     * Gets validation messages for clear vertical spacings between reinforcement rows.
     * The method checks if clear vertical spacings are greater than minimum spacing required by Eurocode 2.
     * The minimum spacing is maximum of 20 mm, aggregate size + 5mm or bar diameter.
     * If spacing is less than minimum spacing a String message is added to the list that is returned.
     *
     * @param face                  beam face top or bottom
     * @param aggregateSize         aggregate size in mm
     * @param clearVerticalSpacings clear vertical spacings between reinforcement rows for given beam face
     * @param diameters             main bar diameters for given beam face
     * @return List of validation messages for vertical spacings
     */
    private List<String> getValidationMessagesForVerticalSpacings(String face, int aggregateSize, List<Integer> clearVerticalSpacings, List<List<Integer>> diameters) {
        List<String> validationMessages = new ArrayList<>();
        String location = face.equals(Constants.BEAM_TOP_FACE) ? "top" : "bottom";

        IntStream.range(1, diameters.size()).forEach(i -> {
            List<Integer> diametersInPreviousRow = diameters.get(i - 1);
            List<Integer> diametersInRow = diameters.get(i);
            int maxDiameterInPreviousRow = Collections.max(diametersInPreviousRow);
            int maxDiameterInRow = Collections.max(diametersInRow);

            int minSpacing = IntStream.of(20, aggregateSize + 5, maxDiameterInPreviousRow, maxDiameterInRow)
                    .max()
                    .orElse(0);

            if (clearVerticalSpacings.get(i - 1) < minSpacing) {
                validationMessages.add(String.format(
                        "Vertical spacing between %s and %s %s row is less than minimum required - %d mm.",
                        Constants.ORDINAL_LABELS.get(i - 1), Constants.ORDINAL_LABELS.get(i), location.toLowerCase(), minSpacing));
            }
        });

        return validationMessages;
    }

    /**
     * Method that sets validation message for beam depth.
     * It checks whether beam defined in Geometry object is deep enough to accommodate vertical spacing between bars, bar diameters, nominal cover etc.
     * If beam is not deep enough, a message is added to the validationMessages field to inform the user.
     *
     * @param geometry          beam geometry
     * @param beamReinforcement beam reinforcement defined using BeamReinforcement class
     * @param designParameters  design parameters used in project defined in DesignParameters class
     */
    private void setValidationMessagesForBeamDepth(Geometry geometry,
                                                   BeamReinforcement beamReinforcement,
                                                   DesignParameters designParameters) {
        int beamDepth = geometry.getDepth();

        List<Integer> clearSpacings = new ArrayList<>();
        clearSpacings.addAll(beamReinforcement.getTopVerticalSpacings());
        clearSpacings.addAll(beamReinforcement.getBottomVerticalSpacings());

        List<List<Integer>> topDiameters = beamReinforcement.getTopDiameters();
        List<List<Integer>> bottomDiameters = beamReinforcement.getBottomDiameters();
        List<Integer> maxTopDiameters = topDiameters.stream().map(Collections::max).collect(Collectors.toList());
        List<Integer> maxBottomDiameters = bottomDiameters.stream().map(Collections::max).collect(Collectors.toList());
        List<Integer> diameters = new ArrayList<>();
        diameters.addAll(maxTopDiameters);
        diameters.addAll(maxBottomDiameters);


        int sumOfReinforcementZoneHeights = diameters
                .stream()
                .reduce(Integer::sum)
                .orElse(0);

        int sumOfClearSpacings = clearSpacings
                .stream()
                .reduce(Integer::sum)
                .orElse(0);

        int minimumSpacingBetweenTopAndBottomRows = IntStream.of(
                20,
                designParameters.getAggregateSize() + 5,
                maxTopDiameters.get(maxTopDiameters.size() - 1),
                maxBottomDiameters.get(maxBottomDiameters.size() - 1))
                .max().orElse(0);

        int shearLinksDiameter = beamReinforcement.getShearLinks().getDiameter();

        int minimumBeamThickness = designParameters.getNominalCoverBottom() + 2 * shearLinksDiameter + sumOfReinforcementZoneHeights + sumOfClearSpacings + minimumSpacingBetweenTopAndBottomRows + designParameters.getNominalCoverTop();

        if (beamDepth < minimumBeamThickness) {
            validationMessages.add(String.format("Invalid beam thickness for given reinforcement. Minimum beam depth: %d mm.", minimumBeamThickness));
        }
    }

    /**
     * Getter for validation messages.
     *
     * @return List of validation messages
     */
    public List<String> getValidationMessages() {
        return validationMessages;
    }
}
