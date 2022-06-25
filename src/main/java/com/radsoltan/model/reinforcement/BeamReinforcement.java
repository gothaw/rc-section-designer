package com.radsoltan.model.reinforcement;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BeamReinforcement extends Reinforcement {

    private final List<List<Integer>> topDiameters;
    private List<Integer> topVerticalSpacings;
    private final List<List<Integer>> bottomDiameters;
    private List<Integer> bottomVerticalSpacings;
    private final ShearLinks shearLinks;
    private int numberOfBarsInSlab;
    private boolean isReinforcementInSlabSymmetrical;

    public BeamReinforcement(List<List<Integer>> topDiameters,
            List<Integer> topVerticalSpacings,
            List<List<Integer>> bottomDiameters,
            List<Integer> bottomVerticalSpacings,
            ShearLinks shearLinks,
            int numberOfBarsInSlab,
            boolean isReinforcementInSlabSymmetrical) {

        this.topDiameters = topDiameters;
        this.topVerticalSpacings = topVerticalSpacings;
        this.bottomDiameters = bottomDiameters;
        this.bottomVerticalSpacings = bottomVerticalSpacings;
        this.shearLinks = shearLinks;
        this.numberOfBarsInSlab = numberOfBarsInSlab;
        this.isReinforcementInSlabSymmetrical = isReinforcementInSlabSymmetrical;
    }

    public BeamReinforcement(List<List<Integer>> topDiameters,
                             List<Integer> topVerticalSpacings,
                             List<List<Integer>> bottomDiameters,
                             List<Integer> bottomVerticalSpacings,
                             ShearLinks shearLinks) {
        this(topDiameters, topVerticalSpacings, bottomDiameters, bottomVerticalSpacings, shearLinks, 0, false);
    }

    public BeamReinforcement(List<Integer> topReinforcementRow, List<Integer> bottomReinforcementRow, ShearLinks shearLinks) {

        List<List<Integer>> topDiameters = new ArrayList<>();
        topDiameters.add(new ArrayList<>());
        topDiameters.get(0).addAll(topReinforcementRow);
        this.topDiameters = topDiameters;

        List<List<Integer>> bottomDiameters = new ArrayList<>();
        bottomDiameters.add(new ArrayList<>());
        bottomDiameters.get(0).addAll(bottomReinforcementRow);
        this.bottomDiameters = bottomDiameters;

        this.shearLinks = shearLinks;
    }

    @Override
    public double getTotalAreaOfTopReinforcement() {
        return getAreaOfReinforcementBars(topDiameters).stream()
                .flatMap(Collection::stream)
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    @Override
    public double getCentroidOfTopReinforcement(int nominalCoverTop) {
        List<List<Double>> areaOfTopBars = getAreaOfReinforcementBars(topDiameters);
        List<List<Double>> firstMomentOfAreaForTopBars = getFirstMomentOfAreaForReinforcementBars(areaOfTopBars, topDiameters, topVerticalSpacings, nominalCoverTop, true);

        double sumOfAreas = getTotalAreaOfTopReinforcement();
        double sumOfFirstMomentsOfArea = firstMomentOfAreaForTopBars.stream()
                .flatMap(Collection::stream)
                .mapToDouble(Double::doubleValue)
                .sum();

        return sumOfFirstMomentsOfArea / sumOfAreas;
    }

    @Override
    public double getTotalAreaOfBottomReinforcement() {
        return getAreaOfReinforcementBars(bottomDiameters).stream()
                .flatMap(Collection::stream)
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    @Override
    public double getCentroidOfBottomReinforcement(int nominalCoverBottom) {
        List<List<Double>> areaOfBottomBars = getAreaOfReinforcementBars(bottomDiameters);
        List<List<Double>> firstMomentOfAreaForBottomBars = getFirstMomentOfAreaForReinforcementBars(areaOfBottomBars, bottomDiameters, bottomVerticalSpacings, nominalCoverBottom, false);

        double sumOfAreas = getTotalAreaOfBottomReinforcement();
        double sumOfFirstMomentsOfArea = firstMomentOfAreaForBottomBars.stream()
                .flatMap(Collection::stream)
                .mapToDouble(Double::doubleValue)
                .sum();

        return sumOfFirstMomentsOfArea / sumOfAreas;
    }

    @Override
    public int getMaxBarSpacingForTensileReinforcement(double SlsMoment) {
        return 0;
    }

    @Override
    public int getMaxBarDiameterForTensileReinforcement(double SlsMoment) {
        return 0;
    }

    @Override
    public String getDescription() {
        // TODO: 02/08/2020
        return null;
    }

    public List<List<Double>> getAreaOfReinforcementBars(List<List<Integer>> reinforcement) {
        return reinforcement.stream()
                .map(row -> row.stream()
                        .map(diameter -> diameter * diameter * Math.PI * 0.25)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    // TODO: 10/06/2020 Encapsulation
    public List<List<Double>> getDistanceFromCentreOfEachBarToEdge(List<List<Integer>> reinforcement, List<Integer> verticalBarSpacing, int nominalCover, boolean isTopReinforcement) {

        List<List<Double>> distanceFromCentroidOfEachBarToEdge = new ArrayList<>();

        // Distance For First Row
        List<Double> distanceForBarsInFirstRow = reinforcement.get(0).stream()
                .map(diameter -> diameter * 0.5 + nominalCover + shearLinks.getDiameter())
                .collect(Collectors.toList());

        distanceFromCentroidOfEachBarToEdge.add(distanceForBarsInFirstRow);

        // Distance For Subsequent Rows
        double largestDistanceToFirstRowBar = getMaxDistanceFromFirstRowRebarToEdgeExcludingSlabRebar(distanceForBarsInFirstRow, isTopReinforcement);

        List<List<Integer>> barsInSubsequentRows = new ArrayList<>(reinforcement);
        barsInSubsequentRows.remove(0);

        List<Double> distanceForSubsequentRows = IntStream
                .range(0, verticalBarSpacing.size())
                .mapToObj(i -> verticalBarSpacing.get(i) + largestDistanceToFirstRowBar + verticalBarSpacing.stream()
                        .limit(i)
                        .reduce(0, Integer::sum))
                .collect(Collectors.toList());

        List<List<Double>> distanceForBarsInSubsequentRows = IntStream.range(0, barsInSubsequentRows.size())
                .mapToObj(i -> IntStream
                        .range(0, barsInSubsequentRows.get(i).size())
                        .mapToObj(distance -> distanceForSubsequentRows.get(i))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        distanceFromCentroidOfEachBarToEdge.addAll(distanceForBarsInSubsequentRows);

        return distanceFromCentroidOfEachBarToEdge;
    }

    private double getMaxDistanceFromFirstRowRebarToEdgeExcludingSlabRebar(List<Double> distanceForBarsInFirstRow, boolean isTopReinforcement) {
        if (isTopReinforcement) {
            int numberOfBarsToBeExcluded = (isReinforcementInSlabSymmetrical) ? numberOfBarsInSlab / 2 : numberOfBarsInSlab;
            List<Double> distanceForBarsInFirstRowExcludingSlab = IntStream
                    .range(numberOfBarsToBeExcluded, distanceForBarsInFirstRow.size() - numberOfBarsToBeExcluded)
                    .mapToObj(distanceForBarsInFirstRow::get)
                    .collect(Collectors.toList());

            return Collections.max(distanceForBarsInFirstRowExcludingSlab);
        } else {
            return Collections.max(distanceForBarsInFirstRow);
        }
    }

    public List<List<Double>> getFirstMomentOfAreaForReinforcementBars(List<List<Double>> areaOfReinforcementBars, List<List<Integer>> reinforcement, List<Integer> verticalBarSpacing, int nominalCover, boolean isTopReinforcement) {

        List<List<Double>> distanceFromCentreOfEachBarToEdge = getDistanceFromCentreOfEachBarToEdge(reinforcement, verticalBarSpacing, nominalCover, isTopReinforcement);

        return IntStream
                .range(0, distanceFromCentreOfEachBarToEdge.size())
                .mapToObj(i -> IntStream
                        .range(0, distanceFromCentreOfEachBarToEdge.get(i).size())
                        .mapToObj(j -> distanceFromCentreOfEachBarToEdge.get(i).get(j) * areaOfReinforcementBars.get(i).get(j))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    public List<List<Integer>> getTopReinforcement() {
        return topDiameters;
    }

    public List<Integer> getTopReinforcementVerticalSpacing() {
        return topVerticalSpacings;
    }

    public List<List<Integer>> getBottomReinforcement() {
        return bottomDiameters;
    }

    public List<Integer> getBottomReinforcementVerticalSpacing() {
        return bottomVerticalSpacings;
    }

    public ShearLinks getShearLinks() {
        return shearLinks;
    }

    @Override
    public void draw() {
        // TODO: 18/05/2020
    }

    @Override
    public boolean isSetupToBeDrawn() {
        return false;
    }
}
