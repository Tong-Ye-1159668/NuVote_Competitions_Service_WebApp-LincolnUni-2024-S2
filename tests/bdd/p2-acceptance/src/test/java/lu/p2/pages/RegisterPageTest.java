package lu.p2.pages;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import lu.p2.models.User;
import lu.p2.selenium.Finders;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@RunWith(JUnitParamsRunner.class)
public class RegisterPageTest {

    private WebDriver webDriver;
    private Finders finders;
    private String siteUrl;
    private RegisterPage registerPage;

    @Before
    public void setUp() {
        webDriver = mock(WebDriver.class);
        finders = mock(Finders.class);
        siteUrl = someString();
        registerPage = new RegisterPage(webDriver, finders, siteUrl);
    }

    @Test
    public void Can_visit() {
        // Given

        // When
        registerPage.visit();

        // Then
        then(webDriver).should().get(siteUrl + "/register");
    }

    @Test
    public void Can_register_a_user_without_details() {
        final User user = mock(User.class);
        final String username = someString();
        final String email = someString();
        final String password = someString();

        // Given
        given(user.getUsername()).willReturn(username);
        given(user.getEmail()).willReturn(email);
        given(user.getPassword()).willReturn(password);

        // When
        registerPage.registerAUser(user);

        // Then
        then(finders).should().setTextByName("username", username);
        then(finders).should().setTextByName("email", email);
        then(finders).should().setTextByName("password", password);
        then(finders).should().setTextByName("password2", password);
        then(finders).should().clickByText("button", "Sign Up");
    }

    @Test
    public void Can_click_sign_up_btn() {
        // Given

        // When
        registerPage.submitInvalidInfo();

        // Then
        then(finders).should().clickByText("button", "Sign Up");
    }

    @Test
    public void Can_see_all_error_feedback() {
        // Given
        final String[] messages = {
                "Username must contain only characters and numbers! Length between 3 and 50.",
                "Please enter a valid email (example@domain.com).",
                "First name must contain only characters and numbers! Max length is 50.",
                "Last name must contain only characters and numbers! Max length is 50.",
//                "Location must contain only characters and numbers! Max length is 50.",
                "Password must be at least 8 characters long and include a mix of letters, numbers, and special",
                "Passwords do not match."
        };

        for (String message : messages) {
            final WebElement webElement = mock(WebElement.class);
            given(finders.findByText("div", message)).willReturn(webElement);
            given(webElement.isDisplayed()).willReturn(TRUE);
        }

        // When
        final boolean actual = registerPage.seeAllErrorFeedbacks();

        // Then
        assertThat(actual, is(TRUE));

    }

    @Test
    @Parameters({
            "0",
            "1",
            "2",
            "3",
            "4",
            "5"
//            "6"
    })
    public void Can_not_see_username_error_feedback(int index) {
        // Given
        final String[] messages = {
                "Username must contain only characters and numbers! Length between 3 and 50.",
                "Please enter a valid email (example@domain.com).",
                "First name must contain only characters and numbers! Max length is 50.",
                "Last name must contain only characters and numbers! Max length is 50.",
//                "Location must contain only characters and numbers! Max length is 50.",
                "Password must be at least 8 characters long and include a mix of letters, numbers, and special",
                "Passwords do not match."
        };

        for (int i = 0; i < messages.length; i++) {
            final WebElement webElement = mock(WebElement.class);
            given(finders.findByText("div", messages[i])).willReturn(webElement);
            if (i == index) {
                given(webElement.isDisplayed()).willReturn(false);
            } else {
                given(webElement.isDisplayed()).willReturn(true);
            }

        }


        // When
        final boolean actual = registerPage.seeAllErrorFeedbacks();

        // Then
        assertThat(actual, is(FALSE));

    }

    @Test
    public void Can_not_see_all_error_feedback() {
        // Given
        final String[] messages = {
                "Username must contain only characters and numbers! Length between 3 and 50.",
                "Please enter a valid email (example@domain.com).",
                "First name must contain only characters and numbers! Max length is 50.",
                "Last name must contain only characters and numbers! Max length is 50.",
//                "Location must contain only characters and numbers! Max length is 50.",
                "Password must be at least 8 characters long and include a mix of letters, numbers, and special",
                "Passwords do not match."
        };

        final List<WebElement> webElements = new ArrayList<>();
        for (String message : messages) {
            final WebElement webElement = mock(WebElement.class);
            given(finders.findByText("div", message)).willReturn(webElement);
            webElements.add(webElement);
        }


        // When
        final boolean actual = registerPage.seeAllErrorFeedbacks();

        // Then
        assertThat(actual, is(FALSE));

    }
}