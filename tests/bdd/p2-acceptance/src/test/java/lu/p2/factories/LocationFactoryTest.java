package lu.p2.factories;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import lu.p2.io.Csv;
import lu.p2.models.Location;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class LocationFactoryTest {

    private LocationFactory locationFactory;

    @Before
    public void setUp() {
        locationFactory = new LocationFactory(new Csv(new CsvMapper()));
    }

    @Test
    public void Can_get_a_random_location() {
        // Given

        // When
        final Location location = locationFactory.getRandomLocation();

        // Then
        assertThat(location.getAddress(), is(notNullValue()));
        assertThat(location.getLatitude(), is(notNullValue()));
        assertThat(location.getLongitude(), is(notNullValue()));
        assertThat(location.getCityLatitude(), is(notNullValue()));
        assertThat(location.getCityLongitude(), is(notNullValue()));
    }
}