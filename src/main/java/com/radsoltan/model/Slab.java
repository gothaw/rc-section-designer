package com.radsoltan.model;

import com.radsoltan.model.geometry.Geometry;
import com.radsoltan.model.geometry.SlabStrip;
import com.radsoltan.model.reinforcement.SlabReinforcement;
import com.radsoltan.util.Constants;

public class Slab implements Flexure {
    private final double UlsMoment;
    private final double SlsMoment;
    private final Geometry geometry;
    private final SlabReinforcement reinforcement;
    private final DesignParameters designParameters;
    private double effectiveDepth;
    private double leverArm;
    /* Material Properties */
    private int fck;
    private double fcd;
    private int fy;
    private double fyd;
    private double fctm;
    /* Provided Reinforcement */
    private double providedTensileReinforcement;
    private double providedCompressiveReinforcement;
    /* Results */
    private double bendingCapacity;
    private double requiredTensileReinforcement;

    public Slab(double UlsMoment, double SlsMoment,
                SlabStrip strip, Concrete concrete,
                SlabReinforcement reinforcement,
                DesignParameters designParameters) {
        this.UlsMoment = UlsMoment;
        this.SlsMoment = SlsMoment;
        this.reinforcement = reinforcement;
        this.designParameters = designParameters;
        this.geometry = new Geometry(strip);
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
            if (kFactor <= kDashFactor) {
                this.requiredTensileReinforcement = Math.max(Math.abs(UlsMoment) * Math.pow(10, 6) / (fyd * leverArm), minimumReinforcement);
                this.bendingCapacity = providedTensileReinforcement * leverArm * fyd * Math.pow(10, -6);
            } else {
                throw new IllegalArgumentException(Constants.REDESIGN_SECTION_DUE_TO_COMPRESSIVE_FORCE_MESSAGE);
            }
        } else {
            throw new IllegalArgumentException(Constants.WRONG_CONCRETE_CLASS_MESSAGE);
        }
    }

}
