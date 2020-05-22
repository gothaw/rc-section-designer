package com.radsoltan.model.reinforcement;

import com.radsoltan.model.Drawable;

public abstract class Reinforcement implements Drawable {
    protected int fy;
    public abstract double calculateCentroidOfTopReinforcement(int nominalCoverTop, int transverseBarDiameter);
    public abstract double calculateCentroidOfBottomReinforcement(int nominalCoverBottom, int transverseBarDiameter);
}
