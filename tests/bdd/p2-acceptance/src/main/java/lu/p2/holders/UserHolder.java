package lu.p2.holders;

import lu.p2.models.User;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("cucumber-glue")
public class UserHolder extends GenericHolder<User> {
}
