package lu.p2.factories;

import lu.p2.io.Csv;
import lu.p2.models.Location;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;


@Component
public class LocationFactory {
    private final Csv csv;

    public LocationFactory(final Csv csv) {
        this.csv = csv;
    }

    public Location getRandomLocation() {
        return getRandomLocation("locations.csv");
    }

    private Location getRandomLocation(final String fileName) {
        final List<Location> locations = csv.read(getClass().getClassLoader().getResourceAsStream(fileName), Location.class);
        final Random rand = new Random();
        return locations.get(rand.nextInt(locations.size()));
    }
}
