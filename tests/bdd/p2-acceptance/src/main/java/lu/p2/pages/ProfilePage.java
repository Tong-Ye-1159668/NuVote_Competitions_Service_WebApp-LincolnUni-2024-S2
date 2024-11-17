package lu.p2.pages;

import lu.p2.factories.LocationFactory;
import lu.p2.io.HClient;
import lu.p2.models.Location;
import lu.p2.models.User;
import lu.p2.selenium.Finders;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Component
public class ProfilePage {

    private final Finders finders;
    private final HClient hClient;
    private final LocationFactory locationFactory;

    public ProfilePage(final Finders finders, final HClient hClient, final LocationFactory locationFactory) {
        this.finders = finders;
        this.hClient = hClient;
        this.locationFactory = locationFactory;
    }

    public boolean verifyMyDetails(final User user) {

        if (!Objects.equals(finders.findByName("input", "username").getAttribute("value"), user.getUsername())) {
            return false;
        }
        if (!Objects.equals(finders.findByName("input", "email").getAttribute("value"), user.getEmail())) {
            return false;
        }
        if (user.getFirstName() != null && !Objects.equals(user.getFirstName(), finders.findByName("input", "first_name").getAttribute("value"))) {
            return false;
        }
        if (user.getLastName() != null && !Objects.equals(user.getLastName(), finders.findByName("input", "last_name").getAttribute("value"))) {
            return false;
        }
        if (user.getLocation() != null && !Objects.equals(user.getLocation(), finders.findByName("input", "location").getAttribute("value"))) {
            return false;
        }
        if (finders.findByName("input", "role").getAttribute("value").isEmpty()) {
            return false;
        }
        return finders.findByName("textarea", "description").isDisplayed();
    }

    public void updateMyProfile(final User user) {
        if (user.getFirstName() != null) {
            finders.findByName("input", "first_name").sendKeys(user.getFirstName());
        }
        if (user.getLastName() != null) {
            finders.findByName("input", "last_name").sendKeys(user.getLastName());
        }
        if (user.getDescription() != null) {
            finders.findByName("textarea", "description").sendKeys(user.getDescription());
        }
        finders.clickByText("button", "Update Profile");
    }

    public void deleteMyProfileImage() {
        finders.clickByText("button", "Delete Image");
        finders.waitById("confirmDeleteButton");
        finders.clickById("confirmDeleteButton");
        finders.refresh();
    }

    public void updateMyProfileImage() throws IOException {
        finders.waitById("profile-image");
        final Cookie cookie = finders.getCookie("session");
        hClient.uploadProfileImage(cookie.getValue());
        finders.refresh();
    }

    public Location updateMyLocation() {
        final Location location = locationFactory.getRandomLocation();
        finders.setTextById("location", location.getAddress());
        finders.waitByText("li", location.getAddress());
        finders.findByText("li", location.getAddress()).click();
        finders.findByText("b", location.getAddress());
        finders.waitByIdContain("latitude", "value", location.getLatitude());
        finders.waitByIdContain("longitude", "value", location.getLongitude());
        finders.waitByIdContain("user_city_latitude", "value", location.getCityLatitude());
        finders.waitByIdContain("user_city_longitude", "value", location.getCityLongitude());
        finders.clickByText("button", "Update Profile");
        return location;
    }

    public void deleteMyLocation(Location location) {
        finders.waitByText("button", "Update Profile");
        final WebElement locationWE = finders.findById("location");
        locationWE.clear();
        finders.getJavascriptExecutor().executeScript("arguments[0].dispatchEvent(new Event('input'));", locationWE);

        try {
            // Check if the sub-element exists and click
            finders.findByText("b", location.getAddress());
        } catch (NoSuchElementException e) {
            // Continue if the element does not exist
        }
        finders.clickByText("button", "Update Profile");
    }

    public boolean mapIsNotDisplay() {
        finders.findByText("div", "Profile updated successfully!");
        finders.findByText("span", "None");
        try {
            // Check if the sub-element exists and click
            finders.waitById("map");
        } catch (TimeoutException e) {
            // Continue if the element does not exist
            return true;
        }
        return false;
    }


    public boolean viewPublicProfileWithoutLocation(final User user) {
        final WebDriver driver = finders.getWebDriver();
        final String currentUrl = driver.getCurrentUrl();
        driver.get(currentUrl + "users/" + user.getUserId() + "/public");
        finders.findByText("h2", user.getUsername());
        if (user.getDescription() != null) {
            finders.findByText("p", user.getDescription());
        } else {
            finders.findByText("p", "No description");
        }
        try {
            // Check if the sub-element exists and click
            finders.waitById("map");
        } catch (TimeoutException e) {
            // Continue if the element does not exist
            return true;
        }
        return false;
    }

    public void shareLocation() {
        finders.waitByText("button", "Update Profile");
        finders.findById("display_location").click();
        finders.clickByText("button", "Update Profile");
    }

    public void clickEditBtn() {
        finders.clickByText("a", "Edit");
    }
}
