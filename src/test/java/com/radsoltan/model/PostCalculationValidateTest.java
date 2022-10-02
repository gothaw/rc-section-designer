package com.radsoltan.model;

import com.radsoltan.model.reinforcement.BeamReinforcement;
import com.radsoltan.model.reinforcement.ShearLinks;
import com.radsoltan.model.reinforcement.SlabReinforcement;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PostCalculationValidateTest {
    private static Beam validBeam;
    private static Beam invalidBeam;
    private static Slab validSlab;
    private static Slab invalidSlab;

    private static ShearLinks shearLinks;
    private static PostCalculationValidate postCalculationValidate;

    @BeforeAll
    static void beforeAll() {
        validBeam = mock(Beam.class);
        invalidBeam = mock(Beam.class);
        validSlab = mock(Slab.class);
        invalidSlab = mock(Slab.class);
        shearLinks = mock(ShearLinks.class);
        when(shearLinks.getSpacing()).thenReturn(300);
    }

    @BeforeEach
    void setUp() {
        postCalculationValidate = null;
    }

    @Test
    void shouldAddMessagesIfInvalidBeam() {
        when(invalidBeam.getRequiredCompressionReinforcement()).thenReturn(500.0);
        when(invalidBeam.getMaximumLinksSpacing()).thenReturn(250.0);

        BeamReinforcement invalidBeamReinforcement = mock(BeamReinforcement.class);
        when(invalidBeam.getReinforcement()).thenReturn(invalidBeamReinforcement);
        when(invalidBeamReinforcement.getTotalAreaOfBottomReinforcement()).thenReturn(1000.0);
        when(invalidBeamReinforcement.getTotalAreaOfTopReinforcement()).thenReturn(1200.0);
        when(invalidBeam.getMaximumReinforcement()).thenReturn(800.0);
        when(invalidBeamReinforcement.getShearLinks()).thenReturn(shearLinks);

        postCalculationValidate = new PostCalculationValidate(invalidBeam);
        List<String> validationMessages = postCalculationValidate.getValidationMessages();

        assertEquals(4, validationMessages.size());
        assertEquals("Compression reinforcement needed. Doubly reinforced section!", validationMessages.get(0));
        assertEquals("Area of top reinforcement greater than maximum allowed!", validationMessages.get(1));
        assertEquals("Area of bottom reinforcement greater than maximum allowed!", validationMessages.get(2));
        assertEquals("Shear link spacing greater than maximum allowed (0.75d)!", validationMessages.get(3));
    }

    @Test
    void shouldNotAddMessagesIfValidBeam() {
        when(validBeam.getRequiredCompressionReinforcement()).thenReturn(0.0);
        when(validBeam.getMaximumLinksSpacing()).thenReturn(300.0);

        BeamReinforcement validBeamReinforcement = mock(BeamReinforcement.class);
        when(validBeam.getReinforcement()).thenReturn(validBeamReinforcement);
        when(validBeamReinforcement.getTotalAreaOfBottomReinforcement()).thenReturn(700.0);
        when(validBeamReinforcement.getTotalAreaOfTopReinforcement()).thenReturn(800.0);
        when(validBeam.getMaximumReinforcement()).thenReturn(800.0);
        when(validBeamReinforcement.getShearLinks()).thenReturn(shearLinks);

        postCalculationValidate = new PostCalculationValidate(validBeam);
        List<String> validationMessages = postCalculationValidate.getValidationMessages();

        assertEquals(0, validationMessages.size());
    }

    @Test
    void shouldAddMessagesIfInvalidSlab() {
        SlabReinforcement invalidSlabReinforcement = mock(SlabReinforcement.class);
        when(invalidSlab.getReinforcement()).thenReturn(invalidSlabReinforcement);
        when(invalidSlabReinforcement.getTotalAreaOfBottomReinforcement()).thenReturn(1000.0);
        when(invalidSlabReinforcement.getTotalAreaOfTopReinforcement()).thenReturn(1200.0);
        when(invalidSlab.getMaximumReinforcement()).thenReturn(800.0);

        postCalculationValidate = new PostCalculationValidate(invalidSlab);
        List<String> validationMessages = postCalculationValidate.getValidationMessages();

        assertEquals(2, validationMessages.size());
        assertEquals("Area of top reinforcement greater than maximum allowed!", validationMessages.get(0));
        assertEquals("Area of bottom reinforcement greater than maximum allowed!", validationMessages.get(1));
    }

    @Test
    void shouldNotAddMessagesIfValidSlab() {
        SlabReinforcement validSlabReinforcement = mock(SlabReinforcement.class);
        when(validSlab.getReinforcement()).thenReturn(validSlabReinforcement);
        when(validSlabReinforcement.getTotalAreaOfBottomReinforcement()).thenReturn(900.0);
        when(validSlabReinforcement.getTotalAreaOfTopReinforcement()).thenReturn(700.0);
        when(validSlab.getMaximumReinforcement()).thenReturn(900.0);

        postCalculationValidate = new PostCalculationValidate(validSlab);
        List<String> validationMessages = postCalculationValidate.getValidationMessages();

        assertEquals(0, validationMessages.size());
    }
}