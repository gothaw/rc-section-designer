package com.radsoltan.model;

import com.radsoltan.model.geometry.SlabStrip;
import com.radsoltan.model.reinforcement.SlabReinforcement;
import com.radsoltan.constants.Constants;
import com.radsoltan.constants.UIText;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SlabTest {
    private static double UlsMomentHogging;
    private static double SlsMomentHogging;
    private static double UlsMomentSagging;
    private static double SlsMomentSagging;
    private static SlabStrip slabStrip;
    private static Concrete concrete;
    private static SlabReinforcement slabReinforcement;
    private static SlabReinforcement slabReinforcementWithExcessiveSpacing;
    private static SlabReinforcement slabReinforcementWithMultipleLayers;
    private static SlabReinforcement slabReinforcementWithAdditionalReinforcement;
    private static DesignParameters designParameters;
    private static DecimalFormat decimalFormat;
    private static DecimalFormat decimalFormatCracks;

    @BeforeAll
    static void beforeAll() {
        decimalFormat = new DecimalFormat("##.000");
        decimalFormatCracks = new DecimalFormat("##.0000");
        UlsMomentHogging = -400;
        SlsMomentHogging = 0.7 * UlsMomentHogging;
        UlsMomentSagging = 600;
        SlsMomentSagging = 0.7 * UlsMomentSagging;
        slabStrip = new SlabStrip(500);
        concrete = Concrete.C32_40;
        slabReinforcement = new SlabReinforcement(List.of(25), List.of(0), List.of(175), Collections.emptyList(),
                List.of(32), List.of(0), List.of(175), Collections.emptyList());
        slabReinforcementWithExcessiveSpacing = new SlabReinforcement(List.of(25), List.of(0), List.of(200), Collections.emptyList(),
                List.of(25), List.of(0), List.of(500), Collections.emptyList());
        slabReinforcementWithMultipleLayers = new SlabReinforcement(List.of(25), List.of(0), List.of(200), Collections.emptyList(),
                List.of(20, 20, 16, 16), List.of(0, 0, 0, 0), List.of(250, 225, 200, 200), List.of(50, 40, 30));
        slabReinforcementWithAdditionalReinforcement = new SlabReinforcement(List.of(25), List.of(0), List.of(200), Collections.emptyList(),
                List.of(25, 16), List.of(20, 10), List.of(300, 250), List.of(50));
        designParameters = new DesignParameters(25, 0, 50, 500, 20,
                Constants.GAMMA_C_PERSISTENT_TRANSIENT, Constants.GAMMA_S_PERSISTENT_TRANSIENT, 0.85, true, true, 0.3);
    }

    @Test
    void bendingCapacityIsCalculatedCorrectlyForSaggingMoment() {
        Slab slab = new Slab(UlsMomentSagging, SlsMomentSagging, slabStrip, concrete, slabReinforcement, designParameters);

        slab.calculateBendingCapacity();

        double bendingCapacity = slab.getBendingCapacity();
        double requiredReinforcement = slab.getRequiredTensileReinforcement();
        double providedReinforcement = slab.getProvidedTensileReinforcement();

        assertEquals(782.794, Double.parseDouble(decimalFormat.format(bendingCapacity)));
        assertEquals(3522.537, Double.parseDouble(decimalFormat.format(requiredReinforcement)));
        assertEquals(4595.701, Double.parseDouble(decimalFormat.format(providedReinforcement)));
    }

    @Test
    void bendingCapacityIsCalculatedCorrectlyForHoggingMoment() {
        Slab slab = new Slab(UlsMomentHogging, SlsMomentHogging, slabStrip, concrete, slabReinforcement, designParameters);

        slab.calculateBendingCapacity();

        double bendingCapacity = slab.getBendingCapacity();
        double requiredReinforcement = slab.getRequiredTensileReinforcement();
        double providedReinforcement = slab.getProvidedTensileReinforcement();

        assertEquals(533.281, Double.parseDouble(decimalFormat.format(bendingCapacity)));
        assertEquals(2103.951, Double.parseDouble(decimalFormat.format(requiredReinforcement)));
        assertEquals(2804.993, Double.parseDouble(decimalFormat.format(providedReinforcement)));
    }

    @Test
    void bendingCapacityIsCalculatedCorrectlyForMinimumReinforcement() {
        Slab slab = new Slab(0, 0, slabStrip, concrete, slabReinforcement, designParameters);

        slab.calculateBendingCapacity();

        double bendingCapacity = slab.getBendingCapacity();
        double requiredReinforcement = slab.getRequiredTensileReinforcement();
        double providedReinforcement = slab.getProvidedTensileReinforcement();

        assertEquals(823.829, Double.parseDouble(decimalFormat.format(bendingCapacity)));
        assertEquals(677.040, Double.parseDouble(decimalFormat.format(requiredReinforcement)));
        assertEquals(4595.701, Double.parseDouble(decimalFormat.format(providedReinforcement)));
    }

    @Test
    void bendingCapacityIsCalculatedCorrectlyForMultipleLayers() {
        Slab slab = new Slab(UlsMomentSagging, SlsMomentSagging, slabStrip, concrete, slabReinforcementWithMultipleLayers, designParameters);

        slab.calculateBendingCapacity();

        double bendingCapacity = slab.getBendingCapacity();
        double requiredReinforcement = slab.getRequiredTensileReinforcement();
        double providedReinforcement = slab.getProvidedTensileReinforcement();

        assertEquals(605.260, Double.parseDouble(decimalFormat.format(bendingCapacity)));
        assertEquals(4622.988, Double.parseDouble(decimalFormat.format(requiredReinforcement)));
        assertEquals(4663.520, Double.parseDouble(decimalFormat.format(providedReinforcement)));
    }

    @Test
    void bendingCapacityIsCalculatedCorrectlyForAdditionalReinforcement() {
        Slab slab = new Slab(UlsMomentSagging, SlsMomentSagging, slabStrip, concrete, slabReinforcementWithAdditionalReinforcement, designParameters);

        slab.calculateBendingCapacity();

        double bendingCapacity = slab.getBendingCapacity();
        double requiredReinforcement = slab.getRequiredTensileReinforcement();
        double providedReinforcement = slab.getProvidedTensileReinforcement();

        assertEquals(615.433, Double.parseDouble(decimalFormat.format(bendingCapacity)));
        assertEquals(3706.511, Double.parseDouble(decimalFormat.format(requiredReinforcement)));
        assertEquals(3801.851, Double.parseDouble(decimalFormat.format(providedReinforcement)));
    }

    @Test
    void errorIsThrownWhenCompressiveForceExceedsCompressionZoneCapacity() {
        Slab slab = new Slab(1.5 * UlsMomentSagging, 1.5 * SlsMomentSagging, slabStrip, concrete, slabReinforcementWithMultipleLayers, designParameters);

        Exception exception = assertThrows(IllegalArgumentException.class, slab::calculateBendingCapacity);

        String errorMessage = exception.getMessage();
        String expectedMessage = UIText.REDESIGN_SECTION_DUE_TO_COMPRESSIVE_FORCE;

        assertEquals(expectedMessage, errorMessage);
    }

    @Test
    void crackingIsCalculatedForSaggingMoment() {
        Slab slab = new Slab(UlsMomentSagging, SlsMomentSagging, slabStrip, concrete, slabReinforcement, designParameters);

        slab.calculateBendingCapacity();
        slab.calculateCracking();

        double crackWidth = slab.getCrackWidth();

        assertEquals(0.3792, Double.parseDouble(decimalFormatCracks.format(crackWidth)));
    }

    @Test
    void crackingIsCalculatedForHoggingMoment() {
        Slab slab = new Slab(UlsMomentHogging, SlsMomentHogging, slabStrip, concrete, slabReinforcement, designParameters);

        slab.calculateBendingCapacity();
        slab.calculateCracking();

        double crackWidth = slab.getCrackWidth();

        assertEquals(0.2512, Double.parseDouble(decimalFormatCracks.format(crackWidth)));
    }

    @Test
    void crackingIsNotCalculatedForExcessiveSpacing() {
        Slab slab = new Slab(UlsMomentSagging, SlsMomentSagging, slabStrip, concrete, slabReinforcementWithExcessiveSpacing, designParameters);

        slab.calculateBendingCapacity();

        Exception exception = assertThrows(IllegalArgumentException.class, slab::calculateCracking);

        String errorMessage = exception.getMessage();
        String expectedMessage = UIText.INVALID_BAR_SPACING_CRACKS;

        assertEquals(expectedMessage, errorMessage);
    }

    @Test
    void crackingIsCalculatedForMinimumReinforcement() {
        Slab slab = new Slab(0, 0, slabStrip, concrete, slabReinforcement, designParameters);

        slab.calculateBendingCapacity();
        slab.calculateCracking();

        double crackWidth = slab.getCrackWidth();

        assertEquals(0, Double.parseDouble(decimalFormatCracks.format(crackWidth)));
    }

    @Test
    void crackingIsCalculatedForMultipleLayers() {
        Slab slab = new Slab(UlsMomentSagging, SlsMomentSagging, slabStrip, concrete, slabReinforcementWithMultipleLayers, designParameters);

        slab.calculateBendingCapacity();
        slab.calculateCracking();

        double crackWidth = slab.getCrackWidth();

        assertEquals(0.4084, Double.parseDouble(decimalFormatCracks.format(crackWidth)));
    }

    @Test
    void crackingIsCalculatedForAdditionalReinforcement() {
        Slab slab = new Slab(UlsMomentSagging, SlsMomentSagging, slabStrip, concrete, slabReinforcementWithAdditionalReinforcement, designParameters);

        slab.calculateBendingCapacity();
        slab.calculateCracking();

        double crackWidth = slab.getCrackWidth();

        assertEquals(0.4739, Double.parseDouble(decimalFormatCracks.format(crackWidth)));
    }

    @Test
    void errorIsThrownForCrackingWhenBendingCapacityIsNotCalculated() {
        Slab slab = new Slab(UlsMomentHogging, SlsMomentHogging, slabStrip, concrete, slabReinforcement, designParameters);

        Exception exception = assertThrows(IllegalArgumentException.class, slab::calculateCracking);

        String errorMessage = exception.getMessage();
        String expectedMessage = UIText.INVALID_BENDING_CAPACITY;

        assertEquals(expectedMessage, errorMessage);
    }
}