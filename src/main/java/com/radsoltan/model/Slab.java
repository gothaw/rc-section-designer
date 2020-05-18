package com.radsoltan.model;

import com.radsoltan.model.geometry.Geometry;
import com.radsoltan.model.reinforcement.Reinforcement;

public class Slab implements Flexure{
    private Geometry geometry;
    private Reinforcement reinforcement;

    @Override
    public double calculateBendingCapacity() {
        return 0;
    }

}
