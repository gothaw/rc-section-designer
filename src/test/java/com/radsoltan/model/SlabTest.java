package com.radsoltan.model;

import com.radsoltan.model.geometry.SlabStrip;
import com.radsoltan.model.reinforcement.SlabReinforcement;
import com.radsoltan.util.Constants;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
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
    private static SlabReinforcement slabReinforcementWithMultipleLayers;
    private static SlabReinforcement slabReinforcementWithAdditionalReinforcement;
    private static DesignParameters designParameters;
    private static DecimalFormat decimalFormat;

    @BeforeAll
    static void beforeAll() {
        decimalFormat = new DecimalFormat("##.000");
        UlsMomentHogging = -400;
        SlsMomentHogging = 0.7 * UlsMomentHogging;
        UlsMomentSagging = 600;
        SlsMomentSagging = 0.7 * UlsMomentSagging;
        slabStrip = new SlabStrip(500);
        concrete = Concrete.C32_40;
        slabReinforcement = new SlabReinforcement(List.of(25), List.of(0), List.of(200), Collections.emptyList(),
                List.of(32), List.of(0), List.of(175), Collections.emptyList());
        slabReinforcementWithMultipleLayers = new SlabReinforcement(List.of(25), List.of(0), List.of(200), Collections.emptyList(),
                List.of(16, 12, 12, 12), List.of(0, 0, 0), List.of(250, 225, 200, 200), List.of(50, 40, 30));
        slabReinforcementWithAdditionalReinforcement = new SlabReinforcement(List.of(25), List.of(0), List.of(200), Collections.emptyList(),
                List.of(16, 16), List.of(0, 12), List.of(250, 500), List.of(50));
        designParameters = new DesignParameters(25, 0, 50, 500, 20,
                Constants.GAMMA_C_PERSISTENT_TRANSIENT, Constants.GAMMA_S_PERSISTENT_TRANSIENT, 0.85, true, true);
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

        assertEquals(466.621, Double.parseDouble(decimalFormat.format(bendingCapacity)));
        assertEquals(2103.951, Double.parseDouble(decimalFormat.format(requiredReinforcement)));
        assertEquals(2454.369, Double.parseDouble(decimalFormat.format(providedReinforcement)));
    }

    @Test
    void bendingCapacityIsCalculatedCorrectlyForMinimumReinforcement() {
        Slab slab = new Slab(0, 0, slabStrip, concrete, slabReinforcement, designParameters);

        slab.calculateBendingCapacity();

        double bendingCapacity = slab.getBendingCapacity();
        double requiredReinforcement = slab.getRequiredTensileReinforcement();
        double providedReinforcement = slab.getProvidedTensileReinforcement();

        assertEquals(823.829, Double.parseDouble(decimalFormat.format(bendingCapacity)));
        assertEquals(682.414, Double.parseDouble(decimalFormat.format(requiredReinforcement)));
        assertEquals(4595.701, Double.parseDouble(decimalFormat.format(providedReinforcement)));
    }

    @Test
    void bendingCapacityIsCalculatedCorrectlyForSlabReinforcementWithMultipleLayers() {

    }

    @Test
    void bendingCapacityIsCalculatedCorrectlyForSlabReinforcementWithAdditionalReinforcement() {

    }

    @Test
    void errorIsThrownWhenCompressiveForceExceedsCompressionZoneCapacity() {

    }
}