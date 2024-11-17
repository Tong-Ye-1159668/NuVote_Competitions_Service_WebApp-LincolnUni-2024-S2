package lu.p2.steps;

import lu.p2.factories.UserFactory;
import lu.p2.holders.UserHolder;
import lu.p2.models.User;
import lu.p2.pages.DashboardPage;
import lu.p2.pages.HomePage;
import lu.p2.pages.ProfilePage;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

public class ProfileStepsTest {

    private HomePage homePage;
    private DashboardPage dashboardPage;
    private ProfilePage profilePage;
    private UserHolder userHolder;
    private UserFactory userFactory;
    private ProfileSteps profileSteps;

    @Before
    public void setUp() {
        homePage = mock(HomePage.class);
        dashboardPage = mock(DashboardPage.class);
        profilePage = mock(ProfilePage.class);
        userHolder = mock(UserHolder.class);
        userFactory = mock(UserFactory.class);
        profileSteps = new ProfileSteps(homePage, dashboardPage, profilePage, userHolder, userFactory);
    }

    @Test
    public void Can_access_the_profile_page() {
        // Given

        // When
        profileSteps.iAccessTheProfilePage();

        // Then
        then(homePage).should().visit();
        then(homePage).should().goToMyDashboardPage();
    }

    @Test
    public void Can_update_my_profile_details() {

        final User user = mock(User.class);
        final User userWithNewDetails = mock(User.class);

        // Given
        given(userHolder.get()).willReturn(user);
        given(userFactory.createNewProfileDetails(user)).willReturn(userWithNewDetails);

        // When
        profileSteps.iUpdateMyProfileDetails();

        // Then
        then(dashboardPage).should().clickEditBtn();
        then(profilePage).should().updateMyProfile(userWithNewDetails);
        then(userHolder).should().set(userWithNewDetails);
    }

    @Test
    public void Can_see_my_updated_profile_details() {

        final User user = mock(User.class);

        // Given
        given(userHolder.get()).willReturn(user);
        given(dashboardPage.verifyMyDetails(user)).willReturn(true);

        // When
        profileSteps.myProfileDetailsShouldBeUpdated();

        // Then
        then(dashboardPage).should().verifyMyDetails(user);
    }

    @Test
    public void Can_goto_profile_page() {
        // Given

        // When
        profileSteps.iNavigateToTheProfilePage();

        // Then
        then(homePage).should().visit();
        then(homePage).should().goToMyDashboardPage();
    }

    @Test
    public void Can_verify_profile_details() {
        final User user = mock(User.class);

        // Given
        given(userHolder.get()).willReturn(user);
        given(dashboardPage.verifyMyDetails(user)).willReturn(true);

        // When
        profileSteps.iViewMyProfileDetails();

        // Then
        then(dashboardPage).should().verifyMyDetails(user);
    }

    @Test
    public void Can_delete_my_profile_details() {
        final User user = mock(User.class);
        final User modifiedUser = mock(User.class);

        // Given
        given(userHolder.get()).willReturn(user);
        given(userFactory.deleteProfileDetails(user)).willReturn(modifiedUser);

        // When
        profileSteps.iDeleteMyProfileDetails();

        // Then
        then(dashboardPage).should().clickEditBtn();
        then(profilePage).should().updateMyProfile(modifiedUser);
        then(userHolder).should().set(modifiedUser);
    }

    @Test
    public void Can_remove_my_profile_image() {
        // Given

        // When
        profileSteps.iRemoveMyProfileImage();

        // Then
        then(profilePage).should().deleteMyProfileImage();
    }

    @Test
    public void Can_check_my_profile_image_is_updated() {
        // Given
        given(dashboardPage.isDefaultProfileImage()).willReturn(FALSE);

        // When
        profileSteps.myProfileImageIsUpdated();

        // Then
        then(dashboardPage).should().isDefaultProfileImage();
    }

    @Test
    public void Can_check_my_profile_image_is_removed() {
        // Given
        given(dashboardPage.isDefaultProfileImage()).willReturn(TRUE);

        // When
        profileSteps.myProfileImageIsRemoved();

        // Then
        then(dashboardPage).should().isDefaultProfileImage();
    }

    @Test
    public void Can_update_my_profile_image() throws IOException {
        // Given

        // When
        profileSteps.iUpdateMyProfileImage();

        // Then
        then(dashboardPage).should().clickEditBtn();
        then(profilePage).should().updateMyProfileImage();
    }
}