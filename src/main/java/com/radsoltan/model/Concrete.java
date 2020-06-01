package com.radsoltan.model;

public enum Concrete {
    C12_15(12, 15, 20, 1.6, 1.1, 2.0, 27, 0.0018, 0.0035, 0.002, 0.0035, 2.0, 0.00175, 0.0035),
    C16_20(16, 20, 24, 1.9, 1.3, 2.5, 29, 0.0019, 0.0035, 0.002, 0.0035, 2.0, 0.00175, 0.0035),
    C20_25(20, 25, 28, 2.2, 1.5, 2.9, 30, 0.0020, 0.0035, 0.002, 0.0035, 2.0, 0.00175, 0.0035),
    C25_30(25, 30, 33, 2.6, 1.8, 3.3, 31, 0.0021, 0.0035, 0.002, 0.0035, 2.0, 0.00175, 0.0035),
    C30_37(30, 37, 38, 2.9, 2.0, 3.8, 33, 0.0022, 0.0035, 0.002, 0.0035, 2.0, 0.00175, 0.0035),
    C32_40(32, 40, 40, 3.0, 2.1, 3.9, 33.4, 0.0022, 0.0035, 0.002, 0.0035, 2.0, 0.00175, 0.0035),
    C35_45(35, 45, 43, 3.2, 2.2, 4.2, 34, 0.00225, 0.0035, 0.002, 0.0035, 2.0, 0.00175, 0.0035),
    C40_50(40, 50, 48, 3.5, 2.5, 4.6, 35, 0.0023, 0.0035, 0.002, 0.0035, 2.0, 0.00175, 0.0035),
    C45_55(45, 55, 53, 3.8, 2.7, 4.9, 36, 0.0024, 0.0035, 0.002, 0.0035, 2.0, 0.00175, 0.0035),
    C50_60(50, 60, 58, 4.1, 2.9, 5.3, 37, 0.00245, 0.0035, 0.002, 0.0035, 2.0, 0.00175, 0.0035);

    private final int fck;
    private final int fckCube;
    private final int fcm;
    private final double fctm;
    private final double fctk005;
    private final double fctk095;
    private final double Ecm;
    private final double ec1;
    private final double ecu1;
    private final double ec2;
    private final double ecu2;
    private final double n;
    private final double ec3;
    private final double ecu3;

    Concrete(int fck, int fckCube,
             int fcm, double fctm,
             double fctk005, double fctk095,
             double Ecm, double ec1,
             double ecu1, double ec2,
             double ecu2, double n,
             double ec3, double ecu3) {
        this.fck = fck;
        this.fckCube = fckCube;
        this.fcm = fcm;
        this.fctm = fctm;
        this.fctk005 = fctk005;
        this.fctk095 = fctk095;
        this.Ecm = Ecm;
        this.ec1 = ec1;
        this.ecu1 = ecu1;
        this.ec2 = ec2;
        this.ecu2 = ecu2;
        this.n = n;
        this.ec3 = ec3;
        this.ecu3 = ecu3;
    }

    @Override
    public String toString() {
        return "C" + fck + "/" + fckCube;
    }

    public int getCompressiveStrength() {
        return fck;
    }

    public int getCubicCompressiveStrength() {
        return fckCube;
    }

    public int getMeanCompressiveStrength() {
        return fcm;
    }

    public double getMeanAxialTensileStrength() {
        return fctm;
    }

    public double getCompressiveStrengthFivePercentile() {
        return fctk005;
    }

    public double getCompressiveStrengthNinetyFivePercentile() {
        return fctk095;
    }

    public double getSecantYoungsModulus() {
        return Ecm;
    }

    public double getCompressiveStrainInConcreteAtPeakStress() {
        return ec1;
    }

    public double getUltimateCompressiveStrainInConcrete() {
        return ecu1;
    }

    public double getCompressiveStrainAtMaximumStrengthForParabolaRectangleModel() {
        return ec2;
    }

    public double getUltimateCompressiveStrainForParabolaRectangleModel() {
        return ecu2;
    }

    public double getNFactor() {
        return n;
    }

    public double getCompressiveStrainAtMaximumStrengthForBilinearModel() {
        return ec3;
    }

    public double getUltimateCompressiveStrainForBilinearModel() {
        return ecu3;
    }

    public double getDesignCompressiveResistance(double gammaC){
        return 0.85 * fck / gammaC;
    }
}
