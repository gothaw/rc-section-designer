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

/**
 * Model for slab reinforcement. It is used to create an object that describes slab reinforcement that can be used in structural calculations.
 * In addition, allows for drawing slab reinforcement on graphics context.
 */
public class SlabReinforcement extends Reinforcement {

    // Lists that describe slab reinforcement
    private final List<Integer> topDiameters;
    private final List<Integer> additionalTopDiameters;
    private final List<Integer> topSpacings;
    private final List<Integer> topVerticalSpacings;
    private final List<Integer> bottomDiameters;
    private final List<Integer> additionalBottomDiameters;
    private final List<Integer> bottomSpacings;
    private final List<Integer> bottomVerticalSpacings;
    // Fields required to draw slab reinforcement
    private final DesignParameters designParameters;
    private final SlabStrip slabStrip;
    private final GraphicsContext graphicsContext;
    private final Color colour;
    private final double slabImageScale;
    // Constants used in drawing slab reinforcement
    public static final int DEFAULT_TEXT_SIZE = 10;
    public static final int DEFAULT_TEXT_OFFSET = 5;


    /**
     * Basic constructor. Used in structural calculations.
     * <p>
     * The reinforcement is described using lists of integers:
     * <p>
     * - topDiameters/bottomDiameters: these include main bar diameters in subsequent layers from the slab face (top/bottom).
     * For instance, bottomDiameters of [16, 12, 8] has 16 mm diameter bars in the layer closest to the bottom edge, 12 mm in the next layer and 8 mm bars in the layer furthest away from the bottom face.
     * <p>
     * - topSpacings/bottomSpacings: lists that include spacings of main bar diameters centres in subsequent layers from the slab face (top/bottom).
     * For instance, if topDiameters is [10, 12] and topSpacings is [200, 150] then the layer closest to the top edge has 10 mm bars at 200 mm and the second layer is 12 mm bars at 150 mm.
     * <p>
     * - additionalTopDiameters/additionalBottomDiameters: lists that include additional bars that are put between main bars. Similarly to lists that store main bar diameters, these lists include bar diameters in subsequent layers from the slab face (top/bottom).
     * The additional bar diameters are assumed to have the same spacing as the main bars. For example, if additionalBottomDiameters are [8, 6] and bottomSpacings are [175, 200] then bottommost layer has 8 mm bars at 175 mm and the next layer is 6 mm bars at 200 mm.
     * <p>
     * - topVerticalSpacings/bottomVerticalSpacings: these include clear vertical spacings between subsequent layers for top/bottom face.
     * For instance, if topDiameters is [10, 12], topSpacings is [175, 150] and topVerticalSpacings is [75] then the topmost layer is 10 mm bars at 175 mm, the second layer is 12 mm bars at 150 mm and the clear vertical distance between these layers is 75mm.
     *
     * @param topDiameters              main top bar diameters in subsequent layers
     * @param additionalTopDiameters    additional top bar diameters placed between main bars in subsequent layers
     * @param topSpacings               top bar spacings between bar centers in subsequent layers
     * @param topVerticalSpacings       clear spacings between top layers
     * @param bottomDiameters           main bottom bar diameters in subsequent layers
     * @param additionalBottomDiameters additional bottom bar diameters placed between main bars in subsequent layers
     * @param bottomSpacings            bottom bar spacings between bar centers in subsequent layers
     * @param bottomVerticalSpacings    clear spacings between bottom layers
     */
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

    /**
     * Constructor. Includes all information required for structural calculations.
     * In addition, it takes arguments that allow for drawing the slab reinforcement on graphics context.
     *
     * @param topDiameters              main top bar diameters in subsequent layers
     * @param additionalTopDiameters    additional top bar diameters placed between main bars in subsequent layers
     * @param topSpacings               top bar spacings between bar centers in subsequent layers
     * @param topVerticalSpacings       clear spacings between top layers
     * @param bottomDiameters           main bottom bar diameters in subsequent layers
     * @param additionalBottomDiameters additional bottom bar diameters placed between main bars in subsequent layers
     * @param bottomSpacings            bottom bar spacings between bar centers in subsequent layers
     * @param bottomVerticalSpacings    clear spacings between bottom layers
     * @param designParameters          DesignParameters object
     * @param slabStrip                 SlabStrip object - created using constructor that allows for drawing the slab strip
     * @param graphicsContext           graphics context to draw slab on
     * @param colour                    colour to draw the reinforcement with
     * @param slabImageScale            slab image scale
     */
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

