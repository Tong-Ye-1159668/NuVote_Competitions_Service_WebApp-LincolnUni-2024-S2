package lu.p2.io;

import com.fasterxml.jackson.core.FormatSchema;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static shiver.me.timbers.data.random.RandomThings.someThing;

public class CsvTest {

    private CsvMapper csvMapper;
    private Csv csv;

    @Before
    public void setUp() {
        csvMapper = mock(CsvMapper.class, RETURNS_DEEP_STUBS);
        csv = new Csv(csvMapper);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void Can_read_a_csv() throws IOException {

        final InputStream stream = mock(InputStream.class);
        final Class<?> type = someThing().getClass();

        final MappingIterator<Object> iterator = mock(MappingIterator.class);
        final List<Object> expected = mock(List.class);

        // Given
        given(csvMapper.readerFor(type).with(any(FormatSchema.class)).readValues(stream)).willReturn(iterator);
        given(iterator.readAll()).willReturn(expected);

        // When
        final List<?> actual = csv.read(stream, type);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_fail_read_a_csv() throws IOException {

        final InputStream stream = mock(InputStream.class);
        final Class<?> type = someThing().getClass();
        final IOException exception = mock(IOException.class);

        // Given
        given(csvMapper.readerFor(type).with(any(FormatSchema.class)).readValues(stream)).willThrow(exception);

        // When
        final IllegalArgumentException actual = assertThrows(IllegalArgumentException.class, () -> csv.read(stream, type));

        // Then
        assertThat(actual.getCause(), is(exception));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void Can_read_multi_csv() throws IOException {
        final InputStream stream = mock(InputStream.class);
        final List<InputStream> streams = asList(stream, stream, stream);
        final Class<?> type = someThing().getClass();

        final MappingIterator<Object> iterator = mock(MappingIterator.class);
        final List<Object> dataList = asList(mock(Object.class), mock(Object.class), mock(Object.class));

        List<Object> expected = new ArrayList<>();
        expected.addAll(dataList);
        expected.addAll(dataList);
        expected.addAll(dataList);

        // Given
        given(csvMapper.readerFor(type).with(any(FormatSchema.class)).readValues(stream)).willReturn(iterator);
        given(iterator.readAll()).willReturn(dataList);

        // When
        final List<?> actual = csv.read(streams, type);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_fail_read_multi_csv() throws IOException {

        final InputStream stream = mock(InputStream.class);
        final List<InputStream> streams = asList(stream, stream, stream);

        final Class<?> type = someThing().getClass();
        final IOException exception = mock(IOException.class);

        // Given
        given(csvMapper.readerFor(type).with(any(FormatSchema.class)).readValues(stream)).willThrow(exception);

        // When
        final IllegalArgumentException actual = assertThrows(IllegalArgumentException.class, () -> csv.read(streams, type));

        // Then
        assertThat(actual.getCause(), is(exception));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void Can_read_a_csv_generic() throws IOException {
        final InputStream stream = mock(InputStream.class);
        final MappingIterator<Object> iterator = mock(MappingIterator.class);

        final List<Object> expected = mock(List.class);

        // Given
        given(csvMapper.readerFor(Map.class).with(any(FormatSchema.class)).readValues(stream)).willReturn(iterator);
        given(iterator.readAll()).willReturn(expected);

        // When
        List<Map<String, String>> actual = csv.readGeneric(stream);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_fail_read_a_csv_generic() throws IOException {

        final InputStream stream = mock(InputStream.class);

        final IOException exception = mock(IOException.class);

        // Given
        given(csvMapper.readerFor(Map.class).with(any(FormatSchema.class)).readValues(stream)).willThrow(exception);

        // When
        final IllegalArgumentException actual = assertThrows(IllegalArgumentException.class, () -> csv.readGeneric(stream));

        // Then
        assertThat(actual.getCause(), is(exception));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void Can_read_a_csv_header() throws IOException {
        final InputStream stream = mock(InputStream.class);
        final MappingIterator<Object> iterator = mock(MappingIterator.class);
        final Map<String, String> headersMap = mock(Map.class);

        final Set<String> expected = mock(Set.class);

        // Given
        given(csvMapper.readerFor(Map.class).with(any(FormatSchema.class)).readValues(stream)).willReturn(iterator);
        given(iterator.next()).willReturn(headersMap);
        given(headersMap.keySet()).willReturn(expected);

        // When
        Set<String> actual = csv.readHeaders(stream);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_fail_read_a_csv_header() throws IOException {

        final InputStream stream = mock(InputStream.class);

        final IOException exception = mock(IOException.class);

        // Given
        given(csvMapper.readerFor(Map.class).with(any(FormatSchema.class)).readValues(stream)).willThrow(exception);

        // When
        final IllegalArgumentException actual = assertThrows(IllegalArgumentException.class, () -> csv.readHeaders(stream));

        // Then
        assertThat(actual.getCause(), is(exception));
    }

}