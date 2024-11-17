package lu.p2.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static shiver.me.timbers.data.random.RandomThings.someThing;

public class JsonTest {

    private ObjectMapper objectMapper;
    private Json json;

    @Before
    public void setUp() {
        objectMapper = mock(ObjectMapper.class);
        json = new Json(objectMapper);
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void Can_deserialise_a_json_object() throws IOException {

        final InputStream stream = mock(InputStream.class);
        final Class type = someThing().getClass();
        final Object expected = someThing();

        // Given
        given(objectMapper.readValue(stream, type)).willReturn(expected);

        // When
        final Object actual = json.read(stream, type);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void Can_fail_to_deserialise_a_json_object() throws IOException {

        final InputStream stream = mock(InputStream.class);
        final Class type = someThing().getClass();
        final IOException exception = mock(IOException.class);

        // Given
        given(objectMapper.readValue(stream, type)).willThrow(exception);

        // When
        final IllegalArgumentException actual = assertThrows(IllegalArgumentException.class, () -> json.read(stream, type));

        // Then
        assertThat(actual.getCause(), is(exception));
    }
}