package com.radsoltan.model.reinforcement;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BeamReinforcement extends Reinforcement {

    private List<List<Integer>> topReinforcement;
    private List<List<Integer>> topReinforcementIncludingFlanges;
    private List<Integer> topReinforcementVerticalSpacing;
    private List<Integer> reinforcementForOneFlange;
    private List<Integer> topRowReinforcement;
    private List<Integer> topRowReinforcementExcludingFlanges;
    private List<List<Integer>> bottomReinforcement;
    private List<Integer> bottomReinforcementVerticalSpacing;
    private boolean hasTwoFlanges;
    private int largestBarDiameterTopRowExcludingFlanges;

    public BeamReinforcement(List<List<Integer>> topReinforcement,
                             List<Integer> topReinforcementVerticalSpacing,
                             List<List<Integer>> bottomReinforcement,
                             List<Integer> bottomReinforcementVerticalSpacing,
                             List<Integer> reinforcementForOneFlange,
                             boolean hasTwoFlanges) {
        this.topRowReinforcementExcludingFlanges = topReinforcement.get(0);
        /*if (reinforcementForOneFlange != null) {
            topReinforcement.get(0).addAll(reinforcementForOneFlange);
            if (hasTwoFlanges) {
                topReinforcement.get(0).addAll(0, reinforcementForOneFlange);
            }
        }*/
        this.topReinforcement = topReinforcement;
        this.topReinforcementVerticalSpacing = topReinforcementVerticalSpacing;
        this.bottomReinforcement = bottomReinforcement;
        this.bottomReinforcementVerticalSpacing = bottomReinforcementVerticalSpacing;
        this.hasTwoFlanges = hasTwoFlanges;
    }

    public BeamReinforcement(List<List<Integer>> topReinforcement,
                             List<Integer> topReinforcementVerticalSpacing,
                             List<List<Integer>> bottomReinforcement,
                             List<Integer> bottomReinforcementVerticalSpacing) {
        this(topReinforcement, topReinforcementVerticalSpacing, bottomReinforcement, bottomReinforcementVerticalSpacing, null, false);
    }


    public BeamReinforcement(List<Integer> topReinforcementRow, List<Integer> bottomReinforcementRow) {

        List<List<Integer>> topReinforcement = new ArrayList<>();
        topReinforcement.add(new ArrayList<>());
        topReinforcement.get(0).addAll(topReinforcementRow);
        this.topReinforcement = topReinforcement;
        this.topRowReinforcement = topReinforcement.get(0);

        List<List<Integer>> bottomReinforcement = new ArrayList<>();
        bottomReinforcement.add(new ArrayList<>());
        bottomReinforcement.get(0).addAll(bottomReinforcementRow);
        this.bottomReinforcement = bottomReinforcement;
    }


    @Override
    public double calculateCentroidOfTopReinforcement(int nominalCoverTop) {
        List<Integer> barDistanceToTheTopEdge = new ArrayList<>();
        barDistanceToTheTopEdge.add(nominalCoverTop + findLargestBarDiameterInTopRowExcludingFlanges() / 2);

        if (topReinforcementVerticalSpacing != null) {
            barDistanceToTheTopEdge.addAll(topReinforcementVerticalSpacing);
        }

        //List<List<Double>> list1 = topReinforcement.stream().map(x -> x.stream().map(y -> y * 1.5).collect(Collectors.toList())).collect(Collectors.toList());

        List<List<Integer>> list2 = IntStream
                .range(0, topReinforcement.size())
                .mapToObj(x -> IntStream.range(0, topReinforcement.get(x).size()).mapToObj(y -> barDistanceToTheTopEdge.get(x) * topReinforcement.get(x).get(y)).collect(Collectors.toList())).collect(Collectors.toList());

        //flatMap(Collection::stream).map(diameter -> diameter * diameter * Math.PI * 0.25).collect(Collectors.toList());
        System.out.println(list2);
        return 0;
    }

    public int findLargestBarDiameterInTopRowExcludingFlanges() {
        return topReinforcement.get(0).stream()
                .max(Integer::compareTo)
                .orElse(0);
    }

    public int findLargestBarDiameterInRow(List<Integer> reinforcementRow) {
        return reinforcementRow.stream()
                .max(Integer::compareTo)
                .orElse(0);
    }

    public List<Double> calculateAreaOfBarsInTopRow() {
        return topRowReinforcement.stream()
                .map(diameter -> diameter * diameter * Math.PI * 0.25)
                .collect(Collectors.toList());
    }

    public List<List<Double>> calculateAreaOfReinforcementBars(List<List<Integer>> reinforcement) {
        return reinforcement.stream()
                .map(row -> row.stream()
                        .map(diameter -> diameter * diameter * Math.PI * 0.25)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    // TODO: 21/05/2020 Change to List<List<double>>
    // TODO: 21/05/2020 Do refactor of names
    public List<Double> calculateDistanceFromCentroidOfEachBarToTheEdge(List<List<Integer>> reinforcement, List<Integer> verticalBarSpacing, int nominalCover, int transverseBar) {

        List<Double> distanceForBarsInFirstRow = reinforcement.get(0).stream()
                .map(diameter -> diameter * 0.5 + nominalCover + transverseBar)
                .collect(Collectors.toList());
        // TODO: 21/05/2020 edgeDistanceToFirstRow - extract to new method, use boolean value to check if top or bottom row
        double edgeDistanceToFirstRow = 0.5 * findLargestBarDiameterInRow(topRowReinforcementExcludingFlanges) + transverseBar + nominalCover;

        List<Double> distanceForBarsInSubsequentRows = IntStream.range(0, verticalBarSpacing.size())
                .mapToObj(i -> verticalBarSpacing.get(i) + edgeDistanceToFirstRow + verticalBarSpacing.stream()
                        .limit(i)
                        .reduce(0, Integer::sum))
                .collect(Collectors.toList());

        reinforcement.remove(0);

        List<List<Double>> distanceFromCentroidOfEachBarToTheEdge = IntStream.range(0, distanceForBarsInSubsequentRows.size())
                .mapToObj(i -> IntStream.range(0, reinforcement.get(i).size())
                        .mapToObj(j -> reinforcement.get(i).get(j) + distanceForBarsInSubsequentRows.get(i))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        System.out.println(distanceFromCentroidOfEachBarToTheEdge);
        System.out.println(reinforcement);
        return distanceForBarsInSubsequentRows;
    }

    public List<Double> calculateFirstMomentOfAreaForBarsInTopRow(int nominalCover, int transverseBarDiameter) {
        List<Double> barAreasInTopRow = calculateAreaOfBarsInTopRow();
        List<Double> barDistancesToTopEdgeTopRow = calculateDistanceToTopEdgeForBarsInTopRow(nominalCover, transverseBarDiameter);
        return IntStream
                .range(0, topRowReinforcement.size())
                .mapToObj(i -> barAreasInTopRow.get(i) * barDistancesToTopEdgeTopRow.get(i))
                .collect(Collectors.toList());
    }

    public List<Double> calculateDistanceToTopEdgeForBarsInTopRow(int nominalCoverTop, int transverseBarDiameter) {
        return topRowReinforcement.stream()
                .map(diameter -> diameter * 0.5 + nominalCoverTop + transverseBarDiameter)
                .collect(Collectors.toList());
    }

    public List<Integer> test() {
        List<Integer> list = new ArrayList<>(List.of(1, 2, 3, 4));
        System.out.println(list);
        return IntStream.range(0, list.size()).mapToObj(i -> list.get(i) + list.stream().limit(i).reduce(0, Integer::sum)).collect(Collectors.toList());
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

    public List<Integer> getReinforcementForOneFlange() {
        return reinforcementForOneFlange;
    }

    public List<List<Integer>> getBottomReinforcement() {
        return bottomReinforcement;
    }

    public List<Integer> getBottomReinforcementVerticalSpacing() {
        return bottomReinforcementVerticalSpacing;
    }
}
