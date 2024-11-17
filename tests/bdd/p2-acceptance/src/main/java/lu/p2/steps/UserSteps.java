package lu.p2.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lu.p2.holders.UserHolder;
import lu.p2.pages.DashboardPage;
import lu.p2.pages.HomePage;
import lu.p2.pages.LoginPage;
import lu.p2.pages.ProfilePage;
import lu.p2.pages.UserPage;

import static java.lang.Boolean.TRUE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class UserSteps {
    private final UserPage userPage;
    private final UserHolder userHolder;
    private final HomePage homePage;
    private final ProfilePage profilePage;
    private final LoginPage loginPage;
    private final DashboardPage dashboardPage;

    public UserSteps(final UserPage userPage, final UserHolder userHolder, final HomePage HomePage, final ProfilePage profilePage, final LoginPage loginPage, final DashboardPage dashboardPage) {
        this.userPage = userPage;
        this.userHolder = userHolder;
        homePage = HomePage;
        this.profilePage = profilePage;
        this.loginPage = loginPage;
        this.dashboardPage = dashboardPage;
    }

    @Then("the user is promoted to the site {string}")
    @Then("the user is demoted to the site {string}")
    public void theUserIsPromotedToTheSite(String role) {
        homePage.logout();
        loginPage.login(userHolder.get());
        homePage.goToMyDashboardPage();
        assertThat(homePage.checkMyRole(role), is(TRUE));
        assertThat(dashboardPage.checkMyRole(role), is(TRUE));
    }

    @When("I assign a site {string} to the voter user")
    @And("there is a power user with {string}")
    public void iAssignASiteToTheVoterUser(String role) {
        userPage.visit();
        userHolder.set(userPage.updateUserRole("Voter", role));
    }

    @When("I assign a site {string} to the power user")
    public void iAssignASiteToThePowerUser(String role) {
        userPage.visit();
        userPage.updateUserRole(userHolder.get(), role);
    }

    @Given("I am a guest")
    public void iAmAGuest() {
        homePage.logoutUser();
        homePage.visit();
    }
}
