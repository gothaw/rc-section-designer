package com.radsoltan.model.geometry;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class VerticalDimensionLine extends DimensionLine {

    private final String text;
    private final Color color;
    private final GraphicsContext graphicsContext;
    private final double startY;
    private final double endY;
    private final double x;
    private final double offsetX;
    private final double scale;
    private final boolean isTextAligned;

    public VerticalDimensionLine(String text, Color color, GraphicsContext graphicsContext, double startY, double endY, double x, double offsetX, double scale, boolean isTextAligned) {
        this.text = text;
        this.color = color;
        this.graphicsContext = graphicsContext;
        this.startY = startY;
        this.endY = endY;
        this.x = x;
        this.offsetX = offsetX;
        this.scale = scale;
        this.isTextAligned = isTextAligned;
    }

    public VerticalDimensionLine(String text, Color color, GraphicsContext graphicsContext, double startY, double endY, double x) {
        this(text, color, graphicsContext, startY, endY, x, DimensionLine.DEFAULT_OFFSET, DimensionLine.DEFAULT_SCALE, true);
    }

    @Override
    protected void drawEndLine(GraphicsContext graphicsContext, double x, double y, double scale) {
        this.drawLine(graphicsContext, x + 0.5 * DimensionLine.DEFAULT_END_LINE_LENGTH * scale, y, x - 0.5 * DimensionLine.DEFAULT_END_LINE_LENGTH * scale, y);
    }

    @Override
    protected void drawText(String text, GraphicsContext graphicsContext, double x, double y, double scale) {
        graphicsContext.beginPath();
        Font font = new Font(DimensionLine.DEFAULT_TEXT_FONT, DimensionLine.DEFAULT_TEXT_SIZE * scale);
        graphicsContext.setFont(font);
        if (isTextAligned) {
            graphicsContext.setTextAlign(TextAlignment.CENTER);
            rotate(graphicsContext, -90, x ,y);
        } else {
            graphicsContext.setTextBaseline(VPos.CENTER);
        }
        graphicsContext.fillText(text, x, y);
        graphicsContext.closePath();
    }

    @Override
    public void draw() {
        graphicsContext.setStroke(color);
        graphicsContext.setFill(color);

        this.drawLine(
                graphicsContext,
                x + offsetX,
                startY - DimensionLine.DEFAULT_MAIN_LINE_EXTENSION * scale,
                x + offsetX,
                endY + DimensionLine.DEFAULT_MAIN_LINE_EXTENSION * scale
        );
        this.drawEndLine(graphicsContext, x + offsetX, startY, scale);
        this.drawEndLine(graphicsContext, x + offsetX, endY, scale);
        this.drawTick(graphicsContext, x + offsetX, startY, scale);
        this.drawTick(graphicsContext, x + offsetX, endY, scale);
        this.drawText(text, graphicsContext, x + offsetX - DimensionLine.DEFAULT_TEXT_OFFSET, 0.5 * (startY + endY), scale);
    }
}
