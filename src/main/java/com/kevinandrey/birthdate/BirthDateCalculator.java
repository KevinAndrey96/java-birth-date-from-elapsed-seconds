package com.kevinandrey.birthdate;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Subtracts the seconds a person has lived from the start of a reference date.
 *
 * <p>Works on civil time without daylight saving shifts (Colombia does not observe DST), so every day has 86 400
 * seconds and leap years are handled by {@code java.time}. Seconds are a {@code long}: an {@code int} overflows
 * after about 68 years, which was the main defect of the 2017 version.
 */
public final class BirthDateCalculator {

    private BirthDateCalculator() {
    }

    public static BirthDate fromElapsedSeconds(LocalDate referenceDate, long elapsedSeconds) {
        Objects.requireNonNull(referenceDate, "referenceDate");
        if (elapsedSeconds < 0) {
            throw new IllegalArgumentException("La cantidad de segundos no puede ser negativa.");
        }
        try {
            LocalDateTime bornAt = referenceDate.atStartOfDay().minusSeconds(elapsedSeconds);
            return new BirthDate(referenceDate, elapsedSeconds, bornAt);
        } catch (DateTimeException | ArithmeticException e) {
            throw new IllegalArgumentException("La cantidad de segundos supera el rango de fechas soportado.", e);
        }
    }

    public static BirthDate fromElapsedSeconds(String referenceDate, String elapsedSeconds) {
        return fromElapsedSeconds(ReferenceDate.parse(referenceDate), parseSeconds(elapsedSeconds));
    }

    static long parseSeconds(String text) {
        String value = text == null ? "" : text.strip().replace(".", "").replace("_", "");
        if (!value.matches("\\d{1,19}")) {
            throw new IllegalArgumentException("Los segundos deben ser un número entero no negativo, por ejemplo 1000000000.");
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("La cantidad de segundos supera el rango de fechas soportado.", e);
        }
    }
}
