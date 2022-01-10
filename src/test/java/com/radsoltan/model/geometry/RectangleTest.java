package com.radsoltan.model.geometry;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RectangleTest {

    private static Rectangle rectangleSmallDepth;
    private static Rectangle rectangleIntermediateDepth;
    private static Rectangle rectangleLargeDepth;
    private static double ULSMoment;

    @BeforeAll
    static void beforeAll() {
        rectangleSmallDepth = new Rectangle(200, 300);
        rectangleIntermediateDepth = new Rectangle(300, 600);
        rectangleLargeDepth = new Rectangle(500, 1000);
        ULSMoment = 100;
    }

    @Test
    void areaIsCalculatedCorrectly() {
        double area = rectangleLargeDepth.getArea();

        assertEquals(500000, area);
    }

    @Test
    void centroidIsCalculatedCorrectly() {
        double centroid = rectangleLargeDepth.getCentroid();

        assertEquals(500, centroid);
    }

    @Test
    void secondMomentOfAreaIsCalculatedCorrectly() {
        double secondMomentOfArea = rectangleLargeDepth.getSecondMomentOfArea();

        assertEquals(Math.pow(1000, 3) * 500 / 12, secondMomentOfArea);
    }

    @Test
    void widthInCompressionZoneIsCalculatedCorrectly() {
        double widthInCompressionZone = rectangleLargeDepth.getWidthInCompressionZone(ULSMoment);

        assertEquals(500, widthInCompressionZone);
    }

    @Test
    void areaInTensionZonePriorToCrackingIsCalculatedCorrectly() {
        double areaInTensionZonePriorToCracking = rectangleLargeDepth.getAreaInTensionZonePriorCracking(ULSMoment);

        assertEquals(1000 * 500 * 0.5, areaInTensionZonePriorToCracking);
    }

    @Test
    void factorKIsCalculatedCorrectlyForSmallDepth() {
        double k = rectangleSmallDepth.getFactorForNonUniformSelfEquilibratingStresses(ULSMoment);

        assertEquals(1.0, k);
    }

    @Test
    void factorKIsCalculatedCorrectlyForIntermediateDepth() {
        double k = rectangleIntermediateDepth.getFactorForNonUniformSelfEquilibratingStresses(ULSMoment);

        assertEquals(0.79, k);

    }

    @Test
    void factorKIsCalculatedCorrectlyForLargeDepth() {
        double k = rectangleLargeDepth.getFactorForNonUniformSelfEquilibratingStresses(ULSMoment);

        assertEquals(0.65, k);
    }

    @Test
    void factorKcIsCalculatedCorrectly() {
        double kc = rectangleLargeDepth.getFactorForStressDistributionPriorCracking(ULSMoment);

        assertEquals(0.4, kc);
    }
}