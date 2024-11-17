package lu.p2.pages;

import lu.p2.factories.UserFactory;
import lu.p2.models.User;
import lu.p2.selenium.Bys;
import lu.p2.selenium.Finders;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class UserPageTest {

    private List<MockedStatic<?>> mocks;

    private Finders finders;
    private UserFactory userFactory;
    private UserPage userPage;

    @Before
    public void setUp() {
        mocks = List.of(mockStatic(Bys.class), mockStatic(ExpectedConditions.class));
        finders = mock(Finders.class);
        userFactory = mock(UserFactory.class);
        userPage = new UserPage(finders, userFactory);
    }

    @After
    public void tearDown() {
        mocks.forEach(MockedStatic::close);
    }

    @Test
    public void Can_visit() {
        // Given

        // When
        userPage.visit();

        // Then
        then(finders).should().clickByAttribute("a", "href", "/users/");
    }

    @Test
    public void Can_update_user_role() {
        //TODO
        final String userRole = someString();
        final WebElement roleWE = mock(WebElement.class);
        final WebElement userRoleOption = mock(WebElement.class);
        final WebElement tbWE = mock(WebElement.class);
        final WebElement rowWE = mock(WebElement.class);
        final WebElement userAWE = mock(WebElement.class);
        final WebElement emailWE = mock(WebElement.class);
        final String username = someAlphanumericString(5);
        final String email = someAlphanumericString(10);
        final User user = mock(User.class);

        final WebElement tableWE = mock(WebElement.class);
        final WebElement tableRowWE = mock(WebElement.class);
        final WebElement selectWE = mock(WebElement.class);
        final WebElement optionWE = mock(WebElement.class);
        final String role = someString();

        // Given
        given(finders.findById("role")).willReturn(roleWE);
        given(roleWE.findElement(Bys.text("option", userRole))).willReturn(userRoleOption);
        given(finders.findById("userTableBody")).willReturn(tbWE, tableWE);
        given(tbWE.findElements(Bys.tagName("tr"))).willReturn(List.of(rowWE));

        given(rowWE.findElement(Bys.tagName("a"))).willReturn(userAWE);
        given(userAWE.getText()).willReturn(username);
        given(rowWE.findElement(Bys.text("td", "@"))).willReturn(emailWE);
        given(emailWE.getText()).willReturn(email);
        given(userFactory.createAUser(any(), any())).willReturn(user);
//        given(user.getUsername()).willReturn(username);
//        given(user.getEmail()).willReturn(email);
//        given(finders.findById("userTableBody")).willReturn(tableWE);
        given(tableWE.findElements(Bys.tagName("tr"))).willReturn(List.of(tableRowWE));
        given(tableRowWE.findElement(Bys.tagName("select"))).willReturn(selectWE);
        given(selectWE.findElements(Bys.tagName("option"))).willReturn(List.of(optionWE));
        given(optionWE.getAttribute("value")).willReturn(role);

        // When
        final User actual = userPage.updateUserRole(userRole, role);

        // Then
        then(finders).should(times(2)).waitById("role");
        then(userRoleOption).should().click();
        then(finders).should(times(2)).waitByText("td", "1");
//        then(finders).should().setTextById("query", email);
        then(finders).should().waitNumberOfElementsToBeLessThan("tr", 3);
        assertThat(actual, is(user));
    }

    @Test
    public void Can_update_user_to_a_role() {
        final User user = mock(User.class);
        final String role = someString(5);
        final String email = someAlphanumericString(10);
        final WebElement tbWE = mock(WebElement.class);
        final WebElement userRowWE = mock(WebElement.class);
        final WebElement selectWE = mock(WebElement.class);
        final WebElement optionWE = mock(WebElement.class);
        final WebElement optionWE2 = mock(WebElement.class);

        // Given
        given(user.getEmail()).willReturn(email);
        given(finders.findById("userTableBody")).willReturn(tbWE);
        given(tbWE.findElements(Bys.tagName("tr"))).willReturn(List.of(userRowWE));
        given(userRowWE.findElement(Bys.tagName("select"))).willReturn(selectWE);
        given(selectWE.findElements(Bys.tagName("option"))).willReturn(List.of(optionWE, optionWE2));
        given(optionWE.getAttribute("value")).willReturn(role);
        given(optionWE2.getAttribute("value")).willReturn(someAlphanumericString());

        // When
        final User actual = userPage.updateUserRole(user, role);

        // Then
        then(finders).should().waitById("role");
        then(finders).should().waitByText("td", "1");
        then(finders).should().setTextById("query", email);
        then(finders).should().waitNumberOfElementsToBeLessThan("tr", 3);
        then(optionWE).should().click();

    }
}