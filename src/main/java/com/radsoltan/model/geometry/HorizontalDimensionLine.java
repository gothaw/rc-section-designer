package com.radsoltan.model.geometry;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * Class used for creating horizontal dimension line
 */
public class HorizontalDimensionLine extends DimensionLine {

    private final String text;
    private final Color color;
    private final GraphicsContext graphicsContext;
    private final double startX;
    private final double endX;
    private final double y;
    private final double offsetY;
    private final double scale;

    /**
     * Constructor. Sets required dimension line properties.
     *
     * @param text            text to be displayed above the dimension line
     * @param color           dimension line colour
     * @param graphicsContext graphics context to draw the dimension line on
     * @param startX          dimension line start point - x coordinate
     * @param endX            dimension line end point - y coordinate
     * @param y               dimension line y coordinate
     * @param offsetY         offset in y direction from y coordinate
     * @param scale           dimension line scale
     */
    public HorizontalDimensionLine(String text, Color color, GraphicsContext graphicsContext, double startX, double endX, double y, double offsetY, double scale) {
        this.text = text;
        this.color = color;
        this.graphicsContext = graphicsContext;
        this.startX = startX;
        this.endX = endX;
        this.y = y;
        this.offsetY = offsetY;
        this.scale = scale;
    }

    /**
     * Constructor. Uses default offset in Y and default scale
     *
     * @param text            text to be displayed above the dimension line
     * @param color           dimension line colour
     * @param graphicsContext graphics context to draw the dimension line on
     * @param startX          dimension line start point - x coordinate
     * @param endX            dimension line end point - y coordinate
     * @param y               dimension line y coordinate
     */
    public HorizontalDimensionLine(String text, Color color, GraphicsContext graphicsContext, double startX, double endX, double y) {
        this(text, color, graphicsContext, startX, endX, y, DimensionLine.DEFAULT_OFFSET, DimensionLine.DEFAULT_SCALE);
    }

    /**
     * Draws a vertical limiting end line.
     *
     * @param graphicsContext graphics context to draw on
     * @param x               x coordinate of the centre of the line
     * @param y               y coordinate of the centre of the line
     * @param scale           dimension line scale
     */
    @Override
    protected void drawEndLine(GraphicsContext graphicsContext, double x, double y, double scale) {
        this.drawLine(graphicsContext, x, y + 0.5 * DimensionLine.DEFAULT_END_LINE_LENGTH * scale, x, y - 0.5 * DimensionLine.DEFAULT_END_LINE_LENGTH * scale);
    }

    /**
     * Draws text above the dimension line.
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
        graphicsContext.setTextAlign(TextAlignment.CENTER);
        graphicsContext.fillText(text, x, y);
        graphicsContext.closePath();
    }

    /**
     * Draws dimension line.
     * It sets stroke and fill colour. It draws the main horizontal line, draws two vertical end lines along with two ticks.
     * Finally, it draws a text in the centre above above the dimension line.
     */
    @Override
    public void draw() {
        graphicsContext.setStroke(color);
        graphicsContext.setFill(color);

        this.drawLine(
                graphicsContext,
                startX - DimensionLine.DEFAULT_MAIN_LINE_EXTENSION * scale,
                y + offsetY,
                endX + DimensionLine.DEFAULT_MAIN_LINE_EXTENSION * scale,
                y + offsetY
        );
        this.drawEndLine(graphicsContext, startX, y + offsetY, scale);
        this.drawEndLine(graphicsContext, endX, y + offsetY, scale);
        this.drawTick(graphicsContext, startX, y + offsetY, scale);
        this.drawTick(graphicsContext, endX, y + offsetY, scale);
        this.drawText(text, graphicsContext, 0.5 * (startX + endX), y + offsetY - DimensionLine.DEFAULT_TEXT_OFFSET, scale);
    }
}
