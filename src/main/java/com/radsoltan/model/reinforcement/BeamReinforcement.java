package com.radsoltan.model.reinforcement;

import com.radsoltan.util.Utility;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BeamReinforcement extends Reinforcement {

    private List<List<Integer>> topReinforcement;
    private List<Integer> topReinforcementVerticalSpacing;
    private List<List<Integer>> bottomReinforcement;
    private List<Integer> bottomReinforcementVerticalSpacing;
    private final ShearLinks shearLinks;
    int numberOfBarsInSlab;
    boolean isReinforcementInSlabSymmetrical;

    public BeamReinforcement(int fy,
                             List<List<Integer>> topReinforcement,
                             List<Integer> topReinforcementVerticalSpacing,
                             List<List<Integer>> bottomReinforcement,
                             List<Integer> bottomReinforcementVerticalSpacing,
                             ShearLinks shearLinks,
                             int numberOfBarsInSlab,
                             boolean isReinforcementInSlabSymmetrical) {

        this.fy = fy;
        this.topReinforcement = topReinforcement;
        this.topReinforcementVerticalSpacing = topReinforcementVerticalSpacing;
        this.bottomReinforcement = bottomReinforcement;
        this.bottomReinforcementVerticalSpacing = bottomReinforcementVerticalSpacing;
        this.shearLinks = shearLinks;
        this.numberOfBarsInSlab = numberOfBarsInSlab;
        this.isReinforcementInSlabSymmetrical = isReinforcementInSlabSymmetrical;
    }

    public BeamReinforcement(int fy,
                             List<List<Integer>> topReinforcement,
                             List<Integer> topReinforcementVerticalSpacing,
                             List<List<Integer>> bottomReinforcement,
                             List<Integer> bottomReinforcementVerticalSpacing,
                             ShearLinks shearLinks) {

        this.fy = fy;
        this.topReinforcement = topReinforcement;
        this.topReinforcementVerticalSpacing = topReinforcementVerticalSpacing;
        this.bottomReinforcement = bottomReinforcement;
        this.bottomReinforcementVerticalSpacing = bottomReinforcementVerticalSpacing;
        this.shearLinks = shearLinks;
        this.numberOfBarsInSlab = 0;
        this.isReinforcementInSlabSymmetrical = false;
    }

    public BeamReinforcement(int fy, List<Integer> topReinforcementRow, List<Integer> bottomReinforcementRow, ShearLinks shearLinks) {

        this.fy = fy;

        List<List<Integer>> topReinforcement = new ArrayList<>();
        topReinforcement.add(new ArrayList<>());
        topReinforcement.get(0).addAll(topReinforcementRow);
        this.topReinforcement = topReinforcement;

        List<List<Integer>> bottomReinforcement = new ArrayList<>();
        bottomReinforcement.add(new ArrayList<>());
        bottomReinforcement.get(0).addAll(bottomReinforcementRow);
        this.bottomReinforcement = bottomReinforcement;

        this.shearLinks = shearLinks;
    }

    @Override
    public double calculateCentroidOfTopReinforcement(int nominalCoverTop, int shearLinkDiameter) {
        List<List<Double>> areaOfTopBars = calculateAreaOfReinforcementBars(topReinforcement);
        List<List<Double>> firstMomentOfAreaForTopBars = calculateFirstMomentOfAreaForReinforcementBars(areaOfTopBars, topReinforcement, topReinforcementVerticalSpacing, nominalCoverTop, shearLinkDiameter, true);

        double sumOfTopBarsArea = areaOfTopBars.stream().flatMap(Collection::stream).mapToDouble(Double::doubleValue).sum();
        double sumOfTopBarsFirstMomentOfArea = firstMomentOfAreaForTopBars.stream().flatMap(Collection::stream).mapToDouble(Double::doubleValue).sum();

        return sumOfTopBarsFirstMomentOfArea / sumOfTopBarsArea;
    }

    @Override
    public double calculateCentroidOfBottomReinforcement(int nominalCoverBottom, int shearLinkDiameter) {
        List<List<Double>> areaOfBottomBars = calculateAreaOfReinforcementBars(bottomReinforcement);
        List<List<Double>> firstMomentOfAreaForBottomBars = calculateFirstMomentOfAreaForReinforcementBars(areaOfBottomBars, bottomReinforcement, bottomReinforcementVerticalSpacing, nominalCoverBottom, shearLinkDiameter, false);

        double sumOfBottomBarsArea = areaOfBottomBars.stream().flatMap(Collection::stream).mapToDouble(Double::doubleValue).sum();
        double sumOfBottomBarsFirstMomentOfArea = firstMomentOfAreaForBottomBars.stream().flatMap(Collection::stream).mapToDouble(Double::doubleValue).sum();

        return sumOfBottomBarsFirstMomentOfArea / sumOfBottomBarsArea;
    }

    public List<List<Double>> calculateAreaOfReinforcementBars(List<List<Integer>> reinforcement) {
        return reinforcement.stream()
                .map(row -> row.stream()
                        .map(diameter -> diameter * diameter * Math.PI * 0.25)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    public List<List<Double>> calculateDistanceFromCentreOfEachBarToTheEdge(List<List<Integer>> reinforcement, List<Integer> verticalBarSpacing, int nominalCover, int shearLinkDiameter, boolean isTopReinforcement) {

        List<List<Double>> distanceFromCentroidOfEachBarToTheEdge = new ArrayList<>();

        // Distance For First Row
        List<Double> distanceForBarsInFirstRow = reinforcement.get(0).stream()
                .map(diameter -> diameter * 0.5 + nominalCover + shearLinkDiameter)
                .collect(Collectors.toList());

        distanceFromCentroidOfEachBarToTheEdge.add(distanceForBarsInFirstRow);

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

        distanceFromCentroidOfEachBarToTheEdge.addAll(distanceForBarsInSubsequentRows);

        return distanceFromCentroidOfEachBarToTheEdge;
    }

    private double getMaxDistanceFromFirstRowRebarToEdgeExcludingSlabRebar(List<Double> distanceForBarsInFirstRow, boolean isTopReinforcement) {
        if (isTopReinforcement) {
            int numberOfBarsToBeExcluded = (isReinforcementInSlabSymmetrical) ? numberOfBarsInSlab / 2 : numberOfBarsInSlab;
            List<Double> distanceForBarsInFirstRowExcludingSlab = IntStream
                    .range(numberOfBarsToBeExcluded, distanceForBarsInFirstRow.size() - numberOfBarsToBeExcluded)
                    .mapToObj(distanceForBarsInFirstRow::get)
                    .collect(Collectors.toList());

            return Utility.findMaxValueInList(distanceForBarsInFirstRowExcludingSlab);
        } else {
            return Utility.findMaxValueInList(distanceForBarsInFirstRow);
        }
    }

    public List<List<Double>> calculateFirstMomentOfAreaForReinforcementBars(List<List<Double>> areaOfReinforcementBars, List<List<Integer>> reinforcement, List<Integer> verticalBarSpacing, int nominalCover, int shearLinkDiameter, boolean isTopReinforcement) {

        List<List<Double>> distanceFromCentreOfEachBarToEdge = calculateDistanceFromCentreOfEachBarToTheEdge(reinforcement, verticalBarSpacing, nominalCover, shearLinkDiameter, isTopReinforcement);

        return IntStream
                .range(0, distanceFromCentreOfEachBarToEdge.size())
                .mapToObj(i -> IntStream
                        .range(0, distanceFromCentreOfEachBarToEdge.get(i).size())
                        .mapToObj(j -> distanceFromCentreOfEachBarToEdge.get(i).get(j) * areaOfReinforcementBars.get(i).get(j))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    public List<List<Integer>> getTopReinforcement() {
        return topReinforcement;
    }

    public List<Integer> getTopReinforcementVerticalSpacing() {
        return topReinforcementVerticalSpacing;
    }

    public List<List<Integer>> getBottomReinforcement() {
        return bottomReinforcement;
    }

    public List<Integer> getBottomReinforcementVerticalSpacing() {
        return bottomReinforcementVerticalSpacing;
    }

    public ShearLinks getShearLinks() {
        return shearLinks;
    }

    @Override
    public void draw() {
        // TODO: 18/05/2020
    }
}
