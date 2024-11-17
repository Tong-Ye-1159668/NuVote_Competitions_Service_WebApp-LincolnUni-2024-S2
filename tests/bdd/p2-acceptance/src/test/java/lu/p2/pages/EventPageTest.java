package lu.p2.pages;

import lu.p2.io.Helper;
import lu.p2.models.Candidate;
import lu.p2.models.Event;
import lu.p2.selenium.Bys;
import lu.p2.selenium.Finders;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class EventPageTest {

    private HomePage homePage;
    private Finders finders;
    private Helper helper;
    private EventPage eventPage;

    @Before
    public void setUp() {
        homePage = mock(HomePage.class);
        finders = mock(Finders.class);
        helper = mock(Helper.class);
        eventPage = new EventPage(homePage, finders, helper);
    }

    @Test
    public void Can_create_an_event() {
        final String themeName = someString();
        final WebElement themeWE = mock(WebElement.class);
        final WebElement webElement = mock(WebElement.class);
        final Event event = mock(Event.class);

        // Given
        given(finders.findByText("h5", themeName)).willReturn(themeWE);
        given(themeWE.findElement(Bys.parentClassName("div", "card-body"))).willReturn(webElement);
        final String name = someString();
        final String description = someString();
        final String image = someString();
        given(event.getName()).willReturn(name);
        given(event.getDescription()).willReturn(description);
        given(event.getImage()).willReturn(image);
        final String startDate = someString();
        final String startTime = someString();
        final String endDate = someString();
        final String endTime = someString();
        given(event.getStartDate()).willReturn(startDate);
        given(event.getStartTime()).willReturn(startTime);
        given(event.getEndDate()).willReturn(endDate);
        given(event.getEndTime()).willReturn(endTime);
        final WebElement startDateWE = mock(WebElement.class);
        final WebElement endDateWE = mock(WebElement.class);
        given(finders.findById("start_date")).willReturn(startDateWE);
        given(finders.findById("end_date")).willReturn(endDateWE);

        // When
        eventPage.createEvent(event);

        // Then
        then(finders).should().clickByText("a", "Create an\n" + "            Event");
        then(finders).should().setTextById("name", name);
        then(finders).should().setTextById("event-description", description);
        then(finders).should().setTextById("image", image);
        then(finders).should().waitById("start_date");
        then(startDateWE).should().sendKeys(startDate);
        then(startDateWE).should().sendKeys(Keys.TAB);
        then(startDateWE).should().sendKeys(startTime);
        then(endDateWE).should().sendKeys(endDate);
        then(endDateWE).should().sendKeys(Keys.TAB);
        then(endDateWE).should().sendKeys(endTime);
        then(finders).should().clickByText("button", "Create");
    }

    @Test
    public void Can_update_an_event() {
        final String themeName = someString();
        final WebElement themeWE = mock(WebElement.class);
        final WebElement webElement = mock(WebElement.class);
        final Event event = mock(Event.class);

        // Given
        given(finders.findByText("h5", themeName)).willReturn(themeWE);
        given(themeWE.findElement(Bys.parentClassName("div", "card-body"))).willReturn(webElement);
        final String name = someString();
        final String description = someString();
        given(event.getName()).willReturn(name);
        given(event.getDescription()).willReturn(description);
        final String startDate = someString();
        final String startTime = someString();
        final String endDate = someString();
        final String endTime = someString();
        given(event.getStartDate()).willReturn(startDate);
        given(event.getStartTime()).willReturn(startTime);
        given(event.getEndDate()).willReturn(endDate);
        given(event.getEndTime()).willReturn(endTime);
        final WebElement startDateWE = mock(WebElement.class);
        final WebElement endDateWE = mock(WebElement.class);
        given(finders.findById("start_date")).willReturn(startDateWE);
        given(finders.findById("end_date")).willReturn(endDateWE);

        // When
        eventPage.updateEvent(event);

        // Then
        then(finders).should().clickByText("a", "Edit");
        then(finders).should().setTextById("name", name);
        then(finders).should().setTextById("description", description);
        then(finders).should().waitById("start_date");
        then(startDateWE).should().sendKeys(startDate);
        then(startDateWE).should().sendKeys(Keys.TAB);
        then(startDateWE).should().sendKeys(startTime);
        then(endDateWE).should().sendKeys(endDate);
        then(endDateWE).should().sendKeys(Keys.TAB);
        then(endDateWE).should().sendKeys(endTime);
        then(finders).should().clickByText("button", "Save");
    }

    @Test
    public void Can_check_event_status() {
        final Event event = mock(Event.class);
        final String name = someString();
        final WebElement titleWE = mock(WebElement.class);
        final WebElement webElement = mock(WebElement.class);
        final String status = someString();

        // Given
        given(event.getName()).willReturn(name);
        given(finders.findByText("b", name)).willReturn(titleWE);
        given(titleWE.findElement(Bys.parentClassName("div", "card-body"))).willReturn(webElement);
        given(webElement.findElement(Bys.text("div", status))).willReturn(mock(WebElement.class));

        // When
        eventPage.checkEventStatus(event, status);

        // Then
        then(finders).should().findByText("b", name);
        then(titleWE).should().findElement(Bys.parentClassName("div", "card-body"));
    }

    @Test
    public void Can_add_candidates() {
        final Event event = mock(Event.class);
        final String eventName = someString();
        final WebElement titleWE = mock(WebElement.class);
        final WebElement parentWE = mock(WebElement.class);
        final WebElement manageBtn = mock(WebElement.class);
        final Candidate candidate = mock(Candidate.class);
        final String name = someString();
        final String author = someString();
        final String description = someString();
        final String image = someString();

        // Given
        given(event.getName()).willReturn(eventName);
        given(finders.findByText("b", eventName)).willReturn(titleWE);
        given(titleWE.findElement(Bys.parentClassName("div", "card-body"))).willReturn(parentWE);
        given(parentWE.findElement(Bys.text("a", "Manage Candidates"))).willReturn(manageBtn);
        given(event.getCandidates()).willReturn(List.of(candidate));
        given(candidate.getName()).willReturn(name);
        given(candidate.getAuthor()).willReturn(author);
        given(candidate.getDescription()).willReturn(description);
        given(candidate.getImage()).willReturn(image);

        // When
        eventPage.addCandidates(event);

        // Then
        then(finders).should().findByText("b", eventName);
        then(titleWE).should().findElement(Bys.parentClassName("div", "card-body"));
        then(manageBtn).should().click();

        then(finders).should().waitByText("button", "Add a Candidate");
        then(finders).should().clickByText("button", "Add a Candidate");
        then(finders).should().setTextById("name", name);
        then(finders).should().setTextById("author", author);
        then(finders).should().setTextById("description", description);
        then(finders).should().setTextById("image", image);
        then(finders).should().waitByText("button", "Save");
    }

    @Test
    public void Can_publish_an_event() {
        final Event event = mock(Event.class);
        final String eventName = someString();
        final WebElement titleWE = mock(WebElement.class);
        final WebElement parentWE = mock(WebElement.class);
        final WebElement editBtn = mock(WebElement.class);


        // Given
        given(event.getName()).willReturn(eventName);
        given(finders.findByText("b", eventName)).willReturn(titleWE);
        given(titleWE.findElement(Bys.parentClassName("div", "card-body"))).willReturn(parentWE);
        given(parentWE.findElement(Bys.text("a", "Edit"))).willReturn(editBtn);

        // When
        eventPage.publishEvent(event);

        // Then
        then(finders).should().findByText("b", eventName);
        then(titleWE).should().findElement(Bys.parentClassName("div", "card-body"));
        then(editBtn).should().click();

        then(finders).should().waitByText("button", "Publish");
        then(finders).should().clickByText("button", "Publish");
    }

    @Test
    public void Can_delete_an_event() {
        final Event event = mock(Event.class);
        final String eventName = someString();
        final WebElement titleWE = mock(WebElement.class);
        final WebElement parentWE = mock(WebElement.class);
        final WebElement editBtn = mock(WebElement.class);


        // Given
        given(event.getName()).willReturn(eventName);
        given(finders.findByText("b", eventName)).willReturn(titleWE);
        given(titleWE.findElement(Bys.parentClassName("div", "card-body"))).willReturn(parentWE);
        given(parentWE.findElement(Bys.text("span", "Delete"))).willReturn(editBtn);

        // When
        eventPage.deleteEvent(event);

        // Then
        then(finders).should().findByText("b", eventName);
        then(titleWE).should().findElement(Bys.parentClassName("div", "card-body"));
        then(editBtn).should().click();

        then(finders).should().waitById("confirmDeleteButton");
        then(finders).should().clickById("confirmDeleteButton");
    }

    @Test
    public void Can_check_event_is_deleted() {
        // Given

        // When
        eventPage.checkEventIsDeleted();

        // Then
        then(finders).should().waitByText("div", "Event deleted successfully");
    }

    @Test
    public void Can_check_event_is_updated() {
        // Given

        // When
        eventPage.checkEventIsUpdated();

        // Then
        then(finders).should().waitByText("div", "Event updated successfully");
    }

    @Test
    public void Can_check_event_details() {
        final Event event = mock(Event.class);
        final String eventName = someString();
        final String eventDescription = someString();
        final WebElement titleWE = mock(WebElement.class);
        final WebElement parentWE = mock(WebElement.class);


        // Given
        given(event.getName()).willReturn(eventName);
        given(finders.findByText("b", eventName)).willReturn(titleWE);
        given(titleWE.findElement(Bys.parentClassName("div", "card "))).willReturn(parentWE);
        given(event.getDescription()).willReturn(eventDescription);
        given(event.getStartDate()).willReturn(someAlphanumericString(8));
        given(event.getStartTime()).willReturn(someAlphanumericString(7));
        given(event.getEndDate()).willReturn(someAlphanumericString(8));
        given(event.getEndTime()).willReturn(someAlphanumericString(7));
        given(finders.findByText("h1", eventName)).willReturn(mock(WebElement.class));
        given(finders.findByText("p", eventDescription)).willReturn(mock(WebElement.class));
        given(finders.findByText("h1", eventName)).willReturn(mock(WebElement.class));


        // When
        eventPage.checkEvent(event);

        // Then
        then(finders).should().findByText("b", eventName);
        then(titleWE).should().findElement(Bys.parentClassName("div", "card "));
        then(parentWE).should().click();
        then(finders).should().findByText("h1", eventName);
        then(finders).should().findByText("p", eventDescription);
//        then(finders).should().findByText("p", eventName);
    }

    @Test
    public void Can_get_a_random_event() {
        final WebElement expected = mock(WebElement.class, RETURNS_DEEP_STUBS);
        final WebElement e1 = mock(WebElement.class, RETURNS_DEEP_STUBS);
        final WebElement e2 = mock(WebElement.class, RETURNS_DEEP_STUBS);
        final List<WebElement> webElements = List.of(expected, e1, e2);

        // Given
        given(finders.findAllByClassName("event-card")).willReturn(webElements);
        given(expected.findElement(Bys.text("a", "Details")).isDisplayed()).willReturn(TRUE);
        given(e1.findElement(Bys.text("a", "Details")).isDisplayed()).willReturn(FALSE);
        given(helper.getRandomElement(List.of(expected))).willReturn(expected);

        // When
        final WebElement actual = eventPage.getAnEvent();

        // Then
        assertThat(TRUE, is(webElements.contains(actual)));
    }

    @Test
    public void Can_check_event() {
        final WebElement webElement = mock(WebElement.class);
        final WebElement eventCardWE = mock(WebElement.class);
        final WebElement backBtnWE = mock(WebElement.class);
        final WebElement btn = mock(WebElement.class);

        // Given
        given(finders.findByClassName("event-card")).willReturn(eventCardWE);
        given(finders.findByClassName("display-5")).willReturn(mock(WebElement.class));
        given(finders.findByClassName("description")).willReturn(mock(WebElement.class));
        given(finders.findById("topBackButton")).willReturn(backBtnWE);
        given(backBtnWE.findElement(Bys.tagName("button"))).willReturn(btn);

        // When
        eventPage.checkEvent(webElement);

        // Then
        then(webElement).should().click();
        then(eventCardWE).should().click();
        then(finders).should().waitById("topBackButton");
        then(btn).should().click();
    }

    @Test
    public void Can_check_candidates() {
        final WebElement candidate1WE = mock(WebElement.class);
        final WebElement candidate2WE = mock(WebElement.class);
        final WebElement backBtnWE = mock(WebElement.class);
        final WebElement btn = mock(WebElement.class);
        final List<WebElement> list = List.of(candidate1WE, candidate2WE);

        // Given
        given(finders.findAllByClassName("candidate-card")).willReturn(list);
        given(finders.findByClassName("display-5")).willReturn(mock(WebElement.class));
        given(finders.findByClassName("info-row")).willReturn(mock(WebElement.class));
        given(finders.findById("topBackButton")).willReturn(backBtnWE);
        given(backBtnWE.findElement(Bys.tagName("button"))).willReturn(btn);

        // When
        eventPage.checkCandidates(0);

        // Then
        then(finders).should(times(list.size())).waitByClassName("event-card");
        then(finders).should(times(list.size())).waitById("topBackButton");
    }
}