package lu.p2.selenium;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.springframework.stereotype.Component;

@Component
public class Selects {

    public Select create(WebElement element) {
        return new Select(element);
    }
}