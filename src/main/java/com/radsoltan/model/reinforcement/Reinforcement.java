package com.radsoltan.model.reinforcement;

import com.radsoltan.model.Drawable;

public abstract class Reinforcement implements Drawable {
    protected int fy;
    public abstract double calculateCentroidOfTopReinforcement();
    public abstract double calculateCentroidOfBottomReinforcement();
}
