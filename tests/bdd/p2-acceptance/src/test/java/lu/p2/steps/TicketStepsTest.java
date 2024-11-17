package lu.p2.steps;

import lu.p2.factories.TicketFactory;
import lu.p2.holders.TicketHolder;
import lu.p2.holders.UserHolder;
import lu.p2.models.Ticket;
import lu.p2.models.User;
import lu.p2.pages.TicketPage;
import org.junit.Before;
import org.junit.Test;

import static java.lang.Boolean.TRUE;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class TicketStepsTest {

    private TicketPage ticketPage;
    private TicketFactory ticketFactory;
    private TicketHolder ticketHolder;
    private UserHolder userHolder;
    private TicketSteps ticketSteps;

    @Before
    public void setUp() {
        ticketPage = mock(TicketPage.class);
        ticketFactory = mock(TicketFactory.class);
        ticketHolder = mock(TicketHolder.class);
        userHolder = mock(UserHolder.class);
        ticketSteps = new TicketSteps(ticketPage, ticketFactory, ticketHolder, userHolder);
    }

    @Test
    public void Can_create_a_ticket() {
        final Ticket ticket = mock(Ticket.class);

        // Given
        given(ticketFactory.createATicket()).willReturn(ticket);
        given(ticketPage.createATicket(ticket)).willReturn(ticket);

        // When
        ticketSteps.iCreateATicket();

        // Then
        then(ticketPage).should().clickHelpBtn();
        then(ticketPage).should().createATicket(ticket);
        then(ticketHolder).should().set(ticket);
    }

    @Test
    public void Can_my_ticket_is_created_successfully() {
        // Given

        // When
        ticketSteps.myTicketIsCreatedSuccessfully();

        // Then
        then(ticketPage).should().ticketIsCreated();
    }

    @Test
    public void Can_view_the_detail_of_the_ticket() {
        final Ticket ticket = mock(Ticket.class);

        // Given
        given(ticketHolder.get()).willReturn(ticket);

        // When
        ticketSteps.iCanViewTheDetailOfTheTicket();

        // Then
        then(ticketPage).should().checkMyTicket(ticket);
    }

    @Test
    public void Can_cancel_my_ticket() {
        final Ticket ticket = mock(Ticket.class);

        // Given
        given(ticketHolder.get()).willReturn(ticket);

        // When
        ticketSteps.iCancelTheTicket();

        // Then
        then(ticketPage).should().cancelTheTicket(ticket);
    }

    @Test
    public void Can_check_my_ticket_is_cancelled() {
        final Ticket ticket = mock(Ticket.class);

        // Given
        given(ticketHolder.get()).willReturn(ticket);

        // When
        ticketSteps.myTicketIsCancelledSuccessfully();

        // Then
        then(ticketPage).should().ticketIsCancelled(ticket);
    }

    @Test
    public void Can_reply_to_the_ticket() {
        final Ticket ticket = mock(Ticket.class);

        // Given
        given(ticketHolder.get()).willReturn(ticket);

        // When
        ticketSteps.iReplyToTheTicket();

        // Then
        then(ticketPage).should().replyTo(ticket);
    }

    @Test
    public void Can_see_all_replies_of_the_ticket() {
        final Ticket ticket = mock(Ticket.class);
        final User user = mock(User.class);

        // Given
        given(ticketHolder.get()).willReturn(ticket);
        given(userHolder.get()).willReturn(user);

        // When
        ticketSteps.iShouldSeeAllRepliesAboutTheTicket();

        // Then
        then(ticketPage).should().checkMyReplies(ticket, user);
    }

    @Test
    public void Can_visit_ticket_mgmt_page() {
        // Given

        // When
        ticketSteps.iVisitTicketMgmtPage();

        // Then
        then(ticketPage).should().visitMgmtPage();
    }

    @Test
    public void Can_check_ticket_status_is_new() {
        final Ticket ticket = mock(Ticket.class);
        // Given
        given(ticketHolder.get()).willReturn(ticket);
        given(ticketPage.ticketStatusIsNew(ticketHolder.get())).willReturn(TRUE);

        // When
        ticketSteps.theTicketStatusIsNew();

        // Then
        then(ticketPage).should().ticketStatusIsNew(ticket);
    }

    @Test
    public void Can_check_ticket_status_is_open() {
        final Ticket ticket = mock(Ticket.class);
        // Given
        given(ticketHolder.get()).willReturn(ticket);
        given(ticketPage.ticketStatusIsOpen(ticketHolder.get())).willReturn(TRUE);

        // When
        ticketSteps.theTicketStatusIsUpdatedToOpen();

        // Then
        then(ticketPage).should().ticketStatusIsOpen(ticket);
    }

    @Test
    public void Can_check_ticket_status_is_closed() {
        final Ticket ticket = mock(Ticket.class);
        // Given
        given(ticketHolder.get()).willReturn(ticket);
        given(ticketPage.ticketStatusIsClosed(ticketHolder.get())).willReturn(TRUE);

        // When
        ticketSteps.theTicketStatusIsUpdatedToClosed();

        // Then
        then(ticketPage).should().ticketStatusIsClosed(ticket);
    }

    @Test
    public void Can_assign_ticket_a_helper() {
        final Ticket ticket = mock(Ticket.class);
        // Given
        given(ticketHolder.get()).willReturn(ticket);

        // When
        ticketSteps.iAssignTheTicketToAHelper();

        // Then
        then(ticketPage).should().assignTheTicket(ticket);
    }

    @Test
    public void Can_unassign_ticket_a_helper() {
        final Ticket ticket = mock(Ticket.class);
        // Given
        given(ticketHolder.get()).willReturn(ticket);

        // When
        ticketSteps.iUnassignTheTicketToAHelper();

        // Then
        then(ticketPage).should().unassignTheTicket(ticket);
    }

    @Test
    public void Can_close_a_ticket_with_resolution() {
        final Ticket ticket = mock(Ticket.class);
        final String resolution = someString();

        // Given
        given(ticketHolder.get()).willReturn(ticket);

        // When
        ticketSteps.iCloseTheTicketWith(resolution);

        // Then
        then(ticketPage).should().closeATicket(ticket, resolution);
    }
}