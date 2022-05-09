package com.radsoltan.model;

import com.radsoltan.constants.UIText;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.text.DecimalFormat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;

class CrackingTest {
    private static Cracking cracking;
    private static int width;
    private static int depth;
    private static double effectiveDepth;
    private static double neutralAxis;
    private static double UlsMoment;
    private static double SlsMoment;
    private static int spacing;
    private static int spacingLarge;
    private static int maxDiameter;
    private static double providedReinforcement;
    private static double requiredReinforcement;
    private static Concrete concrete;
    private static DesignParameters designParameters;
    private static DecimalFormat decimalFormat;

    @BeforeAll
    static void beforeAll() {
        cracking = spy(Cracking.class);
        decimalFormat = new DecimalFormat("##.0000");
        width = 1000;
        depth = 300;
        effectiveDepth = 260;
        neutralAxis = 32.5;
        UlsMoment = 100;
        SlsMoment = 50;
        spacing = 200;
        spacingLarge = 500;
        maxDiameter = 20;
        providedReinforcement = 1570.796;
        requiredReinforcement = 931.174;
        concrete = Concrete.C40_50;
        designParameters = new DesignParameters(30, 0, 30, 500, 20, 1.4, 1.15, 0.85, true, true, 0.3);
    }

    @Test
    void shouldCalculateCrackWidth() {
        double crackWidth = cracking.calculateCrackWidth(width, depth, effectiveDepth, neutralAxis, UlsMoment, SlsMoment, spacing, maxDiameter, providedReinforcement, requiredReinforcement, concrete, designParameters);

        assertEquals(0.1342, Double.parseDouble(decimalFormat.format(crackWidth)));
    }

    @Test
    void shouldNotCalculateCrackWidthIfReinforcementIsWidelySpaced() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> cracking.calculateCrackWidth(width, depth, effectiveDepth, neutralAxis, UlsMoment, SlsMoment, spacingLarge, maxDiameter, providedReinforcement, requiredReinforcement, concrete, designParameters));

        String errorMessage = exception.getMessage();
        String expectedMessage = UIText.INVALID_BAR_SPACING_CRACKS;

        assertEquals(expectedMessage, errorMessage);
    }

    @Test
    void shouldCalculateEffectiveTensionAreaUsingFirstCondition() {
        // Testing 2.5(h-d)
        double effectiveDepth = 280;

        double area = cracking.calculateEffectiveTensionArea(width, depth, effectiveDepth, neutralAxis);
        double expectedArea = width * 2.5 * (300 - 280);

        assertEquals(Double.parseDouble(decimalFormat.format(expectedArea)), Double.parseDouble(decimalFormat.format(area)));
    }

    @Test
    void shouldCalculateEffectiveTensionAreaUsingSecondCondition() {
        // Testing (h-x)/3
        double area = cracking.calculateEffectiveTensionArea(width, depth, effectiveDepth, neutralAxis);
        double expectedArea = (depth - neutralAxis)/3 * 1000;

        assertEquals(Double.parseDouble(decimalFormat.format(expectedArea)), Double.parseDouble(decimalFormat.format(area)));
    }

    @Test
    void shouldCalculateDifferenceBetweenSteelAndConcreteStrain() {
        DecimalFormat decimalFormat = new DecimalFormat("##.0000000");

        double strainDifference = cracking.calculateDifferenceBetweenSteelAndConcreteStrain(151.61223, Concrete.C40_50, 0.01761641);

        assertEquals(0.0004548, Double.parseDouble(decimalFormat.format(strainDifference)));
    }

    @Test
    void shouldCalculateDifferenceBetweenSteelAndConcreteStrainUsingMinimumValue() {
        DecimalFormat decimalFormat = new DecimalFormat("##.0000000");

        double strainDifference = cracking.calculateDifferenceBetweenSteelAndConcreteStrain(200, Concrete.C40_50, 0.0224299065);

        assertEquals(0.0006479, Double.parseDouble(decimalFormat.format(strainDifference)));
    }
}