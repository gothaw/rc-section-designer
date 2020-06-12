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
    public double getArea() {
        return webWidth * downstandDepth + flangeWidth * flangeThickness;
    }

    @Override
    public double getCentroid() {
        double area = getArea();
        double firstMomentOfArea = flangeWidth * flangeThickness * 0.5 * flangeThickness + webWidth * downstandDepth * 0.5 * (depth + flangeThickness);
        return firstMomentOfArea / area;
    }

    @Override
    public double getSecondMomentOfArea() {
        double centroid = getCentroid();
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
    public double getAreaInTensionZonePriorCracking(double UlsMoment) {
        double centroid = getCentroid();
        double areaAboveNeutralAxis = (isElasticNeutralAxisInFlange()) ?
                centroid * flangeWidth :
                flangeThickness * flangeWidth + (centroid - flangeThickness) * webWidth;
        return (UlsMoment >= 0) ? getArea() - areaAboveNeutralAxis : areaAboveNeutralAxis;
    }

    @Override
    public double getFactorForNonUniformSelfEquilibratingStresses(double UlsMoment) {
        int sizeToConsider = (UlsMoment >= 0) ? downstandDepth : flangeWidth;
        return getFactorForNonUniformSelfEquilibratingStressesForWebOrFlange(sizeToConsider);
    }

    @Override
    public double getFactorForStressDistributionPriorCracking(double UlsMoment) {
        if (UlsMoment >= 0) {
            return 0.4;
        } else {
            if (isElasticNeutralAxisInFlange()) {
                return 0.5;
            } else {
                double areaInTensionZone = getAreaInTensionZonePriorCracking(UlsMoment);
                double centroid = getCentroid();
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
    public int getWidth() {
        return webWidth;
    }

    @Override
    public void draw() {
        // TODO: 18/05/2020
    }

    @Override
    public int getFlangeWidth() {
        return flangeWidth;
    }

    @Override
    public int getFlangeThickness() {
        return flangeThickness;
    }

    @Override
    public boolean isElasticNeutralAxisInFlange() {
        return getCentroid() < flangeThickness;
    }

    @Override
    public boolean isPlasticNeutralAxisInFlange(double UlsMoment, double effectiveDepth, double leverArm) {
        double neutralAxisDepth = 2.5 * (effectiveDepth - leverArm);
        return (UlsMoment >= 0) && neutralAxisDepth <= 2.5 * flangeThickness;
    }
}
