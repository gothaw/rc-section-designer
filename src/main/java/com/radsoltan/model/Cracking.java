package com.radsoltan.model;

import com.radsoltan.util.UIText;

import java.util.Collections;
import java.util.List;

/**
 * Interface for structural elements that are subject to cracking due to uniaxial bending.
 */
public interface Cracking {

    /**
     * Interface method to calculate crack width.
     * The crack width to be assigned to a member variable and access using getter.
     */
    void calculateCracking();

    /**
     * Calculates crack width in mm with accordance to 7.3.4 in EC2. The cracking calculations are run if the reinforcement is 'reasonably closely' spaced.
     * That is if it satisfies a requirement that the spacing should be no greater than 5 * (c + 0.5d).
     *
     * @param width element width in mm
     * @param depth element depth in mm
     * @param effectiveDepth effective depth in mm
     * @param neutralAxis depth of neutral axis in mm
     * @param UlsMoment ULS moment in kNm
     * @param SlsMoment SLS moment in kNm
     * @param maxSpacing max spacing between bars in tension in mm
     * @param maxBarDiameter max bar diameters for bars in tension
     * @param providedReinforcement provided tensile reinforcement in mm2 or mm2/m
     * @param requirementReinforcement required tensile reinforcement in mm2 or mm2/m
     * @param concrete concrete enum
     * @param designParameters design parameters object
     * @return crack width in mm
     */
    default double calculateCrackWidth(
            int width,
            int depth,
            double effectiveDepth,
            double neutralAxis,
            double UlsMoment,
            double SlsMoment,
            int maxSpacing,
            int maxBarDiameter,
            double providedReinforcement,
            double requirementReinforcement,
            Concrete concrete,
            DesignParameters designParameters
    ) {
        if (UlsMoment == 0 || SlsMoment == 0) {
            return 0;
        }
        double k1 = 0.8; // for high bond bars
        double k2 = 0.5; // for bending
        double k3 = 3.4; // from National Annex, see cl. 7.3.4 in EC2
        double k4 = 0.425; // from National Annex, see cl. 7.3.4 in EC2

        double permanentLoadRatio = SlsMoment / UlsMoment;
        double yieldStrength = designParameters.getYieldStrength();
        double steelPartialFactorOfSafety = designParameters.getPartialFactorOfSafetyForSteel();
        double redistributionRatio = designParameters.getRedistributionRatio();

        // Calculating service stress in steel - formula from EC2 Concrete Centre guide
        double serviceStress = yieldStrength / steelPartialFactorOfSafety * permanentLoadRatio * requirementReinforcement / providedReinforcement / redistributionRatio;

        // Calculating effective tension area of concrete
        double effectiveTensionArea = calculateEffectiveTensionArea(width, depth, effectiveDepth, neutralAxis);

        double reinforcingRatio = providedReinforcement / effectiveTensionArea;

        int nominalCover = SlsMoment >= 0 ? designParameters.getNominalCoverBottom() : designParameters.getNominalCoverTop();

        boolean isReinforcementCloselySpaced = maxSpacing <= 5 * (nominalCover + 0.5 * maxBarDiameter);

        if (isReinforcementCloselySpaced) {
            double cracksSpacing = k3 * nominalCover + k1 * k2 * k4 * maxBarDiameter / reinforcingRatio;
            double differenceBetweenSteelAndConcreteStrain = calculateDifferenceBetweenSteelAndConcreteStrain(serviceStress, concrete, reinforcingRatio);
            return cracksSpacing * differenceBetweenSteelAndConcreteStrain;
        } else {
            throw new IllegalArgumentException(UIText.INVALID_BAR_SPACING_CRACKS);
        }
    }

    /**
     * Calculates effective tension area of concrete. Calculated with accordance to Figure 7.1 and cl. 7.3.2 (3) in EC2.
     *
     * @param width element width in tension area in mm
     * @param depth element depth in mm
     * @param effectiveDepth element effective dept in mm
     * @param neutralAxis neutral axis depth from compressive edge in mm
     * @return effective tension area of concrete in mm2
     */
    default double calculateEffectiveTensionArea(double width, double depth, double effectiveDepth, double neutralAxis) {
        double effectiveTensionDepth = Collections.min(List.of(2.5 * (depth - effectiveDepth), (depth - neutralAxis) / 3, depth / 2));

        return width * effectiveTensionDepth;
    }

    /**
     * Calculates difference between mean steel and concrete strain - see formula 7.9 in EC2.
     *
     * @param serviceStress    service stress in reinforcing steel in MPa
     * @param concrete         concrete enum
     * @param reinforcingRatio effective reinforcing ratio (ratio of provided reinforcement to effective tension area of concrete)
     * @return difference between mean steel and concrete strain
     */
    default double calculateDifferenceBetweenSteelAndConcreteStrain(double serviceStress, Concrete concrete, double reinforcingRatio) {
        double loadingFactor = 0.4; // long term loading

        double fctm = concrete.getMeanAxialTensileStrength();
        double concreteYoungsModulus = concrete.getSecantYoungsModulus(); // in GPa
        double steelYoungsModulus = DesignParameters.steelYoungsModulus; // in GPa

        double alpha = steelYoungsModulus / concreteYoungsModulus;

        return Math.max(
                (serviceStress - loadingFactor * fctm / reinforcingRatio * (1 + alpha * reinforcingRatio)) / (steelYoungsModulus * 1000),
                0.6 * serviceStress / (steelYoungsModulus * 1000)
        );
    }
}
