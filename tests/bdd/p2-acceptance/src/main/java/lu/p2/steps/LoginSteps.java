package lu.p2.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lu.p2.factories.UserFactory;
import lu.p2.holders.UserHolder;
import lu.p2.models.User;
import lu.p2.pages.HomePage;
import lu.p2.pages.LoginPage;
import lu.p2.pages.ProfilePage;
import lu.p2.pages.RegisterPage;

import static java.lang.Boolean.TRUE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class LoginSteps {
    private final HomePage homePage;
    private final RegisterPage registerPage;
    private final LoginPage loginPage;
    private final ProfilePage profilePage;
    private final UserFactory userFactory;
    private final UserHolder userHolder;

    public LoginSteps(final HomePage homePage, final RegisterPage registerPage, final LoginPage loginPage, final ProfilePage profilePage, final UserFactory userFactory, final UserHolder userHolder) {
        this.homePage = homePage;
        this.registerPage = registerPage;
        this.loginPage = loginPage;
        this.profilePage = profilePage;
        this.userFactory = userFactory;
        this.userHolder = userHolder;
    }

    @When("I log in")
    public void iLogIn() {
        loginPage.login(userHolder.get());
    }

    @Given("I am on the registration page")
    public void iAmOnTheRegistrationPage() {
        homePage.visit();
        homePage.clickRegisterBtn();
    }

    @When("I provide my details to register")
    public void iProvideMyDetailsToRegister() {
        final User user = userFactory.createAUser();
        registerPage.registerAUser(user);
        userHolder.set(user);
    }

    @Then("I should be redirected to my account as a logged-in user")
    public void iShouldBeRedirectedToMyAccountAsALoggedInUser() {
        homePage.findUserMenu();
    }

    @And("I should see a message confirming successful registration")
    public void iShouldSeeAMessageConfirmingSuccessfulRegistration() {
        homePage.findRegisterSuccessfullyMsg();
    }

    @And("I have a valid username and password")
    public void iHaveAValidUsernameAndPassword() {
        final User user = userFactory.getAUser();
        userHolder.set(user);
    }

    @Given("I am on the login page")
    public void iAmOnTheLoginPage() {
        homePage.visit();
        homePage.clickLoginBtn();
    }

    @When("I submit invalid information")
    public void iSubmitInvalidInformation() {
        registerPage.submitInvalidInfo();
    }

    @When("I submit invalid credentials")
    public void iSubmitInvalidCredentials() {
        loginPage.submitInvalidCredentials();
    }

    @Then("I should see an error message indicating the invalid data on the registration page")
    public void iShouldSeeAnErrorMessageIndicatingTheInvalidDataOnTheRegistrationPage() {
        assertThat(registerPage.seeAllErrorFeedbacks(), is(TRUE));
    }

    @Then("I should see an error message indicating the invalid data on the login page")
    public void iShouldSeeAnErrorMessageIndicatingTheInvalidDataOnTheLoginPage() {
        assertThat(loginPage.seeAllErrorFeedbacks(), is(TRUE));
    }

    @Given("I am logged in as a new user")
    public void iAmLoggedInAsANewUser() {
        homePage.visit();
        homePage.clickRegisterBtn();
        final User user = userFactory.createAUser();
        registerPage.registerAUser(user);
        homePage.goToMyDashboardPage();
        profilePage.clickEditBtn();
        profilePage.updateMyProfile(user);
        userHolder.set(homePage.updateUserId(user));
        homePage.visit();
    }

    @When("I log out")
    public void iLogOut() {
        homePage.logout();
    }

    @Then("I am logged out successfully")
    public void iAmLoggedOutSuccessfully() {
        assertThat(homePage.iAmAGuestUser(), is(TRUE));
    }

    @Given("I am logged in as a site admin")
    public void iAmLoggedInAsASiteAdmin() {
        loginPage.login(userFactory.getAnAdminUser());
    }

    @Then("I should see the login page")
    public void iShouldSeeTheLoginPage() {
        loginPage.isLoginPage();
    }
}
