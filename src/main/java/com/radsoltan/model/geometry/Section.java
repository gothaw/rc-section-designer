package com.radsoltan.model.geometry;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class Section implements Drawable {

    public abstract int getDepth();

    public abstract int getWidth();

    public abstract int getWebWidth();

    public abstract GraphicsContext getGraphicsContext();

    public abstract Color getStroke();

    public abstract Color getFill();

    public abstract double getStartX();

    public abstract double getStartY();

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
