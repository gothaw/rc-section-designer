package com.radsoltan.model.reinforcement;

import com.radsoltan.model.Drawable;

public abstract class Reinforcement implements Drawable {

    public abstract double getTotalAreaOfTopReinforcement();

    public abstract double getCentroidOfTopReinforcement(int nominalCoverTop);

    public abstract double getTotalAreaOfBottomReinforcement();

    public abstract double getCentroidOfBottomReinforcement(int nominalCoverBottom);

    public abstract String getDescription();
}
