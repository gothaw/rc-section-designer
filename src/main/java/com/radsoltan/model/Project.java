package com.radsoltan.model;

import com.radsoltan.model.geometry.Geometry;
import com.radsoltan.model.reinforcement.Reinforcement;

public class Project {
    private String name;
    private String id;
    private String description;
    private String author;
    private String UlsMoment;
    private String SlsMoment;
    private String UlsShear;
    private String elementType;
    private Geometry geometry;
    private Reinforcement reinforcement;
    private DesignParameters designParameters;
    private Concrete concrete;

    private static Project project;

    public static Project getInstance() {
        if (project == null) {
            project = new Project();
        }
        return project;
    }

    public void calculate() {
        // TODO: 16/06/2020 do calcs
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
}
