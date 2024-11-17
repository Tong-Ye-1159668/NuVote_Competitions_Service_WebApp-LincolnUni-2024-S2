package lu.p2.factories;

import lu.p2.models.Candidate;
import lu.p2.models.Event;
import lu.p2.models.Location;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class EventFactoryTest {

    private EventFactory eventFactory;
    private LocationFactory locationFactory;

    @Before
    public void setUp() {
        locationFactory = mock(LocationFactory.class);
        eventFactory = new EventFactory(locationFactory);
    }

    @Test
    public void Can_create_an_event() {
        // Given

        // When
        final Event event = eventFactory.createEvent(1);

        // Then
        assertThat(event.getName(), is(notNullValue()));
        assertThat(event.getDescription(), is(notNullValue()));
        assertThat(event.getImage(), is(notNullValue()));
        assertThat(event.getStartDate(), is(notNullValue()));
        assertThat(event.getStartTime(), is(notNullValue()));
        assertThat(event.getEndDate(), is(notNullValue()));
        assertThat(event.getEndTime(), is(notNullValue()));
        assertThat(event.getCandidates(), is(notNullValue()));
    }

    @Test
    public void Can_update_an_event() {
        // Given

        // When
        final Event event = eventFactory.update(new Event());

        // Then
        assertThat(event.getName(), is(notNullValue()));
        assertThat(event.getDescription(), is(notNullValue()));
        assertThat(event.getStartDate(), is(notNullValue()));
        assertThat(event.getStartTime(), is(notNullValue()));
        assertThat(event.getEndDate(), is(notNullValue()));
        assertThat(event.getEndTime(), is(notNullValue()));
    }

    @Test
    public void Can_create_an_event_with_location() {
        final Location location = mock(Location.class);

        // Given
        given(locationFactory.getRandomLocation()).willReturn(location);
        given(location.getAddress()).willReturn(someString(), someString(), someString(), someString());

        // When
        final Event event = eventFactory.createEventWithLocation(1);

        // Then
        assertThat(event.getName(), is(notNullValue()));
        assertThat(event.getDescription(), is(notNullValue()));
        assertThat(event.getImage(), is(notNullValue()));
        assertThat(event.getStartDate(), is(notNullValue()));
        assertThat(event.getStartTime(), is(notNullValue()));
        assertThat(event.getEndDate(), is(notNullValue()));
        assertThat(event.getEndTime(), is(notNullValue()));
        assertThat(event.getCandidates(), is(notNullValue()));
        final List<Candidate> candidates = event.getCandidates();
        assertThat(candidates, is(notNullValue()));
        candidates.forEach(candidate -> {
            assertThat(candidate.getName(), is(notNullValue()));
            assertThat(candidate.getDescription(), is(notNullValue()));
            assertThat(candidate.getImage(), is(notNullValue()));
            assertThat(candidate.getLocation(), is(notNullValue()));
        });
    }
}