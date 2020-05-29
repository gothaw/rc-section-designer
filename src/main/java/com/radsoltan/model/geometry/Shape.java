package com.radsoltan.model.geometry;

import com.radsoltan.model.Drawable;

public abstract class Shape implements Drawable {

    public abstract int getDepth();

    public abstract double calculateArea();

    public abstract double calculateCentroid();

    public abstract double calculateSecondMomentOfArea();

    public abstract int getWidthInCompressionZone(double UlsMoment);

    public abstract int getWidthInTensionZone(double UlsMoment);

    public abstract double calculateAreaInTensionZonePriorCracking(double UlsMoment);

    public abstract double calculateFactorForNonUniformSelfEquilibratingStresses(double UlsMoment);

    public abstract double calculateFactorForStressDistributionPriorCracking(double UlsMoment);
}
