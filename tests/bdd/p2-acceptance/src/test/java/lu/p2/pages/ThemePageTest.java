package lu.p2.pages;

import lu.p2.factories.UserFactory;
import lu.p2.io.DateTimeComparison;
import lu.p2.models.ThemeApplication;
import lu.p2.models.User;
import lu.p2.selenium.Bys;
import lu.p2.selenium.Finders;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class ThemePageTest {

    private WebDriver webDriver;
    private Finders finders;
    private String siteUrl;
    private UserFactory userFactory;
    private DateTimeComparison dateTimeComparison;
    private ThemePage themePage;

    @Before
    public void setUp() {
        webDriver = mock(WebDriver.class);
        finders = mock(Finders.class);
        siteUrl = someString();
        userFactory = mock(UserFactory.class);
        dateTimeComparison = mock(DateTimeComparison.class);
        themePage = new ThemePage(webDriver, finders, siteUrl, userFactory, dateTimeComparison);
    }

    @Test
    public void Can_visit() {
        // Given

        // When
        themePage.visit();

        // Then
        then(webDriver).should().get(siteUrl + "/themes");
    }


    @Test
    public void Can_submit_an_application() {

        final ThemeApplication themeApplication = mock(ThemeApplication.class);
        final String name = someString();
        final String description = someString();

        // Given
        given(themeApplication.getThemeName()).willReturn(name);
        given(themeApplication.getThemeDescription()).willReturn(description);

        // When
        themePage.submitApplication(themeApplication);

        // Then
        then(finders).should().clickByText("button", "Propose a theme");
        then(finders).should().waitByClassName("modal-dialog");
        then(finders).should().setTextById("theme_name", name);
        then(finders).should().setTextById("theme_description", description);
    }

    @Test
    public void Can_see_pending_applications() {
        final ThemeApplication themeApplication = mock(ThemeApplication.class);
        final String name = someString();
        final String description = someString();
        final WebElement title = mock(WebElement.class);
        final WebElement parentWE = mock(WebElement.class);
        final WebElement datetimeWE = mock(WebElement.class);
        final WebElement descriptionWE = mock(WebElement.class);
        final String datetimeString = someString(22);

        // Given
        given(themeApplication.getThemeName()).willReturn(name);
        given(themeApplication.getThemeDescription()).willReturn(description);
        given(finders.findByText("h5", name)).willReturn(title);
        given(title.findElement(Bys.parentClassName("div", "card-body"))).willReturn(parentWE);
        given(parentWE.findElement(Bys.text("span", "Pending"))).willReturn(mock(WebElement.class));
        given(finders.findByText("h6", "proposed by")).willReturn(datetimeWE);
        given(datetimeWE.getText()).willReturn(someString() + datetimeString);
        given(dateTimeComparison.isLessThanSomeMinutes(datetimeString, 3)).willReturn(TRUE);
        given(title.findElement(Bys.followingSibling("p"))).willReturn(descriptionWE);
        given(descriptionWE.getText()).willReturn(description);

        // When
        final boolean actual = themePage.displayTheApplicationAsPending(themeApplication);

        // Then
        assertThat(actual, is(TRUE));
    }

    @Test
    public void Can_goto_my_proposals() {
        // Given

        // When
        themePage.gotoMyProposals();

        // Then
        then(finders).should().clickByText("a", "My Proposals");
    }

    @Test
    public void Can_delete_my_application() {
        final ThemeApplication themeApplication = mock(ThemeApplication.class);
        final String themeName = someString();
        final WebElement titleWE = mock(WebElement.class);
        final WebElement parentWE = mock(WebElement.class);
        final WebElement btnWE = mock(WebElement.class);

        // Given
        given(themeApplication.getThemeName()).willReturn(themeName);
        given(finders.findByText("h5", themeName)).willReturn(titleWE);
        given(titleWE.findElement(Bys.parentClassName("div", "card-body"))).willReturn(parentWE);
        given(parentWE.findElement(Bys.text("button", "Delete"))).willReturn(btnWE);

        // When
        themePage.deleteMyApplication(themeApplication);

        // Then
        then(btnWE).should().click();
        then(finders).should().waitById("confirmDeleteThemeBtn");
        then(finders).should().clickById("confirmDeleteThemeBtn");

    }

    @Test
    public void Can_check_my_theme_is_empty() {
        // Given
        given(finders.findByText("p", "You have not proposed any themes yet. Propose a theme to get started!")).willReturn(mock(WebElement.class));

        // When
        final boolean actual = themePage.myThemeIsEmpty();

        // Then
        then(webDriver).should().get(siteUrl + "/themes");
        then(finders).should().clickByText("a", "My Proposals");
        assertThat(actual, is(TRUE));
    }

    @Test
    public void Can_approve_a_theme() {
        final ThemeApplication themeApplication = mock(ThemeApplication.class);
        final String themeName = someString();
        final String action = someString();
        final WebElement themeWE = mock(WebElement.class);
        final WebElement webElement = mock(WebElement.class);
        final WebElement btnWE = mock(WebElement.class);

        // Given
        given(themeApplication.getThemeName()).willReturn(themeName);
        given(finders.findByText("h5", themeName)).willReturn(themeWE);
        given(themeWE.findElement(Bys.parentClassName("div", "card-body"))).willReturn(webElement);
        given(webElement.findElement(Bys.text("button", action))).willReturn(btnWE);

        // When
        themePage.actOnTheApplication(themeApplication, action);

        // Then
        then(finders).should().clickByAttribute("a", "href", "/themes/approve");
        then(finders).should().waitById("pending-tab");
        then(finders).should().clickById("pending-tab");
        then(btnWE).should().click();
        then(finders).should().waitById("confirmApproveThemeBtn");
        then(finders).should().clickById("confirmApproveThemeBtn");
    }

    @Test
    public void Can_goto_my_theme() {
        final ThemeApplication themeApplication = mock(ThemeApplication.class);
        final User user = mock(User.class);
        final String themeName = someString();
        final WebElement themeWE = mock(WebElement.class);
        final WebElement webElement = mock(WebElement.class);
        final String username = someString();
        final WebElement dateWE = mock(WebElement.class);
        final String dateString = someString(22);

        // Given
        given(themeApplication.getThemeName()).willReturn(themeName);
        given(user.getUsername()).willReturn(username);
        given(finders.findByText("h5", themeName)).willReturn(themeWE);
        given(themeWE.findElement(Bys.parentClassName("div", "card-body"))).willReturn(webElement);
        given(finders.findByText("span", format("Proposed by ", username))).willReturn(dateWE);
        given(dateWE.getText()).willReturn(someString() + dateString);
        given(dateTimeComparison.isLessThanSomeMinutes(dateString, 3)).willReturn(TRUE);

        // When
        themePage.gotoMyTheme(themeApplication, user);

        // Then
        then(finders).should().findByText("span", String.format("Proposed by ", username));
        then(finders).should().findByText("a", "Start a\n            Donation Drive");
        then(finders).should().findByText("a", "Manage Your\n            Donation Drives");
        then(finders).should().findByText("a", "Create an\n" + "            Event");
        then(finders).should().findByText("a", "Manage Roles");
        then(finders).should().findByText("a", "Manage Banned Users");
    }

    @Test
    public void Can_goto_my_theme_as_helper() {
        final ThemeApplication themeApplication = mock(ThemeApplication.class);
        final String themeName = someString();
        final WebElement themeWE = mock(WebElement.class);
        final WebElement webElement = mock(WebElement.class);
        final String username = someString();

        // Given
        given(themeApplication.getThemeName()).willReturn(themeName);
        given(finders.findByText("h5", themeName)).willReturn(themeWE);
        given(themeWE.findElement(Bys.parentClassName("div", "card-body"))).willReturn(webElement);

        // When
        themePage.gotoMyTheme(themeApplication, someString());

        // Then
        then(finders).should().findByText("a", "Manage Roles");
        then(finders).should().findByText("a", "Manage Banned Users");
    }

    @Test
    public void Can_goto_my_theme_as_tadmin() {
        final ThemeApplication themeApplication = mock(ThemeApplication.class);
        final String themeName = someString();
        final WebElement themeWE = mock(WebElement.class);
        final WebElement webElement = mock(WebElement.class);
        final String username = someString();

        // Given
        given(themeApplication.getThemeName()).willReturn(themeName);
        given(finders.findByText("h5", themeName)).willReturn(themeWE);
        given(themeWE.findElement(Bys.parentClassName("div", "card-body"))).willReturn(webElement);

        // When
        themePage.gotoMyTheme(themeApplication, "tAdmin");

        // Then
        then(finders).should().findByText("a", "Create an\n" + "            Event");
        then(finders).should().findByText("a", "Manage Roles");
        then(finders).should().findByText("a", "Manage Banned Users");
    }

    @Test
    public void Can_check_theme_status() {
        final ThemeApplication themeApplication = mock(ThemeApplication.class);
        final String themeName = someString();
        final WebElement themeWE = mock(WebElement.class);
        final WebElement webElement = mock(WebElement.class);
        final String status = someString();

        // Given
        given(themeApplication.getThemeName()).willReturn(themeName);
        given(finders.findByText("h5", themeName)).willReturn(themeWE);
        given(themeWE.findElement(Bys.parentClassName("div", "card-body"))).willReturn(webElement);
        given(webElement.findElement(Bys.text("span", status))).willReturn(mock(WebElement.class));

        // When
        themePage.checkThemeStatus(themeApplication, status);

        // Then
        then(finders).should().clickByAttribute("a", "href", "/themes/approve");
        then(finders).should().clickByText("a", "Rejected & Accepted");

    }

    @Test
    public void Can_check_my_theme_status() {
        final ThemeApplication themeApplication = mock(ThemeApplication.class);
        final String themeName = someString();
        final WebElement themeWE = mock(WebElement.class);
        final WebElement webElement = mock(WebElement.class);
        final String status = someString();

        // Given
        given(themeApplication.getThemeName()).willReturn(themeName);
        given(finders.findByText("h5", themeName)).willReturn(themeWE);
        given(themeWE.findElement(Bys.parentClassName("div", "card-body"))).willReturn(webElement);
        given(webElement.findElement(Bys.text("span", status))).willReturn(mock(WebElement.class));

        // When
        themePage.checkMyThemeStatus(themeApplication, status);

        // Then
    }

    @Test
    public void Can_update_user_role() {

        final WebElement userSelectWE = mock(WebElement.class);
        final WebElement roleSelectWE = mock(WebElement.class);
        final WebElement userWE = mock(WebElement.class);
        final WebElement roleWE = mock(WebElement.class);
        final List<WebElement> userOptions = List.of(userWE);
        final List<WebElement> roleOptions = List.of(roleWE);
        final String username = someString();
        final String role = someString();

        // Given
        given(finders.findById("userSelect")).willReturn(userSelectWE);
        given(userSelectWE.findElements(Bys.tagName("option"))).willReturn(userOptions);

        given(finders.findById("roleSelect")).willReturn(roleSelectWE);
        given(roleSelectWE.findElements(Bys.tagName("option"))).willReturn(roleOptions);
        given(roleWE.getAttribute("value")).willReturn(role);
        given(userWE.getText()).willReturn(username);
        final WebElement formWE = mock(WebElement.class);
        final WebElement addBtn = mock(WebElement.class);
        given(finders.findById("addThemeRoleForm")).willReturn(formWE);
        given(formWE.findElement(Bys.text("button", "Add"))).willReturn(addBtn);

        final User expected = mock(User.class);
        given(userFactory.createAUser(username)).willReturn(expected);
        // When
        final User actual = themePage.updateUserRole(role);

        // Then
        then(finders).should().clickByText("a", "Manage Roles");
        then(finders).should().clickByText("button", "Add Role");
        then(finders).should().waitById("userSelect");
        then(userWE).should().click();
        then(roleWE).should().click();
        then(finders).should().waitById("addThemeRoleForm");
        then(addBtn).should().click();
        assertThat(actual, is(expected));

    }

    @Test
    public void Can_fail_update_user_role() {

        final WebElement userSelectWE = mock(WebElement.class);
        final WebElement roleSelectWE = mock(WebElement.class);
        final WebElement userWE = mock(WebElement.class);
        final WebElement roleWE = mock(WebElement.class);
        final List<WebElement> userOptions = List.of(userWE);
        final List<WebElement> roleOptions = List.of(roleWE);
        final String username = someString();

        // Given
        given(finders.findById("userSelect")).willReturn(userSelectWE);
        given(userSelectWE.findElements(Bys.tagName("option"))).willReturn(userOptions);

        given(finders.findById("roleSelect")).willReturn(roleSelectWE);
        given(roleSelectWE.findElements(Bys.tagName("option"))).willReturn(roleOptions);
        given(roleWE.getAttribute("value")).willReturn(someString());
        given(userWE.getText()).willReturn(username);
        final WebElement formWE = mock(WebElement.class);
        final WebElement addBtn = mock(WebElement.class);
        given(finders.findById("addThemeRoleForm")).willReturn(formWE);
        given(formWE.findElement(Bys.text("button", "Add"))).willReturn(addBtn);

        final User expected = mock(User.class);
        given(userFactory.createAUser(username)).willReturn(expected);
        // When
        final User actual = themePage.updateUserRole(someString());

        // Then
        then(finders).should().clickByText("a", "Manage Roles");
        then(finders).should().clickByText("button", "Add Role");
        then(finders).should().waitById("userSelect");
        then(userWE).should().click();
        then(roleWE).should(never()).click();
        then(finders).should().waitById("addThemeRoleForm");
        then(addBtn).should().click();
        assertThat(actual, is(expected));

    }
}