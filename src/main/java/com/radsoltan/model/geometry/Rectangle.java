package com.radsoltan.model.geometry;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Class used to represent rectangular section.
 */
public class Rectangle extends Section {

    private final int width;
    private final int depth;
    private final GraphicsContext graphicsContext;
    private final Color stroke;
    private final Color fill;
    private final double startX;
    private final double startY;

    /**
     * Constructor used for drawing the section. It takes additional parameters that allows for drawing the section.
     *
     * @param width           width of the rectangle
     * @param depth           depth of the rectangle
     * @param graphicsContext graphics context to draw the rectangle on
     * @param stroke          stroke to draw rectangle with
     * @param fill            fill to fill the rectangle with
     * @param startX          drawing start point, x coordinate
     * @param startY          drawing start point, y coordinate
     */
    public Rectangle(int width, int depth, GraphicsContext graphicsContext, Color stroke, Color fill, double startX, double startY) {
        this.width = width;
        this.depth = depth;
        this.graphicsContext = graphicsContext;
        this.stroke = stroke;
        this.fill = fill;
        this.startX = startX;
        this.startY = startY;
    }

    /**
     * Basic Constructor.
     *
     * @param width width of the rectangle
     * @param depth depth of the rectangle
     */
    public Rectangle(int width, int depth) {
        this(width, depth, null, null, null, 0, 0);
    }

    /**
     * Getter for rectangle depth.
     *
     * @return depth
     */
    @Override
    public int getDepth() {
        return depth;
    }

    /**
     * Getter for rectangle width.
     *
     * @return width
     */
    @Override
    public int getWidth() {
        return width;
    }

    /**
     * Gets web width - width of the rectangle.
     *
     * @return width
     */
    @Override
    public int getWebWidth() {
        return width;
    }

    /**
     * Calculates are of the rectangle.
     *
     * @return area
     */
    @Override
    public double getArea() {
        return width * depth;
    }

    /**
     * Calculates centroid of the rectangle relative to the top edge.
     *
     * @return centroid from the top edge
     */
    @Override
    public double getCentroid() {
        return 0.5 * depth;
    }

    /**
     * Calculates second moment of area.
     *
     * @return second moment of area
     */
    @Override
    public double getSecondMomentOfArea() {
        return width * Math.pow(depth, 3) / 12;
    }

    /**
     * Gets width in compression zone.
     *
     * @param UlsMoment ULS moment in kNm, positive for sagging, negative for hogging
     * @return width in compression zone
     */
    @Override
    public int getWidthInCompressionZone(double UlsMoment) {
        return getWebWidth();
    }

    /**
     * Gets width in tension zone.
     *
     * @param UlsMoment ULS moment in kNm, positive for sagging, negative for hogging
     * @return width in tension zone
     */
    @Override
    public int getWidthInTensionZone(double UlsMoment) {
        return getWebWidth();
    }

    /**
     * Calculates area of tension zone prior to cracking.
     *
     * @param UlsMoment ULS moment in kNm
     * @return area of tension zone prior to cracking
     */
    @Override
    public double getAreaInTensionZonePriorCracking(double UlsMoment) {
        return getWidthInTensionZone(UlsMoment) * getCentroid();
    }

    /**
     * Gets a k factor for the section described in cl. 7.3.2 in EC2.
     *
     * @param UlsMoment ULS moment in kNm
     * @return k factor
     */
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

    /**
     * Gets kc factor for the section described in cl. 7.3.2 in EC2.
     *
     * @param UlsMoment ULS moment in kNm
     * @return kc factor
     */
    @Override
    public double getFactorForStressDistributionPriorCracking(double UlsMoment) {
        return 0.4;
    }

    /**
     * Gets section description.
     *
     * @return section description
     */
    @Override
    public String getDescription() {
        return String.format("Rectangle: %d mm deep x %d mm wide.", depth, width);
    }

    /**
     * Getter for graphics context that section is drawn on.
     *
     * @return graphics context
     */
    @Override
    public GraphicsContext getGraphicsContext() {
        return graphicsContext;
    }

    /**
     * Getter of the stroke that the section is drawn with.
     *
     * @return section stroke
     */
    @Override
    public Color getStroke() {
        return stroke;
    }

    /**
     * Getter of the fill that the the section is drawn with.
     *
     * @return section fill.
     */
    @Override
    public Color getFill() {
        return fill;
    }

    /**
     * Getter for the X coordinate of the section entry point on canvas.
     *
     * @return X coordinate of drawing entry point
     */
    @Override
    public double getStartX() {
        return startX;
    }

    /**
     * Getter for the Y coordinate of the section entry point on canvas.
     *
     * @return Y coordinate of drawing entry point
     */
    @Override
    public double getStartY() {
        return startY;
    }

    /**
     * Draws the rectangle on provided graphics context.
     */
    @Override
    public void draw() {
        // TODO: 18/05/2020
    }
}
