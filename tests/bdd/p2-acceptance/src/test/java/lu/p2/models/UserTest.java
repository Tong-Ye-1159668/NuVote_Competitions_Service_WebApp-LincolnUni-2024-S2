package lu.p2.models;

import org.junit.Test;

import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static shiver.me.timbers.data.random.RandomIntegers.somePositiveInteger;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class UserTest {

    @Test
    public void Can_create_an_user() {

        // Given
        final Integer userId = somePositiveInteger();
        final String username = someString();
        final String password = someString();
        final String firstName = someString();
        final String lastName = someString();
        final String email = someString();
        final String description = someString();
        final String location = someString();

        // When
        final User actual = new User(userId, username, password, firstName, lastName, email, description, location);

        // Then
        assertThat(actual.getUserId(), is(userId));
        assertThat(actual.getUsername(), is(username));
        assertThat(actual.getPassword(), is(password));
        assertThat(actual.getFirstName(), is(firstName));
        assertThat(actual.getLastName(), is(lastName));
        assertThat(actual.getEmail(), is(email));
        assertThat(actual.getDescription(), is(description));
        assertThat(actual.getLocation(), is(location));
        assertThat(actual.toString(), is(format("User{userId=%s, username='%s', password='%s', firstName='%s', lastName='%s', email='%s', description='%s', location='%s'}", userId, username, password, firstName, lastName, email, description, location)));
    }

    @Test
    public void Can_create_an_user_with_setter() {

        // Given
        final Integer userId = somePositiveInteger();
        final String username = someString();
        final String password = someString();
        final String firstName = someString();
        final String lastName = someString();
        final String email = someString();
        final String description = someString();
        final String location = someString();

        // When
        final User actual = new User();
        actual.setUserId(userId);
        actual.setUsername(username);
        actual.setPassword(password);
        actual.setFirstName(firstName);
        actual.setLastName(lastName);
        actual.setEmail(email);
        actual.setDescription(description);
        actual.setLocation(location);

        // Then
        assertThat(actual.getUserId(), is(userId));
        assertThat(actual.getUsername(), is(username));
        assertThat(actual.getPassword(), is(password));
        assertThat(actual.getFirstName(), is(firstName));
        assertThat(actual.getLastName(), is(lastName));
        assertThat(actual.getEmail(), is(email));
        assertThat(actual.getDescription(), is(description));
        assertThat(actual.getLocation(), is(location));
        assertThat(actual.toString(), is(format("User{userId=%s, username='%s', password='%s', firstName='%s', lastName='%s', email='%s', description='%s', location='%s'}", userId, username, password, firstName, lastName, email, description, location)));
    }

}