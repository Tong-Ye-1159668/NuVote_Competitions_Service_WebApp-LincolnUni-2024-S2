package lu.p2.factories;

import lu.p2.models.Ticket;
import org.springframework.stereotype.Component;

import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

@Component
public class TicketFactory {
    public Ticket createATicket() {
        final Ticket ticket = new Ticket();
        ticket.setSubject(someAlphanumericString(10));
        ticket.setContent(someAlphanumericString(20));
        return ticket;
    }
}
