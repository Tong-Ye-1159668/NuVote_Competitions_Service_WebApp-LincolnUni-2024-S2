package lu.p2.models;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class TicketTest {

    @Test
    public void Can_create_a_ticket() {
        // Given
        final String subject = someString();
        final String content = someString();

        // When
        final Ticket ticket = new Ticket();
        ticket.setSubject(subject);
        ticket.setContent(content);

        // Then
        assertThat(ticket.getSubject(), is(subject));
        assertThat(ticket.getContent(), is(content));
        assertThat(ticket.toString(), is(notNullValue()));
    }
}