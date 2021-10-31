package com.radsoltan.model.geometry;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Rotate;

public abstract class DimensionLine implements Drawable {

    public abstract void drawMainLine();

    public abstract void drawEndLine();

    public abstract void drawTick();

    public abstract void drawText();

    protected void rotate(GraphicsContext gc, double angle, double x, double y) {
        Rotate r = new Rotate(angle, x, y);
        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
    }
}
