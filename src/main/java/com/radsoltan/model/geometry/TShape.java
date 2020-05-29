package com.radsoltan.model.geometry;

public class TShape extends Shape implements Flanged {

    private final int webWidth;
    private final int depth;
    private final int downstandDepth;
    private final int flangeWidth;
    private final int flangeThickness;

    public TShape(int webWidth, int depth, int flangeWidth, int flangeThickness) {
        this.webWidth = webWidth;
        this.depth = depth;
        this.flangeWidth = flangeWidth;
        this.flangeThickness = flangeThickness;
        this.downstandDepth = depth - flangeThickness;
    }

    @Override
    public double calculateArea() {
        return webWidth * downstandDepth + flangeWidth * flangeThickness;
    }

    @Override
    public double calculateCentroid() {
        double area = calculateArea();
        double firstMomentOfArea = flangeWidth * flangeThickness * 0.5 * flangeThickness + webWidth * downstandDepth * 0.5 * (depth + flangeThickness);
        return firstMomentOfArea / area;
    }

    @Override
    public double calculateSecondMomentOfArea() {
        double centroid = calculateCentroid();
        return flangeWidth * Math.pow(flangeThickness, 3) / 12
                + flangeWidth * flangeThickness * Math.pow(centroid - 0.5 * flangeThickness, 2)
                + webWidth * Math.pow(downstandDepth, 3) / 12
                + webWidth * downstandDepth * Math.pow(centroid - 0.5 * (depth + flangeThickness), 2);
    }

    @Override
    public int getWidthInCompressionZone(double UlsMoment) {
        if (UlsMoment >= 0) {
            return flangeWidth;
        }
        return webWidth;
    }

    @Override
    public int getWidthInTensionZone(double UlsMoment) {
        if (UlsMoment >= 0) {
            return webWidth;
        } else {
            return flangeWidth;
        }
    }

    @Override
    public double calculateAreaInTensionZonePriorCracking(double UlsMoment) {
        double centroid = calculateCentroid();
        double areaAboveNeutralAxis = (isElasticNeutralAxisInFlange()) ?
                centroid * flangeWidth :
                flangeThickness * flangeWidth + (centroid - flangeThickness) * webWidth;
        return (UlsMoment >= 0) ? calculateArea() - areaAboveNeutralAxis : areaAboveNeutralAxis;
    }

    @Override
    public double calculateFactorForNonUniformSelfEquilibratingStresses(double UlsMoment) {
        int sizeToConsider = (UlsMoment >= 0) ? downstandDepth : flangeWidth;
        return calculateFactorForNonUniformSelfEquilibratingStressesForWebOrFlange(sizeToConsider);
    }

    @Override
    public double calculateFactorForStressDistributionPriorCracking(double UlsMoment) {
        if (UlsMoment >= 0) {
            return 0.4;
        } else {
            if (isElasticNeutralAxisInFlange()) {
                return 0.5;
            } else {
                double areaInTensionZone = calculateAreaInTensionZonePriorCracking(UlsMoment);
                double centroid = calculateCentroid();
                double unitTensileForceInFlange = 0.5 * flangeWidth * flangeThickness * ((centroid - flangeWidth) / centroid + 1);
                return Math.min(0.5, 0.9 * unitTensileForceInFlange / areaInTensionZone);
            }
        }
    }

    @Override
    public int getDepth() {
        return depth;
    }

    @Override
    public void draw() {
        // TODO: 18/05/2020
    }

    @Override
    public boolean isElasticNeutralAxisInFlange() {
        return calculateCentroid() < flangeThickness;
    }

    @Override
    public boolean isPlasticNeutralAxisInFlange(double UlsMoment, double effectiveDepth, double fcd) {
        if (UlsMoment >= 0) {
            double flangeCapacity = flangeWidth * flangeThickness * fcd * (effectiveDepth - 0.5 * flangeThickness);
            return flangeCapacity > UlsMoment;
        } else {
            double webCapacity = webWidth * downstandDepth * fcd * (effectiveDepth - 0.5 * downstandDepth);
            return UlsMoment > webCapacity;
        }
    }

    public boolean isPlasticNeutralAxisInFlange(double leverArm, double effectiveDepth) {
        double neutralAxisDepth = 2.5 * (effectiveDepth - leverArm);
        return neutralAxisDepth <= 2.5 * flangeThickness;
    }
}
