package com.radsoltan.model;

public class Project {
    private String name;
    private int Number;
    private String description;
    private String author;
    private Beam beam;
    private Slab slab;

    public Project(Beam beam) {
        this.beam = beam;
    }

    public Project(Slab slab) {
        this.slab = slab;
    }

    public Beam getBeam() {
        return beam;
    }

    public Slab getSlab() {
        return slab;
    }

    public void calculate(){

    }
}
