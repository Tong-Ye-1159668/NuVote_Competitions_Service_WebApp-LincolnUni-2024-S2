package lu.p2.pages;

import lu.p2.io.Helper;
import lu.p2.models.VoteRecord;
import lu.p2.selenium.Bys;
import lu.p2.selenium.Finders;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static shiver.me.timbers.data.random.RandomStrings.someString;


public class HomeBasePageTest {

    private WebDriver webDriver;
    private Finders finders;
    private String siteUrl;
    private Helper helper;
    private HomePage homePage;

    @Before
    public void setUp() {
        webDriver = mock(WebDriver.class);
        finders = mock(Finders.class);
        siteUrl = someString();
        helper = mock(Helper.class);
        homePage = new HomePage(webDriver, finders, siteUrl, helper);
    }

    @Test
    public void Can_visit() {
        // Given

        // When
        homePage.visit();

        // Then
        then(webDriver).should().get(siteUrl);
    }

    @Test
    public void Can_click_register_btn() {
        // Given

        // When
        homePage.clickRegisterBtn();

        // Then
        then(finders).should().clickByAttribute("a", "href", "/register");
    }

    @Test
    public void Can_click_login_btn() {
        // Given

        // When
        homePage.clickLoginBtn();

        // Then
        then(finders).should().clickByAttribute("a", "href", "/login");
    }

    @Test
    public void Can_find_successfully_registered_msg() {

        final WebElement expected = mock(WebElement.class);

        // Given
        given(finders.findByText("div", "You have successfully registered!")).willReturn(expected);

        // When
        final WebElement actual = homePage.findRegisterSuccessfullyMsg();

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_find_user_menu() {

        final WebElement expected = mock(WebElement.class);

        // Given
        given(finders.findByText("a", "My Dashboard")).willReturn(expected);


        // When
        final WebElement actual = homePage.findUserMenu();

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_goto_my_dashboard_page() {
        // Given

        // When
        homePage.goToMyDashboardPage();

        // Then
        then(finders).should().clickById("navbarDropdown");
        then(finders).should().clickByText("a", "My Dashboard");
    }

    @Test
    public void Can_goto_password_page() {
        // Given

        // When
        homePage.goToPasswordPage();

        // Then
        then(finders).should().clickById("navbarDropdown");
        then(finders).should().clickByText("a", "Change Password");
    }

    @Test
    public void Can_logout() {
        // Given

        // When
        homePage.logout();

        // Then
        then(finders).should().clickById("navbarDropdown");
        then(finders).should().clickByText("a", "Logout");
    }

    @Test
    public void Can_verify_I_am_a_guest() {
        // Given
        given(finders.byAttribute("a", "href", "/register")).willReturn(mock(WebElement.class));
        given(finders.byAttribute("a", "href", "/login")).willReturn(mock(WebElement.class));

        // When
        final boolean actual = homePage.iAmAGuestUser();

        // Then
        assertThat(actual, is(true));
    }

    @Test
    public void Can_check_user_role() {
        final WebElement dropdownWEwebElement = mock(WebElement.class);
        final String role = someString();

        // Given
        given(finders.findById("navbarDropdown")).willReturn(dropdownWEwebElement);
        given(dropdownWEwebElement.getAttribute("title")).willReturn(format("role: %s", role));

        // When
        final boolean actual = homePage.checkMyRole(role);

        // Then
        assertThat(actual, is(true));
    }

    @Test
    public void Can_logout_user() {
        // Given

        // When
        homePage.logoutUser();

        // Then
        then(webDriver).should().get(format("%s/%s", siteUrl, "users/logout"));
    }

    @Test
    public void Can_goto_an_ongoing_event() {
        final WebElement eventWE = mock(WebElement.class);
        final WebElement eventCardWE = mock(WebElement.class);
        final WebElement parentCardWE = mock(WebElement.class);
        final List<WebElement> list = List.of(mock(WebElement.class), eventWE);
        final WebElement bWE = mock(WebElement.class);
        final WebElement aWE = mock(WebElement.class);
        final WebElement btnWE = mock(WebElement.class);
        final String eventName = someString();
        final String themeName = someString();

        // Given
        given(finders.findAllByText("div", "on_going")).willReturn(list);
        given(helper.getRandomElement(list)).willReturn(eventCardWE);
        given(eventCardWE.findElement(Bys.parentClassName("div", "event-card"))).willReturn(parentCardWE);
        given(parentCardWE.findElement(Bys.tagName("b"))).willReturn(bWE);
        given(parentCardWE.findElement(Bys.tagName("a"))).willReturn(aWE);
        given(bWE.getText()).willReturn(eventName);
        given(aWE.getText()).willReturn(themeName);
        given(parentCardWE.findElement(Bys.attribute("a", "class", "stretched-link"))).willReturn(btnWE);

        // When
        final VoteRecord actual = homePage.gotoAnOngoingEvent();


        // Then
        then(btnWE).should().click();
        assertThat(actual.getEventName(), is(eventName));
        assertThat(actual.getThemeName(), is(themeName));
    }
}