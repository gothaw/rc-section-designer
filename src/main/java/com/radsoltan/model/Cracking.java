package com.radsoltan.model;

import java.util.stream.DoubleStream;

/**
 * Interface for structural elements that are subject to cracking due to uniaxial bending.
 */
public interface Cracking {

    default double calculateCracks(
            int width,
            int depth,
            double effectiveDepth,
            double neutralAxis,
            double UlsMoment,
            double SlsMoment,
            double maxSpacing,
            double maxBarDiameter,
            double providedReinforcement,
            double requirementReinforcement,
            Concrete concrete,
            DesignParameters designParameters
    ) {
        double k1 = 0.8; // for high bond bars
        double k2 = 0.5; // for bending
        double k3 = 3.4; // from National Annex, see cl. 7.3.4 in EC2
        double k4 = 0.425; // from National Annex, see cl. 7.3.4 in EC2


        double permanentLoadRatio = requirementReinforcement / providedReinforcement;
        double yieldStrength = designParameters.getYieldStrength();
        double steelPartialFactorOfSafety = designParameters.getPartialFactorOfSafetyForSteel();
        double redistributionRatio = designParameters.getRedistributionRatio();

        // Calculating service stress in steel - formula from EC2 Concrete Centre guide
        double serviceStress = yieldStrength / steelPartialFactorOfSafety * permanentLoadRatio * requirementReinforcement / providedReinforcement / redistributionRatio;

        double effectiveTensionArea = calculateEffectiveTensionArea(width, depth, effectiveDepth, neutralAxis);

        double reinforcingRatio = providedReinforcement / effectiveTensionArea;

        if (isReinforcementCloselySpaced(maxSpacing, maxBarDiameter, SlsMoment, designParameters)) {
            return 1.0;
        } else {
            throw new IllegalArgumentException("Error");
        }
    }

    default double calculateEffectiveTensionArea(double width, double depth, double effectiveDepth, double neutralAxis) {
        double effectiveTensionDepth = DoubleStream.of(2.5 * (depth - effectiveDepth), (depth - neutralAxis) / 3, depth / 2)
                .min()
                .orElse(0);

        return width * effectiveTensionDepth;
    }

    default double calculateDifferenceBetweenSteelAndConcreteStrain(double serviceStress, Concrete concrete, double reinforcingRatio) {
        double loadingFactor = 0.4; // long term

        double fctm = concrete.getMeanAxialTensileStrength();
        double concreteYoungsModulus = concrete.getSecantYoungsModulus(); // in GPa
        double steelYoungsModulus = DesignParameters.steelYoungsModulus; // in GPa

        double alpha = steelYoungsModulus / concreteYoungsModulus;

        return Math.max(
                (serviceStress - loadingFactor * fctm / reinforcingRatio * (1 + alpha * reinforcingRatio)) / (steelYoungsModulus * 1000),
                0.6 * serviceStress / (steelYoungsModulus * 1000)
                );
    }

    default boolean isReinforcementCloselySpaced(double maxSpacing, double maxBarDiameter, double SlsMoment, DesignParameters designParameters) {
        int nominalCover = SlsMoment >= 0 ? designParameters.getNominalCoverBottom() : designParameters.getNominalCoverTop();

        return maxSpacing <= 5 * (nominalCover + 0.5 * maxBarDiameter);
    }
}
