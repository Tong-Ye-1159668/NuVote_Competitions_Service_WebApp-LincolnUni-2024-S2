package lu.p2.factories;

import lu.p2.models.Candidate;
import lu.p2.models.Event;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

@Component
public class EventFactory {

    private final LocationFactory locationFactory;

    public EventFactory(final LocationFactory locationFactory) {
        this.locationFactory = locationFactory;
    }

    public Event createEvent(final int period) {
        final Event event = new Event();
        event.setName(String.format("Event %s", someAlphanumericString(20)));
        event.setDescription(someAlphanumericString(200));
        event.setImage(getImagePath("static/event1.jpg"));
        final String[] startDatetime = getFormattedDatetime(4, 0).split(",");
        event.setStartDate(startDatetime[0]);
        event.setStartTime(startDatetime[1]);
        final String[] endDatetime = getFormattedDatetime(4, period).split(",");
        event.setEndDate(endDatetime[0]);
        event.setEndTime(endDatetime[1]);
        event.setCandidates(createCandidates());
        return event;
    }

    private String getFormattedDatetime(final int minsToAdd, final int daysToAdd) {
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime rounded = now.plusSeconds(60 - now.getSecond()).withSecond(0);
        // Add the specified number of days
        final LocalDateTime plusMinutes = rounded.plusMinutes(minsToAdd);
        // Add the specified number of days
        final LocalDateTime finalDateTime = plusMinutes.plusDays(daysToAdd);

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy,hhmma");
        return finalDateTime.format(formatter);
    }

    private String getImagePath(String path) {
        return Objects.requireNonNull(getClass().getClassLoader().getResource(path)).getPath();
    }

    private List<Candidate> createCandidates() {
        return List.of(
                createCandidate("static/candidate1.jpg"),
                createCandidate("static/candidate2.jpg"),
                createCandidate("static/candidate3.jpg"),
                createCandidate("static/candidate4.jpg"));
    }

    private Candidate createCandidate(String path) {
        final Candidate candidate = new Candidate();
        candidate.setName(String.format("Candidate %s", someAlphanumericString(20)));
        candidate.setAuthor(String.format("Author %s", someAlphanumericString(20)));
        candidate.setDescription(String.format("Description %s", someAlphanumericString(200)));
        candidate.setImage(getImagePath(path));
        return candidate;
    }

    public Event update(final Event event) {
        event.setName(String.format("Event %s", someAlphanumericString(20)));
        event.setDescription(someAlphanumericString(200));
        final String[] startDatetime = getFormattedDatetime(2, 0).split(",");
        event.setStartDate(startDatetime[0]);
        event.setStartTime(startDatetime[1]);
        final String[] endDatetime = getFormattedDatetime(6, 0).split(",");
        event.setEndDate(endDatetime[0]);
        event.setEndTime(endDatetime[1]);
        return event;
    }

    public Event createEventWithLocation(final int period) {
        final Event event = this.createEvent(period);
        event.getCandidates().forEach(candidate -> {
            candidate.setLocation(locationFactory.getRandomLocation().getAddress());
        });
        return event;
    }
}
