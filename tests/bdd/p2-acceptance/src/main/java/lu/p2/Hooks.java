package lu.p2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.spring.CucumberContextConfiguration;
import lu.p2.holders.GenericHolder;
import lu.p2.pages.HomePage;
import lu.p2.selenium.Browser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@CucumberContextConfiguration
@SpringBootTest(classes = lu.p2.AcceptanceConfiguration.class, webEnvironment = NONE)
public class Hooks {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final ObjectMapper objectMapper;
    private final Browser browser;
    private final HomePage homePage;
    private final int width;
    private final int height;

    @SuppressWarnings("rawtypes")
    private final List<GenericHolder<?>> holders;

    public Hooks(
            ObjectMapper objectMapper,
            Browser browser,
            HomePage homePage,
            @Value("${screen.width:1280}") int width,
            @Value("${screen.height:1024}") int height,
            @Autowired(required = false) List<GenericHolder<?>> holders
    ) {
        this.objectMapper = objectMapper;
        this.browser = browser;
        this.homePage = homePage;
        this.width = width;
        this.height = height;
        this.holders = holders;
    }

    @Before
    public void setup() {
        log.info("Scenario Start.");
        browser.setWindowSize(width, height);
        homePage.visit();
        browser.deleteAllCookies();
        browser.clearSessionStorage();
        browser.clearLocalStorage();
        browser.deleteAllCookies();
        browser.clearSessionStorage();
        browser.clearLocalStorage();
        browser.refreshPage();
    }

    @After
    public void tearDown(Scenario scenario) {
        log.info("Scenario End.");
        if (scenario.isFailed()) {
            holders.forEach(this::logHolder);
            browser.getLogs().forEach(log::error);
            scenario.attach(browser.takeScreenShot(), "image/png", scenario.getName());
        }
    }

    private void logHolder(GenericHolder<?> holder) {
        try {
            log.error("Holder for {}: {}", getHolderType(holder), objectMapper.writeValueAsString(holder.get()));
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialise holder containing: " + getHolderType(holder), e);
        }
    }

    private String getHolderType(GenericHolder<?> holder) {
        return Arrays.stream(((ParameterizedType) holder.getClass().getGenericSuperclass()).getActualTypeArguments())
                .findFirst()
                .map(Type::getTypeName)
                .orElse("no generic type");
    }
}
