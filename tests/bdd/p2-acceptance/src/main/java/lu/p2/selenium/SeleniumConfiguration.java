package lu.p2.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.time.Duration;

import static io.github.bonigarcia.wdm.config.DriverManagerType.CHROME;
import static io.github.bonigarcia.wdm.config.DriverManagerType.FIREFOX;
import static io.github.bonigarcia.wdm.config.DriverManagerType.SAFARI;
import static java.lang.String.format;

@Configuration
public class SeleniumConfiguration {


    @Bean(destroyMethod = "quit")
    @Scope("singleton") // Spring will automatically add the WebDriver instance to a special scope that we don't want.
    public WebDriver webDriver(@Value("${web.driver:chrome}") String webDriver) {

        if ("chrome".equals(webDriver)) {
            return WebDriverManager.chromedriver().create();
        }

        if ("chrome-headless".equals(webDriver)) {
            WebDriverManager.getInstance(CHROME).setup();
            final ChromeOptions options = headlessChromeOptions();
            return new ChromeDriver(options);
        }

        if ("chrome-docker".equals(webDriver)) {
            WebDriverManager.getInstance(CHROME).setup();
            final ChromeOptions options = headlessChromeOptions();
            options.addArguments("--disable-gpu");
            return new ChromeDriver(options);
        }

        if ("firefox".equals(webDriver)) {
            WebDriverManager.getInstance(FIREFOX).setup();
            return new FirefoxDriver();
        }

        if ("safari".equals(webDriver)) {
            WebDriverManager.getInstance(SAFARI).setup();
            return new SafariDriver();
        }

        throw new IllegalArgumentException(format("Web driver %s not supported.", webDriver));
    }

    private static ChromeOptions headlessChromeOptions() {
        final ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");

        options.addArguments("--hide-scrollbars");
        options.addArguments("--mute-audio");

        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-extensions");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-background-networking");
        options.addArguments("--disable-background-timer-throttling");
        options.addArguments("--disable-client-side-phishing-detection");
        options.addArguments("--disable-default-apps");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-hang-monitor");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-prompt-on-repost");
        options.addArguments("--disable-sync");
        options.addArguments("--disable-translate");
        options.addArguments("--metrics-recording-only");
        options.addArguments("--no-first-run");
        options.addArguments("--safebrowsing-disable-auto-update");

        options.addArguments("--no-cache");
        options.addArguments("--user-data-dir=/tmp/user-data");
        options.addArguments("--single-process");
        options.addArguments("--data-path=/tmp/data-path");
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("--homedir=/tmp");
        options.addArguments("--disk-cache-dir=/tmp/cache-dir");
        options.addArguments("user-agent=Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36");
        options.setAcceptInsecureCerts(true);
        return options;
    }

    @Bean
    public WebDriverWait webDriverWait(WebDriver webDriver, @Value("${wait_timeout}") long timeout) {
        return new WebDriverWait(webDriver, Duration.ofSeconds(timeout));
    }

    @Bean
    public Actions actions(WebDriver driver) {
        return new Actions(driver);
    }
}