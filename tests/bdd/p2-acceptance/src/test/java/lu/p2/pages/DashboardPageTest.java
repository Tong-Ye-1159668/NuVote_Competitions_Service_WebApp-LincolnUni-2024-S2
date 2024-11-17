package lu.p2.pages;

import lu.p2.io.DateTimeComparison;
import lu.p2.models.User;
import lu.p2.models.VoteRecord;
import lu.p2.selenium.Bys;
import lu.p2.selenium.Finders;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class DashboardPageTest {

    private Finders finders;
    private DateTimeComparison dateTimeComparison;
    private DashboardPage dashboardPage;

    @Before
    public void setUp() {
        finders = mock(Finders.class, RETURNS_DEEP_STUBS);
        dateTimeComparison = mock(DateTimeComparison.class);
        dashboardPage = new DashboardPage(finders, dateTimeComparison);
    }

    @Test
    public void Can_verify_all_details() {
        final User user = mock(User.class);
        final String username = someString();
        final String email = someString();
        final String firstname = someString();
        final String lastname = someString();
        final String location = someString();
        final String description = someString();

        // Given
        given(user.getUsername()).willReturn(username);
        given(finders.findByName("input", "username").getAttribute("value")).willReturn(username);

        given(user.getEmail()).willReturn(email);
        given(finders.findByName("input", "email").getAttribute("value")).willReturn(email);

        given(user.getFirstName()).willReturn(firstname);
        given(finders.findByName("input", "first_name").getAttribute("value")).willReturn(firstname);

        given(user.getLastName()).willReturn(lastname);
        given(finders.findByName("input", "last_name").getAttribute("value")).willReturn(lastname);

        given(user.getLocation()).willReturn(location);
        given(finders.findByName("input", "location").getAttribute("value")).willReturn(location);

        given(finders.findByName("input", "role").getAttribute("value")).willReturn(someString());

        given(user.getDescription()).willReturn(description);
        given(finders.findByName("textarea", "description").isDisplayed()).willReturn(TRUE);

        // When
        final boolean actual = dashboardPage.verifyMyDetails(user);

        // Then
        assertThat(actual, is(TRUE));
    }

    @Test
    public void Can_check_firstname_is_null() {
        final User user = mock(User.class);
        final String username = someString();
        final String email = someString();
        final String lastname = someString();
        final String location = someString();
        final String description = someString();

        // Given
        given(user.getUsername()).willReturn(username);
        given(finders.findByName("input", "username").getAttribute("value")).willReturn(username);

        given(user.getEmail()).willReturn(email);
        given(finders.findByName("input", "email").getAttribute("value")).willReturn(email);

        given(user.getFirstName()).willReturn(null);

        given(user.getLastName()).willReturn(lastname);
        given(finders.findByName("input", "last_name").getAttribute("value")).willReturn(lastname);

        given(user.getLocation()).willReturn(location);
        given(finders.findByName("input", "location").getAttribute("value")).willReturn(location);

        given(finders.findByName("input", "role").getAttribute("value")).willReturn(someString());

        given(user.getDescription()).willReturn(description);
        given(finders.findByName("textarea", "description").isDisplayed()).willReturn(TRUE);

        // When
        final boolean actual = dashboardPage.verifyMyDetails(user);

        // Then
        assertThat(actual, is(TRUE));
    }

    @Test
    public void Can_check_lastname_is_null() {
        final User user = mock(User.class);
        final String username = someString();
        final String email = someString();
        final String firstname = someString();
        final String location = someString();
        final String description = someString();

        // Given
        given(user.getUsername()).willReturn(username);
        given(finders.findByName("input", "username").getAttribute("value")).willReturn(username);

        given(user.getEmail()).willReturn(email);
        given(finders.findByName("input", "email").getAttribute("value")).willReturn(email);

        given(user.getFirstName()).willReturn(firstname);
        given(finders.findByName("input", "first_name").getAttribute("value")).willReturn(firstname);

        given(user.getLastName()).willReturn(null);

        given(user.getLocation()).willReturn(location);
        given(finders.findByName("input", "location").getAttribute("value")).willReturn(location);

        given(finders.findByName("input", "role").getAttribute("value")).willReturn(someString());

        given(user.getDescription()).willReturn(description);

        // When
        final boolean actual = dashboardPage.verifyMyDetails(user);

        // Then
        assertThat(actual, is(TRUE));
    }

    @Test
    public void Can_check_location_is_null() {
        final User user = mock(User.class);
        final String username = someString();
        final String email = someString();
        final String firstname = someString();
        final String lastname = someString();
        final String description = someString();

        // Given
        given(user.getUsername()).willReturn(username);
        given(finders.findByName("input", "username").getAttribute("value")).willReturn(username);

        given(user.getEmail()).willReturn(email);
        given(finders.findByName("input", "email").getAttribute("value")).willReturn(email);

        given(user.getFirstName()).willReturn(firstname);
        given(finders.findByName("input", "first_name").getAttribute("value")).willReturn(firstname);

        given(user.getLastName()).willReturn(lastname);
        given(finders.findByName("input", "last_name").getAttribute("value")).willReturn(lastname);

        given(user.getLocation()).willReturn(null);

        given(finders.findByName("input", "role").getAttribute("value")).willReturn(someString());

        given(user.getDescription()).willReturn(description);
        given(finders.findByName("textarea", "description").isDisplayed()).willReturn(TRUE);

        // When
        final boolean actual = dashboardPage.verifyMyDetails(user);

        // Then
        assertThat(actual, is(TRUE));
    }

    @Test
    public void Can_check_description_is_null() {
        final User user = mock(User.class);
        final String username = someString();
        final String email = someString();
        final String firstname = someString();
        final String lastname = someString();
        final String description = null;

        // Given
        given(user.getUsername()).willReturn(username);
        given(finders.findByName("input", "username").getAttribute("value")).willReturn(username);

        given(user.getEmail()).willReturn(email);
        given(finders.findByName("input", "email").getAttribute("value")).willReturn(email);

        given(user.getFirstName()).willReturn(firstname);
        given(finders.findByName("input", "first_name").getAttribute("value")).willReturn(firstname);

        given(user.getLastName()).willReturn(lastname);
        given(finders.findByName("input", "last_name").getAttribute("value")).willReturn(lastname);

        given(user.getLocation()).willReturn(null);

        given(finders.findByName("input", "role").getAttribute("value")).willReturn(someString());

        given(user.getDescription()).willReturn(description);

        // When
        final boolean actual = dashboardPage.verifyMyDetails(user);

        // Then
        assertThat(actual, is(TRUE));
    }

    @Test
    public void Can_verify_my_details() {
        final User user = mock(User.class);
        final String firstname = someString();
        final String lastname = someString();
        final String description = someString();

        // Given
        given(user.getFirstName()).willReturn(firstname);
        given(user.getLastName()).willReturn(lastname);
        given(user.getDescription()).willReturn(description);

        // When
        final boolean actual = dashboardPage.verifyMyDetails(user);
        // Then
        assertThat(actual, is(TRUE));
    }

    @Test
    public void Can_click_edit_btn() {
        // Given

        // When
        dashboardPage.clickEditBtn();

        // Then
        then(finders).should().clickByText("a", "Edit");
    }

    @Test
    public void Can_check_if_profile_image_is_default() {

        final WebElement profileImageWB = mock(WebElement.class);

        // Given
        given(finders.findById("profile-image")).willReturn(profileImageWB);
        given(profileImageWB.getAttribute("src")).willReturn("static/images/default_profile.png");

        // When
        final boolean actual = dashboardPage.isDefaultProfileImage();

        // Then
        then(finders).should().waitById("profile-image");
        assertThat(actual, is(TRUE));
    }

    @Test
    public void Can_check_if_profile_image_is_not_default() {

        final WebElement profileImageWB = mock(WebElement.class);

        // Given
        given(finders.findById("profile-image")).willReturn(profileImageWB);
        given(profileImageWB.getAttribute("src")).willReturn(someString());

        // When
        final boolean actual = dashboardPage.isDefaultProfileImage();

        // Then
        then(finders).should().waitById("profile-image");
        assertThat(actual, is(FALSE));
    }

    @Test
    public void Can_check_user_role() {
        final WebElement userWE = mock(WebElement.class);
        final WebElement webElement = mock(WebElement.class);
        final String role = someString();

        // Given
        given(finders.findByText("strong", "Role:")).willReturn(userWE);
        given(userWE.findElement(Bys.parentClassName("div", "list-group-item"))).willReturn(webElement);
        given(webElement.getText()).willReturn(role);
        // When
        final boolean actual = dashboardPage.checkMyRole(role);

        // Then
        assertThat(actual, is(TRUE));
    }

    @Test
    public void Can_check_my_vote() {
        final VoteRecord voteRecord = mock(VoteRecord.class);
        final String eventName = someString();
        final String candidateName = someString();
        final String themeName = someString();
        final WebElement webElement = mock(WebElement.class, RETURNS_DEEP_STUBS);
        final String datetimeStr = someString();

        // Given
        given(voteRecord.getEventName()).willReturn(eventName);
        given(voteRecord.getCandidateName()).willReturn(candidateName);
        given(voteRecord.getThemeName()).willReturn(themeName);

        given(finders.findByText("a", eventName)).willReturn(mock(WebElement.class));
        given(finders.findByText("td", candidateName)).willReturn(mock(WebElement.class));
        given(finders.findByText("td", themeName)).willReturn(mock(WebElement.class));
        given(finders.findById("recentVotesBody")).willReturn(webElement);
        given(webElement.findElement(Bys.tagName("tr")).findElement(Bys.tagName("td")).getText()).willReturn(datetimeStr);
        given(dateTimeComparison.isLessThanSomeMinutes(datetimeStr, 3)).willReturn(true);

        // When
        dashboardPage.checkVotes(voteRecord);

        // Then
        then(finders).should().clickById("votes-tab");
    }
}