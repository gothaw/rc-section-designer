package com.radsoltan.model.reinforcement;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class BeamReinforcement extends Reinforcement {

    private List<List<Integer>> topReinforcement;
    private List<Integer> topReinforcementVerticalSpacing;
    private List<Integer> flangeReinforcement;
    private List<Integer> topRowReinforcement;
    private List<List<Integer>> bottomReinforcement;
    private List<Integer> bottomReinforcementVerticalSpacing;
    private boolean isReinforcementInTwoFlanges;


    public BeamReinforcement(List<List<Integer>> topReinforcement, List<Integer> topReinforcementVerticalSpacing, List<Integer> flangeReinforcement, List<List<Integer>> bottomReinforcement, List<Integer> bottomReinforcementVerticalSpacing) {
        this.topReinforcement = topReinforcement;
        this.topReinforcementVerticalSpacing = topReinforcementVerticalSpacing;
        this.flangeReinforcement = flangeReinforcement;
        this.bottomReinforcement = bottomReinforcement;
        this.bottomReinforcementVerticalSpacing = bottomReinforcementVerticalSpacing;

        this.topRowReinforcement = new ArrayList<>(topReinforcement.get(0));
        if (flangeReinforcement != null) {
            topRowReinforcement.addAll(flangeReinforcement);
        }
    }

    public BeamReinforcement(List<List<Integer>> topReinforcement, List<Integer> topReinforcementVerticalSpacing, List<List<Integer>> bottomReinforcement, List<Integer> bottomReinforcementVerticalSpacing) {
        this(topReinforcement, topReinforcementVerticalSpacing, null, bottomReinforcement, bottomReinforcementVerticalSpacing);
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

        List<List<Integer>> list2 = IntStream.range(0, topReinforcement.size()).mapToObj(x -> IntStream.range(0, topReinforcement.get(x).size()).mapToObj(y -> barDistanceToTheTopEdge.get(x) * topReinforcement.get(x).get(y)).collect(Collectors.toList())).collect(Collectors.toList());

        //flatMap(Collection::stream).map(diameter -> diameter * diameter * Math.PI * 0.25).collect(Collectors.toList());
        System.out.println(list2);
        return 0;
    }


    public List<Double> calculateCentroidOfReinforcement


    public int findLargestBarDiameterInTopRowExcludingFlanges() {
        return topReinforcement.get(0).stream()
                .max(Integer::compareTo)
                .orElse(0);
    }

    public List<Double> calculateAreaOfBarsInTopRow() {
        return topRowReinforcement.stream()
                .map(diameter -> diameter * diameter * Math.PI * 0.25)
                .collect(Collectors.toList());
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

    public List<Integer> getFlangeReinforcement() {
        return flangeReinforcement;
    }

    public List<List<Integer>> getBottomReinforcement() {
        return bottomReinforcement;
    }

    public List<Integer> getBottomReinforcementVerticalSpacing() {
        return bottomReinforcementVerticalSpacing;
    }
}
