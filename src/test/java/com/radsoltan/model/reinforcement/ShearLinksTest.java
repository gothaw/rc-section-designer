package com.radsoltan.model.reinforcement;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShearLinksTest {
    private static ShearLinks shearLinks;

    @BeforeAll
    static void beforeAll() {
        shearLinks = new ShearLinks(500, 8, 175, 4);
    }

    @Test
    void shouldCalculateArea() {
        double area = 0.25 * Math.PI * Math.pow(shearLinks.getDiameter(), 2) * shearLinks.getLegs() * 1000 / shearLinks.getSpacing();

        assertEquals(area, shearLinks.getArea());
    }

    @Test
    void shouldGetDescriptionWithLabel() {
        String description = shearLinks.getShearLinksDescription(true);

        assertEquals("Shear links:\n" +
                "4 x \u03c68@175 mm", description);
    }

    @Test
    void shouldGetDescriptionWithoutLabels() {
        String description = shearLinks.getShearLinksDescription(false);

        assertEquals("4 x \u03c68@175 mm", description);
    }
}