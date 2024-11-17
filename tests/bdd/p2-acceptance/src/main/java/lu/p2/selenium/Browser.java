package lu.p2.selenium;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.html5.WebStorage;
import org.openqa.selenium.logging.LogEntry;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.StreamSupport;

import static java.lang.String.format;
import static java.time.Instant.ofEpochMilli;
import static java.time.ZonedDateTime.ofInstant;
import static org.openqa.selenium.OutputType.BYTES;
import static org.openqa.selenium.logging.LogType.BROWSER;

@Component
public class Browser {

    private final WebDriver driver;

    public Browser(WebDriver driver) {
        this.driver = driver;
    }

    public void setWindowSize(int width, int height) {
        driver.manage().window().setSize(new Dimension(width, height));
    }

    public void scrollToBottom() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }

    public List<String> getLogs() {
        return StreamSupport.stream(driver.manage().logs().get(BROWSER).spliterator(), false)
                .map(this::toLogString).toList();
    }

    private String toLogString(LogEntry entry) {
        return format(
                "%s %s: %s",
                ofInstant(ofEpochMilli(entry.getTimestamp()), ZoneId.systemDefault()),
                entry.getLevel().getName(),
                entry.getMessage()
        );
    }

    public byte[] takeScreenShot() {
        return ((TakesScreenshot) driver).getScreenshotAs(BYTES);
    }

    public void deleteAllCookies() {
        driver.manage().deleteAllCookies();
    }

    public void clearSessionStorage() {
        ((WebStorage) driver).getSessionStorage().clear();
    }

    public void clearLocalStorage() {
        ((WebStorage) driver).getLocalStorage().clear();
    }

    public void refreshPage() {
        driver.navigate().refresh();
    }
}