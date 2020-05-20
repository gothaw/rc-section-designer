package com.radsoltan.model;

import com.radsoltan.model.geometry.Geometry;
import com.radsoltan.model.reinforcement.BeamReinforcement;
import com.radsoltan.model.reinforcement.Reinforcement;

public class Beam implements Flexure, Shear {
    private Double UlsMoment;
    private Double UlsShear;
    private Double SlsMoment;
    private Geometry geometry;
    private BeamReinforcement reinforcement;
    private Concrete concrete;
    private DesignParameters designParameters;

    public Beam(double UlsMoment, double UlsShear, double SlsMoment, Geometry geometry, Concrete concrete, DesignParameters designParameters) {
        this.UlsMoment = UlsMoment;
        this.UlsShear = UlsShear;
        this.SlsMoment = SlsMoment;
        this.concrete = concrete;
        this.designParameters = designParameters;
        this.geometry = geometry;
    }

    @Override
    public double calculateBendingCapacity() {
        if(concrete.getCompressiveStrength() <= 50) {

        } else {
            throw new IllegalArgumentException("Concrete class greater than C50/60. Outside of scope of this software.");
        }

        return 0;
    }

    @Override
    public double calculateShearCapacity() {
        return 0;
    }
}
