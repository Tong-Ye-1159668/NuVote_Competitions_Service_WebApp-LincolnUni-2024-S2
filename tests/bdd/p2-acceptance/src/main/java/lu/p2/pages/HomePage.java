package lu.p2.pages;


import lu.p2.io.Helper;
import lu.p2.models.Charity;
import lu.p2.models.User;
import lu.p2.models.VoteRecord;
import lu.p2.selenium.Bys;
import lu.p2.selenium.Finders;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static java.lang.String.format;


@Component
public class HomePage implements BasePage {

    private final WebDriver webDriver;
    private final Finders finders;
    private final String siteUrl;
    private final Helper helper;


    public HomePage(final WebDriver webDriver, final Finders finders, @Value("${site.url}") final String siteUrl, final Helper helper) {
        this.webDriver = webDriver;
        this.finders = finders;
        this.siteUrl = siteUrl;
        this.helper = helper;
    }

    @Override
    public void visit() {
        webDriver.get(siteUrl);
    }

    public void clickRegisterBtn() {
        finders.clickByAttribute("a", "href", "/register");
    }

    public void clickLoginBtn() {
        finders.clickByAttribute("a", "href", "/login");
    }

    public WebElement findRegisterSuccessfullyMsg() {
        return finders.findByText("div", "You have successfully registered!");
    }

    public WebElement findUserMenu() {
        return finders.findByText("a", "My Dashboard");
    }

    public void goToMyDashboardPage() {
        finders.clickById("navbarDropdown");
        finders.clickByText("a", "My Dashboard");
    }

    public void goToPasswordPage() {
        finders.clickById("navbarDropdown");
        finders.clickByText("a", "Change Password");
    }

    public void logout() {
        visit();
        finders.waitById("navbarDropdown");
        finders.clickById("navbarDropdown");
        finders.waitByText("a", "Logout");
        finders.clickByText("a", "Logout");
    }

    public boolean iAmAGuestUser() {
        finders.byAttribute("a", "href", "/register");
        finders.byAttribute("a", "href", "/login");
        return true;
    }

    public boolean checkMyRole(final String role) {
        return finders.findById("navbarDropdown").getAttribute("title").contains("role: " + role);
    }

    public void logoutUser() {
        webDriver.get(format("%s/%s", siteUrl, "users/logout"));
    }

    public VoteRecord gotoAnOngoingEvent() {
        finders.waitByClassName("events");
        final WebElement eventWE = helper.getRandomElement(finders.findAllByText("div", "on_going")).findElement(Bys.parentClassName("div", "event-card"));
        final String eventName = eventWE.findElement(Bys.tagName("b")).getText();
        final String themeName = eventWE.findElement(Bys.tagName("a")).getText();
        final VoteRecord voteRecord = new VoteRecord();
        voteRecord.setEventName(eventName);
        voteRecord.setThemeName(themeName);
        eventWE.findElement(Bys.attribute("a", "class", "stretched-link")).click();
        return voteRecord;
    }

    public void clickDonation(final Charity charity) {
        this.visit();
        finders.waitByClassName("events");
        finders.findByText("b", charity.getName())
                .findElement(Bys.parentClassName("div", "event-card"))
                .findElement(Bys.text("a", "Donate"))
                .click();
    }

    public User updateUserId(final User user) {
        final String[] split = finders.findByText("a", "Change Password").getAttribute("href").split("/");
        final String userId = split[5];
        user.setUserId(Integer.parseInt(userId));
        return user;
    }
}
