package lu.p2.io;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class FilesTest {

    private static final String LINE1 = "Some test text.";
    private static final String LINE2 = "Some more test text.";

    private Files files;

    @Before
    public void setUp() {
        files = new Files();
    }

    @Test
    public void Can_convert_a_file_to_a_stream() throws IOException {
        // Given
        final String file = requireNonNull(
                Thread.currentThread().getContextClassLoader().getResource("test.txt")
        ).getPath();

        // When
        final InputStream actual = files.toStream(file);

        // Then
        assertThat(IOUtils.toString(actual, UTF_8), equalTo(LINE1 + " " + LINE2));
    }

    @Test
    public void Can_fail_to_convert_a_file_to_a_stream() {
        // Given
        final String file = someAlphanumericString(13);

        // When
        final IllegalArgumentException actual = assertThrows(IllegalArgumentException.class, () -> files.toStream(file));

        // Then
        assertThat(actual.getCause(), instanceOf(FileNotFoundException.class));
    }

    @Test
    public void Can_get_steams_from_folder() throws IOException {
        // Given
        final String file = requireNonNull(
                Thread.currentThread().getContextClassLoader().getResource("test")
        ).getPath();
        final File folder = new File(file);

        // When
        final List<InputStream> actual = files.getFiles(folder);

        // Then
        for (InputStream steam : actual) {
            assertThat(IOUtils.toString(steam, UTF_8), equalTo(LINE1 + " " + LINE2));
        }
    }

    @Test
    public void Can_fail_get_steams_from_folder() throws IOException {
        // Given
        final File folder = mock(File.class);

        final String[] filenames = new String[]{someString(), someString()};

        // When
        given(folder.list()).willReturn(filenames);
        given(folder.getAbsolutePath()).willReturn(someString());
        final IllegalArgumentException actual = assertThrows(IllegalArgumentException.class, () -> files.getFiles(folder));

        // Then
        assertThat(actual.getCause(), instanceOf(FileNotFoundException.class));
    }
}