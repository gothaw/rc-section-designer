package com.radsoltan.model;

import com.radsoltan.model.geometry.Geometry;
import com.radsoltan.model.reinforcement.Reinforcement;

/**
 * Interface for structural elements that are subject to uniaxial bending and require tensile reinforcement to resist bending moment.
 * Includes default methods that are used in typical bending moment capacity calculations.
 */
public interface Flexure {

    /**
     * Interface method to calculate bending capacity.
     * The method implementation to calculate required bending reinforcement and bending capacity.
     * These should be assigned to member variables and access using getters.
     */
    void calculateBendingCapacity();

    /**
     * Calculates K factor for a reinforced concrete section.
     * This is used to determine the size of the compression zone and used to calculate the lever arm.
     *
     * @param UlsMoment              ULS moment in kNM
     * @param widthInCompressionZone width of the compression zone in mm
     * @param effectiveDepth         effective depth in mm
     * @param fck                    characteristic compressive cylinder strength of concrete in MPa
     * @return value of K factor
     */
    default double getKFactor(double UlsMoment, int widthInCompressionZone, double effectiveDepth, int fck) {
        return Math.abs(UlsMoment) * Math.pow(10, 6) / (widthInCompressionZone * effectiveDepth * effectiveDepth * fck);
    }

    /**
     * Calculates K' factor for a reinforced concrete section.
     * This describes the limiting value of compression zone for section under bending and is also used to calculate the lever arm.
     *
     * @param isRecommendedRatio  boolean determining if recommended redistribution ratio (0.85) is to be used
     * @param redistributionRatio redistribution ratio between 1.0 and 0.7
     * @return value of K' factor
     */
    default double getKDashFactor(boolean isRecommendedRatio, double redistributionRatio) {
        return (isRecommendedRatio) ? 0.168 : 0.6 * redistributionRatio - 0.18 * redistributionRatio * redistributionRatio - 0.21;
    }

    /**
     * Calculates effective depth for a section based on reinforcement, depth and design parameters.
     *
     * @param depth            depth of the section in mm
     * @param UlsMoment        ULS moment in kNM
     * @param reinforcement    Reinforcement object
     * @param designParameters DesignParameters object
     * @return effective depth in mm
     */
    default double getEffectiveDepth(int depth, double UlsMoment, Reinforcement reinforcement, DesignParameters designParameters) {
        return (UlsMoment >= 0) ?
                depth - reinforcement.getCentroidOfBottomReinforcement(designParameters.getNominalCoverBottom()) :
                depth - reinforcement.getCentroidOfTopReinforcement(designParameters.getNominalCoverTop());
    }

    /**
     * Calculates lever arm based on K, K' and effective depth.
     *
     * @param effectiveDepth effective depth in mm
     * @param kFactor        K factor
     * @param kDashFactor    K' factor
     * @return lever arm in mm
     */
    default double getLeverArm(double effectiveDepth, double kFactor, double kDashFactor) {
        return (kFactor <= kDashFactor) ?
                Math.min(0.5 * effectiveDepth * (1 + Math.sqrt(1 - 3.53 * kFactor)), 0.95 * effectiveDepth) :
                0.5 * effectiveDepth * (1 + Math.sqrt(1 - 3.53 * kDashFactor));
    }

    /**
     * Calculates minimum reinforcement area for given reinforced concrete section.
     *
     * @param UlsMoment      ULS moment in kNm
     * @param fctm           mean value of axial tensile strength of concrete in MPa
     * @param fy             design yield strength of reinforcement in MPa
     * @param effectiveDepth effective depth in mm
     * @param geometry       geometry object
     * @return minimum area of reinforcement in mm2
     */
    default double getMinimumReinforcement(double UlsMoment, double fctm, int fy, double effectiveDepth, Geometry geometry) {
        double minimumAreaFromSectionNine = Math.max(0.26 * fctm / fy, 0.0013) * geometry.getWidthInTensionZone(UlsMoment) * effectiveDepth;
        double k = geometry.getFactorForNonUniformSelfEquilibratingStresses(UlsMoment);
        double kc = geometry.getFactorForStressDistributionPriorCracking(UlsMoment);
        double areaInTensionZone = geometry.getAreaInTensionZonePriorCracking(UlsMoment);
        double minimumAreaFromSectionSeven = kc * k * fctm * areaInTensionZone / fy;
        return Math.max(minimumAreaFromSectionNine, minimumAreaFromSectionSeven);
    }

    /**
     * Calculates maximum reinforcement area for given reinforced concrete section.
     *
     * @param concreteArea concrete area in mm2
     * @return maximum reinforcement area in mm2
     */
    default double getMaximumReinforcement(double concreteArea) {
        return 0.04 * concreteArea;
    }

    /**
     * Calculates depth of plastic neutral axis for given reinforced concrete section.
     * The depth is calculated from the compressive edge of the section
     *
     * @param effectiveDepth effective depth in mm
     * @param leverArm       lever arm in mm
     * @return effective depth in mm
     */
    default double getDepthOfPlasticNeutralAxis(double effectiveDepth, double leverArm) {
        return (effectiveDepth - leverArm) / 0.4;
    }

    /**
     * Gets centroid of compression reinforcement based on the sign of the ULS moment.
     *
     * @param UlsMoment        ULS moment in kNm
     * @param reinforcement    Reinforcement object
     * @param designParameters DesignParameters object
     * @return centroid of compression reinforcement in mm
     */
    default double getCentroidOfCompressionReinforcement(double UlsMoment, Reinforcement reinforcement, DesignParameters designParameters) {
        return (UlsMoment >= 0) ?
                reinforcement.getCentroidOfTopReinforcement(designParameters.getNominalCoverTop()) :
                reinforcement.getCentroidOfBottomReinforcement(designParameters.getNominalCoverBottom());
    }
}
