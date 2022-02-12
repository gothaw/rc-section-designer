package com.radsoltan.model.reinforcement;

import com.radsoltan.model.DesignParameters;
import com.radsoltan.model.geometry.SlabStrip;
import com.radsoltan.util.Constants;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

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

    // TODO: 10/06/2020 Encapsulation
    public List<Double> getDistanceFromCentreOfEachLayerToEdge(List<Integer> diameters, List<Integer> additionalDiameters, List<Integer> verticalSpacings, int nominalCover) {

        List<Integer> maxDiameters = IntStream
                .range(0, diameters.size())
                .mapToObj(i -> Math.max(diameters.get(i), additionalDiameters.get(i)))
                .collect(Collectors.toList());

        return IntStream
                .range(0, maxDiameters.size())
                .mapToObj(i -> nominalCover + 0.5 * maxDiameters.get(i)
                        + verticalSpacings.stream().limit(i).reduce(0, Integer::sum)
                        + maxDiameters.stream().limit(i).reduce(0, Integer::sum))
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

    /**
     * Method draws slab reinforcement.
     */
    @Override
    public void draw() {
        if (graphicsContext == null || colour == null) {
            return;
        }

        graphicsContext.beginPath();
        graphicsContext.setFill(colour);

        double widthInScale = slabStrip.getWidth();
        double realWidth = widthInScale / slabImageScale;
        double slabLeftEdgeX = slabStrip.getStartX();
        double slabTopEdgeY = slabStrip.getStartY();
        double slabEndArchDepth = slabStrip.getEndArchDepth();

        double widthAvailableForRebar = realWidth - topDiameters.get(0) - 2 * slabEndArchDepth / slabImageScale;

        int quotient = (int) (widthAvailableForRebar / topSpacings.get(0));
        int remainder = (int) (widthAvailableForRebar % topSpacings.get(0));

        double firstBarDistanceFromLeftEdge = slabLeftEdgeX + remainder * 0.5 * slabImageScale + slabEndArchDepth;

        List<Double> barCoordinatesX = IntStream.range(0, quotient + 1)
                .mapToObj(x -> firstBarDistanceFromLeftEdge + x * topSpacings.get(0) * slabImageScale)
                .collect(Collectors.toList());

        double barCoordinateY = slabTopEdgeY + designParameters.getNominalCoverTop() * slabImageScale;
        double barDiameterInScale = topDiameters.get(0) * slabImageScale;

        barCoordinatesX.forEach(x -> graphicsContext.fillOval(x, barCoordinateY, barDiameterInScale, barDiameterInScale));

        graphicsContext.closePath();
    }
}
