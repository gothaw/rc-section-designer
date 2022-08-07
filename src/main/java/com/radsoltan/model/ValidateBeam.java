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

    private List<String> validationMessages;

    /**
     * @param geometry
     * @param beamReinforcement
     * @param designParameters
     */
    public ValidateBeam(Geometry geometry, BeamReinforcement beamReinforcement, DesignParameters designParameters) {
        this.validationMessages = new ArrayList<>();
        setValidationMessagesForSpacings(geometry, beamReinforcement, designParameters);
        setValidationMessagesForBeamThickness(geometry, beamReinforcement, designParameters);
    }

    /**
     * @param beamReinforcement
     * @param designParameters
     */
    private void setValidationMessagesForSpacings(Geometry geometry, BeamReinforcement beamReinforcement, DesignParameters designParameters) {
        List<List<Integer>> topDiameters = beamReinforcement.getTopDiameters();
        List<List<Integer>> bottomDiameters = beamReinforcement.getBottomDiameters();
        List<Integer> topClearSpacings = beamReinforcement.getTopVerticalSpacings();
        List<Integer> bottomClearSpacings = beamReinforcement.getBottomVerticalSpacings();
        int width = geometry.getWidth();
        int aggregateSize = designParameters.getAggregateSize();
        int nominalCoverSides = designParameters.getNominalCoverSides();
        int shearLinksDiameter = beamReinforcement.getShearLinks().getDiameter();

        validationMessages.addAll(getValidationMessagesForHorizontalSpacings(Constants.BEAM_TOP_FACE, width, aggregateSize, nominalCoverSides, shearLinksDiameter, topDiameters));
        validationMessages.addAll(getValidationMessagesForHorizontalSpacings(Constants.BEAM_BOTTOM_FACE, width, aggregateSize, nominalCoverSides, shearLinksDiameter, bottomDiameters));
        validationMessages.addAll(getValidationMessagesForVerticalSpacings(Constants.BEAM_TOP_FACE, aggregateSize, topClearSpacings, topDiameters));
        validationMessages.addAll(getValidationMessagesForVerticalSpacings(Constants.BEAM_BOTTOM_FACE, aggregateSize, bottomClearSpacings, bottomDiameters));
    }

    /**
     *
     *
     * @param face
     * @param width
     * @param diameters
     * @param aggregateSize
     * @return
     */
    private List<String> getValidationMessagesForHorizontalSpacings(String face,
                                                                    int width,
                                                                    int aggregateSize,
                                                                    int nominalCoverSides,
                                                                    int shearLinksDiameter,
                                                                    List<List<Integer>> diameters) {
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



        return List.of("");
    }

    /**
     * @param face
     * @param clearVerticalSpacings
     * @return
     */
    private List<String> getValidationMessagesForVerticalSpacings(String face, int aggregateSize, List<Integer> clearVerticalSpacings, List<List<Integer>> diameters) {
        List<String> validationMessages = new ArrayList<>();
        String location = face.equals(Constants.BEAM_TOP_FACE) ? "top" : "bottom";

        IntStream.range(0, diameters.size()).forEach(i -> {
            List<Integer> rowDiameters = diameters.get(i);

            IntStream.range(1, rowDiameters.size()).forEach(j -> {
                int minSpacing = IntStream.of(20, aggregateSize + 5, rowDiameters.get(j - 1), rowDiameters.get(j))
                        .max()
                        .orElse(0);

                if (clearVerticalSpacings.get(i) < minSpacing) {
                    validationMessages.add(String.format(
                            "Vertical spacing between %s and %s %s row is less than minimum required - %d mm.",
                            Constants.ORDINAL_LABELS.get(j - 1), Constants.ORDINAL_LABELS.get(j), location.toLowerCase(), minSpacing));
                }

            });
        });

        return validationMessages;
    }

    private void setValidationMessagesForBeamThickness(Geometry geometry,
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

        int minimumSpacingBetweenTopAndBottomLayers = IntStream.of(
                20,
                designParameters.getAggregateSize() + 5,
                maxTopDiameters.get(maxTopDiameters.size() - 1),
                maxBottomDiameters.get(maxBottomDiameters.size() - 1))
                .max().orElse(0);

        int shearLinksDiameter = beamReinforcement.getShearLinks().getDiameter();

        int minimumBeamThickness = designParameters.getNominalCoverBottom() + 2 * shearLinksDiameter + sumOfReinforcementZoneHeights + sumOfClearSpacings + minimumSpacingBetweenTopAndBottomLayers + designParameters.getNominalCoverTop();

        if (beamDepth < minimumBeamThickness) {
            validationMessages.add(String.format("Invalid beam thickness. Minimum beam depth: %d mm.", minimumBeamThickness));
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
