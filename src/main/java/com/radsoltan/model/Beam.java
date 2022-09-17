package com.radsoltan.model;

import com.radsoltan.model.geometry.Geometry;
import com.radsoltan.model.reinforcement.BeamReinforcement;
import com.radsoltan.model.reinforcement.ShearLinks;
import com.radsoltan.constants.UIText;

import java.io.Serializable;

/**
 * Class that describes a beam with given geometry, materials, reinforcement and internal forces.
 * It implements Flexure, Shear and Cracking interfaces.
 * Includes methods that calculate bending and shear capacity and crack widths.
 */
public class Beam implements Flexure, Shear, Cracking, Serializable {
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
    private final Concrete concrete;
    private final int fck;
    private final double fcd;
    private final int fy;
    private final double fyd;
    private final double fctm;
    /* Provided Reinforcement */
    private final double providedTensileReinforcement;
    private final double providedCompressiveReinforcement;
    private final double providedShearReinforcement;
    /* Results Bending */
    private double bendingCapacity;
    private double requiredTensileReinforcement;
    private double requiredCompressionReinforcement;
    private final double maximumReinforcement;
    /* Results Shear */
    private final double maximumLinksSpacing;
    private double requiredShearReinforcement;
    /* Results Cracking */
    private double crackWidth;

    private static final long serialVersionUID = 1L;

    /**
     * Beam constructor. It includes internal forces, beam geometry, reinforcement, materials and all design parameters.
     *
     * @param UlsMoment        ULS bending moment in kNm
     * @param UlsShear         ULS shear force in kN
     * @param SlsMoment        SLS bending moment in kNm
     * @param geometry         Geometry object
     * @param concrete         Concrete enum
     * @param reinforcement    Beam reinforcement
     * @param designParameters Design parameters object
     */
    public Beam(double UlsMoment, double UlsShear, double SlsMoment,
                Geometry geometry, Concrete concrete,
                BeamReinforcement reinforcement,
                DesignParameters designParameters) {
        this.UlsMoment = UlsMoment;
        this.UlsShear = Math.abs(UlsShear);
        this.SlsMoment = SlsMoment;
        this.reinforcement = reinforcement;
        this.shearLinks = reinforcement.getShearLinks();
        this.designParameters = designParameters;
        this.geometry = geometry;
        this.concrete = concrete;
        this.fck = concrete.getCompressiveStrength();
        this.fcd = concrete.getDesignCompressiveResistance(designParameters.getPartialFactorOfSafetyForConcrete());
        this.fctm = concrete.getMeanAxialTensileStrength();
        this.fy = designParameters.getYieldStrength();
        this.fyd = designParameters.getDesignYieldStrength();
        this.providedCompressiveReinforcement = (UlsMoment >= 0) ? reinforcement.getTotalAreaOfTopReinforcement() : reinforcement.getTotalAreaOfBottomReinforcement();
        this.providedTensileReinforcement = (UlsMoment >= 0) ? reinforcement.getTotalAreaOfBottomReinforcement() : reinforcement.getTotalAreaOfTopReinforcement();
        this.effectiveDepth = getEffectiveDepth(geometry.getDepth(), UlsMoment, reinforcement, designParameters);
        this.maximumLinksSpacing = getMaximumSpacingForShearLinks(effectiveDepth);
        this.maximumReinforcement = getMaximumReinforcement(geometry.getArea() - providedTensileReinforcement - providedCompressiveReinforcement);
        this.providedShearReinforcement = shearLinks.getArea();
    }

