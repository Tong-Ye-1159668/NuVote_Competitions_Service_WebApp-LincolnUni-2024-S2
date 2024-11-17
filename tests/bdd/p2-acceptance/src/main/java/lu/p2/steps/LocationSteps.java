package lu.p2.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lu.p2.factories.UserFactory;
import lu.p2.holders.LocationHolder;
import lu.p2.holders.UserHolder;
import lu.p2.models.Location;
import lu.p2.pages.DashboardPage;
import lu.p2.pages.HomePage;
import lu.p2.pages.LoginPage;
import lu.p2.pages.ProfilePage;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class LocationSteps {

    private final HomePage homePage;
    private final DashboardPage dashboardPage;
    private final ProfilePage profilePage;
    private final LoginPage loginPage;
    private final LocationHolder locationHolder;
    private final UserHolder userHolder;
    private final UserFactory userFactory;

    public LocationSteps(final HomePage homePage, final DashboardPage dashboardPage, final ProfilePage profilePage, final LoginPage loginPage, final LocationHolder locationHolder, final UserHolder userHolder, final UserFactory userFactory) {
        this.homePage = homePage;
        this.dashboardPage = dashboardPage;
        this.profilePage = profilePage;
        this.loginPage = loginPage;
        this.locationHolder = locationHolder;
        this.userHolder = userHolder;
        this.userFactory = userFactory;
    }

    @When("I update my location in profile")
    @And("I have a valid location in profile")
    @And("I have a valid location")
    public void iUpdateMyLocationInProfile() {
        homePage.goToMyDashboardPage();
        dashboardPage.clickEditBtn();
        final Location location = profilePage.updateMyLocation();
        locationHolder.set(location);
    }

    @Then("my location should display on the map")
    public void myLocationShouldDisplayOnTheMap() {
        dashboardPage.checkMyLocation(locationHolder.get());
    }

    @When("I delete my location in profile")
    public void iDeleteMyLocationInProfile() {
        dashboardPage.clickEditBtn();
        profilePage.deleteMyLocation(locationHolder.get());
    }

    @Then("my location should not display on the map")
    public void myLocationShouldNotDisplayOnTheMap() {
        assertThat(TRUE, is(profilePage.mapIsNotDisplay()));
    }

    @And("other users can not see my location")
    public void otherUsersCanNotSeeMyLocation() {
        homePage.logoutUser();
        loginPage.login(userFactory.getAUser());
        assertThat(TRUE, is(profilePage.viewPublicProfileWithoutLocation(userHolder.get())));
    }

    @When("I share my location in profile")
    @When("I stop sharing my location in profile")
    public void iShareMyLocationInProfile() {
        dashboardPage.clickEditBtn();
        profilePage.shareLocation();
    }

    @And("other users can see my shared location")
    public void otherUsersCanSeeMySharedLocation() {
        homePage.logoutUser();
        loginPage.login(userFactory.getAUser());
        assertThat(FALSE, is(profilePage.viewPublicProfileWithoutLocation(userHolder.get())));
    }
}
