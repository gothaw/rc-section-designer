package com.radsoltan.model;

import com.radsoltan.constants.Constants;
import com.radsoltan.constants.UIText;
import com.radsoltan.model.geometry.Geometry;
import com.radsoltan.model.geometry.Rectangle;
import com.radsoltan.model.reinforcement.BeamReinforcement;
import com.radsoltan.model.reinforcement.ShearLinks;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BeamTest {
    private static double UlsMomentHogging;
    private static double SlsMomentHogging;
    private static double UlsMomentSagging;
    private static double SlsMomentSagging;
    private static double UlsShear;
    private static Geometry geometry;
    private static Concrete concrete;
    private static BeamReinforcement beamReinforcement;
    private static BeamReinforcement beamReinforcementWithMultipleRows;
    private static BeamReinforcement beamReinforcementWithMultipleRowsAndBarTypes;
    private static ShearLinks shearLinks;
    private static DesignParameters designParameters;
    private static DecimalFormat decimalFormat;
    private static DecimalFormat decimalFormatCracks;

    @BeforeAll
    static void beforeAll() {
        decimalFormat = new DecimalFormat("##.000");
        decimalFormatCracks = new DecimalFormat("##.0000");
        UlsMomentHogging = -200;
        SlsMomentHogging = 0.7 * UlsMomentHogging;
        UlsMomentSagging = 300;
        SlsMomentSagging = 0.7 * UlsMomentSagging;
        UlsShear = 500;
        geometry = new Geometry(new Rectangle(500, 800));
        concrete = Concrete.C32_40;
        shearLinks = new ShearLinks(500, 10, 200, 3);

        beamReinforcement = new BeamReinforcement(
                List.of(List.of(16, 16, 16, 16)),
                Collections.emptyList(),
                List.of(List.of(25, 25, 25)),
                Collections.emptyList(),
                shearLinks
        );

        beamReinforcementWithMultipleRows = new BeamReinforcement(
                List.of(List.of(32, 32)),
                Collections.emptyList(),
                List.of(List.of(25, 25, 25), List.of(16, 16), List.of(12, 12), List.of(12, 12)),
                List.of(50, 40, 30),
                shearLinks
        );

        beamReinforcementWithMultipleRowsAndBarTypes = new BeamReinforcement(
                List.of(List.of(32, 32)),
                Collections.emptyList(),
                List.of(List.of(32, 25, 20, 25, 20, 25, 32), List.of(16, 12, 16), List.of(12, 10, 10, 12), List.of(12, 10, 12)),
                List.of(50, 40, 30),
                shearLinks
        );

        designParameters = new DesignParameters(35, 25, 35, 500, 20,
                Constants.GAMMA_C_PERSISTENT_TRANSIENT, Constants.GAMMA_S_PERSISTENT_TRANSIENT, 0.85, true, true, 0.3);
    }

    @Test
    void bendingCapacityIsCalculatedCorrectlyForSaggingMoment() {
        Beam beam = new Beam(UlsMomentSagging, UlsShear, SlsMomentSagging, geometry, concrete, beamReinforcement, designParameters);

        beam.calculateBendingCapacity();

        double bendingCapacity = beam.getBendingCapacity();
        double requiredReinforcement = beam.getRequiredTensileReinforcement();
        double providedReinforcement = beam.getProvidedTensileReinforcement();

        assertEquals(451.631, Double.parseDouble(decimalFormat.format(bendingCapacity)));
        assertEquals(978.203, Double.parseDouble(decimalFormat.format(requiredReinforcement)));
        assertEquals(1472.622, Double.parseDouble(decimalFormat.format(providedReinforcement)));
    }

    @Test
    void bendingCapacityIsCalculatedCorrectlyForHoggingMoment() {
        Beam beam = new Beam(UlsMomentHogging, UlsShear, SlsMomentHogging, geometry, concrete, beamReinforcement, designParameters);

        beam.calculateBendingCapacity();

        double bendingCapacity = beam.getBendingCapacity();
        double requiredReinforcement = beam.getRequiredTensileReinforcement();
        double providedReinforcement = beam.getProvidedTensileReinforcement();

        assertEquals(248.145, Double.parseDouble(decimalFormat.format(bendingCapacity)));
        assertEquals(648.207, Double.parseDouble(decimalFormat.format(requiredReinforcement)));
        assertEquals(804.248, Double.parseDouble(decimalFormat.format(providedReinforcement)));
    }

    @Test
    void bendingCapacityIsCalculatedCorrectlyForMinimumReinforcement() {
        Beam beam = new Beam(0, 0, 0, geometry, concrete, beamReinforcement, designParameters);

        beam.calculateBendingCapacity();

        double bendingCapacity = beam.getBendingCapacity();
        double requiredReinforcement = beam.getRequiredTensileReinforcement();
        double providedReinforcement = beam.getProvidedTensileReinforcement();

        assertEquals(451.631, Double.parseDouble(decimalFormat.format(bendingCapacity)));
        assertEquals(579.15, Double.parseDouble(decimalFormat.format(requiredReinforcement)));
        assertEquals(1472.622, Double.parseDouble(decimalFormat.format(providedReinforcement)));
    }

    @Test
    void bendingCapacityIsCalculatedCorrectlyForMultipleRows() {

    }

    @Test
    void bendingCapacityIsCalculatedCorrectlyForMultipleRowsAndBarTypes() {

    }

    @Test
    void bendingCapacityIsCalculatedForDoublyReinforcedRectangularSection() {
        
    }

    @Test
    void errorIsThrownIfExcessiveCompressiveForceAndFlangedSectionWithPNAinWeb() {
    
    }

    @Test
    void shearCapacityIsCalculatedCorrectly() {
        Beam beam = new Beam(UlsMomentSagging, UlsShear, SlsMomentSagging, geometry, concrete, beamReinforcement, designParameters);

        beam.calculateBendingCapacity();
        beam.calculateShearCapacity();

        double requiredShearReinforcement = beam.getRequiredShearReinforcement();
        double providedShearReinforcement = beam.getProvidedShearReinforcement();

        assertEquals(652.089, Double.parseDouble(decimalFormat.format(requiredShearReinforcement)));
        assertEquals(1178.097, Double.parseDouble(decimalFormat.format(providedShearReinforcement)));
    }

    @Test
    void shearCapacityIsCalculatedCorrectlyForMinimumShearReinforcement() {
        Beam beam = new Beam(0, 0, 0, geometry, concrete, beamReinforcement, designParameters);

        beam.calculateBendingCapacity();
        beam.calculateShearCapacity();

        double requiredShearReinforcement = beam.getRequiredShearReinforcement();
        double providedShearReinforcement = beam.getProvidedShearReinforcement();

        assertEquals(452.548, Double.parseDouble(decimalFormat.format(requiredShearReinforcement)));
        assertEquals(1178.097, Double.parseDouble(decimalFormat.format(providedShearReinforcement)));
    }

    @Test
    void errorIsThrownForExcessiveShearForce() {
        double excessiveShear = 1700;

        Beam beam = new Beam(UlsMomentSagging, excessiveShear, SlsMomentSagging, geometry, concrete, beamReinforcement, designParameters);

        beam.calculateBendingCapacity();

        Exception exception = assertThrows(IllegalArgumentException.class, beam::calculateShearCapacity);

        String errorMessage = exception.getMessage();
        String expectedMessage = UIText.REDESIGN_SECTION_DUE_TO_HIGH_SHEAR;

        assertEquals(expectedMessage, errorMessage);
    }

    @Test
    void crackingIsCalculatedForSaggingMoment() {
        Beam beam = new Beam(UlsMomentSagging, UlsShear, SlsMomentSagging, geometry, concrete, beamReinforcement, designParameters);

        beam.calculateBendingCapacity();
        beam.calculateCracking();

        double crackWidth = beam.getCrackWidth();

        assertEquals(0.2809, Double.parseDouble(decimalFormatCracks.format(crackWidth)));
    }
    
    @Test
    void crackingIsCalculatedForHoggingMoment() {
        Beam beam = new Beam(UlsMomentHogging, UlsShear, SlsMomentHogging, geometry, concrete, beamReinforcement, designParameters);

        beam.calculateBendingCapacity();
        beam.calculateCracking();

        double crackWidth = beam.getCrackWidth();

        assertEquals(0.3131, Double.parseDouble(decimalFormatCracks.format(crackWidth)));
    }

    @Test
    void crackingIsNotCalculatedForExcessiveSpacing() {
        Beam beam = new Beam(UlsMomentHogging, UlsShear, SlsMomentHogging, geometry, concrete, beamReinforcementWithMultipleRows, designParameters);

        beam.calculateBendingCapacity();

        Exception exception = assertThrows(IllegalArgumentException.class, beam::calculateCracking);

        String errorMessage = exception.getMessage();
        String expectedMessage = UIText.INVALID_BAR_SPACING_CRACKS;

        assertEquals(expectedMessage, errorMessage);
    }

    @Test
    void crackingIsCalculatedForMinimumReinforcement() {
        Beam beam = new Beam(0, 0, 0, geometry, concrete, beamReinforcement, designParameters);

        beam.calculateBendingCapacity();
        beam.calculateCracking();

        double crackWidth = beam.getCrackWidth();

        assertEquals(0, Double.parseDouble(decimalFormatCracks.format(crackWidth)));
    }

    @Test
    void crackingIsCalculatedForAndMultipleRows() {
    
    }

    @Test
    void crackingIsCalculatedForMultipleRowsAndBarTypes() {
    
    }

    @Test
    void errorIsThrownForCrackingWhenBendingCapacityIsNotCalculated() {
        Beam beam = new Beam(UlsMomentHogging, UlsShear, SlsMomentHogging, geometry, concrete, beamReinforcementWithMultipleRows, designParameters);

        Exception exception = assertThrows(IllegalArgumentException.class, beam::calculateCracking);

        String errorMessage = exception.getMessage();
        String expectedMessage = UIText.INVALID_BENDING_CAPACITY;

        assertEquals(expectedMessage, errorMessage);
    }
}