package com.radsoltan.model.reinforcement;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SlabReinforcementTest {

    private static SlabReinforcement slabReinforcement;
    private static SlabReinforcement slabReinforcementWithMultipleLayers;
    private static SlabReinforcement slabReinforcementWithAdditionalReinforcement;
    private static DecimalFormat decimalFormat;
    private static double SlsMomentSagging;
    private static double SlsMomentHogging;

    @BeforeAll
    static void beforeAll() {
        decimalFormat = new DecimalFormat("##.000");
        SlsMomentSagging = 100;
        SlsMomentHogging = -100;

        slabReinforcement = new SlabReinforcement(
                List.of(25),
                List.of(0),
                List.of(200),
                Collections.emptyList(),
                List.of(32),
                List.of(0),
                List.of(175),
                Collections.emptyList()
        );
        slabReinforcementWithMultipleLayers = new SlabReinforcement(
                List.of(20, 20, 10),
                List.of(0, 0, 0),
                List.of(200, 200, 175),
                List.of(25, 25),
                List.of(20, 20, 16, 16),
                List.of(0, 0, 0, 0),
                List.of(250, 250, 200, 200),
                List.of(50, 40, 30)
        );
        slabReinforcementWithAdditionalReinforcement = new SlabReinforcement(
                List.of(20, 20, 10),
                List.of(16, 0, 12),
                List.of(200, 200, 175),
                List.of(25, 25),
                List.of(20, 20, 16, 16),
                List.of(25, 0, 0, 16),
                List.of(250, 250, 200, 200),
                List.of(50, 40, 30)
        );
    }

    @Test
    void shouldCalculateTopAreaWithSingleLayer() {
        double area = slabReinforcement.getTotalAreaOfTopReinforcement();

        assertEquals(2454.369, Double.parseDouble(decimalFormat.format(area)));
    }

    @Test
    void shouldCalculateTopAreaWithMultipleLayers() {
        double area = slabReinforcementWithMultipleLayers.getTotalAreaOfTopReinforcement();

        assertEquals(3590.392, Double.parseDouble(decimalFormat.format(area)));
    }

    @Test
    void shouldCalculateTopAreaWithAdditionalReinforcement() {
        double area = slabReinforcementWithAdditionalReinforcement.getTotalAreaOfTopReinforcement();

        assertEquals(5241.972, Double.parseDouble(decimalFormat.format(area)));
    }

    @Test
    void shouldCalculateBottomAreaWithSingleLayer() {
        double area = slabReinforcement.getTotalAreaOfBottomReinforcement();

        assertEquals(4595.701, Double.parseDouble(decimalFormat.format(area)));
    }

    @Test
    void shouldCalculateBottomAreaWithMultipleLayers() {
        double area = slabReinforcementWithMultipleLayers.getTotalAreaOfBottomReinforcement();

        assertEquals(4523.893, Double.parseDouble(decimalFormat.format(area)));
    }

    @Test
    void shouldCalculateBottomAreaWithAdditionalReinforcement() {
        double area = slabReinforcementWithAdditionalReinforcement.getTotalAreaOfBottomReinforcement();

        assertEquals(7492.698, Double.parseDouble(decimalFormat.format(area)));
    }

    @Test
    void shouldCalculateCentroidOfTopReinforcementWithSingleLayer() {
        double centroid = slabReinforcement.getCentroidOfTopReinforcement(35);

        assertEquals(47.5, Double.parseDouble(decimalFormat.format(centroid)));
    }

    @Test
    void shouldCalculateCentroidOfTopReinforcementWithMultipleLayers() {
        double centroid = slabReinforcementWithMultipleLayers.getCentroidOfTopReinforcement(20);

        assertEquals(60.312, Double.parseDouble(decimalFormat.format(centroid)));
    }

    @Test
    void shouldCalculateCentroidOfTopReinforcementWithAdditionalReinforcement() {
        double centroid = slabReinforcementWithAdditionalReinforcement.getCentroidOfTopReinforcement(25);

        assertEquals(66.45, Double.parseDouble(decimalFormat.format(centroid)));
    }

    @Test
    void shouldCalculateCentroidOfBottomReinforcementWithSingleLayer() {
        double centroid = slabReinforcement.getCentroidOfBottomReinforcement(50);

        assertEquals(66, Double.parseDouble(decimalFormat.format(centroid)));
    }

    @Test
    void shouldCalculateCentroidOfBottomReinforcementWithMultipleLayers() {
        double centroid = slabReinforcementWithMultipleLayers.getCentroidOfBottomReinforcement(35);

        assertEquals(131.556, Double.parseDouble(decimalFormat.format(centroid)));
    }

    @Test
    void shouldCalculateCentroidOfBottomReinforcementWithAdditionalReinforcement() {
        double centroid = slabReinforcementWithAdditionalReinforcement.getCentroidOfBottomReinforcement(35);

        assertEquals(124.531, Double.parseDouble(decimalFormat.format(centroid)));
    }

    @Test
    void shouldCalculateMaxSpacingForSaggingMoment() {
        double maxSpacing = slabReinforcement.getMaxBarSpacingForTensileReinforcement(SlsMomentSagging);
        double maxSpacingForReinforcementWithAdditionalRebar = slabReinforcementWithAdditionalReinforcement.getMaxBarSpacingForTensileReinforcement(SlsMomentSagging);
        double maxSpacingForReinforcementWithMultipleLayers = slabReinforcementWithMultipleLayers.getMaxBarSpacingForTensileReinforcement(SlsMomentSagging);

        assertEquals(175, maxSpacing);
        assertEquals(250, maxSpacingForReinforcementWithAdditionalRebar);
        assertEquals(250, maxSpacingForReinforcementWithMultipleLayers);
    }

    @Test
    void shouldCalculateMaxDiameterForSaggingMoment() {
        double maxDiameter = slabReinforcement.getMaxBarDiameterForTensileReinforcement(SlsMomentSagging);
        double maxDiameterForReinforcementWithAdditionalRebar = slabReinforcementWithAdditionalReinforcement.getMaxBarDiameterForTensileReinforcement(SlsMomentSagging);
        double maxDiameterForReinforcementWithMultipleLayers = slabReinforcementWithMultipleLayers.getMaxBarDiameterForTensileReinforcement(SlsMomentSagging);

        assertEquals(32, maxDiameter);
        assertEquals(25, maxDiameterForReinforcementWithAdditionalRebar);
        assertEquals(20, maxDiameterForReinforcementWithMultipleLayers);
    }

    @Test
    void shouldCalculateMaxSpacingForHoggingMoment() {
        double maxSpacing = slabReinforcement.getMaxBarSpacingForTensileReinforcement(SlsMomentHogging);
        double maxSpacingForReinforcementWithAdditionalRebar = slabReinforcementWithAdditionalReinforcement.getMaxBarSpacingForTensileReinforcement(SlsMomentHogging);
        double maxSpacingForReinforcementWithMultipleLayers = slabReinforcementWithMultipleLayers.getMaxBarSpacingForTensileReinforcement(SlsMomentHogging);

        assertEquals(200, maxSpacing);
        assertEquals(200, maxSpacingForReinforcementWithAdditionalRebar);
        assertEquals(200, maxSpacingForReinforcementWithAdditionalRebar);
        assertEquals(200, maxSpacingForReinforcementWithMultipleLayers);
    }

    @Test
    void shouldCalculateMaxDiameterForHoggingMoment() {
        double maxDiameter = slabReinforcement.getMaxBarDiameterForTensileReinforcement(SlsMomentHogging);
        double maxDiameterForReinforcementWithAdditionalRebar = slabReinforcementWithAdditionalReinforcement.getMaxBarDiameterForTensileReinforcement(SlsMomentHogging);
        double maxDiameterForReinforcementWithMultipleLayers = slabReinforcementWithMultipleLayers.getMaxBarDiameterForTensileReinforcement(SlsMomentHogging);

        assertEquals(25, maxDiameter);
        assertEquals(20, maxDiameterForReinforcementWithAdditionalRebar);
        assertEquals(20, maxDiameterForReinforcementWithMultipleLayers);
    }

    @Test
    void shouldGetDescription() {
        String description = slabReinforcementWithAdditionalReinforcement.getDescription();

        assertEquals("Top layers:\n" +
                "1st layer: \u03c620@200 + \u03c616@200,  2nd layer: \u03c620@200,  3rd layer: \u03c610@175 + \u03c612@175\n" +
                "Bottom layers:\n" +
                "1st layer: \u03c620@250 + \u03c625@250,  2nd layer: \u03c620@250,  3rd layer: \u03c616@200,  4th layer: \u03c616@200 + \u03c616@200",
                description);
    }
}