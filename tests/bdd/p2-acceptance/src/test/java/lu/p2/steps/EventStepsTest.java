package lu.p2.steps;

import lu.p2.factories.EventFactory;
import lu.p2.holders.EventHolder;
import lu.p2.holders.ThemeApplicationHolder;
import lu.p2.holders.WebElementHolder;
import lu.p2.models.Event;
import lu.p2.models.ThemeApplication;
import lu.p2.pages.EventPage;
import lu.p2.pages.ThemePage;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

public class EventStepsTest {

    private ThemePage themePage;
    private ThemeApplicationHolder themeApplicationHolder;
    private EventPage eventPage;
    private EventHolder eventHolder;
    private EventFactory eventFactory;
    private WebElementHolder webElementHolder;
    private EventSteps eventSteps;

    @Before
    public void setUp() {
        themePage = mock(ThemePage.class);
        themeApplicationHolder = mock(ThemeApplicationHolder.class);
        eventPage = mock(EventPage.class);
        eventHolder = mock(EventHolder.class);
        eventFactory = mock(EventFactory.class);
        webElementHolder = mock(WebElementHolder.class);
        eventSteps = new EventSteps(themePage, themeApplicationHolder, eventPage, eventHolder, eventFactory, webElementHolder);
    }

    @Test
    public void Can_create_an_event() {
        final ThemeApplication themeApplication = mock(ThemeApplication.class);
        final Event event = mock(Event.class);

        // Given
        given(themeApplicationHolder.get()).willReturn(themeApplication);
        given(eventFactory.createEvent(10)).willReturn(event);

        // When
        eventSteps.iCreateAnEvent();

        // Then
        then(themePage).should().gotoMyTheme(themeApplication);
        then(eventPage).should().createEvent(event);
        then(eventHolder).should().set(event);
    }

    @Test
    public void Can_check_event_status() {
        final Event event = mock(Event.class);

        // Given
        given(eventHolder.get()).willReturn(event);

        // When
        eventSteps.theEventIsCreated();

        // Then
        then(eventPage).should().checkEventStatus(event, "draft");

    }

    @Test
    public void Can_add_candidates() {
        final Event event = mock(Event.class);

        // Given
        given(eventHolder.get()).willReturn(event);

        // When
        eventSteps.iCanAddCandidatesToTheEvent();

        // Then
        then(eventPage).should().addCandidates(event);
    }

    @Test
    public void Can_publish_an_event() {
        final ThemeApplication themeApplication = mock(ThemeApplication.class);
        final Event event = mock(Event.class);

        // Given
        given(themeApplicationHolder.get()).willReturn(themeApplication);
        given(eventHolder.get()).willReturn(event);

        // When
        eventSteps.iCanPublishTheEvent();

        // Then
        then(themePage).should().gotoMyTheme(themeApplication);
        then(eventPage).should().publishEvent(event);
    }

    @Test
    public void Can_delete_an_event() {
        final Event event = mock(Event.class);

        // Given
        given(eventHolder.get()).willReturn(event);

        // When
        eventSteps.iDeleteTheEvent();

        // Then
        then(eventPage).should().deleteEvent(event);
    }

    @Test
    public void Can_check_the_event_is_deleted() {
        final Event event = mock(Event.class);

        // Given
        given(eventHolder.get()).willReturn(event);

        // When
        eventSteps.theEventIsDeleted();

        // Then
        then(eventPage).should().checkEventIsDeleted();
    }

    @Test
    public void Can_update_event_details() {
        final Event event = mock(Event.class);
        final Event updatedEvent = mock(Event.class);

        // Given
        given(eventHolder.get()).willReturn(event);
        given(eventFactory.update(event)).willReturn(updatedEvent);

        // When
        eventSteps.iUpdateTheEventDetails();

        // Then
        then(eventPage).should().updateEvent(updatedEvent);
        then(eventHolder).should().set(updatedEvent);
    }

    @Test
    public void Can_check_event_details_is_updated() {
        // Given

        // When
        eventSteps.theEventDetailsIsSuccessfullyUpdated();

        // Then
        then(eventPage).should().checkEventIsUpdated();
    }

    @Test
    public void Can_view_event_details_is_correct() {
        final Event event = mock(Event.class);

        // Given
        given(eventHolder.get()).willReturn(event);

        // When
        eventSteps.iCanViewTheUpdatedEventInformation();

        // Then
        then(eventPage).should().checkEvent(event);
    }

    @Test
    public void Can_visit_the_event_details() {
        final WebElement webElement = mock(WebElement.class);

        // Given
        given(eventPage.getAnEvent()).willReturn(webElement);

        // When
        eventSteps.iVisitTheEventDetails();

        // Then
        then(webElementHolder).should().set(webElement);
    }

    @Test
    public void Can_see_event_details() {
        final WebElement webElement = mock(WebElement.class);

        // Given
        given(webElementHolder.get()).willReturn(webElement);

        // When
        eventSteps.iSeeEventDetails();

        // Then
        then(eventPage).should().checkEvent(webElement);
    }

    @Test
    public void Can_see_candidates_details() {
        // Given

        // When
        eventSteps.iSeeCandidatesDetails();

        // Then
        then(eventPage).should().checkCandidates(0);
    }

    @Test
    public void Can_add_candidate_with_location() {
        final Event event = mock(Event.class);

        // Given
        given(eventHolder.get()).willReturn(event);

        // When
        eventSteps.iAddACandidateWithLocation();


        // Then
        then(eventPage).should().addCandidates(event);
    }

    @Test
    public void Cna_create_an_event_with_location() {
        final ThemeApplication themeApplication = mock(ThemeApplication.class);
        final Event event = mock(Event.class);

        // Given
        given(themeApplicationHolder.get()).willReturn(themeApplication);
        given(eventFactory.createEventWithLocation(10)).willReturn(event);

        // When
        eventSteps.iCreateAnEventWithLocation();

        // Then
        then(eventPage).should().createEvent(event);
        then(eventHolder).should().set(event);
    }

    @Test
    public void Can_see_candidate_on_the_map_view() {
        final Event event = mock(Event.class);

        // Given
        given(eventHolder.get()).willReturn(event);

        // When
        eventSteps.iShouldSeeTheCandidateOnTheMapView();

        // Then
        then(eventPage).should().gotoEvent(event);
        then(eventPage).should().checkCandidatesWithLocation(0);

    }
}