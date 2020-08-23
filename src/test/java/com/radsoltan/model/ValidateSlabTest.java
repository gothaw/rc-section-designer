package com.radsoltan.model;

import com.radsoltan.model.reinforcement.SlabReinforcement;
import com.radsoltan.util.Constants;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class ValidateSlabTest {

    private static DesignParameters designParameters;

    @BeforeAll
    static void beforeAll() {
        designParameters = new DesignParameters(30, 0, 35, 500, 20, 1.5, 1.15, 0.85, true, true);
    }

    @Test
    void noErrorMessagesForValidSlab() {
        List<Integer> topDiameters = new ArrayList<>(List.of(32, 25, 32));
        List<Integer> additionalTopDiameters = new ArrayList<>(List.of(20, 40, 20));
        List<Integer> topSpacings = new ArrayList<>(List.of(250, 250, 300));
        List<Integer> topVerticalSpacings = new ArrayList<>(List.of(50, 45, 50));
        List<Integer> bottomDiameters = new ArrayList<>(List.of(32, 25, 32, 20));
        List<Integer> additionalBottomDiameters = new ArrayList<>(List.of(20, 0, 20, 0));
        List<Integer> bottomSpacings = new ArrayList<>(List.of(400, 400, 300, 400));
        List<Integer> bottomVerticalSpacings = new ArrayList<>(List.of(50, 45, 50));
        SlabReinforcement slabReinforcement = new SlabReinforcement(topDiameters, additionalTopDiameters, topSpacings, topVerticalSpacings,
                bottomDiameters, additionalBottomDiameters, bottomSpacings, bottomVerticalSpacings);

        int topReinforcementHeightSum = IntStream.range(0, topDiameters.size())
                .map(i -> Math.max(topDiameters.get(i), additionalTopDiameters.get(i)))
                .reduce(Integer::sum)
                .orElse(0);

        int topVerticalSpacingSum = topVerticalSpacings.stream().reduce(Integer::sum).orElse(0);

        int bottomReinforcementHeightSum = IntStream.range(0, bottomDiameters.size())
                .map(i -> Math.max(bottomDiameters.get(i), additionalBottomDiameters.get(i)))
                .reduce(Integer::sum)
                .orElse(0);

        int bottomVerticalSpacingSum = bottomVerticalSpacings.stream().reduce(Integer::sum).orElse(0);

        int distanceBetweenTopAndBottomReinforcement = IntStream.of(20, designParameters.getAggregateSize() + 5,
                topDiameters.get(topDiameters.size() - 1), additionalTopDiameters.get(additionalTopDiameters.size() - 1),
                bottomDiameters.get(bottomDiameters.size() - 1), additionalBottomDiameters.get(additionalBottomDiameters.size() - 1)).max().orElse(0);

        int thickness = designParameters.getNominalCoverTop() + topReinforcementHeightSum + topVerticalSpacingSum + distanceBetweenTopAndBottomReinforcement + bottomReinforcementHeightSum + bottomVerticalSpacingSum + designParameters.getNominalCoverBottom();

        ValidateSlab validateSlab = new ValidateSlab(thickness, slabReinforcement, designParameters);

        assertEquals(validateSlab.getValidationMessages(), new ArrayList<>());
    }

    @Test
    void errorMessageIsAddedWhenSlabThicknessIsInvalid() {

    }

    @Test
    void errorMessageIsAddedWhenHorizontalSlabSpacingIsInvalid() {

    }

    @Test
    void errorMessageIsAddedWhenSpacingBetweenLayersIsInvalid() {

    }
}