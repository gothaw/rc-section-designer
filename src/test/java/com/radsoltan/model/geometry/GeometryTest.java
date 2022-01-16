package com.radsoltan.model.geometry;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeometryTest {

    private static Geometry geometry;
    private static TSection tSection;
    private static double moment;

    @BeforeAll
    static void beforeAll() {
        tSection = new TSection(300, 1000, 1500, 250);
        moment = 100;
        geometry = new Geometry(tSection);
    }

    @Test
    void sectionIsRetrievedCorrectly() {
        Section section = geometry.getSection();

        assertEquals(tSection, section);
    }

    @Test
    void depthIsRetrievedCorrectly() {
        int depth = geometry.getDepth();

        assertEquals(1000, depth);
    }

    @Test
    void widthIsRetrievedCorrectly() {
        int width = geometry.getWidth();

        assertEquals(300, width);
    }

    @Test
    void areaIsRetrievedCorrectly() {
        double area = geometry.getArea();

        assertEquals(600000, area);
    }

    @Test
    void widthInCompressionZoneIsRetrievedCorrectly() {
        double width = geometry.getWidthInCompressionZone(moment);

        assertEquals(1500, width);
    }

    @Test
    void widthInTensionZoneIsRetrievedCorrectly() {
        double width = geometry.getWidthInTensionZone(moment);

        assertEquals(300, width);
    }

    @Test
    void areaInTensionZonePriorCrackingIsRetrievedCorrectly() {
        double areaInTensionZone = geometry.getAreaInTensionZonePriorCracking(moment);

        assertEquals(206250 ,areaInTensionZone);
    }

    @Test
    void kFactorIsRetrievedCorrectly() {
        double k = geometry.getFactorForNonUniformSelfEquilibratingStresses(moment);

        assertEquals(0.685, k);
    }

    @Test
    void kCFactorIsRetrievedCorrectly() {
        double kc = geometry.getFactorForStressDistributionPriorCracking(moment);

        assertEquals(0.4 , kc);
    }

    @Test
    void flangedSectionIsIdentifiedCorrectly() {
        boolean isFlanged = geometry.isFlangedSection();

        assertTrue(isFlanged);
    }

    @Test
    void plasticNeutralAxisIsInFlange() {
        TSection tSectionWithLargeFlange = new TSection(250, 800, 1500, 400);
        Geometry geometry = new Geometry(tSectionWithLargeFlange);
        boolean isNeutralAxisInFlange = geometry.checkIfPlasticNeutralAxisInFlange(moment, 750, 0.9 * 750);

        assertTrue(isNeutralAxisInFlange);
    }

    @Test
    void flangeThicknessIsRetrievedCorrectly() {
        int flangeThickness = geometry.getFlangeThickness();

        assertEquals(250, flangeThickness);
    }

    @Test
    void flangeWidthIsRetrievedCorrectly() {
        int flangeWidth = geometry.getFlangeWidth();

        assertEquals(1500, flangeWidth);
    }

    @Test
    void descriptionIsRetrievedCorrectly() {
        String description = geometry.getDescription();

        assertEquals("T section: 750 mm downstand x 300 mm web width + flange 1500 mm wide x 250 mm thick.", description);
    }
}