package com.radsoltan.model;

import com.radsoltan.model.geometry.Geometry;
import com.radsoltan.model.reinforcement.Reinforcement;

public class Project {
    private String name;
    private int Number;
    private String description;
    private String author;
    private double UlsMoment;
    private double SlsMoment;
    private double UlsShear;
    private String elementType;
    private Geometry geometry;
    private Reinforcement reinforcement;
    private DesignParameters designParameters;

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

    public int getNumber() {
        return Number;
    }

    public void setNumber(int number) {
        Number = number;
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

    public double getUlsMoment() {
        return UlsMoment;
    }

    public void setUlsMoment(double ulsMoment) {
        UlsMoment = ulsMoment;
    }

    public double getSlsMoment() {
        return SlsMoment;
    }

    public void setSlsMoment(double slsMoment) {
        SlsMoment = slsMoment;
    }

    public double getUlsShear() {
        return UlsShear;
    }

    public void setUlsShear(double ulsShear) {
        UlsShear = ulsShear;
    }

    public String getElementType() {
        return elementType;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }
}
