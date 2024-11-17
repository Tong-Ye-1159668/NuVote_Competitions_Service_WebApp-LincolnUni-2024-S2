package lu.p2.factories;

import lu.p2.models.ThemeApplication;
import org.springframework.stereotype.Component;

import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

@Component
public class ThemeApplicationFactory {
    public ThemeApplication createAnApplication() {
        final ThemeApplication themeApplication = new ThemeApplication();
        themeApplication.setThemeName(someAlphanumericString(10, 50));
        themeApplication.setThemeDescription(someAlphanumericString(50, 200));
        return themeApplication;
    }

    public ThemeApplication createAnApplicationWithLocation() {
        final ThemeApplication application = this.createAnApplication();
        application.setLocationEnabled(true);
        return application;
    }
}
