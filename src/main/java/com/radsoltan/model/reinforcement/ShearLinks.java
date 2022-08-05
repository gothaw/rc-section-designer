package com.radsoltan.model.reinforcement;

import com.radsoltan.constants.UIText;
import com.radsoltan.model.DesignParameters;
import com.radsoltan.model.geometry.Drawable;
import com.radsoltan.model.geometry.Rectangle;
import com.radsoltan.model.geometry.Section;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.io.Serializable;

/**
 * Class used to describe shear links for a beam.
 */
public class ShearLinks implements Drawable, Serializable {

    private final int fyw;
    private final int diameter;
    private final int spacing;
    private final int legs;
    private final DesignParameters designParameters;
    private final Section section;
    private final GraphicsContext graphicsContext;
    private final Color colour;
    private final double beamImageScale;
    // Constants used in drawing shear links
    public static final int DEFAULT_TEXT_SIZE = 10;
    public static final int DEFAULT_TEXT_OFFSET = 5;
    public static final String DEFAULT_TEXT_FONT = "Source Sans Pro";

    private static final long serialVersionUID = 1L;

    /**
     * Constructor. Used in structural calculations.
     *
     * @param fyw      field strength in MPa
     * @param diameter shear links diameter in mm
     * @param spacing  shear links spacing in mm
     * @param legs     number of legs
     */
    public ShearLinks(int fyw, int diameter, int spacing, int legs) {
        this(fyw, diameter, spacing, legs, null, null, null, null, 0);
    }

    /**
     * Constructor. Used in structural calculations.
     * In addition, it takes arguments that allow for drawing shear links on graphics context.
     *
     * @param fyw              field strength in MPa
     * @param diameter         shear links diameter in mm
     * @param spacing          shear links spacing in mm
     * @param legs             number of legs
     * @param designParameters DesignParameters object
     * @param section          beam section in scale that is to be drawn
     * @param graphicsContext  graphics context to draw beam on
     * @param colour           colour to draw the reinforcement with
     * @param beamImageScale   beam image scale that section is drawn with
     */
    public ShearLinks(int fyw,
                      int diameter,
                      int spacing,
                      int legs,
                      DesignParameters designParameters,
                      Section section,
                      GraphicsContext graphicsContext,
                      Color colour,
                      double beamImageScale) {
        this.fyw = fyw;
        this.diameter = diameter;
        this.spacing = spacing;
        this.legs = legs;
        this.designParameters = designParameters;
        this.section = section;
        this.graphicsContext = graphicsContext;
        this.colour = colour;
        this.beamImageScale = beamImageScale;
    }

    /**
     * Getter for shear links yield strength.
     *
     * @return yield strength
     */
    public int getYieldStrength() {
        return fyw;
    }

    /**
     * Getter for shear links diameter.
     *
     * @return shear links diameter
     */
    public int getDiameter() {
        return diameter;
    }

    /**
     * Getter for shear links spacing.
     *
     * @return shear links spacing
     */
    public int getSpacing() {
        return spacing;
    }

    /**
     * Getter for number of shear links legs.
     *
     * @return number of shear legs
     */
    public int getLegs() {
        return legs;
    }

    /**
     * Gets area of the shear links per 1 m run of the beam.
     *
     * @return area of shear links per 1 m
     */
    public double getArea() {
        return legs * 0.25 * diameter * diameter * Math.PI * 1000 / spacing;
    }

    /**
     * Gets shear links description.
     *
     * @param shouldIncludeLabel determines whether or not include links label
     * @return shear links description
     */
    public String getShearLinksDescription(boolean shouldIncludeLabel) {
        String label = shouldIncludeLabel ? "Shear links:\n" : "";

        return String.format("%s%d x \u03c6%d@%d mm", label, legs, diameter, spacing);
    }

    /**
     * Draws shear links description to the right of the beam image in the middle of the height.
     */
    private void drawShearLinksDescription(double beamRightEdgeX, double beamTopEdgeY, double beamBottomEdgeY) {
        if (!isSetupToBeDrawn()) {
            throw new IllegalArgumentException(UIText.INVALID_BEAM_REINFORCEMENT);
        }
        graphicsContext.beginPath();

        String description = getShearLinksDescription(false);

        Font font = new Font(ShearLinks.DEFAULT_TEXT_FONT, ShearLinks.DEFAULT_TEXT_SIZE);

        graphicsContext.setFont(font);
        graphicsContext.setTextAlign(TextAlignment.LEFT);

        graphicsContext.fillText(description, beamRightEdgeX + ShearLinks.DEFAULT_TEXT_OFFSET, 0.5 * (beamTopEdgeY + beamBottomEdgeY));

        graphicsContext.closePath();
    }

