package lu.p2.pages;

import lu.p2.factories.ThemeApplicationFactory;
import lu.p2.factories.UserFactory;
import lu.p2.io.DateTimeComparison;
import lu.p2.models.ThemeApplication;
import lu.p2.models.User;
import lu.p2.selenium.Bys;
import lu.p2.selenium.Finders;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import shiver.me.timbers.waiting.Wait;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static shiver.me.timbers.waiting.Decision.YES;

@Component
public class ThemePage implements BasePage {
    private final WebDriver webDriver;
    private final Finders finders;
    private final String siteUrl;
    private final UserFactory userFactory;
    private final DateTimeComparison dateTimeComparison;

    public ThemePage(final WebDriver webDriver, final Finders finders, @Value("${site.url}") final String siteUrl, final UserFactory userFactory, final DateTimeComparison dateTimeComparison) {
        this.webDriver = webDriver;
        this.finders = finders;
        this.siteUrl = siteUrl;
        this.userFactory = userFactory;
        this.dateTimeComparison = dateTimeComparison;
    }

    @Override
    public void visit() {
        webDriver.get(siteUrl + "/themes");
    }

    public ThemeApplication submitApplication(final ThemeApplication themeApplication) {
        finders.clickByText("button", "Propose a theme");
        finders.waitByClassName("modal-dialog");
        finders.setTextById("theme_name", themeApplication.getThemeName());
        finders.setTextById("theme_description", themeApplication.getThemeDescription());
        if (themeApplication.isLocationEnabled()){
            finders.clickById("enable_location");
        }
        finders.clickById("submit");
        return themeApplication;
    }

    @Wait(waitForTrue = YES)
    public boolean displayTheApplicationAsPending(final ThemeApplication themeApplication) {
        final WebElement titleWE = finders.findByText("h5", themeApplication.getThemeName());
        final WebElement parentWE = titleWE.findElement(Bys.parentClassName("div", "card-body"));
        parentWE.findElement(Bys.text("span", "Pending"));
        final String text = finders.findByText("h6", "proposed by").getText();
        final String dateString = text.substring(text.length() - 22);
        assertThat(TRUE, is(dateTimeComparison.isLessThanSomeMinutes(dateString, 3)));
        return Objects.equals(titleWE.findElement(Bys.followingSibling("p")).getText(), themeApplication.getThemeDescription());
    }

    public void gotoMyProposals() {
        finders.clickByText("a", "My Proposals");
    }

    public void deleteMyApplication(final ThemeApplication themeApplication) {
        final WebElement titleWE = finders.findByText("h5", themeApplication.getThemeName());
        final WebElement parentWE = titleWE.findElement(Bys.parentClassName("div", "card-body"));
        finders.waitByText("button", "Delete");
        parentWE.findElement(Bys.text("button", "Delete")).click();
        finders.waitById("confirmDeleteThemeBtn");
        finders.clickById("confirmDeleteThemeBtn");
    }

    public boolean myThemeIsEmpty() {
        visit();
        gotoMyProposals();
        finders.findByText("p", "You have not proposed any themes yet. Propose a theme to get started!");
        return true;
    }

    public void actOnTheApplication(final ThemeApplication themeApplication, final String action) {
        gotoManageThemes();
        finders.waitById("pending-tab");
        finders.clickById("pending-tab");
        final WebElement titleWE = finders.findByText("h5", themeApplication.getThemeName());
        final WebElement parentWE = titleWE.findElement(Bys.parentClassName("div", "card-body"));
        parentWE.findElement(Bys.text("button", action)).click();
        finders.waitById("confirmApproveThemeBtn");
        finders.clickById("confirmApproveThemeBtn");
    }

    public void gotoMyTheme(final ThemeApplication themeApplication) {
        try {
            // Check if the sub-element exists and click
            finders.waitByText("a", "My Themes");
            finders.clickByText("a", "My Themes");
        } catch (TimeoutException e) {
            // Continue if the element does not exist
        }
        finders.waitByText("a", themeApplication.getThemeName());
        finders.clickByText("a", themeApplication.getThemeName());
    }

    public void gotoMyTheme(final ThemeApplication themeApplication, final User user) {
        gotoMyTheme(themeApplication);
        final WebElement webElement = finders.findByText("span", format("Proposed by ", user.getUsername()));
        final String text = webElement.getText();
        final String dateString = text.substring(text.length() - 22);
        assertThat(TRUE, is(dateTimeComparison.isLessThanSomeMinutes(dateString, 3)));
        finders.findByText("a", "Start a\n            Donation Drive");
        finders.findByText("a", "Manage Your\n            Donation Drives");
        finders.findByText("a", "Create an\n            Event");
        finders.findByText("a", "Manage Roles");
        finders.findByText("a", "Manage Banned Users");
    }

    public void gotoMyTheme(final ThemeApplication themeApplication, final String role) {
        gotoMyTheme(themeApplication);
        finders.waitByText("a", "Manage Roles");
        if (role.equals("tAdmin")) {
            finders.findByText("a", "Create an\n" + "            Event");
        }
        finders.findByText("a", "Manage Roles");
        finders.findByText("a", "Manage Banned Users");
    }

    public void checkThemeStatus(final ThemeApplication themeApplication, final String status) {
        gotoManageThemes();
        finders.clickByText("a", "Rejected & Accepted");
        final WebElement titleWE = finders.findByText("h5", themeApplication.getThemeName());
        final WebElement parentWE = titleWE.findElement(Bys.parentClassName("div", "card-body"));
        parentWE.findElement(Bys.text("span", status));
    }

    public void checkMyThemeStatus(final ThemeApplication themeApplication, final String status) {
        final WebElement titleWE = finders.findByText("h5", themeApplication.getThemeName());
        final WebElement parentWE = titleWE.findElement(Bys.parentClassName("div", "card-body"));
        parentWE.findElement(Bys.text("span", status));
    }

    public void gotoManageThemes() {
        visit();
        finders.clickByAttribute("a", "href", "/themes/approve");
    }

    public User updateUserRole(final String themeRole) {
        finders.clickByText("a", "Manage Roles");
        finders.clickByText("button", "Add Role");
        finders.waitById("userSelect");
        final List<WebElement> options = finders.findById("userSelect").findElements(Bys.tagName("option"));
        Collections.shuffle(options);
        final WebElement user = options.get(0);
        user.click();
        finders.findById("roleSelect").findElements(Bys.tagName("option")).forEach(webElement -> {
            final String role = webElement.getAttribute("value");
            if (role.equals(themeRole)) {
                webElement.click();
            }
        });
        finders.waitById("addThemeRoleForm");
        finders.findById("addThemeRoleForm").findElement(Bys.text("button", "Add")).click();
        return userFactory.createAUser(user.getText());
    }
}
