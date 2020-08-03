package com.radsoltan.model.geometry;

import com.radsoltan.model.Drawable;

public abstract class Shape implements Drawable {

    public abstract int getDepth();

    public abstract int getWidth();

    public abstract double getArea();

    public abstract double getCentroid();

    public abstract double getSecondMomentOfArea();

    public abstract int getWidthInCompressionZone(double UlsMoment);

    public abstract int getWidthInTensionZone(double UlsMoment);

    public abstract double getAreaInTensionZonePriorCracking(double UlsMoment);

    public abstract double getFactorForNonUniformSelfEquilibratingStresses(double UlsMoment);

    public abstract double getFactorForStressDistributionPriorCracking(double UlsMoment);

    public abstract String getDescription();
}
