package lu.p2.io;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
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

public class YamlTest {

    private YAMLMapper yamlMapper;
    private Yaml yaml;

    @Before
    public void setUp() {
        yamlMapper = mock(YAMLMapper.class);
        yaml = new Yaml(yamlMapper);
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void Can_deserialise_a_yaml_object() throws IOException {

        final InputStream stream = mock(InputStream.class);
        final Class type = someThing().getClass();
        final Object expected = someThing();

        // Given
        given(yamlMapper.readValue(stream, type)).willReturn(expected);

        // When
        final Object actual = yaml.read(stream, type);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void Can_fail_to_deserialise_a_yaml_object() throws IOException {

        final InputStream stream = mock(InputStream.class);
        final Class type = someThing().getClass();
        final IOException exception = mock(IOException.class);

        // Given
        given(yamlMapper.readValue(stream, type)).willThrow(exception);

        // When
        final IllegalArgumentException actual = assertThrows(IllegalArgumentException.class, () -> yaml.read(stream, type));

        // Then
        assertThat(actual.getCause(), is(exception));
    }
}