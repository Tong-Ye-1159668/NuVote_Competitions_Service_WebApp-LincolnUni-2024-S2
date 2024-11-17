package lu.p2.selenium;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.html5.WebStorage;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.Logs;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.logging.Level;

import static java.lang.String.format;
import static java.time.Instant.ofEpochMilli;
import static java.time.ZonedDateTime.ofInstant;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.openqa.selenium.OutputType.BYTES;
import static org.openqa.selenium.WebDriver.Options;
import static org.openqa.selenium.logging.LogType.BROWSER;
import static shiver.me.timbers.data.random.RandomBytes.someBytes;
import static shiver.me.timbers.data.random.RandomIntegers.someInteger;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class BrowserTest {

    private JavaScriptDriver driver;
    private Browser browser;
    private List<MockedStatic<?>> statics;

    @Before
    public void setUp() {
        statics = List.of(mockStatic(Bys.class), mockStatic(By.class));
        driver = mock(JavaScriptDriver.class, RETURNS_DEEP_STUBS);
        browser = new Browser(driver);
    }

    @After
    public void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @Test
    public void Can_scroll_to_the_bottom_of_the_current_page() {

        // When
        browser.scrollToBottom();

        // Then
        then(driver).should().executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }

    @Test
    public void Can_get_the_browser_logs() {

        final Options options = mock(Options.class);
        final Logs logs = mock(Logs.class);
        final LogEntries entries = mock(LogEntries.class);
        final LogEntry entry1 = mock(LogEntry.class);
        final LogEntry entry2 = mock(LogEntry.class);
        final LogEntry entry3 = mock(LogEntry.class);
        final ZonedDateTime now = ZonedDateTime.now();
        final long timestamp1 = now.toEpochSecond();
        final long timestamp2 = now.plusMinutes(1).toEpochSecond();
        final long timestamp3 = now.plusMinutes(2).toEpochSecond();
        final Level level1 = mock(Level.class);
        final Level level2 = mock(Level.class);
        final Level level3 = mock(Level.class);
        final String message1 = someString(3);
        final String message2 = someString(5);
        final String message3 = someString(8);
        final String levelName1 = someString(13);
        final String levelName2 = someString(21);
        final String levelName3 = someString(34);

        // Given
        given(driver.manage()).willReturn(options);
        given(options.logs()).willReturn(logs);
        given(logs.get(BROWSER)).willReturn(entries);
        given(entries.spliterator()).willReturn(asList(entry1, entry2, entry3).spliterator());
        given(entry1.getTimestamp()).willReturn(Long.valueOf(timestamp1));
        given(entry1.getLevel()).willReturn(level1);
        given(entry1.getMessage()).willReturn(message1);
        given(entry2.getTimestamp()).willReturn(Long.valueOf(timestamp2));
        given(entry2.getLevel()).willReturn(level2);
        given(entry2.getMessage()).willReturn(message2);
        given(entry3.getTimestamp()).willReturn(Long.valueOf(timestamp3));
        given(entry3.getLevel()).willReturn(level3);
        given(entry3.getMessage()).willReturn(message3);
        given(level1.getName()).willReturn(levelName1);
        given(level2.getName()).willReturn(levelName2);
        given(level3.getName()).willReturn(levelName3);

        // When
        final List<String> actual = browser.getLogs();

        // Then
        assertThat(actual, contains(
                toLogLine(timestamp1, levelName1, message1),
                toLogLine(timestamp2, levelName2, message2),
                toLogLine(timestamp3, levelName3, message3)
        ));
    }

    @Test
    public void Can_set_window_size() {

        final Options options = mock(Options.class);
        final WebDriver.Window window = mock(WebDriver.Window.class);
        final int width = someInteger();
        final int height = someInteger();

        // Given
        given(driver.manage()).willReturn(options);
        given(options.window()).willReturn(window);

        // When
        browser.setWindowSize(width, height);

        // Then
        then(window).should().setSize(new Dimension(width, height));
    }

    @Test
    public void Can_take_a_screen_shot() {

        final byte[] expected = someBytes();

        // Given
        given(driver.getScreenshotAs(BYTES)).willReturn(expected);

        // When
        final byte[] actual = browser.takeScreenShot();

        // Then
        assertThat(actual, is(expected));
    }

    @Test
    public void Can_delete_all_cookies() {

        final Options options = mock(Options.class);

        // Given
        given(driver.manage()).willReturn(options);


        // When
        browser.deleteAllCookies();

        // Then
        then(options).should().deleteAllCookies();
    }

    @Test
    public void Can_clear_the_session_storage() {

        // When
        browser.clearSessionStorage();

        // Then
        then(driver.getSessionStorage()).should().clear();
    }

    @Test
    public void Can_clear_the_local_storage() {

        // When
        browser.clearLocalStorage();

        // Then
        then(driver.getLocalStorage()).should().clear();
    }

    @Test
    public void Can_refresh_a_page() {

        // When
        browser.refreshPage();

        // Then
        then(driver.navigate()).should().refresh();
    }

    private static String toLogLine(long timestamp, String level, String message) {
        return format("%s %s: %s", ofInstant(ofEpochMilli(timestamp), ZoneId.systemDefault()), level, message);
    }

    private interface JavaScriptDriver extends WebDriver, JavascriptExecutor, TakesScreenshot, WebStorage {
    }

}