    /**
     * It creates a list for given slab face that contains area of each reinforcement layer.
     * This is the area per 1 m of the slab.
     *
     * @param diameters           main bar diameters in subsequent layers
     * @param additionalDiameters additional bar diameters in subsequent layers
     * @param spacings            bar spacings in subsequent layers
     * @return list of areas of reinforcement layers
     */
    private List<Double> getAreaOfReinforcementLayers(List<Integer> diameters, List<Integer> additionalDiameters, List<Integer> spacings) {

        return IntStream.range(0, diameters.size())
                .mapToObj(i -> 0.25 * Math.PI * (diameters.get(i) * diameters.get(i) + additionalDiameters.get(i) * additionalDiameters.get(i)) * 1000 / spacings.get(i))
                .collect(Collectors.toList());
    }

    /**
     * It creates a list of max diameters in each reinforcement layer for given slab face.
     *
     * @param diameters           main bar diameters in subsequent layers
     * @param additionalDiameters additional bar diameters in subsequent layers
     * @return list of max diameters in each reinforcement layer
     */
    private List<Integer> getMaxDiametersForEachLayer(List<Integer> diameters, List<Integer> additionalDiameters) {
        return IntStream
                .range(0, diameters.size())
                .mapToObj(i -> Math.max(diameters.get(i), additionalDiameters.get(i)))
                .collect(Collectors.toList());
    }

    /**
     * It creates a list for given slab face that contains distances from center of each layer to the edge.
     *
     * @param diameters           main bar diameters in subsequent layers
     * @param additionalDiameters additional bar diameters in subsequent layers
     * @param verticalSpacings    clear spacings between layers
     * @param nominalCover        nominal cover in mm
     * @return list of distances of each layer to the edge
     */
    private List<Double> getDistanceFromCentreOfEachLayerToEdge(List<Integer> diameters, List<Integer> additionalDiameters, List<Integer> verticalSpacings, int nominalCover) {

        List<Integer> maxDiameters = getMaxDiametersForEachLayer(diameters, additionalDiameters);

        return IntStream
                .range(0, maxDiameters.size())
                .mapToObj(i -> nominalCover + 0.5 * maxDiameters.get(i)
                        + verticalSpacings.stream().limit(i).reduce(0, Integer::sum)
                        + maxDiameters.stream().limit(i).reduce(0, Integer::sum))
                .collect(Collectors.toList());
    }

    /**
     * It creates a list for given slab face that contains first moment of area for each layer in relation to the edge.
     *
     * @param areaOfReinforcementLayers areas per 1 m of the slab of subsequent reinforcement layers
     * @param diameters                 main bar diameters in subsequent layers
     * @param additionalDiameters       additional bar diameters in subsequent layers
     * @param verticalSpacings          clear spacings between layers
     * @param nominalCover              nominal cover in mm
     * @return list of first moment of area for each layer
     */
    public List<Double> getFirstMomentOfAreaReinforcementLayers(List<Double> areaOfReinforcementLayers, List<Integer> diameters, List<Integer> additionalDiameters, List<Integer> verticalSpacings, int nominalCover) {
        List<Double> distanceFromEachLayerToEdge = getDistanceFromCentreOfEachLayerToEdge(diameters, additionalDiameters, verticalSpacings, nominalCover);

        return IntStream
                .range(0, distanceFromEachLayerToEdge.size())
                .mapToObj(i -> areaOfReinforcementLayers.get(i) * distanceFromEachLayerToEdge.get(i))
                .collect(Collectors.toList());
    }

    /**
     * Calculates total area of top reinforcement. Invokes getAreaOfReinforcementLayers method.
     *
     * @return total area of top reinforcement
     */
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

    /**
     * Calculates total area of bottom reinforcement. Invokes getAreaOfReinforcementLayers method.
     *
     * @return total area of bottom reinforcement
     */
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
