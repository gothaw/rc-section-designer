package com.radsoltan.model;

import com.radsoltan.model.geometry.Geometry;
import com.radsoltan.model.geometry.Shape;
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

    default double calculateMinimumReinforcement(double UlsMoment, double fctm, int fy, double effectiveDepth, Geometry geometry) {
        Shape shape = geometry.getShape();
        double minimumAreaFromSectionNine = Math.max(0.26 * fctm / fy, 0.0013) * shape.getWidthInTensionZone(UlsMoment) * effectiveDepth;
        double k = shape.calculateFactorForNonUniformSelfEquilibratingStresses(UlsMoment);
        double kc = shape.calculateFactorForStressDistributionPriorCracking(UlsMoment);
        double areaInTensionZone = shape.calculateAreaInTensionZonePriorCracking(UlsMoment);
        double minimumAreaFromSectionSeven = kc * k * fctm * areaInTensionZone / fy;
        return Math.max(minimumAreaFromSectionNine, minimumAreaFromSectionSeven);
    }

    default double calculateMaximumReinforcement(double concreteArea) {
        return 0.04 * concreteArea;
    }
}
