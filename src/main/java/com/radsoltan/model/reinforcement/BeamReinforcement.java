package com.radsoltan.model.reinforcement;

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

        this.topDiameters = topDiameters;
        this.topVerticalSpacings = topVerticalSpacings;
        this.bottomDiameters = bottomDiameters;
        this.bottomVerticalSpacings = bottomVerticalSpacings;
        this.shearLinks = shearLinks;
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
                        .mapToObj(distance -> distancesForSubsequentRows.get(i))
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
    public List<List<Double>> getFirstMomentOfAreaForReinforcementBars(List<List<Double>> areasOfReinforcementBars, List<List<Integer>> diameters, List<Integer> clearVerticalSpacings, int nominalCover) {

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

    @Override
    public int getMaxBarSpacingForTensileReinforcement(double SlsMoment) {
        // TODO: 26/06/2022
        return 0;
    }

    @Override
    public int getMaxBarDiameterForTensileReinforcement(double SlsMoment) {
        // TODO: 26/06/2022
        return 0;
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

    @Override
    public String getDescription() {
        // TODO: 02/08/2020
        return null;
    }

    @Override
    public void draw() {
        // TODO: 18/05/2020
    }

    @Override
    public boolean isSetupToBeDrawn() {
        // TODO: 26/06/2022
        return false;
    }
}
