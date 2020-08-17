package com.radsoltan.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilityTest {

    @Test
    void lowerCaseStringIsCapitalized() {
        String string = "hello world";
        String capitalizedString = Utility.capitalize(string);
        assertEquals("Hello world", capitalizedString);
    }

    @Test
    void upperCaseStringIsCapitalized() {
        String string = "HELLO WORLD";
        String capitalizedString = Utility.capitalize(string);
        assertEquals("Hello world", capitalizedString);
    }
}