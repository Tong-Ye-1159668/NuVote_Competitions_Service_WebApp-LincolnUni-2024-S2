package lu.p2.steps;

import lu.p2.factories.UserFactory;
import lu.p2.holders.CharityHolder;
import lu.p2.holders.ThemeApplicationHolder;
import lu.p2.holders.UserHolder;
import lu.p2.models.Charity;
import lu.p2.models.ThemeApplication;
import lu.p2.models.User;
import lu.p2.pages.CharityPage;
import lu.p2.pages.HomePage;
import lu.p2.pages.LoginPage;
import lu.p2.pages.ThemePage;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

public class CharityStepsTest {

    private CharityPage charityPage;
    private HomePage homePage;
    private LoginPage loginPage;
    private ThemePage themePage;
    private UserFactory userFactory;
    private UserHolder userHolder;
    private ThemeApplicationHolder themeApplicationHolder;
    private CharityHolder charityHolder;
    private CharitySteps charitySteps;

    @Before
    public void setUp() {
        charityPage = mock(CharityPage.class);
        homePage = mock(HomePage.class);
        loginPage = mock(LoginPage.class);
        themePage = mock(ThemePage.class);
        userFactory = mock(UserFactory.class);
        userHolder = mock(UserHolder.class);
        themeApplicationHolder = mock(ThemeApplicationHolder.class);
        charityHolder = mock(CharityHolder.class);
        charitySteps = new CharitySteps(charityPage, homePage, loginPage, themePage, userFactory, userHolder, themeApplicationHolder, charityHolder);
    }

    @Test
    public void Can_create_a_charity() {
        final ThemeApplication themeApplication = mock(ThemeApplication.class);
        final Charity charity = mock(Charity.class);

        // Given
        given(themeApplicationHolder.get()).willReturn(themeApplication);
        given(charityPage.createACharity()).willReturn(charity);

        // When
        charitySteps.iCreateACharity();

        // Then
        then(themePage).should().gotoMyTheme(themeApplication);
        then(charityHolder).should().set(charity);
    }

    @Test
    public void Can_approve_a_charity() {
        final User user = mock(User.class);
        final Charity charity = mock(Charity.class);
        final User user1 = mock(User.class);

        // Given
        given(userFactory.getAnAdminUser()).willReturn(user);
        given(charityHolder.get()).willReturn(charity);
        given(userHolder.get()).willReturn(user1);

        // When
        charitySteps.theCharityIsApprovedByOtherAdmins();

        // Then
        then(homePage).should(times(2)).logout();
        then(loginPage).should().login(user);
        then(charityPage).should().actOnTheApplication(charity, "Approve");
        then(loginPage).should().login(user1);
    }

    @Test
    public void Can_see_donation_button_on_homepage() {
        final Charity charity = mock(Charity.class);

        // Given
        given(charityHolder.get()).willReturn(charity);

        // When
        charitySteps.iCanSeeDonationButtonOnHomepage();

        // Then
        then(homePage).should().clickDonation(charity);
    }

    @Test
    public void Can_create_charity_successfully() {
        final Charity charity = mock(Charity.class);

        // Given
        given(charityHolder.get()).willReturn(charity);

        // When
        charitySteps.theCharityIsCreatedSuccessfully();

        // Then
        then(charityPage).should().charityIsCreatedSuccessfully(charity);
    }
}