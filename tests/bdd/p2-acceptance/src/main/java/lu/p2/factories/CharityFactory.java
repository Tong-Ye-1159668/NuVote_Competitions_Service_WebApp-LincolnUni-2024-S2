package lu.p2.factories;

import lu.p2.models.Charity;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static shiver.me.timbers.data.random.RandomStrings.someAlphaString;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someNumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@Component
public class CharityFactory {
    public Charity create() {
        final Charity charity = new Charity();
        charity.setName("Charity" + someAlphanumericString(20));
        charity.setDescription(someString(255));
        charity.setImage(getImagePath("static/event1.jpg"));
        charity.setRegNumber(someAlphaString(2) + someNumericString(4, 6));
        charity.setBankAccount(someNumericString(15, 16));
        charity.setIrdNumber(someNumericString(9));
        return charity;
    }

    private String getImagePath(String path) {
        return Objects.requireNonNull(getClass().getClassLoader().getResource(path)).getPath();
    }
}
