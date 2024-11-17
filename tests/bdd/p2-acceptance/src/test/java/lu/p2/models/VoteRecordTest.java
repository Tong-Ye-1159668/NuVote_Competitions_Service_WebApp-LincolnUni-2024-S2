package lu.p2.models;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class VoteRecordTest {

    @Test
    public void Can_create_a_record() {
        // Given
        final String candidateId = someString();
        final String voteDateTime = someString();
        final String themeName = someString();
        final String eventName = someString();
        final String candidateName = someString();
        final String totalVotes = someString();

        // When
        final VoteRecord actual = new VoteRecord();
        actual.setCandidateId(candidateId);
        actual.setVoteDateTime(voteDateTime);
        actual.setThemeName(themeName);
        actual.setEventName(eventName);
        actual.setCandidateName(candidateName);
        actual.setTotalVotes(totalVotes);

        // Then
        assertThat(actual.getCandidateId(), is(candidateId));
        assertThat(actual.getCandidateName(), is(candidateName));
        assertThat(actual.getThemeName(), is(themeName));
        assertThat(actual.getEventName(), is(eventName));
        assertThat(actual.getVoteDateTime(), is(voteDateTime));
        assertThat(actual.getTotalVotes(), is(totalVotes));
    }
}