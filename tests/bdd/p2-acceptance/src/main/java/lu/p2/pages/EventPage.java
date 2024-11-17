package lu.p2.pages;

import lu.p2.io.Helper;
import lu.p2.models.Event;
import lu.p2.selenium.Bys;
import lu.p2.selenium.Finders;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Component
public class EventPage {
    private final HomePage homePage;
    private final Finders finders;
    private final Helper helper;

    public EventPage(final HomePage homePage, final Finders finders, final Helper helper) {
        this.homePage = homePage;
        this.finders = finders;
        this.helper = helper;
    }

    public void createEvent(final Event event) {
        finders.clickByText("a", "Create an\n" + "            Event");
        finders.setTextById("name", event.getName());
        finders.setTextById("event-description", event.getDescription());
        finders.setTextById("image", event.getImage());
        finders.waitById("start_date");
        final WebElement startDate = finders.findById("start_date");
        startDate.sendKeys(event.getStartDate());
        startDate.sendKeys(Keys.TAB);
        startDate.sendKeys(event.getStartTime());
        finders.waitById("end_date");
        final WebElement endDate = finders.findById("end_date");
        endDate.sendKeys(event.getEndDate());
        endDate.sendKeys(Keys.TAB);
        endDate.sendKeys(event.getEndTime());
        finders.clickByText("button", "Create");
    }

    public void updateEvent(final Event event) {
        finders.clickByText("a", "Edit");
        finders.setTextById("name", event.getName());
        finders.setTextById("description", event.getDescription());
        finders.waitById("start_date");
        final WebElement startDate = finders.findById("start_date");
        startDate.sendKeys(event.getStartDate());
        startDate.sendKeys(Keys.TAB);
        startDate.sendKeys(event.getStartTime());
        finders.waitById("end_date");
        final WebElement endDate = finders.findById("end_date");
        endDate.sendKeys(event.getEndDate());
        endDate.sendKeys(Keys.TAB);
        endDate.sendKeys(event.getEndTime());
        finders.clickByText("button", "Save");
        //TODO verify datetime
    }

    public void checkEventStatus(final Event event, final String status) {
        final WebElement titleWE = finders.findByText("b", event.getName());
        final WebElement parentWE = titleWE.findElement(Bys.parentClassName("div", "card-body"));
        parentWE.findElement(Bys.text("div", status));
    }

    public void addCandidates(final Event event) {
        final WebElement titleWE = finders.findByText("b", event.getName());
        final WebElement parentWE = titleWE.findElement(Bys.parentClassName("div", "card-body"));
        parentWE.findElement(Bys.text("a", "Manage Candidates")).click();

        event.getCandidates().forEach(candidate -> {
            finders.waitByText("button", "Add a Candidate");
            finders.clickByText("button", "Add a Candidate");
            finders.waitByText("button", "Save");
            finders.setTextById("name", candidate.getName());
            finders.setTextById("author", candidate.getAuthor());
            if (candidate.getLocation()!= null) {
                finders.setTextById("location", candidate.getLocation());
                finders.waitByText("li", candidate.getLocation());
                finders.findByText("li", candidate.getLocation()).click();
            }
            finders.setTextById("description", candidate.getDescription());
            finders.setTextById("image", candidate.getImage());
            finders.clickByText("button", "Save");
        });
    }

    public void publishEvent(final Event event) {
        final WebElement titleWE = finders.findByText("b", event.getName());
        final WebElement parentWE = titleWE.findElement(Bys.parentClassName("div", "card-body"));
        parentWE.findElement(Bys.text("a", "Edit")).click();
        finders.waitByText("button", "Publish");
        finders.clickByText("button", "Publish");
        finders.waitById("confirmPublish");
        finders.clickById("confirmPublish");
        finders.waitByText("a", "Manage Roles");
    }

    public void deleteEvent(final Event event) {
        final WebElement titleWE = finders.findByText("b", event.getName());
        final WebElement parentWE = titleWE.findElement(Bys.parentClassName("div", "card-body"));
        parentWE.findElement(Bys.text("span", "Delete")).click();
        finders.waitById("confirmDeleteButton");
        finders.clickById("confirmDeleteButton");
    }

    public void checkEventIsDeleted() {
        finders.waitByText("div", "Event deleted successfully");
    }

    public void checkEventIsUpdated() {
        finders.waitByText("div", "Event updated successfully");
    }

    public void checkEvent(final Event event) {
        final WebElement titleWE = finders.findByText("b", event.getName());
        final WebElement parentWE = titleWE.findElement(Bys.parentClassName("div", "card "));
        parentWE.click();
        finders.findByText("h1", event.getName());
        finders.findByText("p", event.getDescription());
        finders.findByText("p", format("%s - %s", formatDatetime(event.getStartDate(), event.getStartTime()), formatDatetime(event.getEndDate(), event.getEndTime())));
    }

    private String formatDatetime(final String date, final String time) {
        return format("%s/%s/%s %s:%s:00 %s", date.substring(0, 2), date.substring(2, 4), date.substring(4, 8), time.substring(0, 2), time.substring(2, 4), time.substring(4, 6));
    }

    public WebElement getAnEvent() {
        final List<WebElement> allByClassName = finders.findAllByClassName("event-card").stream()
                .filter(webElement -> {
                    try {
                        // Check if the sub-element exists and is displayed
                        return webElement.findElement(Bys.text("a", "Details")).isDisplayed();
                    } catch (NoSuchElementException e) {
                        // If the element is not found, return false
                        return false;
                    }
                })
                .collect(Collectors.toList());
        return helper.getRandomElement(allByClassName);
    }

    public void checkEvent(final WebElement webElement) {
        webElement.click();
        finders.findByClassName("event-card").click();
        finders.findByClassName("display-5");
        finders.findByClassName("description");
        finders.waitById("topBackButton");
        finders.findById("topBackButton").findElement(Bys.tagName("button")).click();
    }

    public void checkCandidates(final int index) {
        finders.waitByClassName("event-card");
        final List<WebElement> allByClassName = finders.findAllByClassName("candidate-card");
        allByClassName.get(index).click();
        finders.findByClassName("display-5");
        finders.findAllByClassName("info-row");
        finders.waitById("topBackButton");
        finders.findById("topBackButton").findElement(Bys.tagName("button")).click();
        if (index + 1 < allByClassName.size()) {
            checkCandidates(index + 1);
        }
    }

    public void checkCandidatesWithLocation(final int index) {
        finders.waitById("map_candidates");
        finders.waitById("map_votes");
        this.checkCandidates(0);
    }

    public void gotoEvent(final Event event) {
        homePage.visit();
        finders.waitByClassName("events");
        final Optional<WebElement> h5 = finders.findAllByClassName("event-card").stream()
                .filter(webElement -> {
                    try {
                        webElement.findElement(Bys.text("b", event.getName()));
                        return true;
                    } catch (final NoSuchElementException e) {
                        return false;
                    }
                })
                .findAny();
        h5.ifPresent(WebElement::click); // Clicks the element if present
    }
}
