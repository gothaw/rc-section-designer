package com.radsoltan.model.geometry;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Class used to describe a slab strip.
 * This inherits from Rectangle class.
 */
public class SlabStrip extends Rectangle {

    public static final int DEFAULT_END_ARCH_DEPTH = 15;

    private final int endArchDepth;

    /**
     * Constructor used for drawing the slab.  It takes additional parameters that allows for drawing the section.
     *
     * @param width           width of the slab strip
     * @param thickness       slab thickness
     * @param graphicsContext graphics context to draw the slab strip on
     * @param stroke          stroke to draw slab strip with
     * @param fill            fill to fill the slab strip with
     * @param startX          drawing start point, x coordinate
     * @param startY          drawing start point, y coordinate
     * @param endArchDepth    end arch that is used to mark end of the slab on the drawing
     */
    public SlabStrip(int width, int thickness, GraphicsContext graphicsContext, Color stroke, Color fill, double startX, double startY, int endArchDepth) {
        super(width, thickness, graphicsContext, stroke, fill, startX, startY);
        this.endArchDepth = endArchDepth;
    }

    /**
     * Constructor used for drawing the slab. Uses default end arch depth.
     *
     * @param width           width of the slab strip
     * @param thickness       slab thickness
     * @param graphicsContext graphics context to draw the slab strip on
     * @param stroke          stroke to draw slab strip with
     * @param fill            fill to fill the slab strip with
     * @param startX          drawing start point, x coordinate
     * @param startY          drawing start point, y coordinate
     */
    public SlabStrip(int width, int thickness, GraphicsContext graphicsContext, Color stroke, Color fill, double startX, double startY) {
        this(width, thickness, graphicsContext, stroke, fill, startX, startY, DEFAULT_END_ARCH_DEPTH);
    }

    /**
     * Basic constructor used for calculations.
     * It's a rectangle with width of 1 m and depth that is slab thickness.
     *
     * @param thickness slab thickness
     */
    public SlabStrip(int thickness) {
        super(1000, thickness);
        this.endArchDepth = DEFAULT_END_ARCH_DEPTH;
    }

    /**
     * Gets depth of the end arch that denominates virtual end of the slab on the drawing.
     *
     * @return end arch depth
     */
    public int getEndArchDepth() {
        return endArchDepth;
    }

    /**
     * Gets slab strip description.
     *
     * @return description
     */
    @Override
    public String getDescription() {
        return String.format("Slab: %d mm thick", getDepth());
    }

    /**
     * Draws slab on provided graphics context.
     */
    @Override
    public void draw() {
        GraphicsContext graphicsContext = getGraphicsContext();
        Color fill = getFill();
        Color stroke = getStroke();

        if (graphicsContext == null || fill == null || stroke == null) {
            return;
        }

        double width = getWidth();
        double depth = getDepth();
        double leftEdgeX = getStartX();
        double topEdgeY = getStartY();
        double rightEdgeX = leftEdgeX + width;
        double bottomEdgeY = topEdgeY + depth;
        double endArchDepth = getEndArchDepth();

        graphicsContext.setFill(fill);
        graphicsContext.setStroke(stroke);

        // Drawing Slab
        graphicsContext.beginPath();
        // Top Edge
        graphicsContext.moveTo(leftEdgeX, topEdgeY);
        graphicsContext.lineTo(rightEdgeX, topEdgeY);
        // Right Edge
        graphicsContext.quadraticCurveTo(rightEdgeX - endArchDepth, topEdgeY + 0.25 * depth, rightEdgeX, topEdgeY + 0.5 * depth);
        graphicsContext.quadraticCurveTo(rightEdgeX + endArchDepth, topEdgeY + 0.75 * depth, rightEdgeX, bottomEdgeY);
        // Bottom Edge
        graphicsContext.lineTo(leftEdgeX, bottomEdgeY);
        // Left Edge
        graphicsContext.quadraticCurveTo(leftEdgeX + endArchDepth, bottomEdgeY - 0.25 * depth, leftEdgeX, bottomEdgeY - 0.5 * depth);
        graphicsContext.quadraticCurveTo(leftEdgeX - endArchDepth, bottomEdgeY - 0.75 * depth, leftEdgeX, topEdgeY);
        // Draw Slab and clean up
        graphicsContext.stroke();
        graphicsContext.fill();
        graphicsContext.closePath();
    }
}
