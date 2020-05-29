package com.radsoltan.model.geometry;

public interface Flanged {
    boolean isElasticNeutralAxisInFlange();
    boolean isPlasticNeutralAxisInFlange(double UlsMoment, double effectiveDepth, double fcd);
}
