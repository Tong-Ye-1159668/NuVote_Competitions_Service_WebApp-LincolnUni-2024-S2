package lu.p2.pages;

import lu.p2.io.DateTimeComparison;
import lu.p2.io.Helper;
import lu.p2.models.Ticket;
import lu.p2.models.User;
import lu.p2.selenium.Bys;
import lu.p2.selenium.Finders;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.lang.Boolean.TRUE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

@Component
public class TicketPage {
    private final Finders finders;
    private final DateTimeComparison dateTimeComparison;
    private final Helper helper;

    public TicketPage(final Finders finders, final DateTimeComparison dateTimeComparison, final Helper helper) {
        this.finders = finders;
        this.dateTimeComparison = dateTimeComparison;
        this.helper = helper;
    }

    public void clickHelpBtn() {
        finders.waitByClassName("help-link");
        finders.clickByClassName("help-link");
    }

    public Ticket createATicket(final Ticket ticket) {
        finders.waitByText("button", "Submit");
        finders.setTextById("subject", ticket.getSubject());
        finders.setTextById("content", ticket.getContent());
        finders.clickByText("button", "Submit");
        return ticket;
    }

    public void cancelTheTicket(final Ticket ticket) {
        clickTicketDetailsBtn();
        finders.findByText("h5", ticket.getSubject());
        finders.findByText("p", ticket.getContent());

        finders.clickByText("button", "Cancel Ticket");
    }

    public void ticketIsCreated() {
        finders.waitById("alert-container");
        finders.findByText("div", "Ticket has created successfully");
    }

    public void ticketIsCancelled(final Ticket ticket) {
        finders.waitById("alert-container");
        finders.findByText("div", "Ticket has cancelled successfully");
        final WebElement h5 = finders.findByText("h5", ticket.getSubject());
        finders.findByText("p", ticket.getContent());
        final WebElement parent = h5.findElement(Bys.parentClassName("li", "mb-3"));
        parent.findElement(Bys.text("span", "closed"));
        parent.findElement(Bys.text("span", "cancelled"));
    }

    public void checkMyTicket(final Ticket ticket) {
        finders.waitByText("a", "Details");
        finders.findByText("h5", ticket.getSubject());
        finders.findByText("p", ticket.getContent());
        finders.waitByText("p", "Created at: ");
        final String datetime = finders.findByText("p", "Created at: ").findElement(Bys.tagName("strong")).getText();
        assertThat(TRUE, is(dateTimeComparison.isLessThanSomeMinutes(datetime, 3)));
        finders.findByText("p", "Assigned to: ").findElement(Bys.text("strong", "None"));

    }

    public void clickTicketDetailsBtn() {
        finders.waitById("tickets");
        finders.findById("tickets").findElement(Bys.text("a", "Details"))
                .click();
    }

    public void replyTo(final Ticket ticket) {
        clickTicketDetailsBtn();
        finders.findByText("h5", ticket.getSubject());
        final String reply = someAlphanumericString(50);
        finders.setTextById("content", reply);
        finders.clickByText("button", "Send Reply");
        finders.waitByClassName("card-body");
        finders.findByText("p", reply);
        final String datetimeSting = finders.findByText("small", "Posted at: ").getText();
        String datetime = datetimeSting.substring(11);
        assertThat(TRUE, is(dateTimeComparison.isLessThanSomeMinutes(datetime, 3)));
    }

    public void checkMyReplies(final Ticket ticket, final User user) {
        finders.waitByText("h5", "Replies");
        finders.findByText("strong", user.getUsername());
    }

    public void visitMgmtPage() {
        finders.clickByText("a", "Tickets");
    }

    public boolean ticketStatusIsNew(final Ticket ticket) {
        finders.waitByClassName("row");
        final WebElement parentWE = finders.findByText("h5", ticket.getSubject()).findElement(Bys.parentClassName("div", "card-body"));
        parentWE.findElement(Bys.text("small", "Assigned to: None"));
        final String datetimeString = parentWE.findElement(Bys.text("small", "Created by:")).getText();
        final String datetime = datetimeString.substring(datetimeString.length() - 22);
        return dateTimeComparison.isLessThanSomeMinutes(datetime, 3);
    }

    public boolean ticketStatusIsOpen(final Ticket ticket) {
        finders.waitByClassName("row");
        final WebElement parentWE = finders.findByText("h5", ticket.getSubject()).findElement(Bys.parentClassName("div", "card-body"));
        final String datetimeString = parentWE.findElement(Bys.text("small", "Updated by:")).getText();
        final String datetime = datetimeString.substring(datetimeString.length() - 22);
        return dateTimeComparison.isLessThanSomeMinutes(datetime, 3);
    }

    public boolean ticketStatusIsClosed(final Ticket ticket) {
        finders.waitByClassName("row");
        final WebElement parentWE = finders.findByText("h5", ticket.getSubject()).findElement(Bys.parentClassName("div", "card-body"));
        final String datetimeString = parentWE.findElement(Bys.text("small", "Completed by:")).getText();
        final String datetime = datetimeString.substring(datetimeString.length() - 22);
        return dateTimeComparison.isLessThanSomeMinutes(datetime, 3);
    }

    public void assignTheTicket(final Ticket ticket) {
        clickEditBtn(ticket);
        final WebElement assignTo = finders.findById("assign_to");
        assignTo.click();
        final List<WebElement> elements = assignTo.findElements(Bys.tagName("option"));
        helper.getRandomElement(elements).click();
        finders.clickByText("button", "Save");
        assertThat(TRUE, is(ticketStatusIsOpen(ticket)));
    }

    public void unassignTheTicket(final Ticket ticket) {
        clickEditBtn(ticket);
        final WebElement assignTo = finders.findById("assign_to");
        assignTo.click();
        assignTo.findElement(Bys.text("option", "Unassign")).click();
        finders.clickByText("button", "Save");
        assertThat(TRUE, is(ticketStatusIsNew(ticket)));
    }

    private void clickEditBtn(final Ticket ticket) {
        finders.waitByClassName("row");
        final WebElement parentWE = finders.findByText("h5", ticket.getSubject()).findElement(Bys.parentClassName("div", "card-body"));
        parentWE.findElement(Bys.text("a", "Edit")).click();
        finders.waitByText("button", "Save");

    }

    public void closeATicket(final Ticket ticket, final String resolution) {
        clickEditBtn(ticket);
        finders.waitById("solution");
        final WebElement solution = finders.findById("solution");
        solution.click();
        solution.findElement(Bys.text("option", resolution)).click();
        finders.waitByText("button", "Update Solution");
        finders.clickByText("button", "Update Solution");
    }
}
