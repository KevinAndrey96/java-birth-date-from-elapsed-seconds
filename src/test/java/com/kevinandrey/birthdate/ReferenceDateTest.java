package com.kevinandrey.birthdate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReferenceDateTest {

    @Test
    void parsesBasicIsoDates() {
        assertEquals(LocalDate.of(2017, 10, 16), ReferenceDate.parse(" 20171016 "));
        assertEquals(LocalDate.of(2024, 2, 29), ReferenceDate.parse("20240229"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"20230229", "20170231", "20171300", "20171000", "20170100", "20170132"})
    void rejectsDatesThatDoNotExist(String text) {
        assertThrows(IllegalArgumentException.class, () -> ReferenceDate.parse(text));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "2017-10-16", "2017101", "201710161", "abcdefgh"})
    void rejectsAnythingButEightDigits(String text) {
        assertThrows(IllegalArgumentException.class, () -> ReferenceDate.parse(text));
    }
}
