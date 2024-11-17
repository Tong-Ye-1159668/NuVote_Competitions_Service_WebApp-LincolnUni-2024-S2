package lu.p2.holders;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static shiver.me.timbers.data.random.RandomThings.someThing;

public class GenericHolderTest {

    private GenericHolder<Object> holder;

    @Before
    public void setUp() {
        holder = new GenericHolder<>();
    }

    @Test
    public void Can_set_a_value() {

        final Object expected = someThing();

        // Given
        holder.set(expected);

        // When
        final Object actual = holder.get();

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Generic_holder_has_to_string() {

        final Object value = someThing();

        // Given
        holder.set(value);

        // When
        final String actual = holder.toString();

        // Then
        assertThat(actual, containsString(value.toString()));
    }
}