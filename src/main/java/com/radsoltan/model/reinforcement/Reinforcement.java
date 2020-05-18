package com.radsoltan.model.reinforcement;

import com.radsoltan.model.Drawable;

public abstract class Reinforcement implements Drawable {
    protected int fy;
    public abstract double calculateCentroidOfTopReinforcement(int nominalCoverTop);
    public abstract double calculateCentroidOfBottomReinforcement(int nominalCoverBottom);
}
