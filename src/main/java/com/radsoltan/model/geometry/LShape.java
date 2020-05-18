package com.radsoltan.model.geometry;

public class LShape extends Shape{

    private int width;
    private int depth;
    private int widthFlange;
    private int thicknessFlange;

    @Override
    public double calculateArea() {
        return 0;
    }

    @Override
    public double calculateFirstMomentOfArea() {
        return 0;
    }

    @Override
    public double calculateSecondMomentOfArea() {
        return 0;
    }

    @Override
    public double getWidthInCompressionZone() {
        return 0;
    }

    @Override
    public void draw() {

    }
}
