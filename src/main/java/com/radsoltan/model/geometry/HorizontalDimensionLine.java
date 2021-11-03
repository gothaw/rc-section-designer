package com.radsoltan.model.geometry;

import javafx.scene.canvas.GraphicsContext;

public class HorizontalDimensionLine extends DimensionLine {

    private final GraphicsContext graphicsContext;
    private final double startX;
    private final double endX;
    private final double y;
    private final double offsetY;
    private final double scale;

    public HorizontalDimensionLine(GraphicsContext graphicsContext, double startX, double endX, double y, double offsetY, double scale) {
        this.graphicsContext = graphicsContext;
        this.startX = startX;
        this.endX = endX;
        this.y = y;
        this.offsetY = offsetY;
        this.scale = scale;
    }

    public HorizontalDimensionLine(GraphicsContext graphicsContext, double startX, double endX, double y) {
        this(graphicsContext, startX, endX, y, DimensionLine.DEFAULT_OFFSET, DimensionLine.DEFAULT_SCALE);
    }

    @Override
    protected void drawMainLine() {
        graphicsContext.beginPath();
        graphicsContext.moveTo(startX - DimensionLine.DEFAULT_MAIN_LINE_EXTENSION * scale, y + offsetY);
        graphicsContext.lineTo(endX + DimensionLine.DEFAULT_MAIN_LINE_EXTENSION * scale, y + offsetY);
        graphicsContext.closePath();
    }

    @Override
    protected void drawEndLine(double x, double y) {
        graphicsContext.beginPath();
        graphicsContext.moveTo(x, y + 0.5 * DimensionLine.DEFAULT_END_LINE_LENGTH);
        graphicsContext.lineTo(x, y - 0.5 * DimensionLine.DEFAULT_END_LINE_LENGTH);
        graphicsContext.closePath();
    }

    @Override
    protected void drawTick(double x, double y) {

    }

    @Override
    protected void drawText() {

    }

    @Override
    public void draw() {
//        this.drawMainLine();
//        this.drawEndLine();
//
//        gc.moveTo(slabLeftEdgeX, slabTopEdgeY - dimensionLineOffset + 20);
//        gc.lineTo(slabLeftEdgeX, slabTopEdgeY - dimensionLineOffset - 20);
    }
}
