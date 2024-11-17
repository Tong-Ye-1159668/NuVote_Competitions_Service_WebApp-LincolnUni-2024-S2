package lu.p2.pages;

import lu.p2.io.DateTimeComparison;
import lu.p2.io.Helper;
import lu.p2.models.Ticket;
import lu.p2.models.User;
import lu.p2.selenium.Bys;
import lu.p2.selenium.Finders;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import java.util.List;

import static java.lang.Boolean.TRUE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class TicketPageTest {

    private Finders finders;
    private DateTimeComparison dateTimeComparison;
    private Helper helper;
    private TicketPage ticketPage;

    @Before
    public void setUp() {
        finders = mock(Finders.class);
        dateTimeComparison = mock(DateTimeComparison.class);
        helper = mock(Helper.class);
        ticketPage = new TicketPage(finders, dateTimeComparison, helper);
    }

    @Test
    public void Can_click_help_btn() {
        // Given

        // When
        ticketPage.clickHelpBtn();

        // Then
        then(finders).should().waitByClassName("help-link");
        then(finders).should().clickByClassName("help-link");
    }

    @Test
    public void Can_create_a_ticket() {
        final Ticket ticket = mock(Ticket.class);
        final String subject = someString();
        final String content = someString();

        // Given
        given(ticket.getSubject()).willReturn(subject);
        given(ticket.getContent()).willReturn(content);

        // When
        final Ticket actual = ticketPage.createATicket(ticket);

        // Then
        then(finders).should().waitByText("button", "Submit");
        then(finders).should().setTextById("subject", subject);
        then(finders).should().setTextById("content", content);
        then(finders).should().clickByText("button", "Submit");
        assertThat(actual, is(ticket));
    }

    @Test
    public void Can_cancel_a_ticket() {
        final Ticket ticket = mock(Ticket.class);
        final WebElement tickets = mock(WebElement.class);
        final WebElement detailsWE = mock(WebElement.class);
        final String subject = someString();
        final String content = someString();

        // Given
        given(finders.findById("tickets")).willReturn(tickets);
        given(tickets.findElement(Bys.text("a", "Details"))).willReturn(detailsWE);
        given(ticket.getSubject()).willReturn(subject);
        given(ticket.getContent()).willReturn(content);

        given(finders.findByText("h5", subject)).willReturn(mock(WebElement.class));
        given(finders.findByText("p", content)).willReturn(mock(WebElement.class));

        // When
        ticketPage.cancelTheTicket(ticket);

        // Then
        then(finders).should().waitById("tickets");
        then(detailsWE).should().click();
    }

    @Test
    public void Can_view_ticket_is_created() {
        // Given

        // When
        ticketPage.ticketIsCreated();

        // Then
        then(finders).should().waitById("alert-container");
        then(finders).should().findByText("div", "Ticket has created successfully");
    }

    @Test
    public void Can_check_ticket_is_cancelled() {
        final Ticket ticket = mock(Ticket.class);
        final String subject = someString();
        final String content = someString();
        final WebElement h5WE = mock(WebElement.class);
        final WebElement parentWE = mock(WebElement.class);

        // Given
        given(ticket.getSubject()).willReturn(subject);
        given(ticket.getContent()).willReturn(content);
        given(finders.findByText("h5", subject)).willReturn(h5WE);
        given(finders.findByText("p", content)).willReturn(mock(WebElement.class));
        given(h5WE.findElement(Bys.parentClassName("li", "mb-3"))).willReturn(parentWE);
        given(parentWE.findElement(Bys.text("span", "closed"))).willReturn(mock(WebElement.class));
        given(parentWE.findElement(Bys.text("span", "cancelled"))).willReturn(mock(WebElement.class));

        // When
        ticketPage.ticketIsCancelled(ticket);

        // Then
        then(finders).should().waitById("alert-container");
        then(finders).should().findByText("div", "Ticket has cancelled successfully");
    }

    @Test
    public void Can_check_my_ticket() {
        final Ticket ticket = mock(Ticket.class);
        final String subject = someString();
        final String content = someString();
        final WebElement createdAtWE = mock(WebElement.class);
        final WebElement strongWE = mock(WebElement.class);
        final String datetimeString = someString();
        final WebElement assignToWE = mock(WebElement.class);

        // Given
        given(ticket.getSubject()).willReturn(subject);
        given(ticket.getContent()).willReturn(content);
        given(finders.findByText("h5", subject)).willReturn(mock(WebElement.class));
        given(finders.findByText("p", content)).willReturn(mock(WebElement.class));
        given(finders.findByText("p", "Created at: ")).willReturn(createdAtWE);

        given(createdAtWE.findElement(Bys.tagName("strong"))).willReturn(strongWE);
        given(strongWE.getText()).willReturn(datetimeString);
        given(dateTimeComparison.isLessThanSomeMinutes(datetimeString, 3)).willReturn(TRUE);
        given(finders.findByText("p", "Assigned to: ")).willReturn(assignToWE);
        given(assignToWE.findElement(Bys.text("strong", "None"))).willReturn(mock(WebElement.class));

        // When
        ticketPage.checkMyTicket(ticket);

        // Then
        then(finders).should().waitByText("a", "Details");
        then(finders).should().waitByText("p", "Created at: ");
    }

    @Test
    public void Can_reply_ticket() {

        final Ticket ticket = mock(Ticket.class);
        final WebElement tickets = mock(WebElement.class);
        final WebElement detailsWE = mock(WebElement.class);
        final WebElement dateWE = mock(WebElement.class);
        final String dateString = someString(50);
        final String date = dateString.substring(11);

        // Given
        given(finders.findById("tickets")).willReturn(tickets);
        given(tickets.findElement(Bys.text("a", "Details"))).willReturn(detailsWE);
        given(finders.findByText("h5", ticket.getSubject())).willReturn(mock(WebElement.class));
        given(finders.findByText("small", "Posted at: ")).willReturn(dateWE);
        given(dateWE.getText()).willReturn(dateString);
        given(dateTimeComparison.isLessThanSomeMinutes(date, 3)).willReturn(TRUE);

        // When
        ticketPage.replyTo(ticket);

        // Then
        then(finders).should().waitById("tickets");
        then(detailsWE).should().click();
        then(finders).should().setTextById(anyString(), anyString());
        then(finders).should().clickByText("button", "Send Reply");
        then(finders).should().waitByClassName("card-body");
        then(finders).should(times(2)).findByText(anyString(), anyString());
        then(finders).should().findByText("small", "Posted at: ");

    }

    @Test
    public void Can_check_all_replies_of_the_ticket() {
        final User user = mock(User.class);
        final String username = someString();

        // Given
        given(user.getUsername()).willReturn(username);

        // When
        ticketPage.checkMyReplies(mock(Ticket.class), user);

        // Then
        then(finders).should().waitByText("h5", "Replies");
        then(finders).should().findByText("strong", username);
    }

    @Test
    public void Can_visit_mgmt_page() {
        // Given

        // When
        ticketPage.visitMgmtPage();

        // Then
        then(finders).should().clickByText("a", "Tickets");
    }

    @Test
    public void Can_check_ticket_is_new() {
        final Ticket ticket = mock(Ticket.class);
        final String subject = someString();
        final WebElement subjectWE = mock(WebElement.class);
        final WebElement parentWE = mock(WebElement.class);
        final WebElement datetimeWE = mock(WebElement.class);
        final String datetimeString = someString();
        final String datetime = datetimeString.substring(datetimeString.length() - 22);

        // Given
        given(ticket.getSubject()).willReturn(subject);
        given(finders.findByText("h5", subject)).willReturn(subjectWE);
        given(subjectWE.findElement(Bys.parentClassName("div", "card-body"))).willReturn(parentWE);

        given(parentWE.findElement(Bys.text("small", "Assigned to: None"))).willReturn(mock(WebElement.class));

        given(parentWE.findElement(Bys.text("small", "Created by:"))).willReturn(datetimeWE);
        given(datetimeWE.getText()).willReturn(datetimeString);
        given(dateTimeComparison.isLessThanSomeMinutes(datetime, 3)).willReturn(TRUE);

        // When
        final boolean actual = ticketPage.ticketStatusIsNew(ticket);

        // Then
        then(finders).should().waitByClassName("row");
        assertThat(actual, is(TRUE));
    }

    @Test
    public void Can_check_ticket_is_open() {
        final Ticket ticket = mock(Ticket.class);
        final String subject = someString();
        final WebElement subjectWE = mock(WebElement.class);
        final WebElement parentWE = mock(WebElement.class);
        final WebElement datetimeWE = mock(WebElement.class);
        final String datetimeString = someString();
        final String datetime = datetimeString.substring(datetimeString.length() - 22);
        // Given
        given(ticket.getSubject()).willReturn(subject);
        given(finders.findByText("h5", subject)).willReturn(subjectWE);
        given(subjectWE.findElement(Bys.parentClassName("div", "card-body"))).willReturn(parentWE);

        given(parentWE.findElement(Bys.text("small", "Updated by:"))).willReturn(datetimeWE);
        given(datetimeWE.getText()).willReturn(datetimeString);
        given(dateTimeComparison.isLessThanSomeMinutes(datetime, 3)).willReturn(TRUE);


        // When
        final boolean actual = ticketPage.ticketStatusIsOpen(ticket);

        // Then
        then(finders).should().waitByClassName("row");
        assertThat(actual, is(TRUE));
    }

    @Test
    public void Can_check_ticket_is_closed() {
        final Ticket ticket = mock(Ticket.class);
        final String subject = someString();
        final WebElement subjectWE = mock(WebElement.class);
        final WebElement parentWE = mock(WebElement.class);
        final WebElement datetimeWE = mock(WebElement.class);
        final String datetimeString = someString(50);
        final String datetime = datetimeString.substring(datetimeString.length() - 22);
        // Given
        given(ticket.getSubject()).willReturn(subject);
        given(finders.findByText("h5", subject)).willReturn(subjectWE);
        given(subjectWE.findElement(Bys.parentClassName("div", "card-body"))).willReturn(parentWE);

        given(parentWE.findElement(Bys.text("small", "Completed by:"))).willReturn(datetimeWE);
        given(datetimeWE.getText()).willReturn(datetimeString);
        given(dateTimeComparison.isLessThanSomeMinutes(datetime, 3)).willReturn(TRUE);


        // When
        final boolean actual = ticketPage.ticketStatusIsClosed(ticket);

        // Then
        then(finders).should().waitByClassName("row");
        assertThat(actual, is(TRUE));
    }

    @Test
    public void Can_assign_a_ticket() {
        final Ticket ticket = mock(Ticket.class);
        final String subject = someString();
        final WebElement subjectWE = mock(WebElement.class);
        final WebElement parentWE = mock(WebElement.class);
        final WebElement editWE = mock(WebElement.class);
        final WebElement assignTo = mock(WebElement.class);
        final WebElement optionWE = mock(WebElement.class);
        final List<WebElement> list = List.of(optionWE);

        final String datetimeString = someString(50);
        final String datetime = datetimeString.substring(datetimeString.length() - 22);

        // Given
        given(ticket.getSubject()).willReturn(subject);
        given(finders.findByText("h5", subject)).willReturn(subjectWE);
        given(subjectWE.findElement(Bys.parentClassName("div", "card-body"))).willReturn(parentWE);
        given(parentWE.findElement(Bys.text("a", "Edit"))).willReturn(editWE);
        given(finders.findById("assign_to")).willReturn(assignTo);
        given(assignTo.findElements(Bys.tagName("option"))).willReturn(list);
        given(helper.getRandomElement(list)).willReturn(optionWE);

        final WebElement datetimeWE = mock(WebElement.class);
        given(parentWE.findElement(Bys.text("small", "Updated by:"))).willReturn(datetimeWE);
        given(datetimeWE.getText()).willReturn(datetimeString);
        given(dateTimeComparison.isLessThanSomeMinutes(datetime, 3)).willReturn(TRUE);

        // When
        ticketPage.assignTheTicket(ticket);

        // Then
        then(finders).should(times(2)).waitByClassName("row");
        then(editWE).should().click();
        then(finders).should().waitByText("button", "Save");
        then(assignTo).should().click();
        then(optionWE).should().click();
        then(finders).should().clickByText("button", "Save");
    }

    @Test
    public void Can_unassign_a_ticket() {
        final Ticket ticket = mock(Ticket.class);
        final String subject = someString();
        final WebElement subjectWE = mock(WebElement.class);
        final WebElement parentWE = mock(WebElement.class);
        final WebElement editWE = mock(WebElement.class);
        final WebElement assignTo = mock(WebElement.class);
        final WebElement optionWE = mock(WebElement.class);

        final String datetimeString = someString(50);
        final String datetime = datetimeString.substring(datetimeString.length() - 22);

        // Given
        given(ticket.getSubject()).willReturn(subject);
        given(finders.findByText("h5", subject)).willReturn(subjectWE);
        given(subjectWE.findElement(Bys.parentClassName("div", "card-body"))).willReturn(parentWE);
        given(parentWE.findElement(Bys.text("a", "Edit"))).willReturn(editWE);
        given(finders.findById("assign_to")).willReturn(assignTo);
        given(assignTo.findElement(Bys.text("option", "Unassign"))).willReturn(optionWE);

        final WebElement datetimeWE = mock(WebElement.class);
        given(parentWE.findElement(Bys.text("small", "Assigned to: None"))).willReturn(mock(WebElement.class));
        given(parentWE.findElement(Bys.text("small", "Created by:"))).willReturn(datetimeWE);
        given(datetimeWE.getText()).willReturn(datetimeString);
        given(dateTimeComparison.isLessThanSomeMinutes(datetime, 3)).willReturn(TRUE);

        // When
        ticketPage.unassignTheTicket(ticket);

        // Then
        then(finders).should(times(2)).waitByClassName("row");
        then(editWE).should().click();
        then(finders).should().waitByText("button", "Save");
        then(assignTo).should().click();
        then(optionWE).should().click();
        then(finders).should().clickByText("button", "Save");
    }

    @Test
    public void Can_close_a_ticket() {

        final Ticket ticket = mock(Ticket.class);
        final String subject = someString();
        final WebElement subjectWE = mock(WebElement.class);
        final WebElement parentWE = mock(WebElement.class);
        final WebElement editWE = mock(WebElement.class);
        final WebElement solution = mock(WebElement.class);
        final String resolution = someString();
        final WebElement optionWE = mock(WebElement.class);

        // Given
        given(ticket.getSubject()).willReturn(subject);
        given(finders.findByText("h5", subject)).willReturn(subjectWE);
        given(subjectWE.findElement(Bys.parentClassName("div", "card-body"))).willReturn(parentWE);
        given(parentWE.findElement(Bys.text("a", "Edit"))).willReturn(editWE);


        given(finders.findById("solution")).willReturn(solution);
        given(solution.findElement(Bys.text("option", resolution))).willReturn(optionWE);


        // When
        ticketPage.closeATicket(ticket, resolution);

        // Then
        then(finders).should().waitByClassName("row");
        then(editWE).should().click();
        then(finders).should().waitByText("button", "Save");
        then(finders).should().waitById("solution");
        then(solution).should().click();
        then(optionWE).should().click();
        then(finders).should().waitByText("button", "Update Solution");
        then(finders).should().clickByText("button", "Update Solution");
    }
}