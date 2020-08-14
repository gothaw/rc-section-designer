package com.radsoltan.model;

public class DesignParameters {
    private int nominalCoverTop;
    private int nominalCoverSides;
    private int nominalCoverBottom;
    private int yieldStrength;
    private int aggregateSize;
    private double gammaC;
    private double gammaS;
    private double redistributionRatio;
    private boolean isRecommendedRatio;
    private boolean includeCrackingCalculations;
    private double crackWidthLimit;

    public DesignParameters(int nominalCoverTop, int nominalCoverSides, int nominalCoverBottom, int yieldStrength, int aggregateSize,
                            double gammaC, double gammaS, double redistributionRatio, boolean isRecommendedRatio, boolean includeCrackingCalculations) {
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
    }

    public int getNominalCoverTop() {
        return nominalCoverTop;
    }

    public int getNominalCoverSides() {
        return nominalCoverSides;
    }

    public int getNominalCoverBottom() {
        return nominalCoverBottom;
    }

    public double getPartialFactorOfSafetyForConcrete() {
        return gammaC;
    }

    public double getPartialFactorOfSafetyForSteel() {
        return gammaS;
    }

    public double getRedistributionRatio() {
        return redistributionRatio;
    }

    public boolean isRecommendedRatio() {
        return isRecommendedRatio;
    }

    public double getDesignYieldStrength() {
        return yieldStrength / gammaS;
    }

    public int getYieldStrength() {
        return yieldStrength;
    }

    public boolean isIncludeCrackingCalculations() {
        return includeCrackingCalculations;
    }

    public int getAggregateSize() {
        return aggregateSize;
    }

    public double getCrackWidthLimit() {
        return crackWidthLimit;
    }
}
