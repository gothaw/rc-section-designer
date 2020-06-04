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

    public int getDepth() {
        return shape.getDepth();
    }

    public int getWidthInCompressionZone(double UlsMoment) {
        return shape.getWidthInCompressionZone(UlsMoment);
    }

    public int getWidthInTensionZone(double UlsMoment) {
        return shape.getWidthInTensionZone(UlsMoment);
    }

    public double getAreaInTensionZonePriorCracking(double UlsMoment) {
        return shape.getAreaInTensionZonePriorCracking(UlsMoment);
    }

    public double getFactorForNonUniformSelfEquilibratingStresses(double UlsMoment) {
        return shape.getFactorForNonUniformSelfEquilibratingStresses(UlsMoment);
    }

    public double getFactorForStressDistributionPriorCracking(double UlsMoment) {
        return shape.getFactorForStressDistributionPriorCracking(UlsMoment);
    }

    public boolean checkIfFlangedSection() {
        return shape instanceof Flanged;
    }

    public boolean checkIfPlasticNeutralAxisInFlange(double leverArm, double effectiveDepth) {
        if (checkIfFlangedSection()) {
            Flanged flangedShape = (Flanged) shape;
            return flangedShape.isPlasticNeutralAxisInFlange(leverArm, effectiveDepth);
        }
        return false;
    }
}
