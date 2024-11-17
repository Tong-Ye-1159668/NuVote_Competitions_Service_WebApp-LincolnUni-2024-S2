package lu.p2.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lu.p2.factories.EventFactory;
import lu.p2.holders.EventHolder;
import lu.p2.holders.ThemeApplicationHolder;
import lu.p2.holders.WebElementHolder;
import lu.p2.models.Event;
import lu.p2.pages.EventPage;
import lu.p2.pages.ThemePage;


public class EventSteps {

    private final ThemePage themePage;
    private final ThemeApplicationHolder themeApplicationHolder;
    private final EventPage eventPage;
    private final EventHolder eventHolder;
    private final EventFactory eventFactory;
    private final WebElementHolder webElementHolder;

    public EventSteps(final ThemePage themePage, final ThemeApplicationHolder themeApplicationHolder, final EventPage eventPage, final EventHolder eventHolder, final EventFactory eventFactory, final WebElementHolder webElementHolder) {
        this.themePage = themePage;
        this.themeApplicationHolder = themeApplicationHolder;
        this.eventPage = eventPage;
        this.eventHolder = eventHolder;
        this.eventFactory = eventFactory;
        this.webElementHolder = webElementHolder;
    }

    @When("I create an event")
    public void iCreateAnEvent() {
        themePage.gotoMyTheme(themeApplicationHolder.get());
        final Event event = eventFactory.createEvent(10);
        eventPage.createEvent(event);
        eventHolder.set(event);
    }

    @Then("the event is created")
    public void theEventIsCreated() {
        eventPage.checkEventStatus(eventHolder.get(), "draft");
    }

    @And("I can add candidates to the event")
    public void iCanAddCandidatesToTheEvent() {
        eventPage.addCandidates(eventHolder.get());
    }

    @And("I can publish the event")
    public void iCanPublishTheEvent() {
        themePage.gotoMyTheme(themeApplicationHolder.get());
        eventPage.publishEvent(eventHolder.get());
    }

    @When("I delete the event")
    public void iDeleteTheEvent() {
        eventPage.deleteEvent(eventHolder.get());
    }

    @Then("the event is deleted")
    public void theEventIsDeleted() {
        eventPage.checkEventIsDeleted();
    }

    @When("I update the event details")
    public void iUpdateTheEventDetails() {
        final Event event = eventFactory.update(eventHolder.get());
        eventPage.updateEvent(event);
        eventHolder.set(event);
    }

    @Then("the event details is successfully updated")
    public void theEventDetailsIsSuccessfullyUpdated() {
        eventPage.checkEventIsUpdated();
    }

    @And("I can view the updated event information")
    public void iCanViewTheUpdatedEventInformation() {
        eventPage.checkEvent(eventHolder.get());
    }

    @When("I visit the event details")
    public void iVisitTheEventDetails() {
        webElementHolder.set(eventPage.getAnEvent());
    }

    @Then("I see event details")
    public void iSeeEventDetails() {
        eventPage.checkEvent(webElementHolder.get());
    }

    @And("I see candidates details")
    public void iSeeCandidatesDetails() {
        eventPage.checkCandidates(0);
    }

    @When("I add a candidate with location")
    public void iAddACandidateWithLocation() {
        eventPage.addCandidates(eventHolder.get());
    }

    @And("I create an event with location")
    public void iCreateAnEventWithLocation() {
        themePage.gotoMyTheme(themeApplicationHolder.get());
        final Event event = eventFactory.createEventWithLocation(10);
        eventPage.createEvent(event);
        eventHolder.set(event);
    }

    @Then("I should see the candidate on the map view")
    public void iShouldSeeTheCandidateOnTheMapView() {
        eventPage.gotoEvent(eventHolder.get());
        eventPage.checkCandidatesWithLocation(0);
    }
}
