package lu.p2.models;

public class ThemeApplication {

    private String themeName;
    private String themeDescription;
    private boolean locationEnabled;

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(final String themeName) {
        this.themeName = themeName;
    }

    public String getThemeDescription() {
        return themeDescription;
    }

    public void setThemeDescription(final String themeDescription) {
        this.themeDescription = themeDescription;
    }

    public boolean isLocationEnabled() {
        return locationEnabled;
    }

    public void setLocationEnabled(final boolean locationEnabled) {
        this.locationEnabled = locationEnabled;
    }

    @Override
    public String toString() {
        return "ThemeApplication{" +
                "themeName='" + themeName + '\'' +
                ", themeDescription='" + themeDescription + '\'' +
                ", locationEnabled=" + locationEnabled +
                '}';
    }
}
