package lu.p2.holders;

import lu.p2.models.Ticket;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("cucumber-glue")
public class TicketHolder extends GenericHolder<Ticket> {
}
