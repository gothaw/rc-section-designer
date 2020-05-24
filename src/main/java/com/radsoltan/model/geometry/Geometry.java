package com.radsoltan.model.geometry;

public class Geometry {
    private Rectangle rectangle;
    private LShape lShape;
    private TShape tShape;
    private SlabStrip slabStrip;
    private final Shape shape;

    public Geometry(Rectangle rectangle) {
        this.rectangle = rectangle;
        this.shape = (Shape) rectangle;
    }

    public Geometry(LShape lShape) {
        this.lShape = lShape;
        this.shape = (Shape) lShape;
    }

    public Geometry(TShape tShape) {
        this.tShape = tShape;
        this.shape = (Shape) tShape;
    }

    public Geometry(SlabStrip slabStrip) {
        this.slabStrip = slabStrip;
        this.shape = (Shape) slabStrip;
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

    public Shape getShape() {
        return shape;
    }

    public double checksIfFlangeTakesCompressionForce(){
        return 1.0;
    }

    public double getWidthInCompressionZone(double UlsMoment, double effectiveDepth, double fcd){
        return shape.getWidthInCompressionZone(UlsMoment, effectiveDepth, fcd);
    }

    public int getDepth(){
        return shape.getDepth();
    }

}
