package com.radsoltan.model.reinforcement;

import java.util.List;
import java.util.Map;

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

    public List<Double> getAreaOfReinforcement

    @Override
    public void draw() {

    }
}
