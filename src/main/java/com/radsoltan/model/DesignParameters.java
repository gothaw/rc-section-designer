package com.radsoltan.model;

/**
 * Class that is used to describe various design parameters needed for project calculations.
 * It includes:
 * - nominal cover
 * - steel yield strength
 * - aggregate size
 * - partial factors of safety
 * - limiting crack widths
 * - moment redistribution ratio
 */
public class DesignParameters {
    private final int nominalCoverTop;
    private final int nominalCoverSides;
    private final int nominalCoverBottom;
    private final int yieldStrength;
    private final int aggregateSize;
    private final double gammaC;
    private final double gammaS;
    private final double redistributionRatio;
    private final boolean isRecommendedRatio;
    private final boolean includeCrackingCalculations;
    private final double crackWidthLimit;

    public static final int steelYoungsModulus = 200; // Steel youngs modulus in GPa

    /**
     * Constructor.
     *
     * @param nominalCoverTop             nominal cover for top edge of the structural element in mm
     * @param nominalCoverSides           nominal cover for sides of the structural element in mm, if slab assume 0
     * @param nominalCoverBottom          nominal cover for bottom edge of the structural element in mm
     * @param yieldStrength               reinforcing steel yield strength in MPa
     * @param aggregateSize               max aggregate size in mm
     * @param gammaC                      Gamma factor for concrete - cl. 2.4.2.4 in EC2
     * @param gammaS                      Gamma factor for steel - cl. 2.4.2.4 in EC2
     * @param redistributionRatio         Moment redistribution ratio
     * @param isRecommendedRatio          Should use recommended moment redistribution - 0.85
     * @param includeCrackingCalculations Should include cracking calculations
     */
    public DesignParameters(int nominalCoverTop, int nominalCoverSides, int nominalCoverBottom, int yieldStrength, int aggregateSize,
                            double gammaC, double gammaS, double redistributionRatio, boolean isRecommendedRatio, boolean includeCrackingCalculations, double crackWidthLimit) {
        this.nominalCoverTop = nominalCoverTop;
        this.nominalCoverSides = nominalCoverSides;
        this.nominalCoverBottom = nominalCoverBottom;
        this.yieldStrength = yieldStrength;
        this.aggregateSize = aggregateSize;
        this.gammaC = gammaC;
        this.gammaS = gammaS;
        this.redistributionRatio = redistributionRatio;
        this.isRecommendedRatio = isRecommendedRatio;
        this.includeCrackingCalculations = includeCrackingCalculations;
        this.crackWidthLimit = crackWidthLimit;
    }

    /**
     * Getter for nominal cover for top edge of the structural element.
     *
     * @return nominal cover for top edge
     */
    public int getNominalCoverTop() {
        return nominalCoverTop;
    }

    /**
     * Getter for nominal cover for bottom edge of the structural element.
     *
     * @return nominal cover for bottom edge
     */
    public int getNominalCoverSides() {
        return nominalCoverSides;
    }

    /**
     * Getter for nominal cover for sides of the structural element in mm, if slab assume 0.
     *
     * @return nominal cover for sides if slab assume 0
     */
    public int getNominalCoverBottom() {
        return nominalCoverBottom;
    }

    /**
     * Getter for partial factor of safety for concrete - cl. 2.4.2.4 in EC2.
     *
     * @return partial factor of safety for concrete
     */
    public double getPartialFactorOfSafetyForConcrete() {
        return gammaC;
    }

    /**
     * Getter for partial factor of safety for steel - cl. 2.4.2.4 in EC2.
     *
     * @return partial factor of safety for steel
     */
    public double getPartialFactorOfSafetyForSteel() {
        return gammaS;
    }

    /**
     * Getter for moment redistribution ratio.
     *
     * @return moment redistribution ratio
     */
    public double getRedistributionRatio() {
        return redistributionRatio;
    }

    /**
     * Getter for should use recommended moment redistribution - 0.85.
     *
     * @return is recommended ratio
     */
    public boolean isRecommendedRatio() {
        return isRecommendedRatio;
    }

    /**
     * Gets design yield strength.
     *
     * @return design yield strength.
     */
    public double getDesignYieldStrength() {
        return yieldStrength / gammaS;
    }

    /**
     * Getter for reinforcing steel yield strength.
     *
     * @return reinforcing steel yield strength
     */
    public int getYieldStrength() {
        return yieldStrength;
    }

    /**
     * Getter for should it calculate cracks.
     *
     * @return are cracking calculations to be included
     */
    public boolean isIncludeCrackingCalculations() {
        return includeCrackingCalculations;
    }

    /**
     * Getter for aggregate size.
     *
     * @return aggregate size.
     */
    public int getAggregateSize() {
        return aggregateSize;
    }

    /**
     * Getter for limiting crack width.
     *
     * @return limiting crack width
     */
    public double getCrackWidthLimit() {
        return crackWidthLimit;
    }
}
