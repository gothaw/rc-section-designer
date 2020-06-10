package com.radsoltan.model.reinforcement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
        return getAreaOfReinforcementLayers(topReinforcement, additionalTopReinforcement, topReinforcementSpacing).stream()
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    @Override
    public double getCentroidOfTopReinforcement(int nominalCoverTop, int transverseBarDiameter) {
        List<Double> areaOfTopLayers = getAreaOfReinforcementLayers(topReinforcement, additionalTopReinforcement, topReinforcementSpacing);
        List<Double> firstMomentOfAreaOfTopLayers = getFirstMomentOfAreaReinforcementLayers(areaOfTopLayers, topReinforcement, additionalTopReinforcement, topReinforcementVerticalSpacing, nominalCoverTop, transverseBarDiameter);

        double sumOfAreas = getTotalAreaOfTopReinforcement();
        double sumOfFirstMomentsOfArea = firstMomentOfAreaOfTopLayers.stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        return sumOfFirstMomentsOfArea / sumOfAreas;
    }

    @Override
    public double getTotalAreaOfBottomReinforcement() {
        return getAreaOfReinforcementLayers(bottomReinforcement, additionalBottomReinforcement, bottomReinforcementSpacing).stream()
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    @Override
    public double getCentroidOfBottomReinforcement(int nominalCoverBottom, int transverseBarDiameter) {
        List<Double> areaOfBottomLayers = getAreaOfReinforcementLayers(bottomReinforcement, additionalBottomReinforcement, bottomReinforcementSpacing);
        List<Double> firstMomentOfAreaOfBottomLayers = getFirstMomentOfAreaReinforcementLayers(areaOfBottomLayers, bottomReinforcement, additionalBottomReinforcement, bottomReinforcementVerticalSpacing, nominalCoverBottom, transverseBarDiameter);

        double sumOfAreas = getTotalAreaOfBottomReinforcement();
        double sumOfFirstMomentsOfArea = firstMomentOfAreaOfBottomLayers.stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        return sumOfFirstMomentsOfArea / sumOfAreas;
    }

    public List<Double> getAreaOfReinforcementLayers(List<Integer> reinforcement, List<Integer> additionalReinforcement, List<Integer> spacing) {
        if (additionalReinforcement != null) {
            List<Double> areaOfReinforcementLayers = IntStream.range(0, additionalReinforcement.size())
                    .mapToObj(i -> 0.25 * Math.PI * (reinforcement.get(i) * reinforcement.get(i) + additionalReinforcement.get(i) * additionalReinforcement.get(i)) * 1000 / spacing.get(i))
                    .collect(Collectors.toList());
            List<Double> areaOfLayersWithoutAdditionalRebar = IntStream.range(additionalReinforcement.size(), reinforcement.size())
                    .mapToObj(i -> 0.25 * Math.PI * reinforcement.get(i) * reinforcement.get(i) * 1000 / spacing.get(i))
                    .collect(Collectors.toList());

            areaOfReinforcementLayers.addAll(areaOfLayersWithoutAdditionalRebar);

            return areaOfReinforcementLayers;
        } else {
            return IntStream.range(0, reinforcement.size())
                    .mapToObj(i -> 0.25 * Math.PI * reinforcement.get(i) * reinforcement.get(i) * 1000 / spacing.get(i))
                    .collect(Collectors.toList());
        }
    }

    // TODO: 10/06/2020 Encapsulation
    public List<Double> getDistanceFromCentreOfEachLayerToEdge(List<Integer> reinforcement, List<Integer> additionalReinforcement, List<Integer> verticalSpacing, int nominalCover, int transverseBarDiameter) {

        List<Double> distanceFromCentroidOfEachLayerToEdge = new ArrayList<>();

        double distanceForFirstLayer = (additionalReinforcement != null) ?
                0.5 * Math.max(Collections.max(reinforcement), Collections.max(additionalReinforcement)) + nominalCover + transverseBarDiameter :
                0.5 * Collections.max(reinforcement) + nominalCover + transverseBarDiameter;

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

    public List<Double> getFirstMomentOfAreaReinforcementLayers(List<Double> areaOfReinforcementLayers, List<Integer> reinforcement, List<Integer> additionalReinforcement, List<Integer> verticalSpacing, int nominalCover, int transverseBarDiameter) {
        List<Double> distanceFromEachLayerToEdge = getDistanceFromCentreOfEachLayerToEdge(reinforcement, additionalReinforcement, verticalSpacing, nominalCover, transverseBarDiameter);

        return IntStream
                .range(0, distanceFromEachLayerToEdge.size())
                .mapToObj(i -> areaOfReinforcementLayers.get(i) * distanceFromEachLayerToEdge.get(i))
                .collect(Collectors.toList());
    }

    @Override
    public void draw() {

    }
}
