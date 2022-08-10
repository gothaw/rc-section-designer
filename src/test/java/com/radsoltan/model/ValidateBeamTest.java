package com.radsoltan.model;

import com.radsoltan.model.geometry.Geometry;
import com.radsoltan.model.reinforcement.BeamReinforcement;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ValidateBeamTest {

    private static Geometry geometry;
    private static DesignParameters designParameters;
    private static BeamReinforcement validBeamReinforcement;
    private static int minimumBeamDepthForValidSpacing;
    private static BeamReinforcement beamReinforcementWithInvalidHorizontalSpacing;
    private static BeamReinforcement beamReinforcementWithInvalidVerticalSpacing;
    private static BeamReinforcement invalidBeamReinforcement;

    @BeforeAll
    static void beforeAll() {
        designParameters = new DesignParameters(30, 25, 35, 500, 20, 1.5, 1.15, 0.85, true, true, 0.3);

    }


    @Test
    void noErrorMessagesForValidBeam() {

    }

    @Test
    void errorMessageIsAddedWhenBeamDepthIsInvalid() {

    }

    @Test
    void errorMessageIsAddedWhenHorizontalBarSpacingIsInvalid() {

    }

    @Test
    void errorMessageIsAddedWhenVerticalSpacingBetweenRowsIsInvalid() {

    }

    @Test
    void errorMessageIsAddedWhenBeamReinforcementIsInvalid() {

    }
}