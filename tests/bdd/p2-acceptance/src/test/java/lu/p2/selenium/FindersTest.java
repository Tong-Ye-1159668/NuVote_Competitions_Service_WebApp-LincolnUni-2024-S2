package lu.p2.selenium;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.openqa.selenium.WebDriver.Navigation;
import static org.openqa.selenium.WebDriver.Options;
import static shiver.me.timbers.data.random.RandomIntegers.somePositiveInteger;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class FindersTest {

    private List<MockedStatic<?>> mocks;
    private WebDriver driver;
    private InternalFinders internalFinders;
    private Finders finders;
    private WebDriverWait webDriverWait;

    @Before
    public void setUp() {
        mocks = List.of(mockStatic(Bys.class), mockStatic(ExpectedConditions.class));
        driver = mock(WebDriver.class);
        internalFinders = mock(InternalFinders.class);
        webDriverWait = mock(WebDriverWait.class);
        finders = new Finders(driver, internalFinders, webDriverWait);
    }

    @After
    public void tearDown() {
        mocks.forEach(MockedStatic::close);
    }

    @Test
    public void Can_find_an_element_by_an_id() {

        final String id = someString();

        final WebElement expected = mock(WebElement.class);

        // Given
        given(internalFinders.findById(driver, id)).willReturn(expected);

        // When
        final WebElement actual = finders.findById(id);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_find_an_element_by_an_id_within_an_element() {

        final SearchContext parent = mock(SearchContext.class);
        final String id = someString();

        final WebElement expected = mock(WebElement.class);

        // Given
        given(internalFinders.findById(parent, id)).willReturn(expected);

        // When
        final WebElement actual = finders.findById(parent, id);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_find_an_element_by_an_id_that_contains_some_text() {

        final String text = someString();

        final WebElement expected = mock(WebElement.class);

        // Given
        given(internalFinders.findByIdThatContains(driver, text)).willReturn(expected);

        // When
        final WebElement actual = finders.findByIdThatContains(text);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_find_an_element_by_an_id_that_contains_some_text_within_an_element() {

        final SearchContext parent = mock(SearchContext.class);
        final String text = someString();

        final WebElement expected = mock(WebElement.class);

        // Given
        given(internalFinders.findByIdThatContains(parent, text)).willReturn(expected);

        // When
        final WebElement actual = finders.findByIdThatContains(parent, text);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_click_an_element_by_an_id() {

        final String id = someString();

        final WebElement element = mock(WebElement.class);

        // Given
        given(internalFinders.findById(driver, id)).willReturn(element);

        // When
        finders.clickById(id);

        // Then
        then(element).should().click();
    }

    @Test
    public void Can_click_an_element_by_an_id_within_an_element() {

        final SearchContext parent = mock(SearchContext.class);
        final String id = someString();

        final WebElement element = mock(WebElement.class);

        // Given
        given(internalFinders.findById(parent, id)).willReturn(element);

        // When
        finders.clickById(parent, id);

        // Then
        then(element).should().click();
    }

    @Test
    public void Can_choose_a_radio_by_its_id() {

        final String id = someString();

        final WebElement element = mock(WebElement.class);

        // Given
        given(internalFinders.findById(driver, id)).willReturn(element);

        // When
        finders.chooseById(id);

        // Then
        then(internalFinders).should().choose(element);
    }

    @Test
    public void Can_choose_a_radio_by_its_id_within_an_element() {

        final SearchContext parent = mock(SearchContext.class);
        final String id = someString();

        final WebElement element = mock(WebElement.class);

        // Given
        given(internalFinders.findById(parent, id)).willReturn(element);

        // When
        finders.chooseById(parent, id);

        // Then
        then(internalFinders).should().choose(element);
    }

    @Test
    public void Can_find_an_element_by_a_class_name() {

        final String className = someString();

        final WebElement expected = mock(WebElement.class);

        // Given
        given(internalFinders.findByClassName(driver, className)).willReturn(expected);

        // When
        final WebElement actual = finders.findByClassName(className);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_find_an_element_by_a_class_name_within_an_element() {

        final SearchContext parent = mock(SearchContext.class);
        final String className = someString();

        final WebElement expected = mock(WebElement.class);

        // Given
        given(internalFinders.findByClassName(parent, className)).willReturn(expected);

        // When
        final WebElement actual = finders.findByClassName(parent, className);

        // Then
        assertThat(expected, is(actual));
    }

    @Test
    public void Can_find_an_elements_text_by_a_class_name() {

        final String className = someString();

        final WebElement element = mock(WebElement.class);

        final String expected = someString();

        // Given
        given(internalFinders.findByClassName(driver, className)).willReturn(element);
        given(element.getText()).willReturn(expected);

        // When
        final String actual = finders.findTextByClassName(className);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_find_an_elements_text_by_a_class_name_within_an_element() {

        final SearchContext parent = mock(SearchContext.class);
        final String className = someString();

        final WebElement element = mock(WebElement.class);

        final String expected = someString();

        // Given
        given(internalFinders.findByClassName(parent, className)).willReturn(element);
        given(element.getText()).willReturn(expected);

        // When
        final String actual = finders.findTextByClassName(parent, className);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_click_an_element_by_a_class_name() {

        final String className = someString();

        final WebElement element = mock(WebElement.class);

        // Given
        given(internalFinders.findByClassName(driver, className)).willReturn(element);

        // When
        finders.clickByClassName(className);

        // Then
        then(element).should().click();
    }

    @Test
    public void Can_click_an_element_by_a_class_name_within_an_element() {

        final SearchContext parent = mock(SearchContext.class);
        final String className = someString();

        final WebElement element = mock(WebElement.class);

        // Given
        given(internalFinders.findByClassName(parent, className)).willReturn(element);

        // When
        finders.clickByClassName(parent, className);

        // Then
        then(element).should().click();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void Can_find_some_elements_by_their_class_name() {

        final String className = someString();

        final List<WebElement> expected = mock(List.class);

        // Given
        given(internalFinders.findAllByClassName(driver, className)).willReturn(expected);

        // When
        final List<WebElement> actual = finders.findAllByClassName(className);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void Can_find_some_elements_by_their_class_name_within_an_element() {

        final SearchContext parent = mock(SearchContext.class);
        final String className = someString();

        final List<WebElement> expected = mock(List.class);

        // Given
        given(internalFinders.findAllByClassName(parent, className)).willReturn(expected);

        // When
        final List<WebElement> actual = finders.findAllByClassName(parent, className);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_find_an_element_by_its_text() {

        final String tag = someString();
        final String text = someString();

        final WebElement expected = mock(WebElement.class);

        // Given
        given(internalFinders.findByText(driver, tag, text)).willReturn(expected);

        // When
        final WebElement actual = finders.findByText(tag, text);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_find_an_element_by_its_text_within_an_element() {

        final SearchContext parent = mock(SearchContext.class);
        final String tag = someString();
        final String text = someString();

        final WebElement expected = mock(WebElement.class);

        // Given
        given(internalFinders.findByText(parent, tag, text)).willReturn(expected);

        // When
        final WebElement actual = finders.findByText(parent, tag, text);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_find_all_elements_by_its_text() {

        final String tag = someString();
        final String text = someString();

        final List<WebElement> expected = mock(List.class);

        // Given
        given(internalFinders.findAllByText(driver, tag, text)).willReturn(expected);

        // When
        final List<WebElement> actual = finders.findAllByText(tag, text);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_find_all_elements_by_its_text_within_an_element() {

        final SearchContext parent = mock(SearchContext.class);
        final String tag = someString();
        final String text = someString();

        final List<WebElement> expected = mock(List.class);

        // Given
        given(internalFinders.findAllByText(parent, tag, text)).willReturn(expected);

        // When
        final List<WebElement> actual = finders.findAllByText(parent, tag, text);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_click_an_element_by_its_text() {

        final String tag = someString();
        final String text = someString();

        final WebElement element = mock(WebElement.class);

        // Given
        given(internalFinders.findByText(driver, tag, text)).willReturn(element);

        // When
        finders.clickByText(tag, text);

        // Then
        then(element).should().click();
    }

    @Test
    public void Can_click_an_element_by_its_text_within_an_element() {

        final SearchContext parent = mock(SearchContext.class);
        final String tag = someString();
        final String text = someString();

        final WebElement element = mock(WebElement.class);

        // Given
        given(internalFinders.findByText(parent, tag, text)).willReturn(element);

        // When
        finders.clickByText(parent, tag, text);

        // Then
        then(element).should().click();
    }

    @Test
    public void Can_select_an_option_by_its_label_name() {

        final String name = someString();
        final String option = someString();

        final WebElement element = mock(WebElement.class);

        // Given
        given(internalFinders.findByLabel(driver, name)).willReturn(element);

        // When
        finders.selectByLabel(name, option);

        // Then
        then(internalFinders).should().select(element, option);
    }

    @Test
    public void Can_select_an_option_by_its_label_name_within_an_element() {

        final SearchContext parent = mock(SearchContext.class);
        final String name = someString();
        final String option = someString();

        final WebElement element = mock(WebElement.class);

        // Given
        given(internalFinders.findByLabel(parent, name)).willReturn(element);

        // When
        finders.selectByLabel(parent, name, option);

        // Then
        then(internalFinders).should().select(element, option);
    }

    @Test
    public void Can_choose_a_radio_by_its_label_name() {

        final String label = someString();

        final WebElement element = mock(WebElement.class);

        // Given
        given(internalFinders.findByLabel(driver, label)).willReturn(element);

        // When
        finders.chooseByLabel(label);

        // Then
        then(internalFinders).should().choose(element);
    }

    @Test
    public void Can_choose_a_radio_by_its_label_name_within_an_element() {

        final SearchContext parent = mock(SearchContext.class);
        final String label = someString();

        final WebElement element = mock(WebElement.class);

        // Given
        given(internalFinders.findByLabel(parent, label)).willReturn(element);

        // When
        finders.chooseByLabel(parent, label);

        // Then
        then(internalFinders).should().choose(element);
    }

    @Test
    public void Can_click_an_input_by_its_value() {

        final String value = someString();

        final WebElement element = mock(WebElement.class);

        // Given
        given(internalFinders.findByValue(driver, value)).willReturn(element);

        // When
        finders.clickByValue(value);

        // Then
        then(element).should().click();
    }

    @Test
    public void Can_click_an_input_by_its_value_within_an_element() {

        final SearchContext parent = mock(SearchContext.class);
        final String value = someString();

        final WebElement element = mock(WebElement.class);

        // Given
        given(internalFinders.findByValue(parent, value)).willReturn(element);

        // When
        finders.clickByValue(parent, value);

        // Then
        then(element).should().click();
    }

    @Test
    public void Can_find_an_elements_parent_by_a_class_name() {

        final SearchContext element = mock(SearchContext.class);
        final String tag = someString();
        final String className = someString();

        final WebElement expected = mock(WebElement.class);

        // Given
        given(internalFinders.findParentByClassName(element, tag, className)).willReturn(expected);

        // When
        final WebElement actual = finders.findParentByClassName(element, tag, className);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_find_an_element_by_its_name() {

        final String tag = someString();
        final String name = someString();

        final WebElement expected = mock(WebElement.class);

        // Given
        given(internalFinders.findByName(driver, tag, name)).willReturn(expected);

        // When
        final WebElement actual = finders.findByName(tag, name);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_find_an_element_by_its_name_within_an_element() {

        final SearchContext parent = mock(SearchContext.class);
        final String tag = someString();
        final String name = someString();

        final WebElement expected = mock(WebElement.class);

        // Given
        given(internalFinders.findByName(parent, tag, name)).willReturn(expected);

        // When
        final WebElement actual = finders.findByName(parent, tag, name);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_set_text_by_an_inputs_label_name() {

        final String name = someString();
        final String text = someString();

        final WebElement element = mock(WebElement.class);

        // Given
        given(internalFinders.findByLabel(driver, name)).willReturn(element);

        // When
        finders.setTextByLabel(name, text);

        // Then
        then(internalFinders).should().setText(element, text);
    }

    @Test
    public void Can_set_text_by_an_inputs_label_name_within_an_element() {

        final SearchContext parent = mock(SearchContext.class);
        final String name = someString();
        final String text = someString();

        final WebElement element = mock(WebElement.class);

        // Given
        given(internalFinders.findByLabel(parent, name)).willReturn(element);

        // When
        finders.setTextByLabel(parent, name, text);

        // Then
        then(internalFinders).should().setText(element, text);
    }

    @Test
    public void Can_set_the_text_of_an_element_by_its_name() {

        final String name = someString();
        final String text = someString();

        final WebElement element = mock(WebElement.class);

        // Given
        given(internalFinders.findByName(driver, "input", name)).willReturn(element);

        // When
        finders.setTextByName(name, text);

        // Then
        then(internalFinders).should().setText(element, text);
    }

    @Test
    public void Can_set_the_text_of_an_element_by_its_name_within_an_element() {

        final SearchContext parent = mock(SearchContext.class);
        final String name = someString();
        final String text = someString();

        final WebElement element = mock(WebElement.class);

        // Given
        given(internalFinders.findByName(parent, "input", name)).willReturn(element);

        // When
        finders.setTextByName(parent, name, text);

        // Then
        then(internalFinders).should().setText(element, text);
    }

    @Test
    public void Can_set_the_text_of_an_element_by_its_class() {

        final String className = someString();
        final String text = someString();

        final WebElement element = mock(WebElement.class);

        // Given
        given(internalFinders.findByClassName(driver, className)).willReturn(element);

        // When
        finders.setTextByClassName(className, text);

        // Then
        then(internalFinders).should().setText(element, text);
    }

    @Test
    public void Can_set_the_text_of_an_element_by_its_class_within_an_element() {

        final SearchContext parent = mock(SearchContext.class);
        final String className = someString();
        final String text = someString();

        final WebElement element = mock(WebElement.class);

        // Given
        given(internalFinders.findByClassName(parent, className)).willReturn(element);

        // When
        finders.setTextByClassName(parent, className, text);

        // Then
        then(internalFinders).should().setText(element, text);
    }

    @Test
    public void Can_click_an_element_by_its_tag_name() {

        final String tagName = someString();

        final WebElement element = mock(WebElement.class);

        // Given
        given(internalFinders.findByTagName(driver, tagName)).willReturn(element);

        // When
        finders.clickByTagName(tagName);

        // Then
        then(element).should().click();
    }

    @Test
    public void Can_find_an_element_by_its_tag_name() {

        final String tagName = someString();

        final WebElement expected = mock(WebElement.class);

        // Given
        given(internalFinders.findByTagName(driver, tagName)).willReturn(expected);

        // When
        final WebElement actual = finders.byTagName(tagName);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_click_an_element_by_its_tag_name_within_an_element() {

        final SearchContext parent = mock(SearchContext.class);
        final String tagName = someString();

        final WebElement element = mock(WebElement.class);

        // Given
        given(internalFinders.findByTagName(parent, tagName)).willReturn(element);

        // When
        finders.clickByTagName(parent, tagName);

        // Then
        then(element).should().click();
    }

    @Test
    public void Can_click_an_element_by_its_attribute() {

        final String tagName = someString();
        final String name = someString();
        final String value = someString();

        final WebElement element = mock(WebElement.class);

        // Given
        given(internalFinders.findByAttribute(driver, tagName, name, value)).willReturn(element);

        // When
        finders.clickByAttribute(tagName, name, value);

        // Then
        then(element).should().click();
    }

    @Test
    public void Can_find_an_elements_by_its_attribute() {

        final String tag = someString();
        final String text = someString();
        final String value = someString();

        final WebElement expected = mock(WebElement.class);

        // Given
        given(internalFinders.findByAttribute(driver, tag, text, value)).willReturn(expected);

        // When
        final WebElement actual = finders.findByAttribute(tag, text, value);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_find_an_elements_by_its_attribute_within_an_element() {

        final SearchContext parent = mock(SearchContext.class);
        final String tag = someString();
        final String text = someString();
        final String value = someString();

        final WebElement expected = mock(WebElement.class);

        // Given
        given(internalFinders.findByAttribute(parent, tag, text, value)).willReturn(expected);

        // When
        final WebElement actual = finders.findByAttribute(parent, tag, text, value);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_find_an_element_by_its_attribute() {

        final String tagName = someString();
        final String name = someString();
        final String value = someString();

        final WebElement expected = mock(WebElement.class);

        // Given
        given(internalFinders.findByAttribute(driver, tagName, name, value)).willReturn(expected);

        // When
        final WebElement actual = finders.byAttribute(tagName, name, value);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_wait_by_class_name() {

        final String className = someString();
        final By by = mock(By.class);

        // Given
        given(Bys.className(className)).willReturn(by);
        given(ExpectedConditions.visibilityOfElementLocated(by)).willReturn(mock(ExpectedCondition.class));
        given(driver.findElement(by)).willReturn(mock(WebElement.class));

        // When
        finders.waitByClassName(className);

        // Then
    }

    @Test
    public void Can_wait_by_text() {

        final String tag = someString();
        final String text = someString();
        final By by = mock(By.class);

        // Given
        given(Bys.text(tag, text)).willReturn(by);
        given(ExpectedConditions.visibilityOfElementLocated(by)).willReturn(mock(ExpectedCondition.class));
        given(driver.findElement(by)).willReturn(mock(WebElement.class));

        // When
        finders.waitByText(tag, text);

        // Then
    }

    @Test
    public void Can_wait_by_id() {

        final String id = someString();
        final By by = mock(By.class);

        // Given
        given(Bys.id(id)).willReturn(by);
        given(ExpectedConditions.visibilityOfElementLocated(by)).willReturn(mock(ExpectedCondition.class));
        given(driver.findElement(by)).willReturn(mock(WebElement.class));

        // When
        finders.waitById(id);

        // Then
    }

    @Test
    public void Can_wait_by_id_contain() {

        final String id = someString();
        final String name = someString();
        final String value = someString();
        final By by = mock(By.class);

        // Given
        given(Bys.id(id)).willReturn(by);
        given(ExpectedConditions.attributeContains(by, name, value)).willReturn(mock(ExpectedCondition.class));
        given(driver.findElement(by)).willReturn(mock(WebElement.class));

        // When
        finders.waitByIdContain(id, name, value);

        // Then
    }

    @Test
    public void Can_set_an_element_by_an_id() {

        final String id = someString();
        final String text = someString();

        final WebElement webElement = mock(WebElement.class);

        // Given
        given(internalFinders.findById(driver, id)).willReturn(webElement);

        // When
        finders.setTextById(id, text);

        // Then
        then(internalFinders).should().setText(webElement, text);
    }

    @Test
    public void Can_return_cookie_set() {
        final Options options = mock(Options.class);
        final Set<Cookie> expected = mock(Set.class);

        // Given
        given(driver.manage()).willReturn(options);
        given(options.getCookies()).willReturn(expected);

        // When
        final Set<Cookie> actual = finders.getCookies();

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_return_cookie() {
        final Options options = mock(Options.class);
        final String name = someString();
        final Cookie expected = mock(Cookie.class);

        // Given
        given(driver.manage()).willReturn(options);
        given(options.getCookieNamed(name)).willReturn(expected);

        // When
        final Cookie actual = finders.getCookie(name);

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_refresh() {
        final Navigation navigation = mock(Navigation.class);

        // Given
        given(driver.navigate()).willReturn(navigation);

        // When
        finders.refresh();

        // Then
        then(navigation).should().refresh();
    }

    @Test
    public void Can_wait_number_of_elements_to_be_less_than() {

        final String tag = someString();
        final Integer expectedNumber = somePositiveInteger();
        final By by = mock(By.class);
        final List<WebElement> list = mock(List.class);

        // Given
        given(Bys.tagName(tag)).willReturn(by);
        given(ExpectedConditions.numberOfElementsToBeLessThan(by, expectedNumber)).willReturn(mock(ExpectedCondition.class));
        given(driver.findElements(by)).willReturn(list);
        given(list.size()).willReturn(expectedNumber - 1);

        // When

        finders.waitNumberOfElementsToBeLessThan(tag, expectedNumber);

        // Then
    }

//    @Test
//    public void Can_return_JavascriptExecutor() {
//        final JavascriptExecutor javascriptExecutor = mock(JavascriptExecutor.class);
//
//        // Given
//        when((JavascriptExecutor) driver).thenReturn(javascriptExecutor);
//
//        // When
//        final JavascriptExecutor actual = finders.getJavascriptExecutor();
//
//        // Then
//        assertThat(actual, is(instanceOf(JavascriptExecutor.class)));
//
//    }

    @Test
    public void Can_return_webdriver() {
        // Given

        // When
        final WebDriver actual = finders.getWebDriver();

        // Then
        assertThat(actual, is(driver));
    }

    //    @Test
//    public void for_coverage() {
//
//        // Given
//
//        // When
//        final Finders finders = new Finders(driver);
//
//        // Then
//        assertThat(finders, is(instanceOf(Finders.class)));
//    }
}