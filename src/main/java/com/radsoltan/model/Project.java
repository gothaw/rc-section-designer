package com.radsoltan.model;

public class Project {
    private String name;
    private int Number;
    private String description;
    private String author;
    private Beam beam;
    private Slab slab;
    private static Project project;

    private Project() {
    }

    public static Project getInstance() {
        if (project == null) {
            project = new Project();
        }
        return project;
    }

    public Beam getBeam() {
        return beam;
    }

    public Slab getSlab() {
        return slab;
    }

    public void calculate() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
