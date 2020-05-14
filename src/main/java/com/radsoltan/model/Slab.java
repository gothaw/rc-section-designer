package com.radsoltan.model;

public class Slab implements Flexure{
    private Geometry geometry;
    private Reinforcement reinforcement;

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
}
