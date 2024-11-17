package lu.p2.models;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class CandidateTest {


    @Test
    public void Can_create_a_candidate() {
        // Given
        final String author = someString();
        final String name = someString();
        final String description = someString();
        final String image = someString();
        final String location = someString();

        // When
        final Candidate candidate = new Candidate();
        candidate.setAuthor(author);
        candidate.setName(name);
        candidate.setDescription(description);
        candidate.setImage(image);
        candidate.setLocation(location);

        // Then
        assertThat(candidate.getAuthor(), is(author));
        assertThat(candidate.getName(), is(name));
        assertThat(candidate.getDescription(), is(description));
        assertThat(candidate.getImage(), is(image));
        assertThat(candidate.getLocation(), is(location));
    }
}