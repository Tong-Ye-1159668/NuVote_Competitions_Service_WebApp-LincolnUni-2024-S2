package lu.p2.io;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;

public class DateTimeComparisonTest {

    private DateTimeComparison dateTimeComparison;
    final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss a");
    final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Before
    public void setUp() {
        dateTimeComparison = new DateTimeComparison();
    }

    @Test
    public void Can_compare_datetime_string_less_than_x() {
        final LocalDateTime nowNZST = LocalDateTime.now(ZoneId.of("Pacific/Auckland"));
        final String formatted = nowNZST.format(dateTimeFormatter);

        // Given

        // When
        final boolean actual = dateTimeComparison.isLessThanSomeMinutes(formatted, 2);

        // Then
        assertThat(actual, is(TRUE));
    }

    @Test
    public void Can_compare_datetime_string_less_than_x_and_greater_than_0() {
        final LocalDateTime nowNZST = LocalDateTime.now(ZoneId.of("Pacific/Auckland")).minusMinutes(-1);
        final String formatted = nowNZST.format(dateTimeFormatter);

        // Given

        // When
        final boolean actual = dateTimeComparison.isLessThanSomeMinutes(formatted, 2);

        // Then
        assertThat(actual, is(TRUE));
    }

    @Test
    public void Can_compare_datetime_string_great_than_x() {
        final LocalDateTime nowNZST = LocalDateTime.now(ZoneId.of("Pacific/Auckland")).minusMinutes(-4);
        final String formatted = nowNZST.format(dateTimeFormatter);

        // Given

        // When
        final boolean actual = dateTimeComparison.isLessThanSomeMinutes(formatted, 2);

        // Then
        assertThat(actual, is(FALSE));
    }

    @Test
    public void Can_compare_datetime_string_greater_than_x() {
        final LocalDateTime nowNZST = LocalDateTime.now(ZoneId.of("Pacific/Auckland")).minusMinutes(4);
        final String formatted = nowNZST.format(dateTimeFormatter);

        // Given

        // When
        final boolean actual = dateTimeComparison.isLessThanSomeMinutes(formatted, 2);

        // Then
        assertThat(actual, is(FALSE));
    }

    @Test
    public void Can_check_dateString_is_today() {
        // Given
        final LocalDate today = LocalDate.now(ZoneId.of("Pacific/Auckland"));
        final String formatted = today.format(dateFormatter);
        // When
        final boolean actual = dateTimeComparison.checkIfToday(formatted);

        // Then
        assertThat(actual, is(TRUE));
    }

    @Test
    public void Can_check_dateString_is_not_today() {
        // Given
        final LocalDate today = LocalDate.now(ZoneId.of("Pacific/Auckland")).plusDays(1);
        final String formatted = today.format(dateFormatter);
        // When
        final boolean actual = dateTimeComparison.checkIfToday(formatted);

        // Then
        assertThat(actual, is(FALSE));
    }

    @Test
    public void Can_get_current_formatted_datetime_string() {
        // Given

        // When
        final String currentDateTime = dateTimeComparison.getCurrentDateTime();

        // Then
        assertThat(currentDateTime, is(notNullValue()));
    }
}