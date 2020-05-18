package com.radsoltan.model;

import com.radsoltan.model.geometry.Geometry;
import com.radsoltan.model.reinforcement.Reinforcement;

public class Beam implements Flexure, Shear {
    private Double UlsMoment;
    private Double UlsShear;
    private Double SlsMoment;
    private Geometry geometry;
    private Reinforcement reinforcement;
    private Concrete concrete;
    private DesignParameters designParameters;
    private double kFactor;
    private double kDashFactor;

    public Beam(double UlsMoment, double UlsShear, double SlsMoment, Geometry geometry, Concrete concrete, DesignParameters designParameters) {
        this.UlsMoment = UlsMoment;
        this.UlsShear = UlsShear;
        this.SlsMoment = SlsMoment;
        this.concrete = concrete;
        this.designParameters = designParameters;
    }

    @Override
    public double calculateBendingCapacity() {
        return 0;
    }

    @Override
    public double calculateShearCapacity() {
        return 0;
    }

    public void doSomethingWithShape() {

    }
}
