package com.radsoltan.model.reinforcement;

import java.util.*;
import java.util.stream.Collectors;

public class BeamReinforcement extends Reinforcement {

    private List<List<Integer>> topReinforcement;
    private List<Integer> topReinforcementVerticalSpacing;
    private List<Integer> flangeReinforcement;
    private List<List<Integer>> bottomReinforcement;
    private List<Integer> bottomReinforcementVerticalSpacing;


    public BeamReinforcement(List<List<Integer>> topReinforcement, List<Integer> topReinforcementVerticalSpacing, List<Integer> flangeReinforcement, List<List<Integer>> bottomReinforcement, List<Integer> bottomReinforcementVerticalSpacing) {
        this.topReinforcement = topReinforcement;
        this.topReinforcementVerticalSpacing = topReinforcementVerticalSpacing;
        this.flangeReinforcement = flangeReinforcement;
        this.bottomReinforcement = bottomReinforcement;
        this.bottomReinforcementVerticalSpacing = bottomReinforcementVerticalSpacing;
    }

    public BeamReinforcement(List<List<Integer>> topReinforcement, List<Integer> topReinforcementVerticalSpacing, List<List<Integer>> bottomReinforcement, List<Integer> bottomReinforcementVerticalSpacing) {
        this(topReinforcement, topReinforcementVerticalSpacing, null, bottomReinforcement, bottomReinforcementVerticalSpacing);
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
        int firstRowDistanceToEdge = nominalCoverTop + findLargestBarDiameterInTopRow() / 2;
        List<Integer> barDistanceToTheTopEdge = new ArrayList<>(firstRowDistanceToEdge);
        if (topReinforcementVerticalSpacing != null) {
            barDistanceToTheTopEdge.addAll(topReinforcementVerticalSpacing);
        }
        List<Double> list = topReinforcement.stream().flatMap(Collection::stream).map(diameter -> diameter * diameter * Math.PI * 0.25).collect(Collectors.toList());
        System.out.println(list);
        return 0;
    }

    public int findLargestBarDiameterInTopRow() {
        List<Integer> topRowReinforcement = new ArrayList<>(topReinforcement.get(0));
        if (flangeReinforcement != null) {
            topRowReinforcement.addAll(flangeReinforcement);
        }
        return topRowReinforcement.stream().max(Integer::compareTo).orElse(0);
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
