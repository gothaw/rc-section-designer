package com.radsoltan.model.geometry;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Class used to represent T shaped section with symmetrical flange at the top.
 */
public class TSection extends Section implements Flanged {

    private final int webWidth;
    private final int depth;
    private final int downstandDepth;
    private final int flangeWidth;
    private final int flangeThickness;

    /**
     * Constructor.
     *
     * @param webWidth        web width in mm
     * @param depth           total depth in mm
     * @param flangeWidth     flange width in mm
     * @param flangeThickness flange thickness in mm
     */
    public TSection(int webWidth, int depth, int flangeWidth, int flangeThickness) {
        this.webWidth = webWidth;
        this.depth = depth;
        this.flangeWidth = flangeWidth;
        this.flangeThickness = flangeThickness;
        this.downstandDepth = depth - flangeThickness;
    }

    /**
     * Gets section area.
     *
     * @return section area
     */
    @Override
    public double getArea() {
        return webWidth * downstandDepth + flangeWidth * flangeThickness;
    }

    /**
     * Gets centroid of the section from the top edge of the section.
     *
     * @return centroid of the section
     */
    @Override
    public double getCentroid() {
        double area = getArea();
        double firstMomentOfArea = flangeWidth * flangeThickness * 0.5 * flangeThickness +
                webWidth * downstandDepth * (0.5 * downstandDepth + flangeThickness);
        return firstMomentOfArea / area;
    }

    /**
     * Calculates second moment of area of the section.
     *
     * @return second moment of area
     */
    @Override
    public double getSecondMomentOfArea() {
        double centroid = getCentroid();
        return flangeWidth * Math.pow(flangeThickness, 3) / 12 +
                flangeWidth * flangeThickness * Math.pow(centroid - 0.5 * flangeThickness, 2) +
                webWidth * Math.pow(downstandDepth, 3) / 12 +
                webWidth * downstandDepth * Math.pow(centroid - (0.5 * downstandDepth + flangeThickness), 2);
    }

    /**
     * Gets width of the section in compression zone.
     *
     * @param UlsMoment ULS moment in kNm, positive for sagging, negative for hogging
     * @return width in compressions zone
     */
    @Override
    public int getWidthInCompressionZone(double UlsMoment) {
        if (UlsMoment >= 0) {
            return flangeWidth;
        }
        return webWidth;
    }

    /**
     * Gets width of the section in tension zone.
     *
     * @param UlsMoment ULS moment in kNm, positive for sagging, negative for hogging
     * @return width in tension zone
     */
    @Override
    public int getWidthInTensionZone(double UlsMoment) {
        if (UlsMoment >= 0) {
            return webWidth;
        } else {
            return flangeWidth;
        }
    }

    /**
     * Gets area of the section prior to cracking
     *
     * @param UlsMoment ULS moment in kNm
     * @return area in tension zone prior to cracking
     */
    @Override
    public double getAreaInTensionZonePriorCracking(double UlsMoment) {
        double centroid = getCentroid();
        double areaAboveNeutralAxis = (isElasticNeutralAxisInFlange()) ?
                centroid * flangeWidth :
                flangeThickness * flangeWidth + (centroid - flangeThickness) * webWidth;
        return UlsMoment >= 0 ? getArea() - areaAboveNeutralAxis : areaAboveNeutralAxis;
    }

    /**
     * Gets a k factor for a section described in cl. 7.3.2 in EC2.
     *
     * @param UlsMoment ULS moment in kNm
     * @return k factor
     */
    @Override
    public double getFactorForNonUniformSelfEquilibratingStresses(double UlsMoment) {
        int sizeToConsider = (UlsMoment >= 0) ? downstandDepth : flangeWidth;
        return getFactorForNonUniformSelfEquilibratingStressesForWebOrFlange(sizeToConsider);
    }

    /**
     * Gets kc factor for a section described in cl. 7.3.2 in EC2.
     *
     * @param UlsMoment ULS moment in kNm
     * @return kc factor
     */
    @Override
    public double getFactorForStressDistributionPriorCracking(double UlsMoment) {
        if (UlsMoment >= 0) {
            return 0.4;
        } else {
            if (isElasticNeutralAxisInFlange()) {
                return 0.5;
            } else {
                double areaInTensionZone = getAreaInTensionZonePriorCracking(UlsMoment);
                double centroid = getCentroid();
                double unitTensileForceInFlange = 0.5 * flangeWidth * flangeThickness * ((centroid - flangeWidth) / centroid + 1);
                return Math.min(0.5, 0.9 * unitTensileForceInFlange / areaInTensionZone);
            }
        }
    }

    /**
     * Getter for section depth.
     *
     * @return total depth of the section
     */
    @Override
    public int getDepth() {
        return depth;
    }

    /**
     * Getter for section width.
     *
     * @return width
     */
    @Override
    public int getWidth() {
        return 0;
    }

    /**
     * Getter for web width.
     *
     * @return web width
     */
    @Override
    public int getWebWidth() {
        return webWidth;
    }

    /**
     * Getter for downstand depth.
     *
     * @return downstand depth
     */
    public int getDownstandDepth() {
        return downstandDepth;
    }

    /**
     * Getter for flange width.
     *
     * @return flange width
     */
    @Override
    public int getFlangeWidth() {
        return flangeWidth;
    }

    /**
     * Getter for flange thickness.
     *
     * @return flange thickness
     */
    @Override
    public int getFlangeThickness() {
        return flangeThickness;
    }

    /**
     * Checks if elastic neutral axis is in flange.
     *
     * @return true if elastic neutral axis is in flange
     */
    @Override
    public boolean isElasticNeutralAxisInFlange() {
        return getCentroid() < flangeThickness;
    }

    /**
     * Checks if plastic neutral axis is in flange. It uses a formula taken from Concrete Centre Guide to EC2 - chapter 4.
     *
     * @param UlsMoment      ULS moment in kNm
     * @param effectiveDepth effective depth in mm
     * @param leverArm       lever arm in mm
     * @return true if plastic neutral axis is in flange
     */
    @Override
    public boolean isPlasticNeutralAxisInFlange(double UlsMoment, double effectiveDepth, double leverArm) {
        double neutralAxisDepth = 2.5 * (effectiveDepth - leverArm);
        return (UlsMoment >= 0) && neutralAxisDepth <= 2.5 * flangeThickness;
    }

    /**
     * Gets section description.
     *
     * @return section description
     */
    @Override
    public String getDescription() {
        return String.format("T section: %d mm downstand x %d mm web width + flange %d mm wide x %d mm thick.", downstandDepth, webWidth, flangeWidth, flangeThickness);
    }

    /**
     * Getter for graphics context.
     *
     * @return graphics context
     */
    @Override
    public GraphicsContext getGraphicsContext() {
        return null;
    }

    /**
     * Getter for section stroke.
     *
     * @return section stroke
     */
    @Override
    public Color getStroke() {
        return null;
    }

    /**
     * Getter for section fill.
     *
     * @return section fill
     */
    @Override
    public Color getFill() {
        return null;
    }

    /**
     * Getter for the X coordinate of section drawing entry point on canvas.
     *
     * @return X coordinate of drawing entry point
     */
    @Override
    public double getStartX() {
        return 0;
    }

    /**
     * Getter for the Y coordinate of section drawing entry point on canvas.
     *
     * @return Y coordinate of drawing entry point
     */
    @Override
    public double getStartY() {
        return 0;
    }

    /**
     * Draws section on canvas.
     */
    @Override
    public void draw() {
        // TODO: 18/05/2020
    }
}
