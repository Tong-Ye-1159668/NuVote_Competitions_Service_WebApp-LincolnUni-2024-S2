package lu.p2.models;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static shiver.me.timbers.data.random.RandomStrings.someNumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class CharityTest {

    @Test
    public void Can_create_a_charity() {
        // Given
        final String name = someString();
        final String description = someString();
        final String image = someString();
        final String irdNumber = someNumericString(9);
        final String bankAccount = someNumericString(18);
        final String regNumber = someString();

        // When
        final Charity actual = new Charity();
        actual.setName(name);
        actual.setDescription(description);
        actual.setImage(image);
        actual.setIrdNumber(irdNumber);
        actual.setBankAccount(bankAccount);
        actual.setRegNumber(regNumber);

        // Then
        assertThat(actual.getName(), is(name));
        assertThat(actual.getDescription(), is(description));
        assertThat(actual.getImage(), is(image));
        assertThat(actual.getIrdNumber(), is(irdNumber));
        assertThat(actual.getBankAccount(), is(bankAccount));
        assertThat(actual.getRegNumber(), is(regNumber));
        assertThat(actual.getFormattedBankAccount(), is(notNullValue()));
        assertThat(actual.getFormattedIrdNumber(), is(notNullValue()));
    }
}