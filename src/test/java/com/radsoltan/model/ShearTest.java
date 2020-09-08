package com.radsoltan.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShearTest {

    private static Shear shear;

    @BeforeAll
    static void beforeAll() {
        shear = () -> {
        };
    }

    @Test
    void maximumShearLinkSpacingIsCalculatedCorrectlyForGivenEffectiveDepth() {
        double maxSpacing = shear.getMaximumSpacingForShearLinks(500);

        assertEquals(375, maxSpacing);
    }
}