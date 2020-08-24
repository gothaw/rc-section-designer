package com.radsoltan.model;

import com.radsoltan.model.reinforcement.SlabReinforcement;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class ValidateSlabTest {

    private static DesignParameters designParameters;
    private static SlabReinforcement validSlabReinforcement;
    private static int minimumSlabThicknessForValidSpacing;
    private static SlabReinforcement slabReinforcementWithInvalidVerticalSpacing;
    private static SlabReinforcement slabReinforcementWithInvalidHorizontalSpacing;
    private static SlabReinforcement invalidSlabReinforcement;

    @BeforeAll
    static void beforeAll() {
        designParameters = new DesignParameters(30, 0, 35, 500, 20, 1.5, 1.15, 0.85, true, true);
        List<Integer> topDiameters = new ArrayList<>(List.of(32, 25, 32));
        List<Integer> additionalTopDiameters = new ArrayList<>(List.of(20, 40, 20));
        List<Integer> bottomDiameters = new ArrayList<>(List.of(32, 25, 32, 20));
        List<Integer> additionalBottomDiameters = new ArrayList<>(List.of(20, 0, 20, 0));
        List<Integer> validTopSpacings = new ArrayList<>(List.of(250, 250, 300));

        List<Integer> validTopVerticalSpacings = new ArrayList<>(List.of(50, 45));
        List<Integer> validBottomSpacings = new ArrayList<>(List.of(400, 400, 300, 400));
        List<Integer> validBottomVerticalSpacings = new ArrayList<>(List.of(50, 45, 50));

        /*List<Integer> invalidTopSpacings = new ArrayList<>(List.of(250, 250, 300));
        List<Integer> invalidTopVerticalSpacings = new ArrayList<>(List.of(50, 39));
        List<Integer> invalidBottomSpacings = new ArrayList<>(List.of(400, 400, 300, 400));
        List<Integer> invalidBottomVerticalSpacings = new ArrayList<>(List.of(50, 45, 50));*/

        int topReinforcementHeightSum = IntStream.range(0, topDiameters.size())
                .map(i -> Math.max(topDiameters.get(i), additionalTopDiameters.get(i)))
                .reduce(Integer::sum)
                .orElse(0);

        int bottomReinforcementHeightSum = IntStream.range(0, bottomDiameters.size())
                .map(i -> Math.max(bottomDiameters.get(i), additionalBottomDiameters.get(i)))
                .reduce(Integer::sum)
                .orElse(0);

        int distanceBetweenTopAndBottomReinforcement = IntStream.of(20, designParameters.getAggregateSize() + 5,
                topDiameters.get(topDiameters.size() - 1), additionalTopDiameters.get(additionalTopDiameters.size() - 1),
                bottomDiameters.get(bottomDiameters.size() - 1), additionalBottomDiameters.get(additionalBottomDiameters.size() - 1)).max().orElse(0);

        int validTopVerticalSpacingSum = validTopVerticalSpacings.stream().reduce(Integer::sum).orElse(0);
        int validBottomVerticalSpacingSum = validBottomVerticalSpacings.stream().reduce(Integer::sum).orElse(0);

        minimumSlabThicknessForValidSpacing = designParameters.getNominalCoverTop() + topReinforcementHeightSum + validTopVerticalSpacingSum + distanceBetweenTopAndBottomReinforcement + bottomReinforcementHeightSum + validBottomVerticalSpacingSum + designParameters.getNominalCoverBottom();

        validSlabReinforcement = new SlabReinforcement(topDiameters, additionalTopDiameters, validTopSpacings, validTopVerticalSpacings,
                bottomDiameters, additionalBottomDiameters, validBottomSpacings, validBottomVerticalSpacings);
    }

    @Test
    void noErrorMessagesForValidSlab() {
        ValidateSlab validateSlab = new ValidateSlab(minimumSlabThicknessForValidSpacing, validSlabReinforcement, designParameters);

        assertEquals(new ArrayList<>(), validateSlab.getValidationMessages());
    }

    @Test
    void errorMessageIsAddedWhenSlabThicknessIsInvalid() {
        int invalidSlabThickness = minimumSlabThicknessForValidSpacing - 10;
        ValidateSlab validateSlab = new ValidateSlab(invalidSlabThickness, validSlabReinforcement, designParameters);

        assertEquals(1, validateSlab.getValidationMessages().size());
        assertEquals("Invalid slab thickness. Minimum slab thickness: 550 mm.", validateSlab.getValidationMessages().get(0));
    }

    @Test
    void errorMessageIsAddedWhenHorizontalSlabSpacingIsInvalid() {

    }

    @Test
    void errorMessageIsAddedWhenSpacingBetweenLayersIsInvalid() {

    }
}