package com.radsoltan.model.reinforcement;

import com.radsoltan.model.Concrete;
import com.radsoltan.model.Drawable;
import com.radsoltan.model.geometry.Geometry;

public abstract class Reinforcement implements Drawable {

    protected int fy;

    public abstract double calculateTotalAreaOfTopReinforcement();

    public abstract double calculateCentroidOfTopReinforcement(int nominalCoverTop, int transverseBarDiameter);

    public abstract double calculateTotalAreaOfBottomReinforcement();

    public abstract double calculateCentroidOfBottomReinforcement(int nominalCoverBottom, int transverseBarDiameter);

    public double calculateDesignYieldStrength(double gammaS) {
        return fy / gammaS;
    }

    public int getYieldStrength() {
        return fy;
    }

}
