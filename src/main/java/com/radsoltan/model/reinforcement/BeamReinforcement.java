package com.radsoltan.model.reinforcement;

import com.radsoltan.constants.Constants;
import com.radsoltan.constants.UIText;
import com.radsoltan.model.DesignParameters;
import com.radsoltan.model.geometry.Rectangle;
import com.radsoltan.model.geometry.Section;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Model for beam reinforcement. It is used to create an object that describes beam reinforcement that can be used in structural calculations.
 * In addition, allows for beam reinforcement on graphics context.
 */
public class BeamReinforcement extends Reinforcement {

    private final List<List<Integer>> topDiameters;
    private final List<Integer> topVerticalSpacings;
    private final List<List<Integer>> bottomDiameters;
    private final List<Integer> bottomVerticalSpacings;
    private final ShearLinks shearLinks;
    private final DesignParameters designParameters;
    private final Section section;
    private final GraphicsContext graphicsContext;
    private final Color colour;
    private final double beamImageScale;
    // Constants used in drawing beam reinforcement
    public static final int DEFAULT_TEXT_SIZE = 10;
    public static final int DEFAULT_TEXT_OFFSET = 5;

    /**
     * Constructor. Used in structural calculations.
     * <p>
     * The reinforcement is described using following lists:
     * <p>
     * - topDiameters/bottomDiameters: these are lists of lists of integers, the length of the outer list corresponds to number of rows - top/bottom.
     * The inner lists include reinforcement diameters for each bar in given row.
     * For example, reinforcement object with topDiameters of [[25, 20, 25, 20, 25], [16, 12, 16], [10, 8, 8, 10]] has 3 rows:
     * 1st row: 3 x 25 mm bars, 2 x 20 mm bars (5 in total)
     * 2nd row: 2 x 16 mm bars, 1 x 16 mm bar (3 in total)
     * 3rd row: 2 x 10 mm bars, 2 x 8 mm bar (4 in total)
     * <p>
     * - topVerticalSpacings/bottomVerticalSpacings: these include clear vertical spacings between subsequent row for top/bottom face.
     * For instance, if reinforcement object has topDiameters of [[20, 16, 16, 20], [16, 16], [10, 10]] and topVerticalSpacings of [75, 50] then the top reinforcement has 3 rows:
     * 1st row: 2 x 20 mm bars, 2 x 16 mm bars (4 in total)
     * 2nd row: 2 x 16 mm bars (2 in total)
     * 3rd row: 2 x 10 mm bars (2 in total)
     * The clear distance between 1st and 2nd row is 75 mm and between 2nd and 3rd is 50 mm.
     *
     * <p>
     * The reinforcement also includes a ShearLinks object that describes shear reinforcement.
     *
     * @param topDiameters           contains top bar diameters in mm
     * @param topVerticalSpacings    clear vertical spacings between top rows in mm
     * @param bottomDiameters        contains bottom bar diameters in mm
     * @param bottomVerticalSpacings clear vertical spacings between bottom rows in mm
     * @param shearLinks             shear links object
     */
    public BeamReinforcement(List<List<Integer>> topDiameters,
                             List<Integer> topVerticalSpacings,
                             List<List<Integer>> bottomDiameters,
                             List<Integer> bottomVerticalSpacings,
                             ShearLinks shearLinks) {

        this(topDiameters, topVerticalSpacings, bottomDiameters, bottomVerticalSpacings, shearLinks, null, null, null, null, 0);
    }

    /**
     * Constructor used in structural calculations.
     * It includes section and design parameters objects. Can be used in SLS calculations.
     *
     * @param topDiameters           contains top bar diameters in mm
     * @param topVerticalSpacings    clear vertical spacings between top rows in mm
     * @param bottomDiameters        contains bottom bar diameters in mm
     * @param bottomVerticalSpacings clear vertical spacings between bottom rows in mm
     * @param shearLinks             shear links object
     * @param designParameters       DesignParameters object
     * @param section                beam section
     *
     */
    public BeamReinforcement(List<List<Integer>> topDiameters,
                             List<Integer> topVerticalSpacings,
                             List<List<Integer>> bottomDiameters,
                             List<Integer> bottomVerticalSpacings,
                             ShearLinks shearLinks,
                             DesignParameters designParameters,
                             Section section) {
        this(topDiameters, topVerticalSpacings, bottomDiameters, bottomVerticalSpacings, shearLinks, designParameters, section, null, null, 0);
    }

