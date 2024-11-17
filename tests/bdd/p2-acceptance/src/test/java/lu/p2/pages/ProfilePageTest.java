package lu.p2.pages;

import lu.p2.factories.LocationFactory;
import lu.p2.io.HClient;
import lu.p2.models.User;
import lu.p2.selenium.Finders;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;

import java.io.IOException;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class ProfilePageTest {

    private Finders finders;
    private HClient hClient;
    private LocationFactory locationFactory;
    private ProfilePage profilePage;

    @Before
    public void setUp() {
        finders = mock(Finders.class, RETURNS_DEEP_STUBS);
        hClient = mock(HClient.class);
        locationFactory = mock(LocationFactory.class);
        profilePage = new ProfilePage(finders, hClient, locationFactory);
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
        final boolean actual = profilePage.verifyMyDetails(user);

        // Then
        assertThat(actual, is(TRUE));
    }

    @Test
    public void Can_check_username_is_emtpy() {
        final User user = mock(User.class);
        final String username = someString();
        final String email = someString();
        final String firstname = someString();
        final String lastname = someString();
        final String location = someString();
        final String description = someString();

        // Given
        given(user.getUsername()).willReturn(username);
        given(finders.findByName("input", "username").getAttribute("value")).willReturn(null);

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
        final boolean actual = profilePage.verifyMyDetails(user);

        // Then
        assertThat(actual, is(FALSE));
    }

    @Test
    public void Can_check_email_is_emtpy() {
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
        given(finders.findByName("input", "email").getAttribute("value")).willReturn(null);

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
        final boolean actual = profilePage.verifyMyDetails(user);

        // Then
        assertThat(actual, is(FALSE));
    }

    @Test
    public void Can_check_firstname_is_emtpy() {
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
        given(finders.findByName("input", "first_name").getAttribute("value")).willReturn(null);

        given(user.getLastName()).willReturn(lastname);
        given(finders.findByName("input", "last_name").getAttribute("value")).willReturn(lastname);

        given(user.getLocation()).willReturn(location);
        given(finders.findByName("input", "location").getAttribute("value")).willReturn(location);

        given(finders.findByName("input", "role").getAttribute("value")).willReturn(someString());

        given(user.getDescription()).willReturn(description);
        given(finders.findByName("textarea", "description").isDisplayed()).willReturn(TRUE);

        // When
        final boolean actual = profilePage.verifyMyDetails(user);

        // Then
        assertThat(actual, is(FALSE));
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
        final boolean actual = profilePage.verifyMyDetails(user);

        // Then
        assertThat(actual, is(TRUE));
    }

    @Test
    public void Can_check_lastname_is_emtpy() {
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
        given(finders.findByName("input", "last_name").getAttribute("value")).willReturn(null);

        given(user.getLocation()).willReturn(location);
        given(finders.findByName("input", "location").getAttribute("value")).willReturn(location);

        given(finders.findByName("input", "role").getAttribute("value")).willReturn(someString());

        given(user.getDescription()).willReturn(description);
        given(finders.findByName("textarea", "description").isDisplayed()).willReturn(TRUE);

        // When
        final boolean actual = profilePage.verifyMyDetails(user);

        // Then
        assertThat(actual, is(FALSE));
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
        given(finders.findByName("textarea", "description").isDisplayed()).willReturn(TRUE);

        // When
        final boolean actual = profilePage.verifyMyDetails(user);

        // Then
        assertThat(actual, is(TRUE));
    }

    @Test
    public void Can_check_location_is_emtpy() {
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
        given(finders.findByName("input", "location").getAttribute("value")).willReturn(null);

        given(finders.findByName("input", "role").getAttribute("value")).willReturn(someString());

        given(user.getDescription()).willReturn(description);
        given(finders.findByName("textarea", "description").isDisplayed()).willReturn(TRUE);

        // When
        final boolean actual = profilePage.verifyMyDetails(user);

        // Then
        assertThat(actual, is(FALSE));
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
        final boolean actual = profilePage.verifyMyDetails(user);

        // Then
        assertThat(actual, is(TRUE));
    }

    @Test
    public void Can_check_role_is_emtpy() {
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

        given(finders.findByName("input", "role").getAttribute("value")).willReturn("");

        given(user.getDescription()).willReturn(description);
        given(finders.findByName("textarea", "description").isDisplayed()).willReturn(TRUE);
        // When
        final boolean actual = profilePage.verifyMyDetails(user);

        // Then
        assertThat(actual, is(FALSE));
    }

    @Test
    public void Can_check_description_is_emtpy() {
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
        given(finders.findByName("textarea", "description").isDisplayed()).willReturn(FALSE);

        // When
        final boolean actual = profilePage.verifyMyDetails(user);

        // Then
        assertThat(actual, is(FALSE));
    }

    @Test
    public void Can_update_user_profile() {
        final User user = mock(User.class);
        final WebElement firstNameWB = mock(WebElement.class);
        final WebElement lastNameWB = mock(WebElement.class);
        final WebElement descriptionWB = mock(WebElement.class);
        final String firstname = someString();
        final String lastname = someString();
        final String description = someString();

        // Given
        given(user.getFirstName()).willReturn(firstname);
        given(user.getLastName()).willReturn(lastname);
        given(user.getDescription()).willReturn(description);
        given(finders.findByName("input", "first_name")).willReturn(firstNameWB);
        given(finders.findByName("input", "last_name")).willReturn(lastNameWB);
        given(finders.findByName("textarea", "description")).willReturn(descriptionWB);

        // When
        profilePage.updateMyProfile(user);

        // Then
        then(firstNameWB).should().sendKeys(firstname);
        then(lastNameWB).should().sendKeys(lastname);
        then(descriptionWB).should().sendKeys(description);
        then(finders).should().clickByText("button", "Update Profile");
    }

    @Test
    public void Can_skip_update_first_name() {
        final User user = mock(User.class);
        final WebElement lastNameWB = mock(WebElement.class);
        final WebElement descriptionWB = mock(WebElement.class);
        final String lastname = someString();
        final String description = someString();

        // Given
        given(user.getFirstName()).willReturn(null);
        given(user.getLastName()).willReturn(lastname);
        given(user.getDescription()).willReturn(description);
        given(finders.findByName("input", "last_name")).willReturn(lastNameWB);
        given(finders.findByName("textarea", "description")).willReturn(descriptionWB);

        // When
        profilePage.updateMyProfile(user);

        // Then
        then(lastNameWB).should().sendKeys(lastname);
        then(descriptionWB).should().sendKeys(description);
        then(finders).should().clickByText("button", "Update Profile");
    }

    @Test
    public void Can_skip_update_last_name() {
        final User user = mock(User.class);
        final WebElement firstNameWB = mock(WebElement.class);
        final WebElement descriptionWB = mock(WebElement.class);
        final String firstname = someString();
        final String description = someString();

        // Given
        given(user.getFirstName()).willReturn(firstname);
        given(user.getLastName()).willReturn(null);
        given(user.getDescription()).willReturn(description);
        given(finders.findByName("input", "first_name")).willReturn(firstNameWB);
        given(finders.findByName("textarea", "description")).willReturn(descriptionWB);

        // When
        profilePage.updateMyProfile(user);

        // Then
        then(firstNameWB).should().sendKeys(firstname);
        then(descriptionWB).should().sendKeys(description);
        then(finders).should().clickByText("button", "Update Profile");
    }

    @Test
    public void Can_skip_update_description() {
        final User user = mock(User.class);
        final WebElement firstNameWB = mock(WebElement.class);
        final WebElement lastNameWB = mock(WebElement.class);
        final String firstname = someString();
        final String lastname = someString();

        // Given
        given(user.getFirstName()).willReturn(firstname);
        given(user.getLastName()).willReturn(lastname);
        given(user.getDescription()).willReturn(null);
        given(finders.findByName("input", "first_name")).willReturn(firstNameWB);
        given(finders.findByName("input", "last_name")).willReturn(lastNameWB);

        // When
        profilePage.updateMyProfile(user);

        // Then
        then(firstNameWB).should().sendKeys(firstname);
        then(lastNameWB).should().sendKeys(lastname);
        then(finders).should().clickByText("button", "Update Profile");
    }

    @Test
    public void Can_delete_my_profile_image() {

        // Given

        // When
        profilePage.deleteMyProfileImage();

        // Then
        then(finders).should().clickByText("button", "Delete Image");
        then(finders).should().waitById("confirmDeleteButton");
        then(finders).should().clickById("confirmDeleteButton");
        then(finders).should().refresh();
    }

    @Test
    public void Can_upload_a_profile_image() throws IOException {

        final Cookie cookie = mock(Cookie.class);
        final String cookieValue = someString();

        // Given
        given(finders.getCookie("session")).willReturn(cookie);
        given(cookie.getValue()).willReturn(cookieValue);
        // When
        profilePage.updateMyProfileImage();

        // Then
        then(finders).should().waitById("profile-image");
        then(hClient).should().uploadProfileImage(cookieValue);
        then(finders).should().refresh();
    }
}