package com.radsoltan.model;

import com.radsoltan.model.geometry.Geometry;
import com.radsoltan.model.geometry.SlabStrip;
import com.radsoltan.model.reinforcement.SlabReinforcement;
import com.radsoltan.util.Messages;

public class Slab implements Flexure, Cracking {
    private final double UlsMoment;
    private final double SlsMoment;
    private final Geometry geometry;
    private final SlabReinforcement reinforcement;
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
    /* Results */
    private double crackWidth;
    private double bendingCapacity;
    private double requiredTensileReinforcement;

    public Slab(double UlsMoment, double SlsMoment,
                SlabStrip slabStrip, Concrete concrete,
                SlabReinforcement reinforcement,
                DesignParameters designParameters) {
        this.UlsMoment = UlsMoment;
        this.SlsMoment = SlsMoment;
        this.reinforcement = reinforcement;
        this.designParameters = designParameters;
        this.geometry = new Geometry(slabStrip);
        this.fck = concrete.getCompressiveStrength();
        this.fcd = concrete.getDesignCompressiveResistance(designParameters.getPartialFactorOfSafetyForConcrete());
        this.fctm = concrete.getMeanAxialTensileStrength();
        this.fy = designParameters.getYieldStrength();
        this.fyd = designParameters.getDesignYieldStrength();
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
            if (kFactor <= kDashFactor) {
                this.requiredTensileReinforcement = Math.max(Math.abs(UlsMoment) * Math.pow(10, 6) / (fyd * leverArm), minimumReinforcement);
                this.bendingCapacity = providedTensileReinforcement * leverArm * fyd * Math.pow(10, -6);
            } else {
                throw new IllegalArgumentException(Messages.REDESIGN_SECTION_DUE_TO_COMPRESSIVE_FORCE);
            }
        } else {
            throw new IllegalArgumentException(Messages.WRONG_CONCRETE_CLASS);
        }
    }

    @Override
    public void calculateCracks() {

    }

    public double getProvidedTensileReinforcement() {
        return providedTensileReinforcement;
    }

    public double getBendingCapacity() {
        return bendingCapacity;
    }

    public double getRequiredTensileReinforcement() {
        return requiredTensileReinforcement;
    }
}
