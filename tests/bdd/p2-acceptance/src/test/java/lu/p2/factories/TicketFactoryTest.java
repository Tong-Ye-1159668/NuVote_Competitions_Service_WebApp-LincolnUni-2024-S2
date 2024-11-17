package lu.p2.factories;

import lu.p2.models.Ticket;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class TicketFactoryTest {

    @Test
    public void Can_create_a_ticket() {
        // Given

        // When
        final Ticket actual = new TicketFactory().createATicket();

        // Then
        assertThat(actual.getSubject(), is(notNullValue()));
        assertThat(actual.getContent(), is(notNullValue()));
    }
}