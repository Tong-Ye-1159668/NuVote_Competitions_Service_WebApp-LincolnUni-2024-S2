package lu.p2.steps;

import lu.p2.factories.UserFactory;
import lu.p2.holders.UserHolder;
import lu.p2.models.User;
import lu.p2.pages.HomePage;
import lu.p2.pages.LoginPage;
import lu.p2.pages.ProfilePage;
import lu.p2.pages.RegisterPage;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

public class LoginStepsTest {

    private HomePage homePage;
    private RegisterPage registerPage;
    private LoginPage loginPage;
    private UserFactory userFactory;
    private UserHolder userHolder;
    private ProfilePage profilePage;
    private LoginSteps loginSteps;


    @Before
    public void setUp() {
        homePage = mock(HomePage.class);
        registerPage = mock(RegisterPage.class);
        loginPage = mock(LoginPage.class);
        profilePage = mock(ProfilePage.class);
        userFactory = mock(UserFactory.class);
        userHolder = mock(UserHolder.class);
        loginSteps = new LoginSteps(homePage, registerPage, loginPage, profilePage, userFactory, userHolder);
    }

    @Test
    public void Can_see_the_register_page() {
        // Given

        // When
        loginSteps.iAmOnTheRegistrationPage();

        // Then
        then(homePage).should().visit();
        then(homePage).should().clickRegisterBtn();
    }

    @Test
    public void Can_register_a_user_with_details() {
        final User user = mock(User.class);

        // Given
        given(userFactory.createAUser()).willReturn(user);

        // When
        loginSteps.iProvideMyDetailsToRegister();

        // Then
        then(registerPage).should().registerAUser(user);
        then(userHolder).should().set(user);
    }

    @Test
    public void Can_see_registered_successfully_message() {
        // Given

        // When
        loginSteps.iShouldSeeAMessageConfirmingSuccessfulRegistration();

        // Then
        then(homePage).should().findRegisterSuccessfullyMsg();
    }

    @Test
    public void Can_be_redirected_to_login() {
        // Given

        // When
        loginSteps.iShouldBeRedirectedToMyAccountAsALoggedInUser();

        // Then
        then(homePage).should().findUserMenu();
    }

    @Test
    public void Can_see_the_login_page() {
        // Given

        // When
        loginSteps.iAmOnTheLoginPage();

        // Then
        then(homePage).should().visit();
        then(homePage).should().clickLoginBtn();
    }

    @Test
    public void Can_get_a_user() {
        final User user = mock(User.class);

        // Given
        given(userFactory.getAUser()).willReturn(user);

        // When
        loginSteps.iHaveAValidUsernameAndPassword();

        // Then
        then(userHolder).should().set(user);
    }

    @Test
    public void Can_log_in_with_a_user() {

        final User user = mock(User.class);

        // Given
        given(userHolder.get()).willReturn(user);

        // When
        loginSteps.iLogIn();

        // Then
        then(loginPage).should().login(user);
    }

    @Test
    public void Can_submit_invalid_info() {
        // Given


        // When
        loginSteps.iSubmitInvalidInformation();

        // Then
        then(registerPage).should().submitInvalidInfo();
    }

    @Test
    public void Can_login_with_invalid_info() {
        // Given


        // When
        loginSteps.iSubmitInvalidCredentials();

        // Then
        then(loginPage).should().submitInvalidCredentials();
    }

    @Test
    public void Can_see_error_feedback_on_register_page() {
        // Given
        given(registerPage.seeAllErrorFeedbacks()).willReturn(true);

        // When
        loginSteps.iShouldSeeAnErrorMessageIndicatingTheInvalidDataOnTheRegistrationPage();

        // Then
        then(registerPage).should().seeAllErrorFeedbacks();
    }

    @Test
    public void Can_see_error_feedback_on_login_page() {
        // Given
        given(loginPage.seeAllErrorFeedbacks()).willReturn(true);

        // When
        loginSteps.iShouldSeeAnErrorMessageIndicatingTheInvalidDataOnTheLoginPage();

        // Then
        then(loginPage).should().seeAllErrorFeedbacks();
    }

    @Test
    public void Can_log_in_as_a_new_user() {

        final User user = mock(User.class);
        final User updatedUser = mock(User.class);

        // Given
        given(userFactory.createAUser()).willReturn(user);
        given(homePage.updateUserId(user)).willReturn(updatedUser);

        // When
        loginSteps.iAmLoggedInAsANewUser();

        // Then
        then(homePage).should(times(2)).visit();
        then(homePage).should().clickRegisterBtn();
        then(registerPage).should().registerAUser(user);
        then(homePage).should().goToMyDashboardPage();
        then(profilePage).should().clickEditBtn();
        then(profilePage).should().updateMyProfile(user);
        then(homePage).should().updateUserId(user);
        then(userHolder).should().set(updatedUser);
    }

    @Test
    public void Can_logout() {
        // Given

        // When
        loginSteps.iLogOut();

        // Then
        then(homePage).should().logout();
    }

    @Test
    public void Can_verify_I_am_a_guest() {
        // Given
        given(homePage.iAmAGuestUser()).willReturn(true);

        // When
        loginSteps.iAmLoggedOutSuccessfully();

        // Then
    }

    @Test
    public void Can_login_as_site_admin() {
        final User user = mock(User.class);

        // Given
        given(userFactory.getAnAdminUser()).willReturn(user);

        // When
        loginSteps.iAmLoggedInAsASiteAdmin();

        // Then
        then(loginPage).should().login(user);
    }

    @Test
    public void Can_check_the_login_page() {
        // Given

        // When
        loginSteps.iShouldSeeTheLoginPage();

        // Then
        then(loginPage).should().isLoginPage();
    }
}