package lu.p2.models;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class LocationTest {

    @Test
    public void Can_can_a_location() {
        // Given
        final String address = someString();
        final String latitude = someString();
        final String longitude = someString();
        final String cityLatitude = someString();
        final String cityLongitude = someString();

        // When
        final Location location = new Location();
        location.setAddress(address);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setCityLatitude(cityLatitude);
        location.setCityLongitude(cityLongitude);

        // Then
        assertThat(location.getAddress(), is(address));
        assertThat(location.getLatitude(), is(latitude));
        assertThat(location.getLongitude(), is(longitude));
        assertThat(location.getCityLatitude(), is(cityLatitude));
        assertThat(location.getCityLongitude(), is(cityLongitude));

    }
}