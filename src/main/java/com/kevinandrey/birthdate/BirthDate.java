package com.kevinandrey.birthdate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

/**
 * Result of the calculation.
 *
 * @param referenceDate  day whose first second (00:00:00) is the moment of reference
 * @param elapsedSeconds seconds lived up to that moment
 * @param bornAt         exact birth date and time
 */
public record BirthDate(LocalDate referenceDate, long elapsedSeconds, LocalDateTime bornAt) {

    public LocalDate date() {
        return bornAt.toLocalDate();
    }

    /** Age in completed years, months and days on the reference date. */
    public Period age() {
        return Period.between(date(), referenceDate);
    }
}
