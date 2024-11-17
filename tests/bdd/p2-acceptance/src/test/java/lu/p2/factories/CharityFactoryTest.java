package lu.p2.factories;

import lu.p2.models.Charity;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class CharityFactoryTest {

    private CharityFactory charityFactory;

    @Before
    public void setUp() {
        charityFactory = new CharityFactory();
    }

    @Test
    public void Can_create_a_charity() {
        // Given

        // When
        final Charity actual = charityFactory.create();

        // Then
        assertThat(actual.getName(), is(notNullValue()));
        assertThat(actual.getDescription(), is(notNullValue()));
        assertThat(actual.getImage(), is(notNullValue()));
        assertThat(actual.getRegNumber(), is(notNullValue()));
        assertThat(actual.getBankAccount(), is(notNullValue()));
        assertThat(actual.getFormattedBankAccount(), is(notNullValue()));
        assertThat(actual.getIrdNumber(), is(notNullValue()));
        assertThat(actual.getFormattedIrdNumber(), is(notNullValue()));

    }
}