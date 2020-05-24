package com.radsoltan.model;

public class DesignParameters {
    private int nominalCoverTop;
    private int nominalCoverSides;
    private int nominalCoverBottom;

    public DesignParameters(int nominalCoverTop, int nominalCoverSides, int nominalCoverBottom) {
        this.nominalCoverTop = nominalCoverTop;
        this.nominalCoverSides = nominalCoverSides;
        this.nominalCoverBottom = nominalCoverBottom;
    }

    public int getNominalCoverTop() {
        return nominalCoverTop;
    }

    public int getNominalCoverSides() {
        return nominalCoverSides;
    }

    public int getNominalCoverBottom() {
        return nominalCoverBottom;
    }
}
