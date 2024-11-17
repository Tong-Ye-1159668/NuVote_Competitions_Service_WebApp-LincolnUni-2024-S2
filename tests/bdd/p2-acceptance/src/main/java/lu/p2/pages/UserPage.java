package lu.p2.pages;

import lu.p2.factories.UserFactory;
import lu.p2.models.User;
import lu.p2.selenium.Bys;
import lu.p2.selenium.Finders;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class UserPage implements BasePage {


    private final Finders finders;
    private final UserFactory userFactory;

    public UserPage(final Finders finders, final UserFactory userFactory) {
        this.finders = finders;
        this.userFactory = userFactory;
    }

    @Override
    public void visit() {
        finders.clickByAttribute("a", "href", "/users/");
    }

    public User updateUserRole(final String userRole, final String role) {

        finders.waitById("role");
        finders.findById("role").findElement(Bys.text("option", userRole)).click();
        finders.waitByText("td", "1");
        final List<WebElement> voters = finders.findById("userTableBody")
                .findElements(Bys.tagName("tr"));
        Collections.shuffle(voters);
        final WebElement userWE = voters.get(0);
        final WebElement usernameWE = userWE.findElement(Bys.tagName("a"));
        final WebElement emailWE = userWE.findElement(Bys.text("td", "@"));
        final User user = userFactory.createAUser(usernameWE.getText(), emailWE.getText());
        return updateUserRole(user, role);
    }

    public User updateUserRole(final User user, final String role) {

        finders.waitById("role");
        finders.waitByText("td", "1");
        finders.setTextById("query", user.getEmail());
        // Wait until there's exactly one user row
        finders.waitNumberOfElementsToBeLessThan("tr", 3);

        final List<WebElement> voters = finders.findById("userTableBody")
                .findElements(Bys.tagName("tr"));
        final WebElement userWE = voters.get(0);
        userWE.findElement(Bys.tagName("select")).findElements(Bys.tagName("option")).forEach(webElement -> {
            final String value = webElement.getAttribute("value");
            if (value.equals(role)) {
                webElement.click();
            }
        });
        return user;
    }
}
