package lu.p2.models;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class ThemeApplicationTest {

    @Test
    public void Can_create_an_theme_application() {
        // Given
        final String themeName = someString();
        final String themeDescription = someString();

        // When
        final ThemeApplication actual = new ThemeApplication();
        actual.setThemeName(themeName);
        actual.setThemeDescription(themeDescription);

        // Then
        assertThat(actual.getThemeName(), is(themeName));
        assertThat(actual.getThemeDescription(), is(themeDescription));
        assertThat(actual.toString(), is(notNullValue()));
    }
}