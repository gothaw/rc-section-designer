package com.radsoltan.model;

public class AnalysisResults {
    private double ULSMoment;
    private double SLSMoment;
    private double shearForce;

    public double getULSMoment() {
        return ULSMoment;
    }

    public void setULSMoment(double ULSMoment) {
        this.ULSMoment = ULSMoment;
    }

    public double getSLSMoment() {
        return SLSMoment;
    }

    public void setSLSMoment(double SLSMoment) {
        this.SLSMoment = SLSMoment;
    }

    public double getShearForce() {
        return shearForce;
    }

    public void setShearForce(double shearForce) {
        this.shearForce = shearForce;
    }
}
