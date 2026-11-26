package com.kevinandrey.birthdate;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

/** Parses the {@code yyyymmdd} reference date of the exercise, rejecting impossible dates such as 2023-02-29. */
public final class ReferenceDate {

    private static final DateTimeFormatter BASIC_DATE =
            DateTimeFormatter.ofPattern("uuuuMMdd").withResolverStyle(ResolverStyle.STRICT);

    private ReferenceDate() {
    }

    public static LocalDate parse(String text) {
        String value = text == null ? "" : text.strip();
        if (!value.matches("\\d{8}")) {
            throw new IllegalArgumentException("La fecha debe tener 8 dígitos en formato aaaammdd, por ejemplo 20171016.");
        }
        try {
            return LocalDate.parse(value, BASIC_DATE);
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("La fecha " + value + " no existe en el calendario.", e);
        }
    }
}
