package com.radsoltan.model;

import com.radsoltan.model.geometry.Geometry;
import com.radsoltan.model.geometry.SlabStrip;
import com.radsoltan.model.reinforcement.SlabReinforcement;
import com.radsoltan.constants.UIText;

import java.io.Serializable;

/**
 * Class that describes a slab with given geometry, materials, reinforcement and internal forces.
 * It implements Flexure and Cracking interfaces.
 * Includes methods that calculate bending capacity and crack widths.
 */
public class Slab implements Flexure, Cracking, Serializable {
    private final double UlsMoment;
    private final double SlsMoment;
    private final Geometry geometry;
    private final SlabReinforcement reinforcement;
    private final DesignParameters designParameters;
    private final double effectiveDepth;
    private double leverArm;
    /* Material Properties */
    private final Concrete concrete;
    private final int fck;
    private final int fy;
    private final double fyd;
    private final double fctm;
    /* Provided Reinforcement */
    private final double providedTensileReinforcement;
    /* Results */
    private double crackWidth;
    private double bendingCapacity;
    private double requiredTensileReinforcement;
    private final double maximumReinforcement;

    private static final long serialVersionUID = 1L;

    /**
     * Slab constructor. It includes internal forces, slab geometry, reinforcement, materials and all design parameters.
     *
     * @param UlsMoment        ULS bending moment in kNm/m
     * @param SlsMoment        SLS bending moment in kNm/m
     * @param slabStrip        SlabStrip geometry object
     * @param concrete         Concrete enum
     * @param reinforcement    Slab reinforcement object
     * @param designParameters Design parameters object
     */
    public Slab(double UlsMoment, double SlsMoment,
                SlabStrip slabStrip, Concrete concrete,
                SlabReinforcement reinforcement,
                DesignParameters designParameters) {
        this.UlsMoment = UlsMoment;
        this.SlsMoment = SlsMoment;
        this.reinforcement = reinforcement;
        this.designParameters = designParameters;
        this.geometry = new Geometry(slabStrip);
        this.concrete = concrete;
        this.fck = concrete.getCompressiveStrength();
        this.fctm = concrete.getMeanAxialTensileStrength();
        this.fy = designParameters.getYieldStrength();
        this.fyd = designParameters.getDesignYieldStrength();
        this.providedTensileReinforcement = (UlsMoment >= 0) ? reinforcement.getTotalAreaOfBottomReinforcement() : reinforcement.getTotalAreaOfTopReinforcement();
        this.effectiveDepth = getEffectiveDepth(geometry.getDepth(), UlsMoment, reinforcement, designParameters);
        this.maximumReinforcement = getMaximumReinforcement(geometry.getArea() - providedTensileReinforcement);
    }

    /**
     * Calculates bending capacity of the slab with accordance to Eurocode 2. Calculations support only singly reinforced section and are valid for concrete with fck less than 50 MPa.
     * The method calculates required section properties such as minimum reinforcement, width in compression zone and lever arm.
     * Based on these bendingCapacity and requiredTensileReinforcement fields are set up with relevant bending capacity and reinforcement required for bending.
     *
     * @throws IllegalArgumentException exception if wrong concrete class or compressive force too great
     */
    @Override
    public void calculateBendingCapacity() throws IllegalArgumentException {
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
                throw new IllegalArgumentException(UIText.REDESIGN_SECTION_DUE_TO_COMPRESSIVE_FORCE);
            }
        } else {
            throw new IllegalArgumentException(UIText.WRONG_CONCRETE_CLASS);
        }
    }

    /**
     * Calculates crack widths with accordance to Eurocode 2. It requires flexure capacity to be calculated beforehand to run the calculations.
     * It uses calculateCrackWidth default method from cracking interface. The crack width value is assigned to member variable.
     */
    @Override
    public void calculateCracking() {
        if (this.bendingCapacity == 0) {
            throw new IllegalArgumentException(UIText.INVALID_BENDING_CAPACITY);
        }
        int width = geometry.getWidth();
        int depth = geometry.getDepth();
        double neutralAxis = getDepthOfPlasticNeutralAxis(effectiveDepth, leverArm);
        double maxSpacing = reinforcement.getMaxBarSpacingForTensileReinforcement(SlsMoment);
        int maxBarDiameter = reinforcement.getMaxBarDiameterForTensileReinforcement(SlsMoment);

        this.crackWidth = calculateCrackWidth(width, depth, effectiveDepth, neutralAxis, UlsMoment, SlsMoment, maxSpacing, maxBarDiameter, providedTensileReinforcement, requiredTensileReinforcement, concrete, designParameters);
    }

    /**
     * Getter for provided tensile reinforcement.
     *
     * @return provided tensile reinforcement area
     */
    public double getProvidedTensileReinforcement() {
        return providedTensileReinforcement;
    }

    /**
     * Getter for required tensile reinforcement.
     *
     * @return required tensile reinforcement area
     */
    public double getMaximumReinforcement() {
        return maximumReinforcement;
    }

    /**
     * Getter for bending Capacity.
     *
     * @return bending capacity in kNm/m
     */
    public double getBendingCapacity() {
        return bendingCapacity;
    }

    /**
     * Getter for required tensile reinforcement.
     *
     * @return required tensile reinforcement area
     */
    public double getRequiredTensileReinforcement() {
        return requiredTensileReinforcement;
    }

    /**
     * Getter for crack width.
     *
     * @return crack width
     */
    public double getCrackWidth() {
        return crackWidth;
    }
}
