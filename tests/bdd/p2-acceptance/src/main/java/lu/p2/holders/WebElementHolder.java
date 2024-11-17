package lu.p2.holders;

import org.openqa.selenium.WebElement;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("cucumber-glue")
public class WebElementHolder extends GenericHolder<WebElement> {
}
