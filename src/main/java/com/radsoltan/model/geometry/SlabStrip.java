package com.radsoltan.model.geometry;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class SlabStrip extends Rectangle {

    private static final int DEFAULT_END_ARCH_DEPTH = 15;

    private final int endArchDepth;

    public SlabStrip(int width, int thickness, GraphicsContext graphicsContext, Color stroke, Color fill, double startX, double startY, int endArchDepth) {
        super(width, thickness, graphicsContext, stroke, fill, startX, startY);
        this.endArchDepth = endArchDepth;
    }

    public SlabStrip(int width, int thickness, GraphicsContext graphicsContext, Color stroke, Color fill, double startX, double startY) {
        this(width, thickness, graphicsContext, stroke, fill, startX, startY, DEFAULT_END_ARCH_DEPTH);
    }

    public SlabStrip(int thickness) {
        super(1000, thickness);
        this.endArchDepth = DEFAULT_END_ARCH_DEPTH;
    }

    public int getEndArchDepth() {
        return endArchDepth;
    }

    @Override
    public String getDescription() {
        return String.format("Slab: %d mm thick", getDepth());
    }

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
