package com.radsoltan.model.geometry;

/**
 * Interface for classes that describe geometry with a flange.
 * This could be asymmetric or symmetric flange (L section or T section) and top or bottom.
 */
public interface Flanged {

    /**
     * Getter for the flange width
     *
     * @return flange width
     */
    int getFlangeWidth();

    /**
     * Getter for the flange thickness
     *
     * @return flange thickness
     */
    int getFlangeThickness();

    /**
     * Method that checks if the elastic neutral axis for the section is in the flange.
     *
     * @return true if elastic neutral axis is in flange
     */
    boolean isElasticNeutralAxisInFlange();

    /**
     * Method that checks if the plastic neutral axis for the section is in the flange.
     *
     * @param UlsMoment      ULS moment in kNm
     * @param effectiveDepth effective depth in mm
     * @param leverArm       lever arm in mm
     * @return true if plastic neutral axis is in flange
     */
    boolean isPlasticNeutralAxisInFlange(double UlsMoment, double effectiveDepth, double leverArm);

    /**
     * It calculates factor for non-uniform self equilibrating stress with accordance to cl. 7.3.2 in EC2.
     * It uses either flange width or down stand depth.
     *
     * @param size flange width or down stand depth in mm
     * @return factor between 0.65 and 1.0
     */
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
