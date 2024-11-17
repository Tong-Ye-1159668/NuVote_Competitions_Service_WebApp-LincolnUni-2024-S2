package lu.p2.factories;

import lu.p2.io.Csv;
import lu.p2.models.User;
import org.springframework.stereotype.Component;
import shiver.me.timbers.data.random.RandomThings;

import java.util.List;
import java.util.Random;

import static java.lang.String.format;
import static shiver.me.timbers.data.random.RandomStrings.someAlphaString;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

@Component
public class UserFactory {
    private final Csv csv;

    public UserFactory(final Csv csv) {
        this.csv = csv;
    }

    public User createAUser() {
        final User user = new User();
        user.setUsername(format("user-%s", someAlphanumericString(2, 20)));
        user.setEmail(format("%s@%s.%s", someAlphanumericString(2, 20), someAlphanumericString(3), someAlphaString(3)));
//        user.setPassword(format("%s%s!@%s", someNumericString(3), someAlphanumericString(10), someAlphanumericString(2)));
        user.setPassword("Vote@2024");
        user.setDescription(RandomThings.someThing(someAlphanumericString(50), null));
        return user;
    }

    public User createAUser(final String username) {
        final User user = new User();
        user.setUsername(username);
        user.setPassword("Vote@2024");
        return user;
    }

    public User createAUser(final String username, final String email) {
        final User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("Vote@2024");
        return user;
    }

    public User getAUser() {
        return getRandomUser("users.csv");
    }

    public User createNewProfileDetails(final User user) {
        user.setFirstName(someAlphanumericString(2, 20));
        user.setLastName(someAlphanumericString(2, 20));
        user.setDescription(someAlphanumericString(2, 20));
        return user;
    }

    public User deleteProfileDetails(final User user) {
        user.setFirstName("");
        user.setLastName("");
        user.setDescription("");
        return user;
    }

    public User getAnAdminUser() {
        return getRandomUser("power_users.csv");
    }

    private User getRandomUser(final String fileName) {
        final List<User> users = csv.read(getClass().getClassLoader().getResourceAsStream(fileName), User.class);
        final Random rand = new Random();
        return users.get(rand.nextInt(users.size()));
    }
}
