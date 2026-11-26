package com.kevinandrey.birthdate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BirthDateCalculatorTest {

    @ParameterizedTest(name = "{0} - {1} s = {2}")
    @CsvSource({
            "20171016, 0, 2017-10-16T00:00",
            "20171016, 1, 2017-10-15T23:59:59",
            "20171016, 86400, 2017-10-15T00:00",
            "20171016, 1000000000, 1986-02-06T22:13:20",
            "20240301, 86400, 2024-02-29T00:00",
            "20230301, 86400, 2023-02-28T00:00",
            "20000101, 31536000, 1999-01-01T00:00",
            "20250101, 31622400, 2024-01-01T00:00"
    })
    void subtractsTheElapsedSecondsFromTheStartOfTheReferenceDate(String reference, long seconds, LocalDateTime expected) {
        assertEquals(expected, BirthDateCalculator.fromElapsedSeconds(ReferenceDate.parse(reference), seconds).bornAt());
    }

    @Test
    @DisplayName("people older than 68 years no longer overflow (the 2017 version used an int)")
    void supportsLifetimesBeyondTheIntRange() {
        BirthDate result = BirthDateCalculator.fromElapsedSeconds(LocalDate.of(2017, 10, 16), 3_000_000_000L);

        assertEquals(LocalDateTime.of(1922, 9, 22, 18, 40), result.bornAt());
        assertEquals(95, result.age().getYears());
    }

    @Test
    @DisplayName("keeps the seconds that do not complete a day (the 2017 version dropped them)")
    void keepsTheRemainderOfAnIncompleteDay() {
        BirthDate result = BirthDateCalculator.fromElapsedSeconds(LocalDate.of(2017, 10, 16), 90_061);

        assertEquals(LocalDateTime.of(2017, 10, 14, 22, 58, 59), result.bornAt());
    }

    @Test
    void computesTheAgeOnTheReferenceDate() {
        BirthDate result = BirthDateCalculator.fromElapsedSeconds(LocalDate.of(2017, 10, 16), 1_000_000_000L);

        assertEquals(LocalDate.of(1986, 2, 6), result.date());
        assertEquals(Period.of(31, 8, 10), result.age());
    }

    @Test
    void rejectsNegativeSeconds() {
        assertThrows(IllegalArgumentException.class, () -> BirthDateCalculator.fromElapsedSeconds(LocalDate.of(2017, 10, 16), -1));
    }

    @Test
    void rejectsLifetimesOutsideTheSupportedCalendar() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> BirthDateCalculator.fromElapsedSeconds(LocalDate.of(2017, 10, 16), Long.MAX_VALUE));

        assertTrue(error.getMessage().contains("rango"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1.000.000.000", " 1000000000 ", "1_000_000_000"})
    void acceptsThousandSeparatorsInTheSeconds(String seconds) {
        assertEquals(1_000_000_000L, BirthDateCalculator.parseSeconds(seconds));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "abc", "-5", "1e9", "99999999999999999999"})
    void rejectsSecondsThatAreNotANonNegativeInteger(String seconds) {
        assertThrows(IllegalArgumentException.class, () -> BirthDateCalculator.fromElapsedSeconds("20171016", seconds));
    }
}
