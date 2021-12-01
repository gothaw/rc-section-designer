package com.radsoltan.model.geometry;

import de.saxsys.javafx.test.JfxRunner;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

import static com.radsoltan.util.Utility.createCanvasSnapshot;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(JfxRunner.class)
class HorizontalDimensionLineTest {

    private static HorizontalDimensionLine horizontalDimensionLine;
    private static Canvas canvas;
    private static GraphicsContext graphicsContext;
    private static WritableImage writableImage;
    private static PixelReader pixelReader;

    @BeforeAll
    static void beforeAll() {
//        JFXPanel jfxPanel = new JFXPanel();

//        Platform.startup(() -> {
            double canvasWidth = 1000, canvasHeight = 1000;
            double startX = 100, endX = 500, y = 500;
            double offset = 0, scale = 1;

            canvas = new Canvas(canvasWidth, canvasHeight);
            graphicsContext = canvas.getGraphicsContext2D();
            horizontalDimensionLine = new HorizontalDimensionLine("X", Color.BLACK, graphicsContext, startX, endX, y, offset, scale);
            horizontalDimensionLine.draw();
            writableImage = createCanvasSnapshot(canvas, 1);
            pixelReader = writableImage.getPixelReader();
//        });

        Runnable runnable = () -> {

        };

//        Thread

    }

    @Test
    void drawsMainLine() {
//        Color color = pixelReader.getColor(100, 500);
//
//        System.out.println(color);
    }

    @Test
    void drawsTickSymbols() {

    }

    @Test
    void drawsEndLines() {

    }

    @Test
    void drawsText() {

    }
}