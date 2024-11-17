package lu.p2.io;

import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class Helper {

    public WebElement getRandomElement(final List<WebElement> elements) {
        final Random random = new Random();
        final int index = random.nextInt(elements.size());
        return elements.get(index);
    }

}
