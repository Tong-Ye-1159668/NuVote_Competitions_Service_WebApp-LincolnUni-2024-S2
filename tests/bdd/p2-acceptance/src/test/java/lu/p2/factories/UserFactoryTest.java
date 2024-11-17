package lu.p2.factories;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import lu.p2.io.Csv;
import lu.p2.models.User;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class UserFactoryTest {


    private UserFactory userFactory;

    @Before
    public void setUp() {
        userFactory = new UserFactory(new Csv(new CsvMapper()));
    }

    @Test
    public void Can_create_a_user() {
        // Given

        // When
        final User actual = userFactory.createAUser();

        // Then
        assertThat(actual.getUsername(), is(notNullValue()));
        assertThat(actual.getEmail(), is(notNullValue()));
        assertThat(actual.getPassword(), is(notNullValue()));
    }

    @Test
    public void Can_read_a_user_from_users_file() {


        // Given


        // When
        final User actual = userFactory.getAUser();

        // Then
        assertThat(actual.getUsername(), is(notNullValue()));
        assertThat(actual.getPassword(), is(notNullValue()));
    }

    @Test
    public void Can_read_a_user_from_power_users_file() {


        // Given


        // When
        final User actual = userFactory.getAnAdminUser();

        // Then
        assertThat(actual.getUsername(), is(notNullValue()));
        assertThat(actual.getPassword(), is(notNullValue()));
    }

    @Test
    public void Can_generate_new_profile_details() {

        // Given
        final User user = new User();

        // When
        final User actual = userFactory.createNewProfileDetails(user);

        // Then
        assertThat(actual.getFirstName(), is(notNullValue()));
        assertThat(actual.getLastName(), is(notNullValue()));
        assertThat(actual.getDescription(), is(notNullValue()));

    }

    @Test
    public void Can_delete_profile_details() {

        // Given
        final User user = new User();

        // When
        final User actual = userFactory.deleteProfileDetails(user);

        // Then
        assertThat(actual.getFirstName(), is(""));
        assertThat(actual.getLastName(), is(""));
        assertThat(actual.getDescription(), is(""));
    }

    @Test
    public void Can_create_a_user_with_username() {
        // Given
        final String username = someString();

        // When
        final User actual = userFactory.createAUser(username);

        // Then
        assertThat(actual.getUsername(), is(username));
        assertThat(actual.getPassword(), is(notNullValue()));
    }

    @Test
    public void Can_create_a_user_with_username_and_email() {
        // Given
        final String username = someString();
        final String email = someString();

        // When
        final User actual = userFactory.createAUser(username, email);

        // Then
        assertThat(actual.getUsername(), is(username));
        assertThat(actual.getPassword(), is(notNullValue()));
        assertThat(actual.getEmail(), is(email));
    }
}