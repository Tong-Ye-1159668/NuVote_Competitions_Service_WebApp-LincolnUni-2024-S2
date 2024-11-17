package lu.p2.holders;

import lu.p2.models.Charity;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("cucumber-glue")
public class CharityHolder extends GenericHolder<Charity> {
}
