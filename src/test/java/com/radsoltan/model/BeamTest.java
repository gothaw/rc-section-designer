package com.radsoltan.model;

import com.radsoltan.constants.Constants;
import com.radsoltan.model.geometry.Geometry;
import com.radsoltan.model.geometry.Rectangle;
import com.radsoltan.model.reinforcement.BeamReinforcement;
import com.radsoltan.model.reinforcement.ShearLinks;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.text.DecimalFormat;
import java.util.List;

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
        UlsMomentHogging = -400;
        SlsMomentHogging = 0.7 * UlsMomentHogging;
        UlsMomentSagging = 600;
        SlsMomentSagging = 0.7 * UlsMomentSagging;
        UlsShear = 300;
        geometry = new Geometry(new Rectangle(500, 800));
        concrete = Concrete.C32_40;
        shearLinks = new ShearLinks(500, 10, 200, 3);

        beamReinforcement = new BeamReinforcement(
                List.of(List.of(25, 25, 25)),
                List.of(),
                List.of(List.of(16, 16, 16, 16)),
                List.of(),
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

        designParameters = new DesignParameters(35, 25, 35, 500, 20,
                Constants.GAMMA_C_PERSISTENT_TRANSIENT, Constants.GAMMA_S_PERSISTENT_TRANSIENT, 0.85, true, true, 0.3);
    }

    @Test
    void bendingCapacityIsCalculatedCorrectlyForSaggingMoment() {

    }

    @Test
    void bendingCapacityIsCalculatedCorrectlyForHoggingMoment() {

    }

    @Test
    void bendingCapacityIsCalculatedCorrectlyForMinimumReinforcement() {

    }

    @Test
    void bendingCapacityIsCalculatedCorrectlyForSaggingAndMultipleRows() {

    }

    @Test
    void bendingCapacityIsCalculatedCorrectlyForSaggingAndMultipleRowsAndBarTypes() {

    }

    @Test
    void bendingCapacityIsCalculatedCorrectlyForHoggingAndMultipleRows() {

    }

    @Test
    void bendingCapacityIsCalculatedCorrectlyForHoggingAndMultipleRowsAndBarTypes() {

    }

    @Test
    void bendingCapacityIsCalculatedForDoublyReinforcedRectangularSection() {
        
    }

    @Test
    void errorIsThrownForBendingIfWrongConcreteClass() {
        
    }

    @Test
    void errorIsThrownIfExcessiveCompressiveForceAndFlangedSectionWithPNAinWeb() {
    
    }

    @Test
    void shearCapacityIsCalculatedCorrectly() {
    }

    @Test
    void shearCapacityIsCalculatedCorrectlyForMinimumShearReinforcement() {

    }

    @Test
    void errorIsThrownForExcessiveShearForce() {
    }

    @Test
    void errorIsThrownForShearIfWrongConcreteClass() {
    }

    @Test
    void crackingIsCalculatedForSaggingMoment() {
    
    }
    
    @Test
    void crackingIsCalculatedForHoggingMoment() {

    }

    @Test
    void crackingIsNotCalculatedForExcessiveSpacing() {
    
    }

    @Test
    void crackingIsCalculatedForMinimumReinforcement() {
    
    }

    @Test
    void crackingIsCalculatedForSaggingAndMultipleRows() {
    
    }

    @Test
    void crackingIsCalculatedForSaggingAndMultipleRowsAndBarTypes() {
    
    }

    @Test
    void crackingIsCalculatedForHoggingMultipleRows() {

    }

    @Test
    void crackingIsCalculatedForHoggingMultipleRowsAndBarTypes() {

    }

    @Test
    void errorIsThrownForCrackingWhenBendingCapacityIsNotCalculated() {
    
    }
}