package com.radsoltan.model.geometry;

import com.radsoltan.model.Drawable;

public abstract class Shape implements Drawable {

    protected double area;
    protected double firstMomentOfArea;
    protected double secondMomentOfArea;

    public abstract double calculateArea();
    public abstract double calculateFirstMomentOfArea();
    public abstract double calculateSecondMomentOfArea();
    public abstract double getWidthInCompressionZone();
}
