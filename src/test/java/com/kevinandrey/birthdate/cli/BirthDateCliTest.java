package com.kevinandrey.birthdate.cli;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.Period;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BirthDateCliTest {

    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private final ByteArrayOutputStream err = new ByteArrayOutputStream();

    private int run(String input, String... args) {
        BirthDateCli cli = new BirthDateCli(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
                new PrintStream(out, true, StandardCharsets.UTF_8), new PrintStream(err, true, StandardCharsets.UTF_8));
        return cli.run(args);
    }

    private String stdout() {
        return out.toString(StandardCharsets.UTF_8);
    }

    private String stderr() {
        return err.toString(StandardCharsets.UTF_8);
    }

    @Test
    void printsTheBirthDateInSpanish() {
        assertEquals(BirthDateCli.OK, run("", "20171016", "1000000000"));

        assertTrue(stdout().contains("Fecha de referencia : 16 de octubre de 2017, 00:00:00"), stdout());
        assertTrue(stdout().contains("Segundos vividos    : 1.000.000.000"), stdout());
        assertTrue(stdout().contains("Fecha de nacimiento : 6 de febrero de 1986, 22:13:20"), stdout());
        assertTrue(stdout().contains("Edad a esa fecha    : 31 años, 8 meses y 10 días"), stdout());
    }

    @Test
    void asksForTheValuesWhenNoArgumentsAreGiven() {
        assertEquals(BirthDateCli.OK, run("20240301\n86400\n"));

        assertTrue(stdout().contains("Fecha de referencia (aaaammdd): "));
        assertTrue(stdout().contains("Fecha de nacimiento : 29 de febrero de 2024, 00:00:00"), stdout());
    }

    @Test
    void reportsInvalidInputWithExitCodeTwo() {
        assertEquals(BirthDateCli.INVALID_INPUT, run("", "20170231", "10"));
        assertTrue(stderr().contains("no existe en el calendario"), stderr());

        assertEquals(BirthDateCli.INVALID_INPUT, run("", "20171016"));
        assertTrue(stderr().contains("Uso:"));

        assertEquals(BirthDateCli.INVALID_INPUT, run("20171016\n"));
        assertTrue(stderr().contains("No se recibió ningún valor."));
    }

    @Test
    void showsHelp() {
        assertEquals(BirthDateCli.OK, run("", "--help"));
        assertTrue(stdout().startsWith("Uso:"));
    }

    @Test
    void describesTheAgeWithSingularAndPluralUnits() {
        assertEquals("1 año, 1 mes y 1 día", BirthDateCli.describe(Period.of(1, 1, 1)));
        assertEquals("0 años, 2 meses y 0 días", BirthDateCli.describe(Period.of(0, 2, 0)));
    }
}
