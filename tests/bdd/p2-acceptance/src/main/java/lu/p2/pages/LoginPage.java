package lu.p2.pages;

import lu.p2.models.User;
import lu.p2.selenium.Finders;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LoginPage implements BasePage {

    private final WebDriver webDriver;
    private final Finders finders;
    private final String siteUrl;

    public LoginPage(final WebDriver webDriver, final Finders finders, @Value("${site.url}") final String siteUrl) {
        this.webDriver = webDriver;
        this.finders = finders;
        this.siteUrl = siteUrl;
    }

    @Override
    public void visit() {
        webDriver.get(siteUrl + "/users/login");
    }

    public void login(final User user) {
        visit();
        finders.waitByClassName("btn-success");
        finders.setTextByName("username", user.getUsername());
        finders.setTextByName("password", user.getPassword());
        finders.clickByText("button", "Sign in");
    }

    public void submitInvalidCredentials() {
        finders.setTextByName("username", "");
        finders.setTextByName("password", "");
        finders.clickByText("button", "Sign in");
    }

    public boolean seeAllErrorFeedbacks() {
        final String[] messages = {"Username must contain only characters and numbers!", "Password is required"};

        for (String message : messages) {
            if (!finders.findByText("div", message).isDisplayed()) {
                return false;
            }
        }
        return true;
    }

    public boolean isLoginPage() {
        finders.waitByText("h3", "Login");
        finders.findById("username");
        finders.findById("password");
        finders.findByText("button", "Sign in");
        finders.findByText("a", "Register");
        return true;
    }
}
