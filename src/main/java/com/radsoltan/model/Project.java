package com.radsoltan.model;

import com.radsoltan.model.geometry.Geometry;
import com.radsoltan.model.geometry.SlabStrip;
import com.radsoltan.model.reinforcement.Reinforcement;
import com.radsoltan.model.reinforcement.SlabReinforcement;
import com.radsoltan.util.UIText;

/**
 * Class is used to represent project calculations. It includes values from fields shown in primary controller along with:
 * - Geometry
 * - Reinforcement
 * - Design Parameters
 * - Concrete type
 * It uses all that information to calculate flexural and shear capacity and crack widths (if applicable). The class uses singleton pattern.
 */
public class Project {
    private String name;
    private String id;
    private String description;
    private String author;
    private String UlsMoment;
    private String SlsMoment;
    private String UlsShear;
    private String elementType = "slab";
    private Geometry geometry;
    private Reinforcement reinforcement;
    private DesignParameters designParameters;
    private Concrete concrete;
    /* Results */
    private double flexureCapacity;
    private double shearCapacity;
    private double crackWidths;
    private String flexureCapacityCheckMessage;
    private String shearCapacityCheckMessage;
    private String crackingCheckMessage;
    private String flexureResultsAdditionalMessage;
    private String shearResultsAdditionalMessage;
    private String crackingResultsAdditionalMessage;

    private static Project project;

    /**
     * Gets project instance. Singleton pattern.
     *
     * @return project instance
     */
    public static Project getInstance() {
        if (project == null) {
            project = new Project();
        }
        return project;
    }

    /**
     * Handler for calculate button. It invokes method that depends on structural element type.
     */
    public void calculate() {
        switch (elementType.toLowerCase()) {
            case "slab":
                calculateSlabProject();
                break;
            case "beam":
                calculateBeamProject();
                break;
            default:
                throw new IllegalArgumentException(UIText.INVALID_ELEMENT_TYPE);
        }
    }

    /**
     * Method used to calculate slab project.
     * It calculates bending capacity and crack widths based on forces and parameters provided.
     */
    private void calculateSlabProject() {
        if (!(geometry.getSection() instanceof SlabStrip)) {
            throw new IllegalArgumentException(UIText.INVALID_SLAB_GEOMETRY);
        }
        if (!(reinforcement instanceof SlabReinforcement)) {
            throw new IllegalArgumentException(UIText.INVALID_SLAB_REINFORCEMENT);
        }
        // Getting slab section and slab reinforcement
        SlabStrip slabStrip = (SlabStrip) geometry.getSection();
        SlabReinforcement slabReinforcement = (SlabReinforcement) reinforcement;
        // Getting moment values
        double UlsMomentValue = Double.parseDouble(UlsMoment);
        double SlsMomentValue = Double.parseDouble(SlsMoment);
        // Instantiating new slab
        Slab slab = new Slab(UlsMomentValue, SlsMomentValue, slabStrip, concrete, slabReinforcement, designParameters);
        try {
            // Calculating bending capacity
            slab.calculateBendingCapacity();
            flexureCapacity = slab.getBendingCapacity();
            flexureCapacityCheckMessage = (Math.abs(UlsMomentValue) <= flexureCapacity) ?
                    String.format("%.2f kNm/m \u003c %.2f kNm/m", Math.abs(UlsMomentValue), flexureCapacity) :
                    String.format("%.2f kNm/m \u003e %.2f kNm/m", Math.abs(UlsMomentValue), flexureCapacity);
            flexureResultsAdditionalMessage = (Math.abs(UlsMomentValue) <= flexureCapacity) ? UIText.SECTION_ADEQUATE : UIText.FLEXURE_FAIL_MESSAGE;
        } catch (IllegalArgumentException e) {
            flexureCapacity = 0;
            flexureCapacityCheckMessage = UIText.CALCULATIONS_ERROR;
            flexureResultsAdditionalMessage = e.getMessage();
        }
        if (designParameters.isIncludeCrackingCalculations()) {
            // TODO: 11/08/2020 Wrap in try catch and implement method
            // Calculating crack widths
        }
    }

    /**
     * Method used to calculate beam project.
     * It calculates bending and shear capacity and crack widths based on forces and parameters provided.
     */
    private void calculateBeamProject() {
        // TODO: 11/08/2020 Implement method
    }

    /**
     * Getter for project name.
     *
     * @return project name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for project name.
     *
     * @param name project name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for project Id.
     *
     * @return project Id
     */
    public String getId() {
        return id;
    }

