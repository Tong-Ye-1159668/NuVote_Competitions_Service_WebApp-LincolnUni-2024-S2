package lu.p2.pages;

import lu.p2.models.User;
import lu.p2.selenium.Finders;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RegisterPage implements BasePage {

    private final WebDriver webDriver;
    private final Finders finders;
    private final String siteUrl;

    public RegisterPage(final WebDriver webDriver, final Finders finders, @Value("${site.url}") final String siteUrl) {
        this.webDriver = webDriver;
        this.finders = finders;
        this.siteUrl = siteUrl;
    }

    @Override
    public void visit() {
        webDriver.get(siteUrl + "/register");
    }

    public void registerAUser(User user) {
        finders.setTextByName("username", user.getUsername());
        finders.setTextByName("email", user.getEmail());
        finders.setTextByName("password", user.getPassword());
        finders.setTextByName("password2", user.getPassword());
        finders.clickByText("button", "Sign Up");
    }

    public void submitInvalidInfo() {
        finders.setTextByName("first_name", "!");
        finders.setTextByName("last_name", "!");
//        finders.setTextByName("location", "!");
        finders.clickByText("button", "Sign Up");
    }

    public boolean seeAllErrorFeedbacks() {
        final String[] messages = {
                "Username must contain only characters and numbers! Length between 3 and 50.",
                "Please enter a valid email (example@domain.com).",
                "First name must contain only characters and numbers! Max length is 50.",
                "Last name must contain only characters and numbers! Max length is 50.",
//                "Location must contain only characters and numbers! Max length is 50.",
                "Password must be at least 8 characters long and include a mix of letters, numbers, and special",
                "Passwords do not match."
        };

        for (String message : messages) {
            if (!finders.findByText("div", message).isDisplayed()) {
                return false;
            }
        }
        return true;
    }
}
