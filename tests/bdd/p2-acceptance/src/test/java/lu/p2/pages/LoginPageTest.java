package lu.p2.pages;

import lu.p2.models.User;
import lu.p2.selenium.Finders;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class LoginPageTest {

    private LoginPage loginPage;
    private WebDriver webDriver;
    private Finders finders;
    private String siteUrl;

    @Before
    public void setUp() {
        webDriver = mock(WebDriver.class);
        finders = mock(Finders.class);
        siteUrl = someString();
        loginPage = new LoginPage(webDriver, finders, siteUrl);
    }

    @Test
    public void Can_visit() {
        // Given

        // When
        loginPage.visit();

        // Then
        then(webDriver).should().get(siteUrl + "/users/login");
    }

    @Test
    public void Can_sign_in_with_a_user() {

        final User user = mock(User.class);
        final String username = someString();
        final String password = someString();

        // Given
        given(user.getUsername()).willReturn(username);
        given(user.getPassword()).willReturn(password);

        // When
        loginPage.login(user);

        // Then
        then(finders).should().setTextByName("username", username);
        then(finders).should().setTextByName("password", password);
        then(finders).should().clickByText("button", "Sign in");
    }

    @Test
    public void Can_click_sign_in_btn() {
        // Given

        // When
        loginPage.submitInvalidCredentials();

        // Then
        then(finders).should().setTextByName("username", "");
        then(finders).should().setTextByName("password", "");
        then(finders).should().clickByText("button", "Sign in");
    }

    @Test
    public void Can_see_all_error_feedback() {

        final WebElement usernameWE = mock(WebElement.class);
        final WebElement passwordWE = mock(WebElement.class);

        // Given
        given(finders.findByText("div", "Username must contain only characters and numbers!")).willReturn(usernameWE);
        given(finders.findByText("div", "Password is required")).willReturn(passwordWE);
        given(usernameWE.isDisplayed()).willReturn(true);
        given(passwordWE.isDisplayed()).willReturn(true);

        // When
        final boolean actual = loginPage.seeAllErrorFeedbacks();

        // Then
        assertThat(actual, is(TRUE));
    }

    @Test
    public void Can_not_see_username_error_feedback() {

        final WebElement usernameWE = mock(WebElement.class);
        final WebElement passwordWE = mock(WebElement.class);

        // Given
        given(finders.findByText("div", "Username must contain only characters and numbers!")).willReturn(usernameWE);
        given(finders.findByText("div", "Password is required")).willReturn(passwordWE);
        given(usernameWE.isDisplayed()).willReturn(false);
        given(passwordWE.isDisplayed()).willReturn(true);

        // When
        final boolean actual = loginPage.seeAllErrorFeedbacks();

        // Then
        assertThat(actual, is(FALSE));
    }

    @Test
    public void Can_not_see_password_error_feedback() {

        final WebElement usernameWE = mock(WebElement.class);
        final WebElement passwordWE = mock(WebElement.class);

        // Given
        given(finders.findByText("div", "Username must contain only characters and numbers!")).willReturn(usernameWE);
        given(finders.findByText("div", "Password is required")).willReturn(passwordWE);
        given(usernameWE.isDisplayed()).willReturn(true);
        given(passwordWE.isDisplayed()).willReturn(false);

        // When
        final boolean actual = loginPage.seeAllErrorFeedbacks();

        // Then
        assertThat(actual, is(FALSE));
    }

    @Test
    public void Can_check_is_login_page() {
        // Given
        given(finders.findById("username")).willReturn(mock(WebElement.class));
        given(finders.findById("password")).willReturn(mock(WebElement.class));
        given(finders.findByText("button", "Sign in")).willReturn(mock(WebElement.class));
        given(finders.findByText("a", "Register")).willReturn(mock(WebElement.class));

        // When
        final boolean actual = loginPage.isLoginPage();

        // Then
        assertThat(actual, is(TRUE));
    }
}