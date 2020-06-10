package com.radsoltan.model;

import com.radsoltan.model.geometry.Geometry;
import com.radsoltan.model.reinforcement.BeamReinforcement;
import com.radsoltan.model.reinforcement.ShearLinks;
import com.radsoltan.util.Constants;

public class Beam implements Flexure, Shear {
    private final double UlsMoment;
    private final double UlsShear;
    private final double SlsMoment;
    private final Geometry geometry;
    private final BeamReinforcement reinforcement;
    private final ShearLinks shearLinks;
    private final DesignParameters designParameters;
    private final double effectiveDepth;
    private double leverArm;
    /* Material Properties */
    private final int fck;
    private final double fcd;
    private final int fy;
    private final double fyd;
    private final double fctm;
    /* Provided Reinforcement */
    private final double providedTensileReinforcement;
    private final double providedCompressiveReinforcement;
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
        this.reinforcement = reinforcement;
        this.shearLinks = reinforcement.getShearLinks();
        this.designParameters = designParameters;
        this.geometry = geometry;
        this.fck = concrete.getCompressiveStrength();
        this.fcd = concrete.getDesignCompressiveResistance(designParameters.getPartialFactorOfSafetyForConcrete());
        this.fctm = concrete.getMeanAxialTensileStrength();
        this.fy = reinforcement.getYieldStrength();
        this.fyd = reinforcement.getDesignYieldStrength(designParameters.getPartialFactorOfSafetyForSteel());
        this.providedCompressiveReinforcement = (UlsMoment >= 0) ? reinforcement.getTotalAreaOfTopReinforcement() : reinforcement.getTotalAreaOfBottomReinforcement();
        this.providedTensileReinforcement = (UlsMoment >= 0) ? reinforcement.getTotalAreaOfBottomReinforcement() : reinforcement.getTotalAreaOfTopReinforcement();
        this.effectiveDepth = getEffectiveDepth(geometry.getDepth(), UlsMoment, reinforcement, designParameters);
    }

    @Override
    public void calculateBendingCapacity() {
        if (fck <= 50) {
            int widthInCompressiveZone = geometry.getWidthInCompressionZone(UlsMoment);
            double kFactor = getKFactor(UlsMoment, widthInCompressiveZone, effectiveDepth, fck);
            double kDashFactor = getKDashFactor(designParameters.isRecommendedRatio(), designParameters.getRedistributionRatio());
            double minimumReinforcement = getMinimumReinforcement(UlsMoment, fctm, fy, effectiveDepth, geometry);
            this.leverArm = getLeverArm(effectiveDepth, kFactor, kDashFactor);
            if (geometry.isFlangedSection()) {
                if (geometry.checkIfPlasticNeutralAxisInFlange(UlsMoment, effectiveDepth, leverArm)) {
                    calculateBendingCapacityForRectangularSection(kFactor, kDashFactor, effectiveDepth, widthInCompressiveZone, minimumReinforcement);
                } else {
                    double flangeThickness = geometry.getFlangeThickness();
                    double flangeCapacity = 0.57 * fck * (geometry.getFlangeWidth() - geometry.getWebThickness()) * flangeThickness * (effectiveDepth - 0.5 * flangeThickness) * Math.pow(10, -6);
                    double flangeKFactor = (UlsMoment - flangeCapacity) * Math.pow(10, 6) / (fck * geometry.getWebThickness() * effectiveDepth * effectiveDepth);
                    if (flangeKFactor <= kDashFactor) {
                        double requiredReinforcementForFlangeResistance = flangeCapacity * Math.pow(10, 6) / (fyd * (effectiveDepth - 0.5 * flangeThickness));
                        this.requiredTensileReinforcement = requiredReinforcementForFlangeResistance + (UlsMoment - flangeCapacity) * Math.pow(10, 6) / (fyd * leverArm);
                        this.bendingCapacity = (providedTensileReinforcement - requiredReinforcementForFlangeResistance) * fyd * leverArm * Math.pow(10, -6) + flangeCapacity;
                    } else {
                        throw new IllegalArgumentException(Constants.REDESIGN_SECTION_DUE_TO_COMPRESSIVE_FORCE_MESSAGE);
                    }
                }
            } else {
                calculateBendingCapacityForRectangularSection(kFactor, kDashFactor, effectiveDepth, widthInCompressiveZone, minimumReinforcement);
            }
        } else {
            throw new IllegalArgumentException(Constants.WRONG_CONCRETE_CLASS_MESSAGE);
        }
    }

    private void calculateBendingCapacityForRectangularSection(double kFactor, double kDashFactor, double effectiveDepth, double widthInCompressionZone, double minimumReinforcement) {
        if (kFactor <= kDashFactor) {
            this.requiredTensileReinforcement = Math.max(Math.abs(UlsMoment) * Math.pow(10, 6) / (fyd * leverArm), minimumReinforcement);
            this.bendingCapacity = providedTensileReinforcement * leverArm * fyd * Math.pow(10, -6);
        } else {
            double depthOfPlasticNeutralAxis = getDepthOfPlasticNeutralAxis(effectiveDepth, leverArm);
            double centroidOfCompressionReinforcement = getCentroidOfCompressionReinforcement(UlsMoment, reinforcement, designParameters);
            double fsc = Math.min(700 * (depthOfPlasticNeutralAxis - centroidOfCompressionReinforcement) / depthOfPlasticNeutralAxis, fyd);
            this.requiredCompressionReinforcement = (kFactor - kDashFactor) * fck * widthInCompressionZone * effectiveDepth * effectiveDepth / (fsc * (effectiveDepth - centroidOfCompressionReinforcement));
            this.requiredTensileReinforcement = Math.max(kDashFactor * fck * widthInCompressionZone * effectiveDepth * effectiveDepth / (fyd * leverArm) + requiredCompressionReinforcement * fsc / fyd, minimumReinforcement);
            this.bendingCapacity = providedTensileReinforcement * fyd * (effectiveDepth - centroidOfCompressionReinforcement) * Math.pow(10, -6) - 0.8 * depthOfPlasticNeutralAxis * widthInCompressionZone * fcd * (0.4 * depthOfPlasticNeutralAxis - centroidOfCompressionReinforcement) * Math.pow(10, -6);
        }
    }

    @Override
    public void calculateShearCapacity() {
        
    }

    public double getProvidedTensileReinforcement() {
        return providedTensileReinforcement;
    }

    public double getProvidedCompressiveReinforcement() {
        return providedCompressiveReinforcement;
    }

    public double getBendingCapacity() {
        return bendingCapacity;
    }

    public double getRequiredTensileReinforcement() {
        return requiredTensileReinforcement;
    }

    public double getRequiredCompressionReinforcement() {
        return requiredCompressionReinforcement;
    }
}
