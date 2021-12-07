package com.radsoltan.model.geometry;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Rectangle extends Shape {

    private final int width;
    private final int depth;
    private final GraphicsContext graphicsContext;
    private final Color stroke;
    private final Color fill;
    private final double startX;
    private final double startY;

    public Rectangle(int width, int depth, GraphicsContext graphicsContext, Color stroke, Color fill, double startX, double startY) {
        this.width = width;
        this.depth = depth;
        this.graphicsContext = graphicsContext;
        this.stroke = stroke;
        this.fill = fill;
        this.startX = startX;
        this.startY = startY;
    }

    public Rectangle(int width, int depth) {
        this(width, depth, null, null, null, 0, 0);
    }

    @Override
    public int getDepth() {
        return depth;
    }

    @Override
    public int getWidth() { return width; }

    @Override
    public int getWebWidth() {
        return width;
    }

    @Override
    public GraphicsContext getGraphicsContext() {
        return graphicsContext;
    }

    @Override
    public Color getStroke() { return stroke; }

    @Override
    public Color getFill() { return fill; }

    @Override
    public double getStartX() { return startX; }

    @Override
    public double getStartY() { return startY; }

    @Override
    public double getArea() {
        return width * depth;
    }

    @Override
    public double getCentroid() {
        return 0.5 * depth;
    }

    @Override
    public double getSecondMomentOfArea() {
        return width * Math.pow(depth, 3) / 12;
    }

    @Override
    public int getWidthInCompressionZone(double UlsMoment) {
        return getWebWidth();
    }

    @Override
    public int getWidthInTensionZone(double UlsMoment) {
        return getWebWidth();
    }

    @Override
    public double getAreaInTensionZonePriorCracking(double UlsMoment) {
        return getWidthInTensionZone(UlsMoment) * getCentroid();
    }

    @Override
    public double getFactorForNonUniformSelfEquilibratingStresses(double UlsMoment) {
        if (depth <= 300) {
            return 1.0;
        } else if (depth < 800) {
            return (800 - depth) * 0.35 / 500 + 0.65;
        } else {
            return 0.65;
        }
    }

    @Override
    public double getFactorForStressDistributionPriorCracking(double UlsMoment) {
        return 0.4;
    }

    @Override
    public String getDescription() {
        return String.format("Rectangle: %d mm deep x %d mm wide.", depth, width);
    }

    @Override
    public void draw() {
        // TODO: 18/05/2020
    }
}
