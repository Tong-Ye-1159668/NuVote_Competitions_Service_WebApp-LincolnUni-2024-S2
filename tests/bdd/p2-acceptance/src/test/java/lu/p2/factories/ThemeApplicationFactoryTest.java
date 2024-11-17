package lu.p2.factories;

import lu.p2.models.ThemeApplication;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class ThemeApplicationFactoryTest {


    private ThemeApplicationFactory themeApplicationFactory;

    @Before
    public void setUp() {
        themeApplicationFactory = new ThemeApplicationFactory();
    }

    @Test
    public void Can_create_a_theme_application() {
        // Given

        // When
        final ThemeApplication actual = themeApplicationFactory.createAnApplication();

        // Then
        assertThat(actual.getThemeName(), is(notNullValue()));
        assertThat(actual.getThemeDescription(), is(notNullValue()));
    }

    @Test
    public void Can_create_a_theme_application_with_location() {
        // Given

        // When
        final ThemeApplication actual = themeApplicationFactory.createAnApplicationWithLocation();

        // Then
        assertThat(actual.getThemeName(), is(notNullValue()));
        assertThat(actual.getThemeDescription(), is(notNullValue()));
        assertThat(actual.isLocationEnabled(), is(true));
    }
}