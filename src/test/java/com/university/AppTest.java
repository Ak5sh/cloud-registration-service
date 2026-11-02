package com.university;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AppTest {

    @Test
    @DisplayName("Boundary Test: 4 courses permits registering the 5th")
    void testUnderLimitAllowsRegistration() {
        assertTrue(App.checkRegistration(4), "Fifth course registration must be permitted.");
    }

    @Test
    @DisplayName("Boundary Test: 5 courses rejects registering a 6th")
    void testAtLimitRejectsRegistration() {
        assertFalse(App.checkRegistration(5), "Sixth course registration must be blocked.");
    }
}