package com.radsoltan.model.geometry;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Rotate;

public abstract class DimensionLine implements Drawable {

    public static final int DEFAULT_OFFSET = 50;
    public static final int DEFAULT_SCALE = 1;
    public static final int DEFAULT_MAIN_LINE_EXTENSION = 15;
    public static final int DEFAULT_END_LINE_LENGTH = 40;

    protected abstract void drawMainLine();

    protected abstract void drawEndLine(double x, double y);

    protected abstract void drawTick(double x, double y);

    protected abstract void drawText();

    /**
     * Draws line between two points
     * @param graphicsContext graphics context to draw line on
     * @param startX x coordinate of the start point
     * @param startY y coordinate of the start point
     * @param endX x coordinate of the end point
     * @param endY y coordinate of the end point
     */
    protected void drawLine(GraphicsContext graphicsContext, double startX, double startY, double endX, double endY) {
        graphicsContext.beginPath();
        graphicsContext.moveTo(startX, startY);
        graphicsContext.lineTo(endX, endY);
        graphicsContext.closePath();
    }

    /**
     * Helper method that rotates graphics context around x and y point by specified angle
     * @param graphicsContext graphics context to be rotated
     * @param angle angle in degrees
     * @param x x coordinate of the centre of rotation
     * @param y y coordinate of the centre of rotation
     */
    protected void rotate(GraphicsContext graphicsContext, double angle, double x, double y) {
        Rotate rotate = new Rotate(angle, x, y);
        graphicsContext.setTransform(rotate.getMxx(), rotate.getMyx(), rotate.getMxy(), rotate.getMyy(), rotate.getTx(), rotate.getTy());
    }
}
