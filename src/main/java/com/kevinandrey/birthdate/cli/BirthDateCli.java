package com.kevinandrey.birthdate.cli;

import com.kevinandrey.birthdate.BirthDate;
import com.kevinandrey.birthdate.BirthDateCalculator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Command line entry point.
 *
 * <pre>
 * java -jar birth-date.jar 20171016 1000000000
 * java -jar birth-date.jar              (asks for both values)
 * </pre>
 */
public final class BirthDateCli {

    static final int OK = 0;
    static final int INVALID_INPUT = 2;

    private static final Locale SPANISH = Locale.forLanguageTag("es-CO");
    private static final DateTimeFormatter LONG_DATE = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' uuuu", SPANISH);
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final BufferedReader in;
    private final PrintStream out;
    private final PrintStream err;

    BirthDateCli(InputStream in, PrintStream out, PrintStream err) {
        this.in = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        this.out = out;
        this.err = err;
    }

    public static void main(String[] args) {
        System.exit(new BirthDateCli(System.in, System.out, System.err).run(args));
    }

    int run(String[] args) {
        if (args.length == 1 && ("-h".equals(args[0]) || "--help".equals(args[0]))) {
            out.println("Uso: java -jar birth-date.jar <aaaammdd> <segundos>");
            return OK;
        }
        if (args.length != 0 && args.length != 2) {
            err.println("Uso: java -jar birth-date.jar <aaaammdd> <segundos>");
            return INVALID_INPUT;
        }
        try {
            String date = args.length == 2 ? args[0] : ask("Fecha de referencia (aaaammdd): ");
            String seconds = args.length == 2 ? args[1] : ask("Segundos vividos: ");
            print(BirthDateCalculator.fromElapsedSeconds(date, seconds));
            return OK;
        } catch (IllegalArgumentException e) {
            err.println("Error: " + e.getMessage());
            return INVALID_INPUT;
        }
    }

    private String ask(String prompt) {
        out.print(prompt);
        out.flush();
        try {
            String line = in.readLine();
            if (line == null) {
                throw new IllegalArgumentException("No se recibió ningún valor.");
            }
            return line;
        } catch (IOException e) {
            throw new IllegalArgumentException("No se pudo leer la entrada.", e);
        }
    }

    private void print(BirthDate result) {
        out.println("Fecha de referencia : " + LONG_DATE.format(result.referenceDate()) + ", 00:00:00");
        out.println("Segundos vividos    : " + NumberFormat.getIntegerInstance(SPANISH).format(result.elapsedSeconds()));
        out.println("Fecha de nacimiento : " + LONG_DATE.format(result.bornAt()) + ", " + TIME.format(result.bornAt()));
        out.println("Edad a esa fecha    : " + describe(result.age()));
    }

    static String describe(Period age) {
        return plural(age.getYears(), "año", "años") + ", " + plural(age.getMonths(), "mes", "meses") + " y "
                + plural(age.getDays(), "día", "días");
    }

    private static String plural(int amount, String one, String many) {
        return amount + " " + (amount == 1 ? one : many);
    }
}
