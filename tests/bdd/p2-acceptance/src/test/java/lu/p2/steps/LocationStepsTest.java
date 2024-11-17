package lu.p2.steps;

import lu.p2.factories.UserFactory;
import lu.p2.holders.LocationHolder;
import lu.p2.holders.UserHolder;
import lu.p2.pages.DashboardPage;
import lu.p2.pages.HomePage;
import lu.p2.pages.LoginPage;
import lu.p2.pages.ProfilePage;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class LocationStepsTest {

    private HomePage homePage;
    private DashboardPage dashboardPage;
    private ProfilePage profilePage;
    private LocationHolder locationHolder;
    private UserHolder userHolder;
    private LoginPage loginPage;
    private UserFactory userFactory;
    private LocationSteps locationSteps;

    @Before
    public void setUp() {
        homePage = mock(HomePage.class);
        dashboardPage = mock(DashboardPage.class);
        profilePage = mock(ProfilePage.class);
        locationHolder = mock(LocationHolder.class);
        userHolder = mock(UserHolder.class);
        loginPage = mock(LoginPage.class);
        userFactory = mock(UserFactory.class);
        locationSteps = new LocationSteps(homePage, dashboardPage, profilePage, loginPage, locationHolder, userHolder, userFactory);
    }

    @Test
    public void Can_update_my_location() {
        // Given

        // When
        locationSteps.iUpdateMyLocationInProfile();

        // Then
    }
}