package com.radsoltan.model.geometry;

public class TShape extends Shape {

    private final int width;
    private final int depth;
    private final int flangeWidth;
    private final int flangeThickness;

    public TShape(int width, int depth, int flangeWidth, int flangeThickness) {
        this.width = width;
        this.depth = depth;
        this.flangeWidth = flangeWidth;
        this.flangeThickness = flangeThickness;
    }

    @Override
    public double calculateArea() {
        return width * (depth - flangeThickness) + flangeWidth * flangeThickness;
    }

    @Override
    public double calculateFirstMomentOfArea() {
        // TODO: 18/05/2020 Implement method
        return 0;
    }

    @Override
    public double calculateSecondMomentOfArea() {
        // TODO: 18/05/2020 ImplementMethod
        return 0;
    }

    @Override
    public double getWidthInCompressionZone(double UlsMoment, double effectiveDepth, double fcd) {
        if (isNeutralAxisIsInFlange(UlsMoment, flangeWidth, flangeThickness, effectiveDepth, fcd)) {
            return flangeWidth;
        }
        return width;
    }

    @Override
    public int getDepth() {
        return depth;
    }

    private boolean isNeutralAxisIsInFlange(double UlsMoment, int flangeWidth, int flangeThickness, double effectiveDepth, double fcd) {
        if (UlsMoment > 0) {
            double capacity = flangeWidth * flangeThickness * fcd * (effectiveDepth - 0.5 * flangeThickness);
            return capacity > UlsMoment;
        }
        return false;
    }

    @Override
    public void draw() {
        // TODO: 18/05/2020
    }
}