    /**
     * Setter for project Id.
     *
     * @param id project Id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Getter for project description.
     *
     * @return project description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter for project description.
     *
     * @param description project description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Getter for project author.
     *
     * @return project author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Setter for project author.
     *
     * @param author project author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Getter for ULS moment.
     *
     * @return ULS moment
     */
    public String getUlsMoment() {
        return UlsMoment;
    }

    /**
     * Setter for ULS moment.
     *
     * @param ulsMoment moment
     */
    public void setUlsMoment(String ulsMoment) {
        this.UlsMoment = ulsMoment;
    }

    /**
     * Getter for SLS moment.
     *
     * @return SLS moment
     */
    public String getSlsMoment() {
        return SlsMoment;
    }

    /**
     * Setter for SLS moment.
     *
     * @param slsMoment moment
     */
    public void setSlsMoment(String slsMoment) {
        this.SlsMoment = slsMoment;
    }

    /**
     * Getter for ULS shear force.
     *
     * @return ULS shear
     */
    public String getUlsShear() {
        return UlsShear;
    }

    /**
     * Setter for ULS shear force.
     *
     * @param ulsShear shear force
     */
    public void setUlsShear(String ulsShear) {
        this.UlsShear = ulsShear;
    }

    /**
     * Getter for element type.
     *
     * @return element type
     */
    public String getElementType() {
        return elementType;
    }

    /**
     * Setter for element type.
     *
     * @param elementType element type
     */
    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    /**
     * Getter for structural element geometry.
     *
     * @return geometry
     */
    public Geometry getGeometry() {
        return geometry;
    }

    /**
     * Setter for structural element geometry.
     *
     * @param geometry geometry object
     */
    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    /**
     * Getter for structural element reinforcement.
     *
     * @return reinforcement
     */
    public Reinforcement getReinforcement() {
        return reinforcement;
    }

    /**
     * Setter for structural element reinforcement.
     *
     * @param reinforcement reinforcement object
     */
    public void setReinforcement(Reinforcement reinforcement) {
        this.reinforcement = reinforcement;
    }

    /**
     * Getter for design parameters used in calculations.
     *
     * @return design parameters
     */
    public DesignParameters getDesignParameters() {
        return designParameters;
    }

    /**
     * Setter for design parameters used in calculations.
     *
     * @param designParameters design parameters object
     */
    public void setDesignParameters(DesignParameters designParameters) {
        this.designParameters = designParameters;
    }

    /**
     * Getter for concrete type.
     *
     * @return concrete
     */
    public Concrete getConcrete() {
        return concrete;
    }

    /**
     * Setter for concrete type.
     *
     * @param concrete concrete enum
     */
    public void setConcrete(Concrete concrete) {
        this.concrete = concrete;
    }

    /**
     * Getter for bending capacity.
     *
     * @return bending capacity
     */
    public double getFlexureCapacity() {
        return flexureCapacity;
    }

    /**
     * Getter for shear capacity.
     *
     * @return shear capacity
     */
    public double getShearCapacity() {
        return shearCapacity;
    }

    /**
     * Getter for calculated crack widths.
     *
     * @return crack widths
     */
    public double getCrackWidths() {
        return crackWidths;
    }

    /**
     * Getter for bending capacity calculation message.
     *
     * @return calculation message
     */
    public String getFlexureCapacityCheckMessage() {
        return flexureCapacityCheckMessage;
    }

    /**
     * Getter for shear capacity calculation message.
     *
     * @return calculation message
     */
    public String getShearCapacityCheckMessage() {
        return shearCapacityCheckMessage;
    }

    /**
     * Getter for cracking calculation message.
     *
     * @return calculation message
     */
    public String getCrackingCheckMessage() {
        return crackingCheckMessage;
    }

    /**
     * Getter for additional calculation message for bending calculations.
     *
     * @return additional calculation message
     */
    public String getFlexureResultsAdditionalMessage() {
        return flexureResultsAdditionalMessage;
    }

    /**
     * Getter for additional calculation message for shear calculations.
     *
     * @return additional calculation message
     */
    public String getShearResultsAdditionalMessage() {
        return shearResultsAdditionalMessage;
    }

    /**
     * Getter for additional calculation message for crack width calculations.
     *
     * @return additional calculation message
     */
    public String getCrackingResultsAdditionalMessage() {
        return crackingResultsAdditionalMessage;
    }
}
