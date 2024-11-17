package lu.p2.steps;

import lu.p2.factories.ThemeApplicationFactory;
import lu.p2.factories.UserFactory;
import lu.p2.holders.ThemeApplicationHolder;
import lu.p2.holders.UserHolder;
import lu.p2.models.ThemeApplication;
import lu.p2.models.User;
import lu.p2.pages.HomePage;
import lu.p2.pages.LoginPage;
import lu.p2.pages.ThemePage;
import org.junit.Before;
import org.junit.Test;

import static java.lang.Boolean.TRUE;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class ThemeStepsTest {

    private UserFactory userFactory;
    private LoginPage loginPage;
    private ThemePage themePage;
    private HomePage homePage;
    private ThemeApplicationHolder themeApplicationHolder;
    private UserHolder userHolder;
    private ThemeApplicationFactory themeApplicationFactory;
    private ThemeSteps themeSteps;

    @Before
    public void setUp() {
        userFactory = mock(UserFactory.class);
        loginPage = mock(LoginPage.class);
        themePage = mock(ThemePage.class);
        homePage = mock(HomePage.class);
        themeApplicationHolder = mock(ThemeApplicationHolder.class);
        userHolder = mock(UserHolder.class);
        themeApplicationFactory = mock(ThemeApplicationFactory.class);
        themeSteps = new ThemeSteps(userFactory, loginPage, themePage, homePage, themeApplicationHolder, userHolder, themeApplicationFactory);
    }

    @Test
    public void Can_login_as_a_user() {

        final User user = mock(User.class);

        // Given
        given(userFactory.getAUser()).willReturn(user);


        // When
        themeSteps.iAmLoggedInAsAUser();

        // Then
        then(loginPage).should().visit();
        then(loginPage).should().login(user);
        then(userHolder).should().set(user);
    }

    @Test
    public void Can_submit_an_application() {
        final ThemeApplication themeApplication = mock(ThemeApplication.class);
        final ThemeApplication updatedApplication = mock(ThemeApplication.class);

        // Given
        given(themeApplicationFactory.createAnApplication()).willReturn(themeApplication);
        given(themePage.submitApplication(themeApplication)).willReturn(updatedApplication);
        // When
        themeSteps.iSubmitAThemeApplication();

        // Then
        then(themePage).should().visit();
        then(themePage).should().submitApplication(themeApplication);
        then(themeApplicationHolder).should().set(updatedApplication);
    }

    @Test
    public void Can_see_my_pending_application() {

        final ThemeApplication themeApplication = mock(ThemeApplication.class);

        // Given
        given(themeApplicationHolder.get()).willReturn(themeApplication);
        given(themePage.displayTheApplicationAsPending(themeApplication)).willReturn(TRUE);

        // When
        themeSteps.theThemeApplicationIsCreated();

        // Then
        then(themePage).should().displayTheApplicationAsPending(themeApplication);
    }

    @Test
    public void Can_see_my_applications() {

        final ThemeApplication themeApplication = mock(ThemeApplication.class);

        // Given
        given(themeApplicationHolder.get()).willReturn(themeApplication);
        given(themePage.displayTheApplicationAsPending(themeApplication)).willReturn(TRUE);

        // When
        themeSteps.iShouldSeeTheNewThemeApplicationUnderMyThemes();

        // Then
        then(themePage).should().visit();
        then(themePage).should().gotoMyProposals();
        then(themePage).should().displayTheApplicationAsPending(themeApplication);
    }

    @Test
    public void theThemeApplicationHasNotBeenApproved() {
        final ThemeApplication themeApplication = mock(ThemeApplication.class);

        // Given
        given(themeApplicationHolder.get()).willReturn(themeApplication);
        given(themePage.displayTheApplicationAsPending(themeApplication)).willReturn(TRUE);

        // When
        themeSteps.theThemeApplicationHasNotBeenApproved();

        // Then
        then(themePage).should().visit();
        then(themePage).should().gotoMyProposals();

    }

    @Test
    public void iDeleteMyThemeApplication() {
        final ThemeApplication themeApplication = mock(ThemeApplication.class);

        // Given
        given(themeApplicationHolder.get()).willReturn(themeApplication);

        // When
        themeSteps.iDeleteMyThemeApplication();

        // Then
        then(themePage).should().deleteMyApplication(themeApplication);
    }

    @Test
    public void theThemeApplicationIsDeleted() {

        // Given
        given(themePage.myThemeIsEmpty()).willReturn(TRUE);

        // When
        themeSteps.theThemeApplicationIsDeleted();

        // Then
    }

    @Test
    public void Can_approve_a_theme() {

        final User adminUser = mock(User.class);
        final User user = mock(User.class);
        final ThemeApplication themeApplication = mock(ThemeApplication.class);

        // Given
        given(userFactory.getAUser()).willReturn(adminUser);
        given(themeApplicationHolder.get()).willReturn(themeApplication);
        given(userHolder.get()).willReturn(user);

        // When
        themeSteps.theThemeIsApprovedByOtherAdmins();

        // Then
//        then(homePage).should().logout();
//        then(loginPage).should().login(adminUser);
        then(themePage).should().actOnTheApplication(themeApplication, "Approve");
//        then(homePage).should().logout();
//        then(loginPage).should().login(user);
    }

    @Test
    public void Can_goto_my_theme() {
        final ThemeApplication themeApplication = mock(ThemeApplication.class);
        final User user = mock(User.class);

        // Given
        given(themeApplicationHolder.get()).willReturn(themeApplication);
        given(userHolder.get()).willReturn(user);

        // When
        themeSteps.iAmAssignedAsTheThemeAdminForTheTheme();

        // Then
        then(themePage).should().visit();
        then(themePage).should().gotoMyTheme(themeApplication, user);
    }

    @Test
    public void Can_create_a_theme() {
        final User user = mock(User.class);
        final ThemeApplication themeApplication = mock(ThemeApplication.class);
        final ThemeApplication updatedApplication = mock(ThemeApplication.class);

        // Given
        given(userFactory.getAUser()).willReturn(user);
        given(themeApplicationFactory.createAnApplication()).willReturn(themeApplication);
        given(themePage.submitApplication(themeApplication)).willReturn(updatedApplication);
        // When
        themeSteps.thereIsAPendingApplications();

        // Then
        then(loginPage).should().visit();
        then(loginPage).should().login(user);
        then(userHolder).should().set(user);
        then(themePage).should().visit();
        then(themePage).should().submitApplication(themeApplication);
        then(themeApplicationHolder).should().set(updatedApplication);
        then(homePage).should().logout();
    }

    @Test
    public void Can_act_on_an_application() {
        final String action = someString();
        final ThemeApplication themeApplication = mock(ThemeApplication.class);

        // Given
        given(themeApplicationHolder.get()).willReturn(themeApplication);

        // When
        themeSteps.iAnApplication(action);

        // Then
        then(themePage).should().actOnTheApplication(themeApplication, action);
    }

    @Test
    public void Can_mark_on_an_application() {
        final String status = someString();
        final ThemeApplication themeApplication = mock(ThemeApplication.class);

        // Given
        given(themeApplicationHolder.get()).willReturn(themeApplication);

        // When
        themeSteps.theApplicationIsMarkedAs(status);

        // Then
        then(themePage).should().checkThemeStatus(themeApplication, status);
    }

    @Test
    public void Can_check_my_application() {
        final User user = mock(User.class);
        final String status = someString();
        final ThemeApplication themeApplication = mock(ThemeApplication.class);

        // Given
        given(userHolder.get()).willReturn(user);
        given(themeApplicationHolder.get()).willReturn(themeApplication);

        // When
        themeSteps.applicantShouldSeeTheTheme(status);

        // Then
        then(homePage).should().logout();
        then(loginPage).should().login(user);
        then(themePage).should().visit();
        then(themePage).should().gotoMyProposals();
        then(themePage).should().checkMyThemeStatus(themeApplication, status);
    }

    @Test
    public void Can_assign_a_role_to_user() {
        // Given
        final String themeRole = someString();
        final ThemeApplication themeApplication = mock(ThemeApplication.class);
        final User updatedUser = mock(User.class);

        given(themeApplicationHolder.get()).willReturn(themeApplication);
        given(themePage.updateUserRole(themeRole)).willReturn(updatedUser);

        // When
        themeSteps.iAssignAThemeToAStandardUser(themeRole);

        // Then
        then(themePage).should().gotoMyTheme(themeApplication);
        then(themePage).should().updateUserRole(themeRole);
        then(userHolder).should().set(updatedUser);
    }

    @Test
    public void Can_check_user_is_promoted_to_the_role() {
        // Given
        final String themeRole = someString();
        final ThemeApplication themeApplication = mock(ThemeApplication.class);
        final User user = mock(User.class);

        given(userHolder.get()).willReturn(user);
        given(themeApplicationHolder.get()).willReturn(themeApplication);


        // When
        themeSteps.theUserIsPromotedToThe(themeRole);

        // Then
        then(homePage).should().logout();
        then(loginPage).should().login(user);
        then(themePage).should().gotoMyTheme(themeApplication, themeRole);
    }

    @Test
    public void Can_submit_a_theme_application_with_location() {
        final ThemeApplication themeApplication = mock(ThemeApplication.class);
        final ThemeApplication updatedThemeApplication = mock(ThemeApplication.class);

        // Given
        given(themeApplicationFactory.createAnApplicationWithLocation()).willReturn(themeApplication);
        given(themePage.submitApplication(themeApplication)).willReturn(updatedThemeApplication);

        // When
        themeSteps.iHaveSubmittedAThemeApplicationWithLocationEnabled();

        // Then
        then(themePage).should().visit();
        then(themeApplicationHolder).should().set(updatedThemeApplication);
    }
}