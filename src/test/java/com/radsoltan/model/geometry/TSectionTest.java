package com.radsoltan.model.geometry;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.text.DecimalFormat;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TSectionTest {

    private static DecimalFormat decimalFormat;
    private static TSection tSection;
    private static TSection tSectionWithLargeFlange;
    private static double saggingMoment;
    private static double hoggingMoment;

    @BeforeAll
    static void beforeAll() {
        decimalFormat = new DecimalFormat("##.0000");
        tSection = new TSection(300, 1000, 1500, 250);
        tSectionWithLargeFlange = new TSection(250, 800, 1500, 400);
        saggingMoment = 100;
        hoggingMoment = -100;
    }

    @Test
    void areaIsCalculatedCorrectly() {
        double area = tSection.getArea();

        assertEquals(600000, area);
    }

    @Test
    void centroidIsCalculatedCorrectly() {
        double centroid = tSection.getCentroid();

        assertEquals(312.5, centroid);
    }

    @Test
    void secondMomentOfAreaIsCalculatedCorrectly() {
        double secondMomentOfArea = tSection.getSecondMomentOfArea();

        assertEquals(4.765625 * Math.pow(10, 10), secondMomentOfArea);
    }

    @Test
    void widthInCompressionZoneIsCalculatedCorrectlySagging() {
        double widthInCompressionZone = tSection.getWidthInCompressionZone(saggingMoment);

        assertEquals(1500, widthInCompressionZone);
    }

    @Test
    void widthInCompressionZoneIsCalculatedCorrectlyHogging() {
        double widthInCompressionZone = tSection.getWidthInCompressionZone(hoggingMoment);

        assertEquals(300, widthInCompressionZone);
    }

    @Test
    void areaInTensionZonePriorToCrackingIsCalculatedCorrectlyNeutralAxisInFlange() {
        double areaInTensionZonePriorCrackingSaggingMoment = tSection.getAreaInTensionZonePriorCracking(saggingMoment);
        double areaInTensionsZonePriorCrackingHoggingMoment = tSection.getAreaInTensionZonePriorCracking(hoggingMoment);

        assertEquals(206250, areaInTensionZonePriorCrackingSaggingMoment);
        assertEquals(393750, areaInTensionsZonePriorCrackingHoggingMoment);
    }

    @Test
    void areaInTensionZonePriorToCrackingIsCalculatedCorrectlyNeutralAxisInWeb() {
        double areaInTensionZonePriorCrackingSaggingMoment = tSectionWithLargeFlange.getAreaInTensionZonePriorCracking(saggingMoment);
        double areaInTensionsZonePriorCrackingHoggingMoment = tSectionWithLargeFlange.getAreaInTensionZonePriorCracking(hoggingMoment);

        assertEquals(314285.7143, Double.parseDouble(decimalFormat.format(areaInTensionZonePriorCrackingSaggingMoment)));
        assertEquals(385714.2857, Double.parseDouble(decimalFormat.format(areaInTensionsZonePriorCrackingHoggingMoment)));
    }

    @Test
    void factorKIsCalculatedCorrectlyInSagging() {
        double kFactor = tSection.getFactorForNonUniformSelfEquilibratingStresses(saggingMoment);

        assertEquals(0.685, kFactor);
    }

    @Test
    void factorKIsCalculatedCorrectlyInHogging() {
        double kFactor = tSection.getFactorForNonUniformSelfEquilibratingStresses(hoggingMoment);

        assertEquals(0.65, kFactor);
    }

    @Test
    void factorKcIsCalculatedCorrectlyForSagging() {
        double kc = tSection.getFactorForStressDistributionPriorCracking(saggingMoment);

        assertEquals(0.4, kc);
    }

    @Test
    void factorKcIsCalculatedCorrectlyForHoggingWhenNeutralAxisIsInFlange() {
        double kc = tSectionWithLargeFlange.getFactorForStressDistributionPriorCracking(hoggingMoment);

        assertEquals(0.5, kc);
    }

    @Test
    void factorKcIsCalculatedCorrectlyForHoggingWhenNeutralAxisIsInWeb() {
        double kc = tSection.getFactorForStressDistributionPriorCracking(hoggingMoment);

        assertEquals(0.5, kc);
    }

    @Test
    void elasticNeutralAxisIsInFlange() {
        boolean isNeutralAxisInFlange = tSectionWithLargeFlange.isElasticNeutralAxisInFlange();

        assertTrue(isNeutralAxisInFlange);
    }

    @Test
    void elasticNeutralAxisIsInWeb() {
        boolean isNeutralAxisInFlange = tSection.isElasticNeutralAxisInFlange();

        assertFalse(isNeutralAxisInFlange);
    }

    @Test
    void plasticNeutralAxisIsInFlange() {
        boolean isPlasticNeutralAxisInFlange = tSectionWithLargeFlange.isPlasticNeutralAxisInFlange(saggingMoment, 750, 0.9 * 750);

        assertTrue(isPlasticNeutralAxisInFlange);
    }

    @Test
    void plasticNeutralAxisIsInWeb() {
        boolean isPlasticNeutralAxisInWeb = tSection.isPlasticNeutralAxisInFlange(hoggingMoment, 950, 0.9 * 950);

        assertFalse(isPlasticNeutralAxisInWeb);
    }
}