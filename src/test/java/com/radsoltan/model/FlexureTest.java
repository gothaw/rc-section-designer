package com.radsoltan.model;

import com.radsoltan.model.geometry.Geometry;
import com.radsoltan.model.geometry.SlabStrip;
import com.radsoltan.model.reinforcement.Reinforcement;
import com.radsoltan.model.reinforcement.SlabReinforcement;
import com.radsoltan.util.Constants;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FlexureTest {

    private static Flexure flexure;
    private static Concrete concrete;
    private static double UlsMomentPositive;
    private static double UlsMomentNegative;
    private static DecimalFormat decimalFormat;
    private static Reinforcement reinforcement;
    private static DesignParameters designParameters;
    private static int yieldStrength;
    private static Geometry geometry;
    private static int widthInCompressionZonePositiveMoment;
    private static int widthInCompressionZoneNegativeMoment;
    private static int depth;

    @BeforeAll
    static void beforeAll() {
        flexure = () -> {
        };
        decimalFormat = new DecimalFormat("##.0000");
        concrete = Concrete.C32_40;
        yieldStrength = 500;
        UlsMomentPositive = 600;
        UlsMomentNegative = -500;
        geometry = new Geometry(new SlabStrip(500));
        widthInCompressionZonePositiveMoment = geometry.getWidthInCompressionZone(UlsMomentPositive);
        widthInCompressionZoneNegativeMoment = geometry.getWidthInCompressionZone(UlsMomentNegative);
        depth = geometry.getDepth();
        reinforcement = new SlabReinforcement(List.of(25), List.of(0), List.of(200), Collections.emptyList(),
                List.of(25, 16), List.of(20, 10), List.of(300, 250), List.of(50));
        designParameters = new DesignParameters(25, 0, 50, 500, 20,
                Constants.GAMMA_C_PERSISTENT_TRANSIENT, Constants.GAMMA_S_PERSISTENT_TRANSIENT, 0.85, true, true);
    }

    @Test
    void kFactorIsCalculatedCorrectlyForPositiveMoment() {
        double effectiveDepth = 415;
        double kFactor = flexure.getKFactor(UlsMomentPositive, widthInCompressionZonePositiveMoment, effectiveDepth, concrete.getCompressiveStrength());

        assertEquals(0.1089, Double.parseDouble(decimalFormat.format(kFactor)));
    }

    @Test
    void kFactorIsCalculatedCorrectlyForNegativeMoment() {
        double effectiveDepth = 434;
        double kFactor = flexure.getKFactor(UlsMomentNegative, widthInCompressionZoneNegativeMoment, effectiveDepth, concrete.getCompressiveStrength());

        assertEquals(0.083, Double.parseDouble(decimalFormat.format(kFactor)));
    }

    @Test
    void kDashFactorIsCalculatedCorrectlyIfRecommendedRatio() {
        double kDashFactor = flexure.getKDashFactor(true, 0.85);

        assertEquals(0.168, kDashFactor);
    }

    @Test
    void kDashFactorIsCalculatedCorrectlyForGivenRedistributionRatio() {
        double kDashFactor = flexure.getKDashFactor(false, 0.80);

        assertEquals(0.1548, Double.parseDouble(decimalFormat.format(kDashFactor)));
    }

    @Test
    void effectiveDepthIsCalculatedCorrectlyForPositiveMoment() {
        double effectiveDepth = flexure.getEffectiveDepth(depth, UlsMomentPositive, reinforcement, designParameters);

        assertEquals(416.7607, Double.parseDouble(decimalFormat.format(effectiveDepth)));
    }

    @Test
    void effectiveDepthIsCalculatedCorrectlyForNegativeMoment() {
        double effectiveDepth = flexure.getEffectiveDepth(depth, UlsMomentNegative, reinforcement, designParameters);

        assertEquals(462.5, Double.parseDouble(decimalFormat.format(effectiveDepth)));
    }

    @Test
    void leverArmIsCalculatedCorrectlyForSinglyReinforcedSection() {
        double effectiveDepth = flexure.getEffectiveDepth(depth, UlsMomentPositive, reinforcement, designParameters);
        double kFactor = flexure.getKFactor(UlsMomentPositive, widthInCompressionZonePositiveMoment, effectiveDepth, concrete.getCompressiveStrength());
        double kDashFactor = flexure.getKDashFactor(false, 0.85);
        double leverArm = flexure.getLeverArm(effectiveDepth, kFactor, kDashFactor);

        assertEquals(372.3178, Double.parseDouble(decimalFormat.format(leverArm)));
    }

    @Test
    void leverArmIsCalculatedCorrectlyForDoublyReinforcedSection() {
        double effectiveDepth = flexure.getEffectiveDepth(depth, UlsMomentPositive, reinforcement, designParameters);
        double kFactor = flexure.getKFactor(2.0 * UlsMomentPositive, widthInCompressionZonePositiveMoment, effectiveDepth, concrete.getCompressiveStrength());
        double kDashFactor = flexure.getKDashFactor(false, 0.85);
        double leverArm = flexure.getLeverArm(effectiveDepth, kFactor, kDashFactor);

        assertEquals(340.1843, Double.parseDouble(decimalFormat.format(leverArm)));
    }

    @Test
    void minimumReinforcementIsCalculatedCorrectlyUsingSectionNineFormulas() {
        double effectiveDepth = flexure.getEffectiveDepth(depth, UlsMomentPositive, reinforcement, designParameters);
        double minimumReinforcementArea = flexure.getMinimumReinforcement(UlsMomentPositive, concrete.getMeanAxialTensileStrength(), yieldStrength, effectiveDepth, geometry);

        assertEquals(650.1467, Double.parseDouble(decimalFormat.format(minimumReinforcementArea)));
    }

    @Test
    void minimumReinforcementIsCalculatedCorrectlyUsingSectionSevenFormulas() {
        Geometry thinnerSlab = new Geometry(new SlabStrip(300));
        double effectiveDepth = flexure.getEffectiveDepth(thinnerSlab.getDepth(), UlsMomentPositive, reinforcement, designParameters);
        double minimumReinforcementArea = flexure.getMinimumReinforcement(UlsMomentPositive, concrete.getMeanAxialTensileStrength(), yieldStrength, effectiveDepth, thinnerSlab);

        assertEquals(360, Double.parseDouble(decimalFormat.format(minimumReinforcementArea)));
    }

    @Test
    void maximumReinforcementIsCalculatedCorrectly() {
        double concreteArea = geometry.getArea() - reinforcement.getTotalAreaOfTopReinforcement() - reinforcement.getTotalAreaOfBottomReinforcement();
        double maximumReinforcement = flexure.getMaximumReinforcement(concreteArea);

        assertEquals(19749.7512, Double.parseDouble(decimalFormat.format(maximumReinforcement)));
    }

    @Test
    void depthOfPlasticNeutralAxisIsCalculatedCorrectly() {
        double effectiveDepth = flexure.getEffectiveDepth(depth, UlsMomentPositive, reinforcement, designParameters);
        double kFactor = flexure.getKFactor(UlsMomentPositive, widthInCompressionZonePositiveMoment, effectiveDepth, concrete.getCompressiveStrength());
        double kDashFactor = flexure.getKDashFactor(true, 0.85);
        double leverArm = flexure.getLeverArm(effectiveDepth, kFactor, kDashFactor);
        double depthOfPlasticNeutralAxis = flexure.getDepthOfPlasticNeutralAxis(effectiveDepth, leverArm);

        assertEquals(111.1072,Double.parseDouble(decimalFormat.format(depthOfPlasticNeutralAxis)));
    }

    @Test
    void centroidOfCompressiveReinforcementIsCorrectForPositiveMoment() {
        double centroidOfCompressiveReinforcement = flexure.getCentroidOfCompressionReinforcement(UlsMomentPositive, reinforcement, designParameters);

        assertEquals(37.5, Double.parseDouble(decimalFormat.format(centroidOfCompressiveReinforcement)));
    }

    @Test
    void centroidOfCompressiveReinforcementIsCorrectForNegativeMoment() {
        double centroidOfCompressiveReinforcement = flexure.getCentroidOfCompressionReinforcement(UlsMomentNegative, reinforcement, designParameters);

        assertEquals(83.2393, Double.parseDouble(decimalFormat.format(centroidOfCompressiveReinforcement)));
    }
}