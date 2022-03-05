package com.radsoltan.model.geometry;

import com.radsoltan.util.UIText;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * Class used for creating a vertical dimension line.
 */
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

    /**
     * Constructor. Sets required dimension line properties.
     *
     * @param text            text to be displayed next to dimension line
     * @param color           dimension line colour
     * @param graphicsContext graphics context to draw the dimension line on
     * @param startY          dimension line start point - y coordinate
     * @param endY            dimension line end point - y coordinate
     * @param x               dimension line x coordinate
     * @param offsetX         offset in x direction from x coordinate
     * @param scale           dimension line scale
     * @param isTextAligned   specifies whether the text should be aligned with the dimension line (vertical) or horizontal
     */
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

    /**
     * Constructor. Uses default offset in X and default scale
     *
     * @param text            text to be displayed next to dimension line
     * @param color           dimension line colour
     * @param graphicsContext graphics context to draw the dimension line on
     * @param startY          dimension line start point - y coordinate
     * @param endY            dimension line end point - y coordinate
     * @param x               dimension line x coordinate
     */
    public VerticalDimensionLine(String text, Color color, GraphicsContext graphicsContext, double startY, double endY, double x) {
        this(text, color, graphicsContext, startY, endY, x, DimensionLine.DEFAULT_OFFSET, DimensionLine.DEFAULT_SCALE, true);
    }

    /**
     * Draws a horizontal limiting end line.
     *
     * @param graphicsContext graphics context to draw on
     * @param x               x coordinate of the centre of the line
     * @param y               y coordinate of the centre of the line
     * @param scale           dimension line scale
     */
    @Override
    protected void drawEndLine(GraphicsContext graphicsContext, double x, double y, double scale) {
        this.drawLine(graphicsContext, x + 0.5 * DimensionLine.DEFAULT_END_LINE_LENGTH * scale, y, x - 0.5 * DimensionLine.DEFAULT_END_LINE_LENGTH * scale, y);
    }

    /**
     * Draws text left to the dimension line.
     * If text is aligned, it rotates the text and displays it vertically, otherwise the text is horizontal.
     *
     * @param text            text to be displayed
     * @param graphicsContext graphics context to draw text one
     * @param x               x coordinate of the text
     * @param y               y coordinate of the text
     * @param scale           dimension line scale
     */
    @Override
    protected void drawText(String text, GraphicsContext graphicsContext, double x, double y, double scale) {
        graphicsContext.beginPath();
        Font font = new Font(DimensionLine.DEFAULT_TEXT_FONT, DimensionLine.DEFAULT_TEXT_SIZE * scale);
        graphicsContext.setFont(font);
        if (isTextAligned) {
            rotate(graphicsContext, -90, x, y);
            graphicsContext.setTextAlign(TextAlignment.CENTER);
        } else {
            graphicsContext.setTextBaseline(VPos.CENTER);
        }
        graphicsContext.fillText(text, x, y);
        if (isTextAligned) {
            // clean up
            rotate(graphicsContext, 0, x, y);
        }
        graphicsContext.closePath();
    }

    /**
     * Draws the dimension line.
     * It sets stroke and fill colour. It draws the main horizontal line, draws two horizontal end lines along with two ticks.
     * Finally, it draws a text in the centre to the left of the dimension line.
     */
    @Override
    public void draw() {
        if (!isSetupToBeDrawn()) {
            throw new IllegalArgumentException(UIText.INVALID_DIMENSION_LINE);
        }
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
        this.drawText(text, graphicsContext, x + offsetX - DimensionLine.DEFAULT_TEXT_OFFSET * scale, 0.5 * (startY + endY), scale);
    }

    /**
     * Method checks if dimension line has been setup correctly and can be drawn.
     *
     * @return true if dimension line can be drawn
     */
    @Override
    public boolean isSetupToBeDrawn() {
        return !(graphicsContext == null || color == null || scale == 0);
    }
}
