package com.radsoltan.model.geometry;

/**
 * Class used for describing project geometry.
 * It holds an object that describes the section and it inherits from Section class.
 */
public class Geometry {
    private Rectangle rectangle;
    private LSection lSection;
    private TSection tSection;
    private SlabStrip slabStrip;
    private final Section section;
    private Flanged flangedSection;

    /**
     * Constructor. Used for rectangular sections.
     *
     * @param rectangle rectangular section
     */
    public Geometry(Rectangle rectangle) {
        this.rectangle = rectangle;
        this.section = (Section) rectangle;
    }

    /**
     * Constructor. Used for L section sections.
     *
     * @param lSection L section section
     */
    public Geometry(LSection lSection) {
        this.lSection = lSection;
        this.section = (Section) lSection;
        this.flangedSection = (Flanged) lSection;
    }

    /**
     * Constructor. Used for T section sections.
     *
     * @param tSection T section section
     */
    public Geometry(TSection tSection) {
        this.tSection = tSection;
        this.section = (Section) tSection;
        this.flangedSection = (Flanged) tSection;
    }

    /**
     * Constructor. Used for slab section.
     *
     * @param slabStrip Section of a slab strip
     */
    public Geometry(SlabStrip slabStrip) {
        this.slabStrip = slabStrip;
        this.section = (Section) slabStrip;
    }

    /**
     * Getter for section.
     *
     * @return section instance
     */
    public Section getSection() {
        return section;
    }

    /**
     * Gets section depth.
     *
     * @return section depth
     */
    public int getDepth() {
        return section.getDepth();
    }

    /**
     * Gets section width.
     *
     * @return section width
     */
    public int getWidth() {
        return section.getWebWidth();
    }

    /**
     * Gets section area.
     *
     * @return section area
     */
    public double getArea() {
        return section.getArea();
    }

    /**
     * Gets section width in compression zone.
     *
     * @param UlsMoment ULS moment in kNm or kNm/m
     * @return width in compression zone
     */
    public int getWidthInCompressionZone(double UlsMoment) {
        return section.getWidthInCompressionZone(UlsMoment);
    }

    /**
     * Gets section width in tension zone.
     *
     * @param UlsMoment ULS moment in kNm or kNm/m
     * @return width in tension zone
     */
    public int getWidthInTensionZone(double UlsMoment) {
        return section.getWidthInTensionZone(UlsMoment);
    }

    /**
     * Gets area in tension zone just before first crack occurs.
     *
     * @param UlsMoment ULS moment in kNm or kNm/m
     * @return area prior to cracking
     */
    public double getAreaInTensionZonePriorCracking(double UlsMoment) {
        return section.getAreaInTensionZonePriorCracking(UlsMoment);
    }

    /**
     * Gets a k factor for a section described in cl. 7.3.2 in EC2.
     *
     * @param UlsMoment ULS moment in kNm or kNm/m
     * @return k factor
     */
    public double getFactorForNonUniformSelfEquilibratingStresses(double UlsMoment) {
        return section.getFactorForNonUniformSelfEquilibratingStresses(UlsMoment);
    }

    /**
     * Gets kc factor for a section described in cl. 7.3.2 in EC2.
     *
     * @param UlsMoment ULS moment in kNm or kNm/m
     * @return kc factor
     */
    public double getFactorForStressDistributionPriorCracking(double UlsMoment) {
        return section.getFactorForStressDistributionPriorCracking(UlsMoment);
    }

    /**
     * Checks if a section has a flange.
     *
     * @return true if a flanged section
     */
    public boolean isFlangedSection() {
        return section instanceof Flanged && flangedSection != null;
    }

    /**
     * Checks if plastic neutral axis for section is in the flange.
     *
     * @param UlsMoment      ULS moment in kNm or kNm/m
     * @param effectiveDepth effective depth in mm
     * @param leverArm       lever arm in mm
     * @return true if plastic neutral axis is in the flange
     */
    public boolean checkIfPlasticNeutralAxisInFlange(double UlsMoment, double effectiveDepth, double leverArm) {
        if (isFlangedSection()) {
            return flangedSection.isPlasticNeutralAxisInFlange(UlsMoment, effectiveDepth, leverArm);
        }
        return false;
    }

    /**
     * Gets flange thickness. If not flanged section, it returns 0.
     *
     * @return flange thickness
     */
    public int getFlangeThickness() {
        if (isFlangedSection()) {
            return flangedSection.getFlangeThickness();
        }
        return 0;
    }

    /**
     * Gets flange width. If not flanged section, it returns 0.
     *
     * @return flange width
     */
    public int getFlangeWidth() {
        if (isFlangedSection()) {
            return flangedSection.getFlangeWidth();
        }
        return 0;
    }

    /**
     * Gets section description.
     *
     * @return section description
     */
    public String getDescription() {
        return section.getDescription();
    }
}
