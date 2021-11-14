package com.radsoltan.model.geometry;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Rotate;

public abstract class DimensionLine implements Drawable {

    public static final int DEFAULT_OFFSET = 50;
    public static final int DEFAULT_SCALE = 1;
    public static final int DEFAULT_MAIN_LINE_EXTENSION = 15;
    public static final int DEFAULT_END_LINE_LENGTH = 40;
    public static final int DEFAULT_TICK_LENGTH = 36;
    public static final int DEFAULT_TICK_WIDTH = 4;
    public static final int DEFAULT_TEXT_OFFSET = 10;
    public static final int DEFAULT_TEXT_SIZE = 26;
    public static final String DEFAULT_TEXT_FONT = "Source Sans Pro";

    protected abstract void drawEndLine(GraphicsContext graphicsContext, double x, double y, double scale);

    protected abstract void drawText(String text, GraphicsContext graphicsContext, double x, double y, double scale);

    /**
     * Draws line between two points
     *
     * @param graphicsContext graphics context to draw line on
     * @param startX          x coordinate of the start point
     * @param startY          y coordinate of the start point
     * @param endX            x coordinate of the end point
     * @param endY            y coordinate of the end point
     */
    protected void drawLine(GraphicsContext graphicsContext, double startX, double startY, double endX, double endY) {
        graphicsContext.beginPath();
        graphicsContext.moveTo(startX, startY);
        graphicsContext.lineTo(endX, endY);
        graphicsContext.stroke();
        graphicsContext.closePath();
    }

    protected void drawTick(GraphicsContext graphicsContext, double x, double y, double scale) {
        double width = DEFAULT_TICK_WIDTH * scale;
        double length = DEFAULT_TICK_LENGTH * scale;

        graphicsContext.beginPath();
        rotate(graphicsContext, 45, x, y);
        graphicsContext.translate(-width * 0.5, -length * 0.5);
        graphicsContext.rect(x, y, width, length);
        graphicsContext.fill();
        // Clean up
        graphicsContext.translate(width * 0.5, length * 0.5);
        rotate(graphicsContext, 0, x, y);
        graphicsContext.closePath();
    }

    /**
     * Helper method that rotates graphics context around x and y point by specified angle
     *
     * @param graphicsContext graphics context to be rotated
     * @param angle           angle in degrees
     * @param x               x coordinate of the centre of rotation
     * @param y               y coordinate of the centre of rotation
     */
    protected void rotate(GraphicsContext graphicsContext, double angle, double x, double y) {
        Rotate rotate = new Rotate(angle, x, y);
        graphicsContext.setTransform(rotate.getMxx(), rotate.getMyx(), rotate.getMxy(), rotate.getMyy(), rotate.getTx(), rotate.getTy());
    }
}
