package com.radsoltan.util;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;
import javafx.scene.transform.Transform;

public class Utility {

    /**
     * Utility method that takes a string and capitalizes the first letter.
     * @param string String to be capitalized
     * @return capitalized string
     */
    public static String capitalize(String string) {
        if (string != null){
            return string.substring(0,1).toUpperCase() + string.substring(1).toLowerCase();
        }
        return null;
    }

    /**
     * Creates a snapshot of the canvas using snapshot method. It also can scale the snapshot up or down using the scale parameter.
     *
     * @param canvas canvas to take the snapshot of
     * @param pixelScale scales the snapshot up or down
     * @return writeable image of the canvas
     */
    public static WritableImage createCanvasSnapshot(Canvas canvas, double pixelScale) {
        WritableImage writableImage = new WritableImage((int)Math.rint(pixelScale*canvas.getWidth()), (int)Math.rint(pixelScale*canvas.getHeight()));
        SnapshotParameters spa = new SnapshotParameters();
        spa.setTransform(Transform.scale(pixelScale, pixelScale));
        return canvas.snapshot(spa, writableImage);
    }
}
