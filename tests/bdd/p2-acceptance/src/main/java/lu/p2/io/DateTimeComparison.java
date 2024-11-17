package lu.p2.io;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Component
public class DateTimeComparison {

    public boolean isLessThanSomeMinutes(final String datetimeStr, final int minutes) {

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss a");


        // Parse the date string to LocalDateTime in NZST (New Zealand Standard Time)
        final LocalDateTime dateTimeNZST = LocalDateTime.parse(datetimeStr, formatter)
                .atZone(ZoneId.of("Pacific/Auckland"))
                .toLocalDateTime();

        // Get the current time in NZST
        final LocalDateTime nowNZST = LocalDateTime.now(ZoneId.of("Pacific/Auckland"));

        final long minutesDifference = ChronoUnit.MINUTES.between(nowNZST, dateTimeNZST);

        return minutesDifference >= 0 && minutesDifference < minutes;
    }

    public boolean checkIfToday(final String dateString) {
        final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        final LocalDate inputDate = LocalDate.parse(dateString, dateFormatter);
        final LocalDate today = LocalDate.now(ZoneId.of("Pacific/Auckland"));
        return inputDate.isEqual(today);
    }

    public String getCurrentDateTime() {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss a");

        // Get the current time in NZST
        return LocalDateTime.now(ZoneId.of("Pacific/Auckland")).format(formatter);
    }
}
