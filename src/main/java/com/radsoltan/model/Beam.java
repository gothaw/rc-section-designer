package com.radsoltan.model;

import com.radsoltan.model.geometry.Geometry;
import com.radsoltan.model.reinforcement.BeamReinforcement;
import com.radsoltan.model.reinforcement.ShearLinks;

public class Beam implements Flexure, Shear {
    private int widthInCompressiveZone;
    private double effectiveDepth;
    private double UlsMoment;
    private double UlsShear;
    private double SlsMoment;
    private double fcd;
    private int fy;
    private double fyd;
    private double fctm;
    private Geometry geometry;
    private BeamReinforcement reinforcement;
    private ShearLinks shearLinks;
    private Concrete concrete;
    private DesignParameters designParameters;
    private double bendingCapacity;
    private double requiredReinforcement;

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
        this.fcd = concrete.getDesignCompressiveResistance(designParameters.getPartialFactorOfSafetyForConcrete());
        this.fctm = concrete.getMeanAxialTensileStrength();
        this.fy = reinforcement.getYieldStrength();
        this.fyd = reinforcement.getDesignYieldStrength(designParameters.getPartialFactorOfSafetyForSteel());
    }

    @Override
    public void calculateBendingCapacity() {
        if (concrete.getCompressiveStrength() <= 50) {
            effectiveDepth = calculateEffectiveDepth(geometry.getDepth(), UlsMoment, reinforcement, designParameters, shearLinks.getShearLinkDiameter());
            widthInCompressiveZone = geometry.getWidthInCompressionZone(UlsMoment);
            double kFactor = calculateKFactor(UlsMoment, widthInCompressiveZone, effectiveDepth, concrete);
            double kDashFactor = calculateKDashFactor(designParameters.isRecommendedRatio(), designParameters.getRedistributionRatio());




            if (kFactor <= kDashFactor) {
                double leverArm = Math.min(calculateLeverArm(effectiveDepth, kFactor), 0.95 * effectiveDepth);
                double minimumReinforcement = calculateMinimumReinforcement(UlsMoment, fctm, fy, effectiveDepth, geometry);
                double providedReinforcement = (UlsMoment >= 0) ? reinforcement.getTotalAreaOfBottomReinforcement() : reinforcement.getTotalAreaOfTopReinforcement();
                this.requiredReinforcement = Math.max(Math.abs(UlsMoment) * Math.pow(10, 6) / (fyd * leverArm), minimumReinforcement);
                this.bendingCapacity = providedReinforcement * leverArm * fyd * Math.pow(10, -6);
            } else {
                double leverArm = calculateLeverArm(effectiveDepth, kDashFactor);
            }

            System.out.println(kDashFactor);
            System.out.println(kFactor);
            System.out.println(effectiveDepth);
            System.out.println(widthInCompressiveZone);

        } else {
            throw new IllegalArgumentException("Concrete class greater than C50/60. Outside of scope of this software.");
        }
    }

    @Override
    public void calculateShearCapacity() {

    }
}