    /**
     * Constructor. Used in structural calculations.
     * In addition, it takes arguments that allow for drawing the beam reinforcement on graphics context.
     *
     * @param topDiameters           contains top bar diameters in mm
     * @param topVerticalSpacings    clear vertical spacings between top rows in mm
     * @param bottomDiameters        contains bottom bar diameters in mm
     * @param bottomVerticalSpacings clear vertical spacings between bottom rows in mm
     * @param shearLinks             shear links object
     * @param designParameters       DesignParameters object
     * @param section                beam section in scale that is to be drawn
     * @param graphicsContext        graphics context to draw beam on
     * @param colour                 colour to draw the reinforcement with
     * @param beamImageScale         beam image scale that section is drawn with
     */
    public BeamReinforcement(List<List<Integer>> topDiameters,
                             List<Integer> topVerticalSpacings,
                             List<List<Integer>> bottomDiameters,
                             List<Integer> bottomVerticalSpacings,
                             ShearLinks shearLinks,
                             DesignParameters designParameters,
                             Section section,
                             GraphicsContext graphicsContext,
                             Color colour,
                             double beamImageScale) {

        this.topDiameters = topDiameters;
        this.topVerticalSpacings = topVerticalSpacings;
        this.bottomDiameters = bottomDiameters;
        this.bottomVerticalSpacings = bottomVerticalSpacings;
        this.shearLinks = shearLinks;
        this.designParameters = designParameters;
        this.section = section;
        this.graphicsContext = graphicsContext;
        this.colour = colour;
        this.beamImageScale = beamImageScale;
    }

