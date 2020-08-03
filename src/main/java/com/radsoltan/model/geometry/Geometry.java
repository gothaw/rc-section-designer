package com.radsoltan.model.geometry;

public class Geometry {
    private Rectangle rectangle;
    private LShape lShape;
    private TShape tShape;
    private SlabStrip slabStrip;
    private final Shape shape;
    private Flanged flangedShape;

    public Geometry(Rectangle rectangle) {
        this.rectangle = rectangle;
        this.shape = (Shape) rectangle;
    }

    public Geometry(LShape lShape) {
        this.lShape = lShape;
        this.shape = (Shape) lShape;
        this.flangedShape = (Flanged) lShape;
    }

    public Geometry(TShape tShape) {
        this.tShape = tShape;
        this.shape = (Shape) tShape;
        this.flangedShape = (Flanged) tShape;
    }

    public Geometry(SlabStrip slabStrip) {
        this.slabStrip = slabStrip;
        this.shape = (Shape) slabStrip;
    }

    public Shape getShape() {
        return shape;
    }

    public int getDepth() {
        return shape.getDepth();
    }

    public int getWidth() {
        return shape.getWidth();
    }

    public double getArea() {
        return shape.getArea();
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

    public boolean isFlangedSection() {
        return shape instanceof Flanged && flangedShape != null;
    }

    public boolean checkIfPlasticNeutralAxisInFlange(double UlsMoment, double effectiveDepth, double leverArm) {
        if (isFlangedSection()) {
            return flangedShape.isPlasticNeutralAxisInFlange(UlsMoment, effectiveDepth, leverArm);
        }
        return false;
    }

    public int getFlangeThickness() {
        if(isFlangedSection()) {
            return flangedShape.getFlangeThickness();
        }
        return 0;
    }

    public int getFlangeWidth() {
        if(isFlangedSection()) {
            return flangedShape.getFlangeThickness();
        }
        return 0;
    }

    public String getDescription() {
        return shape.getDescription();
    }
}
