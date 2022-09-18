package com.radsoltan.model.reinforcement;

import com.radsoltan.constants.Constants;
import com.radsoltan.constants.UIText;
import com.radsoltan.model.DesignParameters;
import com.radsoltan.model.geometry.Rectangle;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BeamReinforcementTest {

    private static BeamReinforcement beamReinforcement;
    private static BeamReinforcement beamReinforcementWithMultipleRows;
    private static BeamReinforcement beamReinforcementWithMultipleRowsAndBarTypes;
    private static BeamReinforcement beamReinforcementForSlsCalculations;
    private static DecimalFormat decimalFormat;
    private static double SlsMomentSagging;
    private static double SlsMomentHogging;


    @BeforeAll
    static void beforeAll() {
        decimalFormat = new DecimalFormat("##.000");
        SlsMomentSagging = 100;
        SlsMomentHogging = -100;
        ShearLinks shearLinks = new ShearLinks(500, 8, 200, 2);

        beamReinforcement = new BeamReinforcement(
            List.of(List.of(25, 25, 25)),
            Collections.emptyList(),
            List.of(List.of(16, 16, 16, 16)),
            Collections.emptyList(),
            shearLinks
        );

        beamReinforcementWithMultipleRows = new BeamReinforcement(
            List.of(List.of(25, 25, 25), List.of(16, 16), List.of(12, 12), List.of(12, 12)),
            List.of(50, 40, 30),
            List.of(List.of(20, 20, 20, 20), List.of(12, 12, 12), List.of(10, 10)),
            List.of(40, 30),
            shearLinks
        );

        beamReinforcementWithMultipleRowsAndBarTypes = new BeamReinforcement(
                List.of(List.of(32, 25, 20, 25, 20, 25, 32), List.of(16, 12, 16), List.of(12, 10, 10, 12), List.of(12, 10, 12)),
                List.of(50, 40, 30),
                List.of(List.of(25, 20, 16, 16, 16, 20, 25), List.of(12, 10, 10, 12), List.of(10, 8, 10)),
                List.of(40, 30),
                shearLinks
        );

        beamReinforcementForSlsCalculations = new BeamReinforcement(
                List.of(List.of(32, 25, 20, 20, 25, 32), List.of(16, 12, 16), List.of(12, 10, 10, 12), List.of(12, 10, 12)),
                List.of(50, 40, 30),
                List.of(List.of(25, 20, 16, 16, 16, 20, 25), List.of(12, 10, 10, 12), List.of(10, 8, 10)),
                List.of(40, 30),
                shearLinks,
                new DesignParameters(
                        30,
                        25,
                        35,
                        500,
                        20,
                        Constants.GAMMA_C_PERSISTENT_TRANSIENT,
                        Constants.GAMMA_S_PERSISTENT_TRANSIENT,
                        0.85,
                        true,
                        true,
                        0.3
                ),
                new Rectangle(700, 1300)
        );
    }

    @Test
    void shouldCalculateTopAreaForSimpleReinforcement() {
        double area = beamReinforcement.getTotalAreaOfTopReinforcement();

        assertEquals(1472.622, Double.parseDouble(decimalFormat.format(area)));
    }

    @Test
    void shouldCalculateTopAreaForMultipleRows() {
        double area = beamReinforcementWithMultipleRows.getTotalAreaOfTopReinforcement();

        assertEquals(2327.135, Double.parseDouble(decimalFormat.format(area)));
    }

    @Test
    void shouldCalculateTopAreaForMultipleRowsAndBarTypes() {
        double area = beamReinforcementWithMultipleRowsAndBarTypes.getTotalAreaOfTopReinforcement();

        assertEquals(4912.666, Double.parseDouble(decimalFormat.format(area)));
    }

    @Test
    void shouldCalculateBottomAreaForSimpleReinforcement() {
        double area = beamReinforcement.getTotalAreaOfBottomReinforcement();

        assertEquals(804.248, Double.parseDouble(decimalFormat.format(area)));
    }

    @Test
    void shouldCalculateBottomAreaForMultipleRows() {
        double area = beamReinforcementWithMultipleRows.getTotalAreaOfBottomReinforcement();

        assertEquals(1753.009, Double.parseDouble(decimalFormat.format(area)));
    }

    @Test
    void shouldCalculateBottomAreaForMultipleRowsAndBarTypes() {
        double area = beamReinforcementWithMultipleRowsAndBarTypes.getTotalAreaOfBottomReinforcement();

        assertEquals(2803.871, Double.parseDouble(decimalFormat.format(area)));
    }

    @Test
    void shouldCalculateCentroidOfTopReinforcementForSimpleReinforcement() {
        double centroid = beamReinforcement.getCentroidOfTopReinforcement(25);

        assertEquals(45.5, Double.parseDouble(decimalFormat.format(centroid)));
    }

    @Test
    void shouldCalculateCentroidOfTopReinforcementForMultipleRows() {
        double centroid = beamReinforcementWithMultipleRows.getCentroidOfTopReinforcement(25);

        assertEquals(85.967, Double.parseDouble(decimalFormat.format(centroid)));
    }

    @Test
    void shouldCalculateCentroidOfTopReinforcementForMultipleRowsAndBarTypes() {
        double centroid = beamReinforcementWithMultipleRowsAndBarTypes.getCentroidOfTopReinforcement(25);

        assertEquals(75.476, Double.parseDouble(decimalFormat.format(centroid)));
    }

    @Test
    void shouldCalculateCentroidOfBottomReinforcementForSimpleReinforcement() {
        double centroid = beamReinforcement.getCentroidOfBottomReinforcement(25);

        assertEquals(41, Double.parseDouble(decimalFormat.format(centroid)));
    }

    @Test
    void shouldCalculateCentroidOfBottomReinforcementForMultipleRows() {
        double centroid = beamReinforcementWithMultipleRows.getCentroidOfBottomReinforcement(25);

        assertEquals(62.530, Double.parseDouble(decimalFormat.format(centroid)));
    }

    @Test
    void shouldCalculateCentroidOfBottomReinforcementForMultipleRowsAndBarTypes() {
        double centroid = beamReinforcementWithMultipleRowsAndBarTypes.getCentroidOfBottomReinforcement(25);

        assertEquals(59.326, Double.parseDouble(decimalFormat.format(centroid)));
    }

    @Test
    void shouldCalculateMaxSpacingForSaggingMoment() {
        double maxSpacing = beamReinforcementForSlsCalculations.getMaxBarSpacingForTensileReinforcement(SlsMomentSagging);

        assertEquals(101.5, Double.parseDouble(decimalFormat.format(maxSpacing)));
    }

    @Test
    void shouldCalculateMaxDiameterForSaggingMoment() {
        int maxDiameter = beamReinforcementForSlsCalculations.getMaxBarDiameterForTensileReinforcement(SlsMomentSagging);

        assertEquals(25, Double.parseDouble(decimalFormat.format(maxDiameter)));
    }

    @Test
    void shouldCalculateMaxSpacingForHoggingMoment() {
        double maxSpacing = beamReinforcementForSlsCalculations.getMaxBarSpacingForTensileReinforcement(SlsMomentHogging);

        assertEquals(120.4, Double.parseDouble(decimalFormat.format(maxSpacing)));
    }

    @Test
    void shouldCalculateMaxDiameterForHoggingMoment() {
        int maxDiameter = beamReinforcementForSlsCalculations.getMaxBarDiameterForTensileReinforcement(SlsMomentHogging);

        assertEquals(32, Double.parseDouble(decimalFormat.format(maxDiameter)));
    }

    @Test
    void shouldNotCalculateMaxSpacingIfNotValidConstructor() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> beamReinforcementWithMultipleRowsAndBarTypes.getMaxBarSpacingForTensileReinforcement(SlsMomentSagging));

        String errorMessage = exception.getMessage();
        String expectedMessage = UIText.INVALID_BEAM_REINFORCEMENT;

        assertEquals(expectedMessage, errorMessage);
    }

    @Test
    void shouldGetDescription() {
        String description = beamReinforcementWithMultipleRowsAndBarTypes.getDescription();

        assertEquals("Top rows:\n" +
                        "1st row: 2\u03c632 3\u03c625 2\u03c620,  2nd row: 2\u03c616 1\u03c612,  3rd row: 2\u03c612 2\u03c610,  4th row: 2\u03c612 1\u03c610\n" +
                        "Bottom rows:\n" +
                        "1st row: 2\u03c625 2\u03c620 3\u03c616,  2nd row: 2\u03c612 2\u03c610,  3rd row: 2\u03c610 1\u03c68\n" +
                        "Shear links:\n" +
                        "2 x \u03c68@200 mm", description);
    }
}