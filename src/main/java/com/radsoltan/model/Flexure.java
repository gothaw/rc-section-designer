package com.radsoltan.model;

import com.radsoltan.model.reinforcement.Reinforcement;

public interface Flexure {
    double calculateBendingCapacity();

    default double calculateKFactor(double UlsMoment, int widthInCompressionZone, double effectiveDepth, Concrete concrete) {
        return Math.abs(UlsMoment) * Math.pow(10,6) / (widthInCompressionZone * effectiveDepth * effectiveDepth * concrete.getCompressiveStrength());
    }

    default double calculateKDashFactor(boolean isRecommendedRatio, double redistributionRatio) {
        return (isRecommendedRatio) ? 0.168 : 0.6 * redistributionRatio - 0.18 * redistributionRatio * redistributionRatio - 0.21;
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
