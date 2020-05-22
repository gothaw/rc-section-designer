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
    int numberOfBarsInSlab;
    boolean isReinforcementInSlabSymmetrical;

    public BeamReinforcement(List<List<Integer>> topReinforcement,
                             List<Integer> topReinforcementVerticalSpacing,
                             List<List<Integer>> bottomReinforcement,
                             List<Integer> bottomReinforcementVerticalSpacing,
                             int numberOfBarsInSlab,
                             boolean isReinforcementInSlabSymmetrical) {

        this.topReinforcement = topReinforcement;
        this.topReinforcementVerticalSpacing = topReinforcementVerticalSpacing;
        this.bottomReinforcement = bottomReinforcement;
        this.bottomReinforcementVerticalSpacing = bottomReinforcementVerticalSpacing;
        this.numberOfBarsInSlab = numberOfBarsInSlab;
        this.isReinforcementInSlabSymmetrical = isReinforcementInSlabSymmetrical;
    }

    public BeamReinforcement(List<List<Integer>> topReinforcement,
                             List<Integer> topReinforcementVerticalSpacing,
                             List<List<Integer>> bottomReinforcement,
                             List<Integer> bottomReinforcementVerticalSpacing) {

        this.topReinforcement = topReinforcement;
        this.topReinforcementVerticalSpacing = topReinforcementVerticalSpacing;
        this.bottomReinforcement = bottomReinforcement;
        this.bottomReinforcementVerticalSpacing = bottomReinforcementVerticalSpacing;
        this.numberOfBarsInSlab = 0;
        this.isReinforcementInSlabSymmetrical = false;
    }

    public BeamReinforcement(List<Integer> topReinforcementRow, List<Integer> bottomReinforcementRow) {

        List<List<Integer>> topReinforcement = new ArrayList<>();
        topReinforcement.add(new ArrayList<>());
        topReinforcement.get(0).addAll(topReinforcementRow);
        this.topReinforcement = topReinforcement;

        List<List<Integer>> bottomReinforcement = new ArrayList<>();
        bottomReinforcement.add(new ArrayList<>());
        bottomReinforcement.get(0).addAll(bottomReinforcementRow);
        this.bottomReinforcement = bottomReinforcement;
    }


    @Override
    public double calculateCentroidOfTopReinforcement(int nominalCoverTop) {
        List<Integer> barDistanceToTheTopEdge = new ArrayList<>();

        if (topReinforcementVerticalSpacing != null) {
            barDistanceToTheTopEdge.addAll(topReinforcementVerticalSpacing);
        }

        List<List<Integer>> list2 = IntStream
                .range(0, topReinforcement.size())
                .mapToObj(x -> IntStream.range(0, topReinforcement.get(x).size()).mapToObj(y -> barDistanceToTheTopEdge.get(x) * topReinforcement.get(x).get(y)).collect(Collectors.toList())).collect(Collectors.toList());

        System.out.println(list2);
        return 0;
    }

    public List<List<Double>> calculateAreaOfReinforcementBars(List<List<Integer>> reinforcement) {
        return reinforcement.stream()
                .map(row -> row.stream()
                        .map(diameter -> diameter * diameter * Math.PI * 0.25)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    // TODO: 22/05/2020 Encapsulate methods
    public List<List<Double>> calculateDistanceFromCentroidOfEachBarToTheEdge(List<List<Integer>> reinforcement, List<Integer> verticalBarSpacing, int nominalCover, int transverseBar, boolean isTopReinforcement) {

        List<List<Double>> distanceFromCentroidOfEachBarToTheEdge = new ArrayList<>();

        // Distance For First Row
        List<Double> distanceForBarsInFirstRow = reinforcement.get(0).stream()
                .map(diameter -> diameter * 0.5 + nominalCover + transverseBar)
                .collect(Collectors.toList());

        distanceFromCentroidOfEachBarToTheEdge.add(distanceForBarsInFirstRow);

        // Distance For Subsequent Rows
        double largestDistanceToFirstRowBar = getMaxDistanceFromFirstRowRebarToEdgeExcludingSlab(distanceForBarsInFirstRow, isTopReinforcement);

        reinforcement.remove(0);

        List<Double> distanceForSubsequentRows = IntStream
                .range(0, verticalBarSpacing.size())
                .mapToObj(i -> verticalBarSpacing.get(i) + largestDistanceToFirstRowBar + verticalBarSpacing.stream()
                        .limit(i)
                        .reduce(0, Integer::sum))
                .collect(Collectors.toList());

        List<List<Double>> distanceForBarsInSubsequentRows = IntStream.range(0, reinforcement.size())
                .mapToObj(i -> IntStream
                        .range(0, reinforcement.get(i).size())
                        .mapToObj(distance -> distanceForSubsequentRows.get(i))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        distanceFromCentroidOfEachBarToTheEdge.addAll(distanceForBarsInSubsequentRows);

        return distanceFromCentroidOfEachBarToTheEdge;
    }

    private double getMaxDistanceFromFirstRowRebarToEdgeExcludingSlab(List<Double> distanceForBarsInFirstRow, boolean isTopReinforcement) {
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

    @Override
    public double calculateCentroidOfBottomReinforcement(int nominalCoverBottom) {
        return 0;
    }

    @Override
    public void draw() {
        // TODO: 18/05/2020
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
}
