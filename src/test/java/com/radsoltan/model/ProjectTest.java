package com.radsoltan.model;

import com.radsoltan.constants.Constants;
import com.radsoltan.model.geometry.Geometry;
import com.radsoltan.model.geometry.Rectangle;
import com.radsoltan.model.geometry.SlabStrip;
import com.radsoltan.model.reinforcement.BeamReinforcement;
import com.radsoltan.model.reinforcement.ShearLinks;
import com.radsoltan.model.reinforcement.SlabReinforcement;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProjectTest {

    private static Project project;

    @BeforeEach
    void setUp() {
        project.resetResults();
    }

    @BeforeAll
    static void beforeAll() {
        project = Project.getInstance();
    }

    @Test
    void shouldCalculateSlabProject() {
        project.setName("Slab project");
        project.setId("123abc");
        project.setDescription("Test slab project");
        project.setAuthor("John Doe");
        // Setting analysis forces
        project.setUlsMoment("150");
        project.setSlsMoment("80");
        project.setUlsShear(null);
        // Setting element type
        project.setElementType("slab");
        // Setting geometry
        project.setGeometry(new Geometry(new SlabStrip(300)));
        // Setting reinforcement
        project.setReinforcement(new SlabReinforcement(
                List.of(25),
                List.of(0),
                List.of(200),
                Collections.emptyList(),
                List.of(32),
                List.of(0),
                List.of(200),
                Collections.emptyList()
        ));
        // Setting design parameters
        project.setDesignParameters(new DesignParameters(
                25,
                0,
                35,
                500,
                20,
                Constants.GAMMA_C_PERSISTENT_TRANSIENT,
                Constants.GAMMA_S_PERSISTENT_TRANSIENT,
                0.85,
                true,
                true,
                0.3
        ));
        // Setting concrete
        project.setConcrete(Concrete.C32_40);

        project.calculate();

        assertNotEquals(0, project.getFlexureCapacity());
        assertNotNull(project.getFlexureCapacityCheckMessage());
        assertNotNull(project.getFlexureResultsAdditionalMessage());
        assertFalse(project.getIsFlexureError());

        assertEquals(0, project.getRequiredShearReinforcement());
        assertNull(project.getShearCapacityCheckMessage());
        assertNull(project.getShearResultsAdditionalMessage());
        assertFalse(project.getIsShearError());

        assertNotEquals(0, project.getCrackWidth());
        assertNotEquals(0, project.getCrackWidthLimit());
        assertNotNull(project.getCrackingCheckMessage());
        assertNotNull(project.getCrackingResultsAdditionalMessage());
        assertFalse(project.getIsCrackingError());
    }

    @Test
    void shouldCalculateBeamProject() {
        project.setName("Beam project");
        project.setId("456efg");
        project.setDescription("Test beam project");
        project.setAuthor("Jane Doe");
        // Setting analysis forces
        project.setUlsMoment("180");
        project.setSlsMoment("90");
        project.setUlsShear("150");
        // Setting element type
        project.setElementType("beam");
        // Setting geometry
        project.setGeometry(new Geometry(new Rectangle(300, 650)));
        // Setting reinforcement
        project.setReinforcement(new BeamReinforcement(
                List.of(List.of(25, 25, 25)),
                Collections.emptyList(),
                List.of(List.of(20, 20, 20, 20)),
                Collections.emptyList(),
                new ShearLinks(500, 8, 200, 2)
        ));
        // Setting design parameters
        project.setDesignParameters(new DesignParameters(
                35,
                25,
                35,
                500,
                20,
                Constants.GAMMA_C_PERSISTENT_TRANSIENT,
                Constants.GAMMA_S_PERSISTENT_TRANSIENT,
                0.85,
                true,
                true,
                0.3
        ));
        // Setting concrete
        project.setConcrete(Concrete.C32_40);

        project.calculate();

        assertNotEquals(0, project.getFlexureCapacity());
        assertNotNull(project.getFlexureCapacityCheckMessage());
        assertNotNull(project.getFlexureResultsAdditionalMessage());
        assertFalse(project.getIsFlexureError());

        assertNotEquals(0, project.getRequiredShearReinforcement());
        assertNotNull(project.getShearCapacityCheckMessage());
        assertNotNull(project.getShearResultsAdditionalMessage());
        assertFalse(project.getIsShearError());

        assertNotEquals(0, project.getCrackWidth());
        assertNotEquals(0, project.getCrackWidthLimit());
        assertNotNull(project.getCrackingCheckMessage());
        assertNotNull(project.getCrackingResultsAdditionalMessage());
        assertFalse(project.getIsCrackingError());
    }

    @Test
    void shouldResetProjectResults() {
        // Setting analysis forces
        project.setUlsMoment("180");
        project.setSlsMoment("90");
        project.setUlsShear("150");
        // Setting element type
        project.setElementType("beam");
        // Setting geometry
        project.setGeometry(new Geometry(new Rectangle(300, 650)));
        // Setting reinforcement
        project.setReinforcement(new BeamReinforcement(
                List.of(List.of(25, 25, 25)),
                Collections.emptyList(),
                List.of(List.of(20, 20, 20, 20)),
                Collections.emptyList(),
                new ShearLinks(500, 8, 200, 2)
        ));
        // Setting design parameters
        project.setDesignParameters(new DesignParameters(
                35,
                25,
                35,
                500,
                20,
                Constants.GAMMA_C_PERSISTENT_TRANSIENT,
                Constants.GAMMA_S_PERSISTENT_TRANSIENT,
                0.85,
                true,
                true,
                0.3
        ));
        // Setting concrete
        project.setConcrete(Concrete.C32_40);
        project.calculate();
        project.resetResults();

        assertEquals(0, project.getFlexureCapacity());
        assertNull(project.getFlexureCapacityCheckMessage());
        assertNull(project.getFlexureResultsAdditionalMessage());
        assertFalse(project.getIsFlexureError());

        assertEquals(0, project.getRequiredShearReinforcement());
        assertNull(project.getShearCapacityCheckMessage());
        assertNull(project.getShearResultsAdditionalMessage());
        assertFalse(project.getIsShearError());

        assertEquals(0, project.getCrackWidth());
        assertEquals(0, project.getCrackWidthLimit());
        assertNull(project.getCrackingCheckMessage());
        assertNull(project.getCrackingResultsAdditionalMessage());
        assertFalse(project.getIsCrackingError());
    }
}