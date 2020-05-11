package com.radsoltan.model;

public class StructuralElement {
    private Beam beam;
    private Slab slab;

    public StructuralElement(Beam beam) {
        this.beam = beam;
    }

    public StructuralElement(Slab slab) {
        this.slab = slab;
    }
}
