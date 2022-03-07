package com.radsoltan.model.reinforcement;

import com.radsoltan.model.DesignParameters;
import com.radsoltan.model.geometry.SlabStrip;
import com.radsoltan.util.Constants;
import com.radsoltan.util.UIText;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SlabReinforcement extends Reinforcement {

    private final List<Integer> topDiameters;
    private final List<Integer> additionalTopDiameters;
    private final List<Integer> topSpacings;
    private final List<Integer> topVerticalSpacings;
    private final List<Integer> bottomDiameters;
    private final List<Integer> additionalBottomDiameters;
    private final List<Integer> bottomSpacings;
    private final List<Integer> bottomVerticalSpacings;
    private final DesignParameters designParameters;
    private final SlabStrip slabStrip;
    private final GraphicsContext graphicsContext;
    private final Color colour;
    private final double slabImageScale;

    public static final int DEFAULT_TEXT_SIZE = 10;
    public static final int DEFAULT_TEXT_OFFSET = 5;


    public SlabReinforcement(List<Integer> topDiameters,
                             List<Integer> additionalTopDiameters,
                             List<Integer> topSpacings,
                             List<Integer> topVerticalSpacings,
                             List<Integer> bottomDiameters,
                             List<Integer> additionalBottomDiameters,
                             List<Integer> bottomSpacings,
                             List<Integer> bottomVerticalSpacings) {
        this(topDiameters, additionalTopDiameters, topSpacings, topVerticalSpacings, bottomDiameters, additionalBottomDiameters, bottomSpacings, bottomVerticalSpacings, null, null, null, null, 0);
    }

    public SlabReinforcement(List<Integer> topDiameters,
                             List<Integer> additionalTopDiameters,
                             List<Integer> topSpacings,
                             List<Integer> topVerticalSpacings,
                             List<Integer> bottomDiameters,
                             List<Integer> additionalBottomDiameters,
                             List<Integer> bottomSpacings,
                             List<Integer> bottomVerticalSpacings,
                             DesignParameters designParameters,
                             SlabStrip slabStrip,
                             GraphicsContext graphicsContext,
                             Color colour,
                             double slabImageScale
    ) {
        this.topDiameters = topDiameters;
        this.additionalTopDiameters = additionalTopDiameters;
        this.topSpacings = topSpacings;
        this.topVerticalSpacings = topVerticalSpacings;
        this.bottomDiameters = bottomDiameters;
        this.additionalBottomDiameters = additionalBottomDiameters;
        this.bottomSpacings = bottomSpacings;
        this.bottomVerticalSpacings = bottomVerticalSpacings;
        this.designParameters = designParameters;
        this.slabStrip = slabStrip;
        this.graphicsContext = graphicsContext;
        this.colour = colour;
        this.slabImageScale = slabImageScale;
    }

    @Override
    public double getTotalAreaOfTopReinforcement() {
        return getAreaOfReinforcementLayers(topDiameters, additionalTopDiameters, topSpacings).stream()
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    @Override
    public double getCentroidOfTopReinforcement(int nominalCoverTop) {
        List<Double> areaOfTopLayers = getAreaOfReinforcementLayers(topDiameters, additionalTopDiameters, topSpacings);
        List<Double> firstMomentOfAreaOfTopLayers = getFirstMomentOfAreaReinforcementLayers(areaOfTopLayers, topDiameters, additionalTopDiameters, topVerticalSpacings, nominalCoverTop);

        double sumOfAreas = getTotalAreaOfTopReinforcement();
        double sumOfFirstMomentsOfArea = firstMomentOfAreaOfTopLayers.stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        return sumOfFirstMomentsOfArea / sumOfAreas;
    }

    @Override
    public double getTotalAreaOfBottomReinforcement() {
        return getAreaOfReinforcementLayers(bottomDiameters, additionalBottomDiameters, bottomSpacings).stream()
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    @Override
    public double getCentroidOfBottomReinforcement(int nominalCoverBottom) {
        List<Double> areaOfBottomLayers = getAreaOfReinforcementLayers(bottomDiameters, additionalBottomDiameters, bottomSpacings);
        List<Double> firstMomentOfAreaOfBottomLayers = getFirstMomentOfAreaReinforcementLayers(areaOfBottomLayers, bottomDiameters, additionalBottomDiameters, bottomVerticalSpacings, nominalCoverBottom);

        double sumOfAreas = getTotalAreaOfBottomReinforcement();
        double sumOfFirstMomentsOfArea = firstMomentOfAreaOfBottomLayers.stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        return sumOfFirstMomentsOfArea / sumOfAreas;
    }

    @Override
    public String getDescription() {
        String descriptionTopLayers = "Top layers:\n" + getDescriptionForReinforcementLayers(topDiameters, additionalTopDiameters, topSpacings);
        String descriptionBottomLayers = "Bottom layers:\n" + getDescriptionForReinforcementLayers(bottomDiameters, additionalBottomDiameters, bottomSpacings);
        return descriptionTopLayers + "\n" + descriptionBottomLayers;
    }

    private String getDescriptionForReinforcementLayers(List<Integer> diameters, List<Integer> additionalDiameters, List<Integer> spacings) {
        String layersDescription = "";

        for (int i = 0; i < diameters.size(); i++) {
            int diameter = diameters.get(i);
            int additionalDiameter = additionalDiameters.get(i);
            int spacing = spacings.get(i);
            String description = (additionalDiameter == 0)
                    ? String.format("%s layer: \u03c6%d@%d", Constants.LAYERS_ORDINAL_LABELS.get(i), diameter, spacing)
                    : String.format("%s layer: \u03c6%d@%d + \u03c6%d@%d", Constants.LAYERS_ORDINAL_LABELS.get(i), diameter, spacing, additionalDiameter, spacing);
            if (i < diameters.size() - 1) {
                description = description.concat(",  ");
            }
            layersDescription = layersDescription.concat(description);
        }

        return layersDescription;
    }

    public List<Double> getAreaOfReinforcementLayers(List<Integer> diameters, List<Integer> additionalDiameters, List<Integer> spacings) {

        return IntStream.range(0, diameters.size())
                .mapToObj(i -> 0.25 * Math.PI * (diameters.get(i) * diameters.get(i) + additionalDiameters.get(i) * additionalDiameters.get(i)) * 1000 / spacings.get(i))
                .collect(Collectors.toList());
    }

    public List<Integer> getMaxDiametersForEachLayer(List<Integer> diameters, List<Integer> additionalDiameters) {
        return IntStream
                .range(0, diameters.size())
                .mapToObj(i -> Math.max(diameters.get(i), additionalDiameters.get(i)))
                .collect(Collectors.toList());
    }

    // TODO: 10/06/2020 Encapsulation
    public List<Double> getDistanceFromCentreOfEachLayerToEdge(List<Integer> diameters, List<Integer> additionalDiameters, List<Integer> verticalSpacings, int nominalCover) {

        List<Integer> maxDiameters = getMaxDiametersForEachLayer(diameters, additionalDiameters);

        return IntStream
                .range(0, maxDiameters.size())
                .mapToObj(i -> nominalCover + 0.5 * maxDiameters.get(i)
                        + verticalSpacings.stream().limit(i).reduce(0, Integer::sum)
                        + maxDiameters.stream().limit(i).reduce(0, Integer::sum))
                .collect(Collectors.toList());
    }

    public List<Double> getDistanceFromTopOfTopLayersToTopEdge(int nominalCoverTop) {
        List<Double> distanceFromCentreOfEachLayer = getDistanceFromCentreOfEachLayerToEdge(topDiameters, additionalTopDiameters, topVerticalSpacings, nominalCoverTop);
        List<Integer> maxDiameters = getMaxDiametersForEachLayer(topDiameters, additionalTopDiameters);

        return IntStream
                .range(0, distanceFromCentreOfEachLayer.size())
                .mapToObj(i -> distanceFromCentreOfEachLayer.get(i) - 0.5 * maxDiameters.get(i))
                .collect(Collectors.toList());
    }

    public List<Double> getDistanceFromTopOBottomLayersToBottomEdge(int nominalCoverBottom) {
        List<Double> distanceFromCentreOfEachLayer = getDistanceFromCentreOfEachLayerToEdge(bottomDiameters, additionalBottomDiameters, bottomVerticalSpacings, nominalCoverBottom);
        List<Integer> maxDiameters = getMaxDiametersForEachLayer(bottomDiameters, additionalBottomDiameters);

        return IntStream
                .range(0, distanceFromCentreOfEachLayer.size())
                .mapToObj(i -> distanceFromCentreOfEachLayer.get(i) + 0.5 * maxDiameters.get(i))
                .collect(Collectors.toList());
    }

    public List<Double> getFirstMomentOfAreaReinforcementLayers(List<Double> areaOfReinforcementLayers, List<Integer> diameters, List<Integer> additionalDiameters, List<Integer> verticalSpacings, int nominalCover) {
        List<Double> distanceFromEachLayerToEdge = getDistanceFromCentreOfEachLayerToEdge(diameters, additionalDiameters, verticalSpacings, nominalCover);

        return IntStream
                .range(0, distanceFromEachLayerToEdge.size())
                .mapToObj(i -> areaOfReinforcementLayers.get(i) * distanceFromEachLayerToEdge.get(i))
                .collect(Collectors.toList());
    }

    public List<Integer> getTopDiameters() {
        return topDiameters;
    }

    public List<Integer> getAdditionalTopDiameters() {
        return additionalTopDiameters;
    }

    public List<Integer> getTopSpacings() {
        return topSpacings;
    }

    public List<Integer> getTopVerticalSpacings() {
        return topVerticalSpacings;
    }

    public List<Integer> getBottomDiameters() {
        return bottomDiameters;
    }

    public List<Integer> getAdditionalBottomDiameters() {
        return additionalBottomDiameters;
    }

    public List<Integer> getBottomSpacings() {
        return bottomSpacings;
    }

    public List<Integer> getBottomVerticalSpacings() {
        return bottomVerticalSpacings;
    }

    public DesignParameters getDesignParameters() {
        return designParameters;
    }

    public SlabStrip getSlabStrip() {
        return slabStrip;
    }

    public GraphicsContext getGraphicsContext() {
        return graphicsContext;
    }

    public Color getColour() {
        return colour;
    }

    public double getSlabImageScale() {
        return slabImageScale;
    }

    private void drawReinforcementDescription(double slabLeftEdgeX, double slabTopEdgeY, double slabBottomEdgeY) {
        if (!isSetupToBeDrawn()) {
            throw new IllegalArgumentException(UIText.INVALID_SLAB_REINFORCEMENT);
        }
        graphicsContext.beginPath();

        String descriptionTopLayers = "Top layers:\n" + getDescriptionForReinforcementLayers(topDiameters, additionalTopDiameters, topSpacings).replaceAll(",  ", "\n");
        String descriptionBottomLayers = "Bottom layers:\n" + getDescriptionForReinforcementLayers(bottomDiameters, additionalBottomDiameters, bottomSpacings).replaceAll(",  ", "\n");

        Font font = new Font(Reinforcement.DEFAULT_TEXT_FONT, SlabReinforcement.DEFAULT_TEXT_SIZE);
        graphicsContext.setFont(font);
        graphicsContext.setTextAlign(TextAlignment.LEFT);

        graphicsContext.setTextBaseline(VPos.BOTTOM);
        graphicsContext.fillText(descriptionTopLayers, slabLeftEdgeX, slabTopEdgeY - SlabReinforcement.DEFAULT_TEXT_OFFSET);

        graphicsContext.setTextBaseline(VPos.TOP);
        graphicsContext.fillText(descriptionBottomLayers, slabLeftEdgeX, slabBottomEdgeY + SlabReinforcement.DEFAULT_TEXT_OFFSET);

        graphicsContext.closePath();
    }

    private void drawReinforcementLayer(double layerX, double layerY, int numberOfBars, int diameter, int spacing) {
        if (!isSetupToBeDrawn()) {
            throw new IllegalArgumentException(UIText.INVALID_SLAB_REINFORCEMENT);
        }
        graphicsContext.beginPath();

        double barDiameterInScale = diameter * slabImageScale;

        List<Double> barCoordinatesX = IntStream.range(0, numberOfBars)
                .mapToObj(i -> layerX + i * spacing * slabImageScale)
                .collect(Collectors.toList());

        barCoordinatesX.forEach(x -> graphicsContext.fillOval(x, layerY, barDiameterInScale, barDiameterInScale));

        graphicsContext.closePath();
    }

    private void drawMainReinforcementLayers(double realWidth, double slabLeftEdgeX, double slabEdgeY, List<Double> reinforcementY, List<Integer> diameters, List<Integer> spacings, String slabFace) {
        if (!slabFace.equals(Constants.SLAB_BOTTOM_FACE) && !slabFace.equals(Constants.SLAB_TOP_FACE)) {
            throw new IllegalArgumentException(UIText.INVALID_SLAB_REINFORCEMENT);
        }

        double slabEndArchDepth = slabStrip.getEndArchDepth();

        IntStream.range(0, diameters.size())
                .forEach(i -> {
                    int spacing = spacings.get(i);
                    int diameter = diameters.get(i);
                    double widthAvailableForRebar = realWidth - diameter - 2 * slabEndArchDepth / slabImageScale;

                    int quotient = (int) (widthAvailableForRebar / spacing);
                    int remainder = (int) (widthAvailableForRebar % spacing);

                    double layerX = slabLeftEdgeX + (0.5 * remainder) * slabImageScale + slabEndArchDepth;
                    double layerY = slabFace.equals(Constants.SLAB_TOP_FACE)
                            ? slabEdgeY + reinforcementY.get(i) * slabImageScale
                            : slabEdgeY - reinforcementY.get(i) * slabImageScale;

                    int numberOfBars = quotient + 1;

                    drawReinforcementLayer(layerX, layerY, numberOfBars, diameter, spacing);
                });
    }

    private void drawAdditionalReinforcementLayer(double realWidth, double slabLeftEdgeX, double slabEdgeY, List<Double> reinforcementY, List<Integer> diameters, List<Integer> additionalDiameters, List<Integer> spacings, String slabFace) {
        if (!isSetupToBeDrawn()) {
            throw new IllegalArgumentException(UIText.INVALID_SLAB_REINFORCEMENT);
        }

        double slabEndArchDepth = slabStrip.getEndArchDepth();

        IntStream.range(0, additionalDiameters.size())
                .forEach(i -> {
                    int spacing = spacings.get(i);
                    int diameter = diameters.get(i);
                    int additionalDiameter = additionalDiameters.get(i);

                    double widthAvailableForRebar = realWidth - diameter - 2 * slabEndArchDepth / slabImageScale;

                    int remainder = (int) (widthAvailableForRebar % spacing);

                    double widthForAdditionalRebar = widthAvailableForRebar - (remainder + spacing - additionalDiameter);

                    double layerX = slabLeftEdgeX + (0.5 * remainder + 0.5 * diameter + 0.5 * spacing - 0.5 * additionalDiameter) * slabImageScale + slabEndArchDepth;
                    double layerY = slabFace.equals(Constants.SLAB_TOP_FACE)
                            ? slabEdgeY + reinforcementY.get(i) * slabImageScale
                            : slabEdgeY - reinforcementY.get(i) * slabImageScale;

                    int quotient = (int) (widthForAdditionalRebar / spacing);

                    int numberOfBars = quotient + 1;

                    drawReinforcementLayer(layerX, layerY, numberOfBars, additionalDiameter, spacing);
                });
    }

    /**
     * Method draws slab reinforcement.
     */
    @Override
    public void draw() throws IllegalArgumentException {
        if (!isSetupToBeDrawn()) {
            throw new IllegalArgumentException(UIText.INVALID_SLAB_REINFORCEMENT);
        }
        graphicsContext.beginPath();
        graphicsContext.setFill(colour);

        double widthInScale = slabStrip.getWidth();
        double realWidth = widthInScale / slabImageScale;
        double slabLeftEdgeX = slabStrip.getStartX();
        double slabTopEdgeY = slabStrip.getStartY();
        double slabBottomEdgeY = slabTopEdgeY + slabStrip.getDepth();
        List<Double> topReinforcementY = getDistanceFromTopOfTopLayersToTopEdge(designParameters.getNominalCoverTop());
        List<Double> bottomReinforcementY = getDistanceFromTopOBottomLayersToBottomEdge(designParameters.getNominalCoverBottom());

        drawMainReinforcementLayers(realWidth, slabLeftEdgeX, slabTopEdgeY, topReinforcementY, topDiameters, topSpacings, Constants.SLAB_TOP_FACE);

        drawMainReinforcementLayers(realWidth, slabLeftEdgeX, slabBottomEdgeY, bottomReinforcementY, bottomDiameters, bottomSpacings, Constants.SLAB_BOTTOM_FACE);

        drawAdditionalReinforcementLayer(realWidth, slabLeftEdgeX, slabTopEdgeY, topReinforcementY, topDiameters, additionalTopDiameters, topSpacings, Constants.SLAB_TOP_FACE);

        drawAdditionalReinforcementLayer(realWidth, slabLeftEdgeX, slabBottomEdgeY, bottomReinforcementY, bottomDiameters, additionalBottomDiameters, bottomSpacings, Constants.SLAB_BOTTOM_FACE);

        drawReinforcementDescription(slabLeftEdgeX, slabTopEdgeY, slabBottomEdgeY);

        graphicsContext.closePath();
    }

    @Override
    public boolean isSetupToBeDrawn() {
        return !(designParameters == null || slabStrip == null || graphicsContext == null || colour == null || slabImageScale == 0);
    }
}
