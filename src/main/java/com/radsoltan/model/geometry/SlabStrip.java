package com.radsoltan.model.geometry;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class SlabStrip extends Rectangle {

    private static final int DEFAULT_END_ARCH_DEPTH = 15;

    private final int endArchDepth;

    public SlabStrip(int thickness, GraphicsContext graphicsContext, Color stroke, Color fill, int endArchDepth) {
        super(1000, thickness, graphicsContext, stroke, fill);
        this.endArchDepth = endArchDepth;
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
        // Drawing Slab
        graphicsContext.beginPath();
        // Top Edge
        graphicsContext.moveTo(slabLeftEdgeX, slabTopEdgeY);
        graphicsContext.lineTo(slabRightEdgeX, slabTopEdgeY);
        // Right Edge
        graphicsContext.quadraticCurveTo(slabRightEdgeX - END_ARCH_DEPTH, slabTopEdgeY + 0.25 * slabDepth, slabRightEdgeX, slabTopEdgeY + 0.5 * slabDepth);
        graphicsContext.quadraticCurveTo(slabRightEdgeX + END_ARCH_DEPTH, slabTopEdgeY + 0.75 * slabDepth, slabRightEdgeX, slabBottomEdgeY);
        // Bottom Edge
        graphicsContext.lineTo(slabLeftEdgeX, slabBottomEdgeY);
        // Left Edge
        graphicsContext.quadraticCurveTo(slabLeftEdgeX + END_ARCH_DEPTH, slabBottomEdgeY - 0.25 * slabDepth, slabLeftEdgeX, slabBottomEdgeY - 0.5 * slabDepth);
        graphicsContext.quadraticCurveTo(slabLeftEdgeX - END_ARCH_DEPTH, slabBottomEdgeY - 0.75 * slabDepth, slabLeftEdgeX, slabTopEdgeY);
        // Draw Slab and clean up
        graphicsContext.stroke();
        graphicsContext.fill();
        graphicsContext.closePath();
    }
}
