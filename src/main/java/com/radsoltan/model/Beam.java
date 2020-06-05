package com.radsoltan.model;

import com.radsoltan.model.geometry.Geometry;
import com.radsoltan.model.reinforcement.BeamReinforcement;
import com.radsoltan.model.reinforcement.ShearLinks;

public class Beam implements Flexure, Shear {
    /* Constructor Parameters */
    private final double UlsMoment;
    private double UlsShear;
    private double SlsMoment;
    private final Geometry geometry;
    private final BeamReinforcement reinforcement;
    private final ShearLinks shearLinks;
    private final Concrete concrete;
    private final DesignParameters designParameters;
    /* Material Properties */
    private final int fck;
    private final double fcd;
    private final int fy;
    private final double fyd;
    private final double fctm;
    /* Results */
    private double bendingCapacity;
    private double requiredTensileReinforcement;
    private double requiredCompressionReinforcement;

    public Beam(double UlsMoment, double UlsShear, double SlsMoment,
                Geometry geometry, Concrete concrete,
                BeamReinforcement reinforcement,
                DesignParameters designParameters) {
        this.UlsMoment = UlsMoment;
        this.UlsShear = UlsShear;
        this.SlsMoment = SlsMoment;
        this.concrete = concrete;
        this.reinforcement = reinforcement;
        this.shearLinks = reinforcement.getShearLinks();
        this.designParameters = designParameters;
        this.geometry = geometry;
        this.fck = concrete.getCompressiveStrength();
        this.fcd = concrete.getDesignCompressiveResistance(designParameters.getPartialFactorOfSafetyForConcrete());
        this.fctm = concrete.getMeanAxialTensileStrength();
        this.fy = reinforcement.getYieldStrength();
        this.fyd = reinforcement.getDesignYieldStrength(designParameters.getPartialFactorOfSafetyForSteel());
    }

    @Override
    public void calculateBendingCapacity() {
        if (fck <= 50) {
            double effectiveDepth = getEffectiveDepth(geometry.getDepth(), UlsMoment, reinforcement, designParameters, shearLinks.getShearLinkDiameter());
            int widthInCompressiveZone = geometry.getWidthInCompressionZone(UlsMoment);
            double kFactor = getKFactor(UlsMoment, widthInCompressiveZone, effectiveDepth, fck);
            double kDashFactor = getKDashFactor(designParameters.isRecommendedRatio(), designParameters.getRedistributionRatio());
            double leverArm = getLeverArm(effectiveDepth, kFactor, kDashFactor);
            if (geometry.checkIfFlangedSection()) {
                if (geometry.checkIfPlasticNeutralAxisInFlange(leverArm, effectiveDepth)) {
                    calculateBendingCapacityForRectangularSection(kFactor, kDashFactor, effectiveDepth, leverArm, widthInCompressiveZone);
                } else {

                }
            } else {
                calculateBendingCapacityForRectangularSection(kFactor, kDashFactor, effectiveDepth, leverArm, widthInCompressiveZone);
            }

            System.out.println(kDashFactor);
            System.out.println(kFactor);
            System.out.println(effectiveDepth);
            System.out.println(widthInCompressiveZone);

        } else {
            throw new IllegalArgumentException("Concrete class greater than C50/60. Outside of scope of this software.");
        }
    }

    private void calculateBendingCapacityForRectangularSection(double kFactor, double kDashFactor, double effectiveDepth, double leverArm, double widthInCompressionZone) {
        double minimumReinforcement = getMinimumReinforcement(UlsMoment, fctm, fy, effectiveDepth, geometry);
        double providedTensileReinforcement = (UlsMoment >= 0) ? reinforcement.getTotalAreaOfBottomReinforcement() : reinforcement.getTotalAreaOfTopReinforcement();
        if (kFactor <= kDashFactor) {
            this.requiredTensileReinforcement = Math.max(Math.abs(UlsMoment) * Math.pow(10, 6) / (fyd * leverArm), minimumReinforcement);
            this.bendingCapacity = providedTensileReinforcement * leverArm * fyd * Math.pow(10, -6);
        } else {
            double depthOfPlasticNeutralAxis = getDepthOfPlasticNeutralAxis(effectiveDepth, leverArm);
            double centroidOfCompressionReinforcement = getCentroidOfCompressionReinforcement(UlsMoment, reinforcement, designParameters, shearLinks.getShearLinkDiameter());
            double fsc = Math.min(700 * (depthOfPlasticNeutralAxis - centroidOfCompressionReinforcement) / depthOfPlasticNeutralAxis, fyd);
            // TODO: 05/06/2020 Check compression reinforcement
            this.requiredCompressionReinforcement = (kFactor - kDashFactor) * fck * widthInCompressionZone * effectiveDepth * effectiveDepth / (fsc * (effectiveDepth - centroidOfCompressionReinforcement));
            this.requiredTensileReinforcement = Math.max(kDashFactor * fck * widthInCompressionZone * effectiveDepth * effectiveDepth / (fyd * leverArm) + requiredCompressionReinforcement * fsc / fyd, minimumReinforcement);
            this.bendingCapacity = providedTensileReinforcement * fyd * (effectiveDepth - centroidOfCompressionReinforcement) * Math.pow(10, -6) - 0.8 * depthOfPlasticNeutralAxis * widthInCompressionZone * fcd * (0.4 * depthOfPlasticNeutralAxis - centroidOfCompressionReinforcement) * Math.pow(10, -6);
        }
    }

    @Override
    public void calculateShearCapacity() {

    }
}
