package com.radsoltan.model.reinforcement;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SlabReinforcement extends Reinforcement {

    private List<Integer> topReinforcement;
    private List<Integer> additionalTopReinforcement;
    private List<Integer> topReinforcementSpacing;
    private List<Integer> topReinforcementVerticalSpacing;
    private List<Integer> bottomReinforcement;
    private List<Integer> additionalBottomReinforcement;
    private List<Integer> bottomReinforcementSpacing;
    private List<Integer> bottomReinforcementVerticalSpacing;

    public SlabReinforcement(int fy,
                             List<Integer> topReinforcement,
                             List<Integer> additionalTopReinforcement,
                             List<Integer> topReinforcementSpacing,
                             List<Integer> topReinforcementVerticalSpacing,
                             List<Integer> bottomReinforcement,
                             List<Integer> additionalBottomReinforcement,
                             List<Integer> bottomReinforcementSpacing,
                             List<Integer> bottomReinforcementVerticalSpacing) {
        this.topReinforcement = topReinforcement;
        this.additionalTopReinforcement = additionalTopReinforcement;
        this.topReinforcementSpacing = topReinforcementSpacing;
        this.topReinforcementVerticalSpacing = topReinforcementVerticalSpacing;
        this.bottomReinforcement = bottomReinforcement;
        this.additionalBottomReinforcement = additionalBottomReinforcement;
        this.bottomReinforcementSpacing = bottomReinforcementSpacing;
        this.bottomReinforcementVerticalSpacing = bottomReinforcementVerticalSpacing;
    }

    public SlabReinforcement(int fy,
                             List<Integer> topReinforcement,
                             List<Integer> topReinforcementSpacing,
                             List<Integer> topReinforcementVerticalSpacing,
                             List<Integer> bottomReinforcement,
                             List<Integer> bottomReinforcementSpacing,
                             List<Integer> bottomReinforcementVerticalSpacing) {
        this(fy, topReinforcement, topReinforcementSpacing, topReinforcementVerticalSpacing, null,
                bottomReinforcement, bottomReinforcementSpacing, bottomReinforcementVerticalSpacing, null);
    }

    @Override
    public double getTotalAreaOfTopReinforcement() {
        return 0;
    }

    @Override
    public double getCentroidOfTopReinforcement(int nominalCoverTop, int transverseBarDiameter) {
        return 0;
    }

    @Override
    public double getTotalAreaOfBottomReinforcement() {
        return 0;
    }

    @Override
    public double getCentroidOfBottomReinforcement(int nominalCoverBottom, int transverseBarDiameter) {
        return 0;
    }

    public List<Double> getAreaOfReinforcementLayers(List<Integer> reinforcement, List<Integer> additionalReinforcement, List<Integer> spacing) {

        List<Double> areaOfReinforcementLayers = IntStream.range(0, additionalReinforcement.size())
                .mapToObj(i -> 0.25 * Math.PI * (reinforcement.get(i) * reinforcement.get(i) + additionalReinforcement.get(i) * additionalReinforcement.get(i)) * 1000 / spacing.get(i))
                .collect(Collectors.toList());
        List<Double> areaOfLayersWithoutAdditionalRebar = IntStream.range(additionalReinforcement.size(), reinforcement.size())
                .mapToObj(i -> 0.25 * Math.PI * reinforcement.get(i) * reinforcement.get(i) * 1000 / spacing.get(i))
                .collect(Collectors.toList());

        areaOfReinforcementLayers.addAll(areaOfLayersWithoutAdditionalRebar);

        return areaOfReinforcementLayers;
    }

    public List<Double> getAreaOfReinforcementLayers(List<Integer> reinforcement, List<Integer> spacing) {
        return IntStream.range(0, reinforcement.size())
                .mapToObj(i -> 0.25 * Math.PI * reinforcement.get(i) * reinforcement.get(i) * 1000 / spacing.get(i))
                .collect(Collectors.toList());
    }

    public List<Double> getDistanceFromCentreOfEachLayerToTheEdge(){

    }

    @Override
    public void draw() {

    }
}
