package com.radsoltan.model;

import com.radsoltan.model.geometry.Geometry;
import com.radsoltan.model.geometry.Shape;
import com.radsoltan.model.reinforcement.Reinforcement;

public interface Flexure {
    void calculateBendingCapacity();

    default double getKFactor(double UlsMoment, int widthInCompressionZone, double effectiveDepth, int fck) {
        return Math.abs(UlsMoment) * Math.pow(10, 6) / (widthInCompressionZone * effectiveDepth * effectiveDepth * fck);
    }

    default double getKDashFactor(boolean isRecommendedRatio, double redistributionRatio) {
        return (isRecommendedRatio) ? 0.168 : 0.6 * redistributionRatio - 0.18 * redistributionRatio * redistributionRatio - 0.21;
    }

    default double getEffectiveDepth(int depth, double UlsMoment, Reinforcement reinforcement, DesignParameters designParameters, int transverseBarDiameter) {
        return (UlsMoment >= 0) ?
                depth - reinforcement.getCentroidOfBottomReinforcement(designParameters.getNominalCoverBottom(), transverseBarDiameter) :
                depth - reinforcement.getCentroidOfTopReinforcement(designParameters.getNominalCoverTop(), transverseBarDiameter);
    }

    default double getLeverArm(double effectiveDepth, double kFactor, double kDashFactor) {
        return (kFactor <= kDashFactor) ?
                Math.min(effectiveDepth / 2 * (1 + Math.sqrt(1 - 3.53 * kFactor)), 0.95) :
                effectiveDepth / 2 * (1 + Math.sqrt(1 - 3.53 * kDashFactor));
    }

    default double getMinimumReinforcement(double UlsMoment, double fctm, int fy, double effectiveDepth, Geometry geometry) {
        double minimumAreaFromSectionNine = Math.max(0.26 * fctm / fy, 0.0013) * geometry.getWidthInTensionZone(UlsMoment) * effectiveDepth;
        double k = geometry.getFactorForNonUniformSelfEquilibratingStresses(UlsMoment);
        double kc = geometry.getFactorForStressDistributionPriorCracking(UlsMoment);
        double areaInTensionZone = geometry.getAreaInTensionZonePriorCracking(UlsMoment);
        double minimumAreaFromSectionSeven = kc * k * fctm * areaInTensionZone / fy;
        return Math.max(minimumAreaFromSectionNine, minimumAreaFromSectionSeven);
    }

    default double getMaximumReinforcement(double concreteArea) {
        return 0.04 * concreteArea;
    }

    default double getDepthOfPlasticNeutralAxis(double effectiveDepth, double leverArm) {
        return (effectiveDepth - leverArm) / 0.4;
    }

    default double getCentroidOfCompressionReinforcement(double UlsMoment, Reinforcement reinforcement, DesignParameters designParameters, int transverseBarDiameter) {
        return (UlsMoment >= 0) ?
                reinforcement.getCentroidOfTopReinforcement(designParameters.getNominalCoverBottom(), transverseBarDiameter) :
                reinforcement.getCentroidOfBottomReinforcement(designParameters.getNominalCoverTop(), transverseBarDiameter);
    }
}
