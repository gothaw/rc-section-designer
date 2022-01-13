package com.radsoltan.model.geometry;

import org.junit.jupiter.api.BeforeAll;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FlangedTest {

    private static Flanged flanged;

    @BeforeAll
    static void beforeAll() {
        flanged = mock(Flanged.class);
    }
}