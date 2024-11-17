package lu.p2.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lu.p2.factories.ThemeApplicationFactory;
import lu.p2.factories.UserFactory;
import lu.p2.holders.ThemeApplicationHolder;
import lu.p2.holders.UserHolder;
import lu.p2.models.User;
import lu.p2.pages.HomePage;
import lu.p2.pages.LoginPage;
import lu.p2.pages.ThemePage;

import static java.lang.Boolean.TRUE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ThemeSteps {
    private final UserFactory userFactory;
    private final LoginPage loginPage;
    private final ThemePage themePage;
    private final HomePage homePage;
    private final ThemeApplicationHolder themeApplicationHolder;
    private final UserHolder userHolder;
    private final ThemeApplicationFactory themeApplicationFactory;

    public ThemeSteps(final UserFactory userFactory, final LoginPage loginPage, final ThemePage themePage, final HomePage homePage, final ThemeApplicationHolder themeApplicationHolder, final UserHolder userHolder, final ThemeApplicationFactory themeApplicationFactory) {
        this.userFactory = userFactory;
        this.loginPage = loginPage;
        this.themePage = themePage;
        this.homePage = homePage;
        this.themeApplicationHolder = themeApplicationHolder;
        this.userHolder = userHolder;
        this.themeApplicationFactory = themeApplicationFactory;
    }

    @Given("I am logged in as a user")
    public void iAmLoggedInAsAUser() {
        loginPage.visit();
        final User user = userFactory.getAUser();
        loginPage.login(user);
        userHolder.set(user);
    }

    @When("I submit a theme application")
    public void iSubmitAThemeApplication() {
        themePage.visit();
        themeApplicationHolder.set(themePage.submitApplication(themeApplicationFactory.createAnApplication()));
    }

    @Then("the theme application is created")
    public void theThemeApplicationIsCreated() {
        themePage.displayTheApplicationAsPending(themeApplicationHolder.get());
    }

    @And("I should see the new theme application under my themes")
    public void iShouldSeeTheNewThemeApplicationUnderMyThemes() {
        themePage.visit();
        themePage.gotoMyProposals();
        assertThat(themePage.displayTheApplicationAsPending(themeApplicationHolder.get()), is(TRUE));
    }

    @And("the theme application has not been approved")
    public void theThemeApplicationHasNotBeenApproved() {
        themePage.visit();
        themePage.gotoMyProposals();
        assertThat(themePage.displayTheApplicationAsPending(themeApplicationHolder.get()), is(TRUE));
    }

    @When("I delete my theme application")
    public void iDeleteMyThemeApplication() {
        themePage.deleteMyApplication(themeApplicationHolder.get());
    }

    @Then("the theme application is deleted")
    public void theThemeApplicationIsDeleted() {
        assertThat(themePage.myThemeIsEmpty(), is(TRUE));
    }

    @When("the theme is approved by other admins")
    public void theThemeIsApprovedByOtherAdmins() {
        homePage.logout();
        loginPage.login(userFactory.getAnAdminUser());
        themePage.actOnTheApplication(themeApplicationHolder.get(), "Approve");
        homePage.logout();
        loginPage.login(userHolder.get());
        homePage.visit();
    }

    @Then("I am assigned as the theme admin for the theme")
    public void iAmAssignedAsTheThemeAdminForTheTheme() {
        themePage.visit();
        themePage.gotoMyTheme(themeApplicationHolder.get(), userHolder.get());
    }

    @Given("there is a pending applications")
    public void thereIsAPendingApplications() {
        loginPage.visit();
        final User user = userFactory.getAUser();
        loginPage.login(user);
        userHolder.set(user);
        themePage.visit();
        themeApplicationHolder.set(themePage.submitApplication(themeApplicationFactory.createAnApplication()));
        homePage.logout();
    }

    @When("I {string} an application")
    public void iAnApplication(String action) {
        themePage.actOnTheApplication(themeApplicationHolder.get(), action);
    }

    @Then("the application is marked as {string}")
    public void theApplicationIsMarkedAs(String status) {
        themePage.checkThemeStatus(themeApplicationHolder.get(), status);
    }

    @And("applicant should see the {string} theme")
    public void applicantShouldSeeTheTheme(String status) {
        homePage.logout();
        loginPage.login(userHolder.get());
        themePage.visit();
        themePage.gotoMyProposals();
        themePage.checkMyThemeStatus(themeApplicationHolder.get(), status);
    }

    @When("I assign a theme {string} to a standard user")
    public void iAssignAThemeToAStandardUser(String themeRole) {
        themePage.gotoMyTheme(themeApplicationHolder.get());
        final User user = themePage.updateUserRole(themeRole);
        userHolder.set(user);
    }

    @Then("the user is promoted to the {string}")
    public void theUserIsPromotedToThe(String themeRole) {
        homePage.logout();
        loginPage.login(userHolder.get());
        themePage.gotoMyTheme(themeApplicationHolder.get(), themeRole);
    }

    @And("I have submitted a theme application with location enabled")
    public void iHaveSubmittedAThemeApplicationWithLocationEnabled() {
        themePage.visit();
        themeApplicationHolder.set(themePage.submitApplication(themeApplicationFactory.createAnApplicationWithLocation()));
    }
}