    /**
     * Calculates bending capacity of the beam with accordance to Eurocode 2. Calculations support both flanged and rectangular section.
     *
     * The method calculates required section properties such as minimum reinforcement, width in compression zone and lever arm.
     * Based on these bendingCapacity and requiredTensileReinforcement fields are set up with relevant bending capacity and reinforcement required for bending.
     * If a beam has a flanged section with plastic neutral axis in the flange, the calculations are carried out as for rectangular section - invoking a separate method.
     *
     * The calculations are based on Concrete Centre guide "How to Design Concrete Structures using Eurocode 2" January 2011 - Chapter 4, Fig. 2 and 11.
     *
     * @throws IllegalArgumentException exception if fck is greater than 50MPa or if compressive force is too great and is flanged section with plastic neutral axis in web
     */
    @Override
    public void calculateBendingCapacity() {
        if (fck <= 50) {
            int widthInCompressiveZone = geometry.getWidthInCompressionZone(UlsMoment);
            double kFactor = getKFactor(UlsMoment, widthInCompressiveZone, effectiveDepth, fck);
            double kDashFactor = getKDashFactor(designParameters.isRecommendedRatio(), designParameters.getRedistributionRatio());
            double minimumReinforcement = getMinimumReinforcement(UlsMoment, fctm, fy, effectiveDepth, geometry);
            this.leverArm = getLeverArm(effectiveDepth, kFactor, kDashFactor);
            if (geometry.isFlangedSection()) {
                // Flanged section
                if (geometry.checkIfPlasticNeutralAxisInFlange(UlsMoment, effectiveDepth, leverArm)) {
                    // PNA in the flange, treat as rectangular section
                    calculateBendingCapacityForRectangularSection(kFactor, kDashFactor, effectiveDepth, widthInCompressiveZone, minimumReinforcement);
                } else {
                    // PNA in the web
                    double flangeThickness = geometry.getFlangeThickness();
                    double flangeCapacity = 0.57 * fck * (geometry.getFlangeWidth() - geometry.getWidth()) * flangeThickness * (effectiveDepth - 0.5 * flangeThickness) * Math.pow(10, -6);
                    double flangeKFactor = (UlsMoment - flangeCapacity) * Math.pow(10, 6) / (fck * geometry.getWidth() * effectiveDepth * effectiveDepth);
                    if (flangeKFactor <= kDashFactor) {
                        double requiredReinforcementForFlangeResistance = flangeCapacity * Math.pow(10, 6) / (fyd * (effectiveDepth - 0.5 * flangeThickness));
                        this.requiredTensileReinforcement = requiredReinforcementForFlangeResistance + (UlsMoment - flangeCapacity) * Math.pow(10, 6) / (fyd * leverArm);
                        this.bendingCapacity = (providedTensileReinforcement - requiredReinforcementForFlangeResistance) * fyd * leverArm * Math.pow(10, -6) + flangeCapacity;
                    } else {
                        throw new IllegalArgumentException(UIText.REDESIGN_SECTION_DUE_TO_COMPRESSIVE_FORCE);
                    }
                }
            } else {
                // Rectangular section
                calculateBendingCapacityForRectangularSection(kFactor, kDashFactor, effectiveDepth, widthInCompressiveZone, minimumReinforcement);
            }
        } else {
            throw new IllegalArgumentException(UIText.WRONG_CONCRETE_CLASS);
        }
    }

    /**
     * Method calculates capacity of a rectangular section. It distinguishes between singly or doubly reinforced section.
     *
     * The calculations are based on Concrete Centre guide "How to Design Concrete Structures using Eurocode 2" January 2011 - Chapter 4, Fig. 2.
     *
     * @param kFactor K factor for the section
     * @param kDashFactor K' factor for the section
     * @param effectiveDepth effective depth of the section in mm
     * @param widthInCompressionZone width of the beam in compression zone in mm
     * @param minimumReinforcement minimum area of reinforcement in mm2
     */
    private void calculateBendingCapacityForRectangularSection(double kFactor, double kDashFactor, double effectiveDepth, double widthInCompressionZone, double minimumReinforcement) {
        if (kFactor <= kDashFactor) {
            // Singly reinforced section
            this.requiredTensileReinforcement = Math.max(Math.abs(UlsMoment) * Math.pow(10, 6) / (fyd * leverArm), minimumReinforcement);
            this.bendingCapacity = providedTensileReinforcement * leverArm * fyd * Math.pow(10, -6);
        } else {
            // Doubly reinforced section
            double depthOfPlasticNeutralAxis = getDepthOfPlasticNeutralAxis(effectiveDepth, leverArm);
            double centroidOfCompressionReinforcement = getCentroidOfCompressionReinforcement(UlsMoment, reinforcement, designParameters);
            double fsc = Math.min(700 * (depthOfPlasticNeutralAxis - centroidOfCompressionReinforcement) / depthOfPlasticNeutralAxis, fyd);
            this.requiredCompressionReinforcement = (kFactor - kDashFactor) * fck * widthInCompressionZone * effectiveDepth * effectiveDepth / (fsc * (effectiveDepth - centroidOfCompressionReinforcement));
            this.requiredTensileReinforcement = Math.max(kDashFactor * fck * widthInCompressionZone * effectiveDepth * effectiveDepth / (fyd * leverArm) + requiredCompressionReinforcement * fsc / fyd, minimumReinforcement);
            this.bendingCapacity = providedTensileReinforcement * fyd * (effectiveDepth - centroidOfCompressionReinforcement) * Math.pow(10, -6) - 0.8 * depthOfPlasticNeutralAxis * widthInCompressionZone * fcd * (0.4 * depthOfPlasticNeutralAxis - centroidOfCompressionReinforcement) * Math.pow(10, -6);
        }
    }

