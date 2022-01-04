package com.radsoltan.model;

import com.radsoltan.model.geometry.Geometry;
import com.radsoltan.model.geometry.SlabStrip;
import com.radsoltan.model.reinforcement.Reinforcement;
import com.radsoltan.model.reinforcement.SlabReinforcement;
import com.radsoltan.util.Messages;

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

    public static Project getInstance() {
        if (project == null) {
            project = new Project();
        }
        return project;
    }

    public void calculate() {
        switch (elementType.toLowerCase()) {
            case "slab":
                calculateSlabProject();
                break;
            case "beam":
                calculateBeamProject();
                break;
            default:
                throw new IllegalArgumentException(Messages.INVALID_ELEMENT_TYPE);
        }
    }

    private void calculateSlabProject() {
        if (!(geometry.getSection() instanceof SlabStrip)) {
            throw new IllegalArgumentException(Messages.INVALID_SLAB_GEOMETRY);
        }
        if (!(reinforcement instanceof SlabReinforcement)) {
            throw new IllegalArgumentException(Messages.INVALID_SLAB_REINFORCEMENT);
        }
        SlabStrip slabStrip = (SlabStrip) geometry.getSection();
        SlabReinforcement slabReinforcement = (SlabReinforcement) reinforcement;
        double UlsMomentValue = Double.parseDouble(UlsMoment);
        double SlsMomentValue = Double.parseDouble(SlsMoment);
        Slab slab = new Slab(UlsMomentValue, SlsMomentValue, slabStrip, concrete, slabReinforcement, designParameters);
        try {
            slab.calculateBendingCapacity();
            flexureCapacity = slab.getBendingCapacity();
            flexureCapacityCheckMessage = (Math.abs(UlsMomentValue) <= flexureCapacity) ?
                    String.format("%.2f kNm/m \u003c %.2f kNm/m", Math.abs(UlsMomentValue), flexureCapacity) :
                    String.format("%.2f kNm/m \u003e %.2f kNm/m", Math.abs(UlsMomentValue), flexureCapacity);
            flexureResultsAdditionalMessage = (Math.abs(UlsMomentValue) <= flexureCapacity) ? Messages.SECTION_ADEQUATE : Messages.FLEXURE_FAIL_MESSAGE;
        } catch (IllegalArgumentException e) {
            flexureCapacity = 0;
            flexureCapacityCheckMessage = Messages.CALCULATIONS_ERROR;
            flexureResultsAdditionalMessage = e.getMessage();
        }
        if (designParameters.isIncludeCrackingCalculations()) {
            // TODO: 11/08/2020 Wrap in try catch and implement method
            slab.calculateCracks();

        }
    }

    private void calculateBeamProject() {
        // TODO: 11/08/2020 Implement method
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUlsMoment() {
        return UlsMoment;
    }

    public void setUlsMoment(String ulsMoment) {
        this.UlsMoment = ulsMoment;
    }

    public String getSlsMoment() {
        return SlsMoment;
    }

    public void setSlsMoment(String slsMoment) {
        this.SlsMoment = slsMoment;
    }

    public String getUlsShear() {
        return UlsShear;
    }

    public void setUlsShear(String ulsShear) {
        this.UlsShear = ulsShear;
    }

    public String getElementType() {
        return elementType;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public Reinforcement getReinforcement() {
        return reinforcement;
    }

    public void setReinforcement(Reinforcement reinforcement) {
        this.reinforcement = reinforcement;
    }

    public DesignParameters getDesignParameters() {
        return designParameters;
    }

    public void setDesignParameters(DesignParameters designParameters) {
        this.designParameters = designParameters;
    }

    public Concrete getConcrete() {
        return concrete;
    }

    public void setConcrete(Concrete concrete) {
        this.concrete = concrete;
    }

    public double getFlexureCapacity() {
        return flexureCapacity;
    }

    public double getShearCapacity() {
        return shearCapacity;
    }

    public double getCrackWidths() {
        return crackWidths;
    }

    public String getFlexureCapacityCheckMessage() {
        return flexureCapacityCheckMessage;
    }

    public String getShearCapacityCheckMessage() {
        return shearCapacityCheckMessage;
    }

    public String getCrackingCheckMessage() {
        return crackingCheckMessage;
    }

    public String getFlexureResultsAdditionalMessage() {
        return flexureResultsAdditionalMessage;
    }

    public String getShearResultsAdditionalMessage() {
        return shearResultsAdditionalMessage;
    }

    public String getCrackingResultsAdditionalMessage() {
        return crackingResultsAdditionalMessage;
    }
}
