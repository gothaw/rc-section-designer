package com.radsoltan.model.geometry;

public class Geometry {
    private Rectangle rectangle;
    private LShape lShape;
    private TShape tShape;
    private SlabStrip slabStrip;

    public Geometry(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    public Geometry(LShape lShape) {
        this.lShape = lShape;
    }

    public Geometry(TShape tShape) {
        this.tShape = tShape;
    }

    public Geometry(SlabStrip slabStrip) {
        this.slabStrip = slabStrip;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public LShape getLShape() {
        return lShape;
    }

    public TShape getTShape() {
        return tShape;
    }

    public SlabStrip getSlabStrip() {
        return slabStrip;
    }

}
