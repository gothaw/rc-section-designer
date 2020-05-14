package com.radsoltan.model;

public class Beam implements Flexure, Shear {
    private Double ULSMoment;
    private Geometry geometry;
    private Reinforcement reinforcement;
    private Concrete concrete;

    @Override
    public double calculateEffectiveDepth() {
        return 0;
    }

    @Override
    public double calculateBendingCapacity() {
        return 0;
    }

    @Override
    public double calculateCracks() {
        return 0;
    }

    @Override
    public double calculateShearCapacity() {
        return 0;
    }
}
