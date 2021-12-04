package com.radsoltan.model.geometry;

import javafx.scene.canvas.GraphicsContext;

public class SlabStrip extends Rectangle {

    public SlabStrip(int thickness, GraphicsContext graphicsContext) {
        super(1000, thickness, graphicsContext);
    }

    public SlabStrip(int thickness) {
        super(1000, thickness);
    }

    @Override
    public String getDescription() {
        return String.format("Slab: %d mm thick", getDepth());
    }

    @Override
    public void draw() {
        // TODO: 18/05/2020
    }
}
