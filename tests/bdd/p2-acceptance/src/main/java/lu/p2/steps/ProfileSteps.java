package lu.p2.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lu.p2.factories.UserFactory;
import lu.p2.holders.UserHolder;
import lu.p2.models.User;
import lu.p2.pages.DashboardPage;
import lu.p2.pages.HomePage;
import lu.p2.pages.ProfilePage;

import java.io.IOException;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ProfileSteps {
    private final HomePage homePage;
    private final DashboardPage dashboardPage;
    private final ProfilePage profilePage;
    private final UserHolder userHolder;
    private final UserFactory userFactory;

    public ProfileSteps(final HomePage homePage, final DashboardPage dashboardPage, final ProfilePage profilePage, final UserHolder userHolder, final UserFactory userFactory) {
        this.homePage = homePage;
        this.dashboardPage = dashboardPage;
        this.profilePage = profilePage;
        this.userHolder = userHolder;
        this.userFactory = userFactory;
    }

    @And("I access the profile page")
    public void iAccessTheProfilePage() {
        homePage.visit();
        homePage.goToMyDashboardPage();
    }

    @When("I update my profile details")
    public void iUpdateMyProfileDetails() {
        final User user = userFactory.createNewProfileDetails(userHolder.get());
        dashboardPage.clickEditBtn();
        profilePage.updateMyProfile(user);
        userHolder.set(user);
    }

    @Then("My profile details should be updated")
    public void myProfileDetailsShouldBeUpdated() {
        final User user = userHolder.get();
        final boolean actual = dashboardPage.verifyMyDetails(user);
        assertThat(actual, is(TRUE));
    }

    @And("I navigate to the profile page")
    public void iNavigateToTheProfilePage() {
        homePage.visit();
        homePage.goToMyDashboardPage();
    }

    @When("I view my profile details")
    @Then("my profile details should be successfully updated")
    @Then("I should see my current profile information")
    @Then("my profile should be removed")
    public void iViewMyProfileDetails() {
        assertThat(dashboardPage.verifyMyDetails(userHolder.get()), is(TRUE));
    }

    @When("I delete my profile details")
    public void iDeleteMyProfileDetails() {
        final User user = userFactory.deleteProfileDetails(userHolder.get());
        dashboardPage.clickEditBtn();
        profilePage.updateMyProfile(user);
        userHolder.set(user);
    }

    @When("I remove my profile image")
    public void iRemoveMyProfileImage() {
        profilePage.deleteMyProfileImage();
    }

    @Then("my profile image is updated")
    public void myProfileImageIsUpdated() {
        assertThat(dashboardPage.isDefaultProfileImage(), is(FALSE));
    }

    @Then("my profile image is removed")
    public void myProfileImageIsRemoved() {
        assertThat(dashboardPage.isDefaultProfileImage(), is(TRUE));
    }

    @When("I update my profile image")
    @And("I have a profile image")
    public void iUpdateMyProfileImage() throws IOException {
        dashboardPage.clickEditBtn();
        profilePage.updateMyProfileImage();
    }
}
