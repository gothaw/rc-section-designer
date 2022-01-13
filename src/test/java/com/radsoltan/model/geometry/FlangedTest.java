package com.radsoltan.model.geometry;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;

class FlangedTest {

    private static Flanged flanged;

    @BeforeAll
    static void beforeAll() {
        flanged = spy(Flanged.class);
    }

    @Test
    void factorKIsCalculatedCorrectlyForSmallSize() {
        int size = 200;

        double kFactor = flanged.getFactorForNonUniformSelfEquilibratingStressesForWebOrFlange(size);

        assertEquals(1.0, kFactor);
    }

    @Test
    void factorKIsCalculatedCorrectlyForIntermediateSize() {
        int size = 600;

        double kFactor = flanged.getFactorForNonUniformSelfEquilibratingStressesForWebOrFlange(size);

        assertEquals(0.79, kFactor);
    }

    @Test
    void factorKIsCalculatedCorrectlyForLargeSize() {
        int size = 1000;

        double kFactor = flanged.getFactorForNonUniformSelfEquilibratingStressesForWebOrFlange(size);

        assertEquals(0.65, kFactor);
    }
}