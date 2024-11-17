package lu.p2.selenium;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.MockedStatic;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class InternalFindersTest {

    private List<MockedStatic<Bys>> mocks;
    private Selects selects;
    private InternalFinders finders;

    @Before
    public void setUp() {
        mocks = List.of(mockStatic(Bys.class));
        selects = mock(Selects.class);
        finders = new InternalFinders(selects);
    }


    @After
    public void tearDown() {
        mocks.forEach(MockedStatic::close);
    }

    @Test
    public void Can_find_an_element_by_an_id() {

        final SearchContext parent = mock(SearchContext.class);
        final String id = someString();

        final By byId = mock(By.class);
        final WebElement expected = mock(WebElement.class);

        // Given
        given(Bys.id(id)).willReturn(byId);
        given(parent.findElement(byId)).willReturn(expected);

        // When
        final WebElement actual = finders.findById(parent, id);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_find_an_element_by_an_id_that_contains_some_text() {

        final SearchContext parent = mock(SearchContext.class);
        final String text = someString();

        final By byIdThatContains = mock(By.class);
        final WebElement expected = mock(WebElement.class);

        // Given
        given(Bys.idThatContains(text)).willReturn(byIdThatContains);
        given(parent.findElement(byIdThatContains)).willReturn(expected);

        // When
        final WebElement actual = finders.findByIdThatContains(parent, text);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_find_an_element_by_a_class_name() {

        final SearchContext parent = mock(SearchContext.class);
        final String className = someString();

        final By byText = mock(By.class);
        final WebElement expected = mock(WebElement.class);

        // Given
        given(Bys.className(className)).willReturn(byText);
        given(parent.findElement(byText)).willReturn(expected);

        // When
        final WebElement actual = finders.findByClassName(parent, className);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void Can_find_some_elements_by_their_class_name() {

        final SearchContext parent = mock(SearchContext.class);
        final String className = someString();

        final By byText = mock(By.class);
        final List<WebElement> expected = mock(List.class);

        // Given
        given(Bys.className(className)).willReturn(byText);
        given(parent.findElements(byText)).willReturn(expected);

        // When
        final List<WebElement> actual = finders.findAllByClassName(parent, className);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_find_an_element_by_its_text() {

        final SearchContext parent = mock(SearchContext.class);
        final String tag = someString();
        final String text = someString();

        final By byText = mock(By.class);
        final WebElement expected = mock(WebElement.class);

        // Given
        given(Bys.text(tag, text)).willReturn(byText);
        given(parent.findElement(byText)).willReturn(expected);

        // When
        final WebElement actual = finders.findByText(parent, tag, text);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_find_all_elements_by_its_text() {

        final SearchContext parent = mock(SearchContext.class);
        final String tag = someString();
        final String text = someString();

        final By byText = mock(By.class);
        final List<WebElement> expected = mock(List.class);

        // Given
        given(Bys.text(tag, text)).willReturn(byText);
        given(parent.findElements(byText)).willReturn(expected);

        // When
        final List<WebElement> actual = finders.findAllByText(parent, tag, text);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_find_a_parent_element_by_a_class_name() {

        final String tag = someString();
        final String className = someString();

        final By byParent = mock(By.class);
        final SearchContext element = mock(SearchContext.class);

        final WebElement expected = mock(WebElement.class);

        // Given
        given(Bys.parentClassName(tag, className)).willReturn(byParent);
        given(element.findElement(byParent)).willReturn(expected);

        // When
        final WebElement actual = finders.findParentByClassName(element, tag, className);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_find_an_element_by_its_name() {

        final SearchContext parent = mock(SearchContext.class);
        final String tag = someString();
        final String name = someString();

        final By byName = mock(By.class);

        final WebElement expected = mock(WebElement.class);

        // Given
        given(Bys.name(tag, name)).willReturn(byName);
        given(parent.findElement(byName)).willReturn(expected);

        // When
        final WebElement actual = finders.findByName(parent, tag, name);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_find_an_element_by_its_label() {

        final SearchContext parent = mock(SearchContext.class);
        final String name = someString();

        final By byLabel = mock(By.class);
        final WebElement label = mock(WebElement.class);
        final String id = someString();
        final By byId = mock(By.class);

        final WebElement expected = mock(WebElement.class);

        // Given
        given(Bys.label(name)).willReturn(byLabel);
        given(parent.findElement(byLabel)).willReturn(label);
        given(label.getAttribute("for")).willReturn(id);
        given(Bys.id(id)).willReturn(byId);
        given(parent.findElement(byId)).willReturn(expected);

        // When
        final WebElement actual = finders.findByLabel(parent, name);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_find_an_element_by_its_value() {

        final SearchContext parent = mock(SearchContext.class);
        final String value = someString();

        final By byValue = mock(By.class);

        final WebElement expected = mock(WebElement.class);

        // Given
        given(Bys.value(value)).willReturn(byValue);
        given(parent.findElement(byValue)).willReturn(expected);

        // When
        final WebElement actual = finders.findByValue(parent, value);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_choose_an_element() {

        final WebElement element = mock(WebElement.class);

        // Given
        given(element.isSelected()).willReturn(FALSE, FALSE, TRUE);

        // When
        finders.choose(element);

        // Then
        then(element).should(times(3)).click();
    }

    @Test
    public void Can_enter_text_into_an_element() {

        // Given
        final WebElement element = mock(WebElement.class);
        final String text = someString();

        // When
        finders.setText(element, text);

        // Then
        final InOrder order = inOrder(element);
        order.verify(element).clear();
        order.verify(element).sendKeys(text);
    }

    @Test
    public void Can_enter_no_text_into_an_element() {

        // Given
        final WebElement element = mock(WebElement.class);

        // When
        finders.setText(element, null);

        // Then
        final InOrder order = inOrder(element);
        order.verify(element).clear();
        order.verify(element).sendKeys("");
    }

    @Test
    public void Can_select_an_option() {

        final WebElement element = mock(WebElement.class);
        final String option = someString();

        final Select select = mock(Select.class);

        // Given
        given(selects.create(element)).willReturn(select);

        // When
        finders.select(element, option);

        // Then
        then(select).should().selectByVisibleText(option);
    }

    @Test
    public void Can_find_by_a_tag_name() {

        final WebElement element = mock(WebElement.class);
        final String tagName = someString();

        final By byTagName = mock(By.class);

        final WebElement expected = mock(WebElement.class);

        // Given
        given(Bys.tagName(tagName)).willReturn(byTagName);
        given(element.findElement(byTagName)).willReturn(expected);

        // When
        final WebElement actual = finders.findByTagName(element, tagName);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_find_by_an_attribute() {

        final WebElement element = mock(WebElement.class);
        final String tagName = someString();
        final String name = someString();
        final String value = someString();

        final By byAttributeName = mock(By.class);

        final WebElement expected = mock(WebElement.class);

        // Given
        given(Bys.attribute(tagName, name, value)).willReturn(byAttributeName);
        given(element.findElement(byAttributeName)).willReturn(expected);

        // When
        final WebElement actual = finders.findByAttribute(element, tagName, name, value);

        // Then
        assertThat(actual, is(expected));
    }
}