    /**
     * Draws shear links and shear links description.
     */
    @Override
    public void draw() {
        if (!isSetupToBeDrawn()) {
            throw new IllegalArgumentException(UIText.INVALID_SHEAR_LINKS);
        }
        if (section instanceof Rectangle) {
            graphicsContext.beginPath();
            graphicsContext.setStroke(colour);
            graphicsContext.setLineWidth(diameter * beamImageScale);

            Rectangle rectangle = (Rectangle) section;

            double beamLeftEdgeX = rectangle.getStartX();
            double beamRightEdgeX = beamLeftEdgeX + rectangle.getWidth();
            double beamTopEdgeY = rectangle.getStartY();
            double beamBottomEdgeY = beamTopEdgeY + rectangle.getDepth();
            double nominalCoverTopScaled = designParameters.getNominalCoverTop() * beamImageScale;
            double nominalCoverSidesScaled = designParameters.getNominalCoverSides() * beamImageScale;
            double nominalCoverBottomScaled = designParameters.getNominalCoverBottom() * beamImageScale;
            double diameterInScale = diameter * beamImageScale;
            double archRadius = 1.5 * diameterInScale;

            // Top line
            graphicsContext.moveTo(
                    beamLeftEdgeX + nominalCoverSidesScaled + 0.5 * diameterInScale + archRadius,
                    beamTopEdgeY + nominalCoverTopScaled + 0.5 * diameterInScale
            );
            graphicsContext.lineTo(
                    beamRightEdgeX - nominalCoverSidesScaled - 0.5 * diameterInScale - archRadius,
                    beamTopEdgeY + nominalCoverTopScaled + 0.5 * diameterInScale
            );
            // Top right arch
            graphicsContext.arc(
                    beamRightEdgeX - nominalCoverSidesScaled - 0.5 * diameterInScale - archRadius,
                    beamTopEdgeY + nominalCoverTopScaled + 0.5 * diameterInScale + archRadius,
                    archRadius,
                    archRadius,
                    90,
                    -90
            );
            // Right edge
            graphicsContext.lineTo(beamRightEdgeX - nominalCoverSidesScaled - 0.5 * diameterInScale, beamBottomEdgeY - nominalCoverBottomScaled - 0.5 * diameterInScale - archRadius);
            // Bottom right arch
            graphicsContext.arc(
                    beamRightEdgeX - nominalCoverSidesScaled - 0.5 * diameterInScale - archRadius,
                    beamBottomEdgeY - nominalCoverBottomScaled - 0.5 * diameterInScale - archRadius,
                    archRadius,
                    archRadius,
                    0,
                    -90
            );
            // Bottom edge
            graphicsContext.lineTo(beamLeftEdgeX + nominalCoverSidesScaled + 0.5 * diameterInScale + archRadius, beamBottomEdgeY - nominalCoverBottomScaled - 0.5 * diameterInScale);
            // Bottom left arch
            graphicsContext.arc(
                    beamLeftEdgeX + nominalCoverSidesScaled + 0.5 * diameterInScale + archRadius,
                    beamBottomEdgeY - nominalCoverBottomScaled - 0.5 * diameterInScale - archRadius,
                    archRadius,
                    archRadius,
                    270,
                    -90
            );
            // Left edge
            graphicsContext.lineTo(beamLeftEdgeX + nominalCoverSidesScaled + 0.5 * diameterInScale, beamTopEdgeY + nominalCoverTopScaled + 0.5 * diameterInScale + archRadius);
            // Top left arch
            graphicsContext.arc(
                    beamLeftEdgeX + nominalCoverSidesScaled + 0.5 * diameterInScale + archRadius,
                    beamTopEdgeY + nominalCoverTopScaled + 0.5 * diameterInScale + archRadius,
                    archRadius,
                    archRadius,
                    180,
                    -90
            );
            graphicsContext.stroke();

            // Drawing shear links description
            drawShearLinksDescription(beamRightEdgeX, beamTopEdgeY, beamBottomEdgeY);

            graphicsContext.closePath();
        }
    }

    /**
     * Method checks if shear links have been setup correctly and can be drawn.
     *
     * @return true if shear links can be drawn
     */
    @Override
    public boolean isSetupToBeDrawn() {
        return !(designParameters == null || section == null || graphicsContext == null || colour == null || beamImageScale == 0);
    }
}