    /**
     * Calculates shear capacity for a beam with accordance to Eurocode 2 - cl. 6.2.
     *
     * The method calculates concrete shear resistance. If this is adequate to withstand the forces then minimal reinforcement is suggested.
     * Otherwise, required shear reinforcement is calculated based on the angle of compressive strut.
     *
     * @throws IllegalArgumentException exception if fck is greater than 50MPa or if shear force is too great
     */
    @Override
    public void calculateShearCapacity() {
        if (fck <= 50) {
            if (leverArm == 0) {
                // If bending capacity not calculated
                leverArm = 0.9 * effectiveDepth;
            }
            double width = geometry.getWidth();
            double shearStress = UlsShear * 1000 / (width * leverArm);
            double reinforcementRatio = providedTensileReinforcement / (width * effectiveDepth);
            double CRdc = 0.18 / designParameters.getPartialFactorOfSafetyForConcrete();
            double k = Math.min(1 + Math.sqrt(200 / effectiveDepth), 2.0);
            double minimumConcreteShearResistance = 0.035 * Math.pow(k, 1.5) * Math.pow(fck, 0.5);
            // Resistance for members not requiring shear reinforcement cl. 6.2.2
            double concreteShearResistance = Math.max(CRdc * k * Math.pow(100 * reinforcementRatio * fck, 0.333), minimumConcreteShearResistance) * width * effectiveDepth * Math.pow(10, -3); // Eq. 6.2
            double yieldStrength = shearLinks.getYieldStrength();
            double minimumShearReinforcement = 0.08 * width * Math.pow(fck, 0.5) / yieldStrength * Math.pow(10, 3); // Eq. 9.5N;
            if (UlsShear > concreteShearResistance) {
                // Calculations if shear reinforcement is needed cl. 6.2.3
                double strengthReductionFactor = 0.6 * (1 - 0.004 * fck);
                double coefficientForStressState = 1.0;
                double maxAngleOfCompressiveStrut = Math.toRadians(45);
                double maxShearResistance = coefficientForStressState * width * leverArm * strengthReductionFactor * fcd / (Math.tan(maxAngleOfCompressiveStrut) + 1 / Math.tan(maxAngleOfCompressiveStrut)); // Eq. 6.9
                if (UlsShear <= maxShearResistance) {
                    double angleOfCompressiveStrut = Math.toRadians(Math.max(0.5 * Math.asin(shearStress / (0.2 * fck * (1 - 0.004 * fck))), 21.8));
                    requiredShearReinforcement = Math.max(shearStress * width / (yieldStrength / Math.tan(angleOfCompressiveStrut)) * Math.pow(10, 3), minimumShearReinforcement);
                } else {
                    throw new IllegalArgumentException(UIText.REDESIGN_SECTION_DUE_TO_HIGH_SHEAR);
                }
            } else {
                requiredShearReinforcement = minimumShearReinforcement;
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

        // Setting up reinforcement to support SLS calculations - max bar spacing and diameter
        BeamReinforcement reinforcementForSls = new BeamReinforcement(
                reinforcement.getTopDiameters(),
                reinforcement.getTopVerticalSpacings(),
                reinforcement.getBottomDiameters(),
                reinforcement.getBottomVerticalSpacings(),
                reinforcement.getShearLinks(),
                designParameters,
                geometry.getSection()
        );

        double maxSpacing = reinforcementForSls.getMaxBarSpacingForTensileReinforcement(SlsMoment);
        int maxBarDiameter = reinforcementForSls.getMaxBarDiameterForTensileReinforcement(SlsMoment);

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
     * Getter for provided compressive reinforcement.
     *
     * @return provided compressive reinforcement area
     */
    public double getProvidedCompressiveReinforcement() {
        return providedCompressiveReinforcement;
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
     * Getter for required compressive reinforcement.
     *
     * @return required compressive reinforcement area
     */
    public double getRequiredCompressionReinforcement() {
        return requiredCompressionReinforcement;
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
     * Getter for required shear reinforcement.
     *
     * @return required shear reinforcement area
     */
    public double getRequiredShearReinforcement() {
        return requiredShearReinforcement;
    }

    /**
     * Getter for provided shear reinforcement.
     *
     * @return provided shear reinforcement area
     */
    public double getProvidedShearReinforcement() {
        return providedShearReinforcement;
    }

    /**
     * Getter for maximum shear links spacing
     *
     * @return maximum shear links spacing
     */
    public double getMaximumLinksSpacing() {
        return maximumLinksSpacing;
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
