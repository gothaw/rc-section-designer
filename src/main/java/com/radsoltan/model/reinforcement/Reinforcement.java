package com.radsoltan.model.reinforcement;

import com.radsoltan.model.Drawable;

public abstract class Reinforcement implements Drawable {

    protected int fy;

    public abstract double getTotalAreaOfTopReinforcement();

    public abstract double getCentroidOfTopReinforcement(int nominalCoverTop);

    public abstract double getTotalAreaOfBottomReinforcement();

    public abstract double getCentroidOfBottomReinforcement(int nominalCoverBottom);

    public double getDesignYieldStrength(double gammaS) {
        return fy / gammaS;
    }

    public int getYieldStrength() {
        return fy;
    }

}
