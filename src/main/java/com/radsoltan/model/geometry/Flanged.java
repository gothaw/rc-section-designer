package com.radsoltan.model.geometry;

public interface Flanged {

    int getFlangeWidth();

    int getFlangeThickness();

    boolean isElasticNeutralAxisInFlange();

    boolean isPlasticNeutralAxisInFlange(double UlsMoment, double effectiveDepth, double leverArm);

    default double getFactorForNonUniformSelfEquilibratingStressesForWebOrFlange(int size) {
        if (size <= 300) {
            return 1.0;
        } else if (size < 800) {
            return (800 - size) * 0.35 / 500 + 0.65;
        } else {
            return 0.65;
        }
    }
}
