package com.radsoltan.model;

import com.radsoltan.model.reinforcement.Reinforcement;

public interface Flexure {
    double calculateBendingCapacity();

    default double calculateKFactor(double UlsMoment, int width, double effectiveDepth, Concrete concrete) {
        return UlsMoment / (width * effectiveDepth * effectiveDepth * concrete.getCompressiveStrength());
    }

    default double calculateKDashFactor(boolean isRecommendedKDash, double redistributionRatio) {
        return (isRecommendedKDash) ? 0.168 : 0.6 * redistributionRatio - 0.18 * redistributionRatio * redistributionRatio - 0.21;
    }

    default double calculateEffectiveDepth(int depth, double UlsMoment, Reinforcement reinforcement, DesignParameters designParameters, int transverseBarDiameter) {
        return (UlsMoment > 0) ?
                depth - reinforcement.calculateCentroidOfBottomReinforcement(designParameters.getNominalCoverBottom(), transverseBarDiameter) :
                depth - reinforcement.calculateCentroidOfTopReinforcement(designParameters.getNominalCoverTop(), transverseBarDiameter);
    }

    default double calculateLeverArm(double effectiveDepth, double kFactor) {
        return effectiveDepth / 2 * (1 + Math.sqrt(1 - 3.53 * kFactor));
    }
}
