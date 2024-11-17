package lu.p2.selenium;

import org.openqa.selenium.WebDriver;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;


@Configuration
public class SeleniumShutdownConfiguration {

    private WebDriver webDriver;

    public SeleniumShutdownConfiguration(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    @PreDestroy
    public void quitWebDriver() {
        if (webDriver != null) {
            webDriver.quit();
        }
    }
}
