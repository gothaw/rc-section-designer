package com.radsoltan.model.geometry;

import com.radsoltan.model.Drawable;

public abstract class Shape implements Drawable {

    public abstract double calculateArea();
    public abstract double calculateFirstMomentOfArea();
    public abstract double calculateSecondMomentOfArea();
    public abstract double getWidthInCompressionZone();
}
