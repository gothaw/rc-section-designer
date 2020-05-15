package com.radsoltan.model;

public enum Rebar {
    B6(6, calculateArea(6)),
    B8(8 , calculateArea(8)),
    B10(10 , calculateArea(10)),
    B12(12 , calculateArea(12)),
    B14(14 , calculateArea(14)),
    B16(16 , calculateArea(16)),
    B20(20 , calculateArea(20)),
    B25(25 , calculateArea(25)),
    B32(32 , calculateArea(32)),
    B40(40 , calculateArea(40));

    private int diameter;
    private double area;

    Rebar(int diameter, double area){
        this.diameter = diameter;
        this.area = area;
    }

    private static double calculateArea(int diameter){
        return diameter * diameter * Math.PI * 0.25;
    }

    public int getDiameter() {
        return diameter;
    }

    public double getArea() {
        return area;
    }
}
