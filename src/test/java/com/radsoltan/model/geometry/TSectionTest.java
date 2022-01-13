package com.radsoltan.model.geometry;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.text.DecimalFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    }

    @Test
    void factorKIsCalculatedCorrectlyInHogging() {

    }

    @Test
    void factorKcIsCalculatedCorrectlyForHoggingWhenNeutralAxisIsInFlange() {

    }

    @Test
    void factorKcIsCalculatedCorrectlyForHoggingWhenNeutralAxisIsInWeb() {

    }

    @Test
    void elasticNeutralAxisIsInFlange() {

    }

    @Test
    void elasticNeutralAxisIsInWeb() {

    }

    @Test
    void plasticNeutralAxisIsInFlange() {

    }

    @Test
    void plasticNeutralAxisIsInWeb() {

    }
}