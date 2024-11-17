package lu.p2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.Scenario;
import lu.p2.holders.GenericHolder;
import lu.p2.pages.HomePage;
import lu.p2.selenium.Browser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.MockedStatic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static shiver.me.timbers.data.random.RandomBytes.someBytes;
import static shiver.me.timbers.data.random.RandomIntegers.someInteger;
import static shiver.me.timbers.data.random.RandomStrings.someString;
import static shiver.me.timbers.data.random.RandomThings.someThing;

public class HooksTest {

    private List<MockedStatic<?>> statics;
    private Logger log;
    private ObjectMapper mapper;
    private TestHolder holder1;
    private TestHolder holder2;
    private TestHolder holder3;
    private Browser browser;
    private HomePage homePage;
    private int width;
    private int height;
    private Hooks hooks;

    @Before
    public void setUp() {
        statics = List.of(mockStatic(LoggerFactory.class));
        log = mock(Logger.class);
        given(LoggerFactory.getLogger(any(Class.class))).willReturn(log);
        mapper = mock(ObjectMapper.class);
        browser = mock(Browser.class);
        homePage = mock(HomePage.class);
        width = someInteger();
        height = someInteger();
        holder1 = mock(TestHolder.class);
        holder2 = mock(TestHolder.class);
        holder3 = mock(TestHolder.class);
        hooks = new Hooks(
                mapper,
                browser,
                homePage,
                width,
                height,
                List.of(holder1, holder2, holder3)
        );
    }

    @After
    public void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @Test
    public void Will_set_the_window_size_and_log_out_before_every_scenario() {

        // When
        hooks.setup();

        // Then
        final InOrder order = inOrder(homePage, browser);
        then(browser).should(order).setWindowSize(width, height);
        then(homePage).should(order).visit();
        then(browser).should(order).deleteAllCookies();
        then(browser).should(order).clearSessionStorage();
        then(browser).should(order).clearLocalStorage();
        then(browser).should(order).deleteAllCookies();
        then(browser).should(order).clearSessionStorage();
        then(browser).should(order).clearLocalStorage();
        then(browser).should(order).refreshPage();
    }

    @Test
    public void Will_do_nothing_on_scenario_scuccess() {

        final Scenario scenario = mock(Scenario.class);

        // Given
        given(scenario.isFailed()).willReturn(false);

        // When
        hooks.tearDown(scenario);

        // Then
        then(holder1).shouldHaveNoInteractions();
        then(holder2).shouldHaveNoInteractions();
        then(holder3).shouldHaveNoInteractions();
        then(browser).shouldHaveNoInteractions();
        then(mapper).shouldHaveNoInteractions();
        then(homePage).shouldHaveNoInteractions();
    }

    @Test
    public void Will_log_all_holder_contents_on_scenario_failure() throws JsonProcessingException {

        final Object object1 = mock(Object.class);
        final Object object2 = mock(Object.class);
        final Object object3 = mock(Object.class);
        final Scenario scenario = mock(Scenario.class);
        final String json1 = someString(3);
        final String json2 = someString(5);
        final String json3 = someString(8);

        // Given
        given(scenario.isFailed()).willReturn(true);
        given(holder1.get()).willReturn(object1);
        given(holder2.get()).willReturn(object2);
        given(holder3.get()).willReturn(object3);
        given(mapper.writeValueAsString(object1)).willReturn(json1);
        given(mapper.writeValueAsString(object2)).willReturn(json2);
        given(mapper.writeValueAsString(object3)).willReturn(json3);

        // When
        hooks.tearDown(scenario);

        // Then
        then(log).should().error("Holder for {}: {}", Object.class.getName(), json1);
        then(log).should().error("Holder for {}: {}", Object.class.getName(), json2);
        then(log).should().error("Holder for {}: {}", Object.class.getName(), json3);
    }

    @Test
    public void Will_log_failed_holder_serialisation() throws JsonProcessingException {

        final Object object = someThing();
        final Scenario scenario = mock(Scenario.class);
        final JsonProcessingException exception = mock(JsonProcessingException.class);

        // Given
        given(scenario.isFailed()).willReturn(true);
        given(holder1.get()).willReturn(object);
        given(mapper.writeValueAsString(object)).willThrow(exception);

        // When
        hooks.tearDown(scenario);

        // Then
        then(log).should().error("Failed to deserialise holder containing: " + Object.class.getName(), exception);
    }

    @Test
    public void Will_log_browser_logs_on_scenario_failure() {

        final Scenario scenario = mock(Scenario.class);
        final String log1 = someString();
        final String log2 = someString();
        final String log3 = someString();

        // Given
        given(scenario.isFailed()).willReturn(true);
        given(browser.getLogs()).willReturn(List.of(log1, log2, log3));

        // When
        hooks.tearDown(scenario);

        // Then
        then(log).should().error(log1);
        then(log).should().error(log2);
        then(log).should().error(log3);
    }

    @Test
    public void Will_take_a_screen_shot_on_scenario_failure() {

        final Scenario scenario = mock(Scenario.class);
        final String name = someString();
        final byte[] bytes = someBytes();

        // Given
        given(scenario.isFailed()).willReturn(true);
        given(scenario.getName()).willReturn(name);
        given(browser.takeScreenShot()).willReturn(bytes);

        // When
        hooks.tearDown(scenario);

        // Then
        then(scenario).should().attach(bytes, "image/png", name);
    }

    private static class TestHolder extends GenericHolder<Object> {
    }
}