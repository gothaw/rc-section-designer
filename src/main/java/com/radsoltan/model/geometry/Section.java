package com.radsoltan.model.geometry;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Abstract class that should be inherited by more specific structural cross sections with a defined shape.
 * That is rectangle, T shape and L shape sections.
 */
public abstract class Section implements Drawable {

    /**
     * Getter for section depth.
     *
     * @return total depth of the section
     */
    public abstract int getDepth();

    /**
     * Getter for section width.
     *
     * @return width
     */
    public abstract int getWidth();

    /**
     * Getter for web width.
     *
     * @return web width
     */
    public abstract int getWebWidth();

    /**
     * Gets the section area.
     *
     * @return section area
     */
    public abstract double getArea();

    /**
     * Gets the centroid of the section from the top edge of the section.
     *
     * @return centroid of the section
     */
    public abstract double getCentroid();

    /**
     * Calculates second moment of area of the section.
     *
     * @return second moment of area
     */
    public abstract double getSecondMomentOfArea();

    /**
     * Gets width of the section in compression zone.
     *
     * @param UlsMoment ULS moment in kNm, positive for sagging, negative for hogging
     * @return width in compressions zone
     */
    public abstract int getWidthInCompressionZone(double UlsMoment);

    /**
     * Gets width of the section in tension zone.
     *
     * @param UlsMoment ULS moment in kNm, positive for sagging, negative for hogging
     * @return width in tension zone
     */
    public abstract int getWidthInTensionZone(double UlsMoment);

    /**
     * Gets area of the section prior to cracking.
     *
     * @param UlsMoment ULS moment in kNm
     * @return area in tension zone prior to cracking
     */
    public abstract double getAreaInTensionZonePriorCracking(double UlsMoment);

    /**
     * Gets a k factor for the section described in cl. 7.3.2 in EC2.
     *
     * @param UlsMoment ULS moment in kNm
     * @return k factor
     */
    public abstract double getFactorForNonUniformSelfEquilibratingStresses(double UlsMoment);

    /**
     * Gets kc factor for the section described in cl. 7.3.2 in EC2.
     *
     * @param UlsMoment ULS moment in kNm
     * @return kc factor
     */
    public abstract double getFactorForStressDistributionPriorCracking(double UlsMoment);

    /**
     * Gets section description.
     *
     * @return section description
     */
    public abstract String getDescription();

    /**
     * Getter for graphics context that section is drawn on.
     *
     * @return graphics context
     */
    public abstract GraphicsContext getGraphicsContext();

    /**
     * Getter of the stroke that the section is drawn with.
     *
     * @return section stroke
     */
    public abstract Color getStroke();

    /**
     * Getter of the fill that the the section is drawn with.
     *
     * @return section fill.
     */
    public abstract Color getFill();

    /**
     * Getter for the X coordinate of the section entry point on canvas.
     *
     * @return X coordinate of drawing entry point
     */
    public abstract double getStartX();

    /**
     * Getter for the Y coordinate of the section entry point on canvas.
     *
     * @return Y coordinate of drawing entry point
     */
    public abstract double getStartY();
}
