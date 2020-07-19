package com.radsoltan.model.reinforcement;

import java.util.ArrayList;
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

    public SlabReinforcement(List<Integer> topDiameters,
                             List<Integer> additionalTopDiameters,
                             List<Integer> topSpacings,
                             List<Integer> topVerticalSpacings,
                             List<Integer> bottomDiameters,
                             List<Integer> additionalBottomDiameters,
                             List<Integer> bottomSpacings,
                             List<Integer> bottomVerticalSpacings) {
        this.topDiameters = topDiameters;
        this.additionalTopDiameters = additionalTopDiameters;
        this.topSpacings = topSpacings;
        this.topVerticalSpacings = topVerticalSpacings;
        this.bottomDiameters = bottomDiameters;
        this.additionalBottomDiameters = additionalBottomDiameters;
        this.bottomSpacings = bottomSpacings;
        this.bottomVerticalSpacings = bottomVerticalSpacings;
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

    public List<Double> getAreaOfReinforcementLayers(List<Integer> reinforcement, List<Integer> additionalReinforcement, List<Integer> spacing) {

        return IntStream.range(0, reinforcement.size())
                .mapToObj(i -> 0.25 * Math.PI * (reinforcement.get(i) * reinforcement.get(i) + additionalReinforcement.get(i) * additionalReinforcement.get(i)) * 1000 / spacing.get(i))
                .collect(Collectors.toList());

    }

    // TODO: 10/06/2020 Encapsulation
    public List<Double> getDistanceFromCentreOfEachLayerToEdge(List<Integer> reinforcement, List<Integer> additionalReinforcement, List<Integer> verticalSpacing, int nominalCover) {

        List<Double> distanceFromCentroidOfEachLayerToEdge = new ArrayList<>();

        double distanceForFirstLayer = 0.5 * Math.max(reinforcement.get(0), additionalReinforcement.get(0)) + nominalCover;

        distanceFromCentroidOfEachLayerToEdge.add(distanceForFirstLayer);

        List<Double> distanceForSubsequentLayers = IntStream
                .range(0, verticalSpacing.size())
                .mapToObj(i -> distanceForFirstLayer + verticalSpacing.get(i) + verticalSpacing.stream()
                        .limit(i)
                        .reduce(0, Integer::sum))
                .collect(Collectors.toList());

        distanceFromCentroidOfEachLayerToEdge.addAll(distanceForSubsequentLayers);

        return distanceFromCentroidOfEachLayerToEdge;
    }

    public List<Double> getFirstMomentOfAreaReinforcementLayers(List<Double> areaOfReinforcementLayers, List<Integer> reinforcement, List<Integer> additionalReinforcement, List<Integer> verticalSpacing, int nominalCover) {
        List<Double> distanceFromEachLayerToEdge = getDistanceFromCentreOfEachLayerToEdge(reinforcement, additionalReinforcement, verticalSpacing, nominalCover);

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

    @Override
    public void draw() {

    }
}
