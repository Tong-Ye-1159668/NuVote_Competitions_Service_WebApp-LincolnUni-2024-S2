package lu.p2.pages;

import lu.p2.factories.CharityFactory;
import lu.p2.models.Charity;
import lu.p2.selenium.Bys;
import lu.p2.selenium.Finders;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import static java.lang.Boolean.TRUE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class CharityPageTest {

    private Finders finders;
    private CharityFactory charityFactory;
    private CharityPage charityPage;

    @Before
    public void setUp() {
        finders = mock(Finders.class);
        charityFactory = mock(CharityFactory.class);
        charityPage = new CharityPage(finders, charityFactory);
    }

    @Test
    public void Can_create_a_charity() {
        final Charity charity = mock(Charity.class);
        final String charityName = someString();
        final String charityDescription = someString();
        final String image = someString();
        final String regNumber = someString();
        final String bankAcc = someString();
        final String irdNumber = someString();

        // Given
        given(charityFactory.create()).willReturn(charity);
        given(charity.getName()).willReturn(charityName);
        given(charity.getDescription()).willReturn(charityDescription);
        given(charity.getImage()).willReturn(image);
        given(charity.getRegNumber()).willReturn(regNumber);
        given(charity.getBankAccount()).willReturn(bankAcc);
        given(charity.getIrdNumber()).willReturn(irdNumber);

        // When
        final Charity actual = charityPage.createACharity();

        // Then
        then(finders).should().waitByText("a", "Start a\n" + "            Donation Drive");
        then(finders).should().clickByText("a", "Start a\n" + "            Donation Drive");
        then(finders).should().waitByText("button", "Submit your application");
        then(finders).should().setTextById("charity_name", charityName);
        then(finders).should().setTextById("charity_description", charityDescription);
        then(finders).should().setTextById("charity_image", image);
        then(finders).should().setTextById("reg_num", regNumber);
        then(finders).should().setTextById("bank_acc", bankAcc);
        then(finders).should().setTextById("ird_num", irdNumber);
        then(finders).should().clickByText("button", "Submit your application");
        assertThat(actual, is(charity));
    }

    @Test
    public void Can_act_on_an_application() {
        final Charity charity = mock(Charity.class);
        final String regNumber = someString();
        final WebElement webElement = mock(WebElement.class, RETURNS_DEEP_STUBS);
        final String approve = someString();
        final WebElement btnWE = mock(WebElement.class);
        final WebElement element = mock(WebElement.class);
        final WebElement e1Btn = mock(WebElement.class);

        // Given
        given(charity.getRegNumber()).willReturn(regNumber);
        given(finders.findByText("td", regNumber)).willReturn(webElement);
        given(webElement.findElement(Bys.parentClassName("tr", "table-custom-row"))
                .findElement(Bys.text("button", approve))).willReturn(btnWE);
        given(finders.findByClassName("modal-content")).willReturn(element);
        given(element.findElement(Bys.text("button", approve))).willReturn(e1Btn);


        // When
        charityPage.actOnTheApplication(charity, approve);

        // Then
        then(finders).should().clickByText("a", "Charities");
        then(finders).should().waitByText("h2", "Charity and Donation Application Management");
        then(btnWE).should().click();
        then(finders).should().waitByClassName("modal-content");
        then(e1Btn).should().click();
        then(finders).should().waitByText("div", "Charity approved successfully");
    }

    @Test
    public void Can_check_charity_is_created() {
        final Charity charity = mock(Charity.class);
        final String charityName = someString();
        final String charityDescription = someString();
        final String image = someString();
        final String regNumber = someString();
        final String bankAcc = someString();
        final String irdNumber = someString();
        final WebElement webElement = mock(WebElement.class);

        // Given
        given(charityFactory.create()).willReturn(charity);
        given(charity.getName()).willReturn(charityName);
        given(charity.getDescription()).willReturn(charityDescription);
        given(charity.getImage()).willReturn(image);
        given(charity.getRegNumber()).willReturn(regNumber);
        given(charity.getFormattedBankAccount()).willReturn(bankAcc);
        given(charity.getFormattedIrdNumber()).willReturn(irdNumber);
        given(finders.findByClassName("card-text")).willReturn(webElement);
        given(webElement.getText()).willReturn(charityDescription);

        // When
        final boolean actual = charityPage.charityIsCreatedSuccessfully(charity);

        // Then
        then(finders).should().waitByText("div", "Charity created successfully");
        then(finders).should().findByText("strong", charityName);
        then(finders).should().findByText("span", "Status: Pending");
        then(finders).should().findByText("li", regNumber);
        then(finders).should().findByText("li", bankAcc);
        then(finders).should().findByText("li",irdNumber);
        assertThat(actual, is(TRUE));
    }
}