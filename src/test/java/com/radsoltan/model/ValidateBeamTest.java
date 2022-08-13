package com.radsoltan.model;

import com.radsoltan.model.geometry.Geometry;
import com.radsoltan.model.geometry.Rectangle;
import com.radsoltan.model.reinforcement.BeamReinforcement;
import com.radsoltan.model.reinforcement.ShearLinks;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ValidateBeamTest {

    private static Geometry validGeometry;
    private static Geometry invalidGeometry;
    private static DesignParameters designParameters;
    private static ShearLinks shearLinks;
    private static BeamReinforcement validBeamReinforcement;
    private static int minimumBeamDepthForValidSpacing;
    private static BeamReinforcement beamReinforcementWithInvalidHorizontalSpacing;
    private static BeamReinforcement beamReinforcementWithInvalidVerticalSpacing;
    private static BeamReinforcement invalidBeamReinforcement;

    @BeforeAll
    static void beforeAll() {
        designParameters = new DesignParameters(30, 25, 35, 500, 20, 1.5, 1.15, 0.85, true, true, 0.3);
        shearLinks = new ShearLinks(500, 8, 200, 3);

        List<List<Integer>> validTopDiameters = List.of(
                List.of(20, 25, 25, 20),
                List.of(16, 16),
                List.of(8, 8)
        );
        List<List<Integer>> validBottomDiameters = List.of(
                List.of(32, 32, 32, 32),
                List.of(16, 16),
                List.of(8, 8),
                List.of(6, 6, 6, 6, 6)
        );
        List<Integer> validTopVerticalSpacings = List.of(50, 30);
        List<Integer> validBottomVerticalSpacings = List.of(45, 40, 25);

        List<List<Integer>> invalidTopDiameters = List.of(
                List.of(20, 25, 20, 25, 20, 20, 25, 20, 25, 20),
                List.of(16, 8, 8, 16),
                List.of(8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8)
        );
        List<List<Integer>> invalidBottomDiameters = List.of(
                List.of(32, 32, 32, 32, 32),
                List.of(16, 12, 12, 12, 12, 12, 12, 12, 12, 12, 16),
                List.of(8, 8),
                List.of(6, 6)
        );
        List<Integer> invalidTopVerticalSpacings = List.of(50, 19);
        List<Integer> invalidBottomVerticalSpacings = List.of(45, 40, 12);

        int topDiametersHeight = validTopDiameters
                .stream()
                .map(Collections::max)
                .collect(Collectors.toList())
                .stream()
                .reduce(Integer::sum)
                .orElse(0);

        int bottomDiametersHeight = validBottomDiameters
                .stream()
                .map(Collections::max)
                .collect(Collectors.toList())
                .stream()
                .reduce(Integer::sum)
                .orElse(0);

        int topVerticalSpacingsSum = validTopVerticalSpacings.stream().reduce(Integer::sum).orElse(0);
        int bottomVerticalSpacingsSum = validBottomVerticalSpacings.stream().reduce(Integer::sum).orElse(0);

        int distanceBetweenTopAndBottomRows = Collections.max(List.of(
                20,
                designParameters.getAggregateSize() + 5,
                Collections.max(validTopDiameters.get(validTopDiameters.size() - 1)),
                Collections.max(validBottomDiameters.get(validBottomDiameters.size() - 1))
        ));

        validGeometry = new Geometry(new Rectangle(300, 800));
        invalidGeometry = new Geometry(new Rectangle(300, 500));

        minimumBeamDepthForValidSpacing = designParameters.getNominalCoverTop() +
                topDiametersHeight +
                topVerticalSpacingsSum +
                distanceBetweenTopAndBottomRows +
                bottomDiametersHeight +
                bottomVerticalSpacingsSum +
                designParameters.getNominalCoverBottom() +
                2 * shearLinks.getDiameter();

        validBeamReinforcement = new BeamReinforcement(
                validTopDiameters,
                validTopVerticalSpacings,
                validBottomDiameters,
                validBottomVerticalSpacings,
                shearLinks
        );

        beamReinforcementWithInvalidVerticalSpacing = new BeamReinforcement(
                validTopDiameters,
                invalidTopVerticalSpacings,
                validBottomDiameters,
                invalidBottomVerticalSpacings,
                shearLinks
        );

        beamReinforcementWithInvalidHorizontalSpacing = new BeamReinforcement(
                invalidTopDiameters,
                validTopVerticalSpacings,
                invalidBottomDiameters,
                validBottomVerticalSpacings,
                shearLinks
        );

        invalidBeamReinforcement = new BeamReinforcement(
                invalidTopDiameters,
                invalidTopVerticalSpacings,
                invalidBottomDiameters,
                invalidBottomVerticalSpacings,
                shearLinks
        );
    }

    @Test
    void noErrorMessagesForValidBeam() {
        ValidateBeam validateBeam = new ValidateBeam(validGeometry, validBeamReinforcement, designParameters);

        assertEquals(new ArrayList<>(), validateBeam.getValidationMessages());
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