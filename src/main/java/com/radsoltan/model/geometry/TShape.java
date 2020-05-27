package com.radsoltan.model.geometry;

public class TShape extends Shape {

    private final int width;
    private final int depth;
    private final int downstandDepth;
    private final int flangeWidth;
    private final int flangeThickness;

    public TShape(int width, int depth, int flangeWidth, int flangeThickness) {
        this.width = width;
        this.depth = depth;
        this.flangeWidth = flangeWidth;
        this.flangeThickness = flangeThickness;
        this.downstandDepth = depth - flangeThickness;
    }

    @Override
    public double calculateArea() {
        return width * downstandDepth + flangeWidth * flangeThickness;
    }

    @Override
    public double calculateCentroid() {
        double area = calculateArea();
        double firstMomentOfArea = flangeWidth * flangeThickness * 0.5 * flangeThickness + width * downstandDepth * 0.5 * (depth + flangeThickness);
        return firstMomentOfArea / area;
    }

    @Override
    public double calculateSecondMomentOfArea() {
        double centroid = calculateCentroid();
        return flangeWidth * Math.pow(flangeThickness, 3) / 12
                + flangeWidth * flangeThickness * Math.pow(centroid - 0.5 * flangeThickness, 2)
                + width * Math.pow(downstandDepth, 3) / 12
                + width * downstandDepth * Math.pow(centroid - 0.5 * (depth + flangeThickness), 2);
    }

    @Override
    public int getWidthInCompressionZone(double UlsMoment, double effectiveDepth, double fcd) {
        if (isPlasticNeutralAxisIsInFlange(UlsMoment, effectiveDepth, fcd)) {
            return flangeWidth;
        }
        return width;
    }

    @Override
    public int getWidthInTensionZone(double UlsMoment) {
        if (UlsMoment >= 0) {
            return width;
        } else {
            return flangeWidth;
        }
    }

    @Override
    public int getDepth() {
        return depth;
    }

    private boolean isPlasticNeutralAxisIsInFlange(double UlsMoment, double effectiveDepth, double fcd) {
        if (UlsMoment >= 0) {
            double flangeCapacity = flangeWidth * flangeThickness * fcd * (effectiveDepth - 0.5 * flangeThickness);
            return flangeCapacity > UlsMoment;
        } else {
            double webCapacity = width * (depth - flangeWidth) * fcd * (effectiveDepth - 0.5 * (depth - flangeWidth));
            return UlsMoment > webCapacity;
        }
    }


    @Override
    public void draw() {
        // TODO: 18/05/2020
    }
}
