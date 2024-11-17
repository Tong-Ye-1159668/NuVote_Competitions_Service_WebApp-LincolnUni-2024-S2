package lu.p2.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lu.p2.factories.TicketFactory;
import lu.p2.holders.TicketHolder;
import lu.p2.holders.UserHolder;
import lu.p2.models.Ticket;
import lu.p2.pages.TicketPage;

import static java.lang.Boolean.TRUE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class TicketSteps {
    private final TicketPage ticketPage;
    private final TicketFactory ticketFactory;
    private final TicketHolder ticketHolder;
    private final UserHolder userHolder;

    public TicketSteps(final TicketPage ticketPage, final TicketFactory ticketFactory, final TicketHolder ticketHolder, final UserHolder userHolder) {
        this.ticketPage = ticketPage;
        this.ticketFactory = ticketFactory;
        this.ticketHolder = ticketHolder;
        this.userHolder = userHolder;
    }

    @When("I create a ticket")
    @Given("I have created a ticket")
    public void iCreateATicket() {
        ticketPage.clickHelpBtn();
        final Ticket ticket = ticketPage.createATicket(ticketFactory.createATicket());
        ticketHolder.set(ticket);
    }

    @Then("My ticket is created successfully")
    public void myTicketIsCreatedSuccessfully() {
        ticketPage.ticketIsCreated();
    }

    @And("I can view the detail of the ticket")
    public void iCanViewTheDetailOfTheTicket() {
        ticketPage.checkMyTicket(ticketHolder.get());
    }

    @When("I cancel the ticket")
    public void iCancelTheTicket() {
        ticketPage.cancelTheTicket(ticketHolder.get());
    }

    @Then("My ticket is cancelled successfully")
    public void myTicketIsCancelledSuccessfully() {
        ticketPage.ticketIsCancelled(ticketHolder.get());
    }

    @When("I reply to the ticket")
    public void iReplyToTheTicket() {
        ticketPage.replyTo(ticketHolder.get());
    }

    @Then("I should see all replies about the ticket")
    public void iShouldSeeAllRepliesAboutTheTicket() {
        ticketPage.checkMyReplies(ticketHolder.get(), userHolder.get());
    }

    @And("I visit ticket mgmt page")
    public void iVisitTicketMgmtPage() {
        ticketPage.visitMgmtPage();
    }

    @And("the ticket status is new")
    public void theTicketStatusIsNew() {
        assertThat(TRUE, is(ticketPage.ticketStatusIsNew(ticketHolder.get())));
    }

    @Then("the ticket status is updated to open")
    public void theTicketStatusIsUpdatedToOpen() {
        assertThat(TRUE, is(ticketPage.ticketStatusIsOpen(ticketHolder.get())));
    }

    @Then("the ticket status is updated to closed")
    public void theTicketStatusIsUpdatedToClosed() {
        assertThat(TRUE, is(ticketPage.ticketStatusIsClosed(ticketHolder.get())));
    }

    @When("I assign the ticket to a helper")
    public void iAssignTheTicketToAHelper() {
        ticketPage.assignTheTicket(ticketHolder.get());
    }

    @When("I unassign the ticket to a helper")
    public void iUnassignTheTicketToAHelper() {
        ticketPage.unassignTheTicket(ticketHolder.get());
    }

    @When("I close the ticket with {string}")
    public void iCloseTheTicketWith(String resolution) {
        ticketPage.closeATicket(ticketHolder.get(), resolution);
    }
}
