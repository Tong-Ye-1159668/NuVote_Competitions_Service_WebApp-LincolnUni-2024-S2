package lu.p2.steps;

import lu.p2.holders.UserHolder;
import lu.p2.models.User;
import lu.p2.pages.DashboardPage;
import lu.p2.pages.HomePage;
import lu.p2.pages.LoginPage;
import lu.p2.pages.ProfilePage;
import lu.p2.pages.UserPage;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class UserStepsTest {

    private UserPage userPage;
    private UserHolder userHolder;
    private HomePage homePage;
    private ProfilePage profilePage;
    private LoginPage loginPage;
    private DashboardPage dashboardPage;
    private UserSteps userSteps;

    @Before
    public void setUp() {
        userPage = mock(UserPage.class);
        userHolder = mock(UserHolder.class);
        homePage = mock(HomePage.class);
        profilePage = mock(ProfilePage.class);
        loginPage = mock(LoginPage.class);
        dashboardPage = mock(DashboardPage.class);
        userSteps = new UserSteps(userPage, userHolder, homePage, profilePage, loginPage, dashboardPage);
    }

    @Test
    public void Can_check_user_role() {
        final String role = someString();
        final User user = mock(User.class);

        // Given
        given(userHolder.get()).willReturn(user);
        given(homePage.checkMyRole(role)).willReturn(true);
        given(dashboardPage.checkMyRole(role)).willReturn(true);

        // When
        userSteps.theUserIsPromotedToTheSite(role);

        // Then
        then(homePage).should().logout();
        then(loginPage).should().login(user);
        then(homePage).should().goToMyDashboardPage();
        then(homePage).should().checkMyRole(role);
        then(dashboardPage).should().checkMyRole(role);
    }

    @Test
    public void Can_assign_role_to_voter() {
        final String role = someString();
        final User user = mock(User.class);

        // Given
        given(userPage.updateUserRole("Voter", role)).willReturn(user);

        // When
        userSteps.iAssignASiteToTheVoterUser(role);

        // Then
        then(userPage).should().visit();
        then(userHolder).should().set(user);
    }

    @Test
    public void Can_assign_role_to_site_roles() {
        final String role = someString();
        final User user = mock(User.class);

        // Given
        given(userHolder.get()).willReturn(user);

        // When
        userSteps.iAssignASiteToThePowerUser(role);

        // Then
        then(userPage).should().visit();
        then(userPage).should().updateUserRole(user, role);
    }

    @Test
    public void Can_access_as_a_guest() {
        // Given

        // When
        userSteps.iAmAGuest();

        // Then
        then(homePage).should().logoutUser();
        then(homePage).should().visit();
    }
}