    /**
     * Method is used to calculate a list of lists that stores areas of each reinforcement bar.
     *
     * @param diameters list of reinforcement rows, each row is a list with bar diameters in mm
     * @return List of reinforcement rows, each row is list that stores area (in mm2) of each bar in that row
     */
    private List<List<Double>> getAreaOfReinforcementBars(List<List<Integer>> diameters) {
        return diameters.stream()
                .map(row -> row.stream()
                        .map(diameter -> diameter * diameter * Math.PI * 0.25)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    /**
     * Calculates total area of top reinforcement.
     *
     * @return Total area of top reinforcement in mm2
     */
    @Override
    public double getTotalAreaOfTopReinforcement() {
        return getAreaOfReinforcementBars(topDiameters).stream()
                .flatMap(Collection::stream)
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    /**
     * Calculates total area of bottom reinforcement.
     *
     * @return Total area of bottom reinforcement in mm2
     */
    @Override
    public double getTotalAreaOfBottomReinforcement() {
        return getAreaOfReinforcementBars(bottomDiameters).stream()
                .flatMap(Collection::stream)
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    /**
     * Calculates distances from each bar to the beam edge - top/bottom.
     * <p>
     * First row bars are considered separately as their distance to the edge is: nominalCover + shearLinkDiameter + 0.5 * barDiameter.
     * This is because first row bars are tied to shear links and don't lie on one horizontal centre line (unless they have the same diameters).
     * <p>
     * Subsequent rows are placed on one horizontal centre line and are separated from other rows by a clear vertical spacing.
     * <p>
     * The result is a list of lists, the outer list size corresponds to number of rows.
     * The values of inner lists are distances from centre of a bar to the beam edge. For example a result of [[50, 40, 50], [125, 125, 125], [185, 185]] means that:
     * - 1st row: 3 bars in total, 2 of them are 50 mm from the edge and one is 40 mm from the edge
     * - 2nd row: 3 bars in total, all of them are 125 mm from the beam's edge
     * - 3rd row: 2 bars in total, all of them are 185 mm from the beam's edge
     *
     * @param diameters             list of reinforcement rows, each row is a list with bar diameters in mm
     * @param clearVerticalSpacings clear vertical spacings between rows in mm
     * @param nominalCover          nominal cover for given beam's edge in mm
     * @return List of reinforcement rows, each row is list that stores distances from centre of each bar to the beam's edge
     */
    private List<List<Double>> getDistanceFromCentreOfEachBarToEdge(List<List<Integer>> diameters, List<Integer> clearVerticalSpacings, int nominalCover) {
        List<List<Double>> distanceFromCentroidOfEachBarToEdge = new ArrayList<>();

        // Distances for first row bars
        List<Double> distanceForBarsInFirstRow = diameters.get(0).stream()
                .map(diameter -> diameter * 0.5 + nominalCover + shearLinks.getDiameter())
                .collect(Collectors.toList());

        distanceFromCentroidOfEachBarToEdge.add(distanceForBarsInFirstRow);

        // Distances for subsequent rows
        double largestDistanceForFirstRow = Collections.max(distanceForBarsInFirstRow);

        // Max diameters for each row
        List<Integer> maxDiametersForEachRow = diameters.stream()
                .map(Collections::max)
                .collect(Collectors.toList());

        // Converting clear spacings to vertical spacings
        List<Double> verticalSpacings = IntStream
                .range(0, clearVerticalSpacings.size())
                .mapToObj(i -> clearVerticalSpacings.get(i) + 0.5 * maxDiametersForEachRow.get(i) + 0.5 * maxDiametersForEachRow.get(i + 1))
                .collect(Collectors.toList());

        // Calculating distances from centroid of each row to edge
        List<Double> distancesForSubsequentRows = IntStream
                .range(0, verticalSpacings.size())
                .mapToObj(i -> largestDistanceForFirstRow + verticalSpacings.get(i) + verticalSpacings.stream().limit(i).reduce(0.0, Double::sum))
                .collect(Collectors.toList());

        // Starting from the second row
        List<List<Double>> distanceForBarsInSubsequentRows = IntStream.range(1, diameters.size())
                .mapToObj(i -> IntStream
                        .range(0, diameters.get(i).size())
                        .mapToObj(distance -> distancesForSubsequentRows.get(i - 1)) // // TODO: 27/07/2022 Check this in unit tests
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        distanceFromCentroidOfEachBarToEdge.addAll(distanceForBarsInSubsequentRows);

        return distanceFromCentroidOfEachBarToEdge;
    }

    /**
     * Method is used to calculate a list of lists that stores first moment of area for each reinforcement bar.
     * First moment of areas are calculated against the closest beam edge.
     *
     * @param areasOfReinforcementBars list of reinforcement rows, each row is a list that stores area of each bar in mm2
     * @param diameters                list of reinforcement rows, each row is a list with bar diameters in mm
     * @param clearVerticalSpacings    clear vertical spacings between rows in mm
     * @param nominalCover             nominal cover for given beam's edge in mm
     * @return List of reinforcement rows, each row is list that stores first moment of area (in mm3) of each bar in that row
     */
    private List<List<Double>> getFirstMomentOfAreaForReinforcementBars(List<List<Double>> areasOfReinforcementBars, List<List<Integer>> diameters, List<Integer> clearVerticalSpacings, int nominalCover) {

        List<List<Double>> distanceFromCentreOfEachBarToEdge = getDistanceFromCentreOfEachBarToEdge(diameters, clearVerticalSpacings, nominalCover);

        return IntStream
                .range(0, distanceFromCentreOfEachBarToEdge.size())
                .mapToObj(i -> IntStream
                        .range(0, distanceFromCentreOfEachBarToEdge.get(i).size())
                        .mapToObj(j -> distanceFromCentreOfEachBarToEdge.get(i).get(j) * areasOfReinforcementBars.get(i).get(j))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    /**
     * Calculates centroid of top reinforcement in mm.
     *
     * @param nominalCoverTop nominal cover for the top face of the element in mm
     * @return Centroid of top reinforcement in mm
     */
    @Override
    public double getCentroidOfTopReinforcement(int nominalCoverTop) {
        List<List<Double>> areaOfTopBars = getAreaOfReinforcementBars(topDiameters);
        List<List<Double>> firstMomentOfAreaForTopBars = getFirstMomentOfAreaForReinforcementBars(areaOfTopBars, topDiameters, topVerticalSpacings, nominalCoverTop);

        double sumOfAreas = getTotalAreaOfTopReinforcement();
        double sumOfFirstMomentsOfArea = firstMomentOfAreaForTopBars.stream()
                .flatMap(Collection::stream)
                .mapToDouble(Double::doubleValue)
                .sum();

        return sumOfFirstMomentsOfArea / sumOfAreas;
    }

    /**
     * Calculates centroid of bottom reinforcement in mm.
     *
     * @param nominalCoverBottom nominal cover for the bottom face of the element in mm
     * @return Centroid of bottom reinforcement in mm
     */
    @Override
    public double getCentroidOfBottomReinforcement(int nominalCoverBottom) {
        List<List<Double>> areaOfBottomBars = getAreaOfReinforcementBars(bottomDiameters);
        List<List<Double>> firstMomentOfAreaForBottomBars = getFirstMomentOfAreaForReinforcementBars(areaOfBottomBars, bottomDiameters, bottomVerticalSpacings, nominalCoverBottom);

        double sumOfAreas = getTotalAreaOfBottomReinforcement();
        double sumOfFirstMomentsOfArea = firstMomentOfAreaForBottomBars.stream()
                .flatMap(Collection::stream)
                .mapToDouble(Double::doubleValue)
                .sum();

        return sumOfFirstMomentsOfArea / sumOfAreas;
    }

    /**
     * Gets max horizontal spacing between reinforcement bars for tensile reinforcement. This is measured between bar centres.
     *
     * @param SlsMoment SLS moment in kNm
     * @return max bar spacing for tensile reinforcement
     */
    @Override
    public int getMaxBarSpacingForTensileReinforcement(double SlsMoment) {
        if (designParameters == null || section == null) {
            throw new IllegalArgumentException(UIText.INVALID_BEAM_REINFORCEMENT);
        }

        List<List<Integer>> diameters = (SlsMoment >= 0) ? bottomDiameters : topDiameters;

        int nominalCover = designParameters.getNominalCoverSides();
        int shearLinkDiameter = shearLinks.getDiameter();
        int width = section.getWidth();

        int availableWidth = width - 2 * nominalCover - 2 * shearLinkDiameter;

        List<Integer> horizontalSpacings = diameters
                .stream()
                .map(row -> {
                    int numberOfBars = row.size();

                    // spacings between bar centres
                    return (availableWidth - row.get(0)) / (numberOfBars - 1);
                }).collect(Collectors.toList());

        return Collections.max(horizontalSpacings);
    }

    /**
     * Gets max reinforcement bar diameter for tensile reinforcement.
     *
     * @param SlsMoment SLS moment in kNm
     * @return max bar diameter for tensile reinforcement
     */
    @Override
    public int getMaxBarDiameterForTensileReinforcement(double SlsMoment) {
        List<List<Integer>> diameters = (SlsMoment >= 0) ? bottomDiameters : topDiameters;
        List<Integer> flattenDiameters = diameters.stream().flatMap(List::stream).collect(Collectors.toList());

        return Collections.max(flattenDiameters);
    }

    /**
     * Getter for the top bar diameters.
     *
     * @return Top diameters
     */
    public List<List<Integer>> getTopDiameters() {
        return topDiameters;
    }

    /**
     * Getter for the top clear vertical spacings between rows.
     *
     * @return Top clear vertical spacings between rows
     */
    public List<Integer> getTopVerticalSpacings() {
        return topVerticalSpacings;
    }

    /**
     * Getter for the bottom bar diameters.
     *
     * @return Bottom diameters
     */
    public List<List<Integer>> getBottomDiameters() {
        return bottomDiameters;
    }

    /**
     * Getter for the bottom clear vertical spacings between rows.
     *
     * @return Bottom clear vertical spacings between rows
     */
    public List<Integer> getBottomVerticalSpacings() {
        return bottomVerticalSpacings;
    }

    /**
     * Getter for shear links.
     *
     * @return Shear links object
     */
    public ShearLinks getShearLinks() {
        return shearLinks;
    }

    /**
     * Gets general reinforcement description
     *
     * @return reinforcement description
     */
    @Override
    public String getDescription() {
        String descriptionTopRows = "Top rows:\n" + getDescriptionForReinforcementRows(topDiameters, true);
        String descriptionBottomRows = "Bottom rows:\n" + getDescriptionForReinforcementRows(bottomDiameters, true);
        String descriptionShearLinks = shearLinks.getShearLinksDescription(true);
        return descriptionTopRows + "\n" + descriptionBottomRows + "\n" + descriptionShearLinks;
    }

    /**
     * Gets description for reinforcement rows for given beam face.
     *
     * @param diameters list of reinforcement rows, each row is a list with bar diameters in mm
     * @param shouldIncludeRowLabel determines whether or not include row label
     * @return reinforcement description for selected beam face
     */
    private String getDescriptionForReinforcementRows(List<List<Integer>> diameters, boolean shouldIncludeRowLabel) {
        String rowsDescription = "";

        for (int i = 0; i < diameters.size(); i++) {
            List<Integer> diametersInRow = diameters.get(i);
            List<Integer> distinctBars = diametersInRow.stream()
                    .distinct()
                    .collect(Collectors.toList());
            List<Integer> numberOfDistinctBars = distinctBars.stream()
                    .map(diameter -> Collections.frequency(diametersInRow, diameter))
                    .collect(Collectors.toList());

            String description = shouldIncludeRowLabel
                    ? String.format("%s row:", Constants.ORDINAL_LABELS.get(i))
                    : "";

            for (int j = 0; j < distinctBars.size(); j++) {
                description = description.concat(String.format(" %d\u03c6%d", numberOfDistinctBars.get(j), distinctBars.get(j)));
            }

            if (i < diameters.size() - 1) {
                description = description.concat(",  ");
            }
            rowsDescription = rowsDescription.concat(description);
        }

        return rowsDescription;
    }

    /**
     * Draws reinforcement description for given beam face top or bottom.
     * Reinforcement description is placed to the right of the beam image and at similar level as reinforcement rows.
     * It uses getDescriptionForReinforcementRows method to get distance from each bar to the edge.
     * It then calculates the average distance in scale for each row. Description labels are places at these distances.
     *
     * @param diameters list of reinforcement rows, each row is a list with bar diameters in mm
     * @param clearVerticalSpacings clear vertical spacings between rows in mm
     * @param nominalCover nominal cover in mm
     * @param font font that the description should be drawn with
     * @param beamRightEdgeX X coordinate of the right edge of the beam
     * @param beamEdgeY Y coordinate of the edge top/bottom
     * @param beamFace beam face top or bottom
     */
    private void drawReinforcementDescriptionForReinforcementRows(List<List<Integer>> diameters, List<Integer> clearVerticalSpacings, int nominalCover, Font font, double beamRightEdgeX, double beamEdgeY, String beamFace) {
        if (!beamFace.equals(Constants.BEAM_BOTTOM_FACE) && !beamFace.equals(Constants.BEAM_TOP_FACE)) {
            throw new IllegalArgumentException(UIText.INVALID_BEAM_FACE);
        }
        if (!isSetupToBeDrawn()) {
            throw new IllegalArgumentException(UIText.INVALID_BEAM_REINFORCEMENT);
        }
        graphicsContext.beginPath();

        String description = getDescriptionForReinforcementRows(diameters, false);

        List<String> descriptionList = Arrays.asList(description.replaceAll(",  ", ",").replaceAll(" ", "+").split(","));

        List<List<Double>> distanceFromCentreToEdge = getDistanceFromCentreOfEachBarToEdge(diameters, clearVerticalSpacings, nominalCover);

        // Average distances from centre of each row to beam edge
        List<Double> averageDistanceInScale = distanceFromCentreToEdge
                .stream()
                .map(row -> row
                        .stream()
                        .mapToDouble(x -> x * beamImageScale)
                        .average()
                        .orElse(0)
                ).collect(Collectors.toList());

        graphicsContext.setFont(font);
        graphicsContext.setTextAlign(TextAlignment.LEFT);

        // Drawing description for each row of given beam face
        IntStream.range(0, descriptionList.size()).forEach(i -> {
            String rowDescription = descriptionList.get(i).substring(1); // Removing first character (space that was replaced to +)
            double coordinateY = beamFace.equals(Constants.BEAM_TOP_FACE)
                    ? beamEdgeY + averageDistanceInScale.get(i)
                    : beamEdgeY - averageDistanceInScale.get(i);

            graphicsContext.fillText(rowDescription, beamRightEdgeX + BeamReinforcement.DEFAULT_TEXT_OFFSET, coordinateY);
        });

        graphicsContext.closePath();
    }

    /**
     * Draws reinforcement description for top and bottom face.
     */
    private void drawReinforcementDescription(double beamRightEdge, double beamTopEdgeY, double beamBottomEdgeY) {
        if (!isSetupToBeDrawn()) {
            throw new IllegalArgumentException(UIText.INVALID_BEAM_REINFORCEMENT);
        }

        Font font = new Font(Reinforcement.DEFAULT_TEXT_FONT, BeamReinforcement.DEFAULT_TEXT_SIZE);

        drawReinforcementDescriptionForReinforcementRows(topDiameters, topVerticalSpacings, designParameters.getNominalCoverTop(), font, beamRightEdge, beamTopEdgeY, Constants.BEAM_TOP_FACE);
        drawReinforcementDescriptionForReinforcementRows(bottomDiameters, bottomVerticalSpacings, designParameters.getNominalCoverBottom(), font, beamRightEdge, beamBottomEdgeY, Constants.BEAM_BOTTOM_FACE);
    }

    /**
     * Gets Y coordinates of top edge of each reinforcement bar. It uses getDistanceFromCentreOfEachBarToEdge method.
     *
     * @param diameters             list of reinforcement rows, each row is a list with bar diameters in mm
     * @param clearVerticalSpacings clear vertical spacings between rows in mm
     * @param beamEdgeY             beam edge top or bottom Y coordinate
     * @param beamFace              beam face (top or bottom)
     * @return Two dimensional list with Y coordinates
     */
    private List<List<Double>> getYCoordinateForReinforcement(List<List<Integer>> diameters, List<Integer> clearVerticalSpacings, double beamEdgeY, String beamFace) {
        List<List<Double>> distanceFromCentreToEdge;
        double distanceSign; // distances from bar centre to edge must be subtracted from beam edge coordinate if bottom face

        // Calculating distance from centre of bar to edge
        switch (beamFace) {
            case Constants.BEAM_TOP_FACE:
                distanceFromCentreToEdge = getDistanceFromCentreOfEachBarToEdge(diameters, clearVerticalSpacings, designParameters.getNominalCoverTop());
                distanceSign = 1;
                break;
            case Constants.BEAM_BOTTOM_FACE:
                distanceFromCentreToEdge = getDistanceFromCentreOfEachBarToEdge(diameters, clearVerticalSpacings, designParameters.getNominalCoverBottom());
                distanceSign = -1;
                break;
            default:
                throw new IllegalArgumentException(UIText.INVALID_BEAM_FACE);
        }

        return IntStream.range(0, diameters.size()).mapToObj(i -> {
            List<Integer> diametersInRow = diameters.get(i);
            List<Double> distancesToEdgeInRow = distanceFromCentreToEdge.get(i);

            return IntStream.range(0, diameters.get(i).size()).mapToObj(j ->
                    beamEdgeY + (distanceSign * distancesToEdgeInRow.get(j) - diametersInRow.get(j) * 0.5) * beamImageScale
            ).collect(Collectors.toList());
        }).collect(Collectors.toList());
    }

    /**
     * Gets X coordinates of left edge of each reinforcement bar.
     *
     * @param diameters     list of reinforcement rows, each row is a list with bar diameters in mm
     * @param realWidth     beam real width in mm
     * @param beamLeftEdgeX beam left edge X coordinate
     * @return Two dimensional list with X coordinates
     */
    private List<List<Double>> getXCoordinateForReinforcement(List<List<Integer>> diameters, double realWidth, double beamLeftEdgeX) {
        List<List<Double>> reinforcementX;
        int nominalCover = designParameters.getNominalCoverSides();
        int shearLinkDiameter = shearLinks.getDiameter();

        // Available width to accommodate main reinforcement
        double availableWidth = realWidth - 2 * shearLinkDiameter - 2 * nominalCover;

        reinforcementX = diameters
                .stream()
                .map(row -> {
                    int numberOfBars = row.size();
                    int totalSumOfDiameters = row.stream().reduce(Integer::sum).orElse(0);

                    // clear spacing between main bars
                    double clearSpacing = (availableWidth - totalSumOfDiameters) / (numberOfBars - 1);

                    return IntStream.range(0, row.size()).mapToObj(i -> {
                        int sumOfDiameters = row.stream().limit(i).reduce(Integer::sum).orElse(0);
                        double sumOfSpacings = clearSpacing * i;

                        return beamLeftEdgeX + (nominalCover + shearLinkDiameter + sumOfSpacings + sumOfDiameters) * beamImageScale;
                    }).collect(Collectors.toList());
                }).collect(Collectors.toList());

        return reinforcementX;
    }

    /**
     * Draws reinforcement rows for given beam face. It gets X and Y coordinates of top left edge of each reinforcement bar.
     * It uses these coordinates to draw rebar using fillOval on graphics context.
     *
     * @param realWidth             beam real width in mmm
     * @param beamLeftEdgeX         beam left edge X coordinate
     * @param beamEdgeY             beam edge top or bottom Y coordinate
     * @param diameters             list of reinforcement rows, each row is a list with bar diameters in mm
     * @param clearVerticalSpacings clear vertical spacings between rows in mm
     * @param beamFace              beam face (top or bottom)
     */
    private void drawReinforcementRows(double realWidth, double beamLeftEdgeX, double beamEdgeY, List<List<Integer>> diameters, List<Integer> clearVerticalSpacings, String beamFace) {
        if (!isSetupToBeDrawn()) {
            throw new IllegalArgumentException(UIText.INVALID_BEAM_REINFORCEMENT);
        }
        graphicsContext.beginPath();

        // Getting X and Y coordinates of top left edge of each reinforcement bar
        List<List<Double>> coordinatesX = getXCoordinateForReinforcement(diameters, realWidth, beamLeftEdgeX);
        List<List<Double>> coordinatesY = getYCoordinateForReinforcement(diameters, clearVerticalSpacings, beamEdgeY, beamFace);

        IntStream.range(0, diameters.size())
                .forEach(i -> {
                    List<Double> rowX = coordinatesX.get(i);
                    List<Double> rowY = coordinatesY.get(i);
                    List<Integer> rowDiameters = diameters.get(i);

                    // Drawing rebar
                    IntStream.range(0, rowDiameters.size())
                            .forEach(j -> graphicsContext.fillOval(rowX.get(j), rowY.get(j), rowDiameters.get(j) * beamImageScale, rowDiameters.get(j) * beamImageScale));
                });

        graphicsContext.closePath();
    }

    /**
     * Draws main beam reinforcement and main reinforcement description. It uses drawReinforcementRows method.
     */
    @Override
    public void draw() {
        if (!isSetupToBeDrawn()) {
            throw new IllegalArgumentException(UIText.INVALID_BEAM_REINFORCEMENT);
        }
        if (section instanceof Rectangle) {

            graphicsContext.beginPath();
            graphicsContext.setFill(colour);
            Rectangle rectangle = (Rectangle) section;

            double widthInScale = section.getWidth();
            double realWidth = widthInScale / beamImageScale;
            double beamLeftEdgeX = rectangle.getStartX();
            double beamRightEdge = beamLeftEdgeX + section.getWidth();
            double beamTopEdgeY = rectangle.getStartY();
            double beamBottomEdgeY = beamTopEdgeY + rectangle.getDepth();

            // Drawing main reinforcement
            drawReinforcementRows(realWidth, beamLeftEdgeX, beamTopEdgeY, topDiameters, topVerticalSpacings, Constants.BEAM_TOP_FACE);
            drawReinforcementRows(realWidth, beamLeftEdgeX, beamBottomEdgeY, bottomDiameters, bottomVerticalSpacings, Constants.BEAM_BOTTOM_FACE);

            // Drawing main reinforcement description
            drawReinforcementDescription(beamRightEdge, beamTopEdgeY, beamBottomEdgeY);

            graphicsContext.closePath();
        } else {
            throw new IllegalArgumentException(UIText.INVALID_BEAM_GEOMETRY);
        }
    }


    /**
     * Checks if reinforcement can be drawn and all necessary fields are set up.
     *
     * @return true if reinforcement can be drawn
     */
    @Override
    public boolean isSetupToBeDrawn() {
        return !(designParameters == null || section == null || graphicsContext == null || colour == null || beamImageScale == 0);
    }
}
