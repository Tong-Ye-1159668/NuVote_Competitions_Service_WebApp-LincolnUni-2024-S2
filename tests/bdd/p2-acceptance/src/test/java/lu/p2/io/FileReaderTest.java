package lu.p2.io;

import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class FileReaderTest {

    @Test
    public void Can_read_an_sql_file() throws IOException {

        // When
        final String actual = new FileReader().toString("test.txt");

        // Then
        assertThat(actual, equalTo("Some test text. Some more test text."));
    